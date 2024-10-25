package de.nikey.nikeysystem.Player.Distributor;

import de.nikey.nikeysystem.Player.API.HideAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.NikeySystem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PermissionDistributor {
    public static void loadAdmins() {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();
        PermissionAPI.clearAdmins();
        PermissionAPI.getAdminsList().addAll(config.getStringList("permissions.admins"));
    }

    public static void saveAdmins() {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();
        config.set("permissions.admins", new ArrayList<>(PermissionAPI.getAdminsList()));
        NikeySystem.getPlugin().saveConfig();
    }

    public static void loadModerators() {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();
        PermissionAPI.clearModerator();
        PermissionAPI.getModeratorList().addAll(config.getStringList("permissions.moderators"));
    }

    public static void saveModerators() {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();
        config.set("permissions.moderators", new ArrayList<>(PermissionAPI.getModeratorList()));
        NikeySystem.getPlugin().saveConfig();
    }

    public static void permissionDistributor(Player sender,String[] args) {
        if (args[3].equalsIgnoreCase("ToggleAdmin")) {
            String target = args[4];
            if (PermissionAPI.isOwner(sender.getName())) {
                if (!PermissionAPI.isAdmin(target)) {
                    PermissionAPI.addAdmin(target);
                    saveAdmins();
                    sender.sendMessage("§bAdded "+target+"'s §cadmin §bpermissions!");
                }else {
                    PermissionAPI.removeAdmin(target);
                    saveAdmins();
                    sender.sendMessage("§bRemoved "+target+"'s §cadmin §bpermissions!");
                }
            }else {
                sender.sendMessage("§cError: required permission missing!");
            }
        } else if (args[3].equalsIgnoreCase("ToggleModerator")) {
            String target = args[4];
            if (!PermissionAPI.isModerator(target)) {
                PermissionAPI.addModerator(target);
                saveModerators();
                sender.sendMessage("§bAdded "+target+"'s §0moderator §bpermissions!");
            }else {
                PermissionAPI.removeModerator(target);
                saveModerators();
                sender.sendMessage("§bRemoved "+target+"'s §0moderator §bpermissions!");
            }
        }else if (args[3].equalsIgnoreCase("TogglePermission")) {
            String target = args[4];
            String permission = args[5];
            // Zielspieler holen
            Player player = Bukkit.getPlayer(target);
            if (player == null || !HideAPI.canSee(sender,player)) {
                sender.sendMessage("§cError: Player not found");
                return;
            }

            

            // Prüfen, ob der Spieler die Berechtigung bereits hat
            if (!player.hasPermission(permission)) {
                PermissionAttachment attachment = player.addAttachment(MyPlugin.getInstance()); // Plugin-Instanz holen
                attachment.setPermission(permission, true);  // Berechtigung hinzufügen
                sender.sendMessage("§bAdded permission §6" + permission + " §bto " + target + "!");
            } else {
                // Berechtigung entfernen
                PermissionAttachment attachment = player.addAttachment(MyPlugin.getInstance());
                attachment.setPermission(permission, false);
                sender.sendMessage("§bRemoved permission §6" + permission + " §bfrom " + target + "!");
            }
            player.recalculatePermissions();  // Berechtigungen neu berechnen

    } else if (args[3].equalsIgnoreCase("List")) {
            String target = args[4];
            if (PermissionAPI.isAdmin(target) || PermissionAPI.isModerator(target) || PermissionAPI.isOwner(target)) {
                sender.sendMessage("§8"+target+" has following permissions: " +
                        (PermissionAPI.isAdmin(target) ? "§cAdmin":"") + (PermissionAPI.isModerator(target) ? "§0Moderator" : "") + (PermissionAPI.isOwner(target) ? "§cOwner" : ""));
            }else {
                sender.sendMessage("§8"+target+" has normal member permissions");
            }
        }else if (args[3].equalsIgnoreCase("ListAll")) {
            String target = args[4];
            if (PermissionAPI.isAdmin(target) || PermissionAPI.isModerator(target) || PermissionAPI.isOwner(target)) {
                sender.sendMessage("§8"+target+" has following permissions: " +
                        (PermissionAPI.isAdmin(target) ? "§cAdmin":"") + (PermissionAPI.isModerator(target) ? "§0Moderator" : "") + (PermissionAPI.isOwner(target) ? "§cOwner" : ""));
            }else {
                sender.sendMessage("§8"+target+" has normal member permissions");
            }
            Player player = Bukkit.getPlayer(target);
            if (player == null || !HideAPI.canSee(sender,player)) {
                sender.sendMessage("§cError: player is null");
                return;
            }
            player.recalculatePermissions();
            Iterator<PermissionAttachmentInfo> iterator = player.getEffectivePermissions().iterator();
            List<String> perm = new ArrayList<>();
            while (iterator.hasNext()){
                perm.add(iterator.next().getPermission());
            }
            sender.sendMessage("§8"+target +" has following default permissions " +(player.isOp() ? "§cOperator + ":"") +"§7" +perm);
        }else if (args[3].equalsIgnoreCase("help")) {
            if (PermissionAPI.isOwner(sender.getName())) {
                sender.sendMessage("§7The path 'System/Player/Permissions' has following sub-paths: §fToggleAdmin <PlayerName>, ToggleModerator <PlayerName>, List <PlayerName>, ListAll <PlayerName>.");
            }else {
                sender.sendMessage("§7The path 'System/Player/Permissions' has following sub-paths: §fToggleModerator <PlayerName>, List <PlayerName>, ListAll <PlayerName>.");
            }
        }
    }
}
