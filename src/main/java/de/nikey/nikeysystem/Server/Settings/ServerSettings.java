package de.nikey.nikeysystem.Server.Settings;

import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Server.API.SettingsAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;


public class ServerSettings implements Listener {

    private static final String inventoryTitle = ChatColor.BLUE + "Server Settings";

    public static void openSettingsInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, inventoryTitle);

        addItemToInventory(inventory, 0, Material.PAPER, "MOTD changing");
        addItemToInventory(inventory, 1, Material.IRON_DOOR, "Whitelist toggle");
        addItemToInventory(inventory, 2, Material.PLAYER_HEAD, "Max players");
        addItemToInventory(inventory, 3, Material.NAME_TAG, "Remove from /plugin");
        String endStatus = isEndAllowed() ? "Enabled" : "Disabled";
        addItemToInventory(inventory, 4, Material.END_PORTAL_FRAME, "End Toggle: " + endStatus);
        String onStatus = NikeySystem.getPlugin().getConfig().getBoolean("system.setting.disable_system_command_logging") ? "Enabled" : "Disabled";
        addItemToInventory(inventory,5,Material.WRITTEN_BOOK,"Disable system-command logging: "+ onStatus);

        String  statusCommandPrefix = NikeySystem.getPlugin().getConfig().getBoolean("system.setting.deactivate_command_with_prefix") ? "Enabled" : "Disabled";
        addItemToInventory(inventory,6,Material.PAPER,"Disable commands with provider prefix: "+ statusCommandPrefix);

        player.openInventory(inventory);
    }

    private static void addItemToInventory(Inventory inventory, int slot, Material material, String displayName) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + displayName);
            item.setItemMeta(meta);
        }
        inventory.setItem(slot, item);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(inventoryTitle)) {
            return;
        }

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        switch (slot) {
            case Slot.MOTD_CHANGE:
                requestInput(player, "Enter the new MOTD in chat:", input -> {
                    String motd = ChatColor.translateAlternateColorCodes('&', input);
                    saveMOTD(player,motd);
                    Bukkit.getServer().setMotd(motd);
                    player.sendMessage(ChatColor.GREEN + "MOTD has been updated to: "+ChatColor.RESET + motd);
                });
                break;
            case Slot.WHITELIST_TOGGLE:
                toggleSetting(player, "Whitelist", Bukkit.getServer().hasWhitelist(), Bukkit.getServer()::setWhitelist);
                break;
            case Slot.MAX_PLAYERS_CHANGE:
                requestInput(player, "Enter the new maximum number of players in chat:", input -> {
                    try {
                        int maxPlayers = Integer.parseInt(input);
                        Bukkit.getServer().setMaxPlayers(maxPlayers);
                        player.sendMessage(ChatColor.GREEN + "Max players has been updated to: " + maxPlayers);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Invalid number!");
                    }
                });
                break;
            case Slot.REMOVE_FROM_PLUGINCMD:
                boolean enabled = NikeySystem.getPlugin().getConfig().getBoolean("system.setting.remove_from_plugincmd");
                if (enabled) {
                    NikeySystem.getPlugin().getConfig().set("system.setting.remove_from_plugincmd",false);
                    NikeySystem.getPlugin().saveConfig();
                    player.sendMessage(Component.text("The system is now ").color(NamedTextColor.GRAY).append(Component.text("added").color(NamedTextColor.GREEN)).append(Component.text(" from /plugin command").color(NamedTextColor.GRAY)));
                }else {
                    NikeySystem.getPlugin().getConfig().set("system.setting.remove_from_plugincmd",true);
                    NikeySystem.getPlugin().saveConfig();
                    player.sendMessage(Component.text("The system is now ").color(NamedTextColor.GRAY).append(Component.text("removed").color(NamedTextColor.RED)).append(Component.text(" from /plugin command").color(NamedTextColor.GRAY)));
                }
                break;
            case Slot.END:
                toggleAllowEnd(player);
                Component endStatus = isEndAllowed() ? Component.text("Enabled").color(NamedTextColor.GREEN) : Component.text("Disabled").color(NamedTextColor.RED);
                ItemStack item = event.getInventory().getItem(Slot.END);
                if (item != null && item.getItemMeta() != null) {
                    ItemMeta meta = item.getItemMeta();

                    meta.displayName(Component.text("End Toggle: ").color(NamedTextColor.GOLD).append(endStatus));
                    meta.lore(List.of(
                            Component.text("Restart needed").color(NamedTextColor.RED)
                    ));
                    item.setItemMeta(meta);
                }
                break;
            case Slot.SYSTEM_LOGGING:
                boolean commandLogging = NikeySystem.getPlugin().getConfig().getBoolean("system.setting.disable_system_command_logging");
                if (commandLogging) {
                    NikeySystem.getPlugin().getConfig().set("system.setting.disable_system_command_logging",false);
                    NikeySystem.getPlugin().saveConfig();
                    ItemStack i = event.getInventory().getItem(Slot.SYSTEM_LOGGING);
                    if (i != null && i.getItemMeta() != null) {
                        ItemMeta meta = i.getItemMeta();

                        meta.displayName(Component.text("Disable system-command logging: ").color(NamedTextColor.GOLD).append(Component.text("Disabled").color(NamedTextColor.RED)));
                        meta.lore(List.of(
                                Component.text("Restart needed").color(NamedTextColor.RED)
                        ));
                        i.setItemMeta(meta);
                    }
                }else {
                    NikeySystem.getPlugin().getConfig().set("system.setting.disable_system_command_logging",true);
                    NikeySystem.getPlugin().saveConfig();
                    ItemStack i = event.getInventory().getItem(Slot.SYSTEM_LOGGING);
                    if (i != null && i.getItemMeta() != null) {
                        ItemMeta meta = i.getItemMeta();

                        meta.displayName(Component.text("Disable system-command logging: ").color(NamedTextColor.GOLD).append(Component.text("Enabled").color(NamedTextColor.GREEN)));
                        meta.lore(List.of(
                                Component.text("Reload needed").color(NamedTextColor.RED)
                        ));
                        i.setItemMeta(meta);
                    }
                }
                break;
            case Slot.DEAKTIVATE_COMMANDS_WITH_PREFIX:
                boolean isDeactivated = NikeySystem.getPlugin().getConfig().getBoolean("system.setting.deactivate_command_with_prefix");

                NikeySystem.getPlugin().getConfig().set("system.setting.deactivate_command_with_prefix", !isDeactivated);
                NikeySystem.getPlugin().saveConfig();

                Component status = Component.text(!isDeactivated ? "Enabled" : "Disabled")
                        .color(!isDeactivated ? NamedTextColor.GREEN : NamedTextColor.RED);

                ItemStack slot6 = event.getInventory().getItem(Slot.DEAKTIVATE_COMMANDS_WITH_PREFIX);
                if (slot6 != null && slot6.getItemMeta() != null) {
                    ItemMeta meta = slot6.getItemMeta();
                    meta.displayName(Component.text("Disable commands with provider prefix: ").color(NamedTextColor.GOLD)
                            .append(status));
                    slot6.setItemMeta(meta);
                }
                break;
        }
    }


    private void toggleSetting(Player player, String settingName, boolean currentValue, Consumer<Boolean> toggleFunction) {
        toggleFunction.accept(!currentValue);
        player.sendMessage(ChatColor.GREEN + settingName + " is now " + (!currentValue ? "enabled" : "disabled"));
    }

    private void requestInput(Player player, String message, Consumer<String> onInputReceived) {
        player.sendMessage(ChatColor.YELLOW + message);
        SettingsAPI.inputRequests.put(player, onInputReceived);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (SettingsAPI.inputRequests.containsKey(player)) {

            String input = event.getMessage();
            Consumer<String> callback = SettingsAPI.inputRequests.remove(player);

            if (callback != null) {
                callback.accept(input);
            }
        }
    }

    private static boolean isEndAllowed() {
        File bukkitYmlFile = new File(Bukkit.getWorldContainer()+"/bukkit.yml");
        if (!bukkitYmlFile.exists()) {
            return false;
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(bukkitYmlFile);
        return yaml.getBoolean("settings.allow-end", true);
    }

    private void saveMOTD(Player player, String motd) {
        File propertiesFile = new File(Bukkit.getWorldContainer(), "server.properties");

        if (!propertiesFile.exists()) {
            player.sendMessage(ChatColor.RED + "Error: server.properties not found!");
            return;
        }

        Properties properties = new Properties();

        try (FileInputStream input = new FileInputStream(propertiesFile)) {
            properties.load(input);
        } catch (IOException e) {
            player.sendMessage(ChatColor.RED + "Error loading server.properties: " + e.getMessage());
            return;
        }

        properties.setProperty("motd", motd);

        try (FileOutputStream output = new FileOutputStream(propertiesFile)) {
            properties.store(output, "Updated MOTD by Plugin");
            player.sendMessage(ChatColor.GREEN + "MOTD updated successfully!");
        } catch (IOException e) {
            player.sendMessage(ChatColor.RED + "Error saving server.properties: " + e.getMessage());
        }
    }

    private void toggleAllowEnd(Player player) {
        File bukkitYmlFile = new File(Bukkit.getWorldContainer()+"/bukkit.yml");
        if (!bukkitYmlFile.exists()) {
            player.sendMessage(ChatColor.RED + "Error: bukkit.yml not found!");
            return;
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(bukkitYmlFile);
        boolean currentStatus = yaml.getBoolean("settings.allow-end", true);
        yaml.set("settings.allow-end", !currentStatus);  // Umgeschaltet auf den gegenteiligen Wert

        try {
            yaml.save(bukkitYmlFile);  // Änderungen speichern
            player.sendMessage(ChatColor.GREEN + "The End is now " + (!currentStatus ? "§aenabled" : "§cdisabled"));
        } catch (IOException e) {
            player.sendMessage(ChatColor.RED + "Error: saving bukkit.yml: " + e.getMessage());
        }
    }

    public static class Slot {
        static final int MOTD_CHANGE = 0;
        static final int WHITELIST_TOGGLE = 1;
        static final int MAX_PLAYERS_CHANGE = 2;
        static final int REMOVE_FROM_PLUGINCMD = 3;
        static final int END = 4;
        static final int SYSTEM_LOGGING = 5;
        static final int DEAKTIVATE_COMMANDS_WITH_PREFIX = 6;

    }

}
