package de.nikey.nikeysystem.Util;

import de.nikey.nikeysystem.API.PermissionAPI;
import de.nikey.nikeysystem.Distributor.HideDistributor;
import de.nikey.nikeysystem.Distributor.PermissionDistributor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

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
                    }else if (PermissionAPI.isOwner(player.getName()) || PermissionAPI.isAdmin(player.getName()) && args[2].equalsIgnoreCase("permissions")) {
                        PermissionDistributor.permissionDistributor(player,args);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
