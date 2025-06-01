package de.nikey.nikeysystem.Player.Distributor;

import com.destroystokyo.paper.profile.PlayerProfile;
import de.nikey.nikeysystem.General.ShieldCause;
import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.*;
import de.nikey.nikeysystem.Player.GUI.ModerationGUI;
import io.papermc.paper.ban.BanListType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.BanEntry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.ban.ProfileBanList;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

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
                sender.sendMessage("Usage: tempban <player> <duration> [reason]");
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
            PlayerHistoryManager historyManager = NikeySystem.getManager();
            historyManager.addPunishment(target.getUniqueId(), punishment);
            Instant banExpiry = Instant.now().plusSeconds(duration);
            Bukkit.getBanList(BanListType.PROFILE).addBan(target.getPlayerProfile(), reason, banExpiry, sender.getName());
            if (target.isOnline()) {
                Player t = Bukkit.getPlayer(target.getUniqueId());
                t.kick(ModerationAPI.KickMessanges.createTemporaryBanMessage(reason,new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date.from(banExpiry))));
            }

            sender.sendMessage(Component.text("Temp-Banned ").color(moderationColor)
                    .append(Component.text(args[4]).color(NamedTextColor.WHITE))
                    .append(Component.text(" for ").color(moderationColor))
                    .append(Component.text(args[5]).color(NamedTextColor.GRAY))
                    .append(Component.text(" due to ").color(moderationColor))
                    .append(Component.text(reason).color(NamedTextColor.DARK_GRAY)));
        }else if (cmd.equalsIgnoreCase("ban")) {
            if (args.length < 5) {
                sender.sendMessage("Usage: ban <player> [reason]");
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
            PlayerHistoryManager historyManager = NikeySystem.getManager();
            historyManager.addPunishment(target.getUniqueId(), punishment);

            // Banne den Spieler
            Bukkit.getBanList(BanListType.PROFILE).addBan(target.getPlayerProfile(), reason, (Date) null, sender.getName());
            if (target.isOnline()) {
                Player t = Bukkit.getPlayer(target.getUniqueId());
                t.kick(ModerationAPI.KickMessanges.createPermanentBanMessage(reason));
            }

            sender.sendMessage(Component.text("Banned ").color(moderationColor)
                    .append(Component.text(args[4]).color(NamedTextColor.WHITE))
                    .append(Component.text(" permanently").color(NamedTextColor.GRAY))
                    .append(Component.text(" for ").color(moderationColor))
                    .append(Component.text(reason).color(NamedTextColor.DARK_GRAY)));
        }else if (cmd.equalsIgnoreCase("freeze")) {
            if (args.length == 5) {
                Player target = Bukkit.getPlayer(args[4]);
                if (target == null || !HideAPI.canSee(sender.getUniqueId(),target.getUniqueId())) {
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

                PlayerHistoryManager historyManager = NikeySystem.getManager();
                historyManager.addPunishment(target.getUniqueId(), punishment);

                ModerationAPI.freezePlayer(target.getUniqueId());

                sender.sendMessage(Component.text("Froze ").color(moderationColor)
                        .append(Component.text(target.getName()).color(NamedTextColor.WHITE)));
            } else if (args.length == 6) {
                Player target = Bukkit.getPlayer(args[4]);
                if (target == null || !HideAPI.canSee(sender.getUniqueId(),target.getUniqueId())) {
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

                PlayerHistoryManager historyManager = NikeySystem.getManager();
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
            if (target == null || !HideAPI.canSee(sender.getUniqueId(),target.getUniqueId())) {
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
            PlayerHistoryManager historyManager = NikeySystem.getManager();
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

            Punishment unbanPunishment = new Punishment(
                    player.getUniqueId(),
                    Punishment.PunishmentType.UNBAN,
                    "Unbanned manually",
                    System.currentTimeMillis(),
                    0,
                    true
            );
            PlayerHistoryManager historyManager = NikeySystem.getManager();
            historyManager.addPunishment(player.getUniqueId(), unbanPunishment);

            sender.sendMessage(Component.text("Unbanned ").color(moderationColor)
                    .append(Component.text(args[4]).color(NamedTextColor.WHITE)));

        }else if (cmd.equalsIgnoreCase("banlist")) {

            ProfileBanList banList = Bukkit.getBanList(BanListType.PROFILE);

            if (banList.getEntries().isEmpty() && ModerationAPI.getFrozenPlayers().isEmpty()) {
                sender.sendMessage(Component.text("There are no banned or frozen players", NamedTextColor.RED));
                return;
            }

            sender.sendMessage(Component.text("=== Punished Players ===", moderationColor, TextDecoration.BOLD).appendNewline()
                    .append(Component.text("-------------------", NamedTextColor.GRAY)));

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (!banList.getEntries().isEmpty()) {
                sender.sendMessage(Component.text("Banned Players:", NamedTextColor.YELLOW));
                for (BanEntry entry : banList.getEntries()) {
                    String target = banList.getBanEntry((PlayerProfile) entry.getBanTarget()).getBanTarget().getName();
                    String reason = entry.getReason() != null ? entry.getReason() : "No reason provided";
                    String source = entry.getSource();
                    String creationDate = dateFormat.format(entry.getCreated());
                    String expiryDate = entry.getExpiration() != null
                            ? dateFormat.format(entry.getExpiration())
                            : "Never";



                    Component banInfo = Component.text()
                            .append(Component.text("Player: ", NamedTextColor.YELLOW))
                            .append(Component.text(target   , NamedTextColor.WHITE))
                            .append(Component.newline())
                            .append(Component.text("Reason: ", NamedTextColor.YELLOW))
                            .append(Component.text(reason, NamedTextColor.WHITE))
                            .append(Component.newline())
                            .append(Component.text("Banned by: ", NamedTextColor.YELLOW))
                            .append(Component.text(source, NamedTextColor.WHITE))
                            .append(Component.newline())
                            .append(Component.text("Created: ", NamedTextColor.YELLOW))
                            .append(Component.text(creationDate, NamedTextColor.WHITE))
                            .append(Component.newline())
                            .append(Component.text("Expires: ", NamedTextColor.RED))
                            .append(Component.text(expiryDate, NamedTextColor.WHITE))
                            .append(Component.newline())
                            .append(Component.text("-------------------", NamedTextColor.GRAY))
                            .build();

                    sender.sendMessage(banInfo);
                }
            }

            if (!ModerationAPI.getFrozenPlayers().isEmpty()) {
                sender.sendMessage(Component.text("Frozen Players:", NamedTextColor.AQUA));
                for (UUID frozenPlayerId : ModerationAPI.getFrozenPlayers()) {
                    String playerName = Bukkit.getOfflinePlayer(frozenPlayerId).getName();

                    if (playerName == null) continue;
                    Component freezeInfo = Component.text()
                            .append(Component.text("Player: ", NamedTextColor.AQUA))
                            .append(Component.text(playerName, NamedTextColor.WHITE))
                            .append(Component.newline())
                            .append(Component.space())
                            .build();

                    sender.sendMessage(freezeInfo);
                }
            }
        }
    }
}
