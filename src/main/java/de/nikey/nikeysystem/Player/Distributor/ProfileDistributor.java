package de.nikey.nikeysystem.Player.Distributor;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.HideAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import static de.nikey.nikeysystem.Player.API.ProfileAPI.originalSkins;

public class ProfileDistributor {
    public static void manageProfile(Player sender, String[] args) {
        String cmd = args[3];
        if (cmd.isEmpty()) return;

        if (cmd.equalsIgnoreCase("skin")){
            if (args.length == 5 && args[4].equalsIgnoreCase("reset")) {
                PlayerProfile playerProfile = sender.getPlayerProfile();
                try {
                    URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + sender.getName());

                    InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());

                    String uuid = JsonParser.parseReader(reader_0).getAsJsonObject().get("id").getAsString();

                    URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");

                    InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());

                    JsonObject properties = JsonParser.parseReader(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();

                    String value = properties.get("value").getAsString();
                    String signature = properties.get("signature").getAsString();

                    playerProfile.setProperty(new ProfileProperty("textures", value, signature));
                    sender.setPlayerProfile(playerProfile);

                    sender.sendMessage(Component.text("Reset your skin").color(TextColor.color(211, 102, 217)));
                } catch (IllegalStateException | IOException | NullPointerException exception) {
                    sender.sendMessage("§cFailed to reset skin Cause: "+ exception.getCause());
                }
            }else if (args.length == 6) {
                if (args[4].equalsIgnoreCase("set")) {
                    PlayerProfile playerProfile = sender.getPlayerProfile();
                    try {
                        URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + args[5]);

                        InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());

                        String uuid = JsonParser.parseReader(reader_0).getAsJsonObject().get("id").getAsString();

                        URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");

                        InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());

                        JsonObject properties = JsonParser.parseReader(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();

                        String value = properties.get("value").getAsString();
                        String signature = properties.get("signature").getAsString();

                        playerProfile.setProperty(new ProfileProperty("textures", value, signature));
                        sender.setPlayerProfile(playerProfile);

                        sender.sendMessage(Component.text("Set your skin to ").color(TextColor.color(211, 102, 217)).append(Component.text(args[5]+"'s").color(NamedTextColor.WHITE)).append(Component.text(" skin").color(TextColor.color(211, 102, 217))));
                    } catch (IllegalStateException | IOException | NullPointerException exception) {
                        sender.sendMessage("§cFailed to set skin Cause: "+ exception.getCause());
                    }
                }else if (args[4].equalsIgnoreCase("reset")) {
                    Player player = Bukkit.getPlayer(args[5]);
                    if (player == null || !HideAPI.canSee(sender.getName(), player.getName())) {
                        sender.sendMessage("§cError: wrong usage");
                        return;
                    }
                    if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName())) {
                        sender.sendMessage("§cError: missing permission");
                        return;
                    }
                    PlayerProfile playerProfile = player.getPlayerProfile();
                    try {
                        URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + player.getName());

                        InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());

                        String uuid = JsonParser.parseReader(reader_0).getAsJsonObject().get("id").getAsString();

                        URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");

                        InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());

                        JsonObject properties = JsonParser.parseReader(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();

                        String value = properties.get("value").getAsString();
                        String signature = properties.get("signature").getAsString();

                        playerProfile.setProperty(new ProfileProperty("textures", value, signature));
                        player.setPlayerProfile(playerProfile);

                    } catch (IllegalStateException | IOException | NullPointerException exception) {
                        player.sendMessage("§cFailed to reset skin Cause: "+ exception.getCause());
                    }
                    sender.sendMessage(Component.text("Reset ").color(TextColor.color(211, 102, 217)).append(Component.text(player.getName()+"'s").color(NamedTextColor.WHITE)).append(Component.text(" skin").color(TextColor.color(211, 102, 217))));
                }
            }else if (args.length == 7) {
                Player player = Bukkit.getPlayer(args[5]);
                if (player == null || !HideAPI.canSee(sender.getName(), player.getName())) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName())) {
                    sender.sendMessage("§cError: missing permission");
                    return;
                }

                if (args[4].equalsIgnoreCase("set")) {
                    PlayerProfile playerProfile = player.getPlayerProfile();
                    try {
                        URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + args[6]);

                        InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());

                        String uuid = JsonParser.parseReader(reader_0).getAsJsonObject().get("id").getAsString();

                        URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");

                        InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());

                        JsonObject properties = JsonParser.parseReader(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();

                        String value = properties.get("value").getAsString();
                        String signature = properties.get("signature").getAsString();

                        playerProfile.setProperty(new ProfileProperty("textures", value, signature));
                        player.setPlayerProfile(playerProfile);

                        sender.sendMessage(Component.text("Set ").color(TextColor.color(211, 102, 217))
                                .append(Component.text(player.getName()+"'s").color(NamedTextColor.WHITE))
                                .append(Component.text(" skin to ")).append(Component.text(args[6]+"'s").color(NamedTextColor.WHITE))
                                .append(Component.text(" skin").color(TextColor.color(211, 102, 217))));
                    } catch (IllegalStateException | IOException | NullPointerException exception) {
                        player.sendMessage("§cFailed to set skin Cause: "+ exception.getCause());
                    }
                }
            }
        }
    }

    private static ProfileProperty fetchSkinProperty(String playerName) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == 204) return null;

            JsonObject uuidJson = JsonParser.parseReader(new InputStreamReader(connection.getInputStream())).getAsJsonObject();
            String uuid = uuidJson.get("id").getAsString();

            URL sessionUrl = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            HttpURLConnection sessionConnection = (HttpURLConnection) sessionUrl.openConnection();
            sessionConnection.setRequestMethod("GET");
            JsonObject profileJson = JsonParser.parseReader(new InputStreamReader(sessionConnection.getInputStream())).getAsJsonObject();
            JsonObject properties = profileJson.getAsJsonArray("properties").get(0).getAsJsonObject();

            return new ProfileProperty(properties.get("name").getAsString(), properties.get("value").getAsString(), properties.get("signature").getAsString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void setPlayerSkin(Player player, ProfileProperty skinProperty) {
        PlayerProfile profile = player.getPlayerProfile();
        if (!originalSkins.containsKey(player.getUniqueId())) {
            originalSkins.put(player.getUniqueId(), profile.getProperties().stream()
                    .filter(prop -> prop.getName().equals("textures"))
                    .findFirst().orElse(null));
        }
        profile.getProperties().removeIf(prop -> prop.getName().equals("textures"));
        profile.getProperties().add(skinProperty);
        player.setPlayerProfile(profile);
        player.kick(Component.text("Skin changed! Please relog").color(TextColor.color(211, 102, 217)));
    }

    private static void resetPlayerSkin(Player player) {
        ProfileProperty originalSkin = originalSkins.get(player.getUniqueId());
        if (originalSkin != null) {
            PlayerProfile profile = player.getPlayerProfile();
            profile.getProperties().removeIf(prop -> prop.getName().equals("textures"));
            profile.getProperties().add(originalSkin);
            player.setPlayerProfile(profile);
            player.kick(Component.text("Skin changed! Please relog").color(TextColor.color(211, 102, 217)));
            originalSkins.remove(player.getUniqueId());
        } else {
            player.sendMessage("§cError: no saved skin");
        }
    }
}
