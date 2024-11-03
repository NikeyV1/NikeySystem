package de.nikey.nikeysystem.Player.Distributor;

import de.nikey.nikeysystem.General.ShieldCause;
import de.nikey.nikeysystem.Player.API.HideAPI;
import de.nikey.nikeysystem.Player.API.MuteAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MuteDistributer {
    public static void muteManager(Player sender, String[] args) {
        String cmd = args[3];
        if (cmd.isEmpty()) return;

        // Mute Command
        if (cmd.equalsIgnoreCase("mute")) {
            if (args.length == 5) {
                Player player = Bukkit.getPlayer(args[4]);
                if (player == null || !HideAPI.canSee(sender, player)) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }

                if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName(),ShieldCause.MUTE)) {
                    sender.sendMessage("§cError: missing permission");
                    return;
                }
                mutePlayer(player, sender, 0);
            } else if (args.length == 6) {
                Player player = Bukkit.getPlayer(args[4]);
                if (player == null || !HideAPI.canSee(sender, player)) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName(),ShieldCause.MUTE)) {
                    sender.sendMessage("§cError: missing permission");
                    return;
                }
                int duration;
                try {
                    duration = Integer.parseInt(args[5]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cError: not a valid number");
                    return;
                }
                // Mute with duration in seconds
                mutePlayer(player, sender, duration);
            }
        }
        // Unmute Command
        else if (cmd.equalsIgnoreCase("unmute")) {
            if (args.length == 5) {
                Player player = Bukkit.getPlayer(args[4]);
                if (player == null || !HideAPI.canSee(sender, player)) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName(),ShieldCause.UNMUTE)) {
                    sender.sendMessage("§cError: missing permission");
                    return;
                }
                unmutePlayer(player, sender);
            }
        }
        // Toggle Mute Command
        else if (cmd.equalsIgnoreCase("togglemute")) {
            if (args.length == 5) {
                Player player = Bukkit.getPlayer(args[4]);
                if (player == null || !HideAPI.canSee(sender, player)) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName(), ShieldCause.TOGGLE_MUTE)) {
                    sender.sendMessage("§cError: missing permission");
                    return;
                }
                toggleMutePlayer(player, sender);
            }
        }
        // Check if a player is muted
        else if (cmd.equalsIgnoreCase("getMuted")) {
            if (args.length == 5) {
                Player player = Bukkit.getPlayer(args[4]);
                if (player == null || !HideAPI.canSee(sender, player)) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                if (MuteAPI.isMuted(player.getName())) {
                    if (MuteAPI.getMutedDuration(player.getName()) == 0) {
                        sender.sendMessage("§7"+player.getName() + " is currently §cmuted §finfinitely");
                    }else {
                        sender.sendMessage("§7"+player.getName() + " is currently §cmuted §7for: §f"+MuteAPI.getMutedDuration(player.getName()) + "sek");
                    }
                } else {
                    sender.sendMessage("§7"+player.getName() + " is §anot muted");
                }
            }
        }
        // Help Command
        else if (cmd.equalsIgnoreCase("help")) {
            sender.sendMessage("§7The path 'System/Player/Mute' has the following sub-paths: §f"
                    + "mute <PlayerName> [Duration], unmute <PlayerName>, togglemute <PlayerName>, isMuted <PlayerName>");
        }
    }

    // Mute a player (0 duration means permanent)
    public static void mutePlayer(Player target, Player sender, int duration) {
        if (MuteAPI.isMuted(target.getName())) {
            sender.sendMessage("§cError: " + target.getName() + " is already muted.");
            return;
        }

        if (duration == 0) {
            if (PermissionAPI.isSystemUser(target)) target.sendMessage(ChatColor.of("#1dc0f0")+"You have been §fpermanently §cmuted");
            MuteAPI.add(target.getName(), 0);
        } else {
            if (PermissionAPI.isSystemUser(target))target.sendMessage(ChatColor.of("#1dc0f0")+"You have been §cmuted"+ChatColor.of("#1dc0f0")+" for §f" + duration +ChatColor.of("#1dc0f0")+ " seconds");
            MuteAPI.add(target.getName(), System.currentTimeMillis() + (duration * 1000L));
        }
        sender.sendMessage(ChatColor.of("#1dc0f0")+target.getName() + " has been §cmuted");
    }

    // Unmute a player
    public static void unmutePlayer(Player target, Player sender) {
        if (!MuteAPI.isMuted(target.getName())) {
            sender.sendMessage("§cError: " + target.getName() + " is not muted");
            return;
        }

        if (PermissionAPI.isSystemUser(target))target.sendMessage(ChatColor.of("#1dc0f0")+"You have been §aunmuted");
        sender.sendMessage(ChatColor.of("#1dc0f0")+target.getName() + " has been §aunmuted");

        // Remove from muted players map
        MuteAPI.remove(target.getName());
    }

    public static void toggleMutePlayer(Player target, Player sender) {
        if (MuteAPI.isMuted(target.getName())) {
            unmutePlayer(target, sender);
        } else {
            mutePlayer(target, sender, 0); // Default to permanent mute if no duration is provided
        }
    }
}
