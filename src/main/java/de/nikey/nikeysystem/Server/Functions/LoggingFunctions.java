package de.nikey.nikeysystem.Server.Functions;

import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Server.API.LoggingAPI;
import de.nikey.nikeysystem.Server.History.HistoryElement;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.FluidLevelChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

import java.util.Date;
import java.util.UUID;

public class LoggingFunctions implements Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event){
        logBlockChange(LoggingAPI.LoggingType.BREAK,event.getPlayer().getName(),event.getBlock().getLocation(), event.getBlock().getType());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event){
        final Block block = event.getBlock();
        logBlockChange(LoggingAPI.LoggingType.PLACE,event.getPlayer().getName(),event.getBlock().getLocation(), event.getBlock().getType());
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBucketEmptied(PlayerBucketEmptyEvent event){
        Bukkit.getScheduler().scheduleSyncDelayedTask(NikeySystem.getPlugin(), () -> {
            final Block block = event.getBlock();
            logBlockChange(LoggingAPI.LoggingType.EMPTY_BUCKET,event.getPlayer().getName(),event.getBlock().getLocation(), event.getBlock().getType());
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBucketFilled(PlayerBucketFillEvent event){
        final Block block = event.getBlock();
        logBlockChange(LoggingAPI.LoggingType.FILL_BUCKET,event.getPlayer().getName(),event.getBlock().getLocation(), event.getBlock().getType());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityExplode(EntityExplodeEvent event){
        if (event.getEntity() instanceof TNTPrimed){
            TNTPrimed primed = (TNTPrimed) event.getEntity();
            for (Block block : event.blockList()) {
                logBlockChange(LoggingAPI.LoggingType.EXPLODE_TNT,primed.getSource() instanceof Player player ? player : null,event.getBlock().getLocation(), event.getBlock().getType());
            }
        } else if (event.getEntity() instanceof Creeper){
            Creeper creeper = (Creeper) event.getEntity();

            for (Block block : event.blockList()) {

            }
        } else if (event.getEntity() instanceof EnderCrystal ){
            EnderCrystal crystal = (EnderCrystal) event.getEntity();

            for (Block block : event.blockList()) {
            }
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockExplode(BlockExplodeEvent event){
        Block explodedBlock = event.getBlock();

        HistoryElement.Type type = HistoryElement.Type.EXPLODE_BLOCK;
        for (Block block : event.blockList()) {

        }
    }

    private void logBlockChange(LoggingAPI.LoggingType type, String playerName, Location location, Material blockType) {
        Date date = new Date();

        String logKey = location.getWorld().getName() + "," +
                location.getBlockX() + "," +
                location.getBlockY() + "," +
                location.getBlockZ();

        String logValue = String.format("%s by %s at %s (Block: %s)", type, playerName, date, blockType);

        LoggingAPI.logConfig.set(logKey, logValue);
    }
}
