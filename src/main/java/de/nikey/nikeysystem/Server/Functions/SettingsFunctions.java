package de.nikey.nikeysystem.Server.Functions;

import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Server.API.SettingsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class SettingsFunctions implements Listener {
    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage().toLowerCase();
        if (!SettingsAPI.isPluginCMDFaked())return;

        if (message.equals("/pl") || message.equals("/plugins")) {
            event.setCancelled(true);

            Plugin[] plugins = Bukkit.getPluginManager().getPlugins();

            List<String> pluginNames = new ArrayList<>();
            for (Plugin plugin : plugins) {
                if (!plugin.getName().equalsIgnoreCase(NikeySystem.getPlugin().getName())) {
                    pluginNames.add(plugin.getName());
                }
            }

            String pluginList = String.join(", ", pluginNames);

            String formattedMessage = ChatColor.WHITE + "Server Plugins (" + pluginNames.size() + "):\n" +
                    ChatColor.GOLD + "Bukkit Plugins: " + "\n" +
                    ChatColor.GREEN + " - " + pluginList;

            event.getPlayer().sendMessage(formattedMessage);
        }
    }
}
