package de.nikey.nikeysystem.Player.Functions;

import de.nikey.nikeysystem.Player.Distributor.Channel;
import de.nikey.nikeysystem.Player.Distributor.ChatDistributor;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

import static de.nikey.nikeysystem.Player.Distributor.ChatDistributor.channels;
import static de.nikey.nikeysystem.Player.Distributor.ChatDistributor.playerChannels;

public class ChatFunctions implements Listener {
    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        UUID currentChannelId = playerChannels.get(playerUUID);
        if (currentChannelId == null) {
            return;
        }

        Channel channel = channels.get(currentChannelId);
        if (channel != null) {
            String message = event.message().toString();
            channel.addMessage(player.getName() + ": " + message);

            for (UUID memberUUID : channel.getMembers()) {
                Player member = Bukkit.getPlayer(memberUUID);
                if (member != null) {
                    member.sendMessage(Component.text(player.getName() + ": " + message).color(TextColor.color(255, 255, 255)));
                }
            }

            event.setCancelled(true);
        }
    }
}
