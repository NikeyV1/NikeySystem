package de.nikey.nikeysystem.Distributor;

import de.nikey.nikeysystem.API.HideAPI;
import de.nikey.nikeysystem.API.PermissionAPI;
import de.nikey.nikeysystem.NikeySystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class HideDistributor implements Listener {

    public static void loadHiddenPlayerNames() {
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

    public static void loadTrueHiddenPlayers() {
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
    public static void loadHideImmunityPlayers() {
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

    public static void loadTrueHideImmunityPlayers() {
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
            toggleAlwaysHide(player,args[4]);
        } else if (args[3].equalsIgnoreCase("ToggleTrueHide") && PermissionAPI.isOwner(player.getName())) {
            toggleTrueAlwaysHide(player,args[4]);
        } else if (args[3].equalsIgnoreCase("ToggleImmunity")) {
            if (HideAPI.getHideImmunity().contains(args[4])) {
                HideAPI.getHideImmunity().remove(args[4]);
                saveHideImmunityPlayers();
                player.sendMessage(ChatColor.GOLD + player.getName() + " has now hide immunity §cremoved");
            } else {
                HideAPI.getHideImmunity().add(args[4]);
                saveHideImmunityPlayers();
                player.sendMessage(ChatColor.GOLD + player.getName() + " has now hide immunity §2added");
            }
        } else if (args[3].equalsIgnoreCase("ToggleTrueImmunity") && PermissionAPI.isOwner(player.getName())) {
            if (HideAPI.getTrueHideImmunity().contains(args[4])) {
                HideAPI.getTrueHideImmunity().remove(args[4]);
                saveTrueHideImmunityPlayers();
                player.sendMessage(ChatColor.GOLD + player.getName() + " has now true hide immunity §cremoved");
            } else {
                HideAPI.getTrueHideImmunity().add(args[4]);
                saveTrueHideImmunityPlayers();
                player.sendMessage(ChatColor.GOLD + player.getName() + " has now true hide immunity §2added");
            }
        }
    }

    public static void toggleAlwaysHide(Player player, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        // Zielspieler zur Liste der versteckten Spieler hinzufügen
        if (!HideAPI.getHiddenPlayerNames().contains(targetName)) {
            HideAPI.getHiddenPlayerNames().add(targetName);
            saveHiddenPlayerNames();  // Speichern nach dem Hinzufügen

            // Wenn der Zielspieler online ist, ihn sofort verstecken
            if (target != null) {
                // Zielspieler für alle anderen Spieler unsichtbar machen
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (!players.equals(target)) {
                        if (!PermissionAPI.isAdmin(players.getName()) && !PermissionAPI.isOwner(players.getName()) && !HideAPI.getTrueHideImmunity().contains(players.getName()) && !HideAPI.getHideImmunity().contains(players.getName())) {
                            players.hidePlayer(NikeySystem.getPlugin(), target);
                        }
                    }
                }
            }
            player.sendMessage(ChatColor.GOLD + targetName + " is now §2hidden.");
        }else {
            HideAPI.getHiddenPlayerNames().remove(targetName );
            saveHiddenPlayerNames();  // Speichern nach dem Entfernen

            // Wenn der Zielspieler online ist, ihn sofort verstecken
            if (target != null) {
                // Zielspieler für alle anderen Spieler unsichtbar machen
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (!players.equals(target)) {
                        players.showPlayer(NikeySystem.getPlugin(), target);
                    }
                }
            }
            player.sendMessage(ChatColor.GOLD + targetName + " is now §cshown.");
        }
    }

    public static void toggleTrueAlwaysHide(Player player, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        // Zielspieler zur Liste der versteckten Spieler hinzufügen
        if (!HideAPI.getTrueHiddenNames().contains(targetName)) {
            HideAPI.getTrueHiddenNames().add(targetName);
            HideAPI.getHideImmunity().remove(targetName);
            saveTrueHiddenPlayers();  // Speichern nach dem Hinzufügen

            // Wenn der Zielspieler online ist, ihn sofort verstecken
            if (target != null) {
                // Zielspieler für alle anderen Spieler unsichtbar machen
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (!players.equals(target)) {
                        if (!PermissionAPI.isOwner(players.getName()) && !HideAPI.getTrueHideImmunity().contains(players.getName())) {
                            players.hidePlayer(NikeySystem.getPlugin(), target);
                        }
                    }
                }
            }
            player.sendMessage(ChatColor.GOLD + targetName + " is now §2true hidden.");
        }else {
            HideAPI.getTrueHiddenNames().remove(targetName );
            saveTrueHiddenPlayers();  // Speichern nach dem Entfernen

            // Wenn der Zielspieler online ist, ihn sofort verstecken
            if (target != null) {
                // Zielspieler für alle anderen Spieler unsichtbar machen
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (!players.equals(target)) {
                        players.showPlayer(NikeySystem.getPlugin(), target);
                    }
                }
            }
            player.sendMessage(ChatColor.GOLD + targetName + " is now §cshown.");
        }
    }
}
