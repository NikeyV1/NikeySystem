package de.nikey.nikeysystem.Server.Functions;

import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Server.API.SettingsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import static de.nikey.nikeysystem.Server.API.SettingsAPI.awaitingTextInput;

public class SettingsFunctions implements Listener {

    public static Properties properties;
    public static File propertiesFile;



    private final List<String> editableProperties = Arrays.asList(
            "allow-flight", "allow-nether", "broadcast-console-to-ops", "broadcast-rcon-to-ops",
            "difficulty", "enable-command-block", "enable-jmx-monitoring", "enable-query",
            "enable-rcon", "enable-status", "enforce-secure-profile", "enforce-whitelist",
            "entity-broadcast-range-percentage", "force-gamemode", "function-permission-level",
            "gamemode", "generate-structures", "generator-settings", "hardcore", "hide-online-players",
            "level-name", "level-seed", "level-type", "max-chained-neighbor-updates", "max-players",
            "max-tick-time", "max-world-size", "motd", "network-compression-threshold", "online-mode",
            "op-permission-level", "player-idle-timeout", "prevent-proxy-connections",
            "pvp", "query.port", "rate-limit", "rcon.password", "rcon.port",
            "require-resource-pack", "resource-pack", "resource-pack-prompt",
            "resource-pack-sha1", "resource-pack-sha256", "server-ip", "server-port",
            "simulation-distance", "spawn-animals", "spawn-monsters", "spawn-npcs",
            "spawn-protection", "sync-chunk-writes", "use-native-transport", "view-distance",
            "white-list"
    );
    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage().toLowerCase();
        Player player = event.getPlayer();

        if (message.equals("/continue") && SettingsAPI.settingsContinue.contains(player)) {
            SettingsAPI.settingsContinue.remove(player);
            openPropertiesInventory(player,0);
            event.setCancelled(true);
        }
    }

    public static void loadProperties() {
        properties = new Properties();
        try (FileInputStream inStream = new FileInputStream(propertiesFile)) {
            properties.load(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveProperties() {
        try (FileOutputStream outStream = new FileOutputStream(propertiesFile)) {
            properties.store(outStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openPropertiesInventory(Player player, int page) {
        int itemsPerPage = 45; // 45 slots for properties, 9 slots for navigation and reload button
        int totalPages = (int) Math.ceil((double) editableProperties.size() / itemsPerPage);
        int startIndex = page * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, editableProperties.size());

        Inventory inv = Bukkit.createInventory(null, 54, "Server Properties - Page " + (page + 1));

        // Add the properties to the inventory
        for (int i = startIndex; i < endIndex; i++) {
            String propertyName = editableProperties.get(i);
            ItemStack item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();

            String propertyValue = properties.getProperty(propertyName, "N/A");
            meta.setDisplayName(ChatColor.GREEN + propertyName);
            meta.setLore(Collections.singletonList(ChatColor.YELLOW + "Value: " + propertyValue));
            item.setItemMeta(meta);

            inv.setItem(i - startIndex, item);
        }

        // Add the navigation buttons
        if (page > 0) {
            ItemStack previousPage = new ItemStack(Material.ARROW);
            ItemMeta meta = previousPage.getItemMeta();
            meta.setDisplayName(ChatColor.RED + "Previous Page");
            previousPage.setItemMeta(meta);
            inv.setItem(45, previousPage);
        }

        if (page < totalPages - 1) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta meta = nextPage.getItemMeta();
            meta.setDisplayName(ChatColor.RED + "Next Page");
            nextPage.setItemMeta(meta);
            inv.setItem(53, nextPage);
        }

        // Add the reload button
        ItemStack reloadButton = new ItemStack(Material.BARRIER);
        ItemMeta reloadMeta = reloadButton.getItemMeta();
        reloadMeta.setDisplayName(ChatColor.RED + "Reload Server");
        reloadButton.setItemMeta(reloadMeta);
        inv.setItem(49, reloadButton);

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().startsWith("Server Properties")) return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        String itemName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

        // Handle navigation buttons
        if (itemName.equals("Next Page")) {
            int currentPage = Integer.parseInt(event.getView().getTitle().split(" ")[4]) - 1;
            openPropertiesInventory(player, currentPage + 1);
            return;
        } else if (itemName.equals("Previous Page")) {
            int currentPage = Integer.parseInt(event.getView().getTitle().split(" ")[4]) - 1;
            openPropertiesInventory(player, currentPage - 1);
            return;
        } else if (itemName.equals("Reload Server")) {
            saveProperties();
            Bukkit.reload();
            player.sendMessage(ChatColor.GREEN + "Server reloaded!");
            return;
        }

        // Handle text input properties
        if (requiresTextInput(itemName)) {
            player.closeInventory();
            player.sendMessage(ChatColor.YELLOW + "Please enter a new value for " + itemName + ":");
            awaitingTextInput.put(player.getUniqueId(), itemName);
        } else {
            // Handle property changes (e.g., boolean or numeric properties)
            String currentValue = properties.getProperty(itemName, "N/A");
            boolean isRightClick = event.isRightClick();
            String newValue = getUpdatedPropertyValue(itemName, currentValue, isRightClick);

            properties.setProperty(itemName, newValue);
            saveProperties();
            int currentPage = Integer.parseInt(event.getView().getTitle().split(" ")[4]) - 1;
            openPropertiesInventory(player, currentPage);

            player.sendMessage(ChatColor.GREEN + itemName + " set to: " + newValue);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (awaitingTextInput.containsKey(playerId)) {
            event.setCancelled(true); // Cancel the chat message

            String propertyName = awaitingTextInput.remove(playerId);
            String newValue = event.getMessage();

            properties.setProperty(propertyName, newValue);
            saveProperties();
            Bukkit.getScheduler().runTask(NikeySystem.getPlugin(), () -> {
                player.sendMessage(ChatColor.GREEN + propertyName + " set to: " + newValue);
                openPropertiesInventory(player, 0); // Reopen the inventory at the first page
            });
        }
    }

    private boolean requiresTextInput(String propertyName) {
        return propertyName.equals("motd") || propertyName.equals("level-name") || propertyName.equals("resource-pack") || propertyName.equals("server-ip");
    }

    private String getUpdatedPropertyValue(String propertyName, String currentValue, boolean isRightClick) {
        if (isBooleanProperty(currentValue)) {
            return toggleBoolean(currentValue); // Boolean values toggle regardless of click type
        } else if (isNumericProperty(currentValue)) {
            return updateNumeric(currentValue, isRightClick);
        } else if (isEnumProperty(propertyName)) {
            return cycleEnumValue(propertyName, currentValue, isRightClick);
        } else {
            return currentValue;
        }
    }

    private boolean isBooleanProperty(String value) {
        return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
    }

    private boolean isNumericProperty(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isEnumProperty(String propertyName) {
        return "difficulty".equals(propertyName) || "gamemode".equals(propertyName);
    }

    private String toggleBoolean(String currentValue) {
        return "true".equalsIgnoreCase(currentValue) ? "false" : "true";
    }

    private String updateNumeric(String currentValue, boolean isRightClick) {
        try {
            int value = Integer.parseInt(currentValue);
            value = isRightClick ? value - 1 : value + 1;
            return String.valueOf(value);
        } catch (NumberFormatException e) {
            return currentValue;
        }
    }

    private String cycleEnumValue(String propertyName, String currentValue, boolean isRightClick) {
        switch (propertyName) {
            case "difficulty":
                return cycleValues(new String[]{"peaceful", "easy", "normal", "hard"}, currentValue, isRightClick);
            case "gamemode":
                return cycleValues(new String[]{"survival", "creative", "adventure", "spectator"}, currentValue, isRightClick);
            default:
                return currentValue;
        }
    }

    private String cycleValues(String[] values, String currentValue, boolean isRightClick) {
        int currentIndex = indexOf(values, currentValue);
        int newIndex = isRightClick ? currentIndex - 1 : currentIndex + 1;
        if (newIndex < 0) {
            newIndex = values.length - 1;
        } else if (newIndex >= values.length) {
            newIndex = 0;
        }
        return values[newIndex];
    }

    private int indexOf(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(value)) return i;
        }
        return -1;
    }
}
