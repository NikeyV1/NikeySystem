package de.nikey.nikeysystem.Server.Functions;

import de.nikey.nikeysystem.Server.API.SettingsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
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


public class SettingsFunctions implements Listener {

    public static Properties properties;
    public static File propertiesFile;
    private final String inventoryTitle = ChatColor.BLUE + "Server Settings";


    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage().toLowerCase();
        Player player = event.getPlayer();

        if (message.equals("/continue") && SettingsAPI.settingsContinue.contains(player)) {
            SettingsAPI.settingsContinue.remove(player);
            openSettingsInventory(player);
            event.setCancelled(true);
        }
    }

    private void openSettingsInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, inventoryTitle);

        addItemToInventory(inventory, 0, Material.PAPER, "MOTD ändern");
        addItemToInventory(inventory, 1, Material.IRON_DOOR, "Whitelist umschalten");
        addItemToInventory(inventory, 2, Material.REDSTONE_TORCH, "Online-Mode umschalten");
        addItemToInventory(inventory, 3, Material.DIAMOND_SWORD, "PvP umschalten");
        addItemToInventory(inventory, 4, Material.FEATHER, "Fliegen erlauben");
        addItemToInventory(inventory, 5, Material.ZOMBIE_HEAD, "Schwierigkeitsgrad ändern");
        addItemToInventory(inventory, 6, Material.PLAYER_HEAD, "Maximale Spieleranzahl ändern");
        addItemToInventory(inventory, 7, Material.ENDER_EYE, "Sichtweite ändern");
        addItemToInventory(inventory, 8, Material.SHEEP_SPAWN_EGG, "Spawn von Tieren umschalten");
        addItemToInventory(inventory, 9, Material.ZOMBIE_SPAWN_EGG, "Spawn von Monstern umschalten");
        addItemToInventory(inventory, 10, Material.MAP, "Strukturen generieren");
        addItemToInventory(inventory, 11, Material.WITHER_SKELETON_SKULL, "Hardcore-Modus umschalten");
        addItemToInventory(inventory,49,Material.MAGENTA_GLAZED_TERRACOTTA,"Reload Server");

        player.openInventory(inventory);
    }

    private void addItemToInventory(Inventory inventory, int slot, Material material, String displayName) {
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
                    Bukkit.getServer().setMotd(motd);
                    player.sendMessage(ChatColor.GREEN + "MOTD has been updated to: "+ChatColor.RESET + motd);
                });
                break;
            case Slot.WHITELIST_TOGGLE:
                toggleSetting(player, "Whitelist", Bukkit.getServer().hasWhitelist(), Bukkit.getServer()::setWhitelist);
                break;
            case Slot.DIFFICULTY_CHANGE:
                cycleDifficulty(player);
                break;
            case Slot.MAX_PLAYERS_CHANGE:
                requestInput(player, "Enter the new maximum number of players in chat:", input -> {
                    try {
                        int maxPlayers = Integer.parseInt(input);
                        Bukkit.getServer().setMaxPlayers(maxPlayers);
                        player.sendMessage(ChatColor.GREEN + "Max players has been updated to: " + maxPlayers);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Invalid number! Please try again.");
                    }
                });
                break;
            case Slot.RENDER_DISTANCE_CHANGE:
                break;
            case Slot.PVP:
                toggleSetting(player, "PVP", player.getWorld().getPVP(), player.getWorld()::setPVP);
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

    private void cycleDifficulty(Player player) {
        Difficulty currentDifficulty = player.getWorld().getDifficulty();
        Difficulty nextDifficulty = switch (currentDifficulty) {
            case PEACEFUL -> Difficulty.EASY;
            case EASY -> Difficulty.NORMAL;
            case NORMAL -> Difficulty.HARD;
            case HARD -> Difficulty.PEACEFUL;
        };
        player.getWorld().setDifficulty(nextDifficulty);
        player.sendMessage(ChatColor.GREEN + "Difficulty set to " + nextDifficulty.name());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (SettingsAPI.inputRequests.containsKey(player)) {
            event.setCancelled(true); // Verhindert, dass die Nachricht im Chat erscheint

            String input = event.getMessage();
            Consumer<String> callback = SettingsAPI.inputRequests.remove(player);

            if (callback != null) {
                callback.accept(input);
            }
        }
    }

    private static class Slot {
        static final int MOTD_CHANGE = 0;
        static final int WHITELIST_TOGGLE = 1;
        static final int PVP = 3;
        static final int DIFFICULTY_CHANGE = 5;
        static final int MAX_PLAYERS_CHANGE = 6;
        static final int RENDER_DISTANCE_CHANGE = 7;
        static final int MAP = 10;

    }

}
