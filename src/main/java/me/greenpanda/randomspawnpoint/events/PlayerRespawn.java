package me.greenpanda.randomspawnpoint.events;

import me.greenpanda.randomspawnpoint.RandomSpawnpoint;
import me.greenpanda.randomspawnpoint.GetSpawnUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.persistence.PersistentDataType;

/**
 * Listens for a PlayerRespawn event. If the player has a valid bed or respawn anchor the vanilla
 * behavior is used and the player is spawned at their bed or respawn anchor at a safe location. If
 * no bed or charged respawn anchor exists, or it is obstructed, then the player respawns at their
 * original spawn point.
 */
public class PlayerRespawn implements Listener {

  public static final World world = Bukkit.getServer().getWorlds().get(0);

  @EventHandler
  public void onPlayerRespawn(PlayerRespawnEvent event) {
    Player player = event.getPlayer();

    var location = player.getBedSpawnLocation();

    if (location != null) {

      //Returns if a bed spawn and a bed at that location exists
      if (event.isBedSpawn() && (GetSpawnUtility.getSafeBedSpawnLocation(player) != null)) {
        //player.sendMessage(ChatColor.GREEN + "Spawning you at your bed");
        return;
      }

      //Returns if an anchor spawn
      if (event.isAnchorSpawn()) {
        //player.sendMessage(ChatColor.GREEN + "Spawning you at your respawn anchor");
        return;
      }
    }

    /*
    * Since they are not spawning at a bed or respawn anchor, set the respawn location to the player's
    * world spawn
    */
    var playerWorldSpawn = getPlayerWorldSpawn(player);
    player.setBedSpawnLocation(playerWorldSpawn, true);
    event.setRespawnLocation(playerWorldSpawn);
  }

  /**
   * Gets the player's stored world spawn from persistent data.
   * @param player player whose world spawn to get
   * @return location at the given player's world spawn
   */
  public static Location getPlayerWorldSpawn(Player player){

    //If not respawning at a bed or charged respawn anchor.
    var data = player.getPersistentDataContainer();

    //Get spawn location from player NBT
    var spawnX = data.get(new NamespacedKey(RandomSpawnpoint.getPlugin(), "UniqueWorldSpawnX"),
        PersistentDataType.INTEGER);
    var spawnY = data.get(new NamespacedKey(RandomSpawnpoint.getPlugin(), "UniqueWorldSpawnY"),
        PersistentDataType.INTEGER);
    var spawnZ = data.get(new NamespacedKey(RandomSpawnpoint.getPlugin(), "UniqueWorldSpawnZ"),
        PersistentDataType.INTEGER);

    return new Location(world, spawnX, spawnY,
        spawnZ);
  }
}
