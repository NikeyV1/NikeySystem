package de.nikey.nikeysystem.Server.History;

import de.nikey.nikeysystem.Player.API.ChatAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private final File logFile;

    public HistoryManager(File logFile) {
        this.logFile = logFile;
    }

    /**
     * Fügt einen Log-Eintrag hinzu und speichert ihn in die Datei.
     */
    public synchronized void logChange(HistoryElement element) {
        try (FileOutputStream fos = new FileOutputStream(logFile, true)) {
            element.saveTo(fos);
        } catch (IOException e) {
            ChatAPI.sendManagementMessage(Component.text("An error occurred while changing block logs: ").color(NamedTextColor.RED)
                    .append(Component.text(e.getMessage()).color(NamedTextColor.WHITE)), ChatAPI.ManagementType.ERROR,true);
            e.printStackTrace();
        }
    }

    /**
     * Lädt alle Log-Einträge aus der Datei.
     */
    public synchronized List<HistoryElement> loadLogs() {
        List<HistoryElement> history = new ArrayList<>();
        if (!logFile.isFile()) return history;

        try (FileInputStream fis = new FileInputStream(logFile)) {
            while (fis.available() > 0) {
                history.add(HistoryElement.load(fis));
            }
        } catch (IOException e) {
            ChatAPI.sendManagementMessage(Component.text("An error occurred while loading block logs: ").color(NamedTextColor.RED)
                    .append(Component.text(e.getMessage()).color(NamedTextColor.WHITE)), ChatAPI.ManagementType.ERROR,true);
            e.printStackTrace();
        }
        return history;
    }

    /**
     * Löscht alle Logs in der Datei.
     */
    public synchronized void clearLogs() {
        if (logFile.isFile() && !logFile.delete()) {
            System.err.println("Failed to delete log file.");
        }
    }

}
