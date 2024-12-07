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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static de.nikey.nikeysystem.Server.API.BackupAPI.formatTime;
import static de.nikey.nikeysystem.Server.API.BackupAPI.parseTime;
import static org.bukkit.Bukkit.getServer;

public class BackupDistributor {

    private static final File backupFolder = new File(NikeySystem.getPlugin().getDataFolder().getParentFile().getParent(), "Backups");
    private static int autoBackupTaskId = -1;

    public static void manageBackup(Player sender, String[] args) {
        String cmd = args[3];
        if (cmd.isEmpty()) return;

        if (cmd.equalsIgnoreCase("list")) {
            listBackups(sender);
        }else if (cmd.equalsIgnoreCase("create")) {
            String name = args.length > 4 ? args[4] : new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

            File backupDestination = new File(backupFolder, name);
            if (backupDestination.exists()) {
                sender.sendMessage(Component.text("A backup with the name ").color(TextColor.color(138, 138, 135))
                        .append(Component.text(name).color(NamedTextColor.WHITE))
                        .append(Component.text(" already exists! Please choose a different name.").color(TextColor.color(138, 138, 135))));
                return;
            }

            sender.sendMessage(Component.text("Creating backup...").color(TextColor.color(138, 138, 135)));
            Bukkit.getScheduler().runTaskAsynchronously(NikeySystem.getPlugin(), () -> {
                createBackup(name);
                sender.sendMessage(Component.text("Backup created: ").color(TextColor.color(138, 138, 135))
                        .append(Component.text(name).color(NamedTextColor.WHITE)));
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
                String time = formatTime(NikeySystem.getPlugin().getConfig().getLong("settings.backup_interval"));
                if (time.isEmpty()) {
                    sender.sendMessage(Component.text("The current interval time is not set").color(TextColor.color(138, 138, 135)));
                }else {
                    sender.sendMessage(Component.text("The current interval time is ").color(TextColor.color(138, 138, 135))
                            .append(Component.text(time).color(NamedTextColor.WHITE)));
                }
                return;
            }
            try {
                long interval = parseTime(args[4]);
                NikeySystem.getPlugin().getConfig().set("settings.backup_interval", interval);
                NikeySystem.getPlugin().saveConfig();

                sender.sendMessage(Component.text("Backup interval set to: ").color(TextColor.color(138, 138, 135))
                        .append(Component.text(args[4]).color(NamedTextColor.WHITE)));

                restartBackupScheduler(interval);
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§cError: " + e.getMessage());
            }
        }else if (cmd.equalsIgnoreCase("setdeletetime")) {
            if (args.length < 5) {
                String time = formatTime(NikeySystem.getPlugin().getConfig().getLong("backup.auto_delete_interval"));
                if (time.isEmpty()) {
                    sender.sendMessage(Component.text("The current delete interval time is not set").color(TextColor.color(138, 138, 135)));
                }else {
                    sender.sendMessage(Component.text("The current delete interval time is ").color(TextColor.color(138, 138, 135))
                            .append(Component.text(time).color(NamedTextColor.WHITE)));
                }
                return;
            }

            try {
                // Zeit aus Argument parsen
                long deleteInterval = parseTime(args[4]);

                // Config-Eintrag aktualisieren
                NikeySystem.getPlugin().getConfig().set("backup.auto_delete_interval", deleteInterval);
                NikeySystem.getPlugin().saveConfig();

                // Auto-Delete-Task starten
                startAutoDeleteTask();

                sender.sendMessage(Component.text("Backup delete interval set to: ").color(TextColor.color(138, 138, 135))
                        .append(Component.text(args[4]).color(NamedTextColor.WHITE)));
                NikeySystem.getPlugin().getLogger().info("Auto-delete interval set to " + args[4]);
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§cInvalid time format: " + e.getMessage());
            }
        }
    }

    private static void createBackup(String name) {
        File backupDestination = new File(backupFolder, name);
        NikeySystem.getPlugin().getLogger().info("Creating backup: " + backupDestination.getAbsolutePath());
        try {
            long freeSpace = getFreeDiskSpace(backupFolder);
            long estimatedBackupSize = estimateBackupSize(getServerFolder());
            long minimumFreeSpace = 5L * 1024 * 1024 * 1024;

            if (freeSpace - estimatedBackupSize < minimumFreeSpace) {
                NikeySystem.getPlugin().getLogger().warning("Not enough disk space for backup. Required: "
                        + (estimatedBackupSize / (1024 * 1024)) + " MB, Available after backup: "
                        + ((freeSpace - estimatedBackupSize) / (1024 * 1024)) + " MB.");
                ChatAPI.sendManagementMessage(Component.text("Backup failed: Not enough disk space"), ChatAPI.ManagementType.ERROR);
                return;
            }

            Files.walkFileTree(getServerFolder().toPath(), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    // Überspringe den Backup-Ordner selbst
                    if (dir.getFileName().toString().equalsIgnoreCase(backupFolder.toPath().toString())) {
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
            if (!e.getMessage().contains("session.lock")) {
                ChatAPI.sendManagementMessage(Component.text("Failed to create backup: " + e.getMessage()), ChatAPI.ManagementType.ERROR);
                NikeySystem.getPlugin().getLogger().severe("Failed to create backup: " + e.getMessage());
            }
        }
    }

    private static long getFreeDiskSpace(File folder) {
        return folder.getUsableSpace();
    }

    private static long estimateBackupSize(File sourceFolder) throws IOException {
        final long[] size = {0};

        Files.walkFileTree(sourceFolder.toPath(), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                // Ignoriere den Backup-Ordner
                if (dir.getFileName().toString().equalsIgnoreCase("Backups")) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                size[0] += Files.size(file);
                return FileVisitResult.CONTINUE;
            }
        });
        return size[0];
    }

    private static void deleteBackup(CommandSender sender, String name) {
        File backupToDelete = new File(backupFolder, name);
        if (!backupToDelete.exists()) {
            sender.sendMessage("§cError: Backup not found: " + name);
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

            sender.sendMessage(Component.text("Backup ").color(TextColor.color(138, 138, 135))
                    .append(Component.text("deleted ").color(TextColor.color(217, 78, 56)))
                    .append(Component.text("successfully").color(NamedTextColor.GREEN))
                    .append(Component.text(": ")).color(TextColor.color(138, 138, 135))
                    .append(Component.text(name).color(NamedTextColor.WHITE)));
        } catch (IOException e) {
            sender.sendMessage("§cFailed to delete backup: " + name);
            NikeySystem.getPlugin().getLogger().severe("Failed to delete backup: " + e.getMessage());
        }
    }
    private static void deleteBackup(String name) {
        File backupToDelete = new File(backupFolder, name);
        if (!backupToDelete.exists()) {
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
        } catch (IOException e) {
            NikeySystem.getPlugin().getLogger().severe("Failed to delete backup: " + e.getMessage());
        }
    }

    private static void loadBackup(CommandSender sender, String name) {
        File backupToLoad = new File(backupFolder, name);
        if (!backupToLoad.exists()) {
            sender.sendMessage("§cBackup doesn't exist: " + name);
            return;
        }
        sender.sendMessage(Component.text("Loading backup: ").color(TextColor.color(138, 138, 135))
                .append(Component.text(name).color(NamedTextColor.WHITE))
                .append(Component.text(" (This will overwrite current server data)").color(TextColor.color(138, 138, 135))));

        try {
            Files.walkFileTree(backupToLoad.toPath(), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    // Zielverzeichnis prüfen: Nur wenn das Zielverzeichnis existiert, fortfahren
                    Path targetPath = getServerFolder().toPath().resolve(backupToLoad.toPath().relativize(dir));
                    Path targetDir = targetPath.resolve(backupToLoad.toPath().relativize(dir));
                    if (Files.exists(targetDir) && Files.isDirectory(targetDir)) {
                        // Wenn das Verzeichnis existiert, einfach fortfahren
                        return FileVisitResult.CONTINUE;
                    }
                    // Andernfalls überspringen wir das Verzeichnis
                    return FileVisitResult.SKIP_SUBTREE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    // Zielpfad für die Datei berechnen
                    Path targetPath = getServerFolder().toPath().resolve(backupToLoad.toPath().relativize(file));

                    if (Files.isRegularFile(file)) {
                        Path targetFile = targetPath.resolve(backupToLoad.toPath().relativize(file));
                        Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                        ChatAPI.sendManagementMessage(Component.text("Copied file:" + file.toString()), ChatAPI.ManagementType.INFO);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            ChatAPI.sendManagementMessage(Component.text("Backup loaded!"), ChatAPI.ManagementType.INFO);
        } catch (IOException e) {
            e.printStackTrace();
            return; // Fehlerbehandlung
        }

    }


    private static void listBackups(CommandSender sender) {
        File[] backups = backupFolder.listFiles();
        if (backups == null || backups.length == 0) {
            sender.sendMessage("§eNo backups available");
            return;
        }
        sender.sendMessage("§eAvailable backups:");
        for (File backup : backups) {
            sender.sendMessage(" - " + backup.getName());
        }
    }

    private static void startAutoDeleteTask() {
        long deleteInterval = NikeySystem.getPlugin().getConfig().getLong("backup.auto_delete_interval", 0);

        if (deleteInterval <= 0) {
            return;
        }

        ChatAPI.sendManagementMessage(Component.text("Backup delete scheduler started with interval: " ,ChatAPI.infoColor).append(Component.text(formatTime(deleteInterval))), ChatAPI.ManagementType.INFO,true);

        // Konvertiere das Intervall von Millisekunden in Ticks
        long intervalInTicks = deleteInterval / 50;

        Bukkit.getScheduler().runTaskTimerAsynchronously(NikeySystem.getPlugin(), () -> {
            try {
                File[] backups = backupFolder.listFiles((dir, name) -> new File(dir, name).isDirectory());
                if (backups == null || backups.length == 0) {
                    return;
                }

                // Ältestes Backup finden
                File oldestBackup = Arrays.stream(backups)
                        .min(Comparator.comparingLong(File::lastModified))
                        .orElse(null);

                long age = System.currentTimeMillis() - oldestBackup.lastModified();

                // Löschen, wenn das Intervall überschritten wurde
                if (age > deleteInterval) {
                    String backupName = oldestBackup.getName();

                    NikeySystem.getPlugin().getLogger().info("Deleting old backup: " + backupName);
                    deleteBackup(backupName);
                }
            } catch (Exception e) {
                ChatAPI.sendManagementMessage(Component.text("Failed to delete old backup: " + e.getMessage()), ChatAPI.ManagementType.ERROR);
                NikeySystem.getPlugin().getLogger().severe("Failed to delete old backup: " + e.getMessage());
            }
        }, 0L, intervalInTicks);
    }


    private static File getServerFolder() {
        return getServer().getWorldContainer();
    }


    public static void startup() {
        // Ordner für Backups erstellen
        if (!backupFolder.exists() && !backupFolder.mkdirs()) {
            NikeySystem.getPlugin().getLogger().warning("Failed to create backup folder: " + backupFolder.getAbsolutePath());
            ChatAPI.sendManagementMessage(Component.text("Failed to create backup folder: " + backupFolder.getAbsolutePath()), ChatAPI.ManagementType.ERROR);
        }

        // Zeit seit dem letzten Backup berechnen und AutoBackup planen
        long interval = NikeySystem.getPlugin().getConfig().getLong("settings.backup_interval", 0);
        if (interval > 0) {
            restartBackupScheduler(interval);
        }
        startAutoDeleteTask();
    }

    private static BukkitTask backupTask;

    private static void restartBackupScheduler(long interval) {
        // Stoppe die aktuelle Backup-Task (falls vorhanden)
        if (backupTask != null) {
            backupTask.cancel();
            NikeySystem.getPlugin().getLogger().info("Previous backup scheduler canceled");
        }

        backupTask = Bukkit.getScheduler().runTaskTimerAsynchronously(NikeySystem.getPlugin(), () -> {
            String name = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            createBackup(name);
        }, interval / 50, interval / 50); // Bukkit verwendet Ticks (1 Tick = 50 ms)
        ChatAPI.sendManagementMessage(Component.text("Backup scheduler started with interval: " ,ChatAPI.infoColor).append(Component.text(formatTime(interval))), ChatAPI.ManagementType.INFO,true);
        NikeySystem.getPlugin().getLogger().info("Backup scheduler started with interval: " + formatTime(interval));
    }



}
