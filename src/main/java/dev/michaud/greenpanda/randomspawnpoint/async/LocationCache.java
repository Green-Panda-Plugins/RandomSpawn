package dev.michaud.greenpanda.randomspawnpoint.async;

import dev.michaud.greenpanda.randomspawnpoint.RandomSpawnpoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;

public class LocationCache {

  public static final int TIMEOUT_SECONDS = 120;
  public static final int MAX_CACHE_SIZE = 10;

  private static final Queue<Location> locations = new LinkedBlockingQueue<>(MAX_CACHE_SIZE);
  private static final Logger logger = RandomSpawnpoint.getPlugin().getLogger();

  public static Location getLocation() {

    Location location = locations.poll();

    generateLocations(1);

    return location;

  }

  public static void generateLocations(int num) {

    for (int i = 0; i < num; i++) {
      generateLocation();
    }

  }

  public static void generateLocation() {

    GenerateLocationAsync.getLocation()
        .thenAccept(LocationCache::addIfNotNull)
        .orTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .whenComplete(LocationCache::onComplete);

  }

  private static void onComplete(Void value, Throwable error) {

    if (error instanceof TimeoutException) {
      logger.log(Level.SEVERE, "Thread timed out" + error);
    } else if (error != null) {
      logger.log(Level.SEVERE, "Thread ran into an error: " + error);
    }

  }

  private static void addIfNotNull(Location location) {

    if (location != null) {
      locations.offer(location);
      logger.log(Level.INFO, "Adding location {0}", location);
    } else {
      generateLocations(1);
      logger.log(Level.INFO, "Location was null");
    }

  }

}