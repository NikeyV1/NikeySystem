package de.nikey.nikeysystem;

import de.nikey.nikeysystem.Player.ResourcePack;
import de.nikey.nikeysystem.hide.CommandRegister;
import de.nikey.nikeysystem.hide.HideEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NikeySystem extends JavaPlugin {


    private static NikeySystem plugin;


    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        CommandRegister.loadHiddenPlayerNames();

        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new CommandRegister() , this);
        manager.registerEvents(new HideEvents(),this);
        manager.registerEvents(new ResourcePack(),this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static NikeySystem getPlugin() {
        return plugin;
    }
}
