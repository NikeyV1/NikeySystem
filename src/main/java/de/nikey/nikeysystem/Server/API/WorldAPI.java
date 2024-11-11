package de.nikey.nikeysystem.Server.API;

import de.nikey.nikeysystem.NikeySystem;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class WorldAPI {

    public static List<String> getAutoStartingWorlds() {
        return NikeySystem.getPlugin().getConfig().getStringList("system.world.autostartworlds");
    }

    public static void addAutoStartToWorld(World world) {
        List<String> worlds = NikeySystem.getPlugin().getConfig().getStringList("system.world.autostartworlds");

        if (!worlds.contains(world)) {
            worlds.add(world);
            NikeySystem.getPlugin().getConfig().set("system.world.autostartworlds", worlds);
            NikeySystem.getPlugin().saveConfig();
        }
    }

    public static void unblockCommandForPlayer(Player player, String command) {
        String playerName = player.getName();
        List<String> blockedCommands = NikeySystem.getPlugin().getConfig().getStringList("blockedCommands." + playerName);

        if (blockedCommands.contains(command)) {
            blockedCommands.remove(command);
            NikeySystem.getPlugin().getConfig().set("blockedCommands." + playerName, blockedCommands);
            NikeySystem.getPlugin().saveConfig();
        }
    }
}
