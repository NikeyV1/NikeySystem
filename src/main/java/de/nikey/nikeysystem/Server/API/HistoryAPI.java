package de.nikey.nikeysystem.Server.API;

import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Server.History.HistoryManager;

import java.io.File;

public class HistoryAPI {

    private static HistoryManager historyManager;

    public static void startup() {
        File logFile = new File(NikeySystem.getPlugin().getDataFolder(), "block_history.log");
        if (!logFile.getParentFile().exists()) logFile.getParentFile().mkdirs();

        historyManager = new HistoryManager(logFile);
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }
}
