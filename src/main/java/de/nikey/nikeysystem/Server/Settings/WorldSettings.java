package de.nikey.nikeysystem.Server.Settings;

import de.nikey.nikeysystem.Server.API.SettingsAPI;
import de.nikey.nikeysystem.Server.API.WorldAPI;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.world.flag.FeatureFlagSetHolder;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static org.bukkit.Bukkit.createWorld;
import static org.bukkit.Bukkit.getSpawnLimit;

public class WorldSettings implements Listener {

    public static void openSettingsInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, Component.text("World Settings").color(TextColor.color(123,97,87)));

        addItemToInventoryWithBoolean(inventory, 0, Material.TOTEM_OF_UNDYING, "Toggle Hardcore",player.getWorld().isHardcore());
        addItemToInventoryWithBoolean(inventory, 1, Material.DIAMOND_SWORD, "Toggle PVP",player.getWorld().getPVP());
        addItemToInventoryWithString(inventory, 2, Material.ZOMBIE_HEAD, "Difficulty cycle", player.getWorld().getDifficulty().name());
        addItemToInventoryWithBoolean(inventory, 3, Material.END_PORTAL_FRAME, "Void Damage Enabled",player.getWorld().isVoidDamageEnabled());
        addItemToInventoryWithInt(inventory, 4, Material.SCAFFOLDING, "Void Damage Min Build Height Offset", (int) player.getWorld().getVoidDamageMinBuildHeightOffset());
        addItemToInventoryWithInt(inventory, 5, Material.IRON_SWORD, "Void Damage", (int) player.getWorld().getVoidDamageAmount());
        addItemToInventoryWithBoolean(inventory, 6, Material.ZOMBIE_VILLAGER_SPAWN_EGG, "Allow Monsters", player.getWorld().getAllowMonsters());
        addItemToInventoryWithBoolean(inventory, 7, Material.GRASS_BLOCK, "Allow Animals", player.getWorld().getAllowAnimals());
        addItemToInventoryWithBoolean(inventory, 8, Material.EMERALD, "Auto Save", player.getWorld().isAutoSave());
        addSpawnLimitItem(inventory, 9, Material.ZOMBIE_SPAWN_EGG, "Set Monster Spawn Limit", player.getWorld().getSpawnLimit(SpawnCategory.MONSTER));
        addSpawnLimitItem(inventory, 10, Material.PIG_SPAWN_EGG, "Set Animal Spawn Limit",  player.getWorld().getSpawnLimit(SpawnCategory.ANIMAL));
        addItemToInventoryWithInt(inventory, 11, Material.SPYGLASS, "View distance", player.getWorld().getViewDistance());
        addItemToInventoryWithInt(inventory, 12, Material.SPYGLASS, "Simulation distance", player.getWorld().getSimulationDistance());
        addItemToInventoryWithBoolean(inventory, 13, Material.FIREWORK_ROCKET, "Auto Start", WorldAPI.isAutoStaring(player.getWorld()));
        addItemToInventoryWithBoolean(inventory, 14, Material.PLAYER_HEAD, "Creator Only", WorldAPI.isCreatorOnly(player.getWorld().getName()));



        player.openInventory(inventory);
    }

    private static void addItemToInventory(Inventory inventory, int slot, Material material, String displayName) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(displayName).color(NamedTextColor.GOLD));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            item.setItemMeta(meta);
        }
        inventory.setItem(slot, item);
    }

    private static void addItemToInventoryWithBoolean(Inventory inventory, int slot, Material material, String displayName, boolean currentSetting) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(displayName).color(NamedTextColor.GOLD));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Currently: " + currentSetting).color(NamedTextColor.GRAY));
            meta.lore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            item.setItemMeta(meta);
        }
        inventory.setItem(slot, item);
    }

    private static void addItemToInventoryWithInt(Inventory inventory, int slot, Material material, String displayName, int i) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(displayName).color(NamedTextColor.GOLD));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Currently: " + i).color(NamedTextColor.GRAY));
            meta.lore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            item.setItemMeta(meta);
        }
        inventory.setItem(slot, item);
    }

    private static void addItemToInventoryWithString(Inventory inventory, int slot, Material material, String displayName, String current) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(displayName).color(NamedTextColor.GOLD));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Currently: " + current).color(NamedTextColor.GRAY));
            meta.lore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
            item.setItemMeta(meta);
        }
        inventory.setItem(slot, item);
    }

    private static void addSpawnLimitItem(Inventory inventory, int slot, Material material, String displayName, int current) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text(displayName).color(NamedTextColor.GOLD));
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Current Limit: " + current).color(NamedTextColor.GRAY));
            meta.lore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
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
            ItemStack item = event.getInventory().getItem(Slot.PVP);
            if (item != null && item.getItemMeta() != null) {
                ItemMeta meta = item.getItemMeta();

                meta.lore(List.of(
                        Component.text("Pvp: ")
                                .color(NamedTextColor.GRAY)
                                .append(Component.text(player.getWorld().getPVP()).color(NamedTextColor.YELLOW))
                ));
                item.setItemMeta(meta);
            }
        }else if (slot == Slot.HARDCORE) {
            toggleSetting(player, "Hardcore", player.getWorld().isHardcore(), player.getWorld()::setHardcore);
            ItemStack item = event.getInventory().getItem(Slot.HARDCORE);
            if (item != null && item.getItemMeta() != null) {
                ItemMeta meta = item.getItemMeta();

                meta.lore(List.of(
                        Component.text("Hardcore: ")
                                .color(NamedTextColor.GRAY)
                                .append(Component.text(player.getWorld().isHardcore()).color(NamedTextColor.YELLOW))
                ));
                item.setItemMeta(meta);
            }
        }else if (slot == Slot.DIFFICULTY) {
            cycleDifficulty(player);
            ItemStack item = event.getInventory().getItem(Slot.DIFFICULTY);
            if (item != null && item.getItemMeta() != null) {
                ItemMeta meta = item.getItemMeta();

                // Aktualisiere die Lore mit dem neuen Offset-Wert
                meta.lore(List.of(
                        Component.text("Difficulty: ")
                                .color(NamedTextColor.GRAY)
                                .append(Component.text(player.getWorld().getDifficulty().name()).color(NamedTextColor.YELLOW))
                ));
                item.setItemMeta(meta);
            }
        }else if (slot == Slot.VOIDDAMAGE) {
            toggleSetting(player, "Void damage", player.getWorld().isVoidDamageEnabled(), player.getWorld()::setVoidDamageEnabled);
            ItemStack item = event.getInventory().getItem(Slot.VOIDDAMAGE);
            if (item != null && item.getItemMeta() != null) {
                ItemMeta meta = item.getItemMeta();

                // Aktualisiere die Lore mit dem neuen Offset-Wert
                meta.lore(List.of(
                        Component.text("Void damage: ")
                                .color(NamedTextColor.GRAY)
                                .append(Component.text(player.getWorld().isVoidDamageEnabled()).color(NamedTextColor.YELLOW))
                ));
                item.setItemMeta(meta);
            }
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
            ItemStack item = event.getInventory().getItem(Slot.AUTOSAVE);
            if (item != null && item.getItemMeta() != null) {
                ItemMeta meta = item.getItemMeta();

                meta.lore(List.of(
                        Component.text("Autosave: ")
                                .color(NamedTextColor.GRAY)
                                .append(Component.text(player.getWorld().isAutoSave()).color(NamedTextColor.YELLOW))
                ));
                item.setItemMeta(meta);
            }
        } else if (slot == Slot.MONSTERSPAWNLIMIT) {
            int currentOffset = player.getWorld().getSpawnLimit(SpawnCategory.MONSTER);  // Beispielwert, der angepasst werden sollte
            int adjustment = event.isLeftClick() ? 1 : -1;
            int newOffset = currentOffset + adjustment;

            player.getWorld().setSpawnLimit(SpawnCategory.MONSTER, newOffset);
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
            if (newOffset > 32)newOffset =32;

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
            if (newOffset > 32)newOffset =32;

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
        }else if (slot == Slot.AUTOSTART) {

            if (WorldAPI.isAutoStaring(player.getWorld())) {
                WorldAPI.removeAutoStart(player.getWorld());
            }else {
                WorldAPI.addAutoStart(player.getWorld());
            }

            ItemStack item = event.getInventory().getItem(Slot.AUTOSTART);
            if (item != null && item.getItemMeta() != null) {
                ItemMeta meta = item.getItemMeta();

                meta.lore(List.of(
                        Component.text("Auto Start: ")
                                .color(NamedTextColor.GRAY)
                                .append(Component.text(WorldAPI.isAutoStaring(player.getWorld())).color(NamedTextColor.YELLOW))
                ));
                item.setItemMeta(meta);
            }
        }else if (slot == Slot.CREATORONLY) {

            if (WorldAPI.isWorldOwner(player.getWorld().getName(),player.getName())) {
                WorldAPI.setCreatorOnly(player.getWorld().getName(), !WorldAPI.isCreatorOnly(player.getWorld().getName()));
            }
            World mainWorld = Bukkit.getWorld("world");

            if (mainWorld != null) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (onlinePlayer.getWorld() == player.getWorld()) {
                        if (!WorldAPI.isAllowedOnWorld(onlinePlayer.getName(),player.getWorld().getName())) {
                            if (WorldAPI.isAllowedOnWorld(onlinePlayer.getName(),mainWorld.getName())) {
                                onlinePlayer.teleport(mainWorld.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                            }else {
                                onlinePlayer.kick(Component.text(""));
                            }
                        }
                    }
                }
            }
            ItemStack item = event.getInventory().getItem(Slot.CREATORONLY);
            if (item != null && item.getItemMeta() != null) {
                ItemMeta meta = item.getItemMeta();

                meta.lore(List.of(
                        Component.text("Creator Only: ")
                                .color(NamedTextColor.GRAY)
                                .append(Component.text(WorldAPI.isCreatorOnly(player.getWorld().getName())).color(NamedTextColor.YELLOW))
                ));
                item.setItemMeta(meta);
            }
        }else if (slot == Slot.VOIDDAMAGEAMOUNT) {
            int currentOffset = (int) player.getWorld().getVoidDamageAmount();
            int adjustment = event.isLeftClick() ? 1 : -1;
            int newOffset = currentOffset + adjustment;

            player.getWorld().setVoidDamageAmount(newOffset);
            ItemStack item = event.getInventory().getItem(Slot.VOIDDAMAGEAMOUNT);
            if (item != null && item.getItemMeta() != null) {
                ItemMeta meta = item.getItemMeta();

                meta.lore(List.of(
                        Component.text("Void-Damage amount: ")
                                .color(NamedTextColor.GRAY)
                                .append(Component.text(player.getWorld().getVoidDamageAmount()).color(NamedTextColor.YELLOW))
                ));
                item.setItemMeta(meta);
            }
        }
    }


    private void toggleSetting(Player player, String settingName, boolean currentValue, Consumer<Boolean> toggleFunction) {
        toggleFunction.accept(!currentValue);
        player.sendMessage(ChatColor.GREEN + settingName + " is now " + (!currentValue ? "enabled" : "disabled"));
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
        player.sendMessage(Component.text("The difficulty has been set to " + nextDifficulty.name()));
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
        static final int HARDCORE = 0;
        static final int PVP = 1;
        static final int DIFFICULTY = 2;
        static final int VOIDDAMAGE = 3;
        static final int VOIDDAMAGEMINBUILDHIGHTOFFSET = 4;
        static final int VOIDDAMAGEAMOUNT = 5;
        static final int ALLOWMONSTERS = 6;
        static final int ALLOWANIMALS = 7;
        static final int AUTOSAVE = 8;
        static final int MONSTERSPAWNLIMIT = 9;
        static final int ANIMALSPAWNLIMIT = 10;
        static final int VIEWDISTANCE = 11;
        static final int SIMULATIONDISTANCE = 12;
        static final int AUTOSTART = 13;
        static final int CREATORONLY = 14;

    }
}
