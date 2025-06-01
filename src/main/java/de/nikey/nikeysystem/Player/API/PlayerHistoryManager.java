package de.nikey.nikeysystem.Player.API;

import de.nikey.nikeysystem.DataBases.PunishmentDatabase;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerHistoryManager {

    private final Map<UUID, List<Punishment>> historyMap = new ConcurrentHashMap<>();

    public void addPunishment(UUID playerUUID, Punishment punishment) {
        historyMap.computeIfAbsent(playerUUID, k -> new ArrayList<>()).add(punishment);
        PunishmentDatabase.savePunishmentHistory(punishment);
    }

    public List<Punishment> getHistory(UUID playerUUID) {
        return historyMap.getOrDefault(playerUUID, new ArrayList<>());
    }

    public List<Punishment> getActivePunishments(UUID playerUUID) {
        return getHistory(playerUUID).stream()
                .filter(p -> !p.isPermanent() && System.currentTimeMillis() < p.getStartTime() + p.getDuration())
                .toList();
    }

    public void loadAllHistories() {
        historyMap.putAll(PunishmentDatabase.loadAllPunishmentHistories());
    }
}