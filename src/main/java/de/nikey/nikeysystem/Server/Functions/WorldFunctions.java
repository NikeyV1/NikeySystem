package de.nikey.nikeysystem.Server.Functions;

import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.Server.API.WorldAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;

public class WorldFunctions implements Listener {

    @EventHandler
    public void onChangedWorld(PlayerChangedWorldEvent event) {
        World world = event.getPlayer().getWorld();
        Player player = event.getPlayer();
        if (PermissionAPI.isOwner(player.getName()))return;
        if (WorldAPI.isCreatorOnly(world.getName())) {
            if (!WorldAPI.isWorldOwner(world.getName(),player.getName())){
                if (PermissionAPI.isSystemUser(player.getName())) player.sendMessage("§cYou are not allowed to enter this world!");
                player.teleport(event.getFrom().getSpawnLocation());
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (PermissionAPI.isOwner(player.getName()))return;
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getWorld() == to.getWorld()) {
            if (WorldAPI.isCreatorOnly(to.getWorld().getName())) {
                if (!WorldAPI.isWorldOwner(to.getWorld().getName(),player.getName())){
                    if (PermissionAPI.isSystemUser(player.getName())) player.sendMessage("§cYou are not allowed to enter this world!");
                    event.setCancelled(true);
                    World mainWorld = Bukkit.getWorld("world");
                    for (Player players : Bukkit.getOnlinePlayers()) {
                        if (mainWorld != null && WorldAPI.isAllowedOnWorld(mainWorld.getName(),players.getName())) {
                            players.teleport(mainWorld.getSpawnLocation());
                        } else {
                            players.kick();
                        }
                    }
                }
            }
        }else {
            if (WorldAPI.isCreatorOnly(to.getWorld().getName())) {
                if (!WorldAPI.isWorldOwner(to.getWorld().getName(),player.getName())){
                    if (PermissionAPI.isSystemUser(player.getName())) player.sendMessage("§cYou are not allowed to enter this world!");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (PermissionAPI.isOwner(player.getName()))return;
        World world = player.getWorld();
        if (WorldAPI.isCreatorOnly(world.getName())) {
            if (!WorldAPI.isWorldOwner(world.getName(),player.getName())){
                if (PermissionAPI.isSystemUser(player.getName())) player.sendMessage("§cYou are not allowed to enter this world!");
                World mainWorld = Bukkit.getWorld("world");
                for (Player players : Bukkit.getOnlinePlayers()) {
                    if (mainWorld != null && WorldAPI.isAllowedOnWorld(mainWorld.getName(),players.getName())) {
                        players.teleport(mainWorld.getSpawnLocation());
                    } else {
                        players.kick();
                    }
                }
            }
        }

    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (WorldAPI.tempWorld.containsKey(event.getPlayer().getName())) {
            unloadAndDeleteWorld(WorldAPI.tempWorld.get(event.getPlayer().getName()));
            WorldAPI.tempWorld.remove(event.getPlayer().getName());
        }
    }


    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        if (!WorldAPI.tempWorld.containsKey(event.getPlayer().getName()))return;
        if (event.getFrom() == WorldAPI.tempWorld.get(event.getPlayer().getName())) {
            unloadAndDeleteWorld(event.getFrom());
            WorldAPI.tempWorld.remove(event.getPlayer().getName());
        }
    }

    public static void deleteTemporaryWorlds() {
        File worldContainer = Bukkit.getWorldContainer();

        if (worldContainer.isDirectory()) {
            for (File worldFolder : worldContainer.listFiles()) {
                if (worldFolder.isDirectory() && worldFolder.getName().startsWith("temp_")) {
                    deleteDirectory(worldFolder);
                    NikeySystem.getPlugin().getLogger().info("Temp-Worlds deleted: " + worldFolder.getName());
                }
            }
        }
    }

    public static void deleteAndUnloadTemporaryWorlds() {
        // Über alle Welten iterieren
        for (World world : Bukkit.getWorlds()) {
            String worldName = world.getName();

            // Überprüfen, ob der Weltname mit "temp_" beginnt
            if (worldName.startsWith("temp_")) {
                // Welt entladen und löschen
                Bukkit.unloadWorld(world, false);  // false verhindert das Speichern von Änderungen

                // Verzeichnis der Welt löschen
                File worldFolder = world.getWorldFolder();
                deleteDirectory(worldFolder);

                NikeySystem.getPlugin().getLogger().info("Temp-Worlds deleted: " + worldName);
            }
        }
    }

    private void unloadAndDeleteWorld(World world) {
        String worldName = world.getName();
        Bukkit.unloadWorld(world, false);

        File worldFolder = new File(Bukkit.getWorldContainer(), worldName);
        deleteDirectory(worldFolder);
    }

    private static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
}
