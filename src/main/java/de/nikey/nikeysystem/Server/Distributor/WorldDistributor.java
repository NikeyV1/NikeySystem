package de.nikey.nikeysystem.Server.Distributor;

import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.Server.Settings.WorldSettings;
import io.papermc.paper.entity.TeleportFlag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.io.IOException;

public class WorldDistributor {
    public static void worldManager(Player sender, String[] args) {
        String cmd = args[3];
        if (cmd.isEmpty()) return;

        if (cmd.equalsIgnoreCase("create")) {
            if (PermissionAPI.isAdmin(sender.getName()) || PermissionAPI.isOwner(sender.getName())) {
                if (args.length == 5) {
                    WorldCreator creator = new WorldCreator(args[4]);

                    World world = Bukkit.createWorld(creator);
                    //Save world and load world missing
                    sender.sendMessage(Component.text("The world ").color(TextColor.color(25,167,80))
                            .append(Component.text(creator.name()).color(NamedTextColor.WHITE))
                            .append(Component.text(" was created").color(TextColor.color(25,167,80))));
                } else if (args.length == 6) {
                    WorldCreator creator = new WorldCreator(args[4]);
                    long l = Long.parseLong(args[5]);
                    creator.seed(l);

                    Bukkit.createWorld(creator);
                    sender.sendMessage(Component.text("The world ").color(TextColor.color(25,167,80))
                            .append(Component.text(creator.name()).color(NamedTextColor.WHITE))
                            .append(Component.text(" was created with seed: ").color(TextColor.color(25,167,80)))
                            .append(Component.text(creator.seed()).color(NamedTextColor.GRAY)));
                }else if (args.length == 7) {
                    WorldCreator creator = new WorldCreator(args[4]);
                    long l = Long.parseLong(args[5]);
                    creator.seed(l);
                    World.Environment environment = World.Environment.valueOf(args[6]);
                    creator.environment(environment);

                    Bukkit.createWorld(creator);
                    sender.sendMessage(Component.text("The world ").color(TextColor.color(25,167,80))
                            .append(Component.text(creator.name()).color(NamedTextColor.WHITE))
                            .append(Component.text(" was created with seed: ").color(TextColor.color(25,167,80)))
                            .append(Component.text(creator.seed()).color(NamedTextColor.GRAY))
                            .append(Component.text(" and with environment: ").color(TextColor.color(25,167,80)))
                            .append(Component.text(creator.environment().name()).color(NamedTextColor.DARK_AQUA)));
                }else if (args.length == 8) {
                    WorldCreator creator = new WorldCreator(args[4]);
                    long l = Long.parseLong(args[5]);
                    creator.seed(l);
                    World.Environment environment = World.Environment.valueOf(args[6]);
                    creator.environment(environment);
                    creator.type(WorldType.valueOf(args[7]));

                    Bukkit.createWorld(creator);
                    sender.sendMessage(Component.text("The world ").color(TextColor.color(25,167,80))
                            .append(Component.text(creator.name()).color(NamedTextColor.WHITE))
                            .append(Component.text(" was created with seed: ").color(TextColor.color(25,167,80)))
                            .append(Component.text(creator.seed()).color(NamedTextColor.GRAY))
                            .append(Component.text(" and with environment: ").color(TextColor.color(25,167,80)))
                            .append(Component.text(creator.environment().name()).color(NamedTextColor.DARK_AQUA))
                            .append(Component.text(" and with type: ").color(TextColor.color(25,167,80)))
                            .append(Component.text(creator.type().name()).color(NamedTextColor.DARK_GREEN)));
                }else if (args.length == 9) {
                    WorldCreator creator = new WorldCreator(args[4]);
                    long l = Long.parseLong(args[5]);
                    creator.seed(l);
                    World.Environment environment = World.Environment.valueOf(args[6]);
                    creator.environment(environment);
                    creator.type(WorldType.valueOf(args[7]));
                    creator.generateStructures(Boolean.parseBoolean(args[8]));

                    Bukkit.createWorld(creator);
                    sender.sendMessage(Component.text("The world ").color(TextColor.color(25,167,80))
                            .append(Component.text(creator.name()).color(NamedTextColor.WHITE))
                            .append(Component.text(" was created with seed: ").color(TextColor.color(25,167,80)))
                            .append(Component.text(creator.seed()).color(NamedTextColor.GRAY))
                            .append(Component.text(", with environment: ").color(TextColor.color(25,167,80)))
                            .append(Component.text(creator.environment().name()).color(NamedTextColor.DARK_AQUA))
                            .append(Component.text(", with type: ").color(TextColor.color(25,167,80)))
                            .append(Component.text(creator.type().name()).color(NamedTextColor.DARK_GREEN))
                            .append(Component.text(" and generate structures: ").color(TextColor.color(25,167,80)))
                            .append(Component.text(creator.generateStructures()).color(NamedTextColor.DARK_GRAY)));
                }
            }
        } else if (cmd.equalsIgnoreCase("delete")) {
            if (PermissionAPI.isAdmin(sender.getName()) || PermissionAPI.isOwner(sender.getName())) {
                if (args.length == 5) {
                    World world = Bukkit.getWorld(args[4]);

                    if (world == null) {
                        sender.sendMessage("§cError: world not found");
                        return;
                    }

                    World normal = Bukkit.getWorld("world");
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getWorld() == world) {
                            if (normal == null) {
                                player.kick();
                                continue;
                            }
                            player.teleport(normal.getSpawnLocation());
                        }
                    }

                    File folder = world.getWorldFolder();
                    Bukkit.unloadWorld(world, false);

                    boolean delete = folder.delete();
                    if (delete) {
                        sender.sendMessage(Component.text("World '").color(TextColor.color(25,167,80))
                                .append(Component.text(world.getName()).color(NamedTextColor.WHITE))
                                .append(Component.text("' was ").color(TextColor.color(25,167,80)))
                                .append(Component.text("successfully").color(NamedTextColor.GREEN))
                                .append(Component.text(" deleted").color(TextColor.color(25,167,80))));
                    }else {
                        sender.sendMessage(Component.text("World '").color(TextColor.color(25,167,80))
                                .append(Component.text(world.getName()).color(NamedTextColor.WHITE))
                                .append(Component.text("' ").color(TextColor.color(25,167,80)))
                                .append(Component.text("couldn't").color(NamedTextColor.RED))
                                .append(Component.text(" be deleted").color(TextColor.color(25,167,80))));
                    }
                }
            }
        } else if (cmd.equalsIgnoreCase("tp")) {
            if (args.length == 5) {
                World world = Bukkit.getWorld(args[4]);

                if (world == null) {
                    sender.sendMessage("§cError: world not found");
                    return;
                }

                sender.teleportAsync(world.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
            }
        } else if (cmd.equalsIgnoreCase("list")) {
            Component message = Component.text("Available Worlds:\n", NamedTextColor.GOLD);

            for (World world : Bukkit.getWorlds()) {
                String worldName = world.getName();
                Component worldComponent;

                switch (worldName.toLowerCase()) {
                    case "world":
                        worldComponent = Component.text(worldName, NamedTextColor.GREEN);
                        break;
                    case "world_nether":
                        worldComponent = Component.text(worldName, NamedTextColor.RED);
                        break;
                    case "world_the_end":
                        worldComponent = Component.text(worldName, NamedTextColor.DARK_PURPLE);
                        break;
                    default:
                        worldComponent = Component.text(worldName, NamedTextColor.BLUE);
                        break;
                }

                // Add line break and append to the message
                message = message.append(worldComponent).append(Component.text("\n"));
            }

            // Send the complete message to the player
            sender.sendMessage(message);
        }else if (cmd.equalsIgnoreCase("settings")) {
            WorldSettings.openSettingsInventory(sender);
        }else if (cmd.equalsIgnoreCase("load")) {
            if (args.length == 5) {
                String worldName = args[4];
                File worldFolder = new File(Bukkit.getWorldContainer(), worldName);

        // Prüfen, ob der Ordner der Welt existiert
                if (worldFolder.exists() && worldFolder.isDirectory()) {
                    World world = Bukkit.getWorld(worldName);

            // Wenn die Welt bereits geladen ist
                    if (world != null) {
                        sender.sendMessage(Component.text("World '").color(TextColor.color(25,167,80))
                            .append(Component.text(world.getName()).color(NamedTextColor.WHITE))
                            .append(Component.text("' is already loaded.").color(TextColor.color(25,167,80))));
                    } else {
                // Welt laden
                        WorldCreator creator = new WorldCreator(worldName);
                        world = Bukkit.createWorld(creator);
                
                        sender.sendMessage(Component.text("World '").color(TextColor.color(25,167,80))
                            .append(Component.text(worldName).color(NamedTextColor.WHITE))
                            .append(Component.text("' has been successfully loaded.").color(TextColor.color(25,167,80))));
                    }
                } else {
            // Fehlermeldung, wenn die Welt nicht existiert
                    sender.sendMessage(Component.text("Error: World '").color(NamedTextColor.RED)
                        .append(Component.text(worldName).color(NamedTextColor.WHITE))
                        .append(Component.text("' does not exist.").color(NamedTextColor.RED)));
                }
            }
        }
    }
}
