package de.nikey.nikeysystem.Player.Distributor;

import de.nikey.nikeysystem.General.ShieldCause;
import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.*;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChatDistributor {
    public static final Map<UUID, Channel> channels = new HashMap<>();
    public static final Map<UUID, UUID> playerChannels = new HashMap<>();
    private static final TextColor channelsColor = TextColor.color(38, 182, 120);
    private static final TextColor muteColor = TextColor.color(29, 192, 240);

    public static void manageChat(Player sender, String[] args) {
        String cmd = args[3];
        if (cmd.isEmpty()) return;
        String basePerm = "system.player.chat.";

        if (cmd.equalsIgnoreCase("Channel")) {
            if (!PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "channel") && !PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "*")) return;
            String subCommand = args[4];
            if (subCommand.equalsIgnoreCase("create")) {

                if (args.length != 6) {
                    sender.sendMessage(Component.text("Usage: channel create <channelName>").color(TextColor.color(255, 0, 0)));
                    return;
                }

                UUID channelId = UUID.randomUUID();
                String name = args[5];
                if (name.toLowerCase().contains("system") || name.toLowerCase().contains("management")) {
                    sender.sendMessage(Component.text("You aren't allowed to name your channel after system channels").color(NamedTextColor.RED));
                    return;
                }

                UUID playerId = sender.getUniqueId();
                if (playerChannels.containsKey(playerId)) {
                    UUID currentChannelId = playerChannels.get(playerId);
                    Channel currentChannel = channels.get(currentChannelId);
                    if (currentChannel != null) {
                        currentChannel.removeMember(playerId);
                        currentChannel.sendMessage(Component.text(sender.getName() + " left the channel"));
                    }
                    playerChannels.remove(playerId);
                    sender.sendMessage(Component.text("You left your previous channel.").color(NamedTextColor.YELLOW));
                }

                Channel newChannel = new Channel(channelId, args[5], playerId,false,null,null);
                newChannel.addMember(playerId);
                channels.put(channelId, newChannel);


                playerChannels.put(playerId, channelId);
                sender.sendMessage(Component.text("Channel ").color(TextColor.color(channelsColor))
                        .append(Component.text("'"+args[5]+"'").color(NamedTextColor.GRAY))
                        .append(Component.text(" created with ID: ").color(channelsColor))
                        .append(Component.text(channelId.toString()).color(NamedTextColor.WHITE)));
                newChannel.sendMessage(Component.text(sender.getName() + " joined the channel"));

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
                    joinChannel.sendMessage(Component.text(sender.getName() + " joined the channel"));
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
                    leaveChannel.sendMessage(Component.text(sender.getName() + " left the channel"));
                    sender.sendMessage(Component.text("Left ").color(NamedTextColor.RED)
                            .append(Component.text("channel: ").color(channelsColor))
                            .append(Component.text(leaveChannel.getName()).color(NamedTextColor.WHITE)));
                }

            } else if (subCommand.equalsIgnoreCase("list")) {
                if (channels.isEmpty()) {
                    sender.sendMessage(Component.text("No channels exist").color(channelsColor));
                } else {
                    sender.sendMessage(Component.text("Available channels:").color(channelsColor));
                    for (Channel channel : channels.values()) {
                        if (!channel.isClosed()) {
                            sender.sendMessage(Component.text("  - " + channel.getName()).color(TextColor.color(82, 221, 161))
                                    .append(Component.text(" ("+channel.getId()+")").color(NamedTextColor.GRAY)));
                        }else {
                            sender.sendMessage(Component.text("  - " ).color(TextColor.color(82, 221, 161))
                                    .append(Component.text("Closed ").color(NamedTextColor.DARK_GRAY))
                                    .append(Component.text(channel.getName()).color(TextColor.color(82, 221, 161)))
                                    .append(Component.text(" ("+channel.getId()+")").color(NamedTextColor.GRAY)));
                        }
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

                if (!channel.getOwner().equals(sender.getUniqueId())) {
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
            }else if (subCommand.equalsIgnoreCase("invite")) {
                if (args.length < 7) {
                    sender.sendMessage("§cUsage: channel invite <channelId> <player>");
                    return;
                }

                UUID channelId = UUID.fromString(args[5]);
                Player targetPlayer = Bukkit.getPlayer(args[6]);

                if (targetPlayer == null || !HideAPI.canSee(sender,targetPlayer)) {
                    sender.sendMessage("§cPlayer not found");
                    return;
                }

                Channel channel = channels.get(channelId);

                if (channel == null) {
                    sender.sendMessage(Component.text("Channel not found").color(NamedTextColor.RED));
                    return;
                }

                if (!channel.getOwner().equals(sender.getUniqueId())) {
                    sender.sendMessage(Component.text("You are not the owner of this channel").color(NamedTextColor.RED));
                    return;
                }

                if (channel.isClosed()) {
                    channel.invitePlayer(targetPlayer.getUniqueId());

                    UUID uuid = targetPlayer.getUniqueId();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            channel.getInvitedPlayers().remove(uuid);
                        }
                    }.runTaskLater(NikeySystem.getPlugin(),20*60);

                    targetPlayer.sendMessage(
                            Component.text("You have been invited to join the channel: ").color(channelsColor)
                                    .append(Component.text(channel.getName()).color(NamedTextColor.WHITE))
                                    .append(Component.text(" (" + channel.getId() + ")").color(NamedTextColor.GRAY))
                                    .append(Component.text(". "))
                                    .append(Component.newline())
                                    .append(Component.text("[Click here to join]")
                                            .color(NamedTextColor.GREEN)
                                            .decorate(TextDecoration.BOLD)
                                            .clickEvent(ClickEvent.runCommand("/system player chat channel accept " + channelId))
                                            .hoverEvent(HoverEvent.showText(Component.text("Click to join the channel!").color(NamedTextColor.YELLOW))))
                    );

                    sender.sendMessage(Component.text("You invited ").color(channelsColor)
                            .append(Component.text(targetPlayer.getName()).color(NamedTextColor.GRAY))
                            .append(Component.text(" to "))
                            .append(Component.text(channel.getName()).color(NamedTextColor.WHITE)));
                } else {
                    sender.sendMessage("§cThis channel is open. No need for invitations");
                }
            } else if (subCommand.equalsIgnoreCase("accept")) {
                if (args.length != 6) {
                    sender.sendMessage(Component.text("Usage: channel accept <channelId>").color(NamedTextColor.RED));
                    return;
                }

                try {
                    UUID channelId = UUID.fromString(args[5]);

                    Channel channel = channels.get(channelId);
                    if (channel == null) {
                        sender.sendMessage(Component.text("Channel not found").color(NamedTextColor.RED));
                        return;
                    }

                    if (!channel.getInvitedPlayers().contains(sender.getUniqueId())) {
                        sender.sendMessage(Component.text("You don't have an invitation to this channel").color(NamedTextColor.RED));
                        return;
                    }

                    channel.addMember(sender.getUniqueId());
                    playerChannels.put(sender.getUniqueId(), channelId);

                    channel.getInvitedPlayers().remove(sender.getUniqueId());

                    channel.sendMessage(Component.text(sender.getName() + " joined the channel"));

                    sender.sendMessage(Component.text("You joined the channel: ").color(channelsColor)
                            .append(Component.text(channel.getName()).color(NamedTextColor.WHITE)));

                } catch (IllegalArgumentException e) {
                    sender.sendMessage(Component.text("Invalid channel ID.").color(NamedTextColor.RED));
                }
            } else if (subCommand.equalsIgnoreCase("kick")) {
                if (args.length < 6) {
                    sender.sendMessage(Component.text("Usage: channel kick <player>").color(NamedTextColor.RED));
                    return;
                }

                UUID targetPlayerId = Bukkit.getPlayerUniqueId(args[5]);

                UUID channelId = playerChannels.get(sender.getUniqueId());
                if (channelId == null) {
                    sender.sendMessage(Component.text("You are not in a channel").color(NamedTextColor.RED));
                    return;
                }

                Channel channel = channels.get(channelId);
                if (channel == null) {
                    sender.sendMessage(Component.text("Channel not found").color(NamedTextColor.RED));
                    return;
                }

                if (!channel.getOwner().equals(sender.getUniqueId())) {
                    sender.sendMessage(Component.text("Only the channel owner can kick players").color(NamedTextColor.RED));
                    return;
                }

                if (targetPlayerId == null) {
                    sender.sendMessage(Component.text("A player with this id doesn't exist").color(NamedTextColor.RED));
                    return;
                }

                if (!channel.getMembers().contains(targetPlayerId)) {
                    sender.sendMessage(Component.text("The player is not in your channel").color(NamedTextColor.RED));
                    return;
                }

                channel.removeMember(targetPlayerId);
                channel.sendMessage(Component.text(sender.getName() + " left the channel"));
                playerChannels.remove(targetPlayerId);

                sender.sendMessage(Component.text("You").color(channelsColor)
                        .append(Component.text(" kicked ").color(NamedTextColor.RED))
                        .append(Component.text(args[5]).color(NamedTextColor.GRAY))
                        .append(Component.text(" from the channel").color(channelsColor)));

                Player target = Bukkit.getPlayer(targetPlayerId);
                if (target == null)return;
                target.sendMessage(Component.text("You have been kicked from the channel: ").color(NamedTextColor.RED)
                        .append(Component.text(channel.getName()).color(NamedTextColor.GRAY)));
            } else if (subCommand.equalsIgnoreCase("delete")) {
                if (!playerChannels.containsKey(sender.getUniqueId())) {
                    sender.sendMessage(Component.text("You are not in a channel").color(NamedTextColor.RED));
                    return;
                }

                UUID channelId = playerChannels.get(sender.getUniqueId());
                Channel channel = channels.get(channelId);

                if (channel == null) {
                    sender.sendMessage(Component.text("Channel not found or doesn't exist anymore").color(NamedTextColor.RED));
                    return;
                }

                if (!channel.getOwner().equals(sender.getUniqueId())) {
                    sender.sendMessage(Component.text("You are not the owner of this channel").color(NamedTextColor.RED));
                    return;
                }

                for (UUID member : channel.getMembers()) {
                    playerChannels.remove(member);
                }
                channels.remove(channelId);

                sender.sendMessage(Component.text("Channel ").color(channelsColor)
                        .append(Component.text(channel.getName()).color(NamedTextColor.WHITE))
                        .append(Component.text(" has been ").color(channelsColor))
                        .append(Component.text("deleted").color(NamedTextColor.RED)));
            }
        } else if (cmd.equalsIgnoreCase("mute")) {
            if (!PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "mute") && !PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "*")) return;
            String subCommand = args[4];
            if (subCommand.equalsIgnoreCase("mute")) {
                if (args.length == 6) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[5]);

                    if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName(), ShieldCause.MUTE)) {
                        sender.sendMessage("§cError: missing permission");
                        return;
                    }
                    mutePlayer(player.getUniqueId(), sender, 0);
                } else if (args.length == 7) {
                    Player player = Bukkit.getPlayer(args[5]);
                    if (player == null || !HideAPI.canSee(sender, player)) {
                        sender.sendMessage("§cError: wrong usage");
                        return;
                    }
                    if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName(),ShieldCause.MUTE)) {
                        sender.sendMessage("§cError: missing permission");
                        return;
                    }
                    int duration = MuteAPI.parseTime(args[6]);
                    mutePlayer(player, sender, duration);
                }
            } else if (subCommand.equalsIgnoreCase("unmute")) {
                if (args.length == 6) {
                    Player player = Bukkit.getPlayer(args[5]);
                    if (player == null || !HideAPI.canSee(sender, player)) {
                        sender.sendMessage("§cError: wrong usage");
                        return;
                    }
                    if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName(),ShieldCause.UNMUTE)) {
                        sender.sendMessage("§cError: missing permission");
                        return;
                    }
                    unmutePlayer(player, sender);
                }
            }else if (subCommand.equalsIgnoreCase("get")) {
                if (args.length == 6) {
                    Player player = Bukkit.getPlayer(args[5]);
                    if (player == null || !HideAPI.canSee(sender, player)) {
                        sender.sendMessage("§cError: player not found");
                        return;
                    }
                    if (MuteAPI.isMuted(player.getUniqueId())) {
                        if (MuteAPI.getMutedDuration(player.getUniqueId()) == 0) {
                            sender.sendMessage(Component.text(player.getName()).color(NamedTextColor.WHITE)
                                    .append(Component.text(" is currently ").color(muteColor))
                                    .append(Component.text("muted ").color(NamedTextColor.RED))
                                    .append(Component.text("permanently").color(NamedTextColor.DARK_GRAY)));
                        } else {
                            String time = MuteAPI.formatSekTime((int) MuteAPI.getMutedDuration(player.getUniqueId()));
                            sender.sendMessage(Component.text(player.getName()).color(NamedTextColor.WHITE)
                                    .append(Component.text(" is currently ").color(muteColor))
                                    .append(Component.text("muted ").color(NamedTextColor.RED))
                                    .append(Component.text("for: ").color(muteColor))
                                    .append(Component.text(time).color(NamedTextColor.DARK_GRAY)));
                        }
                    } else {
                        sender.sendMessage(Component.text(player.getName()).color(NamedTextColor.WHITE)
                                .append(Component.text(" is").color(muteColor))
                                .append(Component.text(" not muted").color(NamedTextColor.GREEN)));
                    }

                }else if (args.length == 5) {
                    Component listHeader = Component.text("Muted Players:")
                            .color(NamedTextColor.GOLD)
                            .decorate(TextDecoration.BOLD);

                    Component playerList = Component.empty();

                    for (UUID playerID : MuteAPI.getMutedPlayers()) {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerID);
                        if (MuteAPI.isMuted(playerID)) {
                            Component playerEntry;
                            if (MuteAPI.getMutedDuration(playerID) == 0) {
                                playerEntry = Component.text(offlinePlayer.getName())
                                        .color(NamedTextColor.WHITE)
                                        .append(Component.text(" - ").color(NamedTextColor.GRAY))
                                        .append(Component.text("Permanently muted").color(NamedTextColor.RED));
                            } else {
                                String time = MuteAPI.formatSekTime((int) MuteAPI.getMutedDuration(playerID));
                                playerEntry = Component.text(offlinePlayer.getName())
                                        .color(NamedTextColor.WHITE)
                                        .append(Component.text(" - ").color(NamedTextColor.GRAY))
                                        .append(Component.text("Muted for: ").color(NamedTextColor.RED))
                                        .append(Component.text(time).color(NamedTextColor.DARK_GRAY));
                            }

                            playerList = playerList.append(playerEntry.decoration(TextDecoration.BOLD,false)).append(Component.newline());
                        }
                    }
                    sender.sendMessage(listHeader.append(Component.newline()).append(playerList));
                }
            }
        } else if (cmd.equalsIgnoreCase("deletemsg")) {
            if (!PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "deletemsg") && !PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "*")) return;
            if (args.length < 6 || args.length > 7) {
                sender.sendMessage(Component.text("Usage: deletemsg <Player> <from> [to]").color(NamedTextColor.RED));
                return;
            }

            UUID targetID = Bukkit.getPlayerUniqueId(args[4]);

            int from, to;

            try {
                from = Integer.parseInt(args[5]);
                to = (args.length == 7) ? Integer.parseInt(args[6]) : from;
            }catch (NumberFormatException ex) {
                sender.sendMessage(Component.text("Invalid number for from/to", NamedTextColor.RED));
                return;
            }

            if (from < 1 || from > to) {
                sender.sendMessage(Component.text("Invalid range", NamedTextColor.RED));
                return;
            }

            ChatHistory history = ChatAPI.getChatHistory(targetID);

            int count = 0;
            for (int i = from; i<= to; i++) {
                SignedMessage msg = history.getMessagebyIndex(i);
                if (msg != null && msg.canDelete()) {
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.deleteMessage(msg);
                    }
                    count++;
                }
            }

            if (count == 0) {
                sender.sendMessage(Component.text("No messages found in range" , NamedTextColor.RED));
            }else {
                sender.sendMessage(Component.text("Deleted " + count + " message(s) from " + args[4], NamedTextColor.GREEN));
            }
        } else if (cmd.equalsIgnoreCase("viewmsgs")) {
            if (!PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "viewmsgs") && !PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "*")) return;
            if (args.length != 5) {
                sender.sendMessage(Component.text("Usage: viewmsgs <Player>" , NamedTextColor.RED));
                return;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[4]);

            ChatHistory history = ChatAPI.getChatHistory(target.getUniqueId());
            List<SignedMessage> allMessages = history.getAllMessages();

            if (allMessages.isEmpty()) {
                sender.sendMessage(Component.text("No messages from " + target.getName()).color(channelsColor));
                return;
            }

            sender.sendMessage(Component.text("Last messages from " + target.getName() + ":" , channelsColor));

            int i = 1;
            for (SignedMessage msg : allMessages) {
                String message = msg.message();

                Component line = Component.text("[" + i + "] ", NamedTextColor.YELLOW)
                        .append(Component.text(message, NamedTextColor.WHITE))
                        .hoverEvent(HoverEvent.showText(Component.text("Click to delete").color(NamedTextColor.GRAY)))
                        .clickEvent(ClickEvent.suggestCommand("/system player chat deletemsg " + target.getName() + " " + i));

                sender.sendMessage(line);
                i++;
            }
        }
    }

    public static void mutePlayer(Player target, Player sender, int duration) {
        if (MuteAPI.isMuted(target.getUniqueId())) {
            sender.sendMessage(Component.text("Error: ")
                    .color(NamedTextColor.RED)
                    .append(Component.text(target.getName())
                            .color(NamedTextColor.WHITE))
                    .append(Component.text(" is already muted.")
                            .color(NamedTextColor.RED)));
            return;
        }

        if (duration == 0) {
            if (PermissionAPI.isSystemUser(target)) {
                target.sendMessage(Component.text("You have been permanently muted")
                        .color(muteColor)
                        .decoration(TextDecoration.BOLD, true));
            }
            MuteAPI.add(target.getUniqueId(), 0);

            Punishment mutePunishment = new Punishment(
                    target.getUniqueId(),
                    sender.getUniqueId(),
                    Punishment.PunishmentType.MUTE,
                    "Muted manually",
                    System.currentTimeMillis(),
                    0,
                    true
            );

            PlayerHistoryManager historyManager = NikeySystem.getManager();
            historyManager.addPunishment(target.getUniqueId(), mutePunishment);

            sender.sendMessage(Component.text(target.getName()).color(NamedTextColor.WHITE)
                    .append(Component.text(" has been muted ").color(muteColor))
                    .append(Component.text("permanently").color(NamedTextColor.DARK_GRAY)));
        } else {
            if (PermissionAPI.isSystemUser(target)) {
                target.sendMessage(Component.text("You have been muted for ").color(muteColor) // RGB color: #1dc0f0
                        .append(Component.text(MuteAPI.decodeTime(duration)).color(NamedTextColor.WHITE)));
            }
            MuteAPI.add(target.getUniqueId(), System.currentTimeMillis() + (duration * 1000L));

            Punishment mutePunishment = new Punishment(
                    target.getUniqueId(),
                    sender.getUniqueId(),
                    Punishment.PunishmentType.MUTE,
                    "Muted manually",
                    System.currentTimeMillis(),
                    duration,
                    false
            );

            PlayerHistoryManager historyManager = NikeySystem.getManager();
            historyManager.addPunishment(target.getUniqueId(), mutePunishment);

            sender.sendMessage(Component.text(target.getName()).color(NamedTextColor.WHITE)
                    .append(Component.text(" has been muted for ").color(muteColor))
                    .append(Component.text(MuteAPI.decodeTime(duration)).color(NamedTextColor.DARK_GRAY)));
        }
    }

    public static void mutePlayer(UUID targetUUID, Player sender, int duration) {
        OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(targetUUID);

        if (offlineTarget.getName() == null) {
            sender.sendMessage(Component.text("Error: Player not found!").color(NamedTextColor.RED));
            return;
        }
        String targetName = offlineTarget.getName();

        if (MuteAPI.isMuted(targetUUID)) {
            sender.sendMessage(Component.text("Error: ")
                    .color(NamedTextColor.RED)
                    .append(Component.text(targetName)
                            .color(NamedTextColor.WHITE))
                    .append(Component.text(" is already muted.")
                            .color(NamedTextColor.RED)));
            return;
        }

        boolean isSystemUser = PermissionAPI.isSystemUser(offlineTarget.getName());
        Player onlineTarget = Bukkit.getPlayer(targetUUID);

        if (duration == 0) {
            if (isSystemUser && onlineTarget != null) {
                onlineTarget.sendMessage(Component.text("You have been permanently muted").color(muteColor));
            }
            MuteAPI.add(targetUUID, 0);

            Punishment mutePunishment = new Punishment(
                    offlineTarget.getUniqueId(),
                    sender.getUniqueId(),
                    Punishment.PunishmentType.MUTE,
                    "Muted manually",
                    System.currentTimeMillis(),
                    0,
                    true
            );

            PlayerHistoryManager historyManager = NikeySystem.getManager();
            historyManager.addPunishment(offlineTarget.getUniqueId(), mutePunishment);

            sender.sendMessage(Component.text(targetName).color(NamedTextColor.WHITE)
                    .append(Component.text(" has been muted ").color(muteColor))
                    .append(Component.text("permanently").color(NamedTextColor.DARK_GRAY)));
        } else {
            if (isSystemUser && onlineTarget != null) {
                onlineTarget.sendMessage(Component.text("You have been muted for ").color(muteColor)
                        .append(Component.text(MuteAPI.decodeTime(duration)).color(NamedTextColor.WHITE)));
            }
            MuteAPI.add(targetUUID, System.currentTimeMillis() + (duration * 1000L));

            Punishment mutePunishment = new Punishment(
                    offlineTarget.getUniqueId(),
                    sender.getUniqueId(),
                    Punishment.PunishmentType.MUTE,
                    "Muted manually",
                    System.currentTimeMillis(),
                    duration,
                    false
            );

            PlayerHistoryManager historyManager = NikeySystem.getManager();
            historyManager.addPunishment(offlineTarget.getUniqueId(), mutePunishment);


            sender.sendMessage(Component.text(targetName).color(NamedTextColor.WHITE)
                    .append(Component.text(" has been muted for ").color(muteColor))
                    .append(Component.text(MuteAPI.decodeTime(duration)).color(NamedTextColor.DARK_GRAY)));
        }
    }


    public static void unmutePlayer(Player target, Player sender) {
        if (!MuteAPI.isMuted(target.getUniqueId())) {
            sender.sendMessage(Component.text("Error: ").color(NamedTextColor.RED)
                    .append(Component.text(target.getName()).color(NamedTextColor.WHITE))
                    .append(Component.text(" is not muted").color(NamedTextColor.RED)));
            return;
        }

        if (PermissionAPI.isSystemUser(target)) {
            target.sendMessage(Component.text("You have been unmuted")
                    .color(TextColor.color(29, 192, 240))
                    .append(Component.text("!")
                            .color(TextColor.color(0, 255, 0))));
        }

        MuteAPI.remove(target.getUniqueId());

        sender.sendMessage(Component.text(target.getName()).color(NamedTextColor.WHITE)
                .append(Component.text(" has been ").color(muteColor))
                .append(Component.text("unmuted").color(NamedTextColor.GREEN)));

        Punishment unmutePunishment = new Punishment(
                target.getUniqueId(),
                sender.getUniqueId(),
                Punishment.PunishmentType.UNMUTE,
                "Unmuted manually",
                System.currentTimeMillis(),
                0,
                true
        );

        PlayerHistoryManager historyManager = NikeySystem.getManager();
        historyManager.addPunishment(target.getUniqueId(), unmutePunishment);
    }
}