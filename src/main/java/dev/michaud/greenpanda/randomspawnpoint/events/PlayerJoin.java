package dev.michaud.greenpanda.randomspawnpoint.events;

import dev.michaud.greenpanda.randomspawnpoint.RandomSpawnpoint;
import dev.michaud.greenpanda.randomspawnpoint.util.SetPlayerCompass;
import dev.michaud.greenpanda.randomspawnpoint.util.SetSpawn;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class PlayerJoin implements Listener {

  @EventHandler
  public void onPlayerSpawn(PlayerSpawnLocationEvent event) {

    Player player = event.getPlayer();

    if (player.hasPlayedBefore()) {
      return;
    }

    Location spawnLocation = SetSpawn.setRandomPlayerSpawn(player, player.getWorld());

    if (spawnLocation != null) {
      event.setSpawnLocation(spawnLocation);
    } else {
      player.sendMessage(Component.text("Unable to find a safe spawn location.", NamedTextColor.RED));
    }

  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {

    Player player = event.getPlayer();
    JavaPlugin plugin = RandomSpawnpoint.getPlugin();
    plugin.getServer().getScheduler().runTaskLater(plugin, () -> SetPlayerCompass.setPlayerCompassLocation(player), 1);

  }

}