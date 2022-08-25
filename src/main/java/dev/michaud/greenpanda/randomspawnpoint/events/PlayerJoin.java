package dev.michaud.greenpanda.randomspawnpoint.events;

import dev.michaud.greenpanda.randomspawnpoint.util.SetSpawn;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class PlayerJoin implements Listener {

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();

    if (!player.hasPlayedBefore()) {
      newPlayerSpawn(player);
    }

  }

  private void newPlayerSpawn(Player player) {

    Location spawnLocation = SetSpawn.setRandomPlayerSpawn(player, player.getWorld());

    if (spawnLocation == null) {
      player.sendMessage(Component.text("Unable to find a safe spawn location after " +
              SetSpawn.FIND_LOCATION_TRIES + " tries. The settings in your config might be too "
              + "strict, or there aren't enough spawnable spaces in your world.")
          .color(NamedTextColor.DARK_RED)
      );
    } else {
      player.teleport(spawnLocation, TeleportCause.PLUGIN);
    }

  }

}