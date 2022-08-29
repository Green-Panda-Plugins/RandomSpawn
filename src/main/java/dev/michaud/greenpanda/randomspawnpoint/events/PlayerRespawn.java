package dev.michaud.greenpanda.randomspawnpoint.events;

import dev.michaud.greenpanda.randomspawnpoint.RandomSpawnpoint;
import dev.michaud.greenpanda.randomspawnpoint.util.SetSpawn;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawn implements Listener {

  @EventHandler
  public void onPlayerRespawn(PlayerRespawnEvent event) {

    Player player = event.getPlayer();
    /*Location location = event.getRespawnLocation();*/

    if (event.isBedSpawn()) {
      /*player.sendMessage(Component.text("Spawned in bed").color(NamedTextColor.AQUA));*/
      return;
    }

    if (event.isAnchorSpawn()) {
      /*player.sendMessage(Component.text("Spawned at anchor").color(NamedTextColor.AQUA));*/
      return;
    }

    Location playerWorldSpawn = SetSpawn.getWorldSpawnNBT(player, RandomSpawnpoint.defaultWorld);

    if (playerWorldSpawn != null) {
      player.setBedSpawnLocation(playerWorldSpawn, true);
      event.setRespawnLocation(playerWorldSpawn);
    }

  }

}