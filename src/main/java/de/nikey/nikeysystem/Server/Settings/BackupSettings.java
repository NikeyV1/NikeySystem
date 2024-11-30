package de.nikey.nikeysystem.Server.Settings;

import de.nikey.nikeysystem.NikeySystem;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;



public class BackupSettings implements Listener {
    public void openSettingsMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9, "§7Backup Settings");

        FileConfiguration config = NikeySystem.getPlugin().getConfig();

        // Optionen: AutoBackup, Backup-Intervall, Löschzeit
        ItemStack autoBackup = createMenuItem(Material.CLOCK, "Toggle AutoBackup",
                config.getBoolean("backup.auto_backup") ? "Enabled" : "Disabled");
        ItemStack interval = createMenuItem(Material.PAPER, "Set Backup Interval",
                config.getInt("backup.backup_interval") + " minutes");
        ItemStack deleteTime = createMenuItem(Material.BARRIER, "Set Delete Time",
                config.getInt("backup.delete_time") + " days");

        // Items im Menü platzieren
        inventory.setItem(0, autoBackup);
        inventory.setItem(2, interval);
        inventory.setItem(4, deleteTime);

        player.openInventory(inventory);
    }

    private ItemStack createMenuItem(Material material, String name, String description) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name).color(NamedTextColor.AQUA));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(description).color(NamedTextColor.GRAY));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if (!event.getView().getTitle().equals("§7Backup Settings")) return;
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        NikeySystem plugin = NikeySystem.getPlugin();
        FileConfiguration config = plugin.getConfig();
        switch (clickedItem.getType()) {
            case CLOCK -> {
                boolean autoBackup = config.getBoolean("settings.auto_backup");
                config.set("settings.auto_backup", !autoBackup);
                plugin.saveConfig();
                player.sendMessage("§aAutoBackup " + (autoBackup ? "disabled" : "enabled") + "!");
                openBackupMenu(player);
            }
            case PAPER -> {
                player.sendMessage("§ePlease use: /backup setAutoInterval [minutes]");
                player.closeInventory();
            }
            case CHEST -> {
                player.sendMessage("§eCreating backup...");
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    plugin.createBackup();
                    player.sendMessage("§aBackup created successfully!");
                });
            }
            case BARRIER -> {
                player.sendMessage("§ePlease use: /backup delete [name]");
                player.closeInventory();
            }
        }
    }
}
