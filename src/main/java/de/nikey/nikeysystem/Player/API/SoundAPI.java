package de.nikey.nikeysystem.Player.API;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class SoundAPI {

    public static Map<Player, Queue<Sound>> soundQueues = new HashMap<>(); // Sound-Warteschlange für jeden Spieler
    public static Map<Player, Queue<Long>> durationQueues = new HashMap<>();

    public static void sendResourcePack(Audience target, ResourcePackInfo info) {
        final ResourcePackRequest request = ResourcePackRequest.resourcePackRequest()
                .packs(info)
                .prompt(Component.text("System needs download!"))
                .required(true)
                .build();

        target.sendResourcePacks(request);
    }
}
