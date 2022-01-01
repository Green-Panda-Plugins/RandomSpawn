package me.greenpanda.randomspawnpoint;

import me.greenpanda.randomspawnpoint.commands.SetRandomSpawnpoint;
import me.greenpanda.randomspawnpoint.events.PlayerJoin;
import me.greenpanda.randomspawnpoint.events.PlayerRespawn;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * <p>RandomSpawnpoint sets each player's spawn to a random position when they first join the
 * world. If a player sets their spawnpoint at a bed or respawn anchor they will spawn there instead
 * unless it becomes destroyed, obstructed, or the respawn anchor looses charge, in which case they
 * will spawn at their original randomly generated spawnpoint.</p>
 * <p>Players with the randomspawnpoint.setspawn permission can manually reset their spawn location
 * by running the /setrandomspawnpoint command.</p>
 *
 * @author Eli Michaud
 * @version 1.0-SNAPSHOT
 * @since 2021-12-31
 */

public final class RandomSpawnpoint extends JavaPlugin {

  //Instance
  private static RandomSpawnpoint plugin;

  public static RandomSpawnpoint getPlugin() {
    return plugin;
  }

  @Override
  public void onEnable() {

    plugin = this;

    //Config
    getConfig().options().copyDefaults(true);
    saveDefaultConfig();

    //Register Events
    getServer().getPluginManager().registerEvents(new PlayerRespawn(), this);
    getServer().getPluginManager().registerEvents(new PlayerJoin(), this);

    //Register Commands
    this.getCommand("setrandomspawnpoint").setExecutor(new SetRandomSpawnpoint());

    //All Done :)
    System.out.println("[RandomSpawnpoint]: RandomSpawnpoint enabled");
  }
}
