package de.nikey.nikeysystem.Player.Functions;

import de.nikey.nikeysystem.Player.API.HideAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.NikeySystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;

import static org.bukkit.GameRule.SEND_COMMAND_FEEDBACK;

@SuppressWarnings("ALL")
public class HideFunctions implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST , ignoreCancelled = true)
    public void onPlayerShowEntity(PlayerShowEntityEvent event) {
        if (event.getEntity() instanceof Player ) {
            if (HideAPI.getHiddenPlayerNames().contains(event.getEntity().getName())) {
                if (!PermissionAPI.isAdmin(event.getPlayer().getName()) && !PermissionAPI.isOwner(event.getPlayer().getName())) {
                    event.getPlayer().hidePlayer(NikeySystem.getPlugin(), (Player) event.getEntity());
                }
            }else if (HideAPI.getTrueHiddenNames().contains(event.getEntity().getName())) {
                if (!PermissionAPI.isOwner(event.getPlayer().getName())) {
                    event.getPlayer().hidePlayer(NikeySystem.getPlugin(), (Player) event.getEntity());
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true , priority = EventPriority.HIGHEST)
    public void onPlayerHideEntity(PlayerHideEntityEvent event) {
        Entity hidden = event.getEntity();
        Player player = event.getPlayer();

        if (HideAPI.getTrueHideImmunity().contains(player.getName()) || HideAPI.getHideImmunity().contains(player.getName())) {
            player.showEntity(NikeySystem.getPlugin(),hidden);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player joiningPlayer = event.getPlayer();
        for (String s: HideAPI.getHiddenPlayerNames()) {
            Player player = Bukkit.getPlayer(s);
            if (player != null && player != joiningPlayer){
                if (!PermissionAPI.isAdmin(joiningPlayer.getName()) && !PermissionAPI.isOwner(joiningPlayer.getName())) {
                    joiningPlayer.hidePlayer(NikeySystem.getPlugin(), player);
                    player.sendMessage(ChatColor.DARK_GRAY + "You are hidden from " + joiningPlayer.getName());
                }
            }
        }
        for (String s: HideAPI.getTrueHiddenNames()) {
            Player player = Bukkit.getPlayer(s);
            if (player != null && player != joiningPlayer){
                if (!PermissionAPI.isOwner(joiningPlayer.getName())) {
                    joiningPlayer.hidePlayer(NikeySystem.getPlugin(), player);
                    player.sendMessage(ChatColor.DARK_GRAY + "You are hidden from " + joiningPlayer.getName());
                }
            }
        }
        if (HideAPI.getHiddenPlayerNames().contains(joiningPlayer.getName())) {
            event.setJoinMessage("");
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!PermissionAPI.isAdmin(onlinePlayer.getName()) && !PermissionAPI.isOwner(onlinePlayer.getName())) {
                    onlinePlayer.hidePlayer(NikeySystem.getPlugin(),joiningPlayer);
                }else if (PermissionAPI.isOwner(onlinePlayer.getName()) || PermissionAPI.isAdmin(onlinePlayer.getName())){
                    onlinePlayer.sendMessage("§e" +joiningPlayer.getName() + " joined the game");
                }
            }
        }

        if (HideAPI.getTrueHiddenNames().contains(joiningPlayer.getName())) {
            event.setJoinMessage("");
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!PermissionAPI.isOwner(onlinePlayer.getName())) {
                    onlinePlayer.hidePlayer(NikeySystem.getPlugin(),joiningPlayer);
                }else if (PermissionAPI.isOwner(onlinePlayer.getName())) {
                    onlinePlayer.sendMessage("§e" +joiningPlayer.getName() + " joined the game");
                }
            }
        }

        if (HideAPI.getHiddenPlayerNames().contains(joiningPlayer.getName()) || HideAPI.getTrueHiddenNames().contains(joiningPlayer.getName())) {
            joiningPlayer.sendMessage("§8You are hidden");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST )
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (HideAPI.getHiddenPlayerNames().contains(event.getPlayer().getName())) {
            event.setQuitMessage("");
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (PermissionAPI.isOwner(player.getName()) || PermissionAPI.isAdmin(player.getName())) {
                    player.sendMessage("§e" +event.getPlayer().getName() + " left the game");
                }
            }
        } else if (HideAPI.getTrueHiddenNames().contains(event.getPlayer().getName())) {
            event.setQuitMessage("");
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (PermissionAPI.isOwner(player.getName())) {
                    player.sendMessage("§e" +event.getPlayer().getName() + " left the game");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST , ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        String senderName = sender.getName();
        String message = event.getMessage();

        if (HideAPI.getHiddenPlayerNames().contains(senderName) ) {
            event.setMessage("\u200E ");
            event.setCancelled(true);
            sender.sendMessage("<" + sender.getName() + "> "+message);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (PermissionAPI.isOwner(player.getName()) || PermissionAPI.isAdmin(player.getName()) && player != sender) {
                    player.sendMessage("<" + sender.getName() + "> "+message);
                }
            }
        }else if (HideAPI.getTrueHiddenNames().contains(senderName)) {
            event.setMessage("\u200E ");
            event.setCancelled(true);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (PermissionAPI.isOwner(player.getName()) && player != sender) {
                    player.sendMessage("<" + sender.getName() + "> "+message);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (HideAPI.getHiddenPlayerNames().contains(player.getName()) || HideAPI.getTrueHiddenNames().contains(player.getName())) {
            if (player.getWorld().getGameRuleValue(SEND_COMMAND_FEEDBACK)) {
                player.getWorld().setGameRule(SEND_COMMAND_FEEDBACK,false);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.getWorld().setGameRule(SEND_COMMAND_FEEDBACK,true);
                    }
                }.runTaskLater(NikeySystem.getPlugin(),1);
            }
        }

        final String[] args = event.getMessage().split(" ");
        if (args[0].toLowerCase().equalsIgnoreCase("/msg") || args[0].toLowerCase().equalsIgnoreCase("/tell") || args[0].toLowerCase().equalsIgnoreCase("/w")) {
            if (args.length >= 2) {

                if (args[1].startsWith("@")) {
                    player.sendMessage(ChatColor.RED + "Error: feature disabled");
                    event.setCancelled(true);
                }

                Player recipient = Bukkit.getPlayerExact(args[1]);
                if (recipient != null && !HideAPI.canSee(player,recipient)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "No player was found");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST )
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        // Überprüfen, ob der Spieler ein versteckter Spieler ist
        if (HideAPI.getHiddenPlayerNames().contains(playerName) || HideAPI.getTrueHiddenNames().contains(playerName)) {
            player.getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS,false);
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS,true);
                }
            }.runTaskLater(NikeySystem.getPlugin(),2);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        String deathMessage = event.getDeathMessage();
        if (HideAPI.getHiddenPlayerNames().contains(player.getName()) ) {
            event.setDeathMessage("");
            for (Player players : Bukkit.getOnlinePlayers()) {
                if (PermissionAPI.isOwner(players.getName()) || PermissionAPI.isAdmin(players.getName())) {
                    players.sendMessage(deathMessage);
                }
            }
        }else if (HideAPI.getTrueHiddenNames().contains(player.getName())) {
            event.setDeathMessage("");
            for (Player players : Bukkit.getOnlinePlayers()) {
                if (PermissionAPI.isOwner(players.getName())) {
                    players.sendMessage(deathMessage);
                }
            }
        }
    }

    @EventHandler
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
        LivingEntity target = event.getTarget();
        Entity entity = event.getEntity();
        if (target == null)return;
        if (HideAPI.getTrueHiddenNames().contains(target.getName()) || HideAPI.getHiddenPlayerNames().contains(target.getName())){
            event.setCancelled(true);
        }
    }

}
