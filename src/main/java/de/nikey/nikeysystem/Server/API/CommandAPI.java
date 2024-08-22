package de.nikey.nikeysystem.Server.API;

import de.nikey.nikeysystem.NikeySystem;

import java.util.List;

public class CommandAPI {
    private static final List<String> disabledCommands = NikeySystem.getPlugin().getConfig().getStringList("security.SystemShieldUsers");

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
