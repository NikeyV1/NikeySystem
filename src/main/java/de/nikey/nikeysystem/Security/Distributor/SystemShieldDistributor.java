package de.nikey.nikeysystem.Security.Distributor;

import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.HideAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.Security.API.SystemShieldAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static de.nikey.nikeysystem.Security.API.SystemShieldAPI.disableShieldRequest;
import static de.nikey.nikeysystem.Security.API.SystemShieldAPI.shieldRequest;

public class SystemShieldDistributor {

    public static void loadSystemShield() {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();
        SystemShieldAPI.getShieldUsers().clear();
        SystemShieldAPI.getShieldUsers().addAll(config.getStringList("security.SystemShieldUsers"));
    }

    public static void saveSystemShield() {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();
        config.set("security.SystemShieldUsers", new ArrayList<>(SystemShieldAPI.getShieldUsers()));
        NikeySystem.getPlugin().saveConfig();
    }

    public static void systemShieldDistributor(Player sender, String[] args) {
        String basePerm = "system.security.systemshield.";
        if (args[3].equalsIgnoreCase("enable")) {
            if (!PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "enable") && !PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "*")) return;
            if (args.length == 4) {
                if (!SystemShieldAPI.isShieldUser(sender.getName())) {
                    SystemShieldAPI.addShieldUser(sender.getName());
                    saveSystemShield();
                    sender.sendMessage(ChatColor.of("#42b6f5")+ "System Shield is now §aenabled§r!");
                }
            }else if (args.length == 5){
                Player player = Bukkit.getPlayer(args[4]);

                if (player == null || !player.isOnline() || !HideAPI.canSee(sender,player)) {
                    sender.sendMessage("§cError: player not found");
                    return;
                }

                if (!SystemShieldAPI.isShieldUser(player.getName())) {

                    if (PermissionAPI.isOwner(sender.getUniqueId())) {
                        SystemShieldAPI.addShieldUser(player.getName());
                        saveSystemShield();
                        player.sendMessage(ChatColor.of("#42b6f5")+ "System Shield is now §aenabled§r!");
                        sender.sendMessage(player.getName()+ "'s System Shield is now §aenabled§r!");
                        return;
                    }


                    TextComponent acceptMessage = new TextComponent("§f[Accept]");
                    acceptMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/accept"));
                    acceptMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Accept the shield")));

                    TextComponent declineMessage = new TextComponent(" §f[Decline]");
                    declineMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/decline"));
                    declineMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Decline the shield")));

                    acceptMessage.addExtra(declineMessage);

                    // Anfrage speichern
                    shieldRequest.put(player.getName(), sender.getName());
                    player.sendMessage(ChatColor.of("#eff542")+ sender.getName()+ " send you a System Shield §aactivate§r"+ChatColor.of("#eff542")+" request");
                    player.spigot().sendMessage(acceptMessage);
                    sender.sendMessage(ChatColor.of("#eff542")+ "System Shield §aactivate"+ChatColor.of("#eff542")+" request send!");
                }
            }else if (args.length == 6) {
                if (PermissionAPI.isOwner(sender.getUniqueId())) {
                    if (args[5].equalsIgnoreCase("Ask")) {
                        Player player = Bukkit.getPlayer(args[4]);
                        if (player == null || !player.isOnline()) {
                            sender.sendMessage("§cError: player not found");
                            return;
                        }

                        if (!SystemShieldAPI.isShieldUser(player.getName())) {

                            TextComponent acceptMessage = new TextComponent("§f[Accept]");
                            acceptMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/accept"));
                            acceptMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Accept the shield")));

                            TextComponent declineMessage = new TextComponent(" §f[Decline]");
                            declineMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/decline"));
                            declineMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Decline the shield")));

                            acceptMessage.addExtra(declineMessage);

                            // Anfrage speichern
                            shieldRequest.put(player.getName(), sender.getName());
                            player.sendMessage(ChatColor.of("#eff542")+ sender.getName()+ " send you a System Shield §aactivate§r "+ChatColor.of("#eff542")+" request");
                            player.spigot().sendMessage(acceptMessage);
                            sender.sendMessage(ChatColor.of("#eff542")+ "System Shield §aactivate"+ChatColor.of("#eff542")+" request send!");
                        }
                    }
                }
            }
        }else if (args[3].equalsIgnoreCase("disable")) {
            if (!PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "disable") && !PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "*")) return;
            if (args.length == 4) {
                if (SystemShieldAPI.isShieldUser(sender.getName())) {
                    SystemShieldAPI.removeShieldUser(sender.getName());
                    saveSystemShield();
                    sender.sendMessage(ChatColor.of("#42b6f5")+ "System Shield is now §cdisabled§r!");
                }
            }else if (args.length == 5){
                Player player = Bukkit.getPlayer(args[4]);
                if (player == null || !player.isOnline()|| !HideAPI.canSee(sender,player)) {
                    sender.sendMessage("§cError: player not found");
                    return;
                }

                if (SystemShieldAPI.isShieldUser(player.getName())) {

                    if (PermissionAPI.isOwner(sender.getUniqueId())) {
                        SystemShieldAPI.removeShieldUser(player.getName());
                        saveSystemShield();
                        player.sendMessage(ChatColor.of("#42b6f5")+ "System Shield is now §cremoved§r!");
                        sender.sendMessage(player.getName()+ "'s System Shield is now §cremoved§r!");
                        return;
                    }

                    TextComponent acceptMessage = new TextComponent("§f[Accept]");
                    acceptMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/accept"));
                    acceptMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Accept the shield removal")));

                    TextComponent declineMessage = new TextComponent(" §f[Decline]");
                    declineMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/decline"));
                    declineMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Decline the shield removal")));

                    acceptMessage.addExtra(declineMessage);

                    // Anfrage speichern
                    disableShieldRequest.put(player.getName(), sender.getName());
                    player.sendMessage(ChatColor.of("#eff542")+ sender.getName()+ " send you a System Shield §cremove§r"+ChatColor.of("#eff542")+" request");
                    player.spigot().sendMessage(acceptMessage);
                    sender.sendMessage(ChatColor.of("#eff542")+ "System Shield §cremove"+ChatColor.of("#eff542")+" request send!");
                }
            }else if (args.length == 6) {
                if (PermissionAPI.isOwner(sender.getUniqueId())) {
                    if (args[5].equalsIgnoreCase("Ask")) {
                        Player player = Bukkit.getPlayer(args[4]);
                        if (player == null || !player.isOnline()) {
                            sender.sendMessage("§cError: player not found");
                            return;
                        }

                        if (SystemShieldAPI.isShieldUser(player.getName())) {

                            TextComponent acceptMessage = new TextComponent("§f[Accept]");
                            acceptMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/accept"));
                            acceptMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Accept the shield removal")));

                            TextComponent declineMessage = new TextComponent(" §f[Decline]");
                            declineMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/decline"));
                            declineMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Decline the shield removal")));

                            acceptMessage.addExtra(declineMessage);

                            // Anfrage speichern
                            disableShieldRequest.put(player.getName(), sender.getName());
                            player.sendMessage(ChatColor.of("#eff542")+ sender.getName()+ " send you a System Shield §cremove§r"+ChatColor.of("#eff542")+" request");
                            player.spigot().sendMessage(acceptMessage);
                            sender.sendMessage(ChatColor.of("#eff542")+ "System Shield §cremove"+ChatColor.of("#eff542")+" request send!");
                        }
                    }
                }
            }
        }else if (args[3].equalsIgnoreCase("list")) {
            String playerName = args[4];
            List<String> messages = new ArrayList<>();
            if (playerName != null) {

                if (SystemShieldAPI.isShieldUser(playerName)) {
                    messages.add("§bSystem Shield enabled");
                }


                String message = "§7" + playerName + " has ";
                if (messages.isEmpty()) {
                    message += "System Shield disabled";
                } else {
                    message += String.join(", ", messages) + ".";
                }

                sender.sendMessage(message);
            }else {

                if (SystemShieldAPI.isShieldUser(sender.getName())) {
                    messages.add("§bSystem Shield enabled");
                }


                String message = "§7" + "You have ";
                if (messages.isEmpty()) {
                    message += "§bSystem Shield disabled";
                } else {
                    message += String.join(", ", messages) + ".";
                }

                sender.sendMessage(message);
            }
        }
    }
}
