package de.nikey.nikeysystem.Server.Settings;

import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class LoggingSettings implements Listener {
    public static void openSettingsMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9, Component.text("Logging Settings").color(NamedTextColor.GRAY));

        ItemStack option1 = new ItemStack(Material.REDSTONE_TORCH);
        ItemStack option2 = new ItemStack(Material.CHEST);
        ItemStack option3 = new ItemStack(Material.TNT);
        ItemStack option4 = new ItemStack(Material.WATER_BUCKET);

        ItemMeta itemMeta = option1.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.displayName(Component.text("Toggle Logging").color(NamedTextColor.RED));
        option1.setItemMeta(itemMeta);

        ItemMeta itemMeta1 = option2.getItemMeta();
        itemMeta1.displayName(Component.text("Toggle Inventory Logging").color(NamedTextColor.DARK_AQUA));
        option2.setItemMeta(itemMeta1);

        ItemMeta itemMeta2 = option3.getItemMeta();
        itemMeta2.displayName(Component.text("Toggle Explosion Logging").color(NamedTextColor.DARK_AQUA));
        option3.setItemMeta(itemMeta2);

        ItemMeta itemMeta3 = option4.getItemMeta();
        itemMeta3.displayName(Component.text("Toggle Water Logging").color(NamedTextColor.DARK_AQUA));
        option4.setItemMeta(itemMeta3);

        updateItemLore(option1, !NikeySystem.getPlugin().getConfig().getBoolean("logging.settings.disabled"));
        updateItemLore(option2,NikeySystem.getPlugin().getConfig().getBoolean("logging.settings.inventory"));
        updateItemLore(option3,NikeySystem.getPlugin().getConfig().getBoolean("logging.settings.explosion"));
        updateItemLore(option4,NikeySystem.getPlugin().getConfig().getBoolean("logging.settings.water"));

        inventory.setItem(0,option1);
        inventory.setItem(2,option2);
        inventory.setItem(4,option3);
        inventory.setItem(6,option4);

        player.openInventory(inventory);
    }

    private static void updateItemLore(ItemStack item, boolean isSelected) {
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = new ArrayList<>();
        if (isSelected) {
            lore.add(Component.text("Enabled").color(NamedTextColor.GREEN));
        } else {
            lore.add(Component.text("Disabled").color(NamedTextColor.RED));
        }
        meta.lore(lore);
        item.setItemMeta(meta);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player))return;
        if (!event.getView().title().equals(Component.text("Logging Settings").color(NamedTextColor.GRAY)) || !PermissionAPI.isSystemUser(player)) return;
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        Material type = clickedItem.getType();
        FileConfiguration config = NikeySystem.getPlugin().getConfig();
        if (type == Material.REDSTONE_TORCH) {
            boolean targeting = config.getBoolean("logging.settings.disabled");
            if (!targeting) {
                config.set("logging.settings.disabled",false);
                NikeySystem.getPlugin().saveConfig();
            } else {
                config.set("logging.settings.disabled", true);
                NikeySystem.getPlugin().saveConfig();
            }
            updateItemLore(clickedItem,config.getBoolean("logging.settings.disabled"));
        }else if (type == Material.CHEST) {
            boolean picking = config.getBoolean("logging.settings.inventory");
            if (picking) {
                config.set("logging.settings.inventory",false);
                NikeySystem.getPlugin().saveConfig();
            }else {
                config.set("logging.settings.inventory",true);
                NikeySystem.getPlugin().saveConfig();
            }
            updateItemLore(clickedItem,config.getBoolean("logging.settings.inventory"));
        }else if (type == Material.TNT) {
            if (config.getBoolean("logging.settings.explosion")) {
                config.set("logging.settings.explosion",false);
                NikeySystem.getPlugin().saveConfig();
            }else {
                config.set("logging.settings.explosion",true);
                NikeySystem.getPlugin().saveConfig();
            }
            updateItemLore(clickedItem,config.getBoolean("logging.settings.explosion"));
        }else if (type == Material.WATER_BUCKET) {
            if (config.getBoolean("logging.settings.water")) {
                config.set("logging.settings.water",false);
                NikeySystem.getPlugin().saveConfig();
            }else {
                config.set("logging.settings.water",true);
                NikeySystem.getPlugin().saveConfig();
            }
            updateItemLore(clickedItem,config.getBoolean("logging.settings.water"));
        }
    }
}
