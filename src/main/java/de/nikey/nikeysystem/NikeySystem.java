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
import de.nikey.nikeysystem.Server.API.WorldAPI;
import de.nikey.nikeysystem.Server.Distributor.BackupDistributor;
import de.nikey.nikeysystem.Server.Distributor.CommandDistributor;
import de.nikey.nikeysystem.Server.Functions.CommandFunctions;
import de.nikey.nikeysystem.Server.Functions.PerformanceFunctions;
import de.nikey.nikeysystem.Server.Functions.SettingsFunctions;
import de.nikey.nikeysystem.Server.Functions.WorldFunctions;
import de.nikey.nikeysystem.Server.Settings.ServerSettings;
import de.nikey.nikeysystem.Server.Settings.WorldSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.UUID;

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
    }
    public void saveAllPlayerInventories() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID playerUUID = player.getUniqueId();

            // Speichern des Inventars in offlineInventories
            InventoryAPI.offlineInventories.put(playerUUID, player.getInventory().getContents());

            // Speichern in der Datei
            InventoryAPI.inventoryData.set(playerUUID.toString(), player.getInventory().getContents());
            getLogger().info("Saving " + player.getName() + "'s inv");
            try {
                InventoryAPI.inventoryData.save(InventoryAPI.inventoryFile);
            } catch (IOException e) {
                ChatAPI.sendManagementMessage(Component.text("Error saving inventory for player ").color(NamedTextColor.RED)
                        .append(Component.text(player.getName()).color(NamedTextColor.WHITE))
                        .append(Component.text(": " + e.getMessage()).color(NamedTextColor.RED)), ChatAPI.ManagementType.ERROR);
                getLogger().severe("Error saving inventory for player " + player.getName() + ": " + e.getMessage());
            }
        }
    }


    @Override
    public void onDisable() {
        MuteAPI.saveMutedPlayers();
        WorldFunctions.deleteAndUnloadTemporaryWorlds();
        WorldFunctions.deleteTemporaryWorlds();
        InventoryAPI.saveInventories();
        saveAllPlayerInventories();
    }

    public static NikeySystem getPlugin() {
        return plugin;
    }
}
