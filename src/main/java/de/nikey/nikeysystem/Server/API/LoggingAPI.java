package de.nikey.nikeysystem.Server.API;

import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.ChatAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class LoggingAPI {
    private static File logFile;
    public static FileConfiguration logConfig;

    public static void initializeFiles() {
        logFile = new File(NikeySystem.getPlugin().getDataFolder(), "block_logs.yml");
        if (!logFile.exists()) {
            try {
                logFile.getParentFile().mkdirs();
                logFile.createNewFile();
            } catch (IOException e) {
                ChatAPI.sendManagementMessage(Component.text("Couldn't create logging file: ").color(NamedTextColor.RED)
                        .append(Component.text(e.getMessage()).color(NamedTextColor.WHITE)), ChatAPI.ManagementType.ERROR,true);
                e.printStackTrace();
            }
        }
        logConfig = YamlConfiguration.loadConfiguration(logFile);
    }

    public static void saveLogs() {
        try {
            logConfig.save(logFile);
        } catch (IOException e) {
            ChatAPI.sendManagementMessage(Component.text("Couldn't save logging file: ").color(NamedTextColor.RED)
                    .append(Component.text(e.getMessage()).color(NamedTextColor.WHITE)), ChatAPI.ManagementType.ERROR,true);
            e.printStackTrace();
        }
    }

    public enum LoggingType {
        PLACE("placed"),
        BREAK("broken"),
        EMPTY_BUCKET("placed_using_bucket"),
        FILL_BUCKET("picked_up_using_bucket"),
        EXPLODE_END_CRYSTAL("exploded_using_end_crystal"),
        EXPLODE_TNT("exploded_using_TNT"),
        EXPLODE_CREEPER("exploded_using_creeper"),
        EXPLODE_BLOCK("exploded"),
        SIGN("changed");

        private final String name;

        LoggingType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
