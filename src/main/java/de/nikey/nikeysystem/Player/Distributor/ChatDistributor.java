package de.nikey.nikeysystem.Player.Distributor;

import de.nikey.nikeysystem.NikeySystem;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class ChatDistributor {

    public static final Map<UUID, Channel> channels = new HashMap<>();
    public static final Map<UUID, UUID> playerChannels = new HashMap<>();
    public static final File dataFile = new File(NikeySystem.getPlugin().getDataFolder(), "channels.dat");

    public static void manageChat(Player sender, String[] args) {
        String cmd = args[3];
        if (cmd.isEmpty()) return;

        if (cmd.equalsIgnoreCase("Channel")) {
            String subCommand = args[4];
            if (subCommand.equalsIgnoreCase("create")) {
                UUID channelId = UUID.randomUUID();
                Channel newChannel = new Channel(channelId, sender.getUniqueId());
                channels.put(channelId, newChannel);
                playerChannels.put(sender.getUniqueId(), channelId);
                sender.sendMessage(Component.text("Channel created with ID: " + channelId).color(net.kyori.adventure.text.format.TextColor.color(0, 255, 0)));

            } else if (subCommand.equalsIgnoreCase("join")) {
                if (args.length < 6) {
                    sender.sendMessage(Component.text("Usage: channel join <channelId>").color(net.kyori.adventure.text.format.TextColor.color(255, 0, 0)));
                    return;
                }
                try {
                    UUID joinChannelId = UUID.fromString(args[5]);
                    Channel joinChannel = channels.get(joinChannelId);
                    if (joinChannel == null) {
                        sender.sendMessage(Component.text("Channel not found.").color(net.kyori.adventure.text.format.TextColor.color(255, 0, 0)));
                        return;
                    }
                    joinChannel.addMember(sender.getUniqueId());
                    playerChannels.put(sender.getUniqueId(), joinChannelId);
                    sender.sendMessage(Component.text("Joined channel: " + joinChannelId).color(net.kyori.adventure.text.format.TextColor.color(0, 255, 0)));
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(Component.text("Invalid channel ID.").color(net.kyori.adventure.text.format.TextColor.color(255, 0, 0)));
                }

            } else if (subCommand.equalsIgnoreCase("leave")) {
                UUID leaveChannelId = playerChannels.remove(sender.getUniqueId());
                if (leaveChannelId == null) {
                    sender.sendMessage(Component.text("You are not in a channel.").color(net.kyori.adventure.text.format.TextColor.color(255, 0, 0)));
                } else {
                    Channel leaveChannel = channels.get(leaveChannelId);
                    if (leaveChannel != null) {
                        leaveChannel.removeMember(sender.getUniqueId());
                    }
                    sender.sendMessage(Component.text("Left channel: " + leaveChannelId).color(net.kyori.adventure.text.format.TextColor.color(0, 255, 0)));
                }

            } else if (subCommand.equalsIgnoreCase("list")) {
                if (channels.isEmpty()) {
                    sender.sendMessage(Component.text("No channels exist.").color(net.kyori.adventure.text.format.TextColor.color(255, 255, 0)));
                } else {
                    sender.sendMessage(Component.text("Available channels:").color(net.kyori.adventure.text.format.TextColor.color(0, 255, 0)));
                    channels.keySet().forEach(id -> sender.sendMessage(Component.text("- " + id).color(net.kyori.adventure.text.format.TextColor.color(0, 255, 255))));
                }

            } else if (subCommand.equalsIgnoreCase("messages")) {
                UUID currentChannelId = playerChannels.get(sender.getUniqueId());
                if (currentChannelId == null) {
                    sender.sendMessage(Component.text("You are not in a channel.").color(net.kyori.adventure.text.format.TextColor.color(255, 0, 0)));
                } else {
                    Channel currentChannel = channels.get(currentChannelId);
                    if (currentChannel == null) {
                        sender.sendMessage(Component.text("Channel not found.").color(net.kyori.adventure.text.format.TextColor.color(255, 0, 0)));
                    } else {
                        sender.sendMessage(Component.text("Messages in channel:").color(net.kyori.adventure.text.format.TextColor.color(0, 255, 0)));
                        currentChannel.getMessages().forEach(msg ->
                                sender.sendMessage(Component.text("- " + msg).color(net.kyori.adventure.text.format.TextColor.color(255, 255, 255))));
                    }
                }
            }else if (subCommand.equalsIgnoreCase("open") || subCommand.equalsIgnoreCase("close")) {
                if (args.length < 6) {
                    sender.sendMessage("§cPlease specify a channel ID.");
                    return;
                }

                UUID channelId = UUID.fromString(args[1]);
                Channel channel = channels.get(channelId);

                if (channel == null) {
                    sender.sendMessage("§cChannel not found.");
                    return;
                }

                // Nur der Owner kann den Channel öffnen/schließen
                if (!channel.getOwner().equals(sender.getUniqueId())) {
                    sender.sendMessage("§cYou are not the owner of this channel.");
                    return;
                }

                if (subCommand.equalsIgnoreCase("open")) {
                    channel.setClosed(false);
                    sender.sendMessage("§aThe channel is now open. Anyone can join.");
                } else if (subCommand.equalsIgnoreCase("close")) {
                    channel.setClosed(true);
                    sender.sendMessage("§cThe channel is now closed. Only invited players can join.");
                }
            }
        }else if (args[0].equalsIgnoreCase("invite")) {
            if (args.length < 7) {
                sender.sendMessage("§cUsage: channel invite <channelId> <player>");
                return;
            }

            UUID channelId = UUID.fromString(args[5]);
            Player targetPlayer = Bukkit.getPlayer(args[6]);

            if (targetPlayer == null) {
                sender.sendMessage("§cPlayer not found.");
                return;
            }

            Channel channel = channels.get(channelId);

            if (channel == null) {
                sender.sendMessage("§cChannel not found.");
                return;
            }

            if (!channel.getOwner().equals(sender.getUniqueId())) {
                sender.sendMessage("§cYou are not the owner of this channel.");
                return;
            }

            // Spieler einladen (nur möglich, wenn der Channel geschlossen ist)
            if (channel.isClosed()) {
                channel.invitePlayer(targetPlayer.getUniqueId());
                sender.sendMessage("§aPlayer " + targetPlayer.getName() + " has been invited to the channel.");
            } else {
                sender.sendMessage("§cThis channel is open. No need for invitations.");
            }

        } else {
                sender.sendMessage(Component.text("Wrong usage. Usage: channel <create|join|leave|list|messages>").color(net.kyori.adventure.text.format.TextColor.color(255, 0, 0)));
        }
    }
}