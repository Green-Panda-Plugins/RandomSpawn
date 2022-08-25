## Overview
Random Spawnpoint changes the vanilla spawning behavior. When a player joins the world for the first time, they will be placed at a random position according to the options set in the config. The goal is for new players, who may join the server after large amounts of resources have already been collected, to have more to do in the early game and must fend for themselves for at least the first few days.

This plugin is made to work with the [Player Compass][1] plugin, which adds a new item that lets players find eachother without using coordinates.

It is also recommended for the player’s F3 position to be hidden from them, the easiest way to achieve this is by setting the gamerule `reducedDebugInfo` to `true`.

[1]: https://github.com/Green-Panda-Plugins/PlayerCompass

## Config
You can change the parameters of what the plugin will consider a valid spawn in the config file. If you are starting a new world, it is recommended to do this before you join for the first time. 

The plugin will always choose a random location in a circle with the radius being the distance you set. 

The block blacklist will prevent players from spawning on any block of that type (or inside of, in the case of liquids).

## Commands
If you want to manually reset your spawn point to a random location, you can run `/setrandomspawnpoint`. This will also teleport you to the generated location. Only players with the `randomspawnpoint.setspawn` permission can run this command.

## Troubleshooting
If players aren't spawning at a random location, the plugin might have been unable to generate a valid location according to your config. 100 attempts will be made to find a valid spawn position, after which it will default to the world spawn. You can change the settings in the config to allow for more spawnable spaces, and then regenerate your spawn position using the `/setrandomspawnpoint` command.

If players are spawning in the wrong world, then make sure that `level-name` in your `server.properties` matches the world file that you want players to spawn in. Normally this will just be called "world".
