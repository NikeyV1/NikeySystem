package de.nikey.nikeysystem.Security.API;

import de.nikey.nikeysystem.NikeySystem;

import java.util.HashMap;
import java.util.List;

public class SystemShieldAPI {
    private static final List<String> shieldUsers = NikeySystem.getPlugin().getConfig().getStringList("");
    public static final HashMap<String, String> shieldRequest = new HashMap<>();
    public static final HashMap<String, String> disableShieldRequest = new HashMap<>();

    public static void addShieldUser(String user) {
        shieldUsers.add(user);
    }

    public static boolean removeShieldUser(String user) {
        if (shieldUsers.contains(user)) {
            shieldUsers.remove(user);
            return true;
        }else {
            return false;
        }
    }

    public static boolean isShieldUser(String user) {
        return shieldUsers.contains(user);
    }

    public static List<String> getShieldUsers() {
        return shieldUsers;
    }
}
