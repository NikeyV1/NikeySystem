package de.nikey.nikeysystem.Player.Functions;

import com.destroystokyo.paper.profile.PlayerProfile;
import de.nikey.nikeysystem.Player.API.ModerationAPI;
import io.papermc.paper.ban.BanListType;
import net.kyori.adventure.text.Component;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ban.ProfileBanList;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.text.SimpleDateFormat;

public class ModerationFunctions implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (ModerationAPI.isFrozen(event.getPlayer().getUniqueId())){
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (ModerationAPI.isFrozen(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true,priority = EventPriority.HIGH)
    public void onPlayerLogin(PlayerLoginEvent event) {
        ProfileBanList banList = Bukkit.getBanList(BanListType.PROFILE);
        BanEntry<PlayerProfile> banEntry = banList.getBanEntry(event.getPlayer().getPlayerProfile());

        if (banEntry != null) {
            String reason = banEntry.getReason() != null ? banEntry.getReason() : "No reason specified";
            String source = banEntry.getSource();
            String expiry = banEntry.getExpiration() != null
                    ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(banEntry.getExpiration())
                    : "Permanent";

            Component banMessage;

            if (banEntry.getExpiration() == null) {
                banMessage = ModerationAPI.KickMessanges.createPermanentBanMessage(reason);
            } else {
                banMessage = ModerationAPI.KickMessanges.createTemporaryBanMessage(reason, expiry);
            }


            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, banMessage);
        }
    }


}
