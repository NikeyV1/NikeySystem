package de.nikey.nikeysystem.Server.Functions;

import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Server.API.SettingsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SettingsFunctions implements Listener {
    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage().toLowerCase();
        if (!SettingsAPI.isPluginCMDFaked())return;

        // Prüfe, ob der Spieler den /pl oder /plugins Befehl eingegeben hat
        if (message.equals("/pl") || message.equals("/plugins")) {
            // Verhindere die Ausführung des eigentlichen Befehls
            event.setCancelled(true);

            // Bekomme alle installierten Plugins
            Plugin[] plugins = Bukkit.getPluginManager().getPlugins();

            // Erstelle eine Liste, die die Plugin-Namen enthält, außer dieses Plugin
            List<String> pluginNames = new ArrayList<>();
            for (Plugin plugin : plugins) {
                if (!plugin.getName().equalsIgnoreCase(NikeySystem.getPlugin().getName())) {
                    pluginNames.add(plugin.getName());
                }
            }

            // Füge die Plugin-Namen zu einem String zusammen, getrennt durch ", "
            String pluginList = String.join(", ", pluginNames);

            // Formatierte Nachricht mit Farbcodes, wie im Screenshot
            String formattedMessage = ChatColor.WHITE + "Server Plugins (" + pluginNames.size() + "):\n" +
                    ChatColor.GOLD + "Bukkit Plugins: " + "\n" +
                    ChatColor.GREEN + " - " + pluginList;

            // Sende dem Spieler die gefälschte Pluginliste
            event.getPlayer().sendMessage(formattedMessage);
        }
    }
}
