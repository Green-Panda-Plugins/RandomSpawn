package dev.michaud.greenpanda.randomspawnpoint.util;

import dev.michaud.greenpanda.randomspawnpoint.RandomSpawnpoint;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SetPlayerCompass {

  public static void setPlayerCompassLocation(Player player) {

    if (!RandomSpawnpoint.setPlayerCompassLocation) {
      return;
    }

    Location playerWorldSpawn = SetSpawn.getWorldSpawnNBT(player, RandomSpawnpoint.defaultWorld);

    if (playerWorldSpawn != null) {
      player.setCompassTarget(playerWorldSpawn);
    }

  }

}