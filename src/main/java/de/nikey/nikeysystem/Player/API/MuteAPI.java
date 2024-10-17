package de.nikey.nikeysystem.Player.API;

import java.util.HashMap;

public class MuteAPI {
    private static HashMap<String, Long> mutedPlayers = new HashMap<>();

    public static void add(String player, long endTime) {
        mutedPlayers.put(player, endTime);
    }

    public static void remove(String player) {
        mutedPlayers.remove(player);
    }

    public static long getMutedDuration(String player) {
        Long l = mutedPlayers.get(player);
        if (l == 0) {
            return l;
        }
        l = l-System.currentTimeMillis();
        l = l/1000;
        return l;
    }

    public static boolean isMuted(String player) {
        if (mutedPlayers.containsKey(player)) {
            long endTime = mutedPlayers.get(player);
            if (endTime == 0 || endTime > System.currentTimeMillis()) {
                return true;
            } else {
                mutedPlayers.remove(player);
                return false;
            }
        }
        return false;
    }
}
