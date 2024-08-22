package de.nikey.nikeysystem.Server.Distributor;

import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Server.API.SettingsAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SettingsDistributor {

    public static void settingsDistributor(Player player, String[] args) {
        Server server = Bukkit.getServer();
        if (args[3].equalsIgnoreCase("open")) {
            TextComponent acceptMessage = new TextComponent("§e[Continue]");
            acceptMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/continue"));
            acceptMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Continue opening server settings")));
            if (!SettingsAPI.settingsContinue.contains(player)) {
                player.spigot().sendMessage(ChatMessageType.valueOf("§cAre you sure you want to continue? "+acceptMessage));
                SettingsAPI.settingsContinue.add(player);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        SettingsAPI.settingsContinue.remove(player);
                    }
                }.runTaskLater(NikeySystem.getPlugin(),200);
            }
        }
    }
}
