package de.nikey.nikeysystem.Server.API;

import de.nikey.nikeysystem.NikeySystem;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorldAPI {

    public static HashMap<Player, World> tempWorld = new HashMap<>();

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
