package me.greenpanda.randomspawnpoint.commands;

import me.greenpanda.randomspawnpoint.SetSpawnUtility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Sets the player's spawn point to a random position as if they had logged in to the server for the
 * first time, then teleports them there. If a suitable spawn location wasn't found, the command
 * send a message to the player then quits. Requires the randomspawnpoint.setspawn permission.
 * On success, will print to the player their new location.
 * Must be run by a player.
 */
public class SetRandomSpawnpoint implements CommandExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
      @NotNull String label, @NotNull String[] args) {

    if (!(sender instanceof Player player)) {
      System.out.println("Command must be run by a player");
      return true; //Not a player.
    }

    if (!player.hasPermission("randomspawnpoint.setspawn")) {
      player.sendMessage("You don't have the required permission to execute this command");
      return true; //Player doesn't have the required permission.
    }

    System.out.println("[RandomSpawnpoint]: Generating random spawnpoint for " + player.getName());

    var spawnLoc = SetSpawnUtility.SetRandomPlayerSpawn(player);
    if (spawnLoc == null){
      player.sendMessage("Unable to find a suitable spawn location");
      return true;
    }

    player.sendMessage(
        "Set your spawn point to " + spawnLoc.getBlockX() + ", " + spawnLoc.getBlockY() + ", "
            + spawnLoc.getBlockX() + ".");
    player.teleport(spawnLoc.getBlock().getLocation().add(.5, 0, .5));

    return true;
  }
}
