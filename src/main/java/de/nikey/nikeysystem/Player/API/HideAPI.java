package de.nikey.nikeysystem.Player.API;

import de.nikey.nikeysystem.DataBases.HideDatabase;
import de.nikey.nikeysystem.NikeySystem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class HideAPI {
    private static final Set<UUID> hiddenPlayers = new HashSet<>();
    private static final Set<UUID> trueHidePlayers = new HashSet<>();
    private static final Set<UUID> immunityPlayers = new HashSet<>();


    public static Set<UUID> getHiddenPlayers() {
        return hiddenPlayers;
    }

    public static Set<UUID> getImmunityPlayers() {
        return immunityPlayers;
    }

    public static Set<UUID> getTrueHidePlayers() {
        return trueHidePlayers;
    }

    public static boolean canSee(UUID observer, UUID target) {
        if (observer.equals(target)) return true;

        if (trueHidePlayers.contains(target)) return false;

        if (hiddenPlayers.contains(target)) {
            if (hasHideImmunity(observer)) return true;
        }

        return !hiddenPlayers.contains(target);
    }

    public static boolean canSee(OfflinePlayer obs, OfflinePlayer tar) {
        UUID observer = obs.getUniqueId();
        UUID target = tar.getUniqueId();
        if (observer.equals(target)) return true;

        if (trueHidePlayers.contains(target)) return false;

        if (hiddenPlayers.contains(target)) {
            if (hasHideImmunity(observer)) return true;
        }

        return !hiddenPlayers.contains(target);
    }

    public static boolean canSee(String o, String t) {
        UUID observer = Bukkit.getPlayerUniqueId(o);
        UUID target = Bukkit.getPlayerUniqueId(t);

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

    public static void trueHidePlayer(UUID playerUUID) {
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

    public static boolean isTrueHidden(UUID playerUUID) {
        return trueHidePlayers.contains(playerUUID);
    }

    public static void updatePlayer(Player player) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (canSee(onlinePlayer.getUniqueId(), player.getUniqueId())) {
                onlinePlayer.showPlayer(NikeySystem.getPlugin() ,player);
            } else {
                onlinePlayer.hidePlayer(NikeySystem.getPlugin(), player);
            }

            if (canSee(player, onlinePlayer)) {
                player.showPlayer(NikeySystem.getPlugin() ,onlinePlayer);
            } else {
                player.hidePlayer(NikeySystem.getPlugin(), onlinePlayer);
            }
        }
    }


    public static void hideStartup() {
        HideDatabase.connect();
        HideDatabase.loadAll();
    }

    public static void hideShutdown() {
        HideDatabase.saveAll();
        HideDatabase.disconnect();
    }
}