package de.nikey.nikeysystem.Security.Functions;

import de.nikey.nikeysystem.Security.API.SystemShieldAPI;
import de.nikey.nikeysystem.Security.Distributor.SystemShieldDistributor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        final String[] args = event.getMessage().split(" ");

        if (args[0].equalsIgnoreCase("/ban") || args[0].equalsIgnoreCase("/ban-ip") || args[0].equalsIgnoreCase("/kick")
                || args[0].equalsIgnoreCase("/damage") || args[0].equalsIgnoreCase("/clear") || args[0].equalsIgnoreCase("/deop") || args[0].equalsIgnoreCase("/kill"))  {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null)return;
            if (SystemShieldAPI.isShieldUser(target.getName())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§cError: no permissions");
            }
        }else if (args[0].equalsIgnoreCase("/gamemode")) {
            Player target = Bukkit.getPlayer(args[2]);
            if (target == null)return;
            if (SystemShieldAPI.isShieldUser(target.getName())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage("§cError: no permissions");
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
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
    public void onServerCommand(ServerCommandEvent event) {
        final String[] args = event.getCommand().split(" ");

        if (args[0].startsWith("/")) {
            args[0] = args[0].substring(1);
        }


        if (args[0].equalsIgnoreCase("ban") || args[0].equalsIgnoreCase("ban-ip") || args[0].equalsIgnoreCase("kick")
                || args[0].equalsIgnoreCase("damage") || args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("deop") || args[0].equalsIgnoreCase("kill"))  {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null)return;
            if (SystemShieldAPI.isShieldUser(target.getName())) {
                event.setCancelled(true);
            }
        }else if (args[0].equalsIgnoreCase("gamemode")) {
            Player target = Bukkit.getPlayer(args[2]);
            if (target == null)return;
            if (SystemShieldAPI.isShieldUser(target.getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onRemoteServerCommand(RemoteServerCommandEvent event) {
        final String[] args = event.getCommand().split(" ");

        CommandSender sender = event.getSender();
        if (args[0].startsWith("/")) {
            args[0] = args[0].substring(1);
        }


        if (args[0].equalsIgnoreCase("ban") || args[0].equalsIgnoreCase("ban-ip") || args[0].equalsIgnoreCase("kick")
                || args[0].equalsIgnoreCase("damage") || args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("deop") || args[0].equalsIgnoreCase("kill"))  {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null)return;
            if (SystemShieldAPI.isShieldUser(target.getName())) {
                event.setCancelled(true);
                sender.sendMessage("§cError: no permissions");
            }
        }else if (args[0].equalsIgnoreCase("gamemode")) {
            Player target = Bukkit.getPlayer(args[2]);
            if (target == null)return;
            if (SystemShieldAPI.isShieldUser(target.getName())) {
                event.setCancelled(true);
                sender.sendMessage("§cError: no permissions");
            }
        }
    }


}
