package de.nikey.nikeysystem.Player.Functions;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.ChatAPI;
import de.nikey.nikeysystem.Player.API.InventoryAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.Player.Distributor.InventoryDistributor;
import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;

import java.io.IOException;
import java.util.UUID;

import static de.nikey.nikeysystem.Player.API.InventoryAPI.*;
import static de.nikey.nikeysystem.Player.Distributor.InventoryDistributor.openEditors;
import static de.nikey.nikeysystem.Player.Distributor.InventoryDistributor.updatePlayerInventory;

public class InventoryFunctions implements Listener {


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player viewer)) return;

        // Ensure the inventory is an equipment editor
        if (!event.getView().getTitle().startsWith("Edit Equipment: ")) return;

        if (event.getClickedInventory() == null) return;

        UUID targetUUID = openEditors.get(viewer.getUniqueId());
        if (targetUUID == null) return;

        Player target = Bukkit.getPlayer(targetUUID);
        if (target == null) {
            viewer.sendMessage("Target player is no longer online!");
            viewer.closeInventory();
            return;
        }

        ItemStack item = event.getInventory().getItem(event.getSlot());
        switch (event.getSlot()) {
            case 0 -> {
                target.getInventory().setHelmet(item);
                target.sendEquipmentChange(target, EquipmentSlot.HEAD, item);
            }
            case 1 -> {
                target.getInventory().setChestplate(item);
                target.sendEquipmentChange(target, EquipmentSlot.CHEST, item);
            }
            case 2 -> {
                target.getInventory().setLeggings(item);
                target.sendEquipmentChange(target,EquipmentSlot.LEGS, item);
            }
            case 3 -> {
                target.getInventory().setBoots(item);
                target.sendEquipmentChange(target,EquipmentSlot.FEET, item);
            }
            case 8 -> {
                target.getInventory().setItemInOffHand(item);
                target.sendEquipmentChange(target, EquipmentSlot.OFF_HAND, item);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        openEditors.values().remove(event.getPlayer().getUniqueId());
        openEditors.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        openEditors.remove(player.getUniqueId());
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
                    .append(Component.text(": " + e.getMessage()).color(NamedTextColor.RED)), ChatAPI.ManagementType.ERROR);
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
                        .append(Component.text(": " + e.getMessage()).color(NamedTextColor.RED)), ChatAPI.ManagementType.ERROR);
                NikeySystem.getPlugin().getLogger().severe("Error saving inventory for player " + target.getName() + ": " + e.getMessage());
                return;
            }

            NikeySystem.getPlugin().getConfig().set("offlineEdited." + target.getUniqueId(), true);
            NikeySystem.getPlugin().saveConfig();
            event.getPlayer().sendMessage(Component.text(target.getName()+"'s ").color(NamedTextColor.WHITE)
                    .append(Component.text("inventory was saved").color(NamedTextColor.GREEN)));
        }
    }



    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Prüfen, ob der Spieler offline bearbeitet wurde
        if (NikeySystem.getPlugin().getConfig().getBoolean("offlineEdited." + playerUUID, false)) {
            ItemStack[] savedInventory = offlineInventories.get(playerUUID);

            // Wiederherstellen des Inventars
            if (savedInventory != null) {
                restorePlayerInventory(player, savedInventory);
                NikeySystem.getPlugin().getConfig().set("offlineEdited." + playerUUID, null);
                NikeySystem.getPlugin().saveConfig();
                try {
                    inventoryData.save(inventoryFile);
                } catch (IOException e) {
                    ChatAPI.sendManagementMessage(Component.text("Error removing offline edit marker for player ").color(NamedTextColor.RED)
                            .append(Component.text(player.getName()).color(NamedTextColor.WHITE))
                            .append(Component.text(": " + e.getMessage()).color(NamedTextColor.RED)), ChatAPI.ManagementType.ERROR);
                    NikeySystem.getPlugin().getLogger().severe("Error removing offline edit marker for player " + player.getName() + ": " + e.getMessage());
                }
            }
        }else {
            offlineInventories.put(playerUUID, player.getInventory().getContents());

            // Inventardaten auch in der Datei speichern
            inventoryData.set(playerUUID.toString(), player.getInventory().getContents());
            try {
                inventoryData.save(inventoryFile);
            } catch (IOException e) {
                ChatAPI.sendManagementMessage(Component.text("Error saving inventory for player " ).color(NamedTextColor.RED)
                        .append(Component.text(player.getName()).color(NamedTextColor.WHITE))
                        .append(Component.text(": " + e.getMessage()).color(NamedTextColor.RED)), ChatAPI.ManagementType.ERROR);
                NikeySystem.getPlugin().getLogger().severe("Error saving inventory for player " + player.getName() + ": " + e.getMessage());
            }
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