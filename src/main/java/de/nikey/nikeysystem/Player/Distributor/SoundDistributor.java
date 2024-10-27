package de.nikey.nikeysystem.Player.Distributor;

import de.nikey.nikeysystem.Player.API.HideAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.Player.API.SoundAPI;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.net.URI;

public class SoundDistributor {
    public static void manageSound(Player sender, String[] args) {
        String cmd = args[3];
        if (cmd.isEmpty()) return;

        if (cmd.equalsIgnoreCase("play")) {
            if (args.length == 7) {
                Player player = Bukkit.getPlayer(args[4]);
                if (player == null || !HideAPI.canSee(sender.getName(), player.getName())) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName())) {
                    sender.sendMessage("§cError: missing permission");
                    return;
                }

                Sound myCustomSound = Sound.sound(Key.key(args[5], args[6]), Sound.Source.MASTER, 1f, 1.1f);
                player.playSound(myCustomSound);
            }else if (args.length == 8) {
                Player player = Bukkit.getPlayer(args[4]);
                if (player == null || !HideAPI.canSee(sender.getName(), player.getName())) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName())) {
                    sender.sendMessage("§cError: missing permission");
                    return;
                }

                Sound myCustomSound = Sound.sound(Key.key(args[5], args[6]), Sound.Source.MASTER, Float.parseFloat(args[7]), 1.1f);
                player.playSound(myCustomSound);
            }else if (args.length == 9) {
                Player player = Bukkit.getPlayer(args[4]);
                if (player == null || !HideAPI.canSee(sender.getName(), player.getName())) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName())) {
                    sender.sendMessage("§cError: missing permission");
                    return;
                }

                Sound myCustomSound = Sound.sound(Key.key(args[5], args[6]), Sound.Source.MASTER, Float.parseFloat(args[7]), Float.parseFloat(args[8]));
                player.playSound(myCustomSound);
            }
        }else if (cmd.equalsIgnoreCase("download")) {
            if (args.length == 5) {
                Player player = Bukkit.getPlayer(args[4]);
                if (player == null || !HideAPI.canSee(sender.getName(), player.getName())) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName())) {
                    sender.sendMessage("§cError: missing permission");
                    return;
                }
                ResourcePackInfo info = ResourcePackInfo.resourcePackInfo()
                        .uri(URI.create("https://download.mc-packs.net/pack/80f9257c73d34b36b998471120db7fe5a2d4f389.zip"))
                        .hash("80f9257c73d34b36b998471120db7fe5a2d4f389")
                        .build();
                SoundAPI.sendResourcePack(player,info);
            } else if (args.length == 7) {
                Player player = Bukkit.getPlayer(args[4]);
                if (player == null || !HideAPI.canSee(sender.getName(), player.getName())) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName())) {
                    sender.sendMessage("§cError: missing permission");
                    return;
                }
                ResourcePackInfo info = ResourcePackInfo.resourcePackInfo()
                        .uri(URI.create(args[5]))
                        .hash(args[6])
                        .build();
                SoundAPI.sendResourcePack(player,info);
            }
        }else if (cmd.equalsIgnoreCase("stopall")){
            Player player = Bukkit.getPlayer(args[4]);
            if (player == null || !HideAPI.canSee(sender.getName(), player.getName())) {
                sender.sendMessage("§cError: wrong usage");
                return;
            }
            if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName())) {
                sender.sendMessage("§cError: missing permission");
                return;
            }

            player.stopAllSounds();
        }
    }
}
