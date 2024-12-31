package de.nikey.nikeysystem.Player.Distributor;

import java.io.Serializable;
import java.util.*;

public class Channel implements Serializable {
        private static final long serialVersionUID = 1L;

        private final UUID id;
        private final UUID owner;
        private final List<String> messages = new ArrayList<>();
        private final Set<UUID> members = new HashSet<>();  // Set of player UUIDs who are members of the channel
        private boolean isClosed = false; // Status des Channels (offen/geschlossen)
        private final Set<UUID> invitedPlayers = new HashSet<>(); // Spieler, die eingeladen wurden

        public Channel(UUID id, UUID owner) {
            this.id = id;
            this.owner = owner;
        }

        public List<String> getMessages() {
            return messages;
        }

        public void addMessage(String message) {
            messages.add(message);
        }

        public void addMember(UUID playerUUID) {
            members.add(playerUUID);
        }

        public void removeMember(UUID playerUUID) {
            members.remove(playerUUID);
            invitedPlayers.remove(playerUUID); // Entfernen von eingeladenen Spielern
        }

        public Set<UUID> getMembers() {
            return members;
        }

        public boolean isClosed() {
            return isClosed;
        }

        public void setClosed(boolean closed) {
            this.isClosed = closed;
        }

        public void invitePlayer(UUID playerUUID) {
            if (isClosed) {
                invitedPlayers.add(playerUUID);
            }
        }

        public Set<UUID> getInvitedPlayers() {
            return invitedPlayers;
        }
        public UUID getOwner() {
            return owner;
        }
    }
