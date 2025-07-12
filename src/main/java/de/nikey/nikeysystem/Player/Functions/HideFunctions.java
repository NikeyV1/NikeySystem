package de.nikey.nikeysystem.Player.Functions;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import de.nikey.nikeysystem.General.GeneralAPI;
import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.HideAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.Player.API.PlayerSettingsAPI;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
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
import org.bukkit.scoreboard.Team;

import java.util.UUID;

import static org.bukkit.GameRule.SEND_COMMAND_FEEDBACK;

@SuppressWarnings("ALL")
public class HideFunctions implements Listener {

    @EventHandler(priority = EventPriority.HIGH , ignoreCancelled = true)
    public void onPlayerShowPlayer(PlayerShowEntityEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (HideAPI.isHidden(player.getUniqueId())) {
                if (!HideAPI.canSee(event.getPlayer(), player) && event.getPlayer().canSee(player)) {
                    event.getPlayer().hidePlayer(NikeySystem.getPlugin(),player);
                }else {
                    Component textComponent = Component.text(event.getPlayer().getName())
                            .color(NamedTextColor.WHITE)
                            .append(Component.text(" can see you now!", NamedTextColor.GRAY));

                    event.getEntity().showTitle(Title.title(Component.empty(),textComponent));
                }
            }else if (HideAPI.isTrueHidden(player.getUniqueId()) && event.getPlayer().canSee(player)) {
                event.getPlayer().hidePlayer(NikeySystem.getPlugin(), (Player) event.getEntity());
            }
        }
    }

    @EventHandler(ignoreCancelled = true , priority = EventPriority.HIGH)
    public void onPlayerHideEntity(PlayerHideEntityEvent event) {
        Entity hidden = event.getEntity();
        Player player = event.getPlayer();
        if (!(hidden instanceof Player))return;

        if (HideAPI.canSee(player, (Player) hidden) && !player.canSee(hidden)) {
            player.showEntity(NikeySystem.getPlugin(), hidden);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player joiningPlayer = event.getPlayer();

        //Hiding hidden players from joining player
        for (UUID hiddenid: HideAPI.getHiddenPlayers()) {
            Player player = Bukkit.getPlayer(hiddenid);
            if (player != null && player != joiningPlayer){
                if (!HideAPI.canSee(joiningPlayer, player)) {
                    joiningPlayer.hidePlayer(NikeySystem.getPlugin(), player);
                    Component textComponent = Component.text("You are hidden from ")
                            .color(NamedTextColor.DARK_GRAY)
                            .append(Component.text(joiningPlayer.getName(), NamedTextColor.WHITE));

                    player.showTitle(Title.title(Component.empty(), textComponent));
                }
            }
        }

        for (UUID hiddenid: HideAPI.getTrueHidePlayers()) {
            Player player = Bukkit.getPlayer(hiddenid);
            if (player != null && player != joiningPlayer){
                if (!HideAPI.canSee(joiningPlayer,player)) {
                    joiningPlayer.hidePlayer(NikeySystem.getPlugin(), player);
                    Component textComponent = Component.text("You are hidden from ")
                            .color(NamedTextColor.DARK_GRAY)
                            .append(Component.text(joiningPlayer.getName(), NamedTextColor.WHITE));

                    player.showTitle(Title.title(Component.empty(), textComponent));
                }
            }
        }


        if (HideAPI.isHidden(joiningPlayer.getUniqueId())) {
            event.setJoinMessage("");
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!HideAPI.canSee(onlinePlayer, joiningPlayer)) {
                    onlinePlayer.hidePlayer(NikeySystem.getPlugin(),joiningPlayer);
                }else {
                    Team playerTeam = onlinePlayer.getScoreboard().getPlayerTeam(onlinePlayer);

                    if (playerTeam == null || playerTeam.prefix().equals(Component.empty())) {
                        onlinePlayer.sendMessage(Component.text(joiningPlayer.getName() + " joined the game").color(NamedTextColor.YELLOW));
                    }else {
                        onlinePlayer.sendMessage(playerTeam.prefix().append(Component.text(joiningPlayer.getName() + " joined the game").color(NamedTextColor.YELLOW)));
                    }
                }
            }
        }

        if (HideAPI.isTrueHidden(joiningPlayer.getUniqueId())) {
            event.setJoinMessage("");
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!HideAPI.canSee(onlinePlayer,joiningPlayer)) {
                    onlinePlayer.hidePlayer(NikeySystem.getPlugin(),joiningPlayer);
                }else {
                    Team playerTeam = onlinePlayer.getScoreboard().getPlayerTeam(onlinePlayer);

                    if (playerTeam == null || playerTeam.prefix().equals(Component.empty())) {
                        onlinePlayer.sendMessage(Component.text(joiningPlayer.getName() + " joined the game").color(NamedTextColor.YELLOW));
                    }else {

                        onlinePlayer.sendMessage(playerTeam.prefix().append(Component.text(joiningPlayer.getName() + " joined the game").color(NamedTextColor.YELLOW)));
                    }
                }
            }
        }

        if (HideAPI.isTrueHidden(joiningPlayer.getUniqueId())) {
            joiningPlayer.sendMessage(Component.text("You're now true hidden").color(NamedTextColor.DARK_GRAY));
        }else if (HideAPI.isHidden(joiningPlayer.getUniqueId())) {
            joiningPlayer.sendMessage(Component.text("You're now hidden").color(NamedTextColor.DARK_GRAY));
        }
    }

    @EventHandler(priority = EventPriority.HIGH )
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (HideAPI.isHidden(event.getPlayer().getUniqueId())) {
            event.setQuitMessage("");
            for (Player online : GeneralAPI.getOnlinePlayers(event.getPlayer())) {
                if (HideAPI.canSee(online, event.getPlayer())) {
                    Team playerTeam = event.getPlayer().getScoreboard().getPlayerTeam(event.getPlayer());

                    if (playerTeam == null || playerTeam.prefix().equals(Component.empty())) {
                        online.sendMessage(Component.text(event.getPlayer().getName() + " left the game").color(NamedTextColor.YELLOW));
                    }else {
                        online.sendMessage(playerTeam.prefix().append(Component.text(event.getPlayer().getName() + " left the game").color(NamedTextColor.YELLOW)));
                    }
                }
            }
        } else if (HideAPI.isTrueHidden(event.getPlayer().getUniqueId())) {
            event.setQuitMessage("");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().toLowerCase();
        Player sender = (Player) event.getPlayer();
        final String[] args = event.getMessage().split(" ");

        for (UUID hidden : HideAPI.getHiddenPlayers()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(hidden);
            String hiddenPlayer = player.getName();
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

        for (UUID hidden : HideAPI.getTrueHidePlayers()) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(hidden);
            String hiddenPlayer = offlinePlayer.getName();
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

    @EventHandler(priority = EventPriority.HIGH ,ignoreCancelled = true)
    public void onAsyncChat(AsyncChatEvent event) {
        Player sender = event.getPlayer();

        if (HideAPI.isHidden(sender.getUniqueId())) {
            event.setCancelled(true);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (HideAPI.canSee(player,sender)) {
                    player.sendMessage(Component.text("<" + sender.getName() + "> ").append(event.message()));
                }
            }
        }else if (HideAPI.isTrueHidden(sender.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (HideAPI.isHidden(player.getUniqueId()) || HideAPI.isTrueHidden(player.getUniqueId())) {
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
            if (PermissionAPI.isOwner(player.getUniqueId()))return;
            if (PermissionAPI.isSystemUser(player))player.sendMessage(ChatColor.RED + "Error: @ disabled");
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST )
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        if (HideAPI.isHidden(player.getUniqueId()) || HideAPI.isTrueHidden(player.getUniqueId())) {

            if (player.getWorld().getGameRuleValue(GameRule.ANNOUNCE_ADVANCEMENTS)) {
                player.getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS,false);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        player.getWorld().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS,true);
                    }
                }.runTaskLater(NikeySystem.getPlugin(),1);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Component deathMessage = event.deathMessage();
        if (HideAPI.isHidden(player.getUniqueId())) {
            event.deathMessage(Component.empty());
            for (Player players : Bukkit.getOnlinePlayers()) {
                if (HideAPI.canSee(players,player)) {
                    players.sendMessage(deathMessage);
                }
            }
        }else if (HideAPI.isTrueHidden(player.getUniqueId())) {
            event.deathMessage(Component.empty());
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
        if (HideAPI.isHidden(target.getUniqueId()) || HideAPI.isTrueHidden(target.getUniqueId())){
            if (!PlayerSettingsAPI.hasMobTargeting(target.getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerAttemptPickupItem(PlayerAttemptPickupItemEvent event) {
        LivingEntity target = event.getPlayer();
        if (target == null)return;
        if (HideAPI.isHidden(target.getUniqueId()) || HideAPI.isTrueHidden(target.getUniqueId())){
            if (!PlayerSettingsAPI.hasItemPickup(target.getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerCropTrample(PlayerInteractEvent event) {
        Player target = event.getPlayer();
        if(event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.FARMLAND){
            if (HideAPI.isHidden(target.getUniqueId()) || HideAPI.isTrueHidden(target.getUniqueId())){
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
        for (UUID playerID : HideAPI.getHiddenPlayers()) {
            Player player = Bukkit.getPlayer(playerID);
            if (player != null)hiddenCount-=1;
        }

        // Zähle Spieler in trueHiddenNames
        for (UUID playerID : HideAPI.getTrueHidePlayers()) {
            if (!HideAPI.isHidden(playerID)){
                Player player = Bukkit.getPlayer(playerID);
                if (player != null)hiddenCount-=1;
            }
        }

        event.setNumPlayers(hiddenCount);
    }
}
