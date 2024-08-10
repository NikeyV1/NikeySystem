package de.nikey.nikeysystem.Server.API;

import de.nikey.nikeysystem.NikeySystem;

import java.util.List;

public class CommandAPI {
    private static final List<String> disabledCommands = NikeySystem.getPlugin().getConfig().getStringList("disabled-commands");

    public static void addCommand(String command) {
        disabledCommands.add(command);
    }

    public static boolean removeCommand(String command) {
        if (disabledCommands.contains(command)) {
            disabledCommands.remove(command);
            return true;
        }else {
            return false;
        }
    }

    public static boolean isBlocked(String command) {
        return disabledCommands.contains(command);
    }

    public static List<String> getDisabledCommands() {
        return disabledCommands;
    }
}
