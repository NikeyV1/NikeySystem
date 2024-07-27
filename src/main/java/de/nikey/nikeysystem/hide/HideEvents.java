package de.nikey.nikeysystem.Listener;

import de.nikey.nikeysystem.NikeySystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings("ALL")
public class HideEvents implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player joiningPlayer = event.getPlayer();
        for (String s:CommandRegister.hiddenPlayerNames) {
            Player player = Bukkit.getPlayer(s);
            if (player != null){
                if (!joiningPlayer.getName().equalsIgnoreCase("NikeyV1")) {
                    joiningPlayer.hidePlayer(NikeySystem.getPlugin(), player);
                    player.sendMessage(ChatColor.DARK_GRAY + "You are hidden from " + joiningPlayer.getName());
                }
            }
        }
        if (CommandRegister.hiddenPlayerNames.contains(event.getPlayer().getName())) {
            event.setJoinMessage("");
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                for (String s:CommandRegister.hiddenPlayerNames) {
                    Player player = Bukkit.getPlayer(s);
                    if (player == null) return;
                    if (!onlinePlayer.getName().equalsIgnoreCase("NikeyV1")) {
                        onlinePlayer.hidePlayer(NikeySystem.getPlugin(),player);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (CommandRegister.hiddenPlayerNames.contains(event.getPlayer().getName())) {
            event.setQuitMessage("");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        String senderName = sender.getName();

        // Überprüfen, ob der Sender ein versteckter Spieler ist
        if (CommandRegister.hiddenPlayerNames.contains(senderName)) {
            event.setMessage("S");
            event.setCancelled(true);  // Nachricht senden verhindern
            sender.sendMessage(ChatColor.DARK_AQUA+"You are hidden you can't type messanges");
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (CommandRegister.hiddenPlayerNames.contains(player.getName())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.DARK_AQUA+"You are hidden you can't send commands");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        // Überprüfen, ob der Spieler ein versteckter Spieler ist
        if (CommandRegister.hiddenPlayerNames.contains(playerName)) {
            player.getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS,false);
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS,true);
                }
            }.runTaskLater(NikeySystem.getPlugin(),40);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (CommandRegister.hiddenPlayerNames.contains(player.getName())) {
            event.setDeathMessage("");
        }
    }
}
