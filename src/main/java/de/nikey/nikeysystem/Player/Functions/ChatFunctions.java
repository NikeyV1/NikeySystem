package de.nikey.nikeysystem.Player.Functions;

import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.Channel;
import de.nikey.nikeysystem.Player.API.MuteAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.UUID;

import static de.nikey.nikeysystem.Player.Distributor.ChatDistributor.channels;
import static de.nikey.nikeysystem.Player.Distributor.ChatDistributor.playerChannels;

public class ChatFunctions implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerChatInChannel(AsyncChatEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        UUID currentChannelId = playerChannels.get(playerUUID);
        if (currentChannelId == null) return;

        Channel channel = channels.get(currentChannelId);
        if (channel != null) {
            Component message = event.message().asComponent();
            channel.addMessage(player.getName() + ": " + PlainTextComponentSerializer.plainText().serialize(message));

            for (UUID memberUUID : channel.getMembers()) {
                Player member = Bukkit.getPlayer(memberUUID);
                if (member != null) {
                    member.sendMessage(channel.getPrefix().append(Component.text(player.getName() + ": " ).color(NamedTextColor.WHITE).append(message.color(NamedTextColor.WHITE))));
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

    @EventHandler
    public void onPlayerCommandMute(PlayerCommandPreprocessEvent event) {
        String[] args = event.getMessage().split(" ");
        String command = args[0].toLowerCase();

        if (command.equalsIgnoreCase("/msg") || command.equalsIgnoreCase("/tell") || command.equalsIgnoreCase("/w") ) {
            Player sender = event.getPlayer();

            if (args.length < 3) {
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                return;
            }

            if (MuteAPI.isMuted(sender.getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (NikeySystem.getPlugin().getConfig().getBoolean("system.setting.deactivate_command_with_prefix")) {
            String[] args = event.getMessage().split(" ");
            String command = args[0].toLowerCase();
            if (command.startsWith("/minecraft:") || command.startsWith("/bukkit:") || command.startsWith("/paper:") || command.startsWith("/spigot:")) {
                if (PermissionAPI.isOwner(event.getPlayer().getName()))return;
                event.setCancelled(true);
            }
        }
    }


}
