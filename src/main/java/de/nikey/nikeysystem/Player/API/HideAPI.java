package de.nikey.nikeysystem.Player.API;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class HideAPI {
    private static Set<String> hiddenPlayerNames = new HashSet<>();
    private static Set<String> trueHiddenNames = new HashSet<>();
    private static Set<String> hideImmunity = new HashSet<>();
    private static Set<String> trueHideImmunity = new HashSet<>();

    public static Set<String> getHiddenPlayerNames() {
        return hiddenPlayerNames;
    }

    public static void setHiddenPlayerNames(Set<String> hiddenPlayerNames) {
        HideAPI.hiddenPlayerNames = hiddenPlayerNames;
    }

    public static Set<String> getTrueHiddenNames() {
        return trueHiddenNames;
    }

    public static void setTrueHiddenNames(Set<String> trueHiddenNames) {
        HideAPI.trueHiddenNames = trueHiddenNames;
    }

    public static Set<String> getTrueHideImmunity() {
        return trueHideImmunity;
    }

    public static void setTrueHideImmunity(Set<String> trueHideImmunity) {
        HideAPI.trueHideImmunity = trueHideImmunity;
    }

    public static Set<String> getHideImmunity() {
        return hideImmunity;
    }

    public static void setHideImmunity(Set<String> hideImmunity) {
        HideAPI.hideImmunity = hideImmunity;
    }

    public static boolean canSee(Player player , Player hidden) {
        if (player == hidden) {
            return true;
        }
        if (HideAPI.getHiddenPlayerNames().contains(hidden.getName()) ) {
            if (PermissionAPI.isOwner(player.getName()) ) {
                return true;
            } else if (PermissionAPI.isAdmin(player.getName()) && PermissionAPI.isModerator(hidden.getName())) {
                return true;
            } else return getHideImmunity().contains(player.getName()) || getTrueHideImmunity().contains(player.getName());
        }else if (HideAPI.getTrueHiddenNames().contains(hidden.getName())) {
            return PermissionAPI.isOwner(player.getName()) || getTrueHideImmunity().contains(player.getName());
        }else {
            return true;
        }
    }

    public static boolean canSee(String player, String hidden) {
        if (player.equals(hidden)) {
            return true;
        }

        if (HideAPI.getHiddenPlayerNames().contains(hidden) ) {
            if (PermissionAPI.isOwner(player) || (PermissionAPI.isAdmin(player) && PermissionAPI.isModerator(hidden))) {
                return true;
            } else return getHideImmunity().contains(player) || getTrueHideImmunity().contains(player);
        }else if (HideAPI.getTrueHiddenNames().contains(hidden)) {
            return PermissionAPI.isOwner(player) || getTrueHideImmunity().contains(player) ;
        }else {
            return true;
        }
    }
}
