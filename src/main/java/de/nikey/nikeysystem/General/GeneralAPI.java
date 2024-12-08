package de.nikey.nikeysystem.General;

import de.nikey.nikeysystem.Player.API.HideAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

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

    public static List<String> handleStringListing(List<String> args, String given) {
        List<String> stringList = new ArrayList<>();

        if (given.isEmpty()) {
            stringList.addAll(args);
        }else {
            for (String name : args) {
                if (name.toLowerCase().startsWith(given.toLowerCase())) {
                    stringList.add(name);
                }
            }
        }

        Collections.sort(stringList);
        return stringList;
    }
}
