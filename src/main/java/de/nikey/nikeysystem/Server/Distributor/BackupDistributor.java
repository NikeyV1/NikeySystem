package de.nikey.nikeysystem.Server.Distributor;

import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.ChatAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.bukkit.Bukkit.getServer;

public class BackupDistributor {

    private static final File backupFolder = new File(NikeySystem.getPlugin().getDataFolder().getParentFile().getParent(), "Backups");
    private long backupDeleteTimeMillis = TimeUnit.DAYS.toMillis(7);

    public static void manageBackup(Player sender, String[] args) {
        String cmd = args[3];
        if (cmd.isEmpty()) return;

        if (cmd.equalsIgnoreCase("list")) {
            listBackups(sender);
        }else if (cmd.equalsIgnoreCase("create")) {
            String name = args.length > 4 ? args[4] : new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            sender.sendMessage(Component.text("Creating backup...").color(TextColor.color(138, 138, 135)));
            Bukkit.getScheduler().runTaskAsynchronously(NikeySystem.getPlugin(), () -> {
                createBackup(name,sender);
                sender.sendMessage("§aBackup created: " + name);
            });
        }else if (cmd.equalsIgnoreCase("delete")) {
            if (args.length < 5) {
                return;
            }
            deleteBackup(sender, args[4]);
        }else if (cmd.equalsIgnoreCase("load")) {
            if (args.length < 5) {
                return;
            }
            loadBackup(sender, args[4]);
        }else if (cmd.equalsIgnoreCase("setautointerval")) {
            if (args.length < 5) {
                return;
            }
            loadBackup(sender, args[4]);
        }
    }


    private static void createBackup(String name, Player sender) {
        File backupDestination = new File(backupFolder, name);
        NikeySystem.getPlugin().getLogger().info("Creating backup: " + backupDestination.getAbsolutePath());
        try {
            Files.walkFileTree(getServerFolder().toPath(), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path target = backupDestination.toPath().resolve(getServerFolder().toPath().relativize(file));
                    Files.createDirectories(target.getParent());
                    Files.copy(file, target, StandardCopyOption.COPY_ATTRIBUTES);
                    return FileVisitResult.CONTINUE;
                }
            });
            sender.sendMessage(Component.text("Backup created successfully").color(TextColor.color(138, 138, 135)));
            NikeySystem.getPlugin().getLogger().info("Backup created successfully");
        } catch (IOException e) {
            ChatAPI.sendManagementMessage(Component.text("Failed to create backup: " + e.getMessage()));
            NikeySystem.getPlugin().getLogger().severe("Failed to create backup: " + e.getMessage());
        }
    }

    private static void deleteBackup(CommandSender sender, String name) {
        File backupToDelete = new File(backupFolder, name);
        if (!backupToDelete.exists()) {
            sender.sendMessage("§cBackup not found: " + name);
            return;
        }
        if (backupToDelete.delete()) {
            sender.sendMessage("§aBackup deleted: " + name);
        } else {
            sender.sendMessage("§cFailed to delete backup: " + name);
        }
    }

    private static void loadBackup(CommandSender sender, String name) {
        File backupToLoad = new File(backupFolder, name);
        if (!backupToLoad.exists()) {
            sender.sendMessage("§cBackup not found: " + name);
            return;
        }
        sender.sendMessage(Component.text("Loading backup: ").color(TextColor.color(138, 138, 135))
                .append(Component.text(name).color(NamedTextColor.WHITE))
                .append(Component.text(" (This will overwrite current server data)").color(TextColor.color(138, 138, 135))));
        try {
            Files.walkFileTree(backupToLoad.toPath(), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path target = getServerFolder().toPath().resolve(backupToLoad.toPath().relativize(file));
                    Files.createDirectories(target.getParent());
                    Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });
            sender.sendMessage("§aBackup loaded successfully!");
        } catch (IOException e) {
            sender.sendMessage("§cFailed to load backup: " + e.getMessage());
        }
    }

    private void setAutoInterval(CommandSender sender, String interval) {
        try {
            long minutes = Long.parseLong(interval);
            long backupIntervalMillis = TimeUnit.MINUTES.toMillis(minutes);
            sender.sendMessage("Automatic backup interval set to " + minutes + " minutes");
            scheduleAutoBackup();
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid number: " + interval);
        }
    }

    private void scheduleAutoBackup() {
        getServer().getScheduler().runTaskTimerAsynchronously(this, BackupDistributor::createBackup, 0L, backupIntervalMillis / 50L);
    }

    private static void listBackups(CommandSender sender) {
        File[] backups = backupFolder.listFiles();
        if (backups == null || backups.length == 0) {
            sender.sendMessage("§eNo backups available.");
            return;
        }
        sender.sendMessage("§eAvailable backups:");
        for (File backup : backups) {
            sender.sendMessage(" - " + backup.getName());
        }
    }

    private static File getServerFolder() {
        return getServer().getWorldContainer();
    }
}
