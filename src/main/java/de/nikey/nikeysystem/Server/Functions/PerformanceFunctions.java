package de.nikey.nikeysystem.Server.Functions;

import de.nikey.nikeysystem.Server.API.PerformanceAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PerformanceFunctions implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/killOne") && PerformanceAPI.killOneRequests.containsKey(event.getPlayer().getUniqueId())) {
            EntityType type = EntityType.valueOf(event.getMessage().substring(8));
            if (type == EntityType.PLAYER){
                event.setCancelled(true);
                return;
            }

            for (Entity entity : event.getPlayer().getWorld().getEntities()) {
                if (entity.getType() == type) {
                    entity.remove();
                    break;
                }
            }

            event.getPlayer().sendActionBar(Component.text("Removed one ").color(TextColor.color(252, 3, 48)).append(Component.text(type.name()).color(NamedTextColor.WHITE)));
            event.setCancelled(true);
        }else if (event.getMessage().startsWith("/killAll") && PerformanceAPI.killAllRequests.containsKey(event.getPlayer().getUniqueId())){
            EntityType type = EntityType.valueOf(event.getMessage().substring(8));
            if (type == EntityType.PLAYER){
                event.setCancelled(true);
                return;
            }

            int killed = 0;
            for (Entity entity : event.getPlayer().getWorld().getEntities()) {
                if (entity.getType() == type) {
                    entity.remove();
                    killed++;
                }
            }

            event.getPlayer().sendActionBar(Component.text("Removed all ").append(Component.text(killed+" ")).color(TextColor.color(252, 3, 48)).append(Component.text(type.name()).color(NamedTextColor.WHITE)));
            event.setCancelled(true);
        }
    }

}
