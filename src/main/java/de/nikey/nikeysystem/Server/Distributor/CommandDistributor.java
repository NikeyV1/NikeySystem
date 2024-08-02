package de.nikey.nikeysystem.Server.Distributor;

import de.nikey.nikeysystem.Player.API.PermissionAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

public class CommandDistributor {
    public static void commandDistributor(Player player, String[] args) {
        if (args[3].equalsIgnoreCase("execute")) {
            if (PermissionAPI.isAdmin(player.getName()) || PermissionAPI.isOwner(player.getName())) {
                execute(args,player);
            }
        }else if (args[3].equalsIgnoreCase("executeas")) {
            if (PermissionAPI.isAdmin(player.getName()) || PermissionAPI.isOwner(player.getName())) {
                executeas(player,args);
            }
        }else if (args[3].equalsIgnoreCase("")) {

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
                Bukkit.getServer().dispatchCommand(player, command);
            }catch (CommandException exception) {
                sender.sendMessage("§cError: error while executing command: §f§n"+ command);
            }

            sender.sendMessage("§aSuccessfully executed command: §f§n" + command);
        }
    }
}
