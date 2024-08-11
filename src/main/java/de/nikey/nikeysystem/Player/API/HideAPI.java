package de.nikey.nikeysystem.Player.API;

import org.bukkit.Bukkit;
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
        if (HideAPI.getHiddenPlayerNames().contains(hidden.getName()) ) {
            return PermissionAPI.isOwner(player.getName()) || PermissionAPI.isAdmin(player.getName()) && player != hidden;
        }else if (HideAPI.getTrueHiddenNames().contains(hidden.getName())) {
            return PermissionAPI.isOwner(player.getName()) && player != hidden;
        }else {
            return true;
        }
    }
}
