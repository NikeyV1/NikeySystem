package de.nikey.nikeysystem.Player.Distributor;

import de.nikey.nikeysystem.General.ShieldCause;
import de.nikey.nikeysystem.Player.API.HideAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import net.md_5.bungee.api.ChatColor;
import net.querz.nbt.io.NBTDeserializer;
import net.querz.nbt.io.NamedTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("ALL")
public class StatsDistributor {
    public static void statsDistributor(Player player, String[] args) {
        String basePerm = "system.player.stats.";

        if (args[3].equalsIgnoreCase("health")) {
            if (!PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "health") && !PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "*")) return;
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[4]);
            UUID uuid = target.getUniqueId();
            File file = new File(Bukkit.getWorld("world").getWorldFolder(), "playerdata/" + uuid + ".dat");

            if (!file.exists()) {
                player.sendMessage(ChatColor.RED + "Keine Daten für " + args[0] + " gefunden.");
                return;
            }
            try {
                NBTDeserializer deserializer = new NBTDeserializer();
                NamedTag tag = deserializer.fromFile(file);
                CompoundTag data = (CompoundTag) tag.getTag();

                float health = data.getFloat("Health");
                int xp = data.getInt("XpLevel");
                String lastKnownName = data.getString("LastKnownName");
                int food = data.getInt("foodLevel");
                int invSize = data.getListTag("Inventory").size();
                ListTag enderChestItems = data.getListTag("EnderItems");
                int enderSize = enderChestItems.size();

                player.sendMessage(ChatColor.YELLOW + "Enderchest-Slots belegt: " + enderSize);


                player.sendMessage(ChatColor.GOLD + "Daten für " + lastKnownName + ":");
                player.sendMessage(ChatColor.YELLOW + "Health: " + health);
                player.sendMessage(ChatColor.YELLOW + "XP Level: " + xp);
                player.sendMessage(ChatColor.YELLOW + "Hunger: " + food);
                player.sendMessage(ChatColor.YELLOW + "Inventar-Slots belegt: " + invSize);

            } catch (IOException e) {
                player.sendMessage(ChatColor.RED + "Fehler beim Lesen der Datei.");
                e.printStackTrace();
            }
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
            if (!PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "invulnerable") && !PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "*")) return;
            if (target.isInvulnerable()) {
                target.setInvulnerable(false);
                player.sendMessage(ChatColor.of("#f08d1d") +"Set invulnerability §coff"+ChatColor.of("#f08d1d") +" for "+ target.getName());
            }else {
                target.setInvulnerable(true);
                player.sendMessage(ChatColor.of("#f08d1d") +"Set invulnerability §aon"+ChatColor.of("#f08d1d") +" for "+ target.getName());
            }
        }else if (args[3].equalsIgnoreCase("Fly")) {
            if (!PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "fly") && !PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "*")) return;
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
            if (!PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "collidable") && !PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "*")) return;
            if (target.isCollidable()) {
                target.setCollidable(false);
                player.sendMessage(ChatColor.of("#f08d1d") +"Set collidable §coff"+ChatColor.of("#f08d1d") +" for "+ target.getName());
            }else {
                target.setCollidable(true);
                player.sendMessage(ChatColor.of("#f08d1d") +"Set collidable §aon"+ChatColor.of("#f08d1d") +" for "+ target.getName());
            }
        }else if (args[3].equalsIgnoreCase("SleepIgnore")) {
            if (!PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "sleepignore") && !PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "*")) return;
            if (target.isSleepingIgnored()) {
                target.setSleepingIgnored(false);
                player.sendMessage(ChatColor.of("#f08d1d") +"Set sleep-ignored §coff"+ChatColor.of("#f08d1d") +" for "+ target.getName());
            }else {
                target.setSleepingIgnored(true);
                player.sendMessage(ChatColor.of("#f08d1d") +"Set sleep-ignored §aon"+ChatColor.of("#f08d1d") +" for "+ target.getName());
            }
        }else if (args[3].equalsIgnoreCase("Invisibility")) {
            if (!PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "invisibility") && !PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "*")) return;
            if (target.isInvisible()) {
                target.setInvisible(false);
                player.sendMessage(ChatColor.of("#f08d1d") +"Set invisible §coff"+ChatColor.of("#f08d1d") +" for "+ target.getName());
            }else {
                target.setInvisible(true);
                player.sendMessage(ChatColor.of("#f08d1d") +"Set invisible §aon"+ChatColor.of("#f08d1d") +" for "+ target.getName());
            }
        }else if (args[3].equalsIgnoreCase("VisualFire")) {
            if (!PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "visualfire") && !PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "*")) return;
            if (target.isVisualFire()) {
                target.setVisualFire(false);
                player.sendMessage(ChatColor.of("#f08d1d") +"Set visual-fire §coff"+ChatColor.of("#f08d1d") +" for "+ target.getName());
            }else {
                target.setVisualFire(true);
                player.sendMessage(ChatColor.of("#f08d1d") +"Set visual-fire §aon"+ChatColor.of("#f08d1d") +" for "+ target.getName());
            }
        }else if (args[3].equalsIgnoreCase("Op")) {
            if (!PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "op") && !PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "*")) return;
            if (target.isOp()) {
                target.setOp(false);
                player.sendMessage(ChatColor.of("#f08d1d") +"Set Operator §coff"+ChatColor.of("#f08d1d") +" for "+ target.getName());
            }else {
                target.setOp(true);
                player.sendMessage(ChatColor.of("#f08d1d") +"Set Operator §aon"+ChatColor.of("#f08d1d") +" for "+ target.getName());
            }
        } else if (args[3].equalsIgnoreCase("Address")) {
            if (!PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "address") && !PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "*")) return;
            player.sendMessage(ChatColor.of("#f08d1d") +"Address from §f" +target.getName() + ChatColor.of("#f08d1d") + " is §7" + target.getAddress().toString() + "/" +target.getAddress().getAddress().getHostAddress());
        } else if (args[3].equalsIgnoreCase("ClientName")) {
            if (!PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "clientname") && !PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "*")) return;
            player.sendMessage(ChatColor.of("#f08d1d") +"Client-Brand-Name from §f" +target.getName() + ChatColor.of("#f08d1d") + " is §7" + target.getClientBrandName());
        }else if (args[3].equalsIgnoreCase("Locale")) {
            if (!PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "locale") && !PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "*")) return;
            player.sendMessage(ChatColor.of("#f08d1d") +"Locale from §f" +target.getName() + ChatColor.of("#f08d1d") + " is §7" + target.locale());
        } else if (args[3].equalsIgnoreCase("Reset")) {
            if (!PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "reset") && !PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "*")) return;
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
            if (!PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "list") && !PermissionAPI.hasPermission(player.getUniqueId(), basePerm + "*")) return;
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