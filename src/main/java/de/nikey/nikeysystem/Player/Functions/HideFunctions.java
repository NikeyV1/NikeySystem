package de.nikey.nikeysystem.Player.Functions;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import de.nikey.nikeysystem.General.GeneralAPI;
import de.nikey.nikeysystem.Player.API.HideAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.PlayerSettingsAPI;
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
            for (Player player : GeneralAPI.getOnlinePlayers(event.getPlayer())) {
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().toLowerCase();
        Player sender = (Player) event.getPlayer();
        final String[] args = command.split(" ");

        // Detect if it's a command to teleport or give items to a hidden player
        for (String hiddenPlayer : HideAPI.getHiddenPlayerNames()) {
            if (command.contains(hiddenPlayer)) {
                sender.sendMessage("§cUnknown or incomplete command, see below for error\n" +
                        command+"<--[HERE]");
                event.setCancelled(true);
            }
        }
        for (String hiddenPlayer : HideAPI.getHiddenPlayerNames()) {
            if (command.contains(hiddenPlayer)) {
                // Täusche eine Vanilla-Meldung vor
                if (command.startsWith("/tp") || command.startsWith("/teleport") || command.startsWith("/spectate")) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer) ||args[2].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                    }
                } else if (command.startsWith("/give")) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                    }
                }else if (command.startsWith("/effect")) {
                    if (args[1].equalsIgnoreCase("give") || args[1].equalsIgnoreCase("clear")) {
                        if (args[2].equalsIgnoreCase(hiddenPlayer)) {
                            sender.sendMessage("§cNo player was found");
                        }
                    }
                } else if ((command.startsWith("/ban") || command.startsWith("/pardon")) && args.length >= 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }

                // Kick-Befehl (prüfe, ob der Spieler als Ziel genannt wird)
                else if (command.startsWith("/kick") && args.length >= 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }

                // OP- und Deop-Befehl (prüfe, ob der Spieler als Ziel genannt wird)
                else if ((command.startsWith("/op") || command.startsWith("/deop")) && args.length == 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }

                // Nachricht- und Tell-Befehl (prüfe, ob der Spieler als Ziel genannt wird)
                else if ((command.startsWith("/msg") || command.startsWith("/tell")) && args.length >= 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }

                // Whitelist-Befehl (prüfe, ob der Spieler als Ziel genannt wird)
                else if (command.startsWith("/whitelist")) {
                    if (args.length >= 2 && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove"))) {
                        if (args[2].equalsIgnoreCase(hiddenPlayer)) {
                            sender.sendMessage("§cNo player was found");
                            event.setCancelled(true);
                        }
                    }
                }else if (command.startsWith("/advancement")) {
                    if (args.length >= 2 && (args[1].equalsIgnoreCase("grant") || args[1].equalsIgnoreCase("revoke"))) {
                        if (args[2].equalsIgnoreCase(hiddenPlayer)) {
                            sender.sendMessage("§cNo player was found");
                            event.setCancelled(true);
                        }
                    }
                }else if (command.equalsIgnoreCase("/xp") || command.equalsIgnoreCase("/experience")) {
                    if (args.length >= 2 && (args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("set")|| args[1].equalsIgnoreCase("query"))) {
                        if (args[2].equalsIgnoreCase(hiddenPlayer)) {
                            sender.sendMessage("§cNo player was found");
                            event.setCancelled(true);
                        }
                    }
                }

                // Team-Befehl (prüfe, ob der Spieler als Ziel genannt wird)
                else if (command.startsWith("/team")) {
                    if (args.length > 2 && args[1].equalsIgnoreCase("leave")) {
                        if (args[2].equalsIgnoreCase(hiddenPlayer)) {
                            sender.sendMessage("§cNo player was found");
                            event.setCancelled(true);
                        }
                    } else if (args.length >= 3 && args[1].equalsIgnoreCase("join")) {
                        if (args[3].equalsIgnoreCase(hiddenPlayer)) {
                            sender.sendMessage("§cNo player was found");
                            event.setCancelled(true);
                        }
                    }
                }

                // Scoreboard-Befehl (prüfe, ob der Spieler als Ziel genannt wird)
                else if (command.startsWith("/scoreboard") && args.length > 2) {
                    if (args[2].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }

                // Clear-Befehl (prüfe, ob der Spieler als Ziel genannt wird)
                else if (command.startsWith("/clear") && args.length >= 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (command.startsWith("/gamemode") && args.length >= 2) { // Spieler ist das 3. Argument
                    if (args[2].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                    }
                }else if (command.startsWith("/transfer") && args.length == 3) { // Spieler ist das 2. Argument
                    if (args[3].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                    }
                }else if (command.startsWith("/spawnpoint") && args.length >= 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (command.startsWith("/recipe") && args.length >= 2) {
                    if (args[2].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (command.startsWith("/playsound") && args.length >= 3) {
                    if (args[3].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (command.startsWith("/enchant") && args.length > 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (command.startsWith("/title") && args.length > 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (command.startsWith("/tellraw") && args.length > 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (command.startsWith("/tag") && args.length > 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (command.startsWith("/stopsound") && args.length > 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (command.startsWith("/ride")) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer) || args[3].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                    }
                }else if (command.startsWith("/loot") && args.length > 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                }else if (command.startsWith("/kill") && args.length > 1) {
                    if (args[1].equalsIgnoreCase(hiddenPlayer)) {
                        sender.sendMessage("§cNo player was found");
                        event.setCancelled(true);
                        return;
                    }
                } else {
                    sender.sendMessage("§cNo player was found");
                }
                event.setCancelled(true);
            }
        }

        for (String hiddenPlayer : HideAPI.getTrueHiddenNames()) {
            if (command.contains(hiddenPlayer)) {
                sender.sendMessage("§cUnknown or incomplete command, see below for error\n" +
                        command+"<--[HERE]");
                event.setCancelled(true);
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
                if (HideAPI.canSee(player,sender)) {
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
        if (event.getMessage().contains("@")) {
            player.sendMessage(ChatColor.RED + "Error: @ disabled");
            event.setCancelled(true);
        }
        if (args[0].toLowerCase().equalsIgnoreCase("/msg") || args[0].toLowerCase().equalsIgnoreCase("/tell") || args[0].toLowerCase().equalsIgnoreCase("/w")) {
            if (args.length >= 2) {

                Player recipient = Bukkit.getPlayer(args[1]);
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
