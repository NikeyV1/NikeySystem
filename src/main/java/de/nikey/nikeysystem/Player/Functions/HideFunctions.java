package de.nikey.nikeysystem.Player.Functions;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import de.nikey.nikeysystem.General.GeneralAPI;
import de.nikey.nikeysystem.Player.API.HideAPI;
import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.PlayerSettingsAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import static org.bukkit.GameRule.SEND_COMMAND_FEEDBACK;

@SuppressWarnings("ALL")
public class HideFunctions implements Listener {

    @EventHandler(priority = EventPriority.HIGH , ignoreCancelled = true)
    public void onPlayerShowEntity(PlayerShowEntityEvent event) {
        if (event.getEntity() instanceof Player ) {
            if (HideAPI.getHiddenPlayerNames().contains(event.getEntity().getName())) {
                if (!HideAPI.canSee(event.getPlayer(), (Player) event.getEntity())) {
                    event.getPlayer().hidePlayer(NikeySystem.getPlugin(), (Player) event.getEntity());
                }else {
                    Component textComponent = Component.text(event.getPlayer().getName())
                            .color(NamedTextColor.WHITE)
                            .append(Component.text(" can see you now!", NamedTextColor.GRAY));

                    event.getEntity().sendActionBar(textComponent);
                }
            }else if (HideAPI.getTrueHiddenNames().contains(event.getEntity().getName())) {
                if (!HideAPI.canSee(event.getPlayer(), (Player) event.getEntity())) {
                    event.getPlayer().hidePlayer(NikeySystem.getPlugin(), (Player) event.getEntity());
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true , priority = EventPriority.HIGH)
    public void onPlayerHideEntity(PlayerHideEntityEvent event) {
        Entity hidden = event.getEntity();
        Player player = event.getPlayer();
        if (!(hidden instanceof Player))return;

        if (HideAPI.canSee(player, (Player) hidden)) {
            player.showEntity(NikeySystem.getPlugin(),hidden);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player joiningPlayer = event.getPlayer();
        for (String s: HideAPI.getHiddenPlayerNames()) {
            Player player = Bukkit.getPlayer(s);
            if (player != null && player != joiningPlayer){
                if (!HideAPI.canSee(joiningPlayer,player)) {
                    joiningPlayer.hidePlayer(NikeySystem.getPlugin(), player);
                    Component textComponent = Component.text("You are hidden from ")
                            .color(NamedTextColor.DARK_GRAY)
                            .append(Component.text(joiningPlayer.getName(), NamedTextColor.WHITE));

                    player.sendActionBar(textComponent);
                }
            }
        }
        for (String s: HideAPI.getTrueHiddenNames()) {
            Player player = Bukkit.getPlayer(s);
            if (player != null && player != joiningPlayer){
                if (!HideAPI.canSee(joiningPlayer,player)) {
                    joiningPlayer.hidePlayer(NikeySystem.getPlugin(), player);
                    Component textComponent = Component.text("You are hidden from ")
                            .color(NamedTextColor.DARK_GRAY)
                            .append(Component.text(joiningPlayer.getName(), NamedTextColor.WHITE));

                    player.sendActionBar(textComponent);
                }
            }
        }
        if (HideAPI.getHiddenPlayerNames().contains(joiningPlayer.getName())) {
            event.setJoinMessage("");
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!HideAPI.canSee(onlinePlayer,joiningPlayer)) {
                    onlinePlayer.hidePlayer(NikeySystem.getPlugin(),joiningPlayer);
                }else {
                    onlinePlayer.sendMessage("§e" +joiningPlayer.getName() + " joined the game");
                }
            }
        }

        if (HideAPI.getTrueHiddenNames().contains(joiningPlayer.getName())) {
            event.setJoinMessage("");
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!HideAPI.canSee(onlinePlayer,joiningPlayer)) {
                    onlinePlayer.hidePlayer(NikeySystem.getPlugin(),joiningPlayer);
                }else {
                    onlinePlayer.sendMessage("§e" +joiningPlayer.getName() + " joined the game");
                }
            }
        }

        if (HideAPI.getHiddenPlayerNames().contains(joiningPlayer.getName()) || HideAPI.getTrueHiddenNames().contains(joiningPlayer.getName())) {
            joiningPlayer.sendMessage("§8You are hidden");
        }
    }

    @EventHandler(priority = EventPriority.HIGH )
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (HideAPI.getHiddenPlayerNames().contains(event.getPlayer().getName())) {
            event.setQuitMessage("");
            for (Player player : GeneralAPI.getOnlinePlayers(event.getPlayer())) {
                if (HideAPI.canSee(player,event.getPlayer())) {
                    player.sendMessage("§e" +event.getPlayer().getName() + " left the game");
                }
            }
        } else if (HideAPI.getTrueHiddenNames().contains(event.getPlayer().getName())) {
            event.setQuitMessage("");
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (HideAPI.canSee(player,event.getPlayer())) {
                    player.sendMessage("§e" +event.getPlayer().getName() + " left the game");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().toLowerCase();
        Player sender = (Player) event.getPlayer();
        final String[] args = event.getMessage().split(" ");

        for (String hiddenPlayer : HideAPI.getHiddenPlayerNames()) {
            if (hiddenPlayer == sender.getName())continue;
            if (command.contains(hiddenPlayer.toLowerCase())) {
                // Täusche eine Vanilla-Meldung vor
                if (args[0].equalsIgnoreCase("/tp") || args[0].equalsIgnoreCase("/teleport") || args[0].equalsIgnoreCase("/spectate")) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer) || args[2].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                } else if (args[0].equalsIgnoreCase("/give")) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (args[0].equalsIgnoreCase("/effect")) {
                    if (args[1].equalsIgnoreCase("give") || args[1].equalsIgnoreCase("clear")) {
                        if (args[2].equalsIgnoreCase(hiddenPlayer)) {
                            sender.sendMessage("§cNo player was found");
                            event.setCancelled(true);
                            return;
                        }
                    }
                } else if ((args[0].equalsIgnoreCase("/ban") || args[0].equalsIgnoreCase("/pardon")) && args.length >= 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }

                // Kick-Befehl (prüfe, ob der Spieler als Ziel genannt wird)
                else if (args[0].equalsIgnoreCase("/kick") && args.length >= 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }

                // OP- und Deop-Befehl (prüfe, ob der Spieler als Ziel genannt wird)
                else if ((args[0].equalsIgnoreCase("/op") || args[0].equalsIgnoreCase("/deop")) && args.length == 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }

                // Nachricht- und Tell-Befehl (prüfe, ob der Spieler als Ziel genannt wird)
                else if ((args[0].equalsIgnoreCase("/msg") || args[0].equalsIgnoreCase("/tell") || args[0].equalsIgnoreCase("/w")) && args.length >= 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }

                // Whitelist-Befehl (prüfe, ob der Spieler als Ziel genannt wird)
                else if (args[0].equalsIgnoreCase("/whitelist")) {
                    if (args.length >= 2 && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
                        if (args[2].equalsIgnoreCase(hiddenPlayer)) {
                            sender.sendMessage("§cNo player was found");
                            event.setCancelled(true);
                            return;
                        }
                    }
                }else if (args[0].equalsIgnoreCase("/advancement")) {
                    if (args.length >= 2 && (args[1].equalsIgnoreCase("grant") || args[1].equalsIgnoreCase("revoke"))) {
                        if (args[2].equalsIgnoreCase(hiddenPlayer)) {
                            sender.sendMessage("§cNo player was found");
                            event.setCancelled(true);
                            return;
                        }
                    }
                }else if (args[0].equalsIgnoreCase("/xp") || args[0].equalsIgnoreCase("/experience")) {
                    if (args.length >= 4 && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("set")|| args[1].equalsIgnoreCase("query"))) {
                        if (args[2].equalsIgnoreCase(hiddenPlayer)) {
                            sender.sendMessage("§cNo player was found");
                            event.setCancelled(true);
                            return;
                        }
                    }
                }

                // Team-Befehl (prüfe, ob der Spieler als Ziel genannt wird)
                else if (args[0].equalsIgnoreCase("/team")) {
                    if (args.length > 2 && args[1].equalsIgnoreCase("leave")) {
                        if (args[2].equalsIgnoreCase(hiddenPlayer)) {
                            sender.sendMessage("§cNo player was found");
                            event.setCancelled(true);
                            return;
                        }
                    } else if (args.length >= 3 && args[1].equalsIgnoreCase("join")) {
                        if (args[3].equalsIgnoreCase(hiddenPlayer)) {
                            sender.sendMessage("§cNo player was found");
                            event.setCancelled(true);
                            return;
                        }
                    }
                }

                // Scoreboard-Befehl (prüfe, ob der Spieler als Ziel genannt wird)
                else if (args[0].equalsIgnoreCase("/scoreboard") && args.length > 2) {
                    if (args[2].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }

                // Clear-Befehl (prüfe, ob der Spieler als Ziel genannt wird)
                else if (args[0].equalsIgnoreCase("/clear") && args.length >= 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (args[0].equalsIgnoreCase("/gamemode") && args.length >= 2) { // Spieler ist das 3. Argument
                    if (args[2].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (args[0].equalsIgnoreCase("/transfer") && args.length == 3) { // Spieler ist das 2. Argument
                    if (args[3].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (args[0].equalsIgnoreCase("/spawnpoint") && args.length >= 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (args[0].equalsIgnoreCase("/recipe") && args.length >= 2) {
                    if (args[2].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (args[0].equalsIgnoreCase("/playsound") && args.length >= 3) {
                    if (args[3].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (args[0].equalsIgnoreCase("/enchant") && args.length > 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (args[0].equalsIgnoreCase("/title") && args.length > 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (args[0].equalsIgnoreCase("/tellraw") && args.length > 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (args[0].equalsIgnoreCase("/tag") && args.length > 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (args[0].equalsIgnoreCase("/stopsound") && args.length > 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (args[0].equalsIgnoreCase("/ride")) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer) || args[3].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (args[0].equalsIgnoreCase("/loot") && args.length > 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (args[0].equalsIgnoreCase("/kill") && args.length > 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }

        for (String hiddenPlayer : HideAPI.getTrueHiddenNames()) {
            if (hiddenPlayer == sender.getName())continue;
            if (command.contains(hiddenPlayer)) {
                if (command.equalsIgnoreCase("/tp") || command.equalsIgnoreCase("/teleport") || command.equalsIgnoreCase("/spectate")) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer) ||args[2].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                } else if (command.equalsIgnoreCase("/give")) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (command.equalsIgnoreCase("/effect")) {
                    if (args[1].equalsIgnoreCase("give") || args[1].equalsIgnoreCase("clear")) {
                        if (args[2].equalsIgnoreCase(hiddenPlayer)) {
                            sender.sendMessage("§cNo player was found");
                            event.setCancelled(true);
                            return;
                        }
                    }
                } else if ((command.equalsIgnoreCase("/ban") || command.equalsIgnoreCase("/pardon")) && args.length >= 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }

                // Kick-Befehl (prüfe, ob der Spieler als Ziel genannt wird)
                else if (command.equalsIgnoreCase("/kick") && args.length >= 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }

                // OP- und Deop-Befehl (prüfe, ob der Spieler als Ziel genannt wird)
                else if ((command.equalsIgnoreCase("/op") || command.equalsIgnoreCase("/deop")) && args.length == 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }

                // Nachricht- und Tell-Befehl (prüfe, ob der Spieler als Ziel genannt wird)
                else if ((command.equalsIgnoreCase("/msg") || command.equalsIgnoreCase("/tell") || command.equalsIgnoreCase("/w")) && args.length >= 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }

                // Whitelist-Befehl (prüfe, ob der Spieler als Ziel genannt wird)
                else if (command.equalsIgnoreCase("/whitelist")) {
                    if (args.length >= 2 && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
                        if (args[2].equalsIgnoreCase(hiddenPlayer)) {
                            sender.sendMessage("§cNo player was found");
                            event.setCancelled(true);
                            return;
                        }
                    }
                }else if (command.equalsIgnoreCase("/advancement")) {
                    if (args.length >= 2 && (args[1].equalsIgnoreCase("grant") || args[1].equalsIgnoreCase("revoke"))) {
                        if (args[2].equalsIgnoreCase(hiddenPlayer)) {
                            sender.sendMessage("§cNo player was found");
                            event.setCancelled(true);
                            return;
                        }
                    }
                }else if (command.equalsIgnoreCase("/xp") || command.equalsIgnoreCase("/experience")) {
                    if (args.length >= 2 && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("set")|| args[1].equalsIgnoreCase("query"))) {
                        if (args[2].equalsIgnoreCase(hiddenPlayer)) {
                            sender.sendMessage("§cNo player was found");
                            event.setCancelled(true);
                            return;
                        }
                    }
                }

                // Team-Befehl (prüfe, ob der Spieler als Ziel genannt wird)
                else if (command.equalsIgnoreCase("/team")) {
                    if (args.length > 2 && args[1].equalsIgnoreCase("leave")) {
                        if (args[2].equalsIgnoreCase(hiddenPlayer)) {
                            sender.sendMessage("§cNo player was found");
                            event.setCancelled(true);
                            return;
                        }
                    } else if (args.length >= 3 && args[1].equalsIgnoreCase("join")) {
                        if (args[3].equalsIgnoreCase(hiddenPlayer)) {
                            sender.sendMessage("§cNo player was found");
                            event.setCancelled(true);
                            return;
                        }
                    }
                }

                // Scoreboard-Befehl (prüfe, ob der Spieler als Ziel genannt wird)
                else if (command.equalsIgnoreCase("/scoreboard") && args.length > 2) {
                    if (args[2].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }

                // Clear-Befehl (prüfe, ob der Spieler als Ziel genannt wird)
                else if (command.equalsIgnoreCase("/clear") && args.length >= 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (command.equalsIgnoreCase("/gamemode") && args.length >= 2) { // Spieler ist das 3. Argument
                    if (args[2].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (command.equalsIgnoreCase("/transfer") && args.length == 3) { // Spieler ist das 2. Argument
                    if (args[3].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (command.equalsIgnoreCase("/spawnpoint") && args.length >= 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (command.equalsIgnoreCase("/recipe") && args.length >= 2) {
                    if (args[2].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (command.equalsIgnoreCase("/playsound") && args.length >= 3) {
                    if (args[3].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (command.equalsIgnoreCase("/enchant") && args.length > 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (command.equalsIgnoreCase("/title") && args.length > 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (command.equalsIgnoreCase("/tellraw") && args.length > 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (command.equalsIgnoreCase("/tag") && args.length > 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (command.equalsIgnoreCase("/stopsound") && args.length > 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (command.equalsIgnoreCase("/ride")) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer) || args[3].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (command.equalsIgnoreCase("/loot") && args.length > 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (command.equalsIgnoreCase("/kill") && args.length > 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }
            }else continue;
        }
    }

    @EventHandler(priority = EventPriority.HIGH , ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        String senderName = sender.getName();
        String message = event.getMessage();

        if (HideAPI.getHiddenPlayerNames().contains(senderName) ) {
            event.setMessage("\u200E ");
            event.setCancelled(true);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (HideAPI.canSee(player,sender)) {
                    player.sendMessage("<" + sender.getName() + "> "+message);
                }
            }
        }else if (HideAPI.getTrueHiddenNames().contains(senderName)) {
            event.setMessage("\u200E ");
            event.setCancelled(true);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (HideAPI.canSee(player,sender)) {
                    player.sendMessage("<" + sender.getName() + "> "+message);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
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
        if (event.getMessage().contains("@a") || event.getMessage().contains("@e")) {
            player.sendMessage(ChatColor.RED + "Error: @ disabled");
            event.setCancelled(true);
            return;
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
                if (HideAPI.canSee(players,player)) {
                    players.sendMessage(deathMessage);
                }
            }
        }else if (HideAPI.getTrueHiddenNames().contains(player.getName())) {
            event.setDeathMessage("");
            for (Player players : Bukkit.getOnlinePlayers()) {
                if (HideAPI.canSee(players,player)) {
                    players.sendMessage(deathMessage);
                }
            }
        }
    }

    @EventHandler
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
        LivingEntity target = event.getTarget();
        if (target == null)return;
        if (HideAPI.getTrueHiddenNames().contains(target.getName()) || HideAPI.getHiddenPlayerNames().contains(target.getName())){
            if (!PlayerSettingsAPI.hasMobTargeting(target.getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerAttemptPickupItem(PlayerAttemptPickupItemEvent event) {
        LivingEntity target = event.getPlayer();
        if (target == null)return;
        if (HideAPI.getTrueHiddenNames().contains(target.getName()) || HideAPI.getHiddenPlayerNames().contains(target.getName())){
            if (!PlayerSettingsAPI.hasItemPickup(target.getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerCropTrample(PlayerInteractEvent event) {
        Player target = event.getPlayer();
        if(event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.FARMLAND){
            if (HideAPI.getTrueHiddenNames().contains(target.getName()) || HideAPI.getHiddenPlayerNames().contains(target.getName())){
                if (!PlayerSettingsAPI.hasCropTrample(target.getName())) {
                    event.setCancelled(true);
                }
            }
        }
    }


    @EventHandler
    public void onPaperServerListPing(PaperServerListPingEvent event) {
        event.getListedPlayers().clear();
        int hiddenCount = event.getNumPlayers();

        // Zähle Spieler in hiddenPlayerNames
        for (String playerName : HideAPI.getHiddenPlayerNames()) {
            Player player = Bukkit.getPlayer(playerName);
            if (player != null)hiddenCount-=1;
        }

        // Zähle Spieler in trueHiddenNames
        for (String playerName : HideAPI.getTrueHiddenNames()) {
            if (!HideAPI.getHiddenPlayerNames().contains(playerName)){
                Player player = Bukkit.getPlayer(playerName);
                if (player != null)hiddenCount-=1;
            }
        }

        event.setNumPlayers(hiddenCount);
    }
}
