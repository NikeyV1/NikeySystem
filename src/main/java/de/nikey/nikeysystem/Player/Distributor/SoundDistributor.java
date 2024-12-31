package de.nikey.nikeysystem.Player.Distributor;

import de.nikey.nikeysystem.General.GeneralAPI;
import de.nikey.nikeysystem.General.ShieldCause;
import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.HideAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.Player.API.SoundAPI;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.intellij.lang.annotations.Subst;

import java.net.URI;
import java.util.*;

import static de.nikey.nikeysystem.Player.API.SoundAPI.*;

public class SoundDistributor {
    public static void manageSound(Player sender, String[] args) {
        String cmd = args[3];
        if (cmd.isEmpty()) return;

        if (cmd.equalsIgnoreCase("play")) {
            if (args.length == 7) {

                if (args[4].equalsIgnoreCase("all")) {
                    Sound myCustomSound = Sound.sound(Key.key(args[5], args[6]), Sound.Source.MASTER, 1f, 1f);
                    for (Player player : GeneralAPI.getOnlinePlayers(sender)) {
                        if (PermissionAPI.isAllowedToChange(sender.getName(),player.getName(), ShieldCause.SOUND_PLAY_ALL)) {
                            player.playSound(myCustomSound);
                            if (PermissionAPI.isSystemUser(player)) {
                                player.sendActionBar(Component.text("Now Playing: ").color(TextColor.color(52, 183, 235))
                                        .append(Component.text(myCustomSound.name().asString()).color(NamedTextColor.WHITE)));
                            }
                        }
                    }

                    sender.sendMessage(Component.text("Now playing :").color(TextColor.color(52, 183, 235))
                            .append(Component.text(myCustomSound.name().asString()).color(NamedTextColor.WHITE))
                            .append(Component.text(" for everyone").color(TextColor.color(52, 183, 235))));
                    return;
                }

                Player player = Bukkit.getPlayer(args[4]);
                if (player == null || !HideAPI.canSee(sender.getName(), player.getName())) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName(),ShieldCause.SOUND_PLAY)) {
                    sender.sendMessage("§cError: missing permission");
                    return;
                }

                Sound myCustomSound = Sound.sound(Key.key(args[5], args[6]), Sound.Source.MASTER, 1f, 1f);
                player.playSound(myCustomSound);
                if (PermissionAPI.isSystemUser(player)) {
                    player.sendActionBar(Component.text("Now Playing: ").color(TextColor.color(52, 183, 235))
                            .append(Component.text(myCustomSound.name().asString()).color(NamedTextColor.WHITE)));
                }
                if (player != sender)sender.sendMessage(Component.text("Now playing :").color(TextColor.color(52, 183, 235))
                        .append(Component.text(myCustomSound.name().asString()).color(NamedTextColor.WHITE))
                        .append(Component.text(" to ").color(TextColor.color(52, 183, 235)))
                        .append(Component.text(player.getName()).color(NamedTextColor.GRAY)));
            }else if (args.length == 8) {

                if (args[4].equalsIgnoreCase("all")) {
                    Sound myCustomSound = Sound.sound(Key.key(args[5], args[6]), Sound.Source.MASTER, Float.parseFloat(args[7]), 1f);
                    for (Player player : GeneralAPI.getOnlinePlayers(sender)) {
                        if (PermissionAPI.isAllowedToChange(sender.getName(),player.getName(),ShieldCause.SOUND_PLAY_ALL)) {
                            player.playSound(myCustomSound);
                            if (PermissionAPI.isSystemUser(player)) {
                                player.sendActionBar(Component.text("Now Playing: ").color(TextColor.color(52, 183, 235))
                                        .append(Component.text(myCustomSound.name().asString()).color(NamedTextColor.WHITE)));
                            }
                        }
                    }

                    sender.sendMessage(Component.text("Now playing :").color(TextColor.color(52, 183, 235))
                            .append(Component.text(myCustomSound.name().asString()).color(NamedTextColor.WHITE))
                            .append(Component.text(" for everyone").color(TextColor.color(52, 183, 235))));
                    return;
                }

                Player player = Bukkit.getPlayer(args[4]);
                if (player == null || !HideAPI.canSee(sender.getName(), player.getName())) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName(),ShieldCause.SOUND_PLAY)) {
                    sender.sendMessage("§cError: missing permission");
                    return;
                }

                Sound myCustomSound = Sound.sound(Key.key(args[5], args[6]), Sound.Source.MASTER, Float.parseFloat(args[7]), 1f);
                player.playSound(myCustomSound);
                if (PermissionAPI.isSystemUser(player)) {
                    player.sendActionBar(Component.text("Now Playing: ").color(TextColor.color(52, 183, 235))
                            .append(Component.text(myCustomSound.name().asString()).color(NamedTextColor.WHITE)));
                }
                if (player != sender)sender.sendMessage(Component.text("Now playing :").color(TextColor.color(52, 183, 235))
                        .append(Component.text(myCustomSound.name().asString()).color(NamedTextColor.WHITE))
                        .append(Component.text(" to ").color(TextColor.color(52, 183, 235)))
                        .append(Component.text(player.getName()).color(NamedTextColor.GRAY)));
            }else if (args.length == 9) {
                if (args[4].equalsIgnoreCase("all")) {
                    Sound myCustomSound = Sound.sound(Key.key(args[5], args[6]), Sound.Source.MASTER, Float.parseFloat(args[7]), Float.parseFloat(args[8]));
                    for (Player player : GeneralAPI.getOnlinePlayers(sender)) {
                        if (PermissionAPI.isAllowedToChange(sender.getName(),player.getName(),ShieldCause.SOUND_PLAY_ALL)) {
                            player.playSound(myCustomSound);
                            if (PermissionAPI.isSystemUser(player)) {
                                player.sendActionBar(Component.text("Now Playing: ").color(TextColor.color(52, 183, 235))
                                        .append(Component.text(myCustomSound.name().asString()).color(NamedTextColor.WHITE)));
                            }
                        }
                    }

                    sender.sendMessage(Component.text("Now playing :").color(TextColor.color(52, 183, 235))
                            .append(Component.text(myCustomSound.name().asString()).color(NamedTextColor.WHITE))
                            .append(Component.text(" for everyone").color(TextColor.color(52, 183, 235))));
                    return;
                }

                Player player = Bukkit.getPlayer(args[4]);
                if (player == null || !HideAPI.canSee(sender.getName(), player.getName())) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName(),ShieldCause.SOUND_PLAY)) {
                    sender.sendMessage("§cError: missing permission");
                    return;
                }

                Sound myCustomSound = Sound.sound(Key.key(args[5], args[6]), Sound.Source.MASTER, Float.parseFloat(args[7]), Float.parseFloat(args[8]));
                player.playSound(myCustomSound);
                if (PermissionAPI.isSystemUser(player)) {
                    player.sendActionBar(Component.text("Now Playing: ").color(TextColor.color(52, 183, 235))
                            .append(Component.text(myCustomSound.name().asString()).color(NamedTextColor.WHITE)));
                }

                if (player != sender) sender.sendMessage(Component.text("Now playing :").color(TextColor.color(52, 183, 235))
                        .append(Component.text(myCustomSound.name().asString()).color(NamedTextColor.WHITE))
                        .append(Component.text(" to ").color(TextColor.color(52, 183, 235)))
                        .append(Component.text(player.getName()).color(NamedTextColor.GRAY)));

            }
        }else if (cmd.equalsIgnoreCase("stopall")){
            if (args.length == 4) {
                sender.stopAllSounds();
            }else if (args.length == 5) {
                Player player = Bukkit.getPlayer(args[4]);
                if (player == null || !HideAPI.canSee(sender.getName(), player.getName())) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName(),ShieldCause.SOUND_STOPALL)) {
                    sender.sendMessage("§cError: missing permission");
                    return;
                }

                player.stopAllSounds();
            }
        }else if (cmd.equalsIgnoreCase("queue")) {
            if (args.length != 10) return;

            Player player = Bukkit.getPlayer(args[4]);
            if (player == null || !HideAPI.canSee(sender.getName(), player.getName())) {
                sender.sendMessage("§cError: wrong usage");
                return;
            }
            if (!PermissionAPI.isAllowedToChange(sender.getName(),player.getName(),ShieldCause.SOUND_QUEUE)) {
                sender.sendMessage("§cError: missing permission");
                return;
            }

            Sound sound = Sound.sound(Key.key(args[5], args[6]), Sound.Source.MASTER, Float.parseFloat(args[7]), Float.parseFloat(args[8]));
            long durationSeconds = Long.parseLong(args[9]);
            queueSound(player, sound, durationSeconds);
            sender.sendMessage(Component.text("Added song ").color(TextColor.color(52, 183, 235))
                    .append(Component.text(sound.name().asString()).color(NamedTextColor.WHITE))
                    .append(Component.text(" to ").color(TextColor.color(52, 183, 235)))
                    .append(Component.text(player.getName()).color(NamedTextColor.GRAY)));
        }else if (cmd.equalsIgnoreCase("showQueue")) {
            Player player = Bukkit.getPlayer(args[4]);
            if (player == null || !HideAPI.canSee(sender.getName(), player.getName())) {
                sender.sendMessage("§cError: wrong usage");
                return;
            }

            Queue<Sound> queue = soundQueues.get(player);
            if (queue == null || queue.isEmpty()) {
                player.sendMessage("§eThe sound queue is empty");
            } else {
                StringBuilder message = new StringBuilder("§eUpcoming sounds in queue:\n");
                int count = 1;
                for (Sound sound : queue) {
                    message.append("§7").append(count).append(". §a")
                            .append(sound.name().asString()).append("\n");
                    count++;
                }
                player.sendMessage(message.toString());
            }
        }else if (cmd.equalsIgnoreCase("clearQueue")) {
            Player player = Bukkit.getPlayer(args[4]);
            if (player == null || !HideAPI.canSee(sender.getName(), player.getName())) {
                sender.sendMessage("§cError: wrong usage");
                return;
            }
            if (!PermissionAPI.isAllowedToChange(sender.getName(), player.getName(),ShieldCause.SOUND_QUEUE_CLEAR)) {
                sender.sendMessage("§cError: missing permission");
                return;
            }

            Queue<Sound> soundQueue = soundQueues.get(player);
            Queue<Long> durationQueue = durationQueues.get(player);

            // Überprüfen, ob der Spieler eine Warteschlange hat und diese dann leeren
            if (soundQueue != null && !soundQueue.isEmpty()) {
                soundQueue.clear();
                durationQueue.clear();
                player.sendMessage(Component.text(player.getName()).color(NamedTextColor.WHITE)
                        .append(Component.text(" sound queue is now").color(TextColor.color(52, 183, 235)))
                        .append(Component.text(" clear").color(NamedTextColor.RED)));
            } else {
                player.sendMessage("§cYour sound queue is already empty");
            }
        }else if (cmd.equalsIgnoreCase("removequeue")) {
            Player player = Bukkit.getPlayer(args[4]);
            if (player == null || !HideAPI.canSee(sender.getName(), player.getName())) {
                sender.sendMessage("§cError: wrong usage");
                return;
            }
            if (!PermissionAPI.isAllowedToChange(sender.getName(), player.getName(),ShieldCause.SOUND_QUEUE_REMOVE)) {
                sender.sendMessage("§cError: missing permission");
                return;
            }

            Queue<Sound> soundQueue = soundQueues.get(player);
            Queue<Long> durationQueue = durationQueues.get(player);

            if (soundQueue == null || soundQueue.isEmpty()) {
                player.sendMessage("§cError: Your sound queue is empty");
                return;
            }

            List<Sound> soundList = new LinkedList<>(soundQueue);
            List<Long> durationList = new LinkedList<>(durationQueue);
            int position = Integer.parseInt(args[5]);

            if (position < 1 || position > soundList.size()) {
                player.sendMessage("§cError: Invalid position. Please enter a number between 1 and " + soundList.size());
                return;
            }

            // Entferne den Sound und die Dauer am angegebenen Index (position - 1, da Listen bei 0 beginnen)
            Sound removedSound = soundList.remove(position - 1);
            durationList.remove(position - 1);

            // Aktualisiere die Warteschlangen
            soundQueue.clear();
            soundQueue.addAll(soundList);

            durationQueue.clear();
            durationQueue.addAll(durationList);
            player.sendMessage(Component.text("Removed sound: ")
                    .append(Component.text(removedSound.name().asString()).color(TextColor.color(52, 183, 235)))
                    .append(Component.text(" at position ").color(TextColor.color(52, 183, 235)))
                    .append(Component.text(position).color(NamedTextColor.WHITE))
                    .append(Component.text(" from ").color(TextColor.color(52, 183, 235)))
                    .append(Component.text(player.getName()).color(NamedTextColor.GRAY)));

        }
    }

    public static void queueSound(Player player, Sound sound, long durationSeconds) {
        soundQueues.putIfAbsent(player, new LinkedList<>());
        durationQueues.putIfAbsent(player, new LinkedList<>());

        Queue<Sound> soundQueue = soundQueues.get(player);
        Queue<Long> durationQueue = durationQueues.get(player);

        soundQueue.add(sound);                // Füge Sound zur Sound-Warteschlange hinzu
        durationQueue.add(durationSeconds);    // Füge Dauer zur Dauer-Warteschlange hinzu

        // Sound sofort abspielen, falls es der einzige Sound in der Warteschlange ist
        if (soundQueue.size() == 1) {
            playNextSound(player);
        }
    }

    // Spielt den nächsten Sound aus der Warteschlange
    private static void playNextSound(Player player) {
        Queue<Sound> soundQueue = soundQueues.get(player);
        Queue<Long> durationQueue = durationQueues.get(player);

        if (soundQueue == null || soundQueue.isEmpty() || durationQueue == null || durationQueue.isEmpty()) {
            return;
        }

        Sound sound = soundQueue.peek(); // Hole den Sound aus der Warteschlange, entferne ihn aber noch nicht
        long durationSeconds = durationQueue.peek(); // Hole die Dauer aus der Warteschlange, entferne sie aber noch nicht
        long durationTicks = durationSeconds * 20;

        player.playSound(sound);

        // Spieler informieren
        if (PermissionAPI.isSystemUser(player)) player.sendActionBar(Component.text("Playing sound: ").color(TextColor.color(52, 183, 235))
                .append(Component.text(sound.name().asString()).color(NamedTextColor.WHITE))
                .append(Component.text(" for ").color(TextColor.color(52, 183, 235)))
                .append(Component.text(durationSeconds).color(NamedTextColor.YELLOW)));

        // Verzögerung zum Abspielen des nächsten Sounds
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }
                soundQueue.poll();
                durationQueue.poll();
                playNextSound(player); // Spiele den nächsten Sound in der Warteschlange
            }
        }.runTaskLater(NikeySystem.getPlugin(), durationTicks);
    }
}
