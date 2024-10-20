package de.nikey.nikeysystem.General;

import de.nikey.nikeysystem.Player.API.LocationAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SystemCommandTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }

        Player player = (Player) sender;

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
                return Arrays.asList("hide", "permissions", "stats", "inventory", "effect", "mute", "location");
            } else if (args[0].equalsIgnoreCase("server")) {
                return Arrays.asList("command", "settings");
            } else if (args[0].equalsIgnoreCase("security")) {
                return Arrays.asList("System-Shield");
            }
        }

        // Handle the third argument for system player permissions
        if (args.length == 3 && args[1].equalsIgnoreCase("permissions")) {
            List<String> subCommands = new ArrayList<>(Arrays.asList("ToggleAdmin", "ToggleModerator", "List", "ListAll"));
            if (!PermissionAPI.isOwner(player.getName())) {
                // Remove owner-specific commands for non-owners
                subCommands.remove("ToggleAdmin");
            }
            return subCommands;
        }

        // Handle the fourth argument (player name) for permissions commands that require a target player
        if (args.length == 4 && args[1].equalsIgnoreCase("permissions")) {
            // Provide list of online player names as suggestions
            return GeneralAPI.getOnlinePlayers((Player) sender).stream().map(Player::getName).collect(Collectors.toList());
        }
        // Handle the third argument for system player hide
        if (args.length == 3 && args[1].equalsIgnoreCase("hide")) {
            List<String> subCommands = new ArrayList<>(Arrays.asList("ToggleHide", "ToggleTrueHide", "ToggleImmunity", "ToggleTrueImmunity", "List", "Settings"));
            if (!PermissionAPI.isOwner(player.getName())) {
                // Remove admin/owner-specific commands for non-owners
                subCommands.removeAll(Arrays.asList("ToggleTrueHide", "ToggleTrueImmunity"));
            }
            return subCommands;
        }

        // Handle the fourth argument (player name) for hide commands that require it
        if (args.length == 4 && args[1].equalsIgnoreCase("hide")) {
            // Provide list of online player names as suggestions
            return GeneralAPI.getOnlinePlayers((Player) sender).stream().map(Player::getName).collect(Collectors.toList());
        }

        // Handle the fifth argument (optional "message" parameter) for ToggleHide and ToggleTrueHide
        if (args.length == 5 && args[1].equalsIgnoreCase("Hide") && (args[2].equalsIgnoreCase("ToggleHide") || args[2].equalsIgnoreCase("ToggleTrueHide"))) {
            return Collections.singletonList("message");
        }

        if (args.length == 3 && args[1].equalsIgnoreCase("stats")) {
            return Arrays.asList("Invulnerable", "Fly", "Collidable", "SleepIgnore", "Invisibility", "VisualFire", "Op", "Reset", "List");
        }

        // Handle the fourth argument (player name) for stats commands that require a target player
        if (args.length == 4 && args[1].equalsIgnoreCase("stats")) {
            // Provide list of online player names as suggestions
            return GeneralAPI.getOnlinePlayers((Player) sender).stream().map(Player::getName).collect(Collectors.toList());
        }

        // Handle the third argument for system player inventory
        if (args.length == 3 && args[1].equalsIgnoreCase("inventory")) {
            return Arrays.asList("add", "remove", "openinv", "openec", "openeq");
        }

        // Handle the fourth argument (player name) for inventory commands that require a player
        if (args.length == 4 && args[1].equalsIgnoreCase("inventory")) {
            // Provide list of online player names as suggestions
            return GeneralAPI.getOnlinePlayers((Player) sender).stream().map(Player::getName).collect(Collectors.toList());
        }

        // Handle the fifth argument (item or target player) for add, remove, and open commands
        if (args.length == 5 && args[1].equalsIgnoreCase("inventory")) {
            if (args[2].equalsIgnoreCase("add") || args[2].equalsIgnoreCase("remove")) {
                // Provide list of material names (items) for add/remove commands
                return Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList());
            } else if (args[2].equalsIgnoreCase("openinv") || args[2].equalsIgnoreCase("openec") || args[2].equalsIgnoreCase("openeq")) {
                // Provide list of online player names for openinv, openec, and openeq
                return GeneralAPI.getOnlinePlayers((Player) sender).stream().map(Player::getName).collect(Collectors.toList());
            }
        }

        // Handle the sixth argument (amount) for add/remove commands
        if ( args.length == 6 && (args[2].equalsIgnoreCase("add") || args[2].equalsIgnoreCase("remove"))) {
            return Collections.singletonList("1");  // Default amount suggestion
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("security")) {
            return Arrays.asList("System-Shield");
        }

        // Handle the third argument: system security System-Shield
        if (args.length == 3 && args[1].equalsIgnoreCase("System-Shield")) {
            return Arrays.asList("enable", "disable", "list");
        }

        // Handle the fourth argument (player name) for enable, disable, list commands
        if (args.length == 4 && args[1].equalsIgnoreCase("System-Shield")) {
            if (args[2].equalsIgnoreCase("enable") || args[2].equalsIgnoreCase("disable") || args[2].equalsIgnoreCase("list")) {
                // Provide list of online player names as suggestions
                return GeneralAPI.getOnlinePlayers((Player) sender).stream().map(Player::getName).collect(Collectors.toList());
            }
        }

        // Handle the fifth argument (Ask) for enable/disable commands
        if (PermissionAPI.isOwner(sender.getName()) &&args.length == 5 && args[1].equalsIgnoreCase("System-Shield") &&
                (args[2].equalsIgnoreCase("enable") || args[2].equalsIgnoreCase("disable"))) {
            return Arrays.asList("Ask");
        }

        if (args.length == 3 && args[1].equalsIgnoreCase("command")) {
            return Arrays.asList("execute", "executeas", "ToggleBlock","BlockPlayer", "list", "help");
        }

        // Handle the fourth argument (command or player name) for execute, executeas, ToggleBlock
        if (args.length == 4 && args[1].equalsIgnoreCase("command")) {
            if (args[2].equalsIgnoreCase("executeas")) {
                // Provide list of online player names for executeas command
                return GeneralAPI.getOnlinePlayers((Player) sender).stream().map(Player::getName).collect(Collectors.toList());
            } else if (args[2].equalsIgnoreCase("ToggleBlock")) {
                // Provide a list of blocked or allowed commands (just as a simple example, you can modify it as needed)
                return Bukkit.getCommandMap().getKnownCommands().keySet().stream().collect(Collectors.toList());
            }else if (args[2].equalsIgnoreCase("execute")) {
                return Bukkit.getCommandMap().getKnownCommands().keySet().stream().collect(Collectors.toList());
            }else if (args[2].equalsIgnoreCase("BlockPlayer")) {
                // Provide a list of blocked or allowed commands (just as a simple example, you can modify it as needed)
                return GeneralAPI.getOnlinePlayers((Player) sender).stream().map(Player::getName).collect(Collectors.toList());
            }
        }

        // Handle the fifth argument (command for executeas) for executeas command
        if (args.length == 5 && args[2].equalsIgnoreCase("executeas")) {
            // Provide a list of commands for the executeas command
            return Bukkit.getCommandMap().getKnownCommands().keySet().stream().collect(Collectors.toList());
        }

        if (args.length == 5 && args[2].equalsIgnoreCase("BlockPlayer")) {
            // Provide a list of commands for the executeas command
            return Bukkit.getCommandMap().getKnownCommands().keySet().stream().collect(Collectors.toList());
        }

        if (args.length == 3 && args[1].equalsIgnoreCase("effect")) {
            return Arrays.asList("give", "remove", "clear", "list");
        }

        // Handle the fourth argument: player name or TargetEntity
        if (args.length == 4 && args[1].equalsIgnoreCase("effect")) {
            List<String> playerNames = GeneralAPI.getOnlinePlayers((Player) sender).stream().map(Player::getName).collect(Collectors.toList());
            playerNames.add("TargetEntity"); // Add the special case for targeting an entity in sight
            return playerNames;
        }

        // Handle the fifth argument: effect type for give/remove commands
        if (args.length == 5 && (args[2].equalsIgnoreCase("give") || args[2].equalsIgnoreCase("remove"))) {
            return Arrays.stream(PotionEffectType.values()).map(PotionEffectType::getName).collect(Collectors.toList());
        }

        // Handle the sixth argument: duration for give command
        if (args.length == 6 && args[2].equalsIgnoreCase("give")) {
            return Collections.singletonList("600"); // Default duration suggestion
        }

        // Handle the seventh argument: amplifier for give command
        if (args.length == 7 && args[2].equalsIgnoreCase("give")) {
            return Arrays.asList("1", "2", "3", "4", "5"); // Suggest some common amplifier values
        }

        if (args.length >= 3 && args[1].equalsIgnoreCase("mute")) {
            // Second argument (subcommands for mute)
            if (args.length == 3) {
                return Arrays.asList("mute", "unmute", "togglemute", "getMuted");
            }

            // Third argument (player name for mute commands)
            if (args.length == 4 && (args[2].equalsIgnoreCase("mute") || args[2].equalsIgnoreCase("unmute") ||
                    args[2].equalsIgnoreCase("togglemute") || args[2].equalsIgnoreCase("getMuted"))) {
                // Suggest all online player names
                return GeneralAPI.getOnlinePlayers((Player) sender).stream().map(Player::getName).collect(Collectors.toList());
            }

            // Fourth argument (mute duration in seconds for the mute command)
            if (args.length == 5 && args[2].equalsIgnoreCase("mute")) {
                return Collections.singletonList("60");
            }
        }

        if (args.length >= 3 && args[1].equalsIgnoreCase("location")) {
            // Second argument (subcommands for mute)
            if (args.length == 3) {
                return Arrays.asList("getLocation", "tp", "lastseen", "placeGuard", "removeGuard", "listGuard");
            }

            String subCommand = args[2].toLowerCase();

            // Tab-Complete für 'getLocation', 'tp', 'lastseen', 'removeGuard'
            if (args.length == 4) {
                if (subCommand.equalsIgnoreCase("getlocation") || subCommand.equalsIgnoreCase("tp") || subCommand.equalsIgnoreCase("lastseen")) {
                    // Liste der online Spieler für getLocation, tp, lastseen
                    return GeneralAPI.getOnlinePlayers((Player) sender).stream().map(Player::getName).collect(Collectors.toList());
                } else if (subCommand.equals("removeguard")) {
                    // Liste der Guards für removeGuard
                    return LocationAPI.guardLocations.keySet().stream().collect(Collectors.toList());
                }
            }
            if (subCommand.equalsIgnoreCase("listGuard") && args.length == 4) {
                return Arrays.asList("show");
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
                    return Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
                }
            }

            // Tab-Complete für die Range bei 'placeGuard'
            if (subCommand.equals("placeguard") && args.length == 5) {
                return Collections.singletonList("12"); // Beispiel für eine Zahl
            }
        }



        return Collections.emptyList();
    }
}
