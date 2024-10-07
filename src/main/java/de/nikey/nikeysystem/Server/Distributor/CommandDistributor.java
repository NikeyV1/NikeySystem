package de.nikey.nikeysystem.Server.Distributor;

import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.HideAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.Server.API.CommandAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandDistributor {

    public static void loadBlockedCommands() {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();
        CommandAPI.getDisabledCommands().clear();
        CommandAPI.getDisabledCommands().addAll(config.getStringList("command.blocked commands"));
    }

    public static void saveBlockedCommands() {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();
        config.set("command.blocked commands", new ArrayList<>(CommandAPI.getDisabledCommands()));
        NikeySystem.getPlugin().saveConfig();
    }



    public static void commandDistributor(Player player, String[] args) {
        if (args[3].equalsIgnoreCase("execute")) {
            if (PermissionAPI.isAdmin(player.getName()) || PermissionAPI.isOwner(player.getName())) {
                execute(args,player);
            }
        }else if (args[3].equalsIgnoreCase("executeas")) {
            if (PermissionAPI.isAdmin(player.getName()) || PermissionAPI.isOwner(player.getName())) {
                executeas(player,args);
            }
        }else if (args[3].equalsIgnoreCase("ToggleBlock")) {
            if (PermissionAPI.isAdmin(player.getName()) || PermissionAPI.isOwner(player.getName())) {

                if (args[4].equalsIgnoreCase("default")) {
                    defaultBlock(player);
                    return;
                }

                if (CommandAPI.isBlocked(args[4])) {
                    CommandAPI.removeCommand(args[4]);
                    saveBlockedCommands();
                    player.sendMessage("§7Now §aallowing §7command: §f§n" + args[4]);
                }else {
                    CommandAPI.addCommand(args[4]);
                    saveBlockedCommands();
                    player.sendMessage("§7Now §cblocking §7command: §f§n" + args[4]);
                }
            }
        }else if (args[3].equalsIgnoreCase("list")) {
            List<String> messages = new ArrayList<>(CommandAPI.getDisabledCommands());

            if (messages.isEmpty()) {
                player.sendMessage("§8There are no blocked commands!");
            }else {
                player.sendMessage("§8Blocked commands are: §f§n" + messages);
            }
        }else if (args[3].equalsIgnoreCase("help")) {
            player.sendMessage("§7The path 'System/Server/Command' has following sub-paths: §fexecute </Command>, executeas <PlayerName> </Command>, ToggleBlock <Command>.");
        }
    }

    public static void defaultBlock(Player sender) {
        List<String> defaultCmd = new ArrayList<>();
        defaultCmd.add("plugins");
        defaultCmd.add("pl");
        defaultCmd.add("version");
        defaultCmd.add("ver");
        defaultCmd.add("about");
        defaultCmd.add("execute");
        defaultCmd.add("minecraft:");
        defaultCmd.add("minecraft:execute");
        defaultCmd.add("bukkit");
        defaultCmd.add("?");
        for (String cmd : defaultCmd) {
            if (!CommandAPI.isBlocked(cmd)) {
                CommandAPI.addCommand(cmd);
                saveBlockedCommands();
            }
        }
        sender.sendMessage("§7Now §cblocking §7default commands");
    }

    public static void execute(String[] args, Player sender) {
        if (args.length > 4) {
            // Alle Argumente ab dem dritten (Index 2) zu einem String zusammenfügen
            StringBuilder commandBuilder = new StringBuilder();
            for (int i = 4; i < args.length; i++) {
                commandBuilder.append(args[i]).append(" ");
            }

            // Den zusammengefügten Befehl als String speichern
            String command = commandBuilder.toString().trim();
            String cmd = command;
            if (command.startsWith("/")) {
                command = command.substring(1);
            }

            // Den Befehl ausführen
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);

            sender.sendMessage("§aSuccessfully executed command: §f§n"+ cmd);
        }
    }

    public static void executeas(Player sender, String[] args) {
        if (args.length > 5) {
            Player player = Bukkit.getPlayer(args[4]);
            if (player == null|| !HideAPI.canSee(sender,player)) {
                sender.sendMessage("§cError: player not found");
                return;
            }
            // Alle Argumente ab dem dritten (Index 2) zu einem String zusammenfügen
            StringBuilder commandBuilder = new StringBuilder();
            for (int i = 5; i < args.length; i++) {
                commandBuilder.append(args[i]).append(" ");
            }

            // Den zusammengefügten Befehl als String speichern
            String command = commandBuilder.toString().trim();
            String cmd = command;
            if (command.startsWith("/")) {
                command = command.substring(1);
            }

            // Den Befehl ausführen

            try {
                player.performCommand(command);
            }catch (CommandException exception) {
                sender.sendMessage("§cError: error while executing command: §f§n"+ cmd);
            }

            sender.sendMessage("§aSuccessfully executed as "+player.getName() + " command: §f§n" + cmd);
        }
    }
}
