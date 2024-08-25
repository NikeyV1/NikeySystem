package de.nikey.nikeysystem.Server.API;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SettingsAPI {
    public static final ArrayList<Player> settingsContinue = new ArrayList<>();
    public static Map<UUID, String> awaitingTextInput = new HashMap<>();
}
