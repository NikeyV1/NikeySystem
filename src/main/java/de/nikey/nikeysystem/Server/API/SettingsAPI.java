package de.nikey.nikeysystem.Server.API;

import de.nikey.nikeysystem.NikeySystem;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class SettingsAPI {
    public static final ArrayList<Player> settingsContinue = new ArrayList<>();
    public static final Map<Player, Consumer<String>> inputRequests = new HashMap<>();

    public static boolean isPluginCMDFaked() {
        return NikeySystem.getPlugin().getConfig().getBoolean("system.setting.remove_from_plugincmd");
    }
}
