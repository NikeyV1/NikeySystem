package de.nikey.nikeysystem.Player.Functions;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.ChatAPI;
import de.nikey.nikeysystem.Player.API.InventoryAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.*;

import java.io.IOException;
import java.util.UUID;

import static de.nikey.nikeysystem.Player.API.InventoryAPI.*;
import static de.nikey.nikeysystem.Player.Distributor.InventoryDistributor.updatePlayerInventory;

public class InventoryFunctions implements Listener {
    @EventHandler
    public void onArmorChange(PlayerArmorChangeEvent event) {
        updatePlayerInventory(event.getPlayer());
    }

    @EventHandler
    public void onInventory(InventoryEvent event) {
        updatePlayerInventory((Player) event.getView().getPlayer());

        if (event.getView().getTitle().equalsIgnoreCase("Equipment") && event.getInventory().getSize() == 9) {
            Player player = Bukkit.getPlayer(InventoryAPI.playerInventories.get(event.getView().getPlayer().getName()));
            if (player == null) {
                event.getView().getPlayer().sendMessage(Component.text("Error: player not found").color(NamedTextColor.RED));
                return;
            }

            Inventory inventory = event.getView().getTopInventory();
            EntityEquipment equipment = player.getEquipment();
            equipment.setItem(EquipmentSlot.FEET,inventory.getItem(0));
            equipment.setItem(EquipmentSlot.LEGS,inventory.getItem(1));
            equipment.setItem(EquipmentSlot.CHEST,inventory.getItem(2));
            equipment.setItem(EquipmentSlot.HEAD,inventory.getItem(3));

            player.getInventory().setItemInOffHand(inventory.getItem(8));
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        updatePlayerInventory((Player) event.getWhoClicked());

        if (InventoryAPI.playerInventories.containsKey(event.getWhoClicked().getName()) && event.getInventory().getSize() == 9) {
            Player player = Bukkit.getPlayer(InventoryAPI.playerInventories.get(event.getWhoClicked().getName()));
            if (player == null) {
                event.getWhoClicked().sendMessage(Component.text("Error: player not found").color(NamedTextColor.RED));
                return;
            }

            Inventory inventory = event.getView().getTopInventory();
            EntityEquipment equipment = player.getEquipment();
            equipment.setItem(EquipmentSlot.FEET,inventory.getItem(0));
            equipment.setItem(EquipmentSlot.LEGS,inventory.getItem(1));
            equipment.setItem(EquipmentSlot.CHEST,inventory.getItem(2));
            equipment.setItem(EquipmentSlot.HEAD,inventory.getItem(3));

            player.getInventory().setItemInOffHand(inventory.getItem(8));

        }
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        updatePlayerInventory((Player) event.getWhoClicked());

        if (InventoryAPI.playerInventories.containsKey(event.getWhoClicked().getName()) && event.getInventory().getSize() == 9) {
            Player player = Bukkit.getPlayer(InventoryAPI.playerInventories.get(event.getWhoClicked().getName()));
            if (player == null) {
                event.getWhoClicked().sendMessage(Component.text("Error: player not found").color(NamedTextColor.RED));
                return;
            }

            Inventory inventory = event.getView().getTopInventory();
            EntityEquipment equipment = player.getEquipment();
            equipment.setItem(EquipmentSlot.FEET,inventory.getItem(0));
            equipment.setItem(EquipmentSlot.LEGS,inventory.getItem(1));
            equipment.setItem(EquipmentSlot.CHEST,inventory.getItem(2));
            equipment.setItem(EquipmentSlot.HEAD,inventory.getItem(3));

            player.getInventory().setItemInOffHand(inventory.getItem(8));
        }
    }

    @EventHandler
    public void onInventory(InventoryCloseEvent event) {
        if (event.getView().getTitle().equalsIgnoreCase("Equipment") && event.getInventory().getSize() == 9) {
            InventoryAPI.playerInventories.remove(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Aktuelles Inventar speichern
        offlineInventories.put(playerUUID, player.getInventory().getContents());

        // Inventardaten auch in der Datei speichern
        inventoryData.set(playerUUID.toString(), player.getInventory().getContents());
        try {
            inventoryData.save(inventoryFile);
        } catch (IOException e) {
            ChatAPI.sendManagementMessage(Component.text("Error saving inventory for player " ).color(NamedTextColor.RED)
                    .append(Component.text(player.getName()).color(NamedTextColor.WHITE))
                    .append(Component.text(": " + e.getMessage()).color(NamedTextColor.RED)));
            NikeySystem.getPlugin().getLogger().severe("Error saving inventory for player " + player.getName() + ": " + e.getMessage());
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!InventoryAPI.hasOfflineEditing(event.getPlayer().getName()))return;
        String title = event.getView().getTitle();
        if (title.startsWith("OfflinePlayer: ")) {
            String playerName = title.replace("OfflinePlayer: ", "");
            OfflinePlayer target = Bukkit.getOfflinePlayer(playerName);

            Inventory inventory = event.getInventory();
            offlineInventories.put(target.getUniqueId(), inventory.getContents());
            inventoryData.set(target.getUniqueId().toString(), inventory.getContents());
            try {
                inventoryData.save(inventoryFile);
            } catch (IOException e) {
                ChatAPI.sendManagementMessage(Component.text("Error saving inventory for player " ).color(NamedTextColor.RED)
                        .append(Component.text(target.getName()).color(NamedTextColor.WHITE))
                        .append(Component.text(": " + e.getMessage()).color(NamedTextColor.RED)));
                NikeySystem.getPlugin().getLogger().severe("Error saving inventory for player " + target.getName() + ": " + e.getMessage());
                return;
            }
            event.getPlayer().sendMessage(Component.text(target.getName()+"'s ").color(NamedTextColor.WHITE)
                    .append(Component.text("inventory was saved").color(NamedTextColor.GREEN)));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!offlineInventories.containsKey(player.getUniqueId())) {
            UUID playerUUID = player.getUniqueId();

            // Aktuelles Inventar speichern
            offlineInventories.put(playerUUID, player.getInventory().getContents());

            // Inventardaten auch in der Datei speichern
            inventoryData.set(playerUUID.toString(), player.getInventory().getContents());
            try {
                inventoryData.save(inventoryFile);
            } catch (IOException e) {
                ChatAPI.sendManagementMessage(Component.text("Error saving inventory for player " ).color(NamedTextColor.RED)
                        .append(Component.text(player.getName()).color(NamedTextColor.WHITE))
                        .append(Component.text(": " + e.getMessage()).color(NamedTextColor.RED)));
                NikeySystem.getPlugin().getLogger().severe("Error saving inventory for player " + player.getName() + ": " + e.getMessage());
            }
            return;
        }
        if (player.getInventory().getContents() != offlineInventories.get(player.getUniqueId())) {
            ItemStack[] savedInventory = offlineInventories.get(player.getUniqueId());

            // Wiederherstellen des Inventars
            restorePlayerInventory(player, savedInventory);
        }
    }

    private void restorePlayerInventory(Player player, ItemStack[] savedInventory) {
        PlayerInventory inventory = player.getInventory();

        // Main inventory (Slots 0-35)
        ItemStack[] mainContents = new ItemStack[36];
        System.arraycopy(savedInventory, 0, mainContents, 0, Math.min(savedInventory.length, 36));
        inventory.setContents(mainContents);

        // Armor contents (Slots 36-39)
        if (savedInventory.length > 36) {
            ItemStack[] armorContents = new ItemStack[4];
            System.arraycopy(savedInventory, 36, armorContents, 0, Math.min(savedInventory.length - 36, 4));
            inventory.setArmorContents(armorContents);
        }

        // Offhand (Slot 40)
        if (savedInventory.length > 40 - 1) {
            inventory.setItemInOffHand(savedInventory[40]);
        }
    }

}
