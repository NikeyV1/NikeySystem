package de.nikey.nikeysystem.Server.Distributor;

import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.ChatAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.bukkit.Bukkit.getServer;

public class BackupDistributor {

    private static final File backupFolder = new File(NikeySystem.getPlugin().getDataFolder().getParentFile().getParent(), "Backups");
    private static long backupIntervalMillis;
    private static long backupDeleteTimeMillis;
    private static long lastBackupTimestamp;
    private static int autoBackupTaskId = -1;

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
            setAutoInterval(sender, args[4]);
        }else if (cmd.equalsIgnoreCase("setdeletetime")) {
            if (args.length < 5) {
                return;
            }
            setDeleteTime(sender, args[4]);
        }
    }

    private static void setDeleteTime(CommandSender sender, String time) {
        try {
            long hours = Long.parseLong(time);
            backupDeleteTimeMillis = TimeUnit.HOURS.toMillis(hours);
            sender.sendMessage("§aOld backups will now be deleted after " + hours + " hours.");
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid number: " + time);
        }
    }

    private static void createBackup(String name, Player sender) {
        File backupDestination = new File(backupFolder, name);
        NikeySystem.getPlugin().getLogger().info("Creating backup: " + backupDestination.getAbsolutePath());
        Path serverFolderPath = getServerFolder().toPath();
        Path backupsFolderPath = backupFolder.toPath();
        try {
            Files.walkFileTree(getServerFolder().toPath(), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    // Überspringe den Backup-Ordner selbst
                    Bukkit.broadcastMessage(backupFolder.toPath().toString());
                    if (dir.startsWith(backupFolder.toPath())) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    // Zielpfad für die aktuelle Datei berechnen
                    Path target = backupDestination.toPath().resolve(getServerFolder().toPath().relativize(file));
                    Files.createDirectories(target.getParent());
                    Files.copy(file, target, StandardCopyOption.COPY_ATTRIBUTES);
                    return FileVisitResult.CONTINUE;
                }
            });
            NikeySystem.getPlugin().getLogger().info("Backup created successfully");
        } catch (IOException e) {
            ChatAPI.sendManagementMessage(Component.text("Failed to create backup: " + e.getMessage()));
            NikeySystem.getPlugin().getLogger().severe("Failed to create backup: " + e.getMessage());
        }
    }

    private static void createBackup(String name) {
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
            NikeySystem.getPlugin().getLogger().info("Backup created successfully");
        } catch (IOException e) {
            ChatAPI.sendManagementMessage(Component.text("Failed to create backup: " + e.getMessage()));
            NikeySystem.getPlugin().getLogger().severe("Failed to create backup: " + e.getMessage());
        }
    }

    private static void deleteBackup(CommandSender sender, String name) {
        File backupToDelete = new File(backupFolder, name);
        Bukkit.broadcastMessage(Arrays.toString(backupFolder.listFiles()));
        if (!backupToDelete.exists()) {
            sender.sendMessage("§cBackup not found: " + name);
            return;
        }

        try {
            // Rekursiv den Ordner und seine Inhalte löschen
            Files.walkFileTree(backupToDelete.toPath(), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });

            sender.sendMessage("§aBackup deleted successfully: " + name);
            NikeySystem.getPlugin().getLogger().info("Backup deleted successfully: " + name);
        } catch (IOException e) {
            sender.sendMessage("§cFailed to delete backup: " + name);
            NikeySystem.getPlugin().getLogger().severe("Failed to delete backup: " + e.getMessage());
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

    private static void setAutoInterval(CommandSender sender, String interval) {
        try {
            long minutes = Long.parseLong(interval);
            backupIntervalMillis = TimeUnit.MINUTES.toMillis(minutes);
            sender.sendMessage("Automatic backup interval set to " + minutes + " minutes");
            scheduleAutoBackup();
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid number: " + interval);
        }
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

    public static void scheduleAutoBackup() {
        long currentTime = System.currentTimeMillis();

        // Berechne verbleibende Zeit bis zum nächsten Backup
        long timeSinceLastBackup = currentTime - lastBackupTimestamp;
        long initialDelay = Math.max(0, backupIntervalMillis - timeSinceLastBackup);

        // Plane automatisches Backup
        autoBackupTaskId = getServer().getScheduler().runTaskTimerAsynchronously(
                NikeySystem.getPlugin(),
                () -> {
                    String name = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
                    createBackup(name);
                    lastBackupTimestamp = System.currentTimeMillis();
                    saveConfiguration();
                },
                initialDelay / 50L,
                backupIntervalMillis / 50L
        ).getTaskId();
    }

    public static void startup() {
        loadConfiguration();

        // Ordner für Backups erstellen
        if (!backupFolder.exists() && !backupFolder.mkdirs()) {
            NikeySystem.getPlugin().getLogger().warning("Failed to create backup folder: " + backupFolder.getAbsolutePath());
            ChatAPI.sendManagementMessage(Component.text("Failed to create backup folder: " + backupFolder.getAbsolutePath()));
        }

        // Zeit seit dem letzten Backup berechnen und AutoBackup planen
        scheduleAutoBackup();
    }

    private static void loadConfiguration() {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();
        backupIntervalMillis = TimeUnit.MINUTES.toMillis(config.getLong("backup.interval_minutes", 1440)); // 24 Stunden
        backupDeleteTimeMillis = TimeUnit.HOURS.toMillis(config.getLong("backup.delete_after_hours", 168)); // 7 Tage
        lastBackupTimestamp = config.getLong("backup.last_backup_timestamp", 0);
    }

    private static void saveConfiguration() {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();
        config.set("backup.interval_minutes", TimeUnit.MILLISECONDS.toMinutes(backupIntervalMillis));
        config.set("backup.delete_after_hours", TimeUnit.MILLISECONDS.toHours(backupDeleteTimeMillis));
        config.set("backup.last_backup_timestamp", lastBackupTimestamp);
        NikeySystem.getPlugin().saveConfig();
    }
}
