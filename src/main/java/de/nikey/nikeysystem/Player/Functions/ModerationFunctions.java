package de.nikey.nikeysystem.Player.Functions;

import de.nikey.nikeysystem.Player.API.ModerationAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ModerationFunctions implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (ModerationAPI.isFrozen(event.getPlayer().getUniqueId())){
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (ModerationAPI.isFrozen(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }
}
