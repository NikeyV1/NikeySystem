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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class LocationSettings implements Listener {
    public static void openSettingsMenu(Player player,String guardname) {
        Inventory inventory = Bukkit.createInventory(player, 9, Component.text("Location Guard Settings: ").color(NamedTextColor.GRAY)
                .append(Component.text(guardname)));

        ItemStack includePlayers = new ItemStack(Material.PLAYER_HEAD);
        ItemStack excludePlayers = new ItemStack(Material.BARRIER);

        ItemMeta includeMeta = includePlayers.getItemMeta();
        includeMeta.displayName(Component.text("Manage Included Players").color(NamedTextColor.DARK_GREEN));
        includePlayers.setItemMeta(includeMeta);

        ItemMeta excludeMeta = excludePlayers.getItemMeta();
        excludeMeta.displayName(Component.text("Manage Excluded Players").color(NamedTextColor.RED));
        excludePlayers.setItemMeta(excludeMeta);

        inventory.setItem(0, includePlayers);
        inventory.setItem(2, excludePlayers);

        player.openInventory(inventory);
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
