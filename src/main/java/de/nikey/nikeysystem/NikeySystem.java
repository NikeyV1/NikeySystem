package de.nikey.nikeysystem;

import de.nikey.nikeysystem.Player.Distributor.PermissionDistributor;
import de.nikey.nikeysystem.Player.Distributor.HideDistributor;
import de.nikey.nikeysystem.Player.Functions.HideFunctions;
import de.nikey.nikeysystem.General.CommandRegister;
import de.nikey.nikeysystem.Server.Distributor.CommandDistributor;
import de.nikey.nikeysystem.Server.Functions.CommandFunctions;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NikeySystem extends JavaPlugin {


    private static NikeySystem plugin;


    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        HideDistributor.loadAll();
        PermissionDistributor.loadAdmins();
        PermissionDistributor.loadModerators();
        CommandDistributor.loadBlockedCommands();

        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new HideFunctions(),this);
        manager.registerEvents(new CommandRegister(),this);
        manager.registerEvents(new CommandFunctions(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static NikeySystem getPlugin() {
        return plugin;
    }
}
