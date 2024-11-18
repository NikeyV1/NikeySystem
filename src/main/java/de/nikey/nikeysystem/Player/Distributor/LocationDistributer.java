package de.nikey.nikeysystem.Player.Distributor;

import de.nikey.nikeysystem.General.ShieldCause;
import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.HideAPI;
import de.nikey.nikeysystem.Player.API.LocationAPI;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import de.nikey.nikeysystem.Server.API.WorldAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class LocationDistributer {

    public static void locationManager(Player sender, String[] args) {
        String cmd = args[3];
        if (cmd.isEmpty()) return;

        // Mute Command
        if (cmd.equalsIgnoreCase("getLocation")) {
            if (args.length == 5) {
                Player target = Bukkit.getPlayer(args[4]);
                if (target == null || !HideAPI.canSee(sender, target)) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }

                if (!PermissionAPI.isAllowedToChange(sender.getName(),target.getName(),ShieldCause.LOCATION_GET_LOCATION)) {
                    sender.sendMessage("§cError: missing permission");
                    return;
                }

                Location loc = target.getLocation();
                sender.sendMessage(ChatColor.GREEN + target.getName() + "'s current location: " +
                        loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() +
                        " in world " + loc.getWorld().getName());

                // Klickbare Nachricht mit Adventure API
                TextComponent teleportMessage = Component.text("Click here to teleport to " + target.getName())
                        .color(NamedTextColor.AQUA)
                        .clickEvent(ClickEvent.runCommand("/tp " + target.getName()));

                sender.sendMessage(teleportMessage);
            }
        } else if (cmd.equalsIgnoreCase("tp")) {
            if (args.length == 5) {
                // Teleport zu einem Spieler
                Player target = Bukkit.getPlayer(args[4]);
                if (target == null || !HideAPI.canSee(sender, target)) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                if (!PermissionAPI.isAllowedToChange(sender.getName(),target.getName(),ShieldCause.TELEPORT)) {
                    sender.sendMessage("§cError: missing permission");
                    return;
                }

                sender.teleport(target.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                sender.sendMessage(ChatColor.BLUE + "Teleported to §f" + target.getName());
            } else if (args.length == 7) {
                try {
                    double x = Double.parseDouble(args[4]);
                    double y = Double.parseDouble(args[5]);
                    double z = Double.parseDouble(args[6]);
                    World world = sender.getWorld();

                    if (WorldAPI.isCreatorOnly(world.getName())) {
                        if (!WorldAPI.isWorldOwner(world.getName(),sender.getName())) {
                            sender.sendMessage("§cYou are not allowed to enter this world!");
                            return;
                        }
                    }

                    sender.teleport(new Location(world, x, y, z));
                    sender.sendMessage(ChatColor.BLUE + "Teleported to coordinates: §f" + x + ", " + y + ", " + z + "§9 in world §f" + world.getName());
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Error: Invalid coordinates");
                }
            } else if (args.length == 8) {
                try {
                    double x = Double.parseDouble(args[4]);
                    double y = Double.parseDouble(args[5]);
                    double z = Double.parseDouble(args[6]);
                    World world = Bukkit.getWorld(args[7]);

                    if (world == null) {
                        sender.sendMessage(ChatColor.RED + "World '" + args[4] + "' not found.");
                    } else {
                        if (WorldAPI.isCreatorOnly(world.getName())) {
                            if (!WorldAPI.isWorldOwner(world.getName(),sender.getName())) {
                                sender.sendMessage("§cYou are not allowed to enter this world!");
                                return;
                            }
                        }
                        sender.teleport(new Location(world, x, y, z));
                        sender.sendMessage(ChatColor.BLUE + "Teleported to coordinates: §f" + x + ", " + y + ", " + z + "§9 in world §f" + world.getName());
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Error: Invalid coordinates");
                }
            }else if (args.length == 9) {
                Player target = Bukkit.getPlayer(args[4]);
                if (target == null || !HideAPI.canSee(sender, target)) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                if (!PermissionAPI.isAllowedToChange(sender.getName(),target.getName(),ShieldCause.TELEPORT)) {
                    sender.sendMessage("§cError: missing permission");
                    return;
                }

                try {
                    double x = Double.parseDouble(args[5]);
                    double y = Double.parseDouble(args[6]);
                    double z = Double.parseDouble(args[7]);
                    World world = Bukkit.getWorld(args[8]);

                    if (world == null) {
                        sender.sendMessage(ChatColor.RED + "World '" + args[4] + "' not found.");
                    } else {
                        if (WorldAPI.isCreatorOnly(world.getName())) {
                            if (!WorldAPI.isWorldOwner(world.getName(),target.getName())) {
                                sender.sendMessage("§f" + target.getName() + "§cis not allowed to enter this world!");
                                return;
                            }
                        }
                        target.teleport(new Location(world, x, y, z));
                        sender.sendMessage(ChatColor.BLUE + "Teleported§f"+target.getName()+"§9 to coordinates: §f" + x + ", " + y + ", " + z + "§9 in world §f" + world.getName());
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Error: Invalid coordinates");
                }
            }
        }
        else if (cmd.equalsIgnoreCase("lastseen")) {
            if (args.length == 5) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[4]);
                if (!HideAPI.canSee(sender.getName(), offlinePlayer.getName())) {
                    sender.sendMessage("§cError: wrong usage");
                    return;
                }
                if (!PermissionAPI.isAllowedToChange(sender.getName(),offlinePlayer.getName(), ShieldCause.LASTSEEN)) {
                    sender.sendMessage("§cError: missing permission");
                    return;
                }
                Location location = offlinePlayer.getLocation();

                if (offlinePlayer.getLocation() != null) {
                    sender.sendMessage(ChatColor.BLUE + "Last seen location of §f" + offlinePlayer.getName() + "§9: " +
                            location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() +
                            " in world " + location.getWorld().getName());
                } else {
                    sender.sendMessage(ChatColor.RED + "Error: No known last seen location for " + args[4]);
                    sender.sendMessage("§8System: this command can cause lag use this with responsibility!");
                }
            }
        }
        else if (cmd.equalsIgnoreCase("placeGuard")) {
            if (args.length == 6) {
                String guardName = args[4];
                if (LocationAPI.guardLocations.containsKey(guardName)) {
                    sender.sendMessage("§cError: guard name already exists");
                    return;
                }
                double range = 10.0; // Standardreichweite
                try {
                    range = Double.parseDouble(args[5]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Error: Invalid number. Using 10");
                }
                LocationAPI.guardLocations.put(guardName, sender.getLocation());
                LocationAPI.guardRanges.put(guardName, range);
                LocationAPI.setGuardCreator(guardName,sender);
                sender.sendMessage(ChatColor.BLUE + "Guard §f'" + guardName + "'§9 has been set at your current location with a §f"+range+" block range");
            }
        }else if (cmd.equalsIgnoreCase("removeGuard")) {
            if (args.length == 5) {
                String guardName = args[4];
                if (LocationAPI.guardLocations.containsKey(guardName)) {
                    LocationAPI.guardLocations.remove(guardName);
                    LocationAPI.guardRanges.remove(guardName);
                    LocationAPI.guardCreators.remove(guardName);
                    sender.sendMessage(ChatColor.BLUE + "Guard §f'" + guardName + "'§9 has been §cremoved.");
                } else {
                    sender.sendMessage("§cError: guard not found");
                }
            }
        }else if (cmd.equalsIgnoreCase("listGuard")) {
            if (args.length == 4) {
                if (LocationAPI.guardLocations.isEmpty()) {
                    sender.sendMessage("§7No guards set.");
                } else {
                    StringBuilder guardList = new StringBuilder("Guard locations: §f");
                    LocationAPI.guardLocations.forEach((name, location) -> {
                        double range = LocationAPI.guardRanges.getOrDefault(name, 10.0);

                        String c = LocationAPI.guardCreators.get(name);
                        Player creator = Bukkit.getPlayer(c);

                        // Prüfen, ob der Ersteller vorhanden ist
                        String creatorName = (creator != null) ? creator.getName() : "Unknown";

                        guardList.append(name).append(" (Range: ").append(range).append(", Creator: ").append(creatorName).append("), ");
                    });
                    sender.sendMessage(ChatColor.DARK_GRAY + guardList.toString());

                }
            }else if (args.length == 5 && args[4].equalsIgnoreCase("show")) {
                for (Location location : LocationAPI.guardLocations.values()) {
                    if (sender.getLocation().getWorld() != location.getWorld())continue;
                    Slime entity = (Slime) location.getWorld().spawnEntity(location, EntityType.SLIME);
                    entity.setGlowing(true);
                    entity.setVisibleByDefault(false);
                    entity.setInvulnerable(true);
                    entity.setAI(false);
                    entity.setGravity(false);
                    entity.setSize(1);
                    entity.setWander(false);
                    sender.showEntity(NikeySystem.getPlugin(),entity);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            entity.remove();;
                        }
                    }.runTaskLater(NikeySystem.getPlugin(),50);
                }
            }
        }
    }
}
