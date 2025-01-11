package de.nikey.nikeysystem.Server.Functions;

import de.nikey.nikeysystem.NikeySystem;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LogFilter extends AbstractFilter {
    //Tanks to https://gist.github.com/SecretX33/0fefa5543e25c638aaadee7916767ceb

    private static final String STRING_WE_DISLIKE = "/system";

    private static final boolean USE_RAW_STRING = false;

    public LogFilter() {
        super(Filter.Result.DENY, Filter.Result.NEUTRAL);
    }

    /**
     * Here is where we decide if we want to filter out the message or not. Returning {@link Filter.Result#DENY}
     * (our {@link AbstractFilter#onMatch onMatch}) will filter out the message, and {@link Filter.Result#NEUTRAL}
     * (our {@link AbstractFilter#onMismatch onMismatch}) will leave it alone.
     */
    @NotNull
    private Result doFilter(@Nullable String message) {
        if (message == null || !message.contains(STRING_WE_DISLIKE) || !NikeySystem.getPlugin().getConfig().getBoolean("system.setting.disable_system_command_logging")) {
            return onMismatch;
        }
        return onMatch;
    }

    @Override
    public Result filter(LogEvent event) {
        Message msg = event == null ? null : event.getMessage();
        String message = msg == null ? null : (USE_RAW_STRING
                ? msg.getFormat()
                : msg.getFormattedMessage());
        return doFilter(message);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
        return doFilter(msg == null ? null : msg.toString());
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
        return doFilter(msg);
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
        String message = msg == null ? null : (USE_RAW_STRING
                ? msg.getFormat()
                : msg.getFormattedMessage());
        return doFilter(message);
    }
}
