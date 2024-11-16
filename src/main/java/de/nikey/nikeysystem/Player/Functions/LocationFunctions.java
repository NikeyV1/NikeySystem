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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.PluginDisableEvent;

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
            if (player.getWorld() != guardLocation.getWorld())return;
            double range = LocationAPI.guardRanges.getOrDefault(guardName, 10.0);
            if (player.getLocation().distance(guardLocation) < range) {
                if (!guardName.equals(LocationAPI.playerInGuardArea.get(player))) {
                    if (!LocationAPI.guardCreators.get(guardName).equals(player)){
                        if (PermissionAPI.isSystemUser(player))player.sendMessage("§8System: §eYou have entered the guarded area: §f" + guardName);
                        NikeySystem.getPlugin().getLogger().info(player.getName() + " entered guard area: " + guardName);
                        LocationAPI.playerInGuardArea.put(player, guardName); // Speichern, dass der Spieler in diesem Guard-Bereich ist
                        Player guardCreator = LocationAPI.guardCreators.get(guardName);
                        if (guardCreator != null && !guardCreator.equals(player)) { // Nur, wenn der Ersteller nicht der gleiche Spieler ist
                            guardCreator.sendMessage(ChatColor.WHITE + player.getName() + " §ehas entered guarded area: §f" + guardName);
                            guardCreator.playSound(Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER,1,1));
                        }
                    }
                }
                isInGuardArea = true;
                break;
            }
        }

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

}
