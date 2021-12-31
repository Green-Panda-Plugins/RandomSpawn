package me.greenpanda.randomspawnpoint;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

/**
 * Utility class relating to setting the player's spawnpoint to a random position
 *
 * @see GetSpawnUtility
 */
public class SetSpawnUtility {

  //Config
  private static final List<String> spawnableBlocks = RandomSpawnpoint.getPlugin().getConfig()
      .getStringList("SpawnableBlocks");
  public static int spawnableBlockSize = spawnableBlocks.size();
  public static int spawnableRadius = RandomSpawnpoint.getPlugin().getConfig()
      .getInt("SpawnDistance");

  //Other vars
  public static final World world = Bukkit.getServer().getWorlds().get(0);

  /**
   * Sets a player's spawn point to a random position. The parameters are based on the values in
   * config.yml. A safe location to spawn the player will be attempted, but the method will return
   * null if no suitable location was found after 5 attempts.
   *
   * @param player the player whose spawn point to set
   * @return the location of the player's new spawn point, null if no location was set
   */
  public static Location SetRandomPlayerSpawn(Player player) {

    //Get a safe location
    Location spawnLocation = getRandomLocationInCircle(spawnableRadius, 0, 0);
    int i = 1;

    while (!isSafeLocation(spawnLocation)) {
      spawnLocation = getRandomLocationInCircle(spawnableRadius, 0, 0);
      i++;
      if (i > 10) {
        return null;
      }
    }
    player.sendMessage("Found location after " + i + " tries");

    //Set the player's spawn location
    player.setBedSpawnLocation(spawnLocation, true);

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
    double r = radius * Math.sqrt(ThreadLocalRandom.current().nextDouble(0, 1));
    double theta = ThreadLocalRandom.current().nextDouble(0, 1) * 2 * Math.PI;

    int x = (int) (centerX + r * Math.cos(theta));
    int z = (int) (centerY + r * Math.sin(theta));

    //Y coordinate is the highest block at given coordinates
    int y = world.getHighestBlockYAt(x, z) + 1;

    System.out.println("Got random location: " + x + ", " + y + ", " + z + ".");
    return new Location(world, x, y, z);
  }

  /**
   * Returns true if the given location is safe to set the player's spawn point to. The requirements
   * for a safe location is a solid block for the player to stand on and two non-solid non-liquid
   * blocks above. The ground block must also comply with the spawnable blocks set in config.yml,
   * unless empty. Returns false if the conditions are not met, or the location is invalid.
   *
   * @param location location to search
   * @return true if the given location is safe for a player to spawn at
   */
  private static boolean isSafeLocation(Location location) {
    if (location == null) {
      return false;
    }

    Location ground = location.clone();
    Location feet = location.clone();
    Location head = location.clone();

    ground.setY(ground.getY() - 1);
    head.setY(head.getY() + 1);

    if (!ground.getBlock().isSolid()                                    //No solid ground
        || feet.getBlock().isSolid() || head.getBlock().isSolid()       //Feet or head in block
        || feet.getBlock().isLiquid() || head.getBlock().isLiquid()) {  //In water or lava
      return false;
    }

    /*
    If spawnableBlocks is empty we can ignore this check and every solid block will be considered.
    Otherwise, only return true if spawnableBlocks contains the ground block.
     */
    if (spawnableBlockSize > 0) {
      String groundBlock = ground.getBlock().getType().toString();
      return spawnableBlocks.stream()
          .anyMatch(groundBlock::equalsIgnoreCase);
    }

    return true; //All conditions met
  }
}
