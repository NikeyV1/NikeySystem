package de.nikey.nikeysystem.Player.Settings;

import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class LocationSettings implements Listener {
    public static void openSettingsMenu(Player player,String guardname) {
        Inventory inventory = Bukkit.createInventory(player, 18, Component.text("Location Guard Settings: ").color(NamedTextColor.GRAY)
                .append(Component.text(guardname)));

        ItemStack includePlayers = new ItemStack(Material.PLAYER_HEAD);
        ItemStack excludePlayers = new ItemStack(Material.BARRIER);

        ItemMeta includeMeta = includePlayers.getItemMeta();
        includeMeta.displayName(Component.text("Manage Trusted Players").color(NamedTextColor.DARK_GREEN));
        includePlayers.setItemMeta(includeMeta);

        ItemMeta excludeMeta = excludePlayers.getItemMeta();
        excludeMeta.displayName(Component.text("Manage Forbidden Players").color(NamedTextColor.RED));
        excludePlayers.setItemMeta(excludeMeta);

        ItemStack option4 = new ItemStack(Material.COBBLESTONE);
        ItemMeta itemMeta4 = option4.getItemMeta();
        itemMeta4.displayName(Component.text("Toggle Block Break Protection").color(NamedTextColor.DARK_RED));
        option4.setItemMeta(itemMeta4);

        ItemStack option5 = new ItemStack(Material.GRASS_BLOCK);
        ItemStack option6 = new ItemStack(Material.TNT);
        ItemStack option7 = new ItemStack(Material.REDSTONE);
        ItemStack option8 = new ItemStack(Material.IRON_DOOR);
        ItemStack option9 = new ItemStack(Material.CHEST);


        updateSettingItem(option5, "Block Place Protection", "location.settings." + guardname + ".preventBlockPlace");
        updateSettingItem(option6, "TNT Protection", "location.settings." + guardname + ".preventTNTExplosions");
        updateSettingItem(option7, "Redstone Protection", "location.settings." + guardname + ".preventRedstoneUsage");
        updateSettingItem(option8, "Prevent Entry", "location.settings." + guardname + ".preventEntry");
        updateSettingItem(option9, "Prevent Interaction", "location.settings." + guardname + ".preventInteraction");

        boolean preventBlockBreak = NikeySystem.getPlugin().getConfig().getBoolean("location.settings." + guardname + ".preventBlockBreak", false);
        updateItemLore(option4, preventBlockBreak);

        inventory.setItem(0, includePlayers);
        inventory.setItem(2, excludePlayers);
        inventory.setItem(4, option4);
        inventory.setItem(6, option5);
        inventory.setItem(8, option6);
        inventory.setItem(10, option7);
        inventory.setItem(12, option8);
        inventory.setItem(14, option9);

        player.openInventory(inventory);
    }

    private static void updateSettingItem(ItemStack item, String displayName, String configPath) {
        ItemMeta meta = item.getItemMeta();
        boolean enabled = NikeySystem.getPlugin().getConfig().getBoolean(configPath, false);

        meta.displayName(Component.text(displayName).color(NamedTextColor.DARK_GREEN));
        List<Component> lore = List.of(
                Component.text("Current State: ").color(NamedTextColor.GRAY)
                        .append(enabled ? Component.text("Enabled").color(NamedTextColor.GREEN)
                                : Component.text("Disabled").color(NamedTextColor.RED))
        );
        meta.lore(lore);

        item.setItemMeta(meta);
    }

    private static void updateItemLore(ItemStack item, boolean enabled) {
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Current State: ").color(NamedTextColor.GRAY)
                .append(enabled ? Component.text("Enabled").color(NamedTextColor.GREEN)
                        : Component.text("Disabled").color(NamedTextColor.RED)));
        meta.lore(lore);
        item.setItemMeta(meta);
    }



    private void toggleSetting(FileConfiguration config, String path, ItemStack item) {
        boolean current = config.getBoolean(path, false);
        config.set(path, !current);
        NikeySystem.getPlugin().saveConfig();

        updateSettingItem(item, item.getItemMeta().getDisplayName(), path);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))return;
        String plainTitle = PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        if (!plainTitle.startsWith("Location Guard Settings: ") || !PermissionAPI.isSystemUser((Player) event.getWhoClicked())) return;
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        String title = event.getView().getTitle();

        String guardName = title.split(": ")[1];

        if (clickedItem.getType() == Material.PLAYER_HEAD) {
            openPlayerManagementMenu(player, guardName, "include");
        } else if (clickedItem.getType() == Material.BARRIER) {
            openPlayerManagementMenu(player, guardName, "exclude");
        } else if (clickedItem.getType() == Material.COBBLESTONE) {
            boolean preventBlockBreak = NikeySystem.getPlugin().getConfig().getBoolean("location.settings." + guardName + ".preventBlockBreak", false);
            NikeySystem.getPlugin().getConfig().set("location.settings." + guardName + ".preventBlockBreak", !preventBlockBreak);
            NikeySystem.getPlugin().saveConfig();
            updateItemLore(clickedItem, !preventBlockBreak);
        } else if (clickedItem.getType() == Material.GRASS_BLOCK) {
            toggleSetting(NikeySystem.getPlugin().getConfig(), "location.settings." + guardName + ".preventBlockPlace", clickedItem);
        } else if (clickedItem.getType() == Material.TNT) {
            toggleSetting(NikeySystem.getPlugin().getConfig(), "location.settings." + guardName + ".preventTNTExplosions", clickedItem);
        } else if (clickedItem.getType() == Material.REDSTONE) {
            toggleSetting(NikeySystem.getPlugin().getConfig(), "location.settings." + guardName + ".preventRedstoneUsage", clickedItem);
        }else if (clickedItem.getType() == Material.IRON_DOOR) {
            toggleSetting(NikeySystem.getPlugin().getConfig(), "location.settings." + guardName + ".preventEntry", clickedItem);
        }else if (clickedItem.getType() == Material.CHEST) {
            toggleSetting(NikeySystem.getPlugin().getConfig(), "location.settings." + guardName + ".preventInteraction", clickedItem);
        }
    }

    public static void openPlayerManagementMenu(Player player, String guardName, String mode) {
        Inventory inventory = Bukkit.createInventory(player, 36, Component.text("Manage " + mode + "d Players: ").color(NamedTextColor.GRAY)
                .append(Component.text(guardName)));

        FileConfiguration config = NikeySystem.getPlugin().getConfig();
        List<String> playersList = config.getStringList("location.settings." + guardName + "." + mode);

        // Spieler in der Liste anzeigen
        for (int i = 0; i < playersList.size() && i < 36; i++) {
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(playersList.get(i)));
            meta.displayName(Component.text(playersList.get(i)).color(NamedTextColor.WHITE));
            playerHead.setItemMeta(meta);

            inventory.setItem(i, playerHead);
        }

        player.openInventory(inventory);
    }

    @EventHandler
    public void onPlayerManagementClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        String plainTitle = PlainTextComponentSerializer.plainText().serialize(event.getView().title());

        // Überprüfen, ob das Inventar zur Spielerverwaltung gehört
        if (!plainTitle.startsWith("Manage included Players: ") && !plainTitle.startsWith("Manage excluded Players: ")) return;

        event.setCancelled(true); // Blockiere alle Standardaktionen

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null && clickedItem.getType() != Material.AIR) {
            // Spieler aus der Liste entfernen
            String clickedPlayerName = PlainTextComponentSerializer.plainText().serialize(clickedItem.getItemMeta().displayName());
            String guardName = plainTitle.split(": ")[1];
            String mode = plainTitle.contains("included") ? "include" : "exclude";

            FileConfiguration config = NikeySystem.getPlugin().getConfig();
            List<String> playersList = config.getStringList("location.settings." + guardName + "." + mode);
            playersList.remove(clickedPlayerName);
            config.set("location.settings." + guardName + "." + mode, playersList);
            NikeySystem.getPlugin().saveConfig();

            player.sendMessage("§aPlayer §f" + clickedPlayerName + " §ahas been removed from the " + mode + " list of guard §f" + guardName + "§a.");
            openPlayerManagementMenu(player, guardName, mode);
        } else {
            // Freier Slot: Spieler hinzufügen
            String guardName = plainTitle.split(": ")[1];
            String mode = plainTitle.contains("included") ? "include" : "exclude";

            player.closeInventory();
            player.sendMessage("§ePlease type the name of the player to add to the " + mode + " list of guard §f'" + guardName + "'§e:");

            // Abfrage im Chat (mit AsyncChatEvent)
            NikeySystem.getPlugin().getServer().getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onChat(AsyncChatEvent chatEvent) {
                    if (chatEvent.getPlayer().equals(player)) {
                        chatEvent.setCancelled(true);

                        String newPlayerName = PlainTextComponentSerializer.plainText().serialize(chatEvent.message());
                        FileConfiguration config = NikeySystem.getPlugin().getConfig();
                        List<String> playersList = config.getStringList("location.settings." + guardName + "." + mode);

                        if (!playersList.contains(newPlayerName)) {
                            playersList.add(newPlayerName);
                            config.set("location.settings." + guardName + "." + mode, playersList);
                            NikeySystem.getPlugin().saveConfig();

                            player.sendMessage("§aPlayer §f" + newPlayerName + " §ahas been added to the " + mode + " list of guard §f'" + guardName + "'§a");
                        } else {
                            player.sendMessage("§cPlayer §f" + newPlayerName + " §cis already in the " + mode + " list");
                        }

                        // Öffne das Management-Inventar erneut
                        Bukkit.getScheduler().runTask(NikeySystem.getPlugin(), () -> openPlayerManagementMenu(player, guardName, mode));
                        AsyncChatEvent.getHandlerList().unregister(this); // Listener entfernen
                    }
                }
            }, NikeySystem.getPlugin());
        }
    }

}
