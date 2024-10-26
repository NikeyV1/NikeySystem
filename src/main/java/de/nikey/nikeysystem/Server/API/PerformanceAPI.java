package de.nikey.nikeysystem.Server.API;

import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PerformanceAPI {
    public static final Map<UUID, EntityType> killAllRequests = new HashMap<>();
    public static final Map<UUID, EntityType> killOneRequests = new HashMap<>();
}
