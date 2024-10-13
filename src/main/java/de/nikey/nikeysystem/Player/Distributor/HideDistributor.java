package de.nikey.nikeysystem.Player.Distributor;

import de.nikey.nikeysystem.Player.API.HideAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.Functions.PlayerSettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HideDistributor {

    public static void loadAll() {
        loadHiddenPlayerNames();
        loadHideImmunityPlayers();
        loadTrueHiddenPlayers();
        loadTrueHideImmunityPlayers();
    }


    private static void loadHiddenPlayerNames() {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();
        HideAPI.getHiddenPlayerNames().clear();
        HideAPI.getHiddenPlayerNames().addAll(config.getStringList("hide.hiddenPlayers"));
    }

    // Speichern der versteckten Spielernamen in die Konfiguration
    public static void saveHiddenPlayerNames() {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();
        config.set("hide.hiddenPlayers", new ArrayList<>(HideAPI.getHiddenPlayerNames()));
        NikeySystem.getPlugin().saveConfig();
    }

    private static void loadTrueHiddenPlayers() {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();
        HideAPI.getTrueHiddenNames().clear();
        HideAPI.getTrueHiddenNames().addAll(config.getStringList("hide.trueHiddenPlayers"));
    }

    // Speichern der versteckten Spielernamen in die Konfiguration
    public static void saveTrueHiddenPlayers() {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();
        config.set("hide.trueHiddenPlayers", new ArrayList<>(HideAPI.getTrueHiddenNames()));
        NikeySystem.getPlugin().saveConfig();
    }


    //Hide immunity
    private static void loadHideImmunityPlayers() {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();
        HideAPI.getHideImmunity().clear();
        HideAPI.getHideImmunity().addAll(config.getStringList("hide.hideImmunity"));
    }

    // Speichern der versteckten Spielernamen in die Konfiguration
    public static void saveHideImmunityPlayers() {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();
        config.set("hide.hideImmunity", new ArrayList<>(HideAPI.getHideImmunity()));
        NikeySystem.getPlugin().saveConfig();
    }

    private static void loadTrueHideImmunityPlayers() {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();
        HideAPI.getTrueHideImmunity().clear();
        HideAPI.getTrueHideImmunity().addAll(config.getStringList("hide.trueHideImmunity"));
    }

    // Speichern der versteckten Spielernamen in die Konfiguration
    public static void saveTrueHideImmunityPlayers() {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();
        config.set("hide.trueHideImmunity", new ArrayList<>(HideAPI.getTrueHideImmunity()));
        NikeySystem.getPlugin().saveConfig();
    }

    public static void hideDistributor(Player player, String[] args) {
        if (args[3].equalsIgnoreCase("ToggleHide")) {
            if (args.length == 6) {
                toggleAlwaysHide(player,args[4], args[5].equalsIgnoreCase("message"));
            }else {
                toggleAlwaysHide(player,args[4],false);
            }
        } else if (args[3].equalsIgnoreCase("ToggleTrueHide") && PermissionAPI.isOwner(player.getName())) {
            if (args.length == 6) {
                toggleTrueAlwaysHide(player,args[4], args[5].equalsIgnoreCase("message"));
            }else {
                toggleTrueAlwaysHide(player,args[4], false);
            }
        } else if (args[3].equalsIgnoreCase("ToggleImmunity")) {

            if (HideAPI.getHideImmunity().contains(args[4])) {
                HideAPI.getHideImmunity().remove(args[4]);
                saveHideImmunityPlayers();
                player.sendMessage(ChatColor.GOLD + args[4] + " has now hide immunity §cremoved");
            } else {
                HideAPI.getHideImmunity().add(args[4]);
                saveHideImmunityPlayers();
                player.sendMessage(ChatColor.GOLD + args[4] + " has now hide immunity §2added");
            }
        } else if (args[3].equalsIgnoreCase("ToggleTrueImmunity") && PermissionAPI.isOwner(player.getName())) {
            if (HideAPI.getTrueHideImmunity().contains(args[4])) {
                HideAPI.getTrueHideImmunity().remove(args[4]);
                saveTrueHideImmunityPlayers();
                player.sendMessage(ChatColor.GOLD + args[4] + " has now true hide immunity §cremoved");
            } else {
                HideAPI.getTrueHideImmunity().add(args[4]);
                saveTrueHideImmunityPlayers();
                player.sendMessage(ChatColor.GOLD + args[4] + " has now true hide immunity §2added");
            }
        } else if (args[3].equalsIgnoreCase("Settings")) {
            PlayerSettings.openSettingsMenu(player);
        }
        if (args[3].equalsIgnoreCase("List")) {
            if (PermissionAPI.isOwner(player.getName())) {
                String playerName = args[4];
                List<String> messages = new ArrayList<>();


                if (HideAPI.getHideImmunity().contains(playerName)) {
                    messages.add("§bHide Immunity");
                }

                if (HideAPI.getTrueHideImmunity().contains(playerName)) {
                    messages.add("§3True Hide Immunity");
                }

                if (HideAPI.getHiddenPlayerNames().contains(playerName)) {
                    messages.add("§bHidden");
                }

                if (HideAPI.getTrueHiddenNames().contains(playerName)) {
                    messages.add("§3True Hidden");
                }

                String message = "§7" + playerName + " has ";
                if (messages.isEmpty()) {
                    message += "no special statuses.";
                } else {
                    message += String.join(", ", messages) + ".";
                }

                player.sendMessage(message);
            }else {
                String playerName = args[4];
                List<String> messages = new ArrayList<>();

                if (HideAPI.canSee(player.getName(),playerName)) {
                    if (HideAPI.getHideImmunity().contains(playerName)) {
                        messages.add("§bHide Immunity");
                    }

                    if (HideAPI.getHiddenPlayerNames().contains(playerName)) {
                        messages.add("§bHidden");
                    }
                }


                String message = "§7" + playerName + " has ";
                if (messages.isEmpty()) {
                    message += "no special statuses.";
                } else {
                    message += String.join(", ", messages) + ".";
                }

                player.sendMessage(message);
            }
        }else if (args[3].equalsIgnoreCase("help")) {
            if (PermissionAPI.isOwner(player.getName())) {
                player.sendMessage("§7The path 'System/Player/Hide' has following sub-paths: §fToggleHide <PlayerName>, ToggleTrueHide <PlayerName>, ToggleImmunity <PlayerName>, ToggleTrueImmunity <PlayerName>, List <PlayerName>.");
            }else {
                player.sendMessage("§7The path 'System/Player/Hide' has following sub-paths: §fToggleHide <PlayerName>, ToggleImmunity <PlayerName>, List <PlayerName>.");
            }
        }
    }

    public static void toggleAlwaysHide(Player player, String targetName, boolean message) {
        Player target = Bukkit.getPlayer(targetName);
        // Zielspieler zur Liste der versteckten Spieler hinzufügen
        if (target == null) {
            player.sendMessage(net.md_5.bungee.api.ChatColor.RED + "Error: Target not found!");
            return;
        }
        if (!HideAPI.canSee(player,target)){
            player.sendMessage(net.md_5.bungee.api.ChatColor.RED + "Error: Target not found!");
            return;
        }

        if (!HideAPI.getHiddenPlayerNames().contains(targetName)) {
            HideAPI.getHiddenPlayerNames().add(targetName);
            saveHiddenPlayerNames();  // Speichern nach dem Hinzufügen
            if (message) {
                Bukkit.broadcastMessage("§e"+ targetName + " left the game");
            }

            // Wenn der Zielspieler online ist, ihn sofort verstecken
            // Zielspieler für alle anderen Spieler unsichtbar machen
            for (Player players : Bukkit.getOnlinePlayers()) {
                if (!players.equals(target)) {
                    if (!HideAPI.canSee(player,target)) {
                        players.hidePlayer(NikeySystem.getPlugin(), target);
                    }
                }
            }
            player.sendMessage(ChatColor.GOLD + targetName + " is now §2hidden.");
        }else {
            HideAPI.getHiddenPlayerNames().remove(targetName );
            saveHiddenPlayerNames();  // Speichern nach dem Entfernen
            if (message) {
                Bukkit.broadcastMessage("§e"+ targetName + " joined the game");
            }

            // Wenn der Zielspieler online ist, ihn sofort verstecken
            // Zielspieler für alle anderen Spieler unsichtbar machen
            for (Player players : Bukkit.getOnlinePlayers()) {
                if (!players.equals(target)) {
                    players.showPlayer(NikeySystem.getPlugin(), target);
                }
            }
            player.sendMessage(ChatColor.GOLD + targetName + " is now §cshown.");
        }
    }

    public static void toggleTrueAlwaysHide(Player player, String targetName, boolean message) {
        Player target = Bukkit.getPlayer(targetName);
        // Zielspieler zur Liste der versteckten Spieler hinzufügen
        if (target == null) {
            player.sendMessage(net.md_5.bungee.api.ChatColor.RED + "Error: Target not found!");
            return;
        }
        if (!HideAPI.canSee(player,target)){
            player.sendMessage(net.md_5.bungee.api.ChatColor.RED + "Error: Target not found!");
            return;
        }

        if (!HideAPI.getTrueHiddenNames().contains(targetName)) {
            HideAPI.getTrueHiddenNames().add(targetName);
            HideAPI.getHideImmunity().remove(targetName);
            saveTrueHiddenPlayers();  // Speichern nach dem Hinzufügen
            if (message) {
                Bukkit.broadcastMessage("§e"+ targetName + " left the game");
            }

            // Wenn der Zielspieler online ist, ihn sofort verstecken
            // Zielspieler für alle anderen Spieler unsichtbar machen
            for (Player players : Bukkit.getOnlinePlayers()) {
                if (!players.equals(target)) {
                    if (!PermissionAPI.isOwner(players.getName()) && !HideAPI.getTrueHideImmunity().contains(players.getName())) {
                        players.hidePlayer(NikeySystem.getPlugin(), target);
                    }
                }
            }
            player.sendMessage(ChatColor.GOLD + targetName + " is now §2true hidden.");
        }else {
            HideAPI.getTrueHiddenNames().remove(targetName );
            saveTrueHiddenPlayers();  // Speichern nach dem Entfernen
            if (message) {
                Bukkit.broadcastMessage("§e"+ targetName + " joined the game");
            }

            // Zielspieler für alle anderen Spieler unsichtbar machen
            for (Player players : Bukkit.getOnlinePlayers()) {
                if (!players.equals(target)) {
                    players.showPlayer(NikeySystem.getPlugin(), target);
                }
            }
            player.sendMessage(ChatColor.GOLD + targetName + " is now §cshown.");
        }
    }
}
