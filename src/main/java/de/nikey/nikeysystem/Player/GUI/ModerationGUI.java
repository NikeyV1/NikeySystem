package de.nikey.nikeysystem.Player.GUI;

import de.nikey.nikeysystem.NikeySystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class ModerationGUI {
    public static void openModerationGUI(Player moderator, Player target) {
        Inventory moderationInventory = Bukkit.createInventory(moderator, 27, Component.text("Manage " + target.getName()));

        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) playerHead.getItemMeta();
        if (headMeta != null) {
            headMeta.setOwningPlayer(target);
            headMeta.displayName(Component.text(target.getName()).color(NamedTextColor.WHITE));
            headMeta.lore(List.of(
                    Component.text("UUID: " + target.getUniqueId()).color(NamedTextColor.GRAY)
            ));
            playerHead.setItemMeta(headMeta);
        }
        moderationInventory.setItem(4, playerHead);

        ItemStack banItem = new ItemStack(Material.MACE);
        banItem.editMeta(meta -> {
            meta.displayName(Component.text("Ban Player").color(TextColor.color(0xFF0000)));
            meta.lore(List.of(
                    Component.text("Temporarily or permanently ban"),
                    Component.text("Click to configure.")
            ));
        });
        moderationInventory.setItem(10, banItem);

        ItemStack kickItem = new ItemStack(Material.FIREWORK_ROCKET);
        kickItem.editMeta(meta -> {
            meta.displayName(Component.text("Kick Player").color(TextColor.color(254, 153, 0)));
            meta.lore(List.of(
                    Component.text("Kick the player from the server").color(NamedTextColor.GRAY)
            ));
        });
        moderationInventory.setItem(12, kickItem);

        ItemStack freezeItem = new ItemStack(Material.BLUE_ICE);
        ItemMeta freezeMeta = freezeItem.getItemMeta();
        if (freezeMeta != null) {
            freezeMeta.displayName(Component.text("Freeze Player", TextColor.color(13,170,176)));
            freezeMeta.lore(List.of(
                    Component.text("Prevent the player from moving."),
                    Component.text("")
            ));
            freezeItem.setItemMeta(freezeMeta);
        }
        moderationInventory.setItem(16, freezeItem);

        ItemStack warnItem = new ItemStack(Material.PAPER);
        ItemMeta warnMeta = warnItem.getItemMeta();
        if (warnMeta != null) {
            warnMeta.displayName(Component.text("Warn Player", TextColor.color(255, 222, 89)));
            warnMeta.lore(List.of(
                    Component.text("Warn the player")
            ));
            warnItem.setItemMeta(warnMeta);
        }
        moderationInventory.setItem(18, warnItem);

        ItemStack logItem = new ItemStack(Material.BOOK);
        ItemMeta logMeta = logItem.getItemMeta();
        if (logMeta != null) {
            logMeta.displayName(Component.text("View Player Log", TextColor.color(0xFFD700)));
            logMeta.lore(List.of(
                    Component.text("View recent actions of the player."),
                    Component.text("Click to view.")
            ));
            logItem.setItemMeta(logMeta);
        }
        moderationInventory.setItem(20, logItem);

        moderator.openInventory(moderationInventory);
    }

}
