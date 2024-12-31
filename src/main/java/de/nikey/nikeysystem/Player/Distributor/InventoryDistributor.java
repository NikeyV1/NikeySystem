package de.nikey.nikeysystem.Player.Distributor;

import de.nikey.nikeysystem.General.ShieldCause;
import de.nikey.nikeysystem.Player.API.HideAPI;
import de.nikey.nikeysystem.Player.API.InventoryAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.Player.Settings.InventorySettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static de.nikey.nikeysystem.Player.API.InventoryAPI.offlineInventories;

public class InventoryDistributor implements Listener {
    public static final Map<UUID, UUID> openEditors = new HashMap<>();

    public static void inventoryDistributor(Player sender, String[] args) {
        String cmd = args[3];
        if (cmd.isEmpty())return;

        if (cmd.equalsIgnoreCase("add")) {
            if (args.length == 6) {
                Player player = Bukkit.getPlayer(args[4]);
                if (player == null || !HideAPI.canSee(sender,player)){
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                addItem(sender,player, args[5], 1);
            }else if (args.length == 7) {
                Player player = Bukkit.getPlayer(args[4]);
                if (player == null|| !HideAPI.canSee(sender,player)){
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }

                int amount;
                try {
                    amount = Integer.parseInt(args[6]);
                } catch (NumberFormatException e) {
                    player.sendMessage("§cError: not a number");
                    return;
                }
                addItem(sender,player, args[5], amount);
            }
        }else if (cmd.equalsIgnoreCase("remove")) {
            if (args.length == 6) {
                Player player = Bukkit.getPlayer(args[4]);
                if (player == null|| !HideAPI.canSee(sender,player)){
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                removeItem(player,sender, args[5], 1000);
            }else if (args.length == 7) {
                Player player = Bukkit.getPlayer(args[4]);
                if (player == null|| !HideAPI.canSee(sender,player)){
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                int amount;
                try {
                    amount = Integer.parseInt(args[6]);
                } catch (NumberFormatException e) {
                    player.sendMessage("§cError: not a number");
                    return;
                }
                removeItem(player,sender, args[5], amount);
            }
        }else if (cmd.equalsIgnoreCase("openinv")) {
            if (args.length == 6) {
                Player player = Bukkit.getPlayer(args[4]);
                Player target = Bukkit.getPlayer(args[5]);

                if (player == null || !HideAPI.canSee(sender,player)) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }

                if (target == null) {
                    OfflinePlayer t = Bukkit.getOfflinePlayer(args[5]);

                    if (t.hasPlayedBefore() && HideAPI.canSee(sender.getName(),args[5])) {
                        openOfflineInventory(player,t);
                    }else {
                        InventoryType inventoryType = InventoryType.valueOf(args[5]);
                        if (inventoryType == InventoryType.PLAYER) {
                            sender.sendMessage("§cError: wrong usage");
                            return;
                        }
                        player.openInventory(Bukkit.createInventory(player, inventoryType));
                    }
                    return;
                }

                if (!HideAPI.canSee(sender,target)) {
                    sender.sendMessage("§cError: wrong usage");
                }

                openInventory(player,target);
            }else if (args.length == 5) {
                Player player = Bukkit.getPlayer(args[4]);
                if (player == null){
                    OfflinePlayer target = Bukkit.getOfflinePlayer(args[4]);

                    if (target.hasPlayedBefore() && HideAPI.canSee(sender.getName(),args[4])) {
                        openOfflineInventory(sender,target);
                    }else {
                        InventoryType inventoryType = InventoryType.valueOf(args[4]);
                        if (inventoryType == InventoryType.PLAYER) {
                            sender.sendMessage("§cError: wrong usage");
                            return;
                        }
                        sender.openInventory(Bukkit.createInventory(sender, inventoryType));
                    }
                    return;
                }

                if (!HideAPI.canSee(sender,player)){
                    InventoryType inventoryType = InventoryType.valueOf(args[4]);
                    if (inventoryType == InventoryType.PLAYER) {
                        sender.sendMessage("§cError: wrong usage");
                        return;
                    }
                    sender.openInventory(Bukkit.createInventory(sender, inventoryType));
                    return;
                }

                openInventory(sender,player);
            }
        }else if (cmd.equalsIgnoreCase("openec")) {
            if (args.length == 6) {
                Player player = Bukkit.getPlayer(args[4]);
                Player target = Bukkit.getPlayer(args[5]);
                if (player == null || target == null){
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }

                if (!HideAPI.canSee(sender,target) || !HideAPI.canSee(sender,player)) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                openEc(player,target);
            }else if (args.length == 5) {
                Player player = Bukkit.getPlayer(args[4]);
                if (player == null||!HideAPI.canSee(sender,player) ){
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                openEc(sender,player);
            }
        }else if (cmd.equalsIgnoreCase("openeq")) {
            if (args.length == 6) {
                Player player = Bukkit.getPlayer(args[4]);
                Player target = Bukkit.getPlayer(args[5]);
                if (player == null || target == null){
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                if (!HideAPI.canSee(sender,target) || !HideAPI.canSee(sender,player)) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                openEquipmentEditor(player,target);
            }else if (args.length == 5) {
                Player player = Bukkit.getPlayer(args[4]);
                if (player == null ){
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                if (!HideAPI.canSee(sender,player)) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                openEquipmentEditor(sender,player);
            }
        }else if (args[3].equalsIgnoreCase("help")) {
            sender.sendMessage("§7The path 'System/Player/Inventory' has following sub-paths: §fadd <PlayerName> <Item> [Amount], remove <PlayerName> <Item> [Amount], openinv [playername]<PlayerName>, openec [playername]<PlayerName>, openeq [playername]<PlayerName>.");
        }else if (args[3].equalsIgnoreCase("Settings")) {
            InventorySettings.openSettingsMenu(sender);
        }
    }

    private static void addItem(Player sender,Player player, String itemName, int amount) {
        Material material = Material.matchMaterial(itemName.toUpperCase());
        if (material == null) {
            sender.sendMessage("§cError: unknown item: " + itemName);
            return;
        }
        if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName(),ShieldCause.INVENTORY_ADD_ITEM)) {
            sender.sendMessage("§cError: missing permission");
            return;
        }
        if (player.getInventory().firstEmpty() == -1){
            sender.sendMessage("§cError: player has full inventory");
        }else {
            player.getInventory().addItem(new ItemStack(material, amount));
            sender.sendMessage("§aItem added: " + material.name().toLowerCase());
        }
    }

    private static void removeItem(Player player, Player sender, String itemName, int amount) {
        Material material = Material.matchMaterial(itemName.toUpperCase());
        if (material == null) {
            sender.sendMessage("§cError: unknown item: " + itemName);
            return;
        }
        if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName(),ShieldCause.INVENTORY_REMOVING_ITEM)) {
            sender.sendMessage("§cError: missing permission");
            return;
        }

        Inventory inventory = player.getInventory();
        int remainingAmount = amount;
        boolean itemFound = false;

        // Durchsuche das Inventar nach dem Item
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.getType() == material) {
                itemFound = true;
                int itemAmount = item.getAmount();

                if (itemAmount > remainingAmount) {
                    item.setAmount(itemAmount - remainingAmount);
                    remainingAmount = 0;
                    break;
                } else {
                    inventory.setItem(i, null);  // Entferne das Item aus dem Slot
                    remainingAmount -= itemAmount;
                }

                if (remainingAmount <= 0) {
                    break;
                }
            }
        }

        // Überprüfe, ob noch Menge übrig ist
        if (remainingAmount > 0) {
            sender.sendMessage("There were only " + (amount - remainingAmount) + "x " + material.name().toLowerCase() + " in the inventory");
        } else {
            player.sendMessage(amount + "x " + material.name().toLowerCase() + " removed");
        }

        if (!itemFound) {
            sender.sendMessage("§cError: inventory doesn't contain item");
        }
    }

    private static void openInventory(Player player, Player target) {
        if (!PermissionAPI.isAllowedToChange(player.getName(),target.getName(),ShieldCause.INVENTORY_OPEN_INVENTORY)) {
            player.sendMessage("§cError: missing permission");
            return;
        }
        player.openInventory(target.getInventory());
    }

    private static void openEc(Player player, Player target) {
        if (!PermissionAPI.isAllowedToChange(player.getName(),target.getName(),ShieldCause.INVENTORY_OPEN_ENDERCHEST)) {
            player.sendMessage("§cError: missing permission");
            return;
        }
        player.openInventory(target.getEnderChest());
    }

    private static void openEquipmentEditor(Player viewer, Player target) {

        if (!PermissionAPI.isAllowedToChange(viewer.getName(),target.getName(), ShieldCause.INVENTORY_OPEN_EQUIPMENT)) {
            viewer.sendMessage("§cError: missing permission");
            return;
        }

        Inventory equipmentInventory = Bukkit.createInventory(null, 9, Component.text("Edit Equipment: " + target.getName()));

        updateInventory(equipmentInventory, target);
        viewer.openInventory(equipmentInventory);

        // Map viewer to target for live updates
        openEditors.put(viewer.getUniqueId(), target.getUniqueId());
    }

    public static void updateInventory(Inventory inventory, Player target) {
        // Helmet
        inventory.setItem(0, target.getInventory().getHelmet() != null ? target.getInventory().getHelmet() : new ItemStack(Material.AIR));
        // Chestplate
        inventory.setItem(1, target.getInventory().getChestplate() != null ? target.getInventory().getChestplate() : new ItemStack(Material.AIR));
        // Leggings
        inventory.setItem(2, target.getInventory().getLeggings() != null ? target.getInventory().getLeggings() : new ItemStack(Material.AIR));
        // Boots
        inventory.setItem(3, target.getInventory().getBoots() != null ? target.getInventory().getBoots() : new ItemStack(Material.AIR));
        // Offhand
        inventory.setItem(8, target.getInventory().getItemInOffHand());
    }

    public static void openOfflineInventory(Player player, OfflinePlayer target) {
        if (!PermissionAPI.isAllowedToChange(player.getName(),target.getName(),ShieldCause.INVENTORY_OPEN_INVENTORY)) {
            player.sendMessage("§cError: missing permission");
            return;
        }

        if (target.isOnline()) {
            openInventory(player, (Player) target);
        } else {

            UUID targetUUID = target.getUniqueId();
            if (!offlineInventories.containsKey(targetUUID)) {
                player.sendMessage(ChatColor.RED + "No saved inventory found");
                return;
            }

            Inventory virtualInventory = Bukkit.createInventory(null, 45, Component.text("OfflinePlayer: " + target.getName()));
            virtualInventory.setContents(offlineInventories.get(targetUUID));
            ItemStack barrier = new ItemStack(Material.BARRIER);
            ItemMeta meta = barrier.getItemMeta();
            meta.displayName(Component.text("Unavailable Slot").color(NamedTextColor.RED));
            barrier.setItemMeta(meta);

            for (int i = 41; i < 45; i++) {
                virtualInventory.setItem(i, barrier);
            }
            player.openInventory(virtualInventory);
        }
    }
}