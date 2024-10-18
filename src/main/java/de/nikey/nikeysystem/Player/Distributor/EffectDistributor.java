package de.nikey.nikeysystem.Player.Distributor;

import de.nikey.nikeysystem.Player.API.HideAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EffectDistributor {

    public static void effectDistributor(Player player, String[] args) {
        // Bestimme das Ziel: Spieler oder die Entität in Sichtlinie
        LivingEntity target = null;

        if (args[4].equalsIgnoreCase("TargetEntity")) {
            target = getTargetEntity(player);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Error: No entity in sight!");
                return;
            }

            if (target instanceof Player && !HideAPI.canSee(player, (Player) target)) {
                player.sendMessage(ChatColor.RED + "Error: No entity in sight!");
                return;
            }
        } else {
            Player targetPlayer = Bukkit.getPlayer(args[4]);
            if (targetPlayer != null && HideAPI.canSee(player,targetPlayer) && PermissionAPI.isAllowedToChange(player.getName(), targetPlayer.getName())) {
                target = targetPlayer;
            }
        }

        if (target == null) {
            player.sendMessage(ChatColor.RED + "Error: Target not found!");
            return;
        }

        // Handling different commands
        if (args[3].equalsIgnoreCase("give")) {
            if (args.length >= 6) {
                PotionEffectType effectType = PotionEffectType.getByName(args[5]);
                if (effectType == null) {
                    player.sendMessage(ChatColor.RED + "Error: Invalid effect type!");
                    return;
                }

                int duration = 600; // Default 30 seconds (600 ticks)
                int amplifier = 1;   // Default amplifier level 1

                if (args.length >= 7) {
                    try {
                        duration = Integer.parseInt(args[6]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Error: Duration must be a number!");
                        return;
                    }
                }

                if (args.length == 8) {
                    try {
                        amplifier = Integer.parseInt(args[7]);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Error: Amplifier must be a number!");
                        return;
                    }
                }

                target.addPotionEffect(new PotionEffect(effectType, duration, amplifier));
                player.sendMessage(ChatColor.DARK_GREEN + "Effect " + ChatColor.YELLOW + effectType.getName() + ChatColor.DARK_GREEN
                        + " added to " + ChatColor.WHITE + target.getName() + ChatColor.DARK_GREEN + " for " + ChatColor.YELLOW + duration + " ticks"
                        + ChatColor.DARK_GREEN + " with amplifier " + ChatColor.YELLOW + amplifier + ChatColor.DARK_GREEN + ".");
            } else {
                player.sendMessage(ChatColor.RED + "Error: Usage: /system effect give <PlayerName/TargetEntity> <EffectType> [Duration] [Amplifier]");
            }

        } else if (args[3].equalsIgnoreCase("remove")) {
            if (args.length == 6) {
                PotionEffectType effectType = PotionEffectType.getByName(args[5]);
                if (effectType == null) {
                    player.sendMessage(ChatColor.RED + "Error: Invalid effect type!");
                    return;
                }

                target.removePotionEffect(effectType);
                player.sendMessage(ChatColor.DARK_RED + "Effect " + ChatColor.YELLOW + effectType.getName() + ChatColor.DARK_RED
                        + " removed from " + ChatColor.WHITE + target.getName() + ChatColor.DARK_RED + ".");
            } else {
                player.sendMessage(ChatColor.RED + "Error: Usage: /system effect remove <PlayerName/TargetEntity> <EffectType>");
            }

        } else if (args[3].equalsIgnoreCase("clear")) {
            for (PotionEffect effect : target.getActivePotionEffects()) {
                target.removePotionEffect(effect.getType());
            }
            player.sendMessage(ChatColor.BLUE + "All effects cleared from " + ChatColor.WHITE + target.getName() + ChatColor.BLUE + ".");


        } else if (args[3].equalsIgnoreCase("list")) {
            if (target.getActivePotionEffects().isEmpty()) {
                player.sendMessage(ChatColor.YELLOW + target.getName() + " has no active effects.");
            } else {
                player.sendMessage(ChatColor.GREEN + target.getName() + "'s active effects:");
                for (PotionEffect effect : target.getActivePotionEffects()) {
                    if (!effect.isInfinite()) {
                        player.sendMessage(ChatColor.GRAY + "- " + ChatColor.GOLD + effect.getType().getName()
                                + ChatColor.GRAY + " for " + ChatColor.AQUA + effect.getDuration() + " ticks"
                                + ChatColor.GRAY + ", amplifier: " + ChatColor.AQUA + effect.getAmplifier());
                    }else {
                        player.sendMessage(ChatColor.GRAY + "- " + ChatColor.GOLD + effect.getType().getName()
                                + ChatColor.GRAY + " for " + ChatColor.AQUA + "Infinite" + " ticks"
                                + ChatColor.GRAY + ", amplifier: " + ChatColor.AQUA + effect.getAmplifier());
                    }
                }
            }

        }
    }

    private static LivingEntity getTargetEntity(Player player) {
        return (LivingEntity) player.getTargetEntity(10); // Findet die Entität in einem Umkreis von 10 Blöcken
    }
}
