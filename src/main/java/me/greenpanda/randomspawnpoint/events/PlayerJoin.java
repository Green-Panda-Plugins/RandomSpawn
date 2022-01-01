package me.greenpanda.randomspawnpoint.events;

import me.greenpanda.randomspawnpoint.SetSpawnUtility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Listens for a PlayerJoinEvent, and gives the player a random spawn point if they haven't played
 * on this world before.
 * @see PlayerJoinEvent
 */
public class PlayerJoin implements Listener {

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();

    //If the player hasn't played before, set their spawn point
    if (!player.hasPlayedBefore()) {
      var spawnLoc = SetSpawnUtility.SetRandomPlayerSpawn(player);

      if (spawnLoc == null){
        System.out.println("Unable to find a spawn location for " + player.getName());
        return;
      }

      player.teleport(spawnLoc);
    }
  }

}
