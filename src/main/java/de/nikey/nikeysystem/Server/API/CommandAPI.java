package de.nikey.nikeysystem.Server.API;

import de.nikey.nikeysystem.NikeySystem;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandAPI {
    private static final List<String> disabledCommands = NikeySystem.getPlugin().getConfig().getStringList("security.SystemShieldUsers");

    public static List<String> getPlayerBlocks(Player player) {
        return NikeySystem.getPlugin().getConfig().getStringList("blockedCommands." + player.getName());
    }

    public static void blockCommandForPlayer(Player player, String command) {
        String playerName = player.getName();
        List<String> blockedCommands = NikeySystem.getPlugin().getConfig().getStringList("blockedCommands." + playerName);

        if (!blockedCommands.contains(command)) {
            blockedCommands.add(command);
            NikeySystem.getPlugin().getConfig().set("blockedCommands." + playerName, blockedCommands);
            NikeySystem.getPlugin().saveConfig();
        }
    }

    // API: Spieler-Befehl entsperren
    public static void unblockCommandForPlayer(Player player, String command) {
        String playerName = player.getName();
        List<String> blockedCommands = NikeySystem.getPlugin().getConfig().getStringList("blockedCommands." + playerName);

        if (blockedCommands.contains(command)) {
            blockedCommands.remove(command);
            NikeySystem.getPlugin().getConfig().set("blockedCommands." + playerName, blockedCommands);
            NikeySystem.getPlugin().saveConfig();
        }
    }

    // API: Prüfen, ob ein Befehl für einen Spieler blockiert ist
    public static boolean isCommandBlockedForPlayer(Player player, String command) {
        String playerName = player.getName();
        List<String> blockedCommands = NikeySystem.getPlugin().getConfig().getStringList("blockedCommands." + playerName);
        return blockedCommands.contains(command);
    }

    public static void addCommand(String command) {
        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        disabledCommands.add(command);
    }

    public static void removeCommand(String command) {

        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        disabledCommands.remove(command);
    }

    public static boolean isBlocked(String command) {

        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        return disabledCommands.contains(command);
    }

    public static List<String> getDisabledCommands() {
        return disabledCommands;
    }
}
