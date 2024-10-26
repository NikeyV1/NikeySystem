package de.nikey.nikeysystem.Server.Distributor;

import de.nikey.nikeysystem.General.GeneralAPI;
import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.HideAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.Server.API.PerformanceAPI;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.text.DecimalFormat;
import java.util.*;

public class PerformanceDistributor {
    private static BossBar bossBar;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    private static final DecimalFormat PERCENT_FORMAT = new DecimalFormat("0.0");

    public static void performanceManager(Player sender, String[] args) {
        String cmd = args[3];
        if (cmd.isEmpty()) return;

        // Mute Command
        if (cmd.equalsIgnoreCase("toggletpsbar")) {
            Player target = sender;
            if (args.length == 5) {
                target = Bukkit.getPlayer(args[4]);
                if (target == null || !HideAPI.canSee(sender, target)) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }

                if (!PermissionAPI.isAllowedToChange(sender.getName(),target.getName())) {
                    sender.sendMessage("§cError: missing permission");
                    return;
                }
            }

            if (bossBar == null) {
                bossBar = BossBar.bossBar(
                        Component.text("Server Performance"),
                        1.0f, // Standard Fortschritt
                        BossBar.Color.GREEN,
                        BossBar.Overlay.PROGRESS
                );
            }

            Iterator<BossBar> iterator = (Iterator<BossBar>) target.activeBossBars().iterator();
            while (iterator.hasNext()) {
                BossBar activeBar = iterator.next();
                if (activeBar == bossBar) {
                    target.hideBossBar(bossBar);
                    return;
                }
            }

            target.showBossBar(bossBar);

            Player finalTarget = target;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!finalTarget.isOnline()) {
                        finalTarget.hideBossBar(bossBar);
                        this.cancel();
                        return;
                    }

                    // TPS-Werte
                    double[] tps = Bukkit.getServer().getTPS();
                    double tps1 = tps[0];
                    if (tps1 > 20) {
                        tps1 = 20;
                    }

                    // Spieler-Ping abrufen
                    int ping = finalTarget.getPing(); // Einfacher Abruf des Pings

                    // Text in der Bossbar setzen
                    bossBar.name(Component.text(String.format("TPS: %.2f  MSPT: %.2f  Ping: %dms", tps1, Bukkit.getAverageTickTime(), ping)));

                    // Farbe und Fortschritt der Bossbar basierend auf TPS/MSPT einstellen
                    if (tps1 >= 18) {
                        bossBar.color(BossBar.Color.GREEN);
                    } else if (tps1 >= 15) {
                        bossBar.color(BossBar.Color.YELLOW);
                    } else {
                        bossBar.color(BossBar.Color.RED);
                    }

                    bossBar.progress((float) Math.min(tps1 / 20.0, 1.0));
                }
            }.runTaskTimer(NikeySystem.getPlugin(), 0, 40);
        } else if (cmd.equalsIgnoreCase("servertick")) {
            double[] tps = Bukkit.getServer().getTPS();
            double tps1m = tps[0];
            double tps5m = tps[1];
            double tps15m = tps[2];

            // CPU und RAM Werte
            OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            double availableRam = Runtime.getRuntime().maxMemory() / 1024.0 / 1024.0;
            double usedRam = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024.0 / 1024.0;
            double ramUsagePercentage = (usedRam / availableRam) * 100;
            double systemCpuLoad = osBean.getCpuLoad()* 100;
            double processCpuLoad = osBean.getProcessCpuLoad() * 100;

            // Dynamische Farbgebung basierend auf den Werten

            NamedTextColor ramColor = getRamColor(ramUsagePercentage);

            long[] tickTimes = Bukkit.getTickTimes();

            double minMspt = Double.MAX_VALUE;
            double maxMspt = Double.MIN_VALUE;

            for (long tickTime : tickTimes) {
                double mspt = tickTime / 1_000_000.0; // Umrechnung von Nanosekunden zu Millisekunden

                if (mspt < minMspt) {
                    minMspt = mspt;
                }
                if (mspt > maxMspt) {
                    maxMspt = mspt;
                }
            }
            // Zusammenstellen des Textes
            Component tpsInfo = Component.text("Server Tick Information", NamedTextColor.AQUA).decoration(TextDecoration.BOLD,true)
                    .append(Component.newline())
                    .append(Component.text("TPS: ", NamedTextColor.GRAY).decoration(TextDecoration.BOLD,false))
                    .append(Component.text(DECIMAL_FORMAT.format(tps1m) + " (1m), ", getTpsColor(tps1m)).decoration(TextDecoration.BOLD,false))
                    .append(Component.text(DECIMAL_FORMAT.format(tps5m) + " (5m), ", getTpsColor(tps5m)).decoration(TextDecoration.BOLD,false))
                    .append(Component.text(DECIMAL_FORMAT.format(tps15m) + " (15m)", getTpsColor(tps15m)).decoration(TextDecoration.BOLD,false))
                    .append(Component.newline())
                    .append(Component.text("MSPT", NamedTextColor.GRAY).decoration(TextDecoration.BOLD,false).append(Component.text(" - ").color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD,false)).append(Component.text("Average, Minimum, Maximum").color(NamedTextColor.GRAY).decoration(TextDecoration.BOLD,false)))
                    .append(Component.newline())
                    .append(Component.text(" └ ").color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD,false))
                    .append(Component.text(Bukkit.getAverageTickTime()).color(getMsptColor(Bukkit.getAverageTickTime())).decoration(TextDecoration.BOLD,false).append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD,false)))
                    .append(Component.text(minMspt).color(getMsptColor(minMspt)).decoration(TextDecoration.BOLD,false).append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD,false)))
                    .append(Component.text(maxMspt).color(getMsptColor(maxMspt)).decoration(TextDecoration.BOLD,false))
                    .append(Component.newline())
                    .append(Component.text("CPU: ", NamedTextColor.WHITE).decoration(TextDecoration.BOLD,false))
                    .append(Component.text(PERCENT_FORMAT.format(systemCpuLoad) + "%", getCpuColor(systemCpuLoad)).decoration(TextDecoration.BOLD,false))
                    .append(Component.text(", ").color(NamedTextColor.WHITE).decoration(TextDecoration.BOLD,false))
                    .append(Component.text(PERCENT_FORMAT.format(processCpuLoad ) + "%", getCpuColor(processCpuLoad)).decoration(TextDecoration.BOLD,false))
                    .append(Component.text(" sys. proc.").color(NamedTextColor.GRAY).decoration(TextDecoration.BOLD,false))
                    .append(Component.newline())
                    .append(Component.text("RAM: ", NamedTextColor.WHITE).decoration(TextDecoration.BOLD,false))
                    .append(Component.text(DECIMAL_FORMAT.format(usedRam) + "MB / " + DECIMAL_FORMAT.format(availableRam) + "MB", ramColor).decoration(TextDecoration.BOLD,false));

            sender.sendMessage(tpsInfo);
        } else if (cmd.equalsIgnoreCase("ping")) {
            if (args.length == 5) {
                Player target = Bukkit.getPlayer(args[4]);
                if (target == null || !HideAPI.canSee(sender, target)) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                Component component;
                if (target.getPing() <= 40) {
                    component = Component.text(target.getName()+"'s ping is: ").color(TextColor.color(255, 218, 117)).append(Component.text(target.getPing()).color(NamedTextColor.GREEN));
                }else if (target.getPing() <= 100) {
                    component = Component.text(target.getName()+"'s ping is: ").color(TextColor.color(255, 218, 117)).append(Component.text(target.getPing()).color(NamedTextColor.YELLOW));
                }else {
                    component = Component.text(target.getName()+"'s ping is: ").color(TextColor.color(255, 218, 117)).append(Component.text(target.getPing()).color(NamedTextColor.RED));
                }
                sender.sendMessage(component);

            } else if (args.length == 4) {
                List<Component> messages = new ArrayList<>();
                messages.add(Component.text("Players pings:").color(TextColor.color(255, 218, 117)).decoration(TextDecoration.BOLD,true));
                for (Player player : GeneralAPI.getOnlinePlayers(sender)) {
                    if (player.getPing() <= 40) {
                        messages.add(Component.text(player.getName()+"'s ping: ").color(TextColor.color(240, 163, 31)).append(Component.text(player.getPing()).color(NamedTextColor.GREEN)));
                    }else if (player.getPing() <= 100) {
                        messages.add(Component.text(player.getName()+"'s ping: ").color(TextColor.color(240, 163, 31)).append(Component.text(player.getPing()).color(NamedTextColor.YELLOW)));
                    }else {
                        messages.add(Component.text(player.getName()+"'s ping: ").color(TextColor.color(240, 163, 31)).append(Component.text(player.getPing()).color(NamedTextColor.RED)));
                    }
                }
                for (Component component : messages) {
                    sender.sendMessage(component);
                }
            }
        }else if (cmd.equalsIgnoreCase("entitys")) {
            listEntities(sender);
        }
    }

    public static void listEntities(Player player) {
        Map<EntityType, Integer> entityCount = new HashMap<>();
        for (Entity entity : player.getWorld().getEntities()) {
            entityCount.put(entity.getType(), entityCount.getOrDefault(entity.getType(), 0) + 1);
        }

        player.sendMessage(Component.text("Entity's in world:", NamedTextColor.GOLD));

        entityCount.forEach((type, count) -> {
            Component message = Component.text(type.name() + ": " + count, NamedTextColor.YELLOW)
                    .append(Component.space())
                    .append(Component.text("[Kill]", NamedTextColor.RED)
                            .hoverEvent(HoverEvent.showText(Component.text("Remove all " + type.name().toLowerCase())))
                            .clickEvent(ClickEvent.runCommand("/killAll"+type.name())) // Hier klicken um zu töten
                    )
                    .append(Component.space())
                    .append(Component.text("[Kill one]", NamedTextColor.GRAY)
                            .hoverEvent(HoverEvent.showText(Component.text("Remove one " + type.name().toLowerCase())))
                            .clickEvent(ClickEvent.runCommand("/killOne" + type.name())) // Hier klicken um einen zu töten
                    );

            player.sendMessage(message);

            // Speichern der Anfrage für diesen Spieler
            PerformanceAPI.killAllRequests.put(player.getUniqueId(), type);
            PerformanceAPI.killOneRequests.put(player.getUniqueId(), type);
        });
    }


    private static NamedTextColor getTpsColor(double tps) {
        if (tps >= 19.5) {
            return NamedTextColor.GREEN;
        } else if (tps >= 17.0) {
            return NamedTextColor.YELLOW;
        } else {
            return NamedTextColor.RED;
        }
    }

    // Farbgebung abhängig von der CPU-Auslastung
    private static NamedTextColor getCpuColor(double cpuLoad) {
        if (cpuLoad <= 0.5) {
            return NamedTextColor.GREEN;
        } else if (cpuLoad <= 0.75) {
            return NamedTextColor.YELLOW;
        } else {
            return NamedTextColor.RED;
        }
    }

    private static NamedTextColor getRamColor(double ramUsagePercentage) {
        if (ramUsagePercentage <= 50.0) {
            return NamedTextColor.GREEN;
        } else if (ramUsagePercentage <= 75.0) {
            return NamedTextColor.YELLOW;
        } else {
            return NamedTextColor.RED;
        }
    }


    private static NamedTextColor getMsptColor(double mspt) {
        if (mspt <= 50) {
            return NamedTextColor.GREEN;
        } else if (mspt <= 100) {
            return NamedTextColor.YELLOW;
        } else {
            return NamedTextColor.RED;
        }
    }
}
