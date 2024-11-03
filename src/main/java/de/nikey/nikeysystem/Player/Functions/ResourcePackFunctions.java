package de.nikey.nikeysystem.Player.Functions;

import de.nikey.nikeysystem.Player.API.ResourcePackAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

public class ResourcePackFunctions implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerResourcePackStatus(PlayerResourcePackStatusEvent event) {
        if (ResourcePackAPI.applying.containsKey(event.getPlayer())) {
            Player sender = ResourcePackAPI.applying.get(event.getPlayer());
            sender.sendActionBar(Component.text(event.getPlayer().getName()+"'s").color(NamedTextColor.WHITE)
                    .append(Component.text(" status: ").color(TextColor.color(59,38,182)))
                    .append(Component.text(event.getStatus().name()).color(NamedTextColor.DARK_GRAY)));
        }
    }
}
