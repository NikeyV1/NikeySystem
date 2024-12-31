package de.nikey.nikeysystem.Player.Distributor;

import de.nikey.nikeysystem.NikeySystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

public class ChatDistributor {

    public static final Map<UUID, Channel> channels = new HashMap<>();
    public static final Map<UUID, UUID> playerChannels = new HashMap<>();
    public static final File dataFile = new File(NikeySystem.getPlugin().getDataFolder(), "channels.dat");
    private static final TextColor channelsColor = TextColor.color(38, 182, 120);

    public static void manageChat(Player sender, String[] args) {
        String cmd = args[3];
        if (cmd.isEmpty()) return;

        if (cmd.equalsIgnoreCase("Channel")) {
            String subCommand = args[4];
            if (subCommand.equalsIgnoreCase("create")) {

                if (args.length != 6) {
                    sender.sendMessage(Component.text("Usage: channel create <channelName>").color(TextColor.color(255, 0, 0)));
                    return;
                }

                UUID channelId = UUID.randomUUID();
                Channel newChannel = new Channel(channelId, sender.getUniqueId(), args[5]);
                channels.put(channelId, newChannel);
                playerChannels.put(sender.getUniqueId(), channelId);
                sender.sendMessage(Component.text("Channel ").color(TextColor.color(channelsColor))
                        .append(Component.text("'"+args[5]+"'").color(NamedTextColor.GRAY))
                        .append(Component.text(" created with ID: ").color(channelsColor))
                        .append(Component.text(channelId.toString()).color(NamedTextColor.WHITE)));

            } else if (subCommand.equalsIgnoreCase("join")) {
                if (args.length < 6) {
                    sender.sendMessage(Component.text("Usage: channel join <channelId>").color(NamedTextColor.RED));
                    return;
                }
                try {
                    UUID joinChannelId = UUID.fromString(args[5]);
                    Channel joinChannel = channels.get(joinChannelId);
                    if (joinChannel == null) {
                        sender.sendMessage(Component.text("Channel not found").color(NamedTextColor.RED));
                        return;
                    }

                    if (playerChannels.containsKey(sender.getUniqueId())) {
                        sender.sendMessage(Component.text("You are already in a channel! Leave it first to join another one").color(TextColor.color(38,182,120)));
                        return;
                    }

                    if (joinChannel.isClosed()) {
                        sender.sendMessage(Component.text("This Channel is").color(TextColor.color(38, 182, 120))
                                .append(Component.text(" closed").color(NamedTextColor.RED))
                                .append(Component.text("! You need to be invited to join").color(TextColor.color(38,182,120))));
                        return;
                    }

                    joinChannel.addMember(sender.getUniqueId());
                    playerChannels.put(sender.getUniqueId(), joinChannelId);
                    sender.sendMessage(Component.text("Joined channel: " + joinChannel.getName()).color(channelsColor));
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(Component.text("Invalid channel ID.").color(TextColor.color(255, 0, 0)));
                }

            } else if (subCommand.equalsIgnoreCase("leave")) {
                if (!playerChannels.containsKey(sender.getUniqueId())) {
                    sender.sendMessage(Component.text("You are not in a channel").color(channelsColor));
                    return;
                }
                UUID leaveChannelId = playerChannels.remove(sender.getUniqueId());

                Channel leaveChannel = channels.get(leaveChannelId);
                if (leaveChannel != null) {
                    leaveChannel.removeMember(sender.getUniqueId());
                    sender.sendMessage(Component.text("Left ").color(NamedTextColor.YELLOW)
                            .append(Component.text("channel: ").color(channelsColor))
                            .append(Component.text(leaveChannel.getName()).color(NamedTextColor.WHITE)));
                }

            } else if (subCommand.equalsIgnoreCase("list")) {
                if (channels.isEmpty()) {
                    sender.sendMessage(Component.text("No channels exist").color(channelsColor));
                } else {
                    sender.sendMessage(Component.text("Available channels:").color(channelsColor));
                    for (Channel channel : channels.values()) {
                        sender.sendMessage(Component.text("  - " + channel.getName()).color(TextColor.color(82, 221, 161))
                                .append(Component.text(" ("+channel.getId()+")").color(NamedTextColor.GRAY)));
                    }
                }

            } else if (subCommand.equalsIgnoreCase("messages")) {
                UUID currentChannelId = playerChannels.get(sender.getUniqueId());
                if (currentChannelId == null) {
                    sender.sendMessage(Component.text("You are not in a channel.").color(NamedTextColor.RED));
                } else {
                    Channel currentChannel = channels.get(currentChannelId);
                    if (currentChannel == null) {
                        sender.sendMessage(Component.text("Channel not found or doesn't exist anymore").color(NamedTextColor.RED));
                    } else {
                        sender.sendMessage(Component.text("Messages in channel:").color(channelsColor));
                        List<String> messages = currentChannel.getMessages();
                        int messageCount = messages.size();
                        List<String> lastMessages = messages.subList(Math.max(0, messageCount - 20), messageCount);

                        lastMessages.forEach(msg ->
                                sender.sendMessage(Component.text("- " + msg).color(NamedTextColor.WHITE))
                        );
                    }
                }
            }else if (subCommand.equalsIgnoreCase("open") || subCommand.equalsIgnoreCase("close")) {
                if (args.length < 6) {
                    sender.sendMessage(Component.text("Usage: channel open/close <channelId>").color(NamedTextColor.RED));
                    return;
                }

                UUID channelId = UUID.fromString(args[5]);
                Channel channel = channels.get(channelId);

                if (channel == null) {
                    sender.sendMessage(Component.text("Channel not found").color(NamedTextColor.RED));
                    return;
                }

                if (channel.getOwner() != sender.getUniqueId()) {
                    sender.sendMessage(Component.text("You are not the owner of this channel").color(NamedTextColor.RED));
                    return;
                }

                if (subCommand.equalsIgnoreCase("open")) {
                    channel.setClosed(false);
                    sender.sendMessage(Component.text("The channel is now ").color(channelsColor)
                            .append(Component.text("open").color(NamedTextColor.GREEN))
                            .append(Component.text(". Anyone can join").color(channelsColor)));
                } else if (subCommand.equalsIgnoreCase("close")) {
                    channel.setClosed(true);
                    sender.sendMessage(Component.text("The channel is now ").color(channelsColor)
                            .append(Component.text("closed").color(NamedTextColor.RED))
                            .append(Component.text(". Only invited players can join").color(channelsColor)));
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
                sender.sendMessage(Component.text("Channel not found").color(NamedTextColor.RED));
                return;
            }

            if (channel.getOwner() != sender.getUniqueId()) {
                sender.sendMessage(Component.text("You are not the owner of this channel").color(NamedTextColor.RED));
                return;
            }

            if (channel.isClosed()) {
                channel.invitePlayer(targetPlayer.getUniqueId());
                sender.sendMessage(Component.text("You invited ").color(channelsColor)
                        .append(Component.text(targetPlayer.getName()).color(NamedTextColor.GRAY))
                        .append(Component.text(" to "))
                        .append(Component.text(channel.getName()).color(NamedTextColor.WHITE)));
            } else {
                sender.sendMessage("§cThis channel is open. No need for invitations.");
            }

        }
    }
}