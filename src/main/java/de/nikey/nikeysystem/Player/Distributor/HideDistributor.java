package de.nikey.nikeysystem.Player.Distributor;

import de.nikey.nikeysystem.General.ShieldCause;
import de.nikey.nikeysystem.Player.API.HideAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.Player.Settings.HideSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HideDistributor {


    public static void hideDistributor(Player player, String[] args) {
        String basePerm = "system.player.hide.";
        if (args[3].equalsIgnoreCase("ToggleHide")) {
            if (!PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "togglehide") && !PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "*")) return;
            if (args.length == 6) {
                toggleHide(player,args[4], args[5].equalsIgnoreCase("message"));
            }else if (args.length == 5){
                toggleHide(player,args[4],false);
            }else if (args.length == 4){
                toggleHide(player, player.getName(),false);
            }
        } else if (args[3].equalsIgnoreCase("ToggleTrueHide")) {
            if (!PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "toggletruehide") && !PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "*")) return;
            if (args.length == 6) {
                toggleTrueHide(player, args[4], args[5].equalsIgnoreCase("message"));
            }else if (args.length == 5){
                toggleTrueHide(player, args[4], false);
            }else if (args.length == 4){
                toggleTrueHide(player, player.getName(), false);
            }
        } else if (args[3].equalsIgnoreCase("ToggleImmunity")) {
            if (!PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "toggleimmunity") && !PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "*")) return;
            if (args.length == 4) {
                toggleImmunity(player, player.getName());
            }else if (args.length == 5){
                toggleImmunity(player, args[4]);
            }
        } else if (args[3].equalsIgnoreCase("Settings")) {
            HideSettings.openSettingsMenu(player);
        }
        if (args[3].equalsIgnoreCase("List")) {
            if (PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "toggletruehide") || PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "*")) {
                String playerName = "";
                if (args.length == 4) {
                    playerName = player.getName();
                }else if (args.length == 5){
                    playerName = args[4];
                }
                List<String> messages = new ArrayList<>();

                UUID playerId = Bukkit.getPlayerUniqueId(playerName);

                if (HideAPI.hasHideImmunity(playerId)) {
                    messages.add("§bHide Immunity");
                }

                if (HideAPI.isHidden(playerId)) {
                    messages.add("§bHidden");
                }

                if (HideAPI.isTrueHidden(playerId)) {
                    messages.add("§3True Hidden");
                }

                String message = "§7" + playerName + " has ";
                if (messages.isEmpty()) {
                    message += "no special statuses.";
                } else {
                    message += String.join(", ", messages) + ".";
                }

                player.sendMessage(message);
            }else {
                String playerName = args[4];
                List<String> messages = new ArrayList<>();
                UUID playerId = Bukkit.getPlayerUniqueId(playerName);

                if (HideAPI.canSee(player.getName(),playerName)) {
                    if (HideAPI.hasHideImmunity(playerId)) {
                        messages.add("§bHide Immunity");
                    }

                    if (HideAPI.isHidden(playerId)) {
                        messages.add("§bHidden");
                    }
                }


                String message = "§7" + playerName + " has ";
                if (messages.isEmpty()) {
                    message += "no special statuses.";
                } else {
                    message += String.join(", ", messages) + ".";
                }

                player.sendMessage(message);
            }
        }
    }

    public static void toggleImmunity(Player player, String targetName) {
        OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetName);

        if (!PermissionAPI.isAllowedToChange(player.getName(), targetName, ShieldCause.HIDE_IMMUNITY)) {
            player.sendMessage(Component.text("Error: missing permission to change player").color(NamedTextColor.RED));
            return;
        }

        if (HideAPI.hasHideImmunity(offlineTarget.getUniqueId())) {
            HideAPI.removeHideImmunity(offlineTarget.getUniqueId());

            Player target = Bukkit.getPlayer(offlineTarget.getUniqueId());
            if (target != null) HideAPI.updatePlayer(target);

            if (player == offlineTarget) {
                player.sendMessage(Component.text("Removed your hide immunity").color(NamedTextColor.DARK_GRAY));
            }else {
                player.sendMessage(Component.text("Hide immunity removed from ").color(NamedTextColor.DARK_GRAY)
                        .append(Component.text(targetName).color(NamedTextColor.WHITE)));
            }
        } else {
            HideAPI.addHideImmunity(offlineTarget.getUniqueId());

            Player target = Bukkit.getPlayer(offlineTarget.getUniqueId());
            if (target != null) HideAPI.updatePlayer(target);

            if (player == offlineTarget) {
                player.sendMessage(Component.text("Added your hide immunity").color(NamedTextColor.DARK_GRAY));
            }else {
                player.sendMessage(Component.text("Hide immunity added for ").color(NamedTextColor.DARK_GRAY)
                        .append(Component.text(targetName).color(NamedTextColor.WHITE)));
            }
        }
    }

    public static void toggleHide(Player player, String targetName, boolean message) {
        OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetName);

        if (!PermissionAPI.isAllowedToChange(player.getName(), targetName, ShieldCause.HIDE_HIDE)) {
            player.sendMessage(Component.text("Error: missing permission to change player").color(NamedTextColor.RED));
            return;
        }

        if (!HideAPI.isHidden(offlineTarget.getUniqueId())) {
            HideAPI.hidePlayer(offlineTarget.getUniqueId());
            if (message && offlineTarget.isOnline()) {
                Player target = Bukkit.getPlayer(offlineTarget.getUniqueId());
                if (target != null) {
                    Team playerTeam = target.getScoreboard().getPlayerTeam(target);

                    if (playerTeam == null || playerTeam.prefix().equals(Component.empty())) {
                        Bukkit.broadcast(Component.text(target.getName() + " left the game").color(NamedTextColor.YELLOW));
                    }else {
                        Bukkit.broadcast(playerTeam.prefix().color(playerTeam.color()).append(Component.text(target.getName() + " left the game").color(NamedTextColor.YELLOW)));
                    }
                }
            }
            Player target = Bukkit.getPlayer(offlineTarget.getUniqueId());
            if (target != null) HideAPI.updatePlayer(target);

            if (player == offlineTarget) {
                player.sendMessage(Component.text("You're now hidden").color(NamedTextColor.DARK_GRAY));
            }else {
                player.sendMessage(Component.text(targetName).append(Component.text(" is now hidden").color(NamedTextColor.DARK_GRAY)));
            }
        }else {
            HideAPI.revealPlayer(offlineTarget.getUniqueId());
            if (message && offlineTarget.isOnline()) {
                Player target = Bukkit.getPlayer(offlineTarget.getUniqueId());
                if (target != null) {
                    Team playerTeam = target.getScoreboard().getPlayerTeam(target);

                    if (playerTeam == null || playerTeam.prefix().equals(Component.empty())) {
                        Bukkit.broadcast(Component.text(target.getName() + " joined the game").color(NamedTextColor.YELLOW));
                    }else {
                        Bukkit.broadcast(playerTeam.prefix().color(playerTeam.color()).append(Component.text(target.getName() + " joined the game").color(NamedTextColor.YELLOW)));
                    }
                }
            }

            Player target = Bukkit.getPlayer(offlineTarget.getUniqueId());
            if (target != null) HideAPI.updatePlayer(target);

            if (player == offlineTarget) {
                player.sendMessage(Component.text("You're now shown").color(NamedTextColor.DARK_GRAY));
            }else {
                player.sendMessage(Component.text(targetName).append(Component.text(" is now shown").color(NamedTextColor.DARK_GRAY)));
            }
        }
    }

    public static void toggleTrueHide(Player player, String targetName, boolean message) {
        OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetName);

        if (!PermissionAPI.isAllowedToChange(player.getName(), targetName, ShieldCause.HIDE_HIDE)) {
            player.sendMessage(Component.text("Error: missing permission to change player").color(NamedTextColor.RED));
            return;
        }

        if (!HideAPI.isTrueHidden(offlineTarget.getUniqueId())) {
            HideAPI.trueHidePlayer(offlineTarget.getUniqueId());
            if (message && offlineTarget.isOnline()) {
                Player target = Bukkit.getPlayer(offlineTarget.getUniqueId());
                if (target != null) {
                    Team playerTeam = target.getScoreboard().getPlayerTeam(target);

                    if (playerTeam == null || playerTeam.prefix().equals(Component.empty())) {
                        Bukkit.broadcast(Component.text(target.getName() + " left the game").color(NamedTextColor.YELLOW));
                    }else {
                        Bukkit.broadcast(playerTeam.prefix().color(playerTeam.color()).append(Component.text(target.getName() + " left the game").color(NamedTextColor.YELLOW)));
                    }
                }
            }
            Player target = Bukkit.getPlayer(offlineTarget.getUniqueId());
            if (target != null) HideAPI.updatePlayer(target);

            if (player == offlineTarget) {
                player.sendMessage(Component.text("You're now true hidden").color(NamedTextColor.DARK_GRAY));
            }else {
                player.sendMessage(Component.text(targetName).append(Component.text(" is now true hidden").color(NamedTextColor.DARK_GRAY)));
            }
        }else {
            HideAPI.revealTrueHidePlayer(offlineTarget.getUniqueId());
            if (message && offlineTarget.isOnline()) {
                Player target = Bukkit.getPlayer(offlineTarget.getUniqueId());
                if (target != null) {
                    Team playerTeam = target.getScoreboard().getPlayerTeam(target);

                    if (playerTeam == null || playerTeam.prefix().equals(Component.empty())) {
                        Bukkit.broadcast(Component.text(target.getName() + " joined the game").color(NamedTextColor.YELLOW));
                    }else {
                        Bukkit.broadcast(playerTeam.prefix().color(playerTeam.color()).append(Component.text(target.getName() + " joined the game").color(NamedTextColor.YELLOW)));
                    }
                }
            }

            Player target = Bukkit.getPlayer(offlineTarget.getUniqueId());
            if (target != null) HideAPI.updatePlayer(target);

            if (player == offlineTarget) {
                player.sendMessage(Component.text("You're now shown").color(NamedTextColor.DARK_GRAY));
            }else {
                player.sendMessage(Component.text(targetName).append(Component.text(" is now shown").color(NamedTextColor.DARK_GRAY)));
            }
        }
    }
}