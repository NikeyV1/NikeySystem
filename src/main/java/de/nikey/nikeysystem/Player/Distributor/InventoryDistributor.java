package de.nikey.nikeysystem.Player.Distributor;

import de.nikey.nikeysystem.Player.API.HideAPI;
import de.nikey.nikeysystem.Player.API.InventoryAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class InventoryDistributor {

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
                if (player == null || target == null){
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }

                if (!HideAPI.canSee(sender,target) || !HideAPI.canSee(sender,player)) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                openInventory(player,target);
            }else if (args.length == 5) {
                Player player = Bukkit.getPlayer(args[4]);
                if (player == null ||!HideAPI.canSee(sender,player)){
                    sender.sendMessage("§cError: wrong usage");
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
        }
    }

    private static void addItem(Player sender,Player player, String itemName, int amount) {
        Material material = Material.matchMaterial(itemName.toUpperCase());
        if (material == null) {
            sender.sendMessage("§cError: unknown item: " + itemName);
            return;
        }
        if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName())) {
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
        if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName())) {
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
        if (!PermissionAPI.isAllowedToChange(player.getName(),target.getName())) {
            player.sendMessage("§cError: missing permission");
            return;
        }
        player.openInventory(target.getInventory());
    }

    private static void openEc(Player player, Player target) {
        if (!PermissionAPI.isAllowedToChange(player.getName(),target.getName())) {
            player.sendMessage("§cError: missing permission");
            return;
        }
        player.openInventory(target.getEnderChest());
    }

    private static void openEq(Player player, Player target) {
        if (!PermissionAPI.isAllowedToChange(player.getName(),target.getName())) {
            player.sendMessage("§cError: missing permission");
            return;
        }

        EntityEquipment equipment = target.getEquipment();
        assert equipment != null;
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
}
