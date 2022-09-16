package dev.michaud.greenpanda.randomspawnpoint.events;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import dev.michaud.greenpanda.randomspawnpoint.RandomSpawnpoint;
import dev.michaud.greenpanda.randomspawnpoint.util.SetPlayerCompass;
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

    if (event.isBedSpawn() || event.isAnchorSpawn()) {
      return;
    }

    Location playerWorldSpawn = SetSpawn.getWorldSpawnNBT(player, RandomSpawnpoint.defaultWorld);

    if (playerWorldSpawn != null) {

      player.setBedSpawnLocation(playerWorldSpawn, true);
      event.setRespawnLocation(playerWorldSpawn);

    }

  }

  @EventHandler
  public void onPostPlayerRespawn(PlayerPostRespawnEvent event) {

    Player player = event.getPlayer();
    SetPlayerCompass.setPlayerCompassLocation(player);

  }

}