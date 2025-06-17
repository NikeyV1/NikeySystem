package de.nikey.nikeysystem;

import de.nikey.nikeysystem.DataBases.BackupDatabase;
import de.nikey.nikeysystem.General.CommandRegister;
import de.nikey.nikeysystem.General.SystemCommandTabCompleter;
import de.nikey.nikeysystem.Player.API.*;
import de.nikey.nikeysystem.Player.Distributor.PermissionDistributor;
import de.nikey.nikeysystem.Player.Functions.*;
import de.nikey.nikeysystem.Player.Settings.HideSettings;
import de.nikey.nikeysystem.Player.Settings.InventorySettings;
import de.nikey.nikeysystem.Player.Settings.LocationSettings;
import de.nikey.nikeysystem.Security.Distributor.SystemShieldDistributor;
import de.nikey.nikeysystem.Security.Functions.SystemShieldFunctions;
import de.nikey.nikeysystem.Server.API.LoggingAPI;
import de.nikey.nikeysystem.Server.API.WorldAPI;
import de.nikey.nikeysystem.Server.Distributor.BackupDistributor;
import de.nikey.nikeysystem.Server.Distributor.CommandDistributor;
import de.nikey.nikeysystem.Server.Functions.*;
import de.nikey.nikeysystem.Server.Settings.BackupSettings;
import de.nikey.nikeysystem.Server.Settings.LoggingSettings;
import de.nikey.nikeysystem.Server.Settings.ServerSettings;
import de.nikey.nikeysystem.Server.Settings.WorldSettings;
import net.kyori.adventure.text.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NikeySystem extends JavaPlugin {

    private static NikeySystem plugin;
    private static PlayerHistoryManager manager;

    @Override
    public void onEnable() {
        plugin = this;
        manager = new PlayerHistoryManager();
        saveDefaultConfig();
        HideAPI.hideStartup();
        PermissionAPI.permissionStartup();
        CommandDistributor.loadBlockedCommands();
        SystemShieldDistributor.loadSystemShield();
        InventoryAPI.startup();
        WorldFunctions.deleteTemporaryWorlds();
        WorldAPI.loadWorlds();
        BackupDistributor.startup();
        LoggingAPI.initializeFiles();
        ChatAPI.chatStartup();
        ModerationAPI.punishmentStartup();
        registerLoggerFilters(new LogFilter());

        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new HideFunctions(),this);
        manager.registerEvents(new CommandRegister(),this);
        manager.registerEvents(new CommandFunctions(), this);
        manager.registerEvents(new SystemShieldFunctions(), this);
        manager.registerEvents(new ServerSettings(), this);
        manager.registerEvents(new InventoryFunctions(), this);
        manager.registerEvents(new HideSettings(),this);
        manager.registerEvents(new SettingsFunctions(),this);
        manager.registerEvents(new LocationFunctions(),this);
        manager.registerEvents(new PerformanceFunctions(),this);
        manager.registerEvents(new InventorySettings(),this);
        manager.registerEvents(new ResourcePackFunctions(),this);
        manager.registerEvents(new WorldSettings(),this);
        manager.registerEvents(new LocationSettings(),this);
        manager.registerEvents(new WorldFunctions(),this);
        manager.registerEvents(new LoggingFunctions(),this);
        manager.registerEvents(new LoggingSettings(),this);
        manager.registerEvents(new ChatFunctions(), this);
        manager.registerEvents(new ModerationFunctions(), this);
        manager.registerEvents(new BackupSettings(), this);

        getCommand("system").setTabCompleter(new SystemCommandTabCompleter());
    }

    private void registerLoggerFilters(Filter... filters) {
        org.apache.logging.log4j.Logger rootLogger = LogManager.getRootLogger();
        if (!(rootLogger instanceof Logger logger)) {
            ChatAPI.sendManagementMessage(Component.text("Something went wrong while registering loggers"), ChatAPI.ManagementType.ERROR);
            return;
        }

        for (Filter filter : filters) {
            logger.addFilter(filter);
        }
    }

    @Override
    public void onDisable() {
        PermissionAPI.permissionShutdown();
        HideAPI.hideShutdown();
        WorldFunctions.deleteAndUnloadTemporaryWorlds();
        WorldFunctions.deleteTemporaryWorlds();
        InventoryAPI.saveInventories();
        LoggingAPI.saveLogs();
        ChatAPI.chatShutdown();
        BackupDatabase.disconnect();
        ModerationAPI.punishmentShutdown();
    }

    public static PlayerHistoryManager getManager() {
        return manager;
    }

    public static NikeySystem getPlugin() {
        return plugin;
    }
}
