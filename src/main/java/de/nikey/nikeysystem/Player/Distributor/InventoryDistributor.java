package de.nikey.nikeysystem.Player.Distributor;

import de.nikey.nikeysystem.General.ShieldCause;
import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.HideAPI;
import de.nikey.nikeysystem.Player.API.InventoryAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.Player.Settings.HideSettings;
import de.nikey.nikeysystem.Player.Settings.InventorySettings;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static de.nikey.nikeysystem.Player.API.InventoryAPI.offlineInventories;

public class InventoryDistributor implements Listener {

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
                openEq(player,target);
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
                openEq(sender,player);
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
            sender.sendMessage("§aItem added: " + material.name());
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
            sender.sendMessage("There were only " + (amount - remainingAmount) + "x " + material.name() + " in the inventory");
        } else {
            player.sendMessage(amount + "x " + material.name() + " removed");
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

    private static void openEq(Player player, Player target) {
        if (!PermissionAPI.isAllowedToChange(player.getName(),target.getName(), ShieldCause.INVENTORY_OPEN_EQUIPMENT)) {
            player.sendMessage("§cError: missing permission");
            return;
        }

        EntityEquipment equipment = target.getEquipment();
        ItemStack[] armorContents = equipment.getArmorContents();
        Inventory inventory = Bukkit.createInventory(null, 9, "Equipment");
        for (int i = 0; i < 4; i++) {
            inventory.setItem(i,armorContents[i]);
        }
        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        inventory.setItem(4,glass);
        inventory.setItem(5,glass);
        inventory.setItem(6,glass);
        inventory.setItem(7,glass);
        inventory.setItem(8,target.getInventory().getItemInOffHand());

        InventoryAPI.playerInventories.put(player.getName(),target.getName());
        player.openInventory(inventory);
    }

    public static void updatePlayerInventory(Player target) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player != target && InventoryAPI.playerInventories.containsKey(player.getName()) && InventoryAPI.playerInventories.containsValue(target.getName())) {
                openEq(player, target);
            }
        }
    }

    public static void openOfflineInventory(Player player, OfflinePlayer target) {
        if (!PermissionAPI.isAllowedToChange(player.getName(),target.getName(),ShieldCause.INVENTORY_OPEN_INVENTORY)) {
            player.sendMessage("§cError: missing permission");
            return;
        }

        if (target.isOnline()) {
            openInventory(player, (Player) target);
            return;
        } else {

            UUID targetUUID = target.getUniqueId();
            if (!offlineInventories.containsKey(targetUUID)) {
                player.sendMessage(ChatColor.RED + "No saved inventory found");
                return;
            }

            Inventory virtualInventory = Bukkit.createInventory(null, 45, Component.text("Player"));
            virtualInventory.setContents(offlineInventories.get(targetUUID));
            player.openInventory(virtualInventory);
        }
    }

}
