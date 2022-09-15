package dev.michaud.greenpanda.randomspawnpoint;

import dev.michaud.greenpanda.randomspawnpoint.async.LocationCache;
import dev.michaud.greenpanda.randomspawnpoint.commands.AsyncTest;
import dev.michaud.greenpanda.randomspawnpoint.commands.SetRandomSpawnpoint;
import dev.michaud.greenpanda.randomspawnpoint.events.PlayerJoin;
import dev.michaud.greenpanda.randomspawnpoint.events.PlayerRespawn;
import dev.michaud.greenpanda.randomspawnpoint.util.ServerProperties;
import io.papermc.lib.PaperLib;
import java.util.HashSet;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * <p>RandomSpawnpoint sets each player's spawn to a random position when they first join the
 * world. If a player sets their spawnpoint at a bed or respawn anchor they will spawn there instead
 * unless it becomes destroyed, obstructed, or the respawn anchor looses charge, in which case they
 * will spawn at their original randomly generated spawnpoint.</p>
 *
 * @author PandaDev
 */
public final class RandomSpawnpoint extends JavaPlugin {

  //Config
  public static World defaultWorld;
  public static FileConfiguration config;
  public static int spawnRadius;
  public static boolean setPlayerCompassLocation;
  public static HashSet<Material> blockBlacklist = new HashSet<>();

  private static RandomSpawnpoint plugin;

  public static RandomSpawnpoint getPlugin() {
    return plugin;
  }

  @Override
  public void onEnable() {

    plugin = this;

    getServer().getPluginManager().registerEvents(new PlayerRespawn(), this);
    getServer().getPluginManager().registerEvents(new PlayerJoin(), this);

    getCommand("setrandomspawnpoint").setExecutor(new SetRandomSpawnpoint());
    getCommand("asynctest").setExecutor(new AsyncTest());

    loadConfig();

    PaperLib.suggestPaper(plugin);

    LocationCache.generateLocations(10);

  }

  private void loadConfig() {
    config = getConfig();
    config.options().copyDefaults(true);
    saveDefaultConfig();

    spawnRadius = config.getInt("SpawnDistance");
    setPlayerCompassLocation = config.getBoolean("SetPlayerCompassLocation");
    blockBlacklist = new HashSet<>();
    config.getStringList("BlockBlacklist").forEach(e -> blockBlacklist.add(Material.valueOf(e)));
    defaultWorld = getWorldFromConfig();
  }

  private World getWorldFromConfig() {
    String w = ServerProperties.getString("level-name");

    if (w == null) {
      return getServer().getWorlds().get(0);
    }

    World world = getServer().getWorld(w);

    if (world == null) {
      return getServer().getWorlds().get(0);
    }

    return world;
  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
  }
}