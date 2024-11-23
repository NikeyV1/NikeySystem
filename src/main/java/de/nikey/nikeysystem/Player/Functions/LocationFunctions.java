package de.nikey.nikeysystem.Player.Functions;

import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.LocationAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.PluginDisableEvent;

import java.util.List;
import java.util.Map;

public class LocationFunctions implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (LocationAPI.guardLocations.isEmpty()) return;

        boolean isInGuardArea = false;
        for (Map.Entry<String, Location> entry : LocationAPI.guardLocations.entrySet()) {
            String guardName = entry.getKey();
            Location guardLocation = entry.getValue();

            if (player.getWorld() != guardLocation.getWorld()) continue;

            double range = LocationAPI.guardRanges.getOrDefault(guardName, 12.0);
            if (player.getLocation().distance(guardLocation) < range) {
                if (!guardName.equals(LocationAPI.playerInGuardArea.get(player)) && LocationAPI.isPlayerExcluded(player,guardName)) {
                    if (!LocationAPI.guardCreators.get(guardName).equals(player.getName())) {
                        if (PermissionAPI.isSystemUser(player))
                            player.sendMessage("§8System: §eYou have entered the guarded area: §f" + guardName);
                        NikeySystem.getPlugin().getLogger().info(player.getName() + " entered guard area: " + guardName);

                        // Nachricht an den Ersteller des Guards
                        String f = LocationAPI.guardCreators.get(guardName);
                        Player guardCreator = Bukkit.getPlayer(f);
                        if (guardCreator != null && !guardCreator.equals(player)) {
                            guardCreator.sendMessage(ChatColor.WHITE + player.getName() + " §ehas entered guarded area: §f" + guardName);
                            guardCreator.playSound(Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1, 1));
                        }
                    }
                }

                // Aktualisieren, dass der Spieler sich im Guard-Bereich befindet
                LocationAPI.playerInGuardArea.put(player, guardName);
                isInGuardArea = true;
            }
        }

        // Wenn der Spieler keinen Guard-Bereich mehr betritt
        if (!isInGuardArea) {
            LocationAPI.playerInGuardArea.remove(player);
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        for (World world: Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Slime && !entity.isVisibleByDefault())entity.remove();
            }
        }
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (LocationAPI.guardLocations.isEmpty()) return;

        Location blockLocation = event.getBlock().getLocation();

        for (Map.Entry<String, Location> entry : LocationAPI.guardLocations.entrySet()) {
            String guardName = entry.getKey();
            Location guardLocation = entry.getValue();

            if (!blockLocation.getWorld().equals(guardLocation.getWorld())) continue;

            double range = LocationAPI.guardRanges.getOrDefault(guardName, 12.0);

            // Prüfen, ob der Block innerhalb des Guard-Radius liegt
            if (blockLocation.distance(guardLocation) <= range) {
                FileConfiguration config = NikeySystem.getPlugin().getConfig();

                boolean preventBlockBreak = config.getBoolean("location.settings." + guardName + ".preventBlockBreak", false);
                if (!preventBlockBreak) continue;

                if (LocationAPI.isPlayerExcluded(player, guardName)) {
                    event.setCancelled(true);
                    if (PermissionAPI.isSystemUser(player))player.sendMessage(ChatColor.RED + "You are not allowed to break blocks in the guard area: " + guardName);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (LocationAPI.guardLocations.isEmpty()) return;

        Location blockLocation = event.getBlock().getLocation();

        for (Map.Entry<String, Location> entry : LocationAPI.guardLocations.entrySet()) {
            String guardName = entry.getKey();
            Location guardLocation = entry.getValue();

            if (!blockLocation.getWorld().equals(guardLocation.getWorld())) continue;

            double range = LocationAPI.guardRanges.getOrDefault(guardName, 12.0);

            if (blockLocation.distance(guardLocation) <= range) {
                FileConfiguration config = NikeySystem.getPlugin().getConfig();

                boolean preventBlockPlace = config.getBoolean("location.settings." + guardName + ".preventBlockPlace", false);
                if (!preventBlockPlace) continue;

                if (LocationAPI.isPlayerExcluded(player, guardName)) {
                    event.setCancelled(true);
                    if (PermissionAPI.isSystemUser(player))player.sendMessage(ChatColor.RED + "You are not allowed to place blocks in the guard area: " + guardName);
                    return;
                }
            }
        }
    }


    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (LocationAPI.guardLocations.isEmpty()) return;

        Location explosionLocation = event.getLocation();

        for (Map.Entry<String, Location> entry : LocationAPI.guardLocations.entrySet()) {
            String guardName = entry.getKey();
            Location guardLocation = entry.getValue();

            if (!explosionLocation.getWorld().equals(guardLocation.getWorld())) continue;

            double range = LocationAPI.guardRanges.getOrDefault(guardName, 12.0);

            if (explosionLocation.distance(guardLocation) <= range) {
                FileConfiguration config = NikeySystem.getPlugin().getConfig();

                boolean preventTNTExplosions = config.getBoolean("location.settings." + guardName + ".preventTNTExplosions", false);
                if (preventTNTExplosions) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onRedstoneUsage(BlockRedstoneEvent event) {
        if (LocationAPI.guardLocations.isEmpty()) return;

        Location redstoneLocation = event.getBlock().getLocation();

        for (Map.Entry<String, Location> entry : LocationAPI.guardLocations.entrySet()) {
            String guardName = entry.getKey();
            Location guardLocation = entry.getValue();

            if (!redstoneLocation.getWorld().equals(guardLocation.getWorld())) continue;

            double range = LocationAPI.guardRanges.getOrDefault(guardName, 12.0);

            if (redstoneLocation.distance(guardLocation) <= range) {
                FileConfiguration config = NikeySystem.getPlugin().getConfig();

                boolean preventRedstoneUsage = config.getBoolean("location.settings." + guardName + ".preventRedstoneUsage", false);
                if (preventRedstoneUsage) {
                    event.setNewCurrent(event.getOldCurrent()); // Verhindert die Aktivierung
                }
            }
        }
    }

    @EventHandler
    public void onGuardEnter(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location toLocation = event.getTo();

        if (LocationAPI.guardLocations.isEmpty()) return;

        for (Map.Entry<String, Location> entry : LocationAPI.guardLocations.entrySet()) {
            String guardName = entry.getKey();
            Location guardLocation = entry.getValue();

            // Welt-Check
            if (!toLocation.getWorld().equals(guardLocation.getWorld())) continue;

            double range = LocationAPI.guardRanges.getOrDefault(guardName, 12.0);

            // Prüfen, ob der Spieler in den Bereich gelangt
            if (toLocation.distance(guardLocation) <= range) {
                FileConfiguration config = NikeySystem.getPlugin().getConfig();

                boolean preventEntry = config.getBoolean("location.settings." + guardName + ".preventEntry", false);
                if (!preventEntry) continue;

                // Exclude-Check: Spieler, die in der Exclude-Liste sind, dürfen nicht betreten
                if (LocationAPI.isPlayerExcluded(player, guardName)) {
                    event.setCancelled(true);
                    // Nachricht an den Spieler
                    if (PermissionAPI.isSystemUser(player))player.sendMessage(ChatColor.RED + "You are not allowed to enter the guard area: " + guardName);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        Location toLocation = event.getTo();

        for (Map.Entry<String, Location> entry : LocationAPI.guardLocations.entrySet()) {
            String guardName = entry.getKey();
            Location guardLocation = entry.getValue();

            // Welt-Check
            if (!toLocation.getWorld().equals(guardLocation.getWorld())) continue;

            double range = LocationAPI.guardRanges.getOrDefault(guardName, 12.0);

            // Prüfen, ob die Teleportation in den Bereich führt
            if (toLocation.distance(guardLocation) <= range) {
                FileConfiguration config = NikeySystem.getPlugin().getConfig();
                boolean preventEntry = config.getBoolean("location.settings." + guardName + ".preventEntry", false);

                if (preventEntry && LocationAPI.isPlayerExcluded(player, guardName)) {
                    event.setCancelled(true);

                    if (PermissionAPI.isSystemUser(player))player.sendMessage(ChatColor.RED + "You are not allowed to teleport into the guard area: " + guardName);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        // Ignorieren, wenn kein Block angeklickt wurde
        if (block == null) return;

        // Überprüfen, ob der Spieler sich in einem Guard-Bereich befindet
        for (Map.Entry<String, Location> entry : LocationAPI.guardLocations.entrySet()) {
            String guardName = entry.getKey();
            Location guardLocation = entry.getValue();

            if (!block.getWorld().equals(guardLocation.getWorld())) continue;

            double range = LocationAPI.guardRanges.getOrDefault(guardName, 10.0);
            if (block.getLocation().distance(guardLocation) <= range) {
                FileConfiguration config = NikeySystem.getPlugin().getConfig();
                boolean preventInteraction = config.getBoolean("location.settings." + guardName + ".preventInteraction", false);

                // Wenn Interaktionen verboten sind und der Spieler nicht berechtigt ist
                if (preventInteraction && LocationAPI.isPlayerExcluded(player, guardName)) {
                    event.setCancelled(true);

                    if (PermissionAPI.isSystemUser(player))player.sendMessage(ChatColor.RED + "You cannot interact with blocks in the guard area: " + guardName);
                    return;
                }
            }
        }
    }


    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        Location blockLocation = event.getInventory().getLocation();

        if (blockLocation == null) return;

        // Überprüfen, ob der Spieler sich in einem Guard-Bereich befindet
        for (Map.Entry<String, Location> entry : LocationAPI.guardLocations.entrySet()) {
            String guardName = entry.getKey();
            Location guardLocation = entry.getValue();

            if (!blockLocation.getWorld().equals(guardLocation.getWorld())) continue;

            double range = LocationAPI.guardRanges.getOrDefault(guardName, 12.0);
            if (blockLocation.distance(guardLocation) <= range) {
                FileConfiguration config = NikeySystem.getPlugin().getConfig();
                boolean preventInteraction = config.getBoolean("location.settings." + guardName + ".preventInteraction", false);

                if (preventInteraction && LocationAPI.isPlayerExcluded(player, guardName)) {
                    event.setCancelled(true);

                    if (PermissionAPI.isSystemUser(player))player.sendMessage(ChatColor.RED + "You cannot open inventories in the guard area: " + guardName);
                    return;
                }
            }
        }
    }

}
