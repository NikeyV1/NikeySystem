package de.nikey.nikeysystem;

import de.nikey.nikeysystem.Player.Distributor.PermissionDistributor;
import de.nikey.nikeysystem.Player.Distributor.HideDistributor;
import de.nikey.nikeysystem.Player.Functions.HideFunctions;
import de.nikey.nikeysystem.General.CommandRegister;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NikeySystem extends JavaPlugin {


    private static NikeySystem plugin;


    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        HideDistributor.loadHiddenPlayerNames();
        HideDistributor.loadTrueHiddenPlayers();
        HideDistributor.loadHideImmunityPlayers();
        HideDistributor.loadTrueHideImmunityPlayers();
        PermissionDistributor.loadAdmins();
        PermissionDistributor.loadModerators();

        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new HideFunctions(),this);
        manager.registerEvents(new CommandRegister(),this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static NikeySystem getPlugin() {
        return plugin;
    }
}
