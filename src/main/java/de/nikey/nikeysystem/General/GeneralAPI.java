package de.nikey.nikeysystem.General;

import de.nikey.nikeysystem.Player.API.HideAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
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
}
