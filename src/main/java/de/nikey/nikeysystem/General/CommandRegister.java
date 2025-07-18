package de.nikey.nikeysystem.General;

import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.Player.Distributor.*;
import de.nikey.nikeysystem.Security.Distributor.SystemShieldDistributor;
import de.nikey.nikeysystem.Server.Distributor.*;
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
            if (PermissionAPI.isSystemUser(player.getUniqueId())) {
                if (args[1].equalsIgnoreCase("player")) {
                    if (args[2].equalsIgnoreCase("hide")) {
                        HideDistributor.hideDistributor(player,args);
                        event.setCancelled(true);
                    }else if (args[2].equalsIgnoreCase("permissions")) {
                        PermissionDistributor.permissionDistributor(player,args);
                        event.setCancelled(true);
                    }else if (args[2].equalsIgnoreCase("stats")) {
                        StatsDistributor.statsDistributor(player,args);
                        event.setCancelled(true);
                    }else if (args[2].equalsIgnoreCase("inventory")) {
                        InventoryDistributor.inventoryDistributor(player,args);
                        event.setCancelled(true);
                    }else if (args[2].equalsIgnoreCase("effect")) {
                        EffectDistributor.effectDistributor(player,args);
                        event.setCancelled(true);
                    }else if (args[2].equalsIgnoreCase("location")) {
                        LocationDistributer.locationManager(player,args);
                        event.setCancelled(true);
                    }else if (args[2].equalsIgnoreCase("profile")) {
                        ProfileDistributor.manageProfile(player,args);
                        event.setCancelled(true);
                    }else if (args[2].equalsIgnoreCase("sound")) {
                        SoundDistributor.manageSound(player,args);
                        event.setCancelled(true);
                    }else if (args[2].equalsIgnoreCase("ResourcePack")) {
                        ResourcePackDistributor.ResourcePackManager(player,args);
                        event.setCancelled(true);
                    } else if (args[2].equalsIgnoreCase("chat")) {
                        ChatDistributor.manageChat(player,args);
                        event.setCancelled(true);
                    } else if (args[2].equalsIgnoreCase("moderation")) {
                        ModerationDistributor.manageModeration(player,args);
                        event.setCancelled(true);
                    }
                }else if (args[1].equalsIgnoreCase("server")) {
                    if (args[2].equalsIgnoreCase("command")) {
                        CommandDistributor.commandDistributor(player,args);
                        event.setCancelled(true);
                    }else if (args[2].equalsIgnoreCase("settings")) {
                        if (PermissionAPI.isManagement(player.getUniqueId())) {
                            SettingsDistributor.settingsDistributor(player,args);
                            event.setCancelled(true);
                        }
                    }else if (args[2].equalsIgnoreCase("Performance")) {
                        PerformanceDistributor.performanceManager(player,args);
                        event.setCancelled(true);
                    }else if (args[2].equalsIgnoreCase("World")) {
                        WorldDistributor.worldManager(player,args);
                        event.setCancelled(true);
                    }else if (args[2].equalsIgnoreCase("Backup")) {
                        BackupDistributor.manageBackup(player,args);
                        event.setCancelled(true);
                    }else if (args[2].equalsIgnoreCase("Logging")) {
                        LoggingDistributor.loggingManager(player,args);
                        event.setCancelled(true);
                    }else if (args[2].equalsIgnoreCase("Discord")) {

                    }
                } else if (args[1].equalsIgnoreCase("Security")) {
                    if (args[2].equalsIgnoreCase("System-Shield")) {
                        if (PermissionAPI.isManagement(player.getUniqueId())) {
                            SystemShieldDistributor.systemShieldDistributor(player,args);
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
}