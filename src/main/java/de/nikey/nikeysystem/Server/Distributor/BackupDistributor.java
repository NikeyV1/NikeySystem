package de.nikey.nikeysystem.Server.Distributor;

import de.nikey.nikeysystem.DataBases.BackupDatabase;
import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.ChatAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.Server.Settings.BackupSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static de.nikey.nikeysystem.Server.API.BackupAPI.formatTime;
import static de.nikey.nikeysystem.Server.API.BackupAPI.parseTime;

public class BackupDistributor {

    private static final File backupFolder = new File(NikeySystem.getPlugin().getDataFolder().getParentFile().getParent(), "Backups");

    public static void manageBackup(Player sender, String[] args) {
        String basePerm = "system.server.backup.";
        String cmd = args[3];
        if (cmd.isEmpty()) return;

        if (cmd.equalsIgnoreCase("list")) {
            if (!PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "list") && !PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "*")) return;
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
                createBackup(name,sender);
                sender.sendMessage(Component.text("Backup created: ").color(TextColor.color(138, 138, 135))
                        .append(Component.text(name).color(NamedTextColor.WHITE)));
            });
        }else if (cmd.equalsIgnoreCase("delete")) {
            if (!PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "delete") && !PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "*")) return;
            if (args.length < 5) {
                return;
            }
            if (!PermissionAPI.isManagement(sender.getUniqueId())) return;
            deleteBackup(sender, args[4]);
        }else if (cmd.equalsIgnoreCase("load")) {
            if (!PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "load") && !PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "*")) return;
            if (args.length < 5) {
                return;
            }
            if (!PermissionAPI.isManagement(sender.getUniqueId())) return;
            sender.sendMessage(Component.text("Loading backups is currently not available").color(NamedTextColor.RED));
        }else if (cmd.equalsIgnoreCase("interval")) {
            if (!PermissionAPI.isManagement(sender.getUniqueId())) return;
            if (args.length == 4) {
                String interval = BackupDatabase.loadSetting("backup_interval");

                if (interval == null) {
                    sender.sendMessage(Component.text("Backup interval is not set").color(TextColor.color(138, 138, 135)));
                    return;
                }

                if (isLong(interval)) {
                    String time = formatTime(Long.parseLong(interval));

                    sender.sendMessage(Component.text("Backup interval is currently: ").color(TextColor.color(138, 138, 135))
                            .append(Component.text(time).color(NamedTextColor.WHITE)));
                }else {
                    sender.sendMessage(Component.text("The current interval time is not set").color(TextColor.color(138, 138, 135)));
                }
            }else if (args.length == 5) {
                try {
                    if (args[4].equalsIgnoreCase("0")) {
                        BackupDatabase.removeSetting("backup_interval");
                        sender.sendMessage(Component.text("Removed/Stopped backup interval").color(TextColor.color(138, 138, 135)));
                        if (backupTask != null) {
                            backupTask.cancel();
                        }
                        return;
                    }

                    long interval = parseTime(args[4]);
                    BackupDatabase.saveSetting("backup_interval", String.valueOf(interval));

                    sender.sendMessage(Component.text("Backup interval set to: ").color(TextColor.color(138, 138, 135))
                            .append(Component.text(args[4]).color(NamedTextColor.WHITE)));

                    restartBackupScheduler(interval);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage("§cError: " + e.getMessage());
                }
            }
        }else if (cmd.equalsIgnoreCase("maxBackups")) {
            if (!PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "maxbackups") && !PermissionAPI.hasPermission(sender.getUniqueId(), basePerm + "*")) return;
            if (!PermissionAPI.isManagement(sender.getUniqueId())) return;

            if (args.length == 4) {
                String max = BackupDatabase.loadSetting("max_backups");

                if (max == null) {
                    sender.sendMessage(Component.text("Max backup limit is not set").color(TextColor.color(138, 138, 135)));
                    return;
                }

                sender.sendMessage(Component.text("Max backup limit is currently: ").color(TextColor.color(138, 138, 135))
                        .append(Component.text(max).color(NamedTextColor.WHITE)));
            } else if (args.length == 5) {
                if (isNumeric(args[4])) {
                    if (args[4].equalsIgnoreCase("0")) {
                        BackupDatabase.removeSetting("max_backups");
                        sender.sendMessage(Component.text("Removed max backup limit").color(TextColor.color(138, 138, 135)));
                        return;
                    }

                    BackupDatabase.saveSetting("max_backups", args[4]);
                    sender.sendMessage(Component.text("Max backup limit set to: ").color(TextColor.color(138, 138, 135))
                            .append(Component.text(args[4]).color(NamedTextColor.WHITE)));
                }else {
                    sender.sendMessage(Component.text("Error: Argument needs to be an integer").color(NamedTextColor.RED));
                }
            }
        }else if (cmd.equalsIgnoreCase("settings")) {
            BackupSettings.openSettingsMenu(sender);
        }
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isLong(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void createBackup(String name, @Nullable Player player ) {
        File backupDestination = new File(backupFolder, name);
        NikeySystem.getPlugin().getLogger().info("Creating backup: " + backupDestination.getAbsolutePath());

        String maxBackupsStr = BackupDatabase.loadSetting("max_backups");
        Integer maxBackups = null;
        if (maxBackupsStr != null && !maxBackupsStr.isBlank()) {
            try {
                maxBackups = Integer.parseInt(maxBackupsStr);
            } catch (NumberFormatException ignored) {}
        }

        if (maxBackups != null) {
            File[] backups = backupFolder.listFiles();
            if (backups != null) {
                List<File> sortedBackups = Arrays.stream(backups)
                        .filter(File::isDirectory)
                        .sorted(Comparator.comparingLong(File::lastModified))
                        .collect(Collectors.toList());

                int currentCount = sortedBackups.size();

                while (currentCount >= maxBackups) {
                    File oldest = sortedBackups.get(0);
                    deleteBackup(oldest.getName());
                    if (player != null) {
                        player.sendMessage(Component.text("Deleted oldest backup: ").color(TextColor.color(138, 138, 135))
                                .append(Component.text(oldest.getName()).color(NamedTextColor.WHITE)));
                    }
                    NikeySystem.getPlugin().getLogger().info("Deleted oldest backup: " + oldest.getName());
                    sortedBackups.remove(0);
                    currentCount--;
                }
            }
        }

        try {
            long freeSpace = getFreeDiskSpace(backupFolder);
            long estimatedBackupSize = estimateBackupSize(getServerFolder());
            long minimumFreeSpace = 10L * 1024 * 1024 * 1024;

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
                    if (dir.getFileName().toString().equalsIgnoreCase(backupFolder.toPath().toString())) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public @NotNull FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    try {
                        Path target = backupDestination.toPath().resolve(getServerFolder().toPath().relativize(file));
                        Files.createDirectories(target.getParent());
                        Files.copy(file, target, StandardCopyOption.COPY_ATTRIBUTES);
                        if (player != null) {
                            if (NikeySystem.getPlugin().getConfig().getBoolean("backup.settings." + player.getName() + ".showpath")) {
                                player.sendActionBar(Component.text(file.toFile().getPath()).color(TextColor.color(138, 138, 135)));
                            }
                        }
                    } catch (IOException e) {
                        if (!e.getMessage().contains("session.lock")) {
                            ChatAPI.sendManagementMessage(Component.text("Failed to copy file for backup: " + e.getMessage()), ChatAPI.ManagementType.ERROR);
                        }
                    }
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


    private static File getServerFolder() {
        return Bukkit.getServer().getWorldContainer();
    }


    public static void startup() {
        if (!backupFolder.exists() && !backupFolder.mkdirs()) {
            NikeySystem.getPlugin().getLogger().warning("Failed to create backup folder: " + backupFolder.getAbsolutePath());
            ChatAPI.sendManagementMessage(Component.text("Failed to create backup folder: " + backupFolder.getAbsolutePath()), ChatAPI.ManagementType.ERROR);
        }
        BackupDatabase.connect();


        String interval = BackupDatabase.loadSetting("backup_interval");

        if (interval == null) {
            return;
        }

        if (isLong(interval)) {
            restartBackupScheduler(Long.parseLong(interval));
        }
    }

    private static BukkitTask backupTask;

    private static void restartBackupScheduler(long interval) {
        if (backupTask != null) {
            backupTask.cancel();
        }

        backupTask = Bukkit.getScheduler().runTaskTimerAsynchronously(NikeySystem.getPlugin(), () -> {
            String name = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            createBackup(name,null);
        }, interval / 50, interval / 50); // Bukkit verwendet Ticks (1 Tick = 50 ms)

        ChatAPI.sendManagementMessage(Component.text("Backup scheduler started with interval: ", TextColor.color(138, 138, 135)).append(Component.text(formatTime(interval))), ChatAPI.ManagementType.INFO);
        NikeySystem.getPlugin().getLogger().info("Backup scheduler started with interval: " + formatTime(interval));
    }
}