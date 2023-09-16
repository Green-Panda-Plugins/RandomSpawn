package dev.michaud.greenpanda.randomspawnpoint.async;

import dev.michaud.greenpanda.randomspawnpoint.RandomSpawnpoint;
import io.papermc.lib.PaperLib;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GenerateLocationAsync {

  private static void log(String msg) {
    String prefix = "[Thread " + Thread.currentThread().getId() + "] ";
    RandomSpawnpoint.getPlugin().getServer().getConsoleSender().sendMessage(prefix + msg);
  }

  @Contract(" -> new")
  public static @NotNull CompletableFuture<Location> getLocation() {
    return CompletableFuture.supplyAsync(GenerateLocationAsync::generateSafeLocation);
  }

  private static @NotNull Location generateSafeLocation() {

    log("Generating location...");

    Location location;

    do {
      location = randomLocationInCircle(RandomSpawnpoint.spawnRadius, 0, 0);
    }
    while (!isSafeLocation(location));

    return location;

  }

  @Contract("_, _, _ -> new")
  private static @NotNull Location randomLocationInCircle(int radius, int centerX, int centerY) {

    log("Getting random location in a circle");

    ThreadLocalRandom random = ThreadLocalRandom.current();
    double rand1 = random.nextDouble();
    double rand2 = random.nextDouble();

    double r = radius * Math.sqrt(rand1);
    double theta = rand2 * 2 * Math.PI;

    int x = (int) (centerX + r * Math.cos(theta));
    int z = (int) (centerY + r * Math.sin(theta));

    return new Location(RandomSpawnpoint.defaultWorld, x, 0, z);

  }

  @Contract("null -> false")
  private static boolean isSafeLocation(@Nullable Location location) {

    if (location == null) {
      return false;
    }

    World world = location.getWorld();
    int x = location.getBlockX() >> 4;
    int z = location.getBlockZ() >> 4;

    ChunkSnapshot chunk = PaperLib.getChunkAtAsync(world, x, z, true, true)
        .join()
        .getChunkSnapshot();

    int chunkX = location.getBlockX() - (chunk.getX() << 4);
    int chunkZ = location.getBlockZ() - (chunk.getZ() << 4);
    int chunkY = chunk.getHighestBlockYAt(chunkX, chunkZ);

    location.setY(chunkY + 1);

    Material ground = chunk.getBlockType(chunkX, chunkY, chunkZ);
    Material feet = chunk.getBlockType(chunkX, chunkY + 1, chunkZ);
    Material head = chunk.getBlockType(chunkX, chunkY + 2, chunkZ);

    HashSet<Material> blacklist = RandomSpawnpoint.blockBlacklist;

    return ground.isSolid()
        && feet.isAir() && head.isAir()
        && !blacklist.contains(ground)
        && !blacklist.contains(feet)
        && !blacklist.contains(head);

  }

}