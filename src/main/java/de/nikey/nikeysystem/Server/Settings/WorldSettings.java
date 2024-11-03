package de.nikey.nikeysystem.Server.Settings;

import de.nikey.nikeysystem.Server.API.SettingsAPI;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.bukkit.Bukkit.getSpawnLimit;

public class WorldSettings implements Listener {

    public static void openSettingsInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, Component.text("World Settings").color(TextColor.color(123,97,87)));

        addItemToInventory(inventory, 0, Material.IRON_DOOR, "Toggle Hardcore");
        addItemToInventory(inventory, 1, Material.DIAMOND_SWORD, "Toggle PVP");
        addItemToInventory(inventory, 2, Material.ZOMBIE_HEAD, "Difficulty cycle");
        addItemToInventory(inventory, 3, Material.END_PORTAL, "Void Damage Enabled");
        addItemToInventory(inventory, 4, Material.SCAFFOLDING, "Void Damage Min Build Height Offset");
        addItemToInventoryWithLore(inventory, 5, Material.SPAWNER, "Allow Monsters", player.getWorld().getAllowMonsters());
        addItemToInventoryWithLore(inventory, 6, Material.GRASS_BLOCK, "Allow Animals", player.getWorld().getAllowAnimals());
        addItemToInventoryWithLore(inventory, 7, Material.EMERALD, "Auto Save", player.getWorld().isAutoSave());
        addSpawnLimitItem(inventory, 8, Material.ZOMBIE_SPAWN_EGG, "Set Monster Spawn Limit", SpawnCategory.MONSTER);
        addSpawnLimitItem(inventory, 9, Material.PIG_SPAWN_EGG, "Set Animal Spawn Limit", SpawnCategory.ANIMAL);
        addItemToInventoryWithInt(inventory, 10, Material.SPYGLASS, "View distance", player.getWorld().getViewDistance());
        addItemToInventoryWithInt(inventory, 11, Material.SPYGLASS, "Simulation distance", player.getWorld().getSimulationDistance());

        player.openInventory(inventory);
    }

    private static void addItemToInventory(Inventory inventory, int slot, Material material, String displayName) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + displayName);
            item.setItemMeta(meta);
        }
        inventory.setItem(slot, item);
    }

    private static void addItemToInventoryWithLore(Inventory inventory, int slot, Material material, String displayName, boolean currentSetting) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + displayName);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Currently: " + (currentSetting ? "Enabled" : "Disabled")).color(NamedTextColor.GRAY));
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        inventory.setItem(slot, item);
    }

    private static void addItemToInventoryWithInt(Inventory inventory, int slot, Material material, String displayName, int i) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + displayName);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Currently: " + i).color(NamedTextColor.GRAY));
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        inventory.setItem(slot, item);
    }

    private static void addSpawnLimitItem(Inventory inventory, int slot, Material material, String displayName, SpawnCategory category) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + displayName);
            int currentLimit = getSpawnLimit(category);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Current Limit: " + currentLimit).color(NamedTextColor.GRAY));
            meta.lore(lore);
            item.setItemMeta(meta);
        }
        inventory.setItem(slot, item);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().title().equals(Component.text("World Settings").color(TextColor.color(123,97,87)))) {
            return;
        }

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        if (slot == Slot.PVP) {
            toggleSetting(player, "PVP", player.getWorld().getPVP(), player.getWorld()::setPVP);
        }else if (slot == Slot.HARDCORE) {
            toggleSetting(player, "Hardcore", player.getWorld().isHardcore(), player.getWorld()::setHardcore);
        }else if (slot == Slot.DIFFICULTY) {
            cycleDifficulty(player);
        }else if (slot == Slot.VOIDDAMAGE) {
            toggleSetting(player, "Void damage", player.getWorld().isVoidDamageEnabled(), player.getWorld()::setVoidDamageEnabled);
        }else if (slot == Slot.VOIDDAMAGEMINBUILDHIGHTOFFSET) {
            int currentOffset = (int) player.getWorld().getVoidDamageMinBuildHeightOffset();  // Beispielwert, der angepasst werden sollte
            int adjustment = event.isLeftClick() ? 1 : -1;
            int newOffset = currentOffset + adjustment;

            player.getWorld().setVoidDamageMinBuildHeightOffset(newOffset);
            ItemStack item = event.getInventory().getItem(Slot.VOIDDAMAGEMINBUILDHIGHTOFFSET);
            if (item != null && item.getItemMeta() != null) {
                ItemMeta meta = item.getItemMeta();

                // Aktualisiere die Lore mit dem neuen Offset-Wert
                meta.lore(List.of(
                        Component.text("Current Offset: ")
                                .color(NamedTextColor.GRAY)
                                .append(Component.text(newOffset).color(NamedTextColor.YELLOW))
                ));
                item.setItemMeta(meta);
            }
        }else if (slot == Slot.ALLOWMONSTERS) {
            boolean currentSetting = player.getWorld().getAllowMonsters();
            player.getWorld().setSpawnFlags(!currentSetting, player.getWorld().getAllowAnimals());
            player.sendMessage(ChatColor.GREEN + "Allow Monsters: " + (!currentSetting ? "enabled" : "disabled"));
            ItemStack item = event.getInventory().getItem(Slot.ALLOWMONSTERS);
            if (item != null && item.getItemMeta() != null) {
                ItemMeta meta = item.getItemMeta();

                // Aktualisiere die Lore mit dem neuen Offset-Wert
                meta.lore(List.of(
                        Component.text("Allow Monsters: ")
                                .color(NamedTextColor.GRAY)
                                .append(Component.text(!currentSetting).color(NamedTextColor.YELLOW))
                ));
                item.setItemMeta(meta);
            }
        } else if (slot == Slot.ALLOWANIMALS) {
            boolean currentSetting = player.getWorld().getAllowAnimals();
            player.getWorld().setSpawnFlags(player.getWorld().getAllowMonsters(), !currentSetting);
            player.sendMessage(ChatColor.GREEN + "Allow Animals: " + (!currentSetting ? "enabled" : "disabled"));
            ItemStack item = event.getInventory().getItem(Slot.ALLOWANIMALS);
            if (item != null && item.getItemMeta() != null) {
                ItemMeta meta = item.getItemMeta();

                // Aktualisiere die Lore mit dem neuen Offset-Wert
                meta.lore(List.of(
                        Component.text("Allow Animals: ")
                                .color(NamedTextColor.GRAY)
                                .append(Component.text(!currentSetting).color(NamedTextColor.YELLOW))
                ));
                item.setItemMeta(meta);
            }
        } else if (slot == Slot.AUTOSAVE) {
            toggleSetting(player, "Auto save", player.getWorld().isAutoSave(), player.getWorld()::setAutoSave);
        } else if (slot == Slot.MONSTERSPAWNLIMIT) {
            int currentOffset = player.getWorld().getSpawnLimit(SpawnCategory.MONSTER);  // Beispielwert, der angepasst werden sollte
            int adjustment = event.isLeftClick() ? 1 : -1;
            int newOffset = currentOffset + adjustment;

            player.getWorld().setSpawnLimit(SpawnCategory.MONSTER,newOffset);
            ItemStack item = event.getInventory().getItem(Slot.MONSTERSPAWNLIMIT);
            if (item != null && item.getItemMeta() != null) {
                ItemMeta meta = item.getItemMeta();

                // Aktualisiere die Lore mit dem neuen Offset-Wert
                meta.lore(List.of(
                        Component.text("Spawn limit for Monsters: ")
                                .color(NamedTextColor.GRAY)
                                .append(Component.text(newOffset).color(NamedTextColor.YELLOW))
                ));
                item.setItemMeta(meta);
            }
        } else if (slot == Slot.ANIMALSPAWNLIMIT) {
            int currentOffset = player.getWorld().getSpawnLimit(SpawnCategory.ANIMAL);  // Beispielwert, der angepasst werden sollte
            int adjustment = event.isLeftClick() ? 1 : -1;
            int newOffset = currentOffset + adjustment;

            player.getWorld().setSpawnLimit(SpawnCategory.ANIMAL,newOffset);
            ItemStack item = event.getInventory().getItem(Slot.ANIMALSPAWNLIMIT);
            if (item != null && item.getItemMeta() != null) {
                ItemMeta meta = item.getItemMeta();

                // Aktualisiere die Lore mit dem neuen Offset-Wert
                meta.lore(List.of(
                        Component.text("Spawn limit for Animals: ")
                                .color(NamedTextColor.GRAY)
                                .append(Component.text(newOffset).color(NamedTextColor.YELLOW))
                ));
                item.setItemMeta(meta);
            }
        } else if (slot == Slot.VIEWDISTANCE) {
            int currentOffset = player.getWorld().getViewDistance();
            int adjustment = event.isLeftClick() ? 1 : -1;
            int newOffset = currentOffset + adjustment;

            player.getWorld().setViewDistance(newOffset);
            ItemStack item = event.getInventory().getItem(Slot.VIEWDISTANCE);
            if (item != null && item.getItemMeta() != null) {
                ItemMeta meta = item.getItemMeta();

                // Aktualisiere die Lore mit dem neuen Offset-Wert
                meta.lore(List.of(
                        Component.text("View distance: ")
                                .color(NamedTextColor.GRAY)
                                .append(Component.text(newOffset).color(NamedTextColor.YELLOW))
                ));
                item.setItemMeta(meta);
            }
        }else if (slot == Slot.SIMULATIONDISTANCE) {
            int currentOffset = player.getWorld().getSimulationDistance();
            int adjustment = event.isLeftClick() ? 1 : -1;
            int newOffset = currentOffset + adjustment;

            player.getWorld().setSimulationDistance(newOffset);
            ItemStack item = event.getInventory().getItem(Slot.SIMULATIONDISTANCE);
            if (item != null && item.getItemMeta() != null) {
                ItemMeta meta = item.getItemMeta();

                // Aktualisiere die Lore mit dem neuen Offset-Wert
                meta.lore(List.of(
                        Component.text("Simulation distance: ")
                                .color(NamedTextColor.GRAY)
                                .append(Component.text(newOffset).color(NamedTextColor.YELLOW))
                ));
                item.setItemMeta(meta);
            }
        }
    }


    private void toggleSetting(Player player, String settingName, boolean currentValue, Consumer<Boolean> toggleFunction) {
        toggleFunction.accept(!currentValue);
        player.sendMessage(ChatColor.GREEN + settingName + " is now " + (!currentValue ? "enabled" : "disabled"));
    }

    private void requestInput(Player player, String message, Consumer<String> onInputReceived) {
        player.sendMessage(ChatColor.YELLOW + message);
        SettingsAPI.inputRequests.put(player, onInputReceived);
    }

    private void cycleDifficulty(Player player) {
        Difficulty currentDifficulty = player.getWorld().getDifficulty();
        Difficulty nextDifficulty = switch (currentDifficulty) {
            case PEACEFUL -> Difficulty.EASY;
            case EASY -> Difficulty.NORMAL;
            case NORMAL -> Difficulty.HARD;
            case HARD -> Difficulty.PEACEFUL;
        };
        player.getWorld().setDifficulty(nextDifficulty);
        player.sendMessage(ChatColor.GREEN + "Difficulty set to " + nextDifficulty.name());
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        if (SettingsAPI.inputRequests.containsKey(player)) {
            event.setCancelled(true);

            String input = event.message().toString();
            Consumer<String> callback = SettingsAPI.inputRequests.remove(player);

            if (callback != null) {
                callback.accept(input);
            }
        }
    }

    private static class Slot {
        static final int PVP = 0;
        static final int HARDCORE = 1;
        static final int DIFFICULTY = 2;
        static final int VOIDDAMAGE = 3;
        static final int VOIDDAMAGEMINBUILDHIGHTOFFSET = 4;
        static final int ALLOWMONSTERS = 5;
        static final int ALLOWANIMALS = 6;
        static final int AUTOSAVE = 7;
        static final int MONSTERSPAWNLIMIT = 8;
        static final int ANIMALSPAWNLIMIT = 9;
        static final int VIEWDISTANCE = 10;
        static final int SIMULATIONDISTANCE = 11;

    }
}
