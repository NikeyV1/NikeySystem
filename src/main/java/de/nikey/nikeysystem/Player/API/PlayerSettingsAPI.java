package de.nikey.nikeysystem.Player.API;

import de.nikey.nikeysystem.NikeySystem;

public class PlayerSettingsAPI {
    public static boolean hasMobTargeting(String player) {
        return NikeySystem.getPlugin().getConfig().getBoolean("hide.settings." + player + ".mobtarget");
    }
    public static boolean hasItemPickup(String player) {
        return NikeySystem.getPlugin().getConfig().getBoolean("hide.settings." + player + ".itempickup");
    }

    public static boolean hasCropTrample(String player) {
        return NikeySystem.getPlugin().getConfig().getBoolean("hide.settings." + player + ".croptrample");
    }
}
