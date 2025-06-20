package de.nikey.nikeysystem.General;

import com.destroystokyo.paper.profile.PlayerProfile;
import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.*;
import de.nikey.nikeysystem.Player.Distributor.ChatDistributor;
import de.nikey.nikeysystem.Server.API.LoggingAPI;
import io.papermc.paper.ban.BanListType;
import org.bukkit.*;
import org.bukkit.ban.ProfileBanList;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.nikey.nikeysystem.Player.Distributor.ChatDistributor.channels;

public class SystemCommandTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player)) {
            return Collections.emptyList();
        }

        // Check if the player has permission to use the system commands
        if (!PermissionAPI.isSystemUser(player)) {
            return Collections.emptyList();
        }

        // Handle the first argument: system
        if (args.length == 1) {
            return Arrays.asList("player", "server", "security");
        }

        // Handle the second argument: system player or system server
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("player")) {
                return Arrays.asList("hide", "permissions", "stats", "inventory", "effect", "location","profile","sound","resourcepack","chat", "moderation");
            } else if (args[0].equalsIgnoreCase("server")) {
                return Arrays.asList("command", "settings","performance","world", "backup", "logging");
            } else if (args[0].equalsIgnoreCase("security")) {
                return List.of("System-Shield");
            }
        }

        if (args.length == 3 && args[1].equalsIgnoreCase("permissions")) {
            return List.of("set","list","remove","info","reload");
        }

        // Handle the fourth argument (player name) for permissions commands that require a target player
        if (args.length == 4 && args[1].equalsIgnoreCase("permissions") && !args[2].equalsIgnoreCase("info")) {
            // Provide list of online player names as suggestions
            return GeneralAPI.handlePlayerListing((Player) sender,args,3);
        }

        if (args.length == 4 && args[1].equalsIgnoreCase("permissions") && args[2].equalsIgnoreCase("info")) {
            return GeneralAPI.handleStringListing(PermissionAPI.ROLES.keySet().stream().toList(),args[3]);
        }

        if (args.length == 5 && args[1].equalsIgnoreCase("permissions") && args[2].equalsIgnoreCase("set")) {
            return GeneralAPI.handleStringListing(PermissionAPI.ROLES.keySet().stream().toList(),args[4]);
        }
        // Handle the third argument for system player hide
        if (args.length == 3 && args[1].equalsIgnoreCase("hide")) {
            List<String> subCommands = new ArrayList<>(Arrays.asList("ToggleHide", "ToggleTrueHide", "ToggleImmunity", "List", "Settings"));
            if (!PermissionAPI.hasPermission(((Player) sender).getUniqueId(),"system.player.hide.ToggleTrueHide")) {
                // Remove admin/owner-specific commands for non-owners
                subCommands.removeAll(List.of("ToggleTrueHide"));
            }
            return subCommands;
        }

        // Handle the fourth argument (player name) for hide commands that require it
        if (args.length == 4 && args[1].equalsIgnoreCase("hide")) {
            // Provide list of online player names as suggestions
            return GeneralAPI.handlePlayerListing((Player) sender,args,3);
        }

        // Handle the fifth argument (optional "message" parameter) for ToggleHide and ToggleTrueHide
        if (args.length == 5 && args[1].equalsIgnoreCase("Hide") && (args[2].equalsIgnoreCase("ToggleHide") || args[2].equalsIgnoreCase("ToggleTrueHide"))) {
            return Collections.singletonList("message");
        }

        if (args.length == 3 && args[1].equalsIgnoreCase("stats")) {
            return Arrays.asList("Invulnerable", "Fly", "Collidable", "SleepIgnore", "Invisibility", "VisualFire", "Op", "Address","ClientName", "Locale", "Reset", "List");
        }

        // Handle the fourth argument (player name) for stats commands that require a target player
        if (args.length == 4 && args[1].equalsIgnoreCase("stats")) {
            // Provide list of online player names as suggestions
            return GeneralAPI.handlePlayerListing((Player) sender,args,3);
        }

        // Handle the third argument for system player inventory
        if (args.length == 3 && args[1].equalsIgnoreCase("inventory")) {
            return Arrays.asList("add", "remove", "openinv", "openec", "openeq","settings");
        }

        if (args.length == 4 && args[1].equalsIgnoreCase("inventory")) {
            if (args[2].equalsIgnoreCase("add") || args[2].equalsIgnoreCase("remove")) {
                return GeneralAPI.handlePlayerListing((Player) sender,args,3);
            } else if (args[2].equalsIgnoreCase("openec") || args[2].equalsIgnoreCase("openeq")) {
                return GeneralAPI.handlePlayerListing((Player) sender,args,3);
            } else if (args[2].equalsIgnoreCase("openinv")) {
                List<String> playerNames = GeneralAPI.handleStringListing(Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).collect(Collectors.toList()),args[3]);
                for (InventoryType type : InventoryType.values()) {
                    if (!type.name().equalsIgnoreCase("Player")&& NikeySystem.getPlugin().getConfig().getBoolean("inventory.settings." + player.getName() + ".showinvtype")) {
                        playerNames.add(type.name());
                    }
                }
                return playerNames;
            }
        }

        if (args.length == 5 && args[1].equalsIgnoreCase("inventory")) {
            if (args[2].equalsIgnoreCase("add") || args[2].equalsIgnoreCase("remove")) {
                return GeneralAPI.handleStringListing(Arrays.stream(Material.values())
                        .map(material -> material.name().toLowerCase())
                        .collect(Collectors.toList()),args[4]);
            } else if (args[2].equalsIgnoreCase("openec") || args[2].equalsIgnoreCase("openeq")) {
                return GeneralAPI.handlePlayerListing((Player) sender,args,4);
            } else if (args[2].equalsIgnoreCase("openinv")) {
                List<String> playerNames = GeneralAPI.handlePlayerListing((Player) sender,args,4);
                for (InventoryType type : InventoryType.values()) {
                    if (!type.name().equalsIgnoreCase("Player") && NikeySystem.getPlugin().getConfig().getBoolean("inventory.settings." + player.getName() + ".showinvtype")) {
                        playerNames.add(type.name());
                    }
                }
                return playerNames;
            }
        }

        // Handle the sixth argument (amount) for add/remove commands
        if ( args.length == 6 && (args[2].equalsIgnoreCase("add") || args[2].equalsIgnoreCase("remove"))) {
            return Collections.singletonList("1");  // Default amount suggestion
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("security")) {
            return List.of("System-Shield");
        }

        // Handle the third argument: system security System-Shield
        if (args.length == 3 && args[1].equalsIgnoreCase("System-Shield")) {
            return Arrays.asList("enable", "disable", "list");
        }

        // Handle the fourth argument (player name) for enable, disable, list commands
        if (args.length == 4 && args[1].equalsIgnoreCase("System-Shield")) {
            if (args[2].equalsIgnoreCase("enable") || args[2].equalsIgnoreCase("disable") || args[2].equalsIgnoreCase("list")) {
                return GeneralAPI.handlePlayerListing((Player) sender,args,3);
            }
        }

        // Handle the fifth argument (Ask) for enable/disable commands
        if (PermissionAPI.isOwner(player.getUniqueId()) &&args.length == 5 && args[1].equalsIgnoreCase("System-Shield") &&
                (args[2].equalsIgnoreCase("enable") || args[2].equalsIgnoreCase("disable"))) {
            return List.of("Ask");
        }

        if (args.length == 3 && args[1].equalsIgnoreCase("command")) {
            return Arrays.asList("execute", "executeas", "ToggleBlock","BlockPlayer", "list", "help");
        }

        // Handle the fourth argument (command or player name) for execute, executeas, ToggleBlock
        if (args.length == 4 && args[1].equalsIgnoreCase("command")) {
            if (args[2].equalsIgnoreCase("executeas")) {
                return GeneralAPI.handlePlayerListing((Player) sender,args,3);
            } else if (args[2].equalsIgnoreCase("ToggleBlock")) {
                return GeneralAPI.handleStringListing(new ArrayList<>(Bukkit.getCommandMap().getKnownCommands().keySet()),args[3]);
            }else if (args[2].equalsIgnoreCase("execute")) {
                return GeneralAPI.handleStringListing(new ArrayList<>(Bukkit.getCommandMap().getKnownCommands().keySet()),args[3]);
            }else if (args[2].equalsIgnoreCase("BlockPlayer")) {
                return GeneralAPI.handlePlayerListing((Player) sender,args,3);
            }
        }

        if (args.length == 5 && args[2].equalsIgnoreCase("executeas")) {
            return GeneralAPI.handleStringListing(new ArrayList<>(Bukkit.getCommandMap().getKnownCommands().keySet()),args[4]);
        }

        if (args.length == 5 && args[2].equalsIgnoreCase("BlockPlayer")) {
            return GeneralAPI.handleStringListing(Bukkit.getCommandMap().getKnownCommands().keySet().stream().collect(Collectors.toList()),args[4]);
        }

        if (args.length == 3 && args[1].equalsIgnoreCase("effect")) {
            return Arrays.asList("give", "remove", "clear", "list");
        }

        // Handle the fourth argument: player name or TargetEntity
        if (args.length == 4 && args[1].equalsIgnoreCase("effect")) {
            List<String> playerNames = GeneralAPI.handlePlayerListing((Player) sender,args,3);
            playerNames.add("TargetEntity"); // Add the special case for targeting an entity in sight
            return playerNames;
        }

        // Handle the fifth argument: effect type for give/remove commands
        if (args.length == 5 && args[1].equalsIgnoreCase("effect") && (args[2].equalsIgnoreCase("give") || args[2].equalsIgnoreCase("remove"))) {
            return GeneralAPI.handleStringListing(Arrays.stream(PotionEffectType.values()).map(PotionEffectType::getName).collect(Collectors.toList()),args[4]);
        }

        // Handle the sixth argument: duration for give command
        if (args.length == 6 && args[2].equalsIgnoreCase("give")) {
            return Collections.singletonList("600"); // Default duration suggestion
        }

        // Handle the seventh argument: amplifier for give command
        if (args.length == 7 && args[2].equalsIgnoreCase("give")) {
            return Arrays.asList("1", "2", "3", "4", "5");
        }

        if (args.length >= 3 && args[1].equalsIgnoreCase("location")) {
            if (args.length == 3) {
                return Arrays.asList("getLocation", "tp", "lastseen", "placeGuard", "removeGuard", "listGuard", "settings");
            }

            String subCommand = args[2].toLowerCase();

            // Tab-Complete für 'getLocation', 'tp', 'lastseen', 'removeGuard'
            if (args.length == 4) {
                if (subCommand.equalsIgnoreCase("getlocation") || subCommand.equalsIgnoreCase("tp") || subCommand.equalsIgnoreCase("lastseen")) {
                    // Liste der online Spieler für getLocation, tp, lastseen
                    return GeneralAPI.handlePlayerListing((Player) sender,args,3);
                } else if (subCommand.equals("removeguard") || subCommand.equalsIgnoreCase("settings")) {
                    // Liste der Guards für removeGuard
                    return new ArrayList<>(LocationAPI.guardLocations.keySet());
                }
            }
            if (subCommand.equalsIgnoreCase("listGuard") && args.length == 4) {
                return List.of("show");
            }

            // Tab-Complete für 'placeGuard': Erwarte einen Guard-Namen
            if (subCommand.equals("placeguard") && args.length == 4) {
                return Collections.singletonList("name"); // Beispiel für den Guard-Namen
            }

            // Tab-Complete für 'tp' mit Koordinaten x, y, z und Welt
            if (subCommand.equals("tp")) {
                if (args.length == 5 || args.length == 6 || args.length == 7) {
                    return Collections.singletonList("10");
                } else if (args.length == 8) {
                    return GeneralAPI.handleStringListing(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()),args[7]);
                }
            }

            // Tab-Complete für die Range bei 'placeGuard'
            if (subCommand.equals("placeguard") && args.length == 5) {
                return Collections.singletonList("12"); // Beispiel für eine Zahl
            }
        }

        if (args.length == 3 && args[1].equalsIgnoreCase("performance")) {
            return new ArrayList<>(Arrays.asList("toggletpsbar","servertick","ping","entitys"));
        }

        // Handle the fourth argument (player name) for permissions commands that require a target player
        if (args.length == 4 && args[1].equalsIgnoreCase("performance")) {
            // Provide list of online player names as suggestions
            return GeneralAPI.handlePlayerListing((Player) sender,args,3);
        }

        if (args.length == 3 && args[1].equalsIgnoreCase("profile")) {
            return new ArrayList<>(Arrays.asList("skin","name"));
        }

        // Handle the fourth argument (player name) for permissions commands that require a target player
        if (args.length == 4 && args[1].equalsIgnoreCase("profile")) {
            // Provide list of online player names as suggestions
            return new ArrayList<>(Arrays.asList("set","reset"));
        }

        if (args.length == 5 && args[1].equalsIgnoreCase("profile")) {
            return GeneralAPI.handlePlayerListing((Player) sender,args,4);
        }

        if (args.length == 6 && args[1].equalsIgnoreCase("profile") && args[2].equalsIgnoreCase("set")) {
            return GeneralAPI.handlePlayerListing((Player) sender,args,5);
        }

        if (args.length == 3 && args[1].equalsIgnoreCase("sound")) {
            return new ArrayList<>(Arrays.asList("play","stopall","queue","showqueue","clearqueue","removequeue"));
        }

        if (args.length == 4 && args[1].equalsIgnoreCase("sound")) {
            List<String> list = new ArrayList<>(Objects.requireNonNull(GeneralAPI.handlePlayerListing((Player) sender, args, 3)));
            if (args[2].equalsIgnoreCase("play"))list.add("all");
            return list;
        }

        if (args[1].equalsIgnoreCase("sound") && args[2].equalsIgnoreCase("play")) {
            if (args.length == 5) {
                return new ArrayList<>(Arrays.asList("custom","minecraft"));
            }else if (args.length == 6) {
                return new ArrayList<>(Arrays.asList("stayinalive","ambient.cave"));
            }else if (args.length == 7) {
                return new ArrayList<>(List.of("1.0"));
            }else if (args.length == 8) {
                return new ArrayList<>(List.of("1.0"));
            }
        }

        if (args[1].equalsIgnoreCase("sound") && args[2].equalsIgnoreCase("removequeue")) {
            if (args.length == 5) {
                return new ArrayList<>(Arrays.asList("1","2","5"));
            }
        }

        if (args[1].equalsIgnoreCase("sound") && args[2].equalsIgnoreCase("queue")) {
            if (args.length == 5) {
                return new ArrayList<>(Arrays.asList("custom","minecraft"));
            }else if (args.length == 6) {
                return new ArrayList<>(Arrays.asList("stayinalive","ambient.cave"));
            }else if (args.length == 7) {
                return new ArrayList<>(List.of("1.0"));
            }else if (args.length == 8) {
                return new ArrayList<>(List.of("1.0"));
            }else if (args.length == 9) {
                return new ArrayList<>(List.of("273"));
            }
        }


        if (args[1].equalsIgnoreCase("ResourcePack") && args.length == 4) {
            return GeneralAPI.handlePlayerListing((Player) sender,args,3);
        }

        if (args[1].equalsIgnoreCase("ResourcePack") && args[2].equalsIgnoreCase("download")) {
            if (args.length == 5) {
                return new ArrayList<>(List.of("uri"));
            }else if (args.length == 6) {
                return new ArrayList<>(List.of("hash"));
            }
        }

        if (args[1].equalsIgnoreCase("ResourcePack") && args[2].equalsIgnoreCase("remove")) {
            if (args.length == 4) {
                return new ArrayList<>(Arrays.asList("1","2"));
            }
        }

        if (args.length == 3 && args[1].equalsIgnoreCase("resourcepack")) {
            return new ArrayList<>(Arrays.asList("download","clear","remove"));
        }

        if (args.length == 3 && args[1].equalsIgnoreCase("world")) {
            return new ArrayList<>(Arrays.asList("create","delete","tp","list","settings","load","createTempWorld","unload"));
        }

        if (args[1].equalsIgnoreCase("world") && args[2].equalsIgnoreCase("create")) {
            if (args.length == 4) {
                return new ArrayList<>(List.of("name"));
            }else if (args.length == 5) {
                return new ArrayList<>(List.of("seed"));
            }else if (args.length == 6) {
                return GeneralAPI.handleStringListing(Arrays.stream(World.Environment.values()).map(World.Environment::name).collect(Collectors.toList()),args[5]);
            }else if (args.length == 7) {
                return GeneralAPI.handleStringListing(Arrays.stream(WorldType.values()).map(WorldType::name).collect(Collectors.toList()),args[6]);
            }else if (args.length == 8) {
                return new ArrayList<>(Arrays.asList("true","false"));
            }
        }

        if (args[1].equalsIgnoreCase("world") && args[2].equalsIgnoreCase("delete")) {
            if (args.length == 4) {
                return GeneralAPI.handleStringListing(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()),args[3]);
            }
        }

        if (args[1].equalsIgnoreCase("world") && args[2].equalsIgnoreCase("tp")) {
            if (args.length == 4) {
                return GeneralAPI.handleStringListing(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()),args[3]);
            }
        }

        if (args[1].equalsIgnoreCase("world") && args[2].equalsIgnoreCase("load")) {
            if (args.length == 4) {
                File worldContainer = Bukkit.getWorldContainer();

                List<String> worlds = new ArrayList<>();
                if (worldContainer.listFiles() == null)return Collections.emptyList();

                if (!args[3].isEmpty()) {
                    for (File file : worldContainer.listFiles()) {
                        if (file.isDirectory()) {
                            String worldName = file.getName();
                            List<String > folder = Arrays.asList("plugins","versions","logs","libraries","debug","config","cache","crash-reports","Backups");

                            if (Bukkit.getWorld(worldName) == null && !folder.contains(worldName)) {
                                if (worldName.toLowerCase().startsWith(args[3].toLowerCase())) {
                                    worlds.add(worldName);
                                }
                            }
                        }
                    }
                }else {
                    for (File file : worldContainer.listFiles()) {
                        if (file.isDirectory()) {
                            String worldName = file.getName();
                            List<String > folder = Arrays.asList("plugins","versions","logs","libraries","debug","config","cache","crash-reports","Backups");

                            if (Bukkit.getWorld(worldName) == null && !folder.contains(worldName)) {
                                worlds.add(worldName);
                            }
                        }
                    }
                }
                Collections.sort(worlds);
                return worlds;
            }
        }

        if (args[1].equalsIgnoreCase("world") && args[2].equalsIgnoreCase("unload")) {
            if (args.length == 4) {
                List<World> worlds = Bukkit.getWorlds();
                List<String> names = new ArrayList<>();
                for (World world : worlds) {
                    names.add(world.getName());
                }
                return GeneralAPI.handleStringListing(names,args[3]);
            }
        }

        if (args.length == 3 && args[1].equalsIgnoreCase("backup")) {
            if (PermissionAPI.isManagement(((Player) sender).getUniqueId())) {
                return new ArrayList<>(Arrays.asList("list","create","delete","load","interval","maxbackups", "settings"));
            }else {
                return new ArrayList<>(Arrays.asList("list","create", "settings"));
            }
        }

        if (args.length == 4 && args[1].equalsIgnoreCase("backup")) {
            if (args[2].equalsIgnoreCase("create")) {
                return new ArrayList<>(List.of("name"));
            }
        }

        if (args.length == 4 && args[1].equalsIgnoreCase("backup")) {
            if (args[2].equalsIgnoreCase("delete") || args[2].equalsIgnoreCase("load")) {
                File[] backups = new File(NikeySystem.getPlugin().getDataFolder().getParentFile().getParent(), "Backups").listFiles();

                if (backups == null) {
                    return Collections.emptyList();
                }

                ArrayList<String> backupNames = new ArrayList<>();
                if (!args[3].isEmpty()) {
                    for (File backup : backups) {
                        if (backup.getName().toLowerCase().startsWith(args[3].toLowerCase())) {
                            backupNames.add(backup.getName());
                        }
                    }
                }else {
                    for (File backup : backups) {
                        backupNames.add(backup.getName());
                    }
                }
                Collections.sort(backupNames);
                return backupNames;
            }

            if (args[2].equalsIgnoreCase("interval")) {
                return Arrays.asList("1d","1w","30m","10h","0");
            }

            if (args[2].equalsIgnoreCase("maxbackups")) {
                return Arrays.asList("4","6","8");
            }
        }

        if (args.length == 3 && args[1].equalsIgnoreCase("logging")) {
            return new ArrayList<>(Arrays.asList("blocklog","clearblocklog", "cleanup", "filter","settings"));
        }

        if (args.length >= 4 && args[1].equalsIgnoreCase("logging")) {
            if (args[2].equalsIgnoreCase("blocklog") || args[2].equalsIgnoreCase("clearblocklog")) {
                Block targetBlockExact = ((Player) sender).getTargetBlockExact(12);
                if (targetBlockExact == null)return Collections.emptyList();
                if (args.length == 4) {
                    return List.of(String.valueOf(targetBlockExact.getX()));
                }else if (args.length == 5) {
                    return List.of(String.valueOf(targetBlockExact.getY()));
                }else if (args.length == 6) {
                    return List.of(String.valueOf(targetBlockExact.getZ()));
                }
            }else if (args[2].equalsIgnoreCase("cleanup") && args.length == 4) {
                return Arrays.asList("1d","1w","30m","10h");
            }else if (args[2].equalsIgnoreCase("filter") ) {
                if (args.length == 4) {
                    List<String> collect = new ArrayList<>(Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).toList());
                    collect.add("null");
                    return GeneralAPI.handleStringListing(collect, args[3]) ;
                }else if (args.length == 5){
                    return Arrays.asList("10","15","20","infinity");
                }else if (args.length == 6) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM");
                    List<String> list = new ArrayList<>();
                    list.add(sdf.format(Calendar.getInstance().getTime()));
                    list.add("null");
                    return list;
                }else if (args.length == 7) {
                    return Arrays.asList("1d","1w","30m","10h","infinity");
                }else if (args.length == 8) {
                    List<String> collect = Arrays.asList("placed", "broken", "placed_using_bucket", "picked_up_using_bucket", "exploded_using_end_crystal", "exploded_using_TNT"
                            , "exploded_using_creeper", "exploded", "put", "took");
                    return GeneralAPI.handleStringListing(collect,args[7]);
                }
            }
        }

        if (args.length == 3 && args[1].equalsIgnoreCase("chat")) {
            return new ArrayList<>(Arrays.asList("channel","mute","deletemsg","viewmsgs"));
        }

        if (args[2].equalsIgnoreCase("channel") && args[1].equalsIgnoreCase("chat")) {
            if (args.length == 4) {
                return Arrays.asList("create", "join", "leave", "list", "messages", "open", "close", "invite", "delete", "kick");
            } else if (args.length == 5 && args[2].equalsIgnoreCase("channel")) {
                String subCommand = args[3];
                List<String> completions = new ArrayList<>();

                if (subCommand.equalsIgnoreCase("join") || subCommand.equalsIgnoreCase("invite") || subCommand.equalsIgnoreCase("open") || subCommand.equalsIgnoreCase("close")) {
                    ChatDistributor.channels.values().forEach(channel -> completions.add(channel.getId().toString()));
                }

                if (subCommand.equalsIgnoreCase("create")) {
                    completions.add("<channelName>");
                }

                if (subCommand.equals("kick")) {
                    UUID channelId = ChatDistributor.playerChannels.get(player.getUniqueId());
                    if (channelId == null) {
                        return Collections.emptyList();
                    }

                    Channel channel = channels.get(channelId);
                    if (channel == null || !channel.getOwner().equals(player.getUniqueId())) {
                        return Collections.emptyList();
                    }

                    completions.addAll(channel.getMembers().stream()
                            .map(Bukkit::getOfflinePlayer)
                            .map(OfflinePlayer::getName)
                            .filter(Objects::nonNull) // Entferne potenzielle Nullwerte
                            .sorted()
                            .toList());
                }

                return GeneralAPI.handleStringListing(completions,args[4]);
            } else if (args.length == 6 && args[3].equalsIgnoreCase("invite")) {
                return GeneralAPI.handlePlayerListing((Player) sender,args,5);
            }
        }

        if (args[1].equalsIgnoreCase("chat") && args[2].equalsIgnoreCase("mute")) {
            if (args.length == 4) {
                return Arrays.asList("mute", "unmute", "get");
            } else if (args.length == 5) {
                if (args[3].equalsIgnoreCase("unmute")) {
                    ArrayList<String> comps = new ArrayList<>();
                    for (UUID uuid : MuteAPI.getMutedPlayers()) {
                        comps.add(Bukkit.getOfflinePlayer(uuid).getName());
                    }
                    return GeneralAPI.handleStringListing(comps,args[4]);
                }

                return GeneralAPI.handlePlayerListing((Player) sender,args,4);
            } else if (args.length == 6 && args[3].equalsIgnoreCase("mute")) {
                return Arrays.asList("1d","1w","30m","10h");
            }
        }

        if (args[1].equalsIgnoreCase("chat") ) {
            if (args[2].equalsIgnoreCase("deletemsg")) {
                if (args.length == 4) {
                    return GeneralAPI.handlePlayerListing((Player) sender, args,3);
                } else if (args.length == 5 || args.length == 6) {
                    return IntStream.rangeClosed(1,30).mapToObj(String::valueOf).filter(s -> s.startsWith(args[args.length-1])).toList();
                }
            }else if (args[2].equalsIgnoreCase("viewmsgs")) {
                if (args.length == 4) {
                    return GeneralAPI.handlePlayerListing((Player) sender, args,3);
                }
            }
        }

        if (args.length == 3 && args[1].equalsIgnoreCase("moderation")) {
            return new ArrayList<>(Arrays.asList("manage","tempban","ban","freeze","unfreeze","unban","banlist","history"));
        }

        if (args.length == 4 && args[1].equalsIgnoreCase("moderation")) {
            if (args[2].equalsIgnoreCase("freeze") ||args[2].equalsIgnoreCase("manage")) {
                return GeneralAPI.handlePlayerListing(player, args, 3);
            }

            if (args[2].equalsIgnoreCase("ban") || args[2].equalsIgnoreCase("tempban") || args[2].equalsIgnoreCase("history")) {
                return GeneralAPI.handleStringListing(Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).collect(Collectors.toList()),args[3]);
            }

            if (args[2].equalsIgnoreCase("unfreeze")) {
                ArrayList<String> comps = new ArrayList<>();
                for (UUID uuid : ModerationAPI.getFrozenPlayers()) {
                    comps.add(Bukkit.getOfflinePlayer(uuid).getName());
                }
                return GeneralAPI.handleStringListing(comps,args[3]);
            }

            if (args[2].equalsIgnoreCase("unban")) {
                ProfileBanList banList = Bukkit.getBanList(BanListType.PROFILE);
                ArrayList<String> comps = new ArrayList<>();
                for (BanEntry entry : banList.getEntries()) {
                    comps.add(banList.getBanEntry((PlayerProfile) entry.getBanTarget()).getBanTarget().getName());
                }
                return GeneralAPI.handleStringListing(comps,args[3]);
            }
        }

        if (args.length == 5 && args[1].equalsIgnoreCase("moderation") && (args[2].equalsIgnoreCase("tempban") || args[2].equalsIgnoreCase("freeze"))) {
            return Arrays.asList("1h", "12h", "2d", "5d");
        }

        if (args.length == 5 && args[1].equalsIgnoreCase("moderation") && (args[2].equalsIgnoreCase("history"))) {
            UUID id = Bukkit.getPlayerUniqueId(args[3]);
            if (id == null) return Collections.emptyList();

            List<Punishment> punishments = NikeySystem.getManager().getHistory(id);
            if (punishments == null || punishments.isEmpty()) return Collections.emptyList();

            int totalPages = (int) Math.ceil(punishments.size() / 20.0);
            List<String> suggestions = new ArrayList<>();

            for (int i = 1; i <= Math.min(5, totalPages); i++) {
                suggestions.add(String.valueOf(i));
            }

            return GeneralAPI.handleStringListing(suggestions,args[4]);
        }

        if (args.length == 5 && args[1].equalsIgnoreCase("moderation") && args[2].equalsIgnoreCase("ban")) {
            return List.of("<Reason>");
        }

        if (args.length == 6 && args[1].equalsIgnoreCase("moderation") && args[2].equalsIgnoreCase("tempban")) {
            return List.of("<Reason>");
        }

        return Collections.emptyList();
    }
}