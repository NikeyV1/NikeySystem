package de.nikey.nikeysystem;

import de.nikey.nikeysystem.General.CommandFilter;
import de.nikey.nikeysystem.General.SystemCommandTabCompleter;
import de.nikey.nikeysystem.Player.API.InventoryAPI;
import de.nikey.nikeysystem.Player.API.MuteAPI;
import de.nikey.nikeysystem.Player.Distributor.PermissionDistributor;
import de.nikey.nikeysystem.Player.Distributor.HideDistributor;
import de.nikey.nikeysystem.Player.Functions.*;
import de.nikey.nikeysystem.General.CommandRegister;
import de.nikey.nikeysystem.Player.Settings.HideSettings;
import de.nikey.nikeysystem.Player.Settings.InventorySettings;
import de.nikey.nikeysystem.Player.Settings.LocationSettings;
import de.nikey.nikeysystem.Security.Distributor.SystemShieldDistributor;
import de.nikey.nikeysystem.Security.Functions.SystemShieldFunctions;
import de.nikey.nikeysystem.Server.API.WorldAPI;
import de.nikey.nikeysystem.Server.Distributor.CommandDistributor;
import de.nikey.nikeysystem.Server.Functions.CommandFunctions;
import de.nikey.nikeysystem.Server.Functions.PerformanceFunctions;
import de.nikey.nikeysystem.Server.Functions.SettingsFunctions;
import de.nikey.nikeysystem.Server.Functions.WorldFunctions;
import de.nikey.nikeysystem.Server.Settings.ServerSettings;
import de.nikey.nikeysystem.Server.Settings.WorldSettings;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

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
        MuteAPI.loadMutedPlayers();
        InventoryAPI.startup();

        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new HideFunctions(),this);
        manager.registerEvents(new CommandRegister(),this);
        manager.registerEvents(new CommandFunctions(), this);
        manager.registerEvents(new SystemShieldFunctions(), this);
        manager.registerEvents(new ServerSettings(), this);
        manager.registerEvents(new InventoryFunctions(), this);
        manager.registerEvents(new HideSettings(),this);
        manager.registerEvents(new SettingsFunctions(),this);
        manager.registerEvents(new MuteFunctions(),this);
        manager.registerEvents(new LocationFunctions(),this);
        manager.registerEvents(new PerformanceFunctions(),this);
        manager.registerEvents(new InventorySettings(),this);
        manager.registerEvents(new ResourcePackFunctions(),this);
        manager.registerEvents(new WorldSettings(),this);
        manager.registerEvents(new LocationSettings(),this);
        manager.registerEvents(new WorldFunctions(),this);
        

        getCommand("system").setTabCompleter(new SystemCommandTabCompleter());

        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true);
            world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK,true);
        }

        WorldAPI.loadWorlds();
    }

    private void applyLoggingFilter() {
        Logger logger = getLogger();

        logger.setFilter(new Filter() {
            @Override
            public boolean isLoggable(LogRecord record) {
                // Check if logging for /system is disabled
                boolean loggingEnabled = getConfig().getBoolean("system.setting.system_command_logging");

                // Suppress /system command logs if logging is disabled
                Bukkit.broadcastMessage(record.getMessage() + loggingEnabled);
                return loggingEnabled && !record.getMessage().contains("issued server command: /system");// Allow all other messages
            }
        });
    }

    @Override
    public void onDisable() {
        MuteAPI.saveMutedPlayers();
        WorldFunctions.deleteAndUnloadTemporaryWorlds();
        WorldFunctions.deleteTemporaryWorlds();
        InventoryAPI.saveInventories();
    }

    public static NikeySystem getPlugin() {
        return plugin;
    }
}
