package de.nikey.nikeysystem.General;

import de.nikey.nikeysystem.Player.API.HideAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GeneralAPI {
    public static Collection<? extends Player> getOnlinePlayers(Player forplayer) {
        List<Player> players = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (forplayer == player) {
                players.add(player);
            }else if (HideAPI.canSee(forplayer,player)) {
                players.add(player);
            }
        }
        return players;
    }

    public static List<String> handlePlayerListing(Player sender, String[] args, int arg) {
        Collection<? extends Player> onlinePlayers = getOnlinePlayers(sender);

        List<String> players = new ArrayList<>();
        if (onlinePlayers.isEmpty())return null;
        if (!args[arg].isEmpty()) {
            for (String names : onlinePlayers.stream().map(Player::getName).toList()) {
                if (names.toLowerCase().startsWith(args[arg].toLowerCase())) {
                    players.add(names);
                }
            }
        }else {
            players.addAll(onlinePlayers.stream().map(Player::getName).toList());
        }
        Collections.sort(players);
        return players;
    }
}
