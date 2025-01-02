package de.nikey.nikeysystem.Player.API;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import java.io.Serializable;
import java.util.*;

public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;

        private final UUID id;
        private final UUID owner;
        private final String name;
        private final List<String> messages = new ArrayList<>();
        private final Set<UUID> members = new HashSet<>();  // Set of player UUIDs who are members of the channel
        private boolean isClosed = false; // Status des Channels (offen/geschlossen)
        private final Set<UUID> invitedPlayers = new HashSet<>(); // Spieler, die eingeladen wurden

        public Channel(UUID id, UUID owner, String name) {
            this.id = id;
            this.owner = owner;
            this.name = name;
        }

        public Component getPrefix() {
            String cap = name.substring(0, 1).toUpperCase() + name.substring(1);
            return Component.text("[" + cap + "] ").color(TextColor.color(241, 183, 84));
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

        public String getName() {
            return name;
        }

        public UUID getId() {
            return id;
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
