package de.nikey.nikeysystem.Player.API;

import java.util.UUID;

public class Punishment {
    private final UUID playerUUID;
    private final PunishmentType type;
    private final String reason;
    private final long startTime;
    private final long duration;
    private final boolean isPermanent;

    public Punishment(UUID playerUUID, PunishmentType type, String reason, long startTime, long duration, boolean isPermanent) {
        this.playerUUID = playerUUID;
        this.type = type;
        this.reason = reason;
        this.startTime = startTime;
        this.duration = duration;
        this.isPermanent = isPermanent;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public PunishmentType getType() {
        return type;
    }

    public String getReason() {
        return reason;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isPermanent() {
        return isPermanent;
    }

    public enum PunishmentType {
        MUTE,
        WARN,
        BAN,
        TEMPBAN,
        IPBAN,
        TEMPIPBAN,
        FULLBAN,
        KICK,
        FREEZE,
        UNMUTE,
        UNFREEZE,
        UNBAN;

        public static PunishmentType fromString(String type) {
            try {
                return valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

}

