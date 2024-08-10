package de.nikey.nikeysystem.Server.Functions;

import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.Server.API.CommandAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandFunctions implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        final String cmd = event.getMessage();
        final String[] args = cmd.split(" ");

        //for (String command : CommandAPI.getDisabledCommands()) {
        //            if (cmd.startsWith(command)) {
        //                if (!PermissionAPI.isOwner(player.getName())) {
        //                    event.setCancelled(true);
        //                }
        //            }
        //        }

        if (CommandAPI.isBlocked(args[0])) {
            if (!PermissionAPI.isOwner(player.getName())) {
                event.setCancelled(true);
            }
        }
    }
}
