package dev.michaud.greenpanda.randomspawnpoint.util;

import dev.michaud.greenpanda.randomspawnpoint.RandomSpawnpoint;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SetSpawn {

  //How many times we try to find a suitable location.
  public static final int FIND_LOCATION_TRIES = 100;

  /**
   * Sets the player's spawn point to a random position. The location must be safe as defined by
   * {@link SetSpawn#getSafeLocation(World)}. If after 100 tries no suitable location is found, the
   *
   * @param world  the world to set the player's spawnpoint in.
   * @param player the player to set the spawnpoint for.
   * @return the location of the player's new spawn point, null if no location was set.
   */
  @Nullable
  public static Location setRandomPlayerSpawn(Player player, World world) {

    Location location = getSafeLocation(world);

    if (location == null) {
      return null;
    }

    player.setBedSpawnLocation(location, true);

    if (RandomSpawnpoint.setPlayerCompassLocation) {
      player.setCompassTarget(location);
    }

    //Store the world spawn in the player's NBT
    setWorldSpawnNBT(player, location);

    return location;

  }

  public static void setWorldSpawnNBT(@NotNull Player player, @NotNull Location location) {
    PersistentDataContainer data = player.getPersistentDataContainer();

    data.set(new NamespacedKey(RandomSpawnpoint.getPlugin(), "UniqueWorldSpawnX"),
        PersistentDataType.INTEGER, location.getBlockX());
    data.set(new NamespacedKey(RandomSpawnpoint.getPlugin(), "UniqueWorldSpawnY"),
        PersistentDataType.INTEGER, location.getBlockY());
    data.set(new NamespacedKey(RandomSpawnpoint.getPlugin(), "UniqueWorldSpawnZ"),
        PersistentDataType.INTEGER, location.getBlockZ());
  }

  @Nullable
  public static Location getWorldSpawnNBT(@NotNull Player player, @NotNull World world) {

    PersistentDataContainer data = player.getPersistentDataContainer();

    Integer spawnX = data.get(new NamespacedKey(RandomSpawnpoint.getPlugin(), "UniqueWorldSpawnX"),
        PersistentDataType.INTEGER);
    Integer spawnY = data.get(new NamespacedKey(RandomSpawnpoint.getPlugin(), "UniqueWorldSpawnY"),
        PersistentDataType.INTEGER);
    Integer spawnZ = data.get(new NamespacedKey(RandomSpawnpoint.getPlugin(), "UniqueWorldSpawnZ"),
        PersistentDataType.INTEGER);

    if (spawnX == null || spawnY == null || spawnZ == null) {
      return null;
    }

    return new Location(world, spawnX, spawnY, spawnZ);
  }

  @Nullable
  public static Location getSafeLocation(World world) {
    Location location = GetRandom.getRandomLocationInCircle(world, RandomSpawnpoint.spawnRadius, 0,
        0);
    int i = 0;

    while (!isSafeLocation(location)) {

      if (i >= FIND_LOCATION_TRIES) {
        return null;
      }

      location = GetRandom.getRandomLocationInCircle(world, RandomSpawnpoint.spawnRadius, 0, 0);

      i++;

    }

    return location;
  }

  @Contract("null -> false")
  public static boolean isSafeLocation(Location location) {
    if (location == null) {
      return false;
    }

    Block feet = location.clone().getBlock();
    Block ground = feet.getRelative(BlockFace.DOWN);
    Block head = feet.getRelative(BlockFace.UP);

    //------------------------------------------------Returns true if:
    return ground.isSolid()                           //Standing on solid ground with
        && !feet.isSolid() && !head.isSolid()         //2 non-solid blocks above,
        && !RandomSpawnpoint.blockBlacklist.contains(feet.getType())
        //no blacklisted blocks at the player's feet,
        && !RandomSpawnpoint.blockBlacklist.contains(ground.getType()) //on the ground,
        && !RandomSpawnpoint.blockBlacklist.contains(head.getType());  //or on the player's head
  }

}