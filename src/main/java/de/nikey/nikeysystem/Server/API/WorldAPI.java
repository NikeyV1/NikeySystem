package de.nikey.nikeysystem.Server.API;

import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Player.API.PermissionAPI;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class WorldAPI {

    public static HashMap<String , World> tempWorld = new HashMap<>();

    public static boolean isAllowedOnWorld(String player, String world) {
        if (isCreatorOnly(world)) {
            return isWorldOwner(world, player);
        }else {
            return true;
        }
    }

    public static void removeWorld(String world) {
        setWorldOwner(world,null);
        setCreatorOnly(world,false);
    }

    public static boolean isCreatorOnly(String world) {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();

        return config.getBoolean("system.world.creatoronly." + world);
    }

    public static void setCreatorOnly(String world, boolean active) {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();

         config.set("system.world.creatoronly." + world , active);
         NikeySystem.getPlugin().saveConfig();
    }

    public static String getWorldOwner(String world) {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();

        String owner = config.getString("system.world.owner." + world);
        return Objects.requireNonNullElseGet(owner, PermissionAPI::getOwner);
    }

    public static void setWorldOwner(String world,String owner) {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();

        if (owner == null) {
            config.set("system.world.owner." + world,"");
        }
        config.set("system.world.owner." + world,owner);
        NikeySystem.getPlugin().saveConfig();
    }

    public static boolean isWorldOwner(String world,String owner) {
        FileConfiguration config = NikeySystem.getPlugin().getConfig();

        String st = config.getString("system.world.owner." + world);
        String o = Objects.requireNonNullElseGet(st, PermissionAPI::getOwner);
        return o.equalsIgnoreCase(owner);
    }


    public static List<String> getAutoStarting() {
        List<String> autostartWorlds = new ArrayList<>(NikeySystem.getPlugin().getConfig().getStringList("system.world.autostartworlds"));
        File worldContainer = Bukkit.getWorldContainer();

        autostartWorlds.removeIf(worldName -> !new File(worldContainer, worldName).exists());

        NikeySystem.getPlugin().getConfig().set("system.world.autostartworlds", autostartWorlds);
        NikeySystem.getPlugin().saveConfig();
        return autostartWorlds;
    }

    public static void addAutoStart(World world) {
        List<String> worlds = NikeySystem.getPlugin().getConfig().getStringList("system.world.autostartworlds");

        if (!worlds.contains(world.getName())) {
            worlds.add(world.getName());
            NikeySystem.getPlugin().getConfig().set("system.world.autostartworlds", worlds);
            NikeySystem.getPlugin().saveConfig();
        }
    }

    public static void removeAutoStart(World world) {
        List<String> worlds = NikeySystem.getPlugin().getConfig().getStringList("system.world.autostartworlds");

        if (worlds.contains(world.getName())) {
            worlds.remove(world.getName());
            NikeySystem.getPlugin().getConfig().set("system.world.autostartworlds", worlds);
            NikeySystem.getPlugin().saveConfig();
        }
    }

    public static boolean isAutoStaring(World world) {
        List<String> stringList = NikeySystem.getPlugin().getConfig().getStringList("system.world.autostartworlds");
        return stringList.contains(world.getName());
    }

    public static void loadWorlds() {
        for (String worlds : getAutoStarting()) {
            Bukkit.createWorld(new WorldCreator(worlds));
        }
    }
}
