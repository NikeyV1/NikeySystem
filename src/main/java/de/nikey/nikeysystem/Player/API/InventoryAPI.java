package de.nikey.nikeysystem.Player.API;


import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.Functions.InventoryFunctions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryAPI implements Listener {
    public static final Map<String , String> playerInventories = new HashMap<>();
    public static final Map<UUID, ItemStack[]> offlineInventories = new HashMap<>();
    public static File inventoryFile;
    public static FileConfiguration inventoryData;

    public static void saveInventories() {
        for (Map.Entry<UUID, ItemStack[]> entry : offlineInventories.entrySet()) {
            String key = entry.getKey().toString();
            inventoryData.set(key, entry.getValue());
        }
        try {
            inventoryData.save(inventoryFile);
        } catch (IOException e) {
            NikeySystem.getPlugin().getLogger().severe("Failed to save inventory data: " + e.getMessage());
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID playerUUID = player.getUniqueId();

            // Speichern des Inventars in offlineInventories
            InventoryAPI.offlineInventories.put(playerUUID, player.getInventory().getContents());

            InventoryAPI.inventoryData.set(playerUUID.toString(), player.getInventory().getContents());
            try {
                InventoryAPI.inventoryData.save(InventoryAPI.inventoryFile);
            } catch (IOException e) {
                ChatAPI.sendManagementMessage(Component.text("Error saving inventory for player ").color(NamedTextColor.RED)
                        .append(Component.text(player.getName()).color(NamedTextColor.WHITE))
                        .append(Component.text(": " + e.getMessage()).color(NamedTextColor.RED)), ChatAPI.ManagementType.ERROR);
            }
        }
    }

    private static void loadInventories() {
        for (String key : inventoryData.getKeys(false)) {
            UUID playerUUID = UUID.fromString(key);
            ItemStack[] items = inventoryData.getList(key).toArray(new ItemStack[0]);
            offlineInventories.put(playerUUID, items);
        }
    }


    public static void startup() {
        inventoryFile = new File(NikeySystem.getPlugin().getDataFolder(), "inventories.yml");
        if (!inventoryFile.exists()) {
            try {
                inventoryFile.createNewFile();
            } catch (IOException | SecurityException ex) {
                ChatAPI.sendManagementMessage(Component.text("Failed to create inventory file: " + ex.getMessage()), ChatAPI.ManagementType.ERROR);
            }
        }
        inventoryData = YamlConfiguration.loadConfiguration(inventoryFile);

        // Offline-Inventare laden
        loadInventories();
    }

    public static boolean hasOfflineEditing(String player) {
        return NikeySystem.getPlugin().getConfig().getBoolean("inventory.settings." + player + ".editofflineplayers");
    }
}
