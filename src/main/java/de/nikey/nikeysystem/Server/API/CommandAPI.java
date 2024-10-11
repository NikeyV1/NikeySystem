package de.nikey.nikeysystem.Server.API;

import de.nikey.nikeysystem.NikeySystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandAPI {
    private static final List<String> disabledCommands = NikeySystem.getPlugin().getConfig().getStringList("security.SystemShieldUsers");
    private static HashMap<String, Set<String>> blockedCommands;

    public static HashMap<String, Set<String>> getBlockedCommands() {
        return blockedCommands;
    }

    public static void addPlayerCommand(String playerName, String cmd) {
        if (cmd.startsWith("/")) {
            cmd = cmd.substring(1);
        }

        blockedCommands.putIfAbsent(playerName, new HashSet<>());
        blockedCommands.get(playerName).add(cmd);
    }

    public static void removePlayerCommand(String playerName, String cmd) {

        if (cmd.startsWith("/")) {
            cmd = cmd.substring(1);
        }

        if (blockedCommands.containsKey(playerName)) {
            blockedCommands.get(playerName).remove(cmd);
        }
    }

    public static boolean isPlayerBlocked(String cmd, String player) {

        if (cmd.startsWith("/")) {
            cmd = cmd.substring(1);
        }
        if (blockedCommands.containsKey(player)) return false;

        return blockedCommands.get(player).contains(cmd);
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
