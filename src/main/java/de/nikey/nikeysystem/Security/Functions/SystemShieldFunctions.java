package de.nikey.nikeysystem.Security.Functions;

import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent;
import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.HideAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.Security.API.SystemShieldAPI;
import de.nikey.nikeysystem.Security.Distributor.SystemShieldDistributor;
import io.papermc.paper.ban.BanListType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.ban.IpBanList;
import org.bukkit.ban.ProfileBanList;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.command.UnknownCommandEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.RemoteServerCommandEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static de.nikey.nikeysystem.Security.API.SystemShieldAPI.disableShieldRequest;
import static de.nikey.nikeysystem.Security.API.SystemShieldAPI.shieldRequest;

public class SystemShieldFunctions implements Listener {


    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage().toLowerCase();
        Player player = event.getPlayer();

        if (message.equals("/accept")) {
            handleAccept(player);
            event.setCancelled(true);  // Verhindere, dass der Befehl weiter verarbeitet wird
        } else if (message.equals("/decline")) {
            handleDecline(player);
            event.setCancelled(true);  // Verhindere, dass der Befehl weiter verarbeitet wird
        }
    }




    private void handleAccept(Player player) {
        String requesterName = shieldRequest.get(player.getName());

        if (requesterName == null) {
            if (disableShieldRequest.get(player.getName()) != null) {
                String name = disableShieldRequest.get(player.getName());
                Player requesterPlayer = Bukkit.getPlayer(name);

                if (requesterPlayer == null || !requesterPlayer.isOnline()) {
                    player.sendMessage("§cError: Request sender not found!");
                    disableShieldRequest.remove(player.getName());
                    return;
                }

                requesterPlayer.sendMessage(player.getName()+ " has §aaccepted§r your System Shield remove request");
                player.sendMessage(ChatColor.of("#42b6f5")+ "System Shield is now §cremoved§r!");

                SystemShieldAPI.removeShieldUser(player.getName());
                SystemShieldDistributor.saveSystemShield();

                // Entferne die Anfrage nach Annahme
                disableShieldRequest.remove(player.getName());
            }
            return;
        }

        Player requesterPlayer = Bukkit.getPlayer(requesterName);

        if (requesterPlayer == null || !requesterPlayer.isOnline()) {
            player.sendMessage("§cError: Request sender not found!");
            shieldRequest.remove(player.getName());
            return;
        }

        // Teleportiere den anfragenden Spieler zum Zielspieler
        requesterPlayer.sendMessage(player.getName()+ " has §aaccepted§r your System Shield request");
        player.sendMessage(ChatColor.of("#42b6f5")+ "System Shield is now §aenabled§r!");

        SystemShieldAPI.addShieldUser(player.getName());
        SystemShieldDistributor.saveSystemShield();

        // Entferne die Anfrage nach Annahme
        shieldRequest.remove(player.getName());
    }

    private void handleDecline(Player player) {
        String requesterName = shieldRequest.get(player.getName());

        if (requesterName == null) {
            if (disableShieldRequest.get(player.getName()) != null) {
                Player requesterPlayer = Bukkit.getPlayer(disableShieldRequest.get(player.getName()));

                if (requesterPlayer == null || !requesterPlayer.isOnline()) {
                    player.sendMessage("§cError: Request sender not found!");
                    disableShieldRequest.remove(player.getName());
                    return;
                }

                player.sendMessage("You have §cdeclined §rthe system shield remove request");
                requesterPlayer.sendMessage(player.getName() + " has §cdeclined§r your System Shield remove request");

                // Entferne die Anfrage nach Ablehnung
                disableShieldRequest.remove(player.getName());
            }
            return;
        }

        Player requesterPlayer = Bukkit.getPlayer(requesterName);

        if (requesterPlayer == null || !requesterPlayer.isOnline()) {
            player.sendMessage("§cError: Request sender not found!");
            shieldRequest.remove(player.getName());
            return;
        }

        player.sendMessage("You have §cdeclined §rthe system shield request");
        requesterPlayer.sendMessage(player.getName() + " has §cdeclined§r your System Shield request");

        // Entferne die Anfrage nach Ablehnung
        shieldRequest.remove(player.getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (SystemShieldAPI.isShieldUser(player.getName())) {
                EntityDamageEvent.DamageCause cause = event.getCause();
                if (cause == EntityDamageEvent.DamageCause.KILL || cause == EntityDamageEvent.DamageCause.SUICIDE) {
                    event.setCancelled(true);
                    player.sendActionBar(Component.text("System Shield blocked damage cause: ").color(NamedTextColor.RED).append(Component.text(event.getCause().name())));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        final String[] args = event.getMessage().split(" ");

        if (args[0].equalsIgnoreCase("/ban") || args[0].equalsIgnoreCase("/ban-ip") || args[0].equalsIgnoreCase("/kick")
                || args[0].equalsIgnoreCase("/damage") || args[0].equalsIgnoreCase("/clear") || args[0].equalsIgnoreCase("/deop") || args[0].equalsIgnoreCase("/kill"))  {
            if (args.length >= 2) {
                if (SystemShieldAPI.isShieldUser(args[1])) {
                    Player target = Bukkit.getPlayer(args[1]);
                    assert target != null;
                    if (target != event.getPlayer()) {
                        if (HideAPI.canSee(event.getPlayer() , target)) {
                            event.setCancelled(true);
                            if (PermissionAPI.isSystemUser(event.getPlayer())) {
                                event.getPlayer().sendMessage(Component.text("Security:").color(TextColor.color(202, 34, 34)).append(Component.text(" Command blocked by system shield")));
                            }
                            Component textComponent = Component.text(event.getPlayer().getName())
                                    .color(NamedTextColor.WHITE)
                                    .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                                    .append(Component.text(event.getMessage(), NamedTextColor.RED))
                                    .append(Component.text(" on you", NamedTextColor.GRAY));

                            target.sendActionBar(textComponent);
                        }else {
                            event.setCancelled(true);
                            event.getPlayer().sendMessage("§cNo player was found");
                            Component textComponent = Component.text(event.getPlayer().getName())
                                    .color(NamedTextColor.WHITE)
                                    .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                                    .append(Component.text(event.getMessage(), NamedTextColor.RED))
                                    .append(Component.text(" on you", NamedTextColor.GRAY));

                            target.sendActionBar(textComponent);
                        }
                    }
                }
            }
        }else if (args[0].equalsIgnoreCase("/gamemode")) {
            if (args.length >= 3) {
                if (SystemShieldAPI.isShieldUser(args[2])) {
                    Player target = Bukkit.getPlayer(args[2]);
                    assert target != null;
                    if (target != event.getPlayer()) {
                        if (HideAPI.canSee(event.getPlayer() , target)) {
                            event.setCancelled(true);
                            if (PermissionAPI.isSystemUser(event.getPlayer())) {
                                event.getPlayer().sendMessage(Component.text("Security:").color(TextColor.color(202, 34, 34)).append(Component.text(" Command blocked by system shield")));
                            }
                            TextComponent textComponent = Component.text(event.getPlayer().getName())
                                    .color(NamedTextColor.WHITE)
                                    .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                                    .append(Component.text(event.getMessage(), NamedTextColor.RED))
                                    .append(Component.text(" on you", NamedTextColor.GRAY));

                            target.sendActionBar(textComponent);
                        }else {
                            event.setCancelled(true);
                            event.getPlayer().sendMessage("§cNo player was found");
                            Component textComponent = Component.text(event.getPlayer().getName())
                                    .color(NamedTextColor.WHITE)
                                    .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                                    .append(Component.text(event.getMessage(), NamedTextColor.RED))
                                    .append(Component.text(" on you", NamedTextColor.GRAY));

                            target.sendActionBar(textComponent);
                        }
                    }
                }
            }
        }
        if (args[0].equalsIgnoreCase("/tp") || args[0].equalsIgnoreCase("/teleport")) {
            if (args.length > 2) {
                Player targetPlayer = event.getPlayer().getServer().getPlayer(args[1]);
                Player targetPlayer2 = event.getPlayer().getServer().getPlayer(args[2]);

                if (targetPlayer2 == null)return;
                if (targetPlayer != null && SystemShieldAPI.isShieldUser(targetPlayer.getName())) {
                    // Wenn der Sender nicht das Ziel ist, blockieren wir die Teleportation
                    if (targetPlayer != event.getPlayer()) {
                        if (HideAPI.canSee(event.getPlayer(), targetPlayer)) {
                            event.setCancelled(true);
                            if (PermissionAPI.isSystemUser(event.getPlayer())) {
                                event.getPlayer().sendMessage(Component.text("Security:").color(TextColor.color(202, 34, 34)).append(Component.text(" Command blocked by system shield")));
                            }
                            Component textComponent = Component.text(event.getPlayer().getName())
                                    .color(NamedTextColor.WHITE)
                                    .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                                    .append(Component.text(event.getMessage(), NamedTextColor.RED))
                                    .append(Component.text(" on you", NamedTextColor.GRAY));

                            targetPlayer2.sendActionBar(textComponent);
                        } else {
                            event.setCancelled(true);
                            event.getPlayer().sendMessage("§cNo player was found");
                            Component textComponent = Component.text(event.getPlayer().getName())
                                    .color(NamedTextColor.WHITE)
                                    .append(Component.text("tried to use: ", NamedTextColor.GRAY))
                                    .append(Component.text(event.getMessage(), NamedTextColor.RED))
                                    .append(Component.text(" on you", NamedTextColor.GRAY));

                            targetPlayer2.sendActionBar(textComponent);
                        }
                    }
                }else if (SystemShieldAPI.isShieldUser(targetPlayer2.getName())){
                    if (targetPlayer2 != event.getPlayer()) {
                        if (HideAPI.canSee(event.getPlayer(), targetPlayer2)) {
                            event.setCancelled(true);
                            if (PermissionAPI.isSystemUser(event.getPlayer())) {
                                event.getPlayer().sendMessage(Component.text("Security:").color(TextColor.color(202, 34, 34)).append(Component.text(" Command blocked by system shield")));
                            }
                            Component textComponent = Component.text(event.getPlayer().getName())
                                    .color(NamedTextColor.WHITE)
                                    .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                                    .append(Component.text(event.getMessage(), NamedTextColor.RED))
                                    .append(Component.text(" on you", NamedTextColor.GRAY));

                            // Actionbar an den Spieler senden
                            targetPlayer2.sendActionBar(textComponent);
                        } else {
                            event.setCancelled(true);
                            event.getPlayer().sendMessage("§cNo player was found");
                            Component textComponent = Component.text(event.getPlayer().getName())
                                    .color(NamedTextColor.WHITE)
                                    .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                                    .append(Component.text(event.getMessage(), NamedTextColor.RED))
                                    .append(Component.text(" on you", NamedTextColor.GRAY));

                            targetPlayer2.sendActionBar(textComponent);
                        }
                    }
                }
            }
        }else if (args[0].equalsIgnoreCase("/effect") ){
            if (args.length >= 4) {
                if (SystemShieldAPI.isShieldUser(args[2]) && (args[1].equalsIgnoreCase("give") || args[1].equalsIgnoreCase("clear"))) {
                    Player target = Bukkit.getPlayer(args[2]);
                    assert target != null;
                    if (target != event.getPlayer()) {
                        if (HideAPI.canSee(event.getPlayer() , target)) {
                            event.setCancelled(true);
                            if (PermissionAPI.isSystemUser(event.getPlayer())) {
                                event.getPlayer().sendMessage(Component.text("Security:").color(TextColor.color(202, 34, 34)).append(Component.text(" Command blocked by system shield")));
                            }
                            TextComponent textComponent = Component.text(event.getPlayer().getName())
                                    .color(NamedTextColor.WHITE)
                                    .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                                    .append(Component.text(event.getMessage(), NamedTextColor.RED))
                                    .append(Component.text(" on you", NamedTextColor.GRAY));

                            target.sendActionBar(textComponent);
                        }else {
                            event.setCancelled(true);
                            event.getPlayer().sendMessage("§cNo player was found");
                            Component textComponent = Component.text(event.getPlayer().getName())
                                    .color(NamedTextColor.WHITE)
                                    .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                                    .append(Component.text(event.getMessage(), NamedTextColor.RED))
                                    .append(Component.text(" on you", NamedTextColor.GRAY));

                            target.sendActionBar(textComponent);
                        }
                    }
                }
            }
        }

        String command = event.getMessage().startsWith("/")
                ? event.getMessage().substring(1)
                : event.getMessage();
        handleCommand(event.getPlayer(), command, event);
    }

    private void handleCommand(CommandSender sender, String command, Cancellable event) {
        final String[] args = command.split(" ");
        if (args.length == 0) return;

        Map<String, Integer> commandTargetMap = new HashMap<>();
        commandTargetMap.put("ban", 1);
        commandTargetMap.put("ban-ip", 1);
        commandTargetMap.put("kick", 1);
        commandTargetMap.put("damage", 1);
        commandTargetMap.put("clear", 1);
        commandTargetMap.put("op", 1);
        commandTargetMap.put("deop", 1);
        commandTargetMap.put("kill", 1);
        commandTargetMap.put("gamemode", 2);
        commandTargetMap.put("advancement", 2);
        commandTargetMap.put("tp", 1);
        commandTargetMap.put("teleport", 1);
        commandTargetMap.put("effect", 2);
        commandTargetMap.put("pardon", 1);
        commandTargetMap.put("pardon-ip", 1);
        commandTargetMap.put("enchant", 1);
        commandTargetMap.put("experience", 2);
        commandTargetMap.put("spawnpoint", 1);
        commandTargetMap.put("particle", 11);
        commandTargetMap.put("bossbar", 5);
        commandTargetMap.put("data", 3);
        commandTargetMap.put("give", 1);
        commandTargetMap.put("item", 3);
        commandTargetMap.put("msg", 1);
        commandTargetMap.put("w", 1);
        commandTargetMap.put("tell", 1);
        commandTargetMap.put("playsound", 3);
        commandTargetMap.put("recipe", 2);
        commandTargetMap.put("rotate", 1);
        commandTargetMap.put("transfer", 3);
        commandTargetMap.put("title", 1);
        commandTargetMap.put("whitelist", 2);

        // Zielindex basierend auf dem Kommando abrufen
        int targetIndex = commandTargetMap.getOrDefault(args[0].toLowerCase(), -1);

        // Prüfen, ob ein gültiger Zielindex existiert
        if (targetIndex >= 0 && args.length > targetIndex) {
            for (int i = targetIndex; i < args.length; i++) {
                String targetName = args[i];

                if (SystemShieldAPI.isShieldUser(targetName)) {
                    if (sender.getName().equalsIgnoreCase(targetName))continue;
                    event.setCancelled(true);
                    Player targetPlayer = Bukkit.getPlayer(targetName);

                    if (targetPlayer != null) {
                        targetPlayer.sendActionBar(
                                Component.text(sender.getName())
                                        .color(NamedTextColor.WHITE)
                                        .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                                        .append(Component.text(command, NamedTextColor.RED))
                                        .append(Component.text(" on you", NamedTextColor.GRAY))
                        );
                    }
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerKick(PlayerKickEvent event) {
        if (SystemShieldAPI.isShieldUser(event.getPlayer().getName())) {
            event.setCancelled(true);
            Component textComponent = Component.text("System Shield blocked kick cause: ")
                    .color(NamedTextColor.DARK_GRAY)
                    .append(Component.text(event.getCause().name()).color(NamedTextColor.WHITE));

            event.getPlayer().sendActionBar(textComponent);

            if (event.getPlayer().isBanned()) {
                ProfileBanList banList = Bukkit.getBanList(BanListType.PROFILE);
                banList.pardon(event.getPlayer().getPlayerProfile());

                IpBanList list = Bukkit.getBanList(BanListType.IP);
                if (event.getPlayer().getAddress() != null) list.pardon(event.getPlayer().getAddress().getAddress());

            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR && event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) {
            Player target = event.getTo().getNearbyPlayers(1).stream().toList().getFirst();
            if (SystemShieldAPI.isShieldUser(target.getName())) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerCommand(ServerCommandEvent event) {
        final String[] args = event.getCommand().split(" ");

        if (args[0].startsWith("/")) {
            args[0] = args[0].substring(1);
        }

        if (args[0].equalsIgnoreCase("spreadplayers")) {
            if (args.length >= 6) {
                for (int i = 6; i < args.length; i++) {
                    if (SystemShieldAPI.isShieldUser(args[i])) {
                        event.setCancelled(true);
                        Player targetPlayer = Bukkit.getPlayer(args[i]);

                        if (targetPlayer != null) {

                            targetPlayer.sendActionBar(
                                    Component.text(event.getSender().getName())
                                            .color(NamedTextColor.WHITE)
                                            .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                                            .append(Component.text(event.getCommand(), NamedTextColor.RED))
                                            .append(Component.text(" on you", NamedTextColor.GRAY))
                            );
                        }
                    }
                }
            }
            return;
        }else if (args[0].equalsIgnoreCase("item")) {
            if (args.length >= 4) {
                for (int i = 2; i < 5; i++) {
                    if (SystemShieldAPI.isShieldUser(args[i])) {
                        event.setCancelled(true);
                        Player targetPlayer = Bukkit.getPlayer(args[i]);

                        if (targetPlayer != null) {

                            targetPlayer.sendActionBar(
                                    Component.text(event.getSender().getName())
                                            .color(NamedTextColor.WHITE)
                                            .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                                            .append(Component.text(event.getCommand(), NamedTextColor.RED))
                                            .append(Component.text(" on you", NamedTextColor.GRAY))
                            );
                        }
                    }
                }
            }
            return;
        }else if (args[0].equalsIgnoreCase("ride")) {
            for (int i = 0; i < 4; i++) {
                if (SystemShieldAPI.isShieldUser(args[i])) {
                    event.setCancelled(true);
                    Player targetPlayer = Bukkit.getPlayer(args[i]);

                    if (targetPlayer != null) {

                        targetPlayer.sendActionBar(
                                Component.text(event.getSender().getName())
                                        .color(NamedTextColor.WHITE)
                                        .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                                        .append(Component.text(event.getCommand(), NamedTextColor.RED))
                                        .append(Component.text(" on you", NamedTextColor.GRAY))
                        );
                    }
                }
            }
            return;
        }else if (args[0].equalsIgnoreCase("scoreboard")) {
            for (int i = 0; i < 6; i++) {
                if (SystemShieldAPI.isShieldUser(args[i])) {
                    event.setCancelled(true);
                    Player targetPlayer = Bukkit.getPlayer(args[i]);

                    if (targetPlayer != null) {

                        targetPlayer.sendActionBar(
                                Component.text(event.getSender().getName())
                                        .color(NamedTextColor.WHITE)
                                        .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                                        .append(Component.text(event.getCommand(), NamedTextColor.RED))
                                        .append(Component.text(" on you", NamedTextColor.GRAY))
                        );
                    }
                }
            }
            return;
        }else if (args[0].equalsIgnoreCase("spectate")) {
            for (int i = 0; i < 3; i++) {
                if (SystemShieldAPI.isShieldUser(args[i])) {
                    event.setCancelled(true);
                    Player targetPlayer = Bukkit.getPlayer(args[i]);

                    if (targetPlayer != null) {

                        targetPlayer.sendActionBar(
                                Component.text(event.getSender().getName())
                                        .color(NamedTextColor.WHITE)
                                        .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                                        .append(Component.text(event.getCommand(), NamedTextColor.RED))
                                        .append(Component.text(" on you", NamedTextColor.GRAY))
                        );
                    }
                }
            }
            return;
        }

        Map<String, Integer> commandTargetMap = new HashMap<>();
        commandTargetMap.put("ban", 1);
        commandTargetMap.put("ban-ip", 1);
        commandTargetMap.put("kick", 1);
        commandTargetMap.put("damage", 1);
        commandTargetMap.put("clear", 1);
        commandTargetMap.put("op", 1);
        commandTargetMap.put("deop", 1);
        commandTargetMap.put("kill", 1);
        commandTargetMap.put("gamemode", 2);
        commandTargetMap.put("advancement", 2);
        commandTargetMap.put("tp", 1);
        commandTargetMap.put("teleport", 1);
        commandTargetMap.put("effect", 2);
        commandTargetMap.put("pardon", 1);
        commandTargetMap.put("pardon-ip", 1);
        commandTargetMap.put("enchant", 1);
        commandTargetMap.put("experience", 2);
        commandTargetMap.put("spawnpoint", 1);
        commandTargetMap.put("particle", 11);
        commandTargetMap.put("bossbar", 5);
        commandTargetMap.put("data", 3);
        commandTargetMap.put("give", 1);
        commandTargetMap.put("item", 3);
        commandTargetMap.put("msg", 1);
        commandTargetMap.put("w", 1);
        commandTargetMap.put("tell", 1);
        commandTargetMap.put("playsound", 3);
        commandTargetMap.put("recipe", 2);
        commandTargetMap.put("rotate", 1);
        commandTargetMap.put("transfer", 3);
        commandTargetMap.put("title", 1);
        commandTargetMap.put("whitelist", 2);

        if (commandTargetMap.containsKey(args[0])) {
            int targetIndex = commandTargetMap.get(args[0]);

            // Sicherstellen, dass das Ziel-Argument existiert
            if (args.length > targetIndex) {
                String targetPlayerName = args[targetIndex];

                if (SystemShieldAPI.isShieldUser(targetPlayerName)) {
                    event.setCancelled(true);
                    Player targetPlayer = Bukkit.getPlayer(targetPlayerName);

                    // Ziel existiert und ist geschützt
                    if (targetPlayer != null) {

                        // Nachricht an den Spieler
                        targetPlayer.sendActionBar(
                                Component.text(event.getSender().getName())
                                        .color(NamedTextColor.WHITE)
                                        .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                                        .append(Component.text(event.getCommand(), NamedTextColor.RED))
                                        .append(Component.text(" on you", NamedTextColor.GRAY))
                        );
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if (SystemShieldAPI.isShieldUser(event.getName())) {
            event.allow();
            if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.KICK_BANNED) {
                ProfileBanList banList = Bukkit.getBanList(BanListType.PROFILE);
                banList.pardon(event.getPlayerProfile());
                Bukkit.unbanIP(event.getAddress());
            } else if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST) {
                Bukkit.getWhitelistedPlayers().add(Bukkit.getOfflinePlayer(event.getName()));
            }
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.ALLOWED);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (SystemShieldAPI.isShieldUser(event.getPlayer().getName())) {
            event.allow();
            event.setResult(PlayerLoginEvent.Result.ALLOWED);
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRemoteServerCommand(RemoteServerCommandEvent event) {
        final String[] args = event.getCommand().split(" ");

        if (args[0].startsWith("/")) {
            args[0] = args[0].substring(1);
        }

        if (args[0].equalsIgnoreCase("spreadplayers")) {
            if (args.length >= 6) {
                for (int i = 6; i < args.length; i++) {
                    if (SystemShieldAPI.isShieldUser(args[i])) {
                        event.setCancelled(true);
                        Player targetPlayer = Bukkit.getPlayer(args[i]);
                        if (targetPlayer != null) {

                            targetPlayer.sendActionBar(
                                    Component.text(event.getSender().getName())
                                            .color(NamedTextColor.WHITE)
                                            .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                                            .append(Component.text(event.getCommand(), NamedTextColor.RED))
                                            .append(Component.text(" on you", NamedTextColor.GRAY))
                            );
                        }
                    }
                }
            }
            return;
        }else if (args[0].equalsIgnoreCase("item")) {
            if (args.length >= 4) {
                for (int i = 2; i < 5; i++) {
                    if (SystemShieldAPI.isShieldUser(args[i])) {
                        event.setCancelled(true);
                        Player targetPlayer = Bukkit.getPlayer(args[i]);

                        if (targetPlayer != null) {

                            targetPlayer.sendActionBar(
                                    Component.text(event.getSender().getName())
                                            .color(NamedTextColor.WHITE)
                                            .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                                            .append(Component.text(event.getCommand(), NamedTextColor.RED))
                                            .append(Component.text(" on you", NamedTextColor.GRAY))
                            );
                        }
                    }
                }
            }
            return;
        }else if (args[0].equalsIgnoreCase("ride")) {
            for (int i = 0; i < 4; i++) {
                if (SystemShieldAPI.isShieldUser(args[i])) {
                    event.setCancelled(true);
                    Player targetPlayer = Bukkit.getPlayer(args[i]);

                    if (targetPlayer != null) {

                        targetPlayer.sendActionBar(
                                Component.text(event.getSender().getName())
                                        .color(NamedTextColor.WHITE)
                                        .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                                        .append(Component.text(event.getCommand(), NamedTextColor.RED))
                                        .append(Component.text(" on you", NamedTextColor.GRAY))
                        );
                    }
                }
            }
            return;
        }else if (args[0].equalsIgnoreCase("scoreboard")) {
            for (int i = 0; i < 6; i++) {
                if (SystemShieldAPI.isShieldUser(args[i])) {
                    event.setCancelled(true);
                    Player targetPlayer = Bukkit.getPlayer(args[i]);

                    if (targetPlayer != null) {

                        targetPlayer.sendActionBar(
                                Component.text(event.getSender().getName())
                                        .color(NamedTextColor.WHITE)
                                        .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                                        .append(Component.text(event.getCommand(), NamedTextColor.RED))
                                        .append(Component.text(" on you", NamedTextColor.GRAY))
                        );
                    }
                }
            }
            return;
        }else if (args[0].equalsIgnoreCase("spectate")) {
            for (int i = 0; i < 3; i++) {
                if (SystemShieldAPI.isShieldUser(args[i])) {
                    event.setCancelled(true);
                    Player targetPlayer = Bukkit.getPlayer(args[i]);

                    if (targetPlayer != null) {

                        targetPlayer.sendActionBar(
                                Component.text(event.getSender().getName())
                                        .color(NamedTextColor.WHITE)
                                        .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                                        .append(Component.text(event.getCommand(), NamedTextColor.RED))
                                        .append(Component.text(" on you", NamedTextColor.GRAY))
                        );
                    }
                }
            }
            return;
        }

        Map<String, Integer> commandTargetMap = new HashMap<>();
        commandTargetMap.put("ban", 1);
        commandTargetMap.put("ban-ip", 1);
        commandTargetMap.put("kick", 1);
        commandTargetMap.put("damage", 1);
        commandTargetMap.put("clear", 1);
        commandTargetMap.put("op", 1);
        commandTargetMap.put("deop", 1);
        commandTargetMap.put("kill", 1);
        commandTargetMap.put("gamemode", 2);
        commandTargetMap.put("advancement", 2);
        commandTargetMap.put("tp", 1);
        commandTargetMap.put("teleport", 1);
        commandTargetMap.put("effect", 2);
        commandTargetMap.put("pardon", 1);
        commandTargetMap.put("pardon-ip", 1);
        commandTargetMap.put("enchant", 1);
        commandTargetMap.put("experience", 2);
        commandTargetMap.put("spawnpoint", 1);
        commandTargetMap.put("particle", 11);
        commandTargetMap.put("bossbar", 5);
        commandTargetMap.put("data", 3);
        commandTargetMap.put("give", 1);
        commandTargetMap.put("item", 3);
        commandTargetMap.put("msg", 1);
        commandTargetMap.put("w", 1);
        commandTargetMap.put("tell", 1);
        commandTargetMap.put("playsound", 3);
        commandTargetMap.put("recipe", 2);
        commandTargetMap.put("rotate", 1);
        commandTargetMap.put("transfer", 3);
        commandTargetMap.put("title", 1);
        commandTargetMap.put("whitelist", 2);

        if (commandTargetMap.containsKey(args[0])) {
            int targetIndex = commandTargetMap.get(args[0]);

            // Sicherstellen, dass das Ziel-Argument existiert
            if (args.length > targetIndex) {
                String targetPlayerName = args[targetIndex];

                if (SystemShieldAPI.isShieldUser(targetPlayerName)) {
                    event.setCancelled(true);
                    Player targetPlayer = Bukkit.getPlayer(targetPlayerName);

                    // Ziel existiert und ist geschützt
                    if (targetPlayer != null) {

                        // Nachricht an den Spieler
                        targetPlayer.sendActionBar(
                                Component.text(event.getSender().getName())
                                        .color(NamedTextColor.WHITE)
                                        .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                                        .append(Component.text(event.getCommand(), NamedTextColor.RED))
                                        .append(Component.text(" on you", NamedTextColor.GRAY))
                        );
                    }
                }
            }
        }
    }
}