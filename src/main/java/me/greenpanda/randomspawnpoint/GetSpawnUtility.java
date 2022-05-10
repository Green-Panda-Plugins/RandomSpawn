package me.greenpanda.randomspawnpoint;

import java.util.EnumSet;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Bed.Part;
import org.bukkit.entity.Player;

public class GetSpawnUtility {

  private static final Set<BlockFace> SEARCH_DIRECTIONS = EnumSet.noneOf(BlockFace.class);

  static {
    SEARCH_DIRECTIONS.add(BlockFace.NORTH);
    SEARCH_DIRECTIONS.add(BlockFace.NORTH_EAST);
    SEARCH_DIRECTIONS.add(BlockFace.EAST);
    SEARCH_DIRECTIONS.add(BlockFace.SOUTH_EAST);
    SEARCH_DIRECTIONS.add(BlockFace.SOUTH);
    SEARCH_DIRECTIONS.add(BlockFace.SOUTH_WEST);
    SEARCH_DIRECTIONS.add(BlockFace.WEST);
    SEARCH_DIRECTIONS.add(BlockFace.NORTH_WEST);
  }

  /**
   * Finds a safe location near the player's spawn bed. Will return null if the player hasn't slept
   * in a bed or their bed is invalid or obstructed.
   *
   * @param player player whose bed location to find
   * @return a safe location by the player's bed if it exists, otherwise null.
   */
  public static Location getSafeBedSpawnLocation(Player player){
    var location = player.getBedSpawnLocation();

    if (location == null){
      return null;
    }

    final Location trySpawn = getSafeSpawnAroundBlock(location);
    if (trySpawn != null){
      return trySpawn;
    }

    Location otherBlock = findOtherBedPiece(location);
    if (otherBlock != null){
      return getSafeSpawnAroundBlock(otherBlock);
    }

    return null;
  }

  /**
   * Given the head piece of a bed, find the foot piece. Given the foot piece, returns the head.
   * Returns null if given a jacked up bed.
   * @param location location of the bed piece
   * @return location of the other bed piece, null if invalid bed.
   */
  private static Location findOtherBedPiece(Location location) {
    var data = location.getBlock().getBlockData();
    if (!(data instanceof Bed bed)){
      return null;
    }

    if (bed.getPart() == Part.HEAD){
      return location.getBlock().getRelative(bed.getFacing().getOppositeFace()).getLocation();
    }

    //So no head
    return location.getBlock().getRelative(bed.getFacing()).getLocation();
  }

  /**
   * Finds a safe location around a given block. Returns null if no location was found. Height is
   * ignored, and a position will only be searched for in the  North, East, South, West, North East,
   * North West, South East and South West directions.
   * @param location location of the block to search
   * @return a safe location near the given block, null if none was found.
   */
  private static Location getSafeSpawnAroundBlock(Location location){

    for (BlockFace face : SEARCH_DIRECTIONS) {
      final Location faceLocation = location.getBlock().getRelative(face).getLocation();

      if (isSafeBedLocation(faceLocation)) {
        return faceLocation.add(.5, 0, .5);
      }
    }
    return null;
  }

  /**
   * Returns true if the given position is safe for spawning, meaning there is a solid block on the
   * ground, and two non-solid blocks above. Returns false if the location is unsafe, or the given
   * location is invalid.
   *
   * @param location location to search
   * @return true if the location is safe
   */
  private static boolean isSafeBedLocation(Location location){
    if (location == null){
      return false;
    }

    Location ground = location.clone();
    Location feet = location.clone();
    Location head = location.clone();

    ground.setY(ground.getY() - 1);
    head.setY(head.getY() + 1);

    return ground.getBlock().isSolid()    //Solid ground block
        && !feet.getBlock().isSolid()     //Non-solid block at feet
        && !head.getBlock().isSolid();    //Non-solid block at head
  }
}
