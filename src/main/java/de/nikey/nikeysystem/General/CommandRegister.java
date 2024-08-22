package de.nikey.nikeysystem.General;

import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.Player.Distributor.HideDistributor;
import de.nikey.nikeysystem.Player.Distributor.PermissionDistributor;
import de.nikey.nikeysystem.Player.Distributor.StatsDistributor;
import de.nikey.nikeysystem.Security.Distributor.SystemShieldDistributor;
import de.nikey.nikeysystem.Server.Distributor.CommandDistributor;
import de.nikey.nikeysystem.Server.Distributor.SettingsDistributor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class CommandRegister implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        final String cmd = event.getMessage();
        final String[] args = cmd.split(" ");
        if (args[0].equalsIgnoreCase("/system")) {
            if (PermissionAPI.isSystemUser(player)) {
                if (args[1].equalsIgnoreCase("player")) {
                    if (args[2].equalsIgnoreCase("hide")) {
                        HideDistributor.hideDistributor(player,args);
                        event.setCancelled(true);
                    }else if (args[2].equalsIgnoreCase("permissions")) {
                        if (PermissionAPI.isOwner(player.getName()) || PermissionAPI.isAdmin(player.getName())) {
                            PermissionDistributor.permissionDistributor(player,args);
                            event.setCancelled(true);
                        }
                    }else if (args[2].equalsIgnoreCase("stats")) {
                        StatsDistributor.statsDistributor(player,args);
                        event.setCancelled(true);
                    }else if (args[2].equalsIgnoreCase("help")) {
                        player.sendMessage("§7The path 'System/Player' has following sub-paths: §fhide, permissions, stats ");
                        event.setCancelled(true);
                    }
                }else if (args[1].equalsIgnoreCase("server")) {
                    if (args[2].equalsIgnoreCase("command")) {
                        CommandDistributor.commandDistributor(player,args);
                        event.setCancelled(true);
                    }else if (args[2].equalsIgnoreCase("settings")) {
                        SettingsDistributor.settingsDistributor(player,args);
                        event.setCancelled(true);
                    }
                } else if (args[1].equalsIgnoreCase("Security")) {
                    if (args[2].equalsIgnoreCase("System-Shield")) {
                        if (PermissionAPI.isOwner(player.getName()) || PermissionAPI.isAdmin(player.getName())) {
                            SystemShieldDistributor.systemShieldDistributor(player,args);
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onServerCommand(ServerCommandEvent event) {
        final String[] args = event.getCommand().split(" ");
        if (args[0].equalsIgnoreCase("systemdisable")) {
            NikeySystem.getPlugin().getPluginLoader().disablePlugin(NikeySystem.getPlugin());
            event.setCancelled(true);
        }
    }
}
