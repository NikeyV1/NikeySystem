package de.nikey.nikeysystem.Player.Distributor;

import de.nikey.nikeysystem.General.ShieldCause;
import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.HideAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SuppressWarnings("ALL")
public class StatsDistributor {
    public static void statsDistributor(Player player, String[] args) {
        if (args[3].equalsIgnoreCase("help")) {
            player.sendMessage("§7The path 'System/Player/Stats' has following sub-paths: §fInvulnerable <PlayerName>, Fly <PlayerName>, Collidable <PlayerName>, SleepIgnore <PlayerName>, Invisibility <PlayerName>, VisualFire <PlayerName>, Op <Playername>.");
            return;
        }

        Player target;
        if (args.length >= 5) {
            target = Bukkit.getPlayer(args[4]);
        }else {
            target = player;
        }
        if (target == null || !HideAPI.canSee(player,target)) {
            player.sendMessage("§cError: target not found!");
            return;
        }

        if (!PermissionAPI.isAllowedToChange(player.getName(),target.getName(), ShieldCause.STATS_CHANGE)) {
            player.sendMessage("§cError: missing permission");
            return;
        }
        if (args[3].equalsIgnoreCase("Invulnerable")) {
            if (target.isInvulnerable()) {
                target.setInvulnerable(false);
                player.sendMessage(ChatColor.of("#f08d1d") +"Set invulnerability §coff"+ChatColor.of("#f08d1d") +" for "+ target.getName());
            }else {
                target.setInvulnerable(true);
                player.sendMessage(ChatColor.of("#f08d1d") +"Set invulnerability §aon"+ChatColor.of("#f08d1d") +" for "+ target.getName());
            }
        }else if (args[3].equalsIgnoreCase("Fly")) {
            if (target.getAllowFlight()) {
                target.setAllowFlight(false);
                target.setFlying(false);
                player.sendMessage(ChatColor.of("#f08d1d") +"Set flying §coff"+ChatColor.of("#f08d1d") +" for "+ target.getName());
            }else {
                target.setAllowFlight(true);
                target.setFlying(true);
                player.sendMessage(ChatColor.of("#f08d1d") +"Set flying §aon"+ChatColor.of("#f08d1d") +" for "+ target.getName());
            }
        }else if (args[3].equalsIgnoreCase("Collidable")) {
            if (target.isCollidable()) {
                target.setCollidable(false);
                player.sendMessage(ChatColor.of("#f08d1d") +"Set collidable §coff"+ChatColor.of("#f08d1d") +" for "+ target.getName());
            }else {
                target.setCollidable(true);
                player.sendMessage(ChatColor.of("#f08d1d") +"Set collidable §aon"+ChatColor.of("#f08d1d") +" for "+ target.getName());
            }
        }else if (args[3].equalsIgnoreCase("SleepIgnore")) {
            if (target.isSleepingIgnored()) {
                target.setSleepingIgnored(false);
                player.sendMessage(ChatColor.of("#f08d1d") +"Set sleep-ignored §coff"+ChatColor.of("#f08d1d") +" for "+ target.getName());
            }else {
                target.setSleepingIgnored(true);
                player.sendMessage(ChatColor.of("#f08d1d") +"Set sleep-ignored §aon"+ChatColor.of("#f08d1d") +" for "+ target.getName());
            }
        }else if (args[3].equalsIgnoreCase("Invisibility")) {
            if (target.isInvisible()) {
                target.setInvisible(false);
                player.sendMessage(ChatColor.of("#f08d1d") +"Set invisible §coff"+ChatColor.of("#f08d1d") +" for "+ target.getName());
            }else {
                target.setInvisible(true);
                player.sendMessage(ChatColor.of("#f08d1d") +"Set invisible §aon"+ChatColor.of("#f08d1d") +" for "+ target.getName());
            }
        }else if (args[3].equalsIgnoreCase("VisualFire")) {
            if (target.isVisualFire()) {
                target.setVisualFire(false);
                player.sendMessage(ChatColor.of("#f08d1d") +"Set visual-fire §coff"+ChatColor.of("#f08d1d") +" for "+ target.getName());
            }else {
                target.setVisualFire(true);
                player.sendMessage(ChatColor.of("#f08d1d") +"Set visual-fire §aon"+ChatColor.of("#f08d1d") +" for "+ target.getName());
            }
        }else if (args[3].equalsIgnoreCase("Op")) {
            if (target.isOp()) {
                target.setOp(false);
                player.sendMessage(ChatColor.of("#f08d1d") +"Set Operator §coff"+ChatColor.of("#f08d1d") +" for "+ target.getName());
            }else {
                target.setOp(true);
                player.sendMessage(ChatColor.of("#f08d1d") +"Set Operator §aon"+ChatColor.of("#f08d1d") +" for "+ target.getName());
            }
        } else if (args[3].equalsIgnoreCase("Address")) {
            player.sendMessage(ChatColor.of("#f08d1d") +"Address from §f" +target.getName() + ChatColor.of("#f08d1d") + " is §7" + target.getAddress().toString() + "/" +target.getAddress().getAddress().getHostAddress());
        } else if (args[3].equalsIgnoreCase("ClientName")) {
            player.sendMessage(ChatColor.of("#f08d1d") +"Client-Brand-Name from §f" +target.getName() + ChatColor.of("#f08d1d") + " is §7" + target.getClientBrandName());
        }else if (args[3].equalsIgnoreCase("Locale")) {
            player.sendMessage(ChatColor.of("#f08d1d") +"Locale from §f" +target.getName() + ChatColor.of("#f08d1d") + " is §7" + target.locale());
        } else if (args[3].equalsIgnoreCase("Reset")) {
            target.setInvulnerable(false);
            target.setAllowFlight(false);
            target.setFlying(false);
            target.setCollidable(true);
            target.setSleepingIgnored(false);
            target.setInvisible(false);
            target.setVisualFire(false);
            target.setOp(false);
            player.sendMessage("§cReset "+ChatColor.of("#f08d1d") +"stats for "+ target.getName());
        }else if (args[3].equalsIgnoreCase("List")) {
            String playerName = args[4];
            List<String> messages = new ArrayList<>();

            if (target.isInvulnerable()) {
                messages.add("§bInvulnerability");
            }

            if (target.isFlying()) {
                messages.add("§bFlying");
            }

            if (target.isCollidable()) {
                messages.add("§bCollidability");
            }

            if (target.isSleepingIgnored()) {
                messages.add("§bSleepIgnored");
            }

            if (target.isInvisible()) {
                messages.add("§bInvisible");
            }

            if (target.isVisualFire()) {
                messages.add("§bVisualFire");
            }

            if (target.isOp()) {
                messages.add("§bOp");
            }
            messages.add("§b"+target.getClientBrandName());

            messages.add("§b"+target.locale().toString());

            String message = "§7" + playerName + " has ";
            if (messages.isEmpty()) {
                message += "no special stats.";
            } else {
                message += String.join(", ", messages) + ".";
            }

            player.sendMessage(message);
        }
    }
}