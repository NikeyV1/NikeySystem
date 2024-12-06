package de.nikey.nikeysystem.Server.History;

import org.bukkit.Location;

import java.io.*;
import java.time.Instant;
import java.util.UUID;

public class HistoryElement {
    private final UUID playerUUID;
    private final Location location;
    private final String blockType;
    private final Instant timestamp;

    public HistoryElement(UUID playerUUID, Location location, String blockType) {
        this.playerUUID = playerUUID;
        this.location = location;
        this.blockType = blockType;
        this.timestamp = Instant.now();
    }

    public void saveTo(OutputStream out) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(out)) {
            dos.writeUTF(playerUUID.toString());
            dos.writeUTF(location.getWorld().getName());
            dos.writeDouble(location.getX());
            dos.writeDouble(location.getY());
            dos.writeDouble(location.getZ());
            dos.writeUTF(blockType);
            dos.writeLong(timestamp.toEpochMilli());
        }
    }

    public static HistoryElement load(InputStream in) throws IOException {
        try (DataInputStream dis = new DataInputStream(in)) {
            UUID playerUUID = UUID.fromString(dis.readUTF());
            String worldName = dis.readUTF();
            double x = dis.readDouble();
            double y = dis.readDouble();
            double z = dis.readDouble();
            String blockType = dis.readUTF();
            Instant timestamp = Instant.ofEpochMilli(dis.readLong());

            Location location = new Location(org.bukkit.Bukkit.getWorld(worldName), x, y, z);
            return new HistoryElement(playerUUID, location, blockType);
        }
    }

    @Override
    public String toString() {
        return "[%s] Player %s changed %s at %s".formatted(
                timestamp, playerUUID, blockType, location);
    }

    public enum Type {
        PLACE("PLACED"),
        BREAK("BROKEN"),
        EMPTY_BUCKET("PLACED USING BUCKET"),
        FILL_BUCKET("PICKED UP USING BUCKET"),
        EXPLODE_END_CRYSTAL("EXPLODED USING END CRYSTAL"),
        EXPLODE_TNT("EXPLODED USING TNT"),
        EXPLODE_CREEPER("EXPLODED USING CREEPER"),
        EXPLODE_BLOCK("EXPLODED USING BLOCK"),
        SIGN("CHANGED");

        public final String name;

        Type (String name) {
            this.name = name;
        }
    }
}
