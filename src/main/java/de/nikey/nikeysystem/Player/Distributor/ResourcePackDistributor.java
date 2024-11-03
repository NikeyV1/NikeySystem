package de.nikey.nikeysystem.Player.Distributor;

import de.nikey.nikeysystem.General.ShieldCause;
import de.nikey.nikeysystem.Player.API.HideAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.Player.API.ResourcePackAPI;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.net.URI;
import java.util.UUID;

public class ResourcePackDistributor {
    public static void ResourcePackManager(Player sender, String[] args) {
        String cmd = args[3];
        if (cmd.isEmpty()) return;

        if (cmd.equalsIgnoreCase("download")) {
            if (args.length == 5) {
                Player player = Bukkit.getPlayer(args[4]);
                if (player == null || !HideAPI.canSee(sender.getName(), player.getName())) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName(),ShieldCause.RESOURCE_PACK_DOWNLOAD)) {
                    sender.sendMessage("§cError: missing permission");
                    return;
                }
                ResourcePackInfo info = ResourcePackInfo.resourcePackInfo()
                        .uri(URI.create(" https://download.mc-packs.net/pack/7d6ca4c1906ec46e3168459065788165f6c6c9ec.zip"))
                        .hash("7d6ca4c1906ec46e3168459065788165f6c6c9ec")
                        .build();
                ResourcePackAPI.sendResourcePack(player,info);
                ResourcePackAPI.applying.put(player,sender);
            } else if (args.length == 7) {
                Player player = Bukkit.getPlayer(args[4]);
                if (player == null || !HideAPI.canSee(sender.getName(), player.getName())) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName(),ShieldCause.RESOURCE_PACK_DOWNLOAD)) {
                    sender.sendMessage("§cError: missing permission");
                    return;
                }
                ResourcePackInfo info = ResourcePackInfo.resourcePackInfo()
                        .uri(URI.create(args[5]))
                        .hash(args[6])
                        .build();
                ResourcePackAPI.sendResourcePack(player,info);
                ResourcePackAPI.applying.put(player,sender);
                sender.sendActionBar(Component.text("Downloading resource pack for ").color(TextColor.color(59,38,182))
                        .append(Component.text(player.getName()).color(NamedTextColor.WHITE)));
            }
        }else if (cmd.equalsIgnoreCase("clear")) {
            if (args.length == 4){
                sender.clearResourcePacks();
            }else if (args.length == 5) {
                Player player = Bukkit.getPlayer(args[4]);
                if (player == null || !HideAPI.canSee(sender.getName(), player.getName())) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName(),ShieldCause.RESOURCE_PACK_CLEAR)) {
                    sender.sendMessage("§cError: missing permission");
                    return;
                }

                player.clearResourcePacks();
                sender.sendActionBar(Component.text("Clearing resource packs for ").color(TextColor.color(59,38,182))
                        .append(Component.text(player.getName()).color(NamedTextColor.WHITE)));
            }
        }else if (cmd.equalsIgnoreCase("remove")) {
            if (args.length == 5) {
                UUID id = UUID.fromString(args[4]);
                sender.removeResourcePack(id);

            }else if (args.length == 6) {
                Player player = Bukkit.getPlayer(args[4]);
                if (player == null || !HideAPI.canSee(sender.getName(), player.getName())) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName(), ShieldCause.RESOURCE_PACK_REMOVE)) {
                    sender.sendMessage("§cError: missing permission");
                    return;
                }

                UUID id = UUID.fromString(args[4]);
                sender.removeResourcePack(id);
                sender.sendActionBar(Component.text("Removing resource pack '").color(TextColor.color(59,38,182))
                        .append(Component.text(args[4]).color(NamedTextColor.DARK_GRAY))
                        .append(Component.text("' for ").color(TextColor.color(59,38,182)))
                        .append(Component.text(player.getName()).color(NamedTextColor.WHITE)));
            }
        }
    }
}
