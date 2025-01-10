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

public class BackupSettings implements Listener {
    public static void openSettingsMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9, Component.text("Backup Settings").color(NamedTextColor.GRAY));

        ItemStack option1 = new ItemStack(Material.BIRCH_SIGN);

        ItemMeta itemMeta = option1.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.displayName(Component.text("Toggle Show Path").color(NamedTextColor.RED));
        option1.setItemMeta(itemMeta);

        updateItemLore(option1, NikeySystem.getPlugin().getConfig().getBoolean("backup.settings." + player.getName() + ".showpath"));

        inventory.setItem(0,option1);

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
        if (!event.getView().title().equals(Component.text("Backup Settings").color(NamedTextColor.GRAY)) || !PermissionAPI.isSystemUser(player)) return;
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        Material type = clickedItem.getType();
        FileConfiguration config = NikeySystem.getPlugin().getConfig();
        if (type == Material.BIRCH_SIGN) {
            boolean targeting = config.getBoolean("backup.settings." + player.getName() + ".showpath");
            if (targeting) {
                config.set("backup.settings." + player.getName() + ".showpath",false);
                NikeySystem.getPlugin().saveConfig();
            } else {
                config.set("backup.settings." + player.getName() + ".showpath", true);
                NikeySystem.getPlugin().saveConfig();
            }
            updateItemLore(clickedItem,config.getBoolean("backup.settings." + player.getName() + ".showpath"));
        }
    }
}
