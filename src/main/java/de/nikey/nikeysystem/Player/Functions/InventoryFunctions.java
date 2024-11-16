package de.nikey.nikeysystem.Player.Functions;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.ChatAPI;
import de.nikey.nikeysystem.Player.API.InventoryAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;

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
}
