package de.nikey.nikeysystem.Player.Distributor;

import de.nikey.nikeysystem.General.ShieldCause;
import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.*;
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
import java.util.Arrays;
import java.util.Date;

import static de.nikey.nikeysystem.Player.API.ModerationAPI.parseDuration;
import static de.nikey.nikeysystem.Player.API.ModerationAPI.parseTime;

public class ModerationDistributor {

    private static final TextColor moderationColor = TextColor.color(129, 59, 233);

    public static void manageModeration(Player sender, String[] args) {
        String cmd = args[3];
        if (cmd.isEmpty()) return;

        if (cmd.equalsIgnoreCase("manage")) {
            ModerationGUI.openModerationGUI(sender, sender);
        } else if (cmd.equalsIgnoreCase("tempban")) {
            if (args.length < 6) {
                sender.sendMessage("Usage: tempban <player> <duration> <reason>");
                return;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(args[4]);
            if (!PermissionAPI.isAllowedToChange(sender.getName(),target.getName(),ShieldCause.MODERATION_TEMPBAN)) {
                sender.sendMessage(Component.text("Missing permission to change target").color(NamedTextColor.RED));
                return;
            }

            int duration = parseTime(args[5]);
            if (duration <= 0) {
                sender.sendMessage("Invalid duration format");
                return;
            }

            String reason = String.join(" ", Arrays.copyOfRange(args, 6, args.length));

            // Create punishment
            Punishment punishment = new Punishment(
                    target.getUniqueId(),
                    Punishment.PunishmentType.TEMPBAN,
                    reason,
                    System.currentTimeMillis(),
                    duration,
                    false
            );

            // Add to history and kick player
            PlayerHistoryManager historyManager = new PlayerHistoryManager();
            historyManager.addPunishment(target.getUniqueId(), punishment);
            if (target.isOnline()) {
                Player t = Bukkit.getPlayer(target.getUniqueId());
                t.kick(Component.text("You have been temporarily banned for " + args[5] + " due to " + reason));
            }
            Instant banExpiry = Instant.now().plusSeconds(duration);

            Bukkit.getBanList(BanListType.PROFILE).addBan(target.getPlayerProfile(), reason, banExpiry, sender.getName());

            ChatAPI.sendManagementMessage(Component.text(sender.getName()).color(NamedTextColor.WHITE)
                            .append(Component.text(" temp-banned ").color(moderationColor))
                            .append(Component.text(args[4]).color(NamedTextColor.WHITE))
                            .append(Component.text(" for ").color(moderationColor))
                            .append(Component.text(reason).color(NamedTextColor.GRAY)),
                    ChatAPI.ManagementType.INFO,
                    true
            );

            sender.sendMessage(Component.text("Temp-Banned ").color(moderationColor)
                    .append(Component.text(args[4]).color(NamedTextColor.WHITE))
                    .append(Component.text(" for ").color(moderationColor))
                    .append(Component.text(args[5]).color(NamedTextColor.GRAY))
                    .append(Component.text(" due to ").color(moderationColor))
                    .append(Component.text(reason).color(NamedTextColor.DARK_GRAY)));
        }else if (cmd.equalsIgnoreCase("ban")) {
            if (args.length < 6) {
                sender.sendMessage("Usage: ban <player> <reason>");
                return;
            }

            // Hole den Zielspieler
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[4]);
            if (!PermissionAPI.isAllowedToChange(sender.getName(),target.getName(),ShieldCause.MODERATION_BAN)) {
                sender.sendMessage(Component.text("Missing permission to change target").color(NamedTextColor.RED));
                return;
            }

            // Grund für den Bann
            String reason = String.join(" ", Arrays.copyOfRange(args, 5, args.length));

            Punishment punishment = new Punishment(
                    target.getUniqueId(),
                    Punishment.PunishmentType.BAN,
                    reason,
                    System.currentTimeMillis(),
                    0,
                    true
            );

            // Füge das Punishment der Historie hinzu
            PlayerHistoryManager historyManager = new PlayerHistoryManager();
            historyManager.addPunishment(target.getUniqueId(), punishment);

            // Banne den Spieler
            if (target.isOnline()) {
                Player t = Bukkit.getPlayer(target.getUniqueId());
                t.kick(Component.text("You have been permanently banned for: " + reason));
            }
            Bukkit.getBanList(BanListType.PROFILE).addBan(target.getPlayerProfile(), reason, (Date) null, sender.getName());

            ChatAPI.sendManagementMessage(Component.text(sender.getName()).color(NamedTextColor.WHITE)
                            .append(Component.text(" banned ").color(moderationColor))
                            .append(Component.text(args[4]).color(NamedTextColor.WHITE))
                            .append(Component.text(" for ").color(moderationColor))
                            .append(Component.text(reason).color(NamedTextColor.GRAY)),
                    ChatAPI.ManagementType.INFO,
                    true
            );

            sender.sendMessage(Component.text("Banned ").color(moderationColor)
                    .append(Component.text(args[4]).color(NamedTextColor.WHITE))
                    .append(Component.text(" permanently").color(NamedTextColor.GRAY))
                    .append(Component.text(" for ").color(moderationColor))
                    .append(Component.text(reason).color(NamedTextColor.DARK_GRAY)));
        }else if (cmd.equalsIgnoreCase("freeze")) {
            if (args.length == 5) {
                Player target = Bukkit.getPlayer(args[4]);
                if (target == null || !HideAPI.canSee(sender,target)) {
                    sender.sendMessage(Component.text("Target not found").color(NamedTextColor.RED));
                    return;
                }

                if (!PermissionAPI.isAllowedToChange(sender.getName(),target.getName(),ShieldCause.MODERATION_FREEZE)) {
                    sender.sendMessage(Component.text("Missing permission to change target").color(NamedTextColor.RED));
                    return;
                }

                if (ModerationAPI.isFrozen(target.getUniqueId())) {
                    sender.sendMessage(Component.text(target.getName()).color(NamedTextColor.WHITE)
                            .append(Component.text(" is already frozen").color(moderationColor)));
                    return;
                }

                Punishment punishment = new Punishment(
                        target.getUniqueId(),
                        Punishment.PunishmentType.FREEZE,
                        "Frozen by " + sender.getName(),
                        System.currentTimeMillis(),
                        0,
                        true
                );

                PlayerHistoryManager historyManager = new PlayerHistoryManager();
                historyManager.addPunishment(target.getUniqueId(), punishment);

                ModerationAPI.freezePlayer(target.getUniqueId());

                sender.sendMessage(Component.text("Froze ").color(moderationColor)
                        .append(Component.text(target.getName()).color(NamedTextColor.WHITE)));
            } else if (args.length == 6) {
                Player target = Bukkit.getPlayer(args[4]);
                if (target == null || !HideAPI.canSee(sender,target)) {
                    sender.sendMessage(Component.text("Target not found").color(NamedTextColor.RED));
                    return;
                }

                if (!PermissionAPI.isAllowedToChange(sender.getName(),target.getName(),ShieldCause.MODERATION_FREEZE)) {
                    sender.sendMessage(Component.text("Missing permission to change target").color(NamedTextColor.RED));
                    return;
                }

                if (ModerationAPI.isFrozen(target.getUniqueId())) {
                    sender.sendMessage(Component.text(target.getName()).color(NamedTextColor.WHITE)
                            .append(Component.text(" is already frozen").color(moderationColor)));
                    return;
                }

                String timeInput = args[5];
                int freezeTimeInSeconds;
                try {
                    freezeTimeInSeconds = ModerationAPI.parseTime(timeInput); // Konvertiere die Zeit in Sekunden
                } catch (IllegalArgumentException e) {
                    sender.sendMessage("Invalid time format. Please use s, m, h, d, or w (e.g., 5m, 2h).");
                    return;
                }

                Punishment punishment = new Punishment(
                        target.getUniqueId(),
                        Punishment.PunishmentType.FREEZE,
                        "Frozen for " + timeInput,
                        System.currentTimeMillis(),
                        freezeTimeInSeconds,
                        false
                );

                PlayerHistoryManager historyManager = new PlayerHistoryManager();
                historyManager.addPunishment(target.getUniqueId(), punishment);

                ModerationAPI.freezePlayer(target.getUniqueId());

                sender.sendMessage(Component.text("Froze ").color(moderationColor)
                        .append(Component.text(target.getName()).color(NamedTextColor.WHITE))
                        .append(Component.text(" for ").color(moderationColor))
                        .append(Component.text(timeInput).color(NamedTextColor.GRAY)));

                Bukkit.getScheduler().runTaskLater(NikeySystem.getPlugin(), () -> {
                    ModerationAPI.unfreezePlayer(target.getUniqueId());
                    Punishment unfreezePunishment = new Punishment(
                            target.getUniqueId(),
                            Punishment.PunishmentType.UNFREEZE,
                            "Unfrozen after " + timeInput,
                            System.currentTimeMillis(),
                            0,
                            true
                    );
                    historyManager.addPunishment(target.getUniqueId(), unfreezePunishment);

                    if (sender.isOnline()) {
                        sender.sendMessage(Component.text("Unfroze ").color(moderationColor)
                                .append(Component.text(target.getName()).color(NamedTextColor.WHITE)));
                    }
                }, freezeTimeInSeconds * 20L);
            }
        } else if (cmd.equalsIgnoreCase("Unfreeze")) {
            if (args.length != 5) {
                sender.sendMessage("Usage: unfreeze <player>");
                return;
            }

            Player target = Bukkit.getPlayer(args[4]);
            if (target == null || !HideAPI.canSee(sender,target)) {
                sender.sendMessage(Component.text("Target not found").color(NamedTextColor.RED));
                return;
            }

            if (!PermissionAPI.isAllowedToChange(sender.getName(),target.getName(),ShieldCause.MODERATION_UNFREEZE)) {
                sender.sendMessage(Component.text("Missing permission to change target").color(NamedTextColor.RED));
                return;
            }

            if (!ModerationAPI.isFrozen(target.getUniqueId())) {
                sender.sendMessage(Component.text(target.getName()).color(NamedTextColor.WHITE)
                        .append(Component.text(" is not frozen").color(moderationColor)));
                return;
            }

            ModerationAPI.unfreezePlayer(target.getUniqueId());

            Punishment unfreezePunishment = new Punishment(
                    target.getUniqueId(),
                    Punishment.PunishmentType.UNFREEZE,
                    "Unfrozen manually",
                    System.currentTimeMillis(),
                    0,
                    true
            );
            PlayerHistoryManager historyManager = new PlayerHistoryManager();
            historyManager.addPunishment(target.getUniqueId(), unfreezePunishment);

            sender.sendMessage(Component.text("Unfroze ").color(moderationColor)
                    .append(Component.text(target.getName()).color(NamedTextColor.WHITE)));
        }else if (cmd.equalsIgnoreCase("unban")) {
            if (args.length != 5) {
                sender.sendMessage("Usage: unban <player>");
                return;
            }

            OfflinePlayer player = Bukkit.getOfflinePlayer(args[4]);

            if (!Bukkit.getBanList(BanListType.PROFILE).isBanned(player.getPlayerProfile())) {
                sender.sendMessage(Component.text(args[4]).color(NamedTextColor.WHITE)
                        .append(Component.text(" is not banned").color(moderationColor)));
                return;
            }

            Bukkit.getBanList(BanListType.PROFILE).pardon(player.getPlayerProfile());

            ChatAPI.sendManagementMessage(Component.text(sender.getName()).color(NamedTextColor.WHITE)
                    .append(Component.text(" unbanned ").color(moderationColor))
                    .append(Component.text(args[4]).color(NamedTextColor.WHITE)),
                    ChatAPI.ManagementType.INFO,
                    true
            );

            Punishment unbanPunishment = new Punishment(
                    player.getUniqueId(),
                    Punishment.PunishmentType.UNBAN,
                    "Unbanned manually",
                    System.currentTimeMillis(),
                    0,
                    true
            );
            PlayerHistoryManager historyManager = new PlayerHistoryManager();
            historyManager.addPunishment(player.getUniqueId(), unbanPunishment);

            sender.sendMessage(Component.text("Unbanned ").color(moderationColor)
                    .append(Component.text(args[4]).color(NamedTextColor.WHITE)));

        }
    }
}