package de.nikey.nikeysystem.Server.API;

import de.nikey.nikeysystem.NikeySystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SettingsAPI {
    public static final ArrayList<Player> settingsContinue = new ArrayList<>();
    public static final Map<Player, Consumer<String>> inputRequests = new HashMap<>();

    public static boolean isPluginCMDFaked() {
        return NikeySystem.getPlugin().getConfig().getBoolean("system.setting.remove_from_plugincmd");
    }

    public static void setupFilter() {
        if (NikeySystem.getPlugin().getConfig().getBoolean("")) {
        }
    }

}
