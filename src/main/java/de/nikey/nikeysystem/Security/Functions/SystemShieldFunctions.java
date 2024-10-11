package de.nikey.nikeysystem.Security.Functions;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import de.nikey.nikeysystem.Player.API.HideAPI;
import de.nikey.nikeysystem.Security.API.SystemShieldAPI;
import de.nikey.nikeysystem.Security.Distributor.SystemShieldDistributor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.TitlePart;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (event.getPlayer().isBanned() && SystemShieldAPI.isShieldUser(event.getPlayer().getName())) {
            OfflinePlayer target = event.getPlayer().getServer().getOfflinePlayer(String.valueOf(event.getPlayer()));
            BanList<?> banList = Bukkit.getBanList(BanList.Type.PROFILE);
            banList.pardon(target.getName());

            BanList<?> ban = Bukkit.getBanList(BanList.Type.IP);
            ban.pardon(target.getName());
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
                            event.getPlayer().sendMessage("§cError: no permissions");
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
                            event.getPlayer().sendMessage("§cError: no permissions");
                            TextComponent textComponent = Component.text(event.getPlayer().getName())
                                    .color(NamedTextColor.WHITE)
                                    .append(Component.text(" tried to use: ", NamedTextColor.GRAY))
                                    .append(Component.text(event.getMessage(), NamedTextColor.RED))
                                    .append(Component.text(" on you", NamedTextColor.GRAY));

                            target.sendTitlePart(TitlePart.SUBTITLE,textComponent);
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
                            event.getPlayer().sendMessage("§cError: no permissions");
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
                            event.getPlayer().sendMessage("§cError: no permissions");
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
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerKick(PlayerKickEvent event) {
        if (SystemShieldAPI.isShieldUser(event.getPlayer().getName())) {
            event.setCancelled(true);
            if (event.getPlayer().isBanned()) {
                BanList<?> banList = Bukkit.getBanList(BanList.Type.PROFILE);
                banList.pardon(event.getPlayer().getName());

                BanList<?> ban = Bukkit.getBanList(BanList.Type.IP);
                ban.pardon(event.getPlayer().getName());
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (SystemShieldAPI.isShieldUser(player.getName()) && event.getCause() == EntityDamageEvent.DamageCause.KILL) {
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


        if (args[0].equalsIgnoreCase("ban") || args[0].equalsIgnoreCase("ban-ip") || args[0].equalsIgnoreCase("kick")
                || args[0].equalsIgnoreCase("damage") || args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("deop") || args[0].equalsIgnoreCase("kill"))  {
            if (SystemShieldAPI.isShieldUser(args[1]) ) {
                Player player = Bukkit.getPlayer(args[1]);
                if (player != event.getSender()) {
                    event.setCancelled(true);
                }
            }
        }else if (args[0].equalsIgnoreCase("gamemode")) {
            if (SystemShieldAPI.isShieldUser(args[2])) {
                Player player = Bukkit.getPlayer(args[2]);
                if (player != event.getSender()) {
                    event.setCancelled(true);
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
                if (player != event.getSender()) {
                    event.setCancelled(true);
                }
            }
        }else if (args[0].equalsIgnoreCase("gamemode")) {
            if (SystemShieldAPI.isShieldUser(args[2])) {
                Player player = Bukkit.getPlayer(args[2]);
                if (player != event.getSender()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}