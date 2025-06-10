package de.nikey.nikeysystem.Server.Distributor;

import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.ChatAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.Server.API.WorldAPI;
import de.nikey.nikeysystem.Server.Settings.WorldSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class WorldDistributor {
    public static void worldManager(Player sender, String[] args) {
        String cmd = args[3];
        if (cmd.isEmpty()) return;

        if (cmd.equalsIgnoreCase("create")) {
            if (PermissionAPI.isAdmin(sender.getName()) || PermissionAPI.isOwner(sender.getName())) {
                if (args.length == 5) {
                    WorldCreator creator = new WorldCreator(args[4]);

                    Bukkit.createWorld(creator);
                    WorldAPI.setWorldOwner(creator.name(),sender.getName());
                    //Save world and load world missing
                    sender.sendMessage(Component.text("The world ").color(TextColor.color(25,167,80))
                            .append(Component.text(creator.name()).color(NamedTextColor.WHITE))
                            .append(Component.text(" was created").color(TextColor.color(25,167,80))));
                } else if (args.length == 6) {
                    WorldCreator creator = new WorldCreator(args[4]);
                    if (!args[5].equalsIgnoreCase("seed")) {
                        long l = Long.parseLong(args[5]);
                        creator.seed(l);
                    }

                    Bukkit.createWorld(creator);
                    WorldAPI.setWorldOwner(creator.name(),sender.getName());
                    sender.sendMessage(Component.text("The world ").color(TextColor.color(25,167,80))
                            .append(Component.text(creator.name()).color(NamedTextColor.WHITE))
                            .append(Component.text(" was created with seed: ").color(TextColor.color(25,167,80)))
                            .append(Component.text(creator.seed()).color(NamedTextColor.GRAY)));
                }else if (args.length == 7) {
                    WorldCreator creator = new WorldCreator(args[4]);
                    if (!args[5].equalsIgnoreCase("seed")) {
                        long l = Long.parseLong(args[5]);
                        creator.seed(l);
                    }
                    World.Environment environment = World.Environment.valueOf(args[6]);
                    creator.environment(environment);

                    Bukkit.createWorld(creator);
                    WorldAPI.setWorldOwner(creator.name(),sender.getName());
                    sender.sendMessage(Component.text("The world ").color(TextColor.color(25,167,80))
                            .append(Component.text(creator.name()).color(NamedTextColor.WHITE))
                            .append(Component.text(" was created with seed: ").color(TextColor.color(25,167,80)))
                            .append(Component.text(creator.seed()).color(NamedTextColor.GRAY))
                            .append(Component.text(" and with environment: ").color(TextColor.color(25,167,80)))
                            .append(Component.text(creator.environment().name()).color(NamedTextColor.DARK_AQUA)));
                }else if (args.length == 8) {
                    WorldCreator creator = new WorldCreator(args[4]);
                    if (!args[5].equalsIgnoreCase("seed")) {
                        long l = Long.parseLong(args[5]);
                        creator.seed(l);
                    }
                    World.Environment environment = World.Environment.valueOf(args[6]);
                    creator.environment(environment);
                    creator.type(WorldType.valueOf(args[7]));

                    Bukkit.createWorld(creator);
                    WorldAPI.setWorldOwner(creator.name(),sender.getName());
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
                    if (!args[5].equalsIgnoreCase("seed")) {
                        long l = Long.parseLong(args[5]);
                        creator.seed(l);
                    }
                    World.Environment environment = World.Environment.valueOf(args[6]);
                    creator.environment(environment);
                    creator.type(WorldType.valueOf(args[7]));
                    creator.generateStructures(Boolean.parseBoolean(args[8]));

                    Bukkit.createWorld(creator);
                    WorldAPI.setWorldOwner(creator.name(),sender.getName());
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
                        File worldContainer = Bukkit.getWorldContainer();
                        File worldFile = findWorldFile(worldContainer, args[4]);

                        if (worldFile != null) {
                            WorldAPI.removeWorld(worldFile.getName());
                            delete(worldFile);

                            // Überprüfen, ob das Verzeichnis gelöscht wurde
                            String message = !worldFile.exists() ? "successfully" : "couldn't be";
                            sendDeleteMessage(sender, args[4], message);
                        } else {
                            sender.sendMessage("§cError: World not found.");
                        }
                        return;
                    }

                    World mainWorld = Bukkit.getWorld("world");
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getWorld().equals(world)) {
                            if (mainWorld != null) {
                                player.teleport(mainWorld.getSpawnLocation());
                            } else {
                                player.kick();
                            }
                        }
                    }

                    File worldFolder = world.getWorldFolder();
                    Bukkit.unloadWorld(world, false);
                    WorldAPI.removeWorld(args[4]);
                    delete(worldFolder);
                    String message = !new File(Bukkit.getWorldContainer(), args[4]).exists() ? "successfully" : "couldn't be";
                    sendDeleteMessage(sender, args[4], message);
                }
            }
        } else if (cmd.equalsIgnoreCase("tp")) {
            if (args.length == 5) {
                World world = Bukkit.getWorld(args[4]);

                if (world == null) {
                    sender.sendMessage("§cError: world not found");
                    return;
                }

                if (WorldAPI.isCreatorOnly(world.getName())) {
                    if (!WorldAPI.isWorldOwner(world.getName(),sender.getName())) {
                        sender.sendMessage("§cYou are not allowed to enter this world!");
                        return;
                    }
                }

                sender.teleportAsync(world.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
            }
        } else if (cmd.equalsIgnoreCase("list")) {
            Component message = Component.text("Available Worlds:\n", NamedTextColor.GOLD);

            for (World world : Bukkit.getWorlds()) {
                String worldName = world.getName();
                Component worldComponent = switch (worldName.toLowerCase()) {
                    case "world" -> Component.text(worldName, NamedTextColor.GREEN);
                    case "world_nether" -> Component.text(worldName, NamedTextColor.RED);
                    case "world_the_end" -> Component.text(worldName, NamedTextColor.YELLOW);
                    default -> Component.text(worldName, NamedTextColor.BLUE);
                };

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
                    List<String > folder = Arrays.asList("plugins","versions","logs","libraries","debug","config","cache","crash-reports");
                    if (folder.contains(worldName)){
                        sender.sendMessage(Component.text("World '").color(TextColor.color(25,167,80))
                                .append(Component.text(worldName).color(NamedTextColor.WHITE))
                                .append(Component.text("' is not a world").color(TextColor.color(25,167,80))));
                        return;
                    }
                    World world = Bukkit.getWorld(worldName);

                    if (world != null) {
                        sender.sendMessage(Component.text("World '").color(TextColor.color(25,167,80))
                                .append(Component.text(world.getName()).color(NamedTextColor.WHITE))
                                .append(Component.text("' is already loaded").color(TextColor.color(25,167,80))));
                    } else {
                        WorldCreator creator = new WorldCreator(worldName);
                        Bukkit.createWorld(creator);

                        sender.sendMessage(Component.text("World '").color(TextColor.color(25,167,80))
                                .append(Component.text(worldName).color(NamedTextColor.WHITE))
                                .append(Component.text("' has been successfully loaded").color(TextColor.color(25,167,80))));
                    }
                } else {
                    // Fehlermeldung, wenn die Welt nicht existiert
                    sender.sendMessage(Component.text("Error: World '").color(NamedTextColor.RED)
                            .append(Component.text(worldName).color(NamedTextColor.WHITE))
                            .append(Component.text("' does not exist").color(NamedTextColor.RED)));
                }
            }
        }else if (cmd.equalsIgnoreCase("unload")) {
            if (args.length == 5) {
                String worldName = args[4];
                File worldFolder = new File(Bukkit.getWorldContainer(), worldName);

                if (worldFolder.exists() && worldFolder.isDirectory()) {
                    List<String > folder = Arrays.asList("plugins","versions","logs","libraries","debug","config","cache","crash-reports");
                    if (folder.contains(worldName)){
                        sender.sendMessage(Component.text("World '").color(TextColor.color(25,167,80))
                                .append(Component.text(worldName).color(NamedTextColor.WHITE))
                                .append(Component.text("' is not a world").color(TextColor.color(25,167,80))));
                        return;
                    }
                    World world = Bukkit.getWorld(worldName);

                    if (world == null) {
                        sender.sendMessage(Component.text("World '").color(TextColor.color(25,167,80))
                                .append(Component.text(worldName).color(NamedTextColor.WHITE))
                                .append(Component.text("' isn't loaded").color(TextColor.color(25,167,80))));
                    } else {

                        World mainWorld = Bukkit.getWorld("world");
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (player.getWorld().equals(world)) {
                                if (mainWorld != null) {
                                    player.teleport(mainWorld.getSpawnLocation());
                                } else {
                                    player.kick();
                                }
                            }
                        }

                        boolean success = Bukkit.unloadWorld(world, true);

                        if (success) {
                            sender.sendMessage(Component.text("World '").color(TextColor.color(25,167,80))
                                    .append(Component.text(worldName).color(NamedTextColor.WHITE))
                                    .append(Component.text("' has been successfully unloaded").color(TextColor.color(25,167,80))));
                        }else {
                            sender.sendMessage(Component.text("The world couldn't be unloaded correctly").color(NamedTextColor.RED));
                        }
                    }
                } else {
                    sender.sendMessage(Component.text("Error: World '").color(NamedTextColor.RED)
                            .append(Component.text(worldName).color(NamedTextColor.WHITE))
                            .append(Component.text("' does not exist").color(NamedTextColor.RED)));
                }
            }
        } else if (cmd.equalsIgnoreCase("createTempWorld")) {
            String worldName = "temp_" + sender.getName();

            // Prüfen, ob die Welt bereits existiert
            if (Bukkit.getWorld(worldName) != null) {
                sender.sendMessage(Component.text("You already have a temp world").color(NamedTextColor.RED));
                return;
            }

            // Temporäre Welt für den Spieler erstellen
            WorldCreator creator = new WorldCreator(worldName);
            World world = creator.createWorld();

            // Benachrichtigung an den Spieler
            WorldAPI.setWorldOwner(worldName,sender.getName());
            WorldAPI.tempWorld.put(sender.getName(),world);
            sender.sendMessage(
                    Component.text("Your test world has been created! ")
                            .color(TextColor.color(25, 167, 80))
                            .append(Component.text("[Click to Teleport]")
                                    .color(NamedTextColor.AQUA)
                                    .decorate(TextDecoration.BOLD)
                                    .clickEvent(ClickEvent.suggestCommand("/system server world tp " + worldName)))
            );

        }
    }

    private static File findWorldFile(File container, String worldName) {
        for (File file : container.listFiles()) {
            Set<String> stringSet = Set.of("plugins", "versions", "logs", "libraries", "debug", "config", "cache");
            if (file.isDirectory() && file.getName().equalsIgnoreCase(worldName) && !stringSet.contains(file.getName())) {
                return file;
            }
        }
        return null;
    }

    private static void sendDeleteMessage(CommandSender sender, String worldName, String result) {
        TextColor successColor = result.equals("successfully") ? NamedTextColor.GREEN : NamedTextColor.RED;
        sender.sendMessage(Component.text("World '").color(TextColor.color(25, 167, 80))
                .append(Component.text(worldName).color(NamedTextColor.WHITE))
                .append(Component.text("' was ").color(TextColor.color(25, 167, 80)))
                .append(Component.text(result).color(successColor))
                .append(Component.text(" deleted").color(TextColor.color(25, 167, 80))));
    }

    public static void delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null)return;
            for (File child : files) {
                delete(child);
            }
        }

        file.delete();
    }
}
