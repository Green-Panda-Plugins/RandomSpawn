package dev.michaud.greenpanda.randomspawnpoint.commands;

import dev.michaud.greenpanda.randomspawnpoint.async.LocationCache;
import io.papermc.lib.PaperLib;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.jetbrains.annotations.NotNull;

public class AsyncTest implements CommandExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
      @NotNull String label, @NotNull String[] args) {

    if (!(sender instanceof Player player)) {
      sender.sendMessage("You must be a player to run that command");
      return true;
    }

    Location location = LocationCache.getLocation();

    if (location == null) {
      player.sendMessage(Component.text("Unable to find a suitable spawn location")
          .color(NamedTextColor.RED));
      return true;
    }

    int x = location.getBlockX();
    int y = location.getBlockY();
    int z = location.getBlockZ();

    player.sendMessage(Component.text("Set your spawn point to " + x + ", " + y + ", " + z));
    PaperLib.teleportAsync(player, location, TeleportCause.COMMAND);

    return true;

  }

}