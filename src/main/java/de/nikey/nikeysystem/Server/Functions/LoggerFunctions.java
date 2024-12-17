package de.nikey.nikeysystem.Server.Functions;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class LoggerFunctions implements Filter {
    @Override
    public boolean isLoggable(LogRecord record) {
        String message = record.getMessage();
        if (message == null) return true;

        if (message.contains("system")) {
            return false; // Nachricht unterdrücken
        }
        return true; // Nachricht anzeigen
    }

}
