package de.nikey.nikeysystem.Player.Distributor;

import de.nikey.nikeysystem.DataBases.PermissionDatabase;
import de.nikey.nikeysystem.General.ShieldCause;
import de.nikey.nikeysystem.Player.API.HideAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.PermissionRole;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.*;

public class PermissionDistributor {

    public static void permissionDistributor(Player sender, String[] args) {
        String cmd = args[3];
        if (cmd.isEmpty()) return;
        String basePerm = "system.player.permissions.";

        if (cmd.equalsIgnoreCase("set")) {
            if (!PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "set") && !PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "*")) return;
            if (args.length == 6) {
                String roleId = args[5].toUpperCase(Locale.US);
                PermissionRole targetRole = PermissionAPI.get(roleId);
                if (targetRole == null) {
                    sender.sendMessage(Component.text("This role doesn't exist").color(NamedTextColor.RED));
                    return;
                }

                if (!PermissionAPI.canAssignRole(sender.getUniqueId(), targetRole)) {
                    sender.sendMessage(Component.text("You can't assign a role equal to or higher than your own.", NamedTextColor.RED));
                    return;
                }

                OfflinePlayer target = Bukkit.getOfflinePlayer(args[4]);

                if (target.getName() == null) {
                    sender.sendMessage(Component.text("Target not found").color(NamedTextColor.RED));
                    return;
                }

                PermissionRole playerRole = PermissionAPI.getRole(target.getUniqueId());
                if (playerRole != null) {
                    if (playerRole.getLevel() >= targetRole.getLevel()) {
                        sender.sendMessage(Component.text("Player has a stronger role").color(NamedTextColor.RED));
                        return;
                    }
                }

                PermissionDatabase.setRole(target.getUniqueId(), targetRole.getName().toUpperCase(Locale.US));
                PermissionAPI.playerRoles.put(target.getUniqueId(),targetRole.getName().toUpperCase(Locale.US));
                sender.sendMessage(Component.text()
                        .append(Component.text("You assigned ").color(NamedTextColor.GREEN))
                        .append(Component.text(target.getName()).color(NamedTextColor.YELLOW))
                        .append(Component.text(" the role ").color(NamedTextColor.GREEN))
                        .append(Component.text(targetRole.getName()))
                        .append(Component.text(" successfully!").color(NamedTextColor.GREEN)));
                return;
            }
        }
        if (args.length == 5 && cmd.equalsIgnoreCase("remove")) {
            if (!PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "remove") && !PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "*")) return;
            String playerName = args[4];
            UUID uuid = Bukkit.getPlayerUniqueId(playerName);
            if (uuid == null) {
                sender.sendMessage(Component.text("Player not found").color(NamedTextColor.RED));
                return;
            }

            if (!PermissionAPI.playerRoles.containsKey(uuid)) {
                sender.sendMessage(Component.text("This player doesn't have a role").color(NamedTextColor.YELLOW));
                return;
            }

            if (!PermissionAPI.hasHigherLevelThan(sender.getUniqueId(),uuid)) {
                sender.sendMessage(Component.text("You don't have enough permissions").color(NamedTextColor.RED));
                return;
            }

            PermissionAPI.playerRoles.remove(uuid);
            PermissionDatabase.removePlayerRole(uuid);

            sender.sendMessage(Component.text("Role from " + playerName + " removed").color(NamedTextColor.GREEN));
        }

        if (cmd.equalsIgnoreCase("list")) {
            if (!PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "list") && !PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "*")) return;
            OfflinePlayer target = sender;

            if (args.length == 5) {
                target = Bukkit.getOfflinePlayer(args[4]);
            }

            if (target.getName() == null) {
                sender.sendMessage(Component.text("Target not found").color(NamedTextColor.RED));
                return;
            }


            PermissionRole targetRole = PermissionAPI.getRole(target.getUniqueId());
            if (targetRole == null) {
                sender.sendMessage(Component.text()
                        .append(Component.text(target.getName(), NamedTextColor.YELLOW))
                        .append(Component.text(" has no role.", NamedTextColor.GRAY)));
                return;
            }

            sender.sendMessage(Component.text("» ").color(NamedTextColor.DARK_GRAY)
                    .append(Component.text("Role of ").color(NamedTextColor.GRAY))
                    .append(Component.text(target.getName()).color(NamedTextColor.WHITE))
                    .append(Component.text(": ").color(NamedTextColor.GRAY))
                    .append(Component.text(targetRole.getName()).color(NamedTextColor.WHITE)));

            sender.sendMessage(Component.text("» Level: ").color(NamedTextColor.DARK_GRAY)
                    .append(Component.text(String.valueOf(targetRole.getLevel())).color(NamedTextColor.YELLOW)));

            sender.sendMessage(Component.text("» Permissions:").color(NamedTextColor.DARK_GRAY));

            for (String perm : targetRole.getPermissions()) {
                sender.sendMessage(Component.text(" - ").color(NamedTextColor.DARK_GRAY)
                        .append(Component.text(perm).color(NamedTextColor.WHITE)));
            }
        }

        if (cmd.equalsIgnoreCase("info")) {
            if (!PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "info") && !PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "*")) return;
            if (args.length == 4) {
                List<PermissionRole> roles = PermissionAPI.getSortedRoles();

                for (PermissionRole role : roles) {
                    // Zähle, wie viele Spieler diese Rolle aktuell haben
                    long count = PermissionAPI.playerRoles.values().stream()
                            .filter(r -> r.equalsIgnoreCase(role.getName()))
                            .count();

                    Component line = Component.text("[", NamedTextColor.DARK_GRAY)
                            .append(Component.text(role.getName(), NamedTextColor.AQUA))
                            .append(Component.text("] ", NamedTextColor.DARK_GRAY))
                            .append(Component.text("Level: ", NamedTextColor.GRAY))
                            .append(Component.text(String.valueOf(role.getLevel()), NamedTextColor.YELLOW))
                            .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                            .append(Component.text(role.getPermissions().size() + " perms", NamedTextColor.GREEN))
                            .append(Component.text(" | ", NamedTextColor.DARK_GRAY))
                            .append(Component.text(count + " player" + (count == 1 ? "" : "s"), NamedTextColor.GOLD));

                    sender.sendMessage(line);
                }
            } else if (args.length == 5) {
                String roleName = args[4].toUpperCase();
                PermissionRole role = PermissionAPI.get(roleName);

                if (role == null) {
                    sender.sendMessage(Component.text("That role does not exist").color(NamedTextColor.RED));
                    return;
                }

                sender.sendMessage(Component.text("» ", NamedTextColor.DARK_GRAY)
                                .append(Component.text("Details for role: ", NamedTextColor.GRAY))
                                .append(Component.text(role.getName(), NamedTextColor.WHITE)));

                sender.sendMessage(Component.text("• ", NamedTextColor.DARK_GRAY)
                                .append(Component.text("Level: ", NamedTextColor.GRAY))
                                .append(Component.text(String.valueOf(role.getLevel()), NamedTextColor.AQUA)));

                sender.sendMessage(Component.text("• ", NamedTextColor.DARK_GRAY)
                                .append(Component.text("Permissions: ", NamedTextColor.GRAY))
                                .append(Component.text(String.valueOf(role.getPermissions().size()), NamedTextColor.GREEN)));

                for (String permission : role.getPermissions()) {
                    sender.sendMessage(
                            Component.text("  - ", NamedTextColor.DARK_GRAY)
                                    .append(Component.text(permission, NamedTextColor.WHITE))
                    );
                }
            }
        }else if (cmd.equalsIgnoreCase("reload")) {
            if (!PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "reload") && !PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "*")) return;

            PermissionAPI.loadRoles();
            sender.sendMessage(Component.text("Reloaded the roles").color(NamedTextColor.GREEN));
        }
    }
}
