package de.nikey.nikeysystem.Server.Functions;

import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.Server.API.CommandAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.ArrayList;
import java.util.List;

public class CommandFunctions implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        final String cmd = event.getMessage();
        final String[] args = cmd.split(" ");

        if (!PermissionAPI.isSystemUser(event.getPlayer()) && cmd.startsWith("/system")) {
            player.sendMessage("§cUnknown or incomplete command, see below for error\n" +
                    cmd+"<--[HERE]");
            event.setCancelled(true);
        }

        if (!PermissionAPI.isOwner(player.getName()) && cmd.startsWith("/minecraft:")) {
            player.sendMessage("§cUnknown or incomplete command, see below for error\n" +
                    cmd+"<--[HERE]");
            event.setCancelled(true);
        }

        if (CommandAPI.isBlocked(args[0])) {
            if (!PermissionAPI.isOwner(player.getName())) {
                player.sendMessage("§cUnknown or incomplete command, see below for error\n" +
                        cmd+"<--[HERE]");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandSend(PlayerCommandSendEvent event) {

        if (!PermissionAPI.isSystemUser(event.getPlayer())) {
            event.getCommands().remove("system");
        }

        if (!PermissionAPI.isOwner(event.getPlayer().getName())) {
            for (String cmd :CommandAPI.getDisabledCommands()) {
                event.getCommands().remove(cmd);
            }
        }
    }


    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        String buffer = event.getBuffer();
        String[] split = buffer.split(" ");
        if (CommandAPI.isBlocked(split[0])) {
            List<String > comp = new ArrayList<>();
            if (!PermissionAPI.isOwner(event.getSender().getName())) {
                event.setCompletions(comp);
            }
        }
    }
}
