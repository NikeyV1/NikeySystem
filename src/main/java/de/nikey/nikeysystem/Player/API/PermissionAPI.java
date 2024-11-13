package de.nikey.nikeysystem.Player.API;

import de.nikey.nikeysystem.General.ShieldCause;
import de.nikey.nikeysystem.Security.API.SystemShieldAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Objects;

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

    public static String getOwner() {
        return "NikeyV1";
    }

    public static boolean isSystemUser(Player player) {
        return isAdmin(player.getName()) || isModerator(player.getName()) || isOwner(player.getName());
    }

    public static boolean isSystemUser(String  player) {
        return isAdmin(player) || isModerator(player) || isOwner(player);
    }

    public static boolean isAllowedToChange(String player, String target, ShieldCause cause) {
        if (isSystemUser(player)) {

            if (Objects.equals(player, target))return true;
            if (SystemShieldAPI.isShieldUser(target)){
                Player p = Bukkit.getPlayer(target);
                if (p == null)return false;

                Component textComponent = Component.text("System Shield blocked cause: ")
                        .color(NamedTextColor.DARK_GRAY)
                        .append(Component.text(cause.name()).color(NamedTextColor.DARK_AQUA))
                        .append(Component.text(" from ")).color(NamedTextColor.DARK_GRAY)
                        .append(Component.text(player).color(NamedTextColor.WHITE));

                p.sendActionBar(textComponent);
            }

            if (!isSystemUser(target)) return true;

            if (isOwner(player)) {
                return true;
            }else if (isAdmin(player)) {
                return isModerator(target) || isAdmin(target);
            } else if (isModerator(player)) {
                return isModerator(target) ;
            }else {
                return false;
            }
        }else {
            return false;
        }
    }
}
