package de.nikey.nikeysystem.Player.Settings;

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

public class HideSettings implements Listener {
    public static void openSettingsMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9, "§7Hide Settings");

        ItemStack option1 = new ItemStack(Material.DIAMOND_SWORD);
        ItemStack option2 = new ItemStack(Material.IRON_INGOT);
        ItemStack option3 = new ItemStack(Material.WHEAT_SEEDS);

        ItemMeta itemMeta = option1.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.displayName(Component.text("Toggle Mob targeting").color(NamedTextColor.DARK_AQUA));
        option1.setItemMeta(itemMeta);

        ItemMeta itemMeta1 = option2.getItemMeta();
        itemMeta1.displayName(Component.text("Toggle Item Pickup").color(NamedTextColor.DARK_AQUA));
        option2.setItemMeta(itemMeta1);

        ItemMeta itemMeta2 = option3.getItemMeta();
        itemMeta2.displayName(Component.text("Toggle Crop Trample").color(NamedTextColor.DARK_AQUA));
        option3.setItemMeta(itemMeta2);

        updateItemLore(option1,NikeySystem.getPlugin().getConfig().getBoolean("hide.settings." + player.getName() + ".mobtarget"));
        updateItemLore(option2,NikeySystem.getPlugin().getConfig().getBoolean("hide.settings." + player.getName() + ".itempickup"));
        updateItemLore(option3,NikeySystem.getPlugin().getConfig().getBoolean("hide.settings." + player.getName() + ".croptrample"));

        inventory.setItem(0,option1);
        inventory.setItem(2,option2);
        inventory.setItem(4,option3);

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
        if (!(event.getWhoClicked() instanceof Player))return;
        if (!event.getView().getTitle().equals("§7Hide Settings") || !PermissionAPI.isSystemUser((Player) event.getWhoClicked())) return;
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        Material type = clickedItem.getType();
        FileConfiguration config = NikeySystem.getPlugin().getConfig();
        Player player = (Player) event.getWhoClicked();
        if (type == Material.DIAMOND_SWORD) {
            boolean targeting = config.getBoolean("hide.settings." + player.getName() + ".mobtarget");
            if (targeting) {
                config.set("hide.settings." + player.getName() + ".mobtarget",false);
                NikeySystem.getPlugin().saveConfig();
            } else {
                config.set("hide.settings." + player.getName() + ".mobtarget", true);
                NikeySystem.getPlugin().saveConfig();
            }
            updateItemLore(clickedItem,config.getBoolean("hide.settings." + player.getName() + ".mobtarget"));
        }else if (type == Material.IRON_INGOT) {
            boolean picking = config.getBoolean("hide.settings." + player.getName() + ".itempickup");
            if (picking) {
                config.set("hide.settings." + player.getName() + ".itempickup",false);
                NikeySystem.getPlugin().saveConfig();
            }else {
                config.set("hide.settings." + player.getName() + ".itempickup",true);
                NikeySystem.getPlugin().saveConfig();
            }
            updateItemLore(clickedItem,config.getBoolean("hide.settings." + player.getName() + ".itempickup"));
        }else if (type == Material.WHEAT_SEEDS) {
            if (config.getBoolean("hide.settings." + player.getName() + ".croptrample")) {
                config.set("hide.settings." + player.getName() + ".croptrample",false);
                NikeySystem.getPlugin().saveConfig();
            }else {
                config.set("hide.settings." + player.getName() + ".croptrample",true);
                NikeySystem.getPlugin().saveConfig();
            }
            updateItemLore(clickedItem,config.getBoolean("hide.settings." + player.getName() + ".croptrample"));
        }
    }
}