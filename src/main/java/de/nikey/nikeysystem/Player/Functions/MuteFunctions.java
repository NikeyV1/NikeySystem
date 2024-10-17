package de.nikey.nikeysystem.Player.Functions;

import de.nikey.nikeysystem.Player.API.MuteAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MuteFunctions implements Listener {
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();

        if (MuteAPI.isMuted(name)) {
            if (PermissionAPI.isSystemUser(player)) player.sendMessage("§cYou are muted and cannot chat");
            event.setCancelled(true);
        }
    }
}
