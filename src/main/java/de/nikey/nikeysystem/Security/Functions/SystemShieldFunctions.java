package de.nikey.nikeysystem.Security.Functions;

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
import org.bukkit.ban.IpBanList;
import org.bukkit.ban.ProfileBanList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.server.RemoteServerCommandEvent;
import org.bukkit.event.server.ServerCommandEvent;

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
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
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
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerKick(PlayerKickEvent event) {
        if (SystemShieldAPI.isShieldUser(event.getPlayer().getName())) {
            event.setCancelled(true);
            Component textComponent = Component.text("System Shield blocked cause: ")
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


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerCommand(ServerCommandEvent event) {
        final String[] args = event.getCommand().split(" ");

        if (args[0].startsWith("/")) {
            args[0] = args[0].substring(1);
        }


        if (args[0].equalsIgnoreCase("ban") || args[0].equalsIgnoreCase("ban-ip") || args[0].equalsIgnoreCase("kick")
                || args[0].equalsIgnoreCase("damage") || args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("deop") || args[0].equalsIgnoreCase("kill"))  {
            if (SystemShieldAPI.isShieldUser(args[1]) ) {
                Player player = Bukkit.getPlayer(args[1]);
                if (player != null && player != event.getSender()) {
                    event.setCancelled(true);
                    Component textComponent = Component.text(event.getSender().getName())
                            .color(NamedTextColor.WHITE)
                            .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                            .append(Component.text(event.getCommand(), NamedTextColor.RED))
                            .append(Component.text(" on you", NamedTextColor.GRAY));

                    player.sendActionBar(textComponent);
                }
            }
        }else if (args[0].equalsIgnoreCase("gamemode")) {
            if (SystemShieldAPI.isShieldUser(args[2])) {
                Player player = Bukkit.getPlayer(args[2]);
                if (player != null && player != event.getSender()) {
                    event.setCancelled(true);
                    Component textComponent = Component.text(event.getSender().getName())
                            .color(NamedTextColor.WHITE)
                            .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                            .append(Component.text(event.getCommand(), NamedTextColor.RED))
                            .append(Component.text(" on you", NamedTextColor.GRAY));

                    player.sendActionBar(textComponent);
                }
            }
        }
    }



    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRemoteServerCommand(RemoteServerCommandEvent event) {
        final String[] args = event.getCommand().split(" ");

        if (args[0].startsWith("/")) {
            args[0] = args[0].substring(1);
        }


        if (args[0].equalsIgnoreCase("ban") || args[0].equalsIgnoreCase("ban-ip") || args[0].equalsIgnoreCase("kick")
                || args[0].equalsIgnoreCase("damage") || args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("deop") || args[0].equalsIgnoreCase("kill"))  {
            if (SystemShieldAPI.isShieldUser(args[1])) {
                Player player = Bukkit.getPlayer(args[1]);
                if (player!= null && player != event.getSender()) {
                    event.setCancelled(true);
                    Component textComponent = Component.text(event.getSender().getName())
                            .color(NamedTextColor.WHITE)
                            .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                            .append(Component.text(event.getCommand(), NamedTextColor.RED))
                            .append(Component.text(" on you", NamedTextColor.GRAY));

                    player.sendActionBar(textComponent);
                }
            }
        }else if (args[0].equalsIgnoreCase("gamemode")) {
            if (SystemShieldAPI.isShieldUser(args[2])) {
                Player player = Bukkit.getPlayer(args[2]);
                if (player!= null && player != event.getSender()) {
                    event.setCancelled(true);
                    Component textComponent = Component.text(event.getSender().getName())
                            .color(NamedTextColor.WHITE)
                            .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                            .append(Component.text(event.getCommand(), NamedTextColor.RED))
                            .append(Component.text(" on you", NamedTextColor.GRAY));

                    player.sendActionBar(textComponent);
                }
            }
        }
    }
}