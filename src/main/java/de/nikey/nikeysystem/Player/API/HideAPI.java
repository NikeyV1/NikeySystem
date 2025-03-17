package de.nikey.nikeysystem.Player.API;

import de.nikey.nikeysystem.NikeySystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class HideAPI {
    private static final Set<UUID> hiddenPlayers = new HashSet<>();
    private static final Set<UUID> trueHidePlayers = new HashSet<>();
    private static final Set<UUID> immunityPlayers = new HashSet<>();

    public static boolean canSee(UUID observer, UUID target) {
        if (observer.equals(target)) return true;

        if (trueHidePlayers.contains(target)) return false;

        if (hiddenPlayers.contains(target)) {
            if (hasHideImmunity(observer)) return true;
        }

        return !hiddenPlayers.contains(target);
    }

    public static void hidePlayer(UUID playerUUID) {
        hiddenPlayers.add(playerUUID);
    }

    public static void revealPlayer(UUID playerUUID) {
        hiddenPlayers.remove(playerUUID);
    }

    public static void addTrueHidePlayer(UUID playerUUID) {
        trueHidePlayers.add(playerUUID);
    }

    public static void revealTrueHidePlayer(UUID playerUUID) {
        trueHidePlayers.remove(playerUUID);
    }

    public static void addHideImmunity(UUID playerUUID) {
        immunityPlayers.add(playerUUID);
    }

    public static void removeHideImmunity(UUID playerUUID) {
        immunityPlayers.remove(playerUUID);
    }

    public static boolean hasHideImmunity(UUID playerUUID) {
        return immunityPlayers.contains(playerUUID);
    }

    public static boolean isHidden(UUID playerUUID) {
        return hiddenPlayers.contains(playerUUID);
    }

    public static boolean isTrueHide(UUID playerUUID) {
        return trueHidePlayers.contains(playerUUID);
    }

    public static void updatePlayer(Player player) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (canSee(onlinePlayer.getUniqueId(), player.getUniqueId())) {
                onlinePlayer.showPlayer(NikeySystem.getPlugin() ,player);
            } else {
                onlinePlayer.hidePlayer(NikeySystem.getPlugin(), player);
            }
        }
    }
}
