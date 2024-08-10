package de.nikey.nikeysystem.Server.Distributor;

import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.Server.API.CommandAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;

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
                if (CommandAPI.isBlocked(args[4])) {
                    CommandAPI.removeCommand(args[4]);
                    player.sendMessage("§1Now §aallowing  §1command: §f" + args[4]);
                }else {
                    CommandAPI.addCommand(args[4]);
                    player.sendMessage("§1Now §cblocking §1command: §f" + args[4]);
                }
            }
        }else if (args[3].equalsIgnoreCase("help")) {
            player.sendMessage("§7The path 'System/Server/Command' has following sub-paths: §fexecute </Command>, executeas <PlayerName> </Command>, ListAll <PlayerName>.");
        }
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

            // Den Befehl ausführen
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);

            sender.sendMessage("§aSuccessfully executed command: §f§n"+ command);
        }
    }

    public static void executeas(Player sender, String[] args) {
        if (args.length > 5) {
            Player player = Bukkit.getPlayer(args[4]);
            if (player == null) {
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

            // Den Befehl ausführen

            try {
                player.performCommand(command);
            }catch (CommandException exception) {
                sender.sendMessage("§cError: error while executing command: §f§n"+ command);
            }

            sender.sendMessage("§aSuccessfully executed as "+player.getName() + " command: §f§n" + command);
        }
    }
}
