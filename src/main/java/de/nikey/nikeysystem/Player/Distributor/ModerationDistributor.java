package de.nikey.nikeysystem.Player.Distributor;

import de.nikey.nikeysystem.Player.API.ModerationAPI;
import de.nikey.nikeysystem.Player.GUI.ModerationGUI;
import io.papermc.paper.ban.BanListType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModerationDistributor {

    private static final TextColor moderationColor = TextColor.color(129, 59, 233);

    public static void manageModeration(Player sender, String[] args) {
        String cmd = args[3];
        if (cmd.isEmpty()) return;

        if (cmd.equalsIgnoreCase("manage")) {
            ModerationGUI.openModerationGUI(sender,sender);
        } else if (cmd.equalsIgnoreCase("ban")) {
            if (args.length == 5) {
                String playerName = args[4];

                OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);

                ModerationAPI.Reason reason = ModerationAPI.Reason.OTHER;

                Bukkit.getBanList(BanListType.PROFILE).addBan(target.getPlayerProfile(), reason.getDescription(), (Date) null, sender.getName());
                if (target.isOnline()) {
                    target.getPlayer().kick(Component.text("You are banned"));
                }

                sender.sendMessage(Component.text("Banned ").color(moderationColor)
                        .append(Component.text(playerName).color(NamedTextColor.WHITE))
                        .append(Component.text(" permanently").color(NamedTextColor.GRAY)));


            }else if (args.length == 6) {
                String playerName = args[4];
                String timeInput = args[5];

                OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);

                ModerationAPI.Reason reason = ModerationAPI.Reason.DEFAULT;

                if (timeInput.equalsIgnoreCase("p")) {
                    // Permanent ban
                    Bukkit.getBanList(BanListType.PROFILE).addBan(target.getPlayerProfile(),reason.getDescription(), (Date) null,sender.getName());
                    sender.sendMessage(Component.text("Banned ").color(moderationColor)
                            .append(Component.text(playerName).color(NamedTextColor.WHITE))
                            .append(Component.text(" permanently").color(NamedTextColor.GRAY)));
                    return;
                }

                Duration duration = ModerationAPI.parseDuration(timeInput);
                if (duration == null) {
                    sender.sendMessage(Component.text("Invalid time format! Use something like 2M, 2w, 5d, or 1h")
                            .color(NamedTextColor.RED));
                    return;
                }

                Instant banExpiry = Instant.now().plus(duration);
                Date expiryDate = Date.from(banExpiry);

                Bukkit.getBanList(BanListType.PROFILE).addBan(target.getPlayerProfile(), reason.getDescription(), expiryDate, sender.getName());
                if (target.isOnline()) {
                    target.getPlayer().kick(Component.text("You are banned for " + timeInput + ". Reason: " + reason.getDescription()));
                }

                sender.sendMessage(Component.text("Banned ").color(moderationColor)
                        .append(Component.text(playerName).color(NamedTextColor.WHITE))
                        .append(Component.text(" until ").color(moderationColor))
                        .append(Component.text(expiryDate.toString()).color(NamedTextColor.GRAY))
                        .append(Component.text(" for ").color(moderationColor))
                        .append(Component.text(reason.getDescription()).color(NamedTextColor.DARK_GRAY)));
            }else if (args.length == 7) {
                String playerName = args[4];
                String timeInput = args[5];
                String reasonInput = args[6].toUpperCase();

                OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);

                ModerationAPI.Reason reason;
                try {
                    reason = ModerationAPI.Reason.valueOf(reasonInput);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(Component.text("Invalid reason! Available reasons: " + getAvailableReasons())
                            .color(NamedTextColor.RED));
                    return;
                }

                if (timeInput.equalsIgnoreCase("p")) {
                    // Permanent ban
                    Bukkit.getBanList(BanListType.PROFILE).addBan(target.getPlayerProfile(),reason.getDescription(), (Date) null,sender.getName());
                    sender.sendMessage(Component.text("Banned ").color(moderationColor)
                            .append(Component.text(playerName).color(NamedTextColor.WHITE))
                            .append(Component.text(" permanently").color(NamedTextColor.GRAY))
                            .append(Component.text(" for ").color(moderationColor))
                            .append(Component.text(reason.getDescription()).color(NamedTextColor.DARK_GRAY)));
                    return;
                }

                // Temporary ban
                Duration duration = ModerationAPI.parseDuration(timeInput);
                if (duration == null) {
                    sender.sendMessage(Component.text("Invalid time format! Use something like 2M, 2w, 5d, or 1h")
                            .color(NamedTextColor.RED));
                    return;
                }

                Instant banExpiry = Instant.now().plus(duration);
                Date expiryDate = Date.from(banExpiry);

                Bukkit.getBanList(BanListType.PROFILE).addBan(target.getPlayerProfile(), reason.getDescription(), expiryDate, sender.getName());
                if (target.isOnline()) {
                    target.getPlayer().kick(Component.text("You are banned for " + timeInput + ". Reason: " + reason.getDescription()));
                }

                sender.sendMessage(Component.text("Banned ").color(moderationColor)
                        .append(Component.text(playerName).color(NamedTextColor.WHITE))
                        .append(Component.text(" until ").color(moderationColor))
                        .append(Component.text(expiryDate.toString()).color(NamedTextColor.GRAY))
                        .append(Component.text(" for ").color(moderationColor))
                        .append(Component.text(reason.getDescription()).color(NamedTextColor.DARK_GRAY)));
            }
        }
    }

    private static String getAvailableReasons() {
        return Stream.of(ModerationAPI.Reason.values())
                .map(ModerationAPI.Reason::name)
                .collect(Collectors.joining(", "));
    }
}
