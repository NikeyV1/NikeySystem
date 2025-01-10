package de.nikey.nikeysystem.Player.API;

import de.nikey.nikeysystem.NikeySystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Channel{
    private final UUID id;
    private final UUID owner;
    private final String name;
    private List<String> messages = new ArrayList<>();
    private Set<UUID> members = new HashSet<>();
    private boolean isClosed = false;
    private final Set<UUID> invitedPlayers = new HashSet<>();

    public Channel(UUID id, String name, UUID owner, boolean isClosed, List<String> messages, Set<UUID> members) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.isClosed = isClosed;
        this.messages = messages != null ? messages : new ArrayList<>();
        this.members = members != null ? members : new HashSet<>();
    }

    public Component getPrefix() {
        String cap = name.substring(0, 1).toUpperCase() + name.substring(1);
        return Component.text("[" + cap + " Channel] ").color(TextColor.color(241, 183, 84));
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

    public void sendMessage(Component message) {
        addMessage(PlainTextComponentSerializer.plainText().serialize(message));
        for (UUID memberUUID : getMembers()) {
            Player member = Bukkit.getPlayer(memberUUID);
            if (member != null) {
                member.sendMessage(getPrefix().append(message.color(NamedTextColor.WHITE)));
            }
        }
    }

    // Serialisieren
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", id.toString());
        data.put("owner", owner.toString());
        data.put("name", name);
        data.put("isClosed", isClosed);
        data.put("messages", messages);
        data.put("members", members.stream().map(UUID::toString).toList());
        data.put("invitedPlayers", invitedPlayers.stream().map(UUID::toString).toList());
        return data;
    }

    // Deserialisieren
    @SuppressWarnings("unchecked")
    public static Channel deserialize(Map<String, Object> data) {
        UUID id = UUID.fromString((String) data.get("id"));
        UUID owner = UUID.fromString((String) data.get("owner"));
        String name = (String) data.get("name");
        boolean isClosed = (boolean) data.get("isClosed");

        List<String> messages = (List<String>) data.get("messages");
        Set<UUID> members = ((List<String>) data.get("members")).stream()
                .map(UUID::fromString).collect(Collectors.toSet());
        Set<UUID> invitedPlayers = ((List<String>) data.get("invitedPlayers")).stream()
                .map(UUID::fromString).collect(Collectors.toSet());

        Bukkit.broadcastMessage(String.valueOf(owner));
        Channel channel = new Channel(id, name, owner, isClosed, messages, members);
        channel.invitedPlayers.addAll(invitedPlayers);
        new BukkitRunnable() {
            @Override
            public void run() {
                channel.invitedPlayers.removeAll(invitedPlayers);
            }
        }.runTaskLater(NikeySystem.getPlugin(),20*60);
        return channel;
    }
}
