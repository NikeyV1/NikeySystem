package de.nikey.nikeysystem.Player.Functions;

import de.nikey.nikeysystem.Player.API.Channel;
import de.nikey.nikeysystem.Player.API.MuteAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;

import static de.nikey.nikeysystem.Player.Distributor.ChatDistributor.channels;
import static de.nikey.nikeysystem.Player.Distributor.ChatDistributor.playerChannels;

public class ChatFunctions implements Listener {
    @EventHandler
    public void onPlayerChatInChannel(AsyncChatEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        UUID currentChannelId = playerChannels.get(playerUUID);
        if (currentChannelId == null) return;

        Channel channel = channels.get(currentChannelId);
        if (channel != null) {
            String message = event.message().toString();
            channel.addMessage(player.getName() + ": " + message);

            for (UUID memberUUID : channel.getMembers()) {
                Player member = Bukkit.getPlayer(memberUUID);
                if (member != null) {
                    member.sendMessage(Component.text(player.getName() + ": " + message).color(NamedTextColor.WHITE));
                }
            }

            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true,priority = EventPriority.HIGH)
    public void onAsyncChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        if (MuteAPI.isMuted(player.getName())) {
            if (PermissionAPI.isSystemUser(player)) player.sendMessage("§cYou are muted and cannot chat");
            event.setCancelled(true);
        }
    }

}
