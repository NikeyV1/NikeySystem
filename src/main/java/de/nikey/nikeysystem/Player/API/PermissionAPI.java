package de.nikey.nikeysystem.Player.API;

import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PermissionAPI {
    private static final ArrayList<String > admins = new ArrayList<>();
    private static final ArrayList<String> moderators = new ArrayList<>();

    public static void addAdmin(String playername) {
        admins.add(playername);
    }

    public static void removeAdmin(String playername) {
        admins.remove(playername);
    }

    public static void clearAdmins() {
        admins.clear();
    }

    public static boolean isAdmin(String playername) {
        return admins.contains(playername);
    }

    public static ArrayList<String> getAdminsList() {
        return admins;
    }


    public static void addModerator(String playername) {
        moderators.add(playername);
    }

    public static void removeModerator(String playername) {
        moderators.remove(playername);
    }

    public static void clearModerator() {
        moderators.clear();
    }

    public static boolean isModerator(String playername) {
        return moderators.contains(playername);
    }

    public static ArrayList<String> getModeratorList() {
        return moderators;
    }


    public static boolean isOwner(String player) {
        return player.equalsIgnoreCase("NikeyV1");
    }

    public static boolean isSystemUser(Player player) {
        return isAdmin(player.getName()) || isModerator(player.getName()) || isOwner(player.getName());
    }

    public static void clearAll(){
        moderators.clear();
        admins.clear();
    }
}
