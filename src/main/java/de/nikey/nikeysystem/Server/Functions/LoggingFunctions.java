package de.nikey.nikeysystem.Server.Functions;

import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Server.API.LoggingAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.*;

public class LoggingFunctions implements Listener {

    private final Map<UUID, UUID> igniters = new HashMap<>();
    private final Map<Block, UUID> clickedBlocks = new HashMap<>();
    private final Map<UUID, ItemStack[]> inventorySnapshots = new HashMap<>();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event){
        logBlockChange(LoggingAPI.LoggingType.BREAK,event.getPlayer().getName(),event.getBlock().getLocation(), event.getBlock().getType());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event){
        logBlockChange(LoggingAPI.LoggingType.PLACE,event.getPlayer().getName(),event.getBlock().getLocation(), event.getBlock().getType());
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBucketEmptied(PlayerBucketEmptyEvent event){
        Bukkit.getScheduler().scheduleSyncDelayedTask(NikeySystem.getPlugin(), () -> {
            logBlockChange(LoggingAPI.LoggingType.EMPTY_BUCKET,event.getPlayer().getName(),event.getBlock().getLocation(), event.getBlock().getType());
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBucketFilled(PlayerBucketFillEvent event){
        logBlockChange(LoggingAPI.LoggingType.FILL_BUCKET,event.getPlayer().getName(),event.getBlock().getLocation(), event.getBlock().getType());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityExplode(EntityExplodeEvent event){
        if (event.getEntity() instanceof TNTPrimed primed){
            for (Block block : event.blockList()) {
                logBlockChange(LoggingAPI.LoggingType.EXPLODE_TNT,primed.getSource() instanceof Player player ? player.getName() : "Unknown",block.getLocation(), block.getType());
            }
        } else if (event.getEntity() instanceof Creeper creeper){
            UUID ignited = igniters.remove(creeper.getUniqueId());
            for (Block block : event.blockList()) {
                logBlockChange(LoggingAPI.LoggingType.EXPLODE_CREEPER,Bukkit.getOfflinePlayer(ignited).getName(),block.getLocation(), block.getType());
            }
        } else if (event.getEntity() instanceof EnderCrystal crystal){
            UUID ignited = igniters.remove(crystal.getUniqueId());
            for (Block block : event.blockList()) {
                logBlockChange(LoggingAPI.LoggingType.EXPLODE_END_CRYSTAL,Bukkit.getOfflinePlayer(ignited).getName(),block.getLocation(), block.getType());
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockExplode(BlockExplodeEvent event){
        Block explodedBlock = event.getBlock();

        UUID accountable = clickedBlocks.remove(explodedBlock);
        if (accountable == null) {
            for (Block block : event.blockList()) {
                logBlockChange(LoggingAPI.LoggingType.EXPLODE_BLOCK,"Unknown",block.getLocation(), block.getType());
            }
            return;
        }
        for (Block block : event.blockList()) {
            logBlockChange(LoggingAPI.LoggingType.EXPLODE_BLOCK,Bukkit.getOfflinePlayer(accountable).getName(),block.getLocation(), block.getType());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        inventorySnapshots.put(player.getUniqueId(), cloneInventory(event.getInventory()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        UUID playerId = player.getUniqueId();
        Inventory inventory = event.getInventory();
        ItemStack[] previousItems = inventorySnapshots.getOrDefault(playerId, new ItemStack[0]);

        // Compare inventory changes
        compareAndLogChanges(player.getName(), previousItems, inventory.getContents(), inventory.getType().name(), player.getLocation());

        // Clean up snapshot
        inventorySnapshots.remove(playerId);
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            if (event.getEntity() instanceof EnderCrystal enderCrystal) {
                igniters.put(enderCrystal.getUniqueId(), player.getUniqueId());
            }
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked() instanceof Creeper creeper) {
            if (event.getPlayer().getEquipment().getItemInMainHand().getType() == Material.FLINT_AND_STEEL ||
                    event.getPlayer().getEquipment().getItemInMainHand().getType() == Material.FIRE_CHARGE ||
                    event.getPlayer().getEquipment().getItemInOffHand().getType() == Material.FLINT_AND_STEEL ||
                    event.getPlayer().getEquipment().getItemInOffHand().getType() == Material.FIRE_CHARGE ) {
                igniters.put(creeper.getUniqueId(), event.getPlayer().getUniqueId());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event){
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null)return;
            if (clickedBlock.getType() == Material.RESPAWN_ANCHOR) {
                clickedBlocks.put(clickedBlock, event.getPlayer().getUniqueId());
            } else if (clickedBlock.getBlockData() instanceof Bed bed){
                BlockFace facing = bed.getFacing();
                if (bed.getPart() == Bed.Part.FOOT){
                    Block becBlock = new Location(clickedBlock.getWorld(), clickedBlock.getX() + (facing.getModX()), clickedBlock.getY() + (facing.getModY()), clickedBlock.getZ() + (facing.getModZ())).getBlock();
                    if (becBlock.getBlockData() instanceof Bed){
                        clickedBlocks.put(becBlock,event.getPlayer().getUniqueId());
                    }
                } else {
                    clickedBlocks.put(clickedBlock, event.getPlayer().getUniqueId());
                }
            }
        }
    }

    private ItemStack[] cloneInventory(Inventory inventory) {
        ItemStack[] items = inventory.getContents();
        ItemStack[] clone = new ItemStack[items.length];
        for (int i = 0; i < items.length; i++) {
            clone[i] = (items[i] != null) ? items[i].clone() : null;
        }
        return clone;
    }

    private void compareAndLogChanges(String playerName, ItemStack[] oldItems, ItemStack[] newItems, String inventoryType, Location location) {
        Map<Material, Integer> oldItemMap = countItems(oldItems);
        Map<Material, Integer> newItemMap = countItems(newItems);

        for (Material material : newItemMap.keySet()) {
            int oldCount = oldItemMap.getOrDefault(material, 0);
            int newCount = newItemMap.get(material);

            if (newCount > oldCount) {
                int amountAdded = newCount - oldCount;
                logInventoryChange(playerName, "put", new ItemStack(material, amountAdded), inventoryType, location);
            } else if (newCount < oldCount) {
                int amountRemoved = oldCount - newCount;
                logInventoryChange(playerName, "took", new ItemStack(material, amountRemoved), inventoryType, location);
            }
        }
    }

    private Map<Material, Integer> countItems(ItemStack[] items) {
        Map<Material, Integer> itemMap = new HashMap<>();
        for (ItemStack item : items) {
            if (item != null && item.getType() != Material.AIR) {
                itemMap.put(item.getType(), itemMap.getOrDefault(item.getType(), 0) + item.getAmount());
            }
        }
        return itemMap;
    }


    private void logBlockChange(LoggingAPI.LoggingType type, String playerName, Location location, Material blockType) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM-HH:mm");
        String formattedDate = dateFormat.format(new Date());

        if (type == LoggingAPI.LoggingType.EMPTY_BUCKET || type == LoggingAPI.LoggingType.FILL_BUCKET) {
            if (location.getBlock().getBlockData() instanceof Waterlogged && ((Waterlogged) location.getBlock().getBlockData()).isWaterlogged()) {
                blockType = Material.WATER;
            }
        }

        String logKey = location.getWorld().getName() + "," +
                location.getBlockX() + "," +
                location.getBlockY() + "," +
                location.getBlockZ();

        String logValue = String.format(
                "Block %s was %s by %s at %s",
                blockType.name(),     // Blockname
                type.getName(),       // Action
                playerName,           // Playername
                formattedDate         // Zeit
        );

        List<String> stringList = LoggingAPI.logConfig.getStringList(logKey);
        stringList.add(logValue);
        LoggingAPI.logConfig.set(logKey, stringList);
    }

    private static void logInventoryChange(String playerName, String action, ItemStack item, String inventoryType, Location location) {
        if (location == null) return; // Sicherheit: Falls das Inventar keine Location hat (z. B. Crafting-Tisch)

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM HH:mm");
        String formattedDate = dateFormat.format(new Date());

        String itemName = item.getType().name(); // Name des Items
        int itemAmount = item.getAmount(); // Menge des Items

        String logKey = location.getWorld().getName() + "," +
                location.getBlockX() + "," +
                location.getBlockY() + "," +
                location.getBlockZ();

        String logValue = String.format(
                "%s %s %d %s %s %s %s at %s",
                playerName,       // Spielername
                action,           // Aktion (put/took)
                itemAmount,       // Anzahl der Items
                itemName,         // Name des Items
                action.equals("put") ? "in" : "from", // "in" oder "from"
                inventoryType,    // Typ des Inventars
                "inventory",      // Zusatz für bessere Lesbarkeit
                formattedDate     // Zeitstempel
        );

        // Logging in die Konfiguration
        List<String> stringList = LoggingAPI.logConfig.getStringList(logKey);
        stringList.add(logValue);
        LoggingAPI.logConfig.set(logKey, stringList);
    }


}
