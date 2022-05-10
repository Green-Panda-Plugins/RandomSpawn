package me.greenpanda.randomspawnpoint;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class relating to setting the player's spawnpoint to a random position
 *
 * @see GetSpawnUtility
 */
public class SetSpawnUtility {

  //Config
  private static final @NotNull FileConfiguration config = RandomSpawnpoint.getPlugin().getConfig();
  private static final int spawnableRadius = config.getInt("SpawnDistance");
  private static final boolean setPlayerCompassLoc = config.getBoolean("SetPlayerCompassLocation");

  private static final List<String> blacklistString = config.getStringList("BlockBlacklist");
  private static final HashSet<Material> blockBlacklist = new HashSet<>();

  static {
    for (String block : blacklistString) {
      blockBlacklist.add(Material.valueOf(block));
    }
  }

  //Other vars
  public static final World world = Bukkit.getServer().getWorlds().get(0);

  /**
   * Sets a player's spawn point to a random position. A safe location is defined by
   * getSafeSpawnLocation. Also stores the player's new spawn point as persistent data in their nbt
   * for access later
   *
   * @param player the player whose spawn point to set
   * @return the location of the player's new spawn point, world spawn if no location was set
   */
  public static @NotNull Location SetRandomPlayerSpawn(Player player) {

    Location spawnLocation = getSafeSpawnLocation();

    //Set the player's spawn location
    player.setBedSpawnLocation(spawnLocation, true);

    if (setPlayerCompassLoc){
      player.setCompassTarget(spawnLocation);
    }

    //Store the world spawn in the player's NBT
    var data = player.getPersistentDataContainer();

    data.set(new NamespacedKey(RandomSpawnpoint.getPlugin(), "UniqueWorldSpawnX"),
        PersistentDataType.INTEGER, spawnLocation.getBlockX());
    data.set(new NamespacedKey(RandomSpawnpoint.getPlugin(), "UniqueWorldSpawnY"),
        PersistentDataType.INTEGER, spawnLocation.getBlockY());
    data.set(new NamespacedKey(RandomSpawnpoint.getPlugin(), "UniqueWorldSpawnZ"),
        PersistentDataType.INTEGER, spawnLocation.getBlockZ());

    return spawnLocation;
  }

  /**
   * Gets a random location until a safe one is found. What block is safe is defined by
   * isSafeLocation(). If a safe location can't be found after 100 attempts, the method will quit
   * and return the world spawn.
   *
   * @return a random safe location
   */
  public static Location getSafeSpawnLocation() {
    Location location = getRandomLocationInCircle(spawnableRadius, 0, 0);
    int i = 0;

    while (!isSafeLocation(location)) {
      location = getRandomLocationInCircle(spawnableRadius, 0, 0);
      i++;
      if (i > 100) {
        System.out.println(
            "Unable to find a safe location after 100 iterations. The settings in your config might"
                + " be too strict, or there aren't enough spawnable spaces in your world.");
        return world.getSpawnLocation();
      }
    }

    return location;
  }

  /**
   * Gets a uniformly random position inside a circle with the given radius. Always gets returns a
   * position in the default overworld. The Y coordinate is one block above the highest non-empty
   * block at the generated x and z coordinates
   *
   * @param radius  the radius of the bounding circle
   * @param centerX x coordinate of the center of the circle
   * @param centerY y coordinate of the center of the circle
   * @return a location at the randomly generated coordinates
   */
  public static Location getRandomLocationInCircle(int radius, int centerX, int centerY) {
    var rand = ThreadLocalRandom.current();
    double r = radius * Math.sqrt(rand.nextDouble(0, 1));
    double theta = rand.nextDouble(0, 1) * 2 * Math.PI;

    int x = (int) (centerX + r * Math.cos(theta));
    int z = (int) (centerY + r * Math.sin(theta));

    //Y coordinate is the highest block at given coordinates
    int y = world.getHighestBlockYAt(x, z) + 1;

    System.out.println("Got random location: " + x + ", " + y + ", " + z + ".");
    return new Location(world, x, y, z);
  }

  /**
   * Returns true if the given location is safe to set the player's spawn point to. The requirements
   * for a safe location is a solid block for the player to stand on and two non-solid blocks above.
   * The ground block and the 2 blocks above must also not be one blacklisted in the config.yml.
   * Returns false if the conditions are not met, or the location is invalid.
   *
   * @param location location to search
   * @return true if the given location is safe for a player to spawn at
   */
  private static boolean isSafeLocation(Location location) {
    if (location == null) {
      return false;
    }

    Block feet = location.clone().getBlock();
    Block ground = feet.getRelative(BlockFace.DOWN);
    Block head = feet.getRelative(BlockFace.UP);

    //------------------------------------------------Returns true if:
    return ground.isSolid()                           //Standing on solid ground with
        && !feet.isSolid() && !head.isSolid()         //2 non-solid blocks above,
        && !blockBlacklist.contains(feet.getType())   //no blacklisted blocks at the player's feet,
        && !blockBlacklist.contains(ground.getType()) //on the ground,
        && !blockBlacklist.contains(head.getType());  //or on the player's head
  }
}
