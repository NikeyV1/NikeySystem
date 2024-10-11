package de.nikey.nikeysystem;

import de.nikey.nikeysystem.General.SystemCommandTabCompleter;
import de.nikey.nikeysystem.Player.Distributor.PermissionDistributor;
import de.nikey.nikeysystem.Player.Distributor.HideDistributor;
import de.nikey.nikeysystem.Player.Functions.HideFunctions;
import de.nikey.nikeysystem.General.CommandRegister;
import de.nikey.nikeysystem.Player.Functions.InventoryFunctions;
import de.nikey.nikeysystem.Player.Functions.PlayerSettings;
import de.nikey.nikeysystem.Security.Distributor.SystemShieldDistributor;
import de.nikey.nikeysystem.Security.Functions.SystemShieldFunctions;
import de.nikey.nikeysystem.Server.Distributor.CommandDistributor;
import de.nikey.nikeysystem.Server.Functions.CommandFunctions;
import de.nikey.nikeysystem.Server.Functions.SettingsFunctions;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
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
        SystemShieldDistributor.loadSystemShield();
        CommandDistributor.loadPlayerBlockedCommands();

        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new HideFunctions(),this);
        manager.registerEvents(new CommandRegister(),this);
        manager.registerEvents(new CommandFunctions(), this);
        manager.registerEvents(new SystemShieldFunctions(), this);
        manager.registerEvents(new SettingsFunctions(), this);
        manager.registerEvents(new InventoryFunctions(), this);
        manager.registerEvents(new PlayerSettings(),this);
        

        getCommand("system").setTabCompleter(new SystemCommandTabCompleter());

        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true);
            world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK,true);
        }
    }

    @Override
    public void onDisable() {
        CommandDistributor.savePlayerBlockedCommands();
    }

    public static NikeySystem getPlugin() {
        return plugin;
    }
}
