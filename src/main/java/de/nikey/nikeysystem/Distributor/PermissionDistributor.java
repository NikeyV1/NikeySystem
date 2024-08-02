package de.nikey.nikeysystem.Distributor;

import de.nikey.nikeysystem.API.PermissionAPI;
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
        String target = args[4];
        if (args[3].equalsIgnoreCase("ToggleAdmin")) {
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
            }
        } else if (args[3].equalsIgnoreCase("ToggleModerator")) {
            if (!PermissionAPI.isModerator(target)) {
                PermissionAPI.addModerator(target);
                saveModerators();
                sender.sendMessage("§bAdded "+target+"'s §0moderator §bpermissions!");
            }else {
                PermissionAPI.removeModerator(target);
                saveModerators();
                sender.sendMessage("§bRemoved "+target+"'s §0moderator §bpermissions!");
            }
        } else if (args[3].equalsIgnoreCase("List")) {
            if (PermissionAPI.isAdmin(target) || PermissionAPI.isModerator(target) || PermissionAPI.isOwner(target)) {
                sender.sendMessage("§8"+target+" has following permissions: " +
                        (PermissionAPI.isAdmin(target) ? "§cAdmin":"") + (PermissionAPI.isModerator(target) ? "§0Moderator" : "") + (PermissionAPI.isOwner(target) ? "§cOwner" : ""));
            }else {
                sender.sendMessage("§8"+target+" has normal member permissions");
            }
            Player player = Bukkit.getPlayer(target);
            if (player == null) {
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
        }
    }
}
