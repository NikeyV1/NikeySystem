package de.nikey.nikeysystem;

import de.nikey.nikeysystem.General.SystemCommandTabCompleter;
import de.nikey.nikeysystem.Player.API.ChatAPI;
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
import de.nikey.nikeysystem.Server.API.LoggingAPI;
import de.nikey.nikeysystem.Server.API.WorldAPI;
import de.nikey.nikeysystem.Server.Distributor.BackupDistributor;
import de.nikey.nikeysystem.Server.Distributor.CommandDistributor;
import de.nikey.nikeysystem.Server.Functions.*;
import de.nikey.nikeysystem.Server.Settings.LoggingSettings;
import de.nikey.nikeysystem.Server.Settings.ServerSettings;
import de.nikey.nikeysystem.Server.Settings.WorldSettings;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.module.Configuration;
import java.nio.file.Path;
import java.util.UUID;
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
        WorldFunctions.deleteTemporaryWorlds();
        WorldAPI.loadWorlds();
        BackupDistributor.startup();
        LoggingAPI.initializeFiles();
        ChatAPI.loadChannels();

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

        getCommand("system").setTabCompleter(new SystemCommandTabCompleter());

        for (World world : Bukkit.getWorlds()) {
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true);
            world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK,true);
        }
    }

    @Override
    public void onDisable() {
        MuteAPI.saveMutedPlayers();
        WorldFunctions.deleteAndUnloadTemporaryWorlds();
        WorldFunctions.deleteTemporaryWorlds();
        InventoryAPI.saveInventories();
        LoggingAPI.saveLogs();
        ChatAPI.saveChannels();
    }



    public static NikeySystem getPlugin() {
        return plugin;
    }
}
