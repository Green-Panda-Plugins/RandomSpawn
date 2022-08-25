package dev.michaud.greenpanda.randomspawnpoint.util;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.World;

public class GetRandom {

  /**
   * Gets a uniformly random position inside a circle with the given radius. The Y coordinate is one
   * block above the highest non-empty block at the generated x and z coordinates
   *
   * @param radius  the radius of the bounding circle
   * @param centerX x coordinate of the center of the circle
   * @param centerY y coordinate of the center of the circle
   * @return a location at the randomly generated coordinates
   */
  public static Location getRandomLocationInCircle(World world, int radius, int centerX,
      int centerY) {

    Random rand = new Random();
    double r = radius * Math.sqrt(rand.nextDouble(0, 1));
    double theta = rand.nextDouble(0, 1) * 2 * Math.PI;

    int x = (int) (centerX + r * Math.cos(theta));
    int z = (int) (centerY + r * Math.sin(theta));

    int y = world.getHighestBlockYAt(x, z) + 1;

    return new Location(world, x, y, z);
  }

}