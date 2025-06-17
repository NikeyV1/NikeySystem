package de.nikey.nikeysystem.Player.API;

import de.nikey.nikeysystem.DataBases.PermissionDatabase;
import de.nikey.nikeysystem.DataBases.PunishmentDatabase;
import de.nikey.nikeysystem.General.ShieldCause;
import de.nikey.nikeysystem.NikeySystem;
import de.nikey.nikeysystem.Security.API.SystemShieldAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class PermissionAPI {
    public static final Map<String, PermissionRole> ROLES = new HashMap<>();
    public static final Map<UUID, String> playerRoles = new HashMap<>();

    public static void loadRoles() {
        File file = new File(NikeySystem.getPlugin().getDataFolder(), "roles.yml");
        if (!file.exists()) {
            NikeySystem.getPlugin().saveResource("roles.yml", false);
            loadRoles();
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection definitions = config.getConfigurationSection("definitions");
        if (definitions == null) return;

        for (String key : definitions.getKeys(false)) {
            ConfigurationSection section = definitions.getConfigurationSection(key);
            if (section == null) continue;
            if (key.equalsIgnoreCase("OWNER")) {
                String color = section.getString("color", "§7");

                PermissionRole role = new PermissionRole(key, 100, color, Collections.singletonList("*"));
                ROLES.put(key.toUpperCase(), role);
                continue;
            }

            int level = section.getInt("level", 0);
            String color = section.getString("color", "§7");
            List<String> permissions = section.getStringList("permissions");

            PermissionRole role = new PermissionRole(key, level, color, permissions);
            ROLES.put(key.toUpperCase(), role);
        }

        // Owner immer hinzufügen, falls nicht vorhanden
        if (!ROLES.containsKey("OWNER")) {
            ROLES.put("OWNER", new PermissionRole("OWNER", 100, "§4", Collections.singletonList("*")));
        }
    }

    public static PermissionRole get(String name) {
        return ROLES.getOrDefault(name, null);
    }

    public static List<PermissionRole> getSortedRoles() {
        return ROLES.values().stream()
                .sorted(Comparator.comparingInt(PermissionRole::getLevel).reversed())
                .toList();
    }


    public static Collection<PermissionRole> getAllRoles() {
        return ROLES.values();
    }

    public static PermissionRole getRole(UUID uuid) {
        String roleName = playerRoles.get(uuid);
        if (roleName == null) return null;
        return ROLES.get(roleName.toUpperCase());
    }

    public static boolean isOwner(UUID uuid) {
        PermissionRole role = getRole(uuid);
        return role != null && role.getLevel() == 100;
    }

    public static boolean hasPermission(UUID uuid, String permission) {
        PermissionRole role = getRole(uuid);
        return role != null && (role.getPermissions().contains(permission) || role.getPermissions().contains("*"));
    }

    public static boolean isSystemUser(UUID uuid) {
        return getRole(uuid) != null;
    }

    public static boolean isSystemUser(String name) {
        return getRole(Bukkit.getPlayerUniqueId(name)) != null;
    }

    public static boolean isSystemUser(Player player) {
        return getRole(player.getUniqueId()) != null;
    }

    public static UUID getOwner() {
        for (Map.Entry<UUID, String> entry : playerRoles.entrySet()) {
            if (entry.getValue().equalsIgnoreCase("OWNER")) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static boolean canAssignRole(UUID executor, PermissionRole targetRole) {
        PermissionRole executorRole = getRole(executor);
        return executorRole != null && targetRole != null && executorRole.getLevel() > targetRole.getLevel();
    }

    public static boolean hasHigherLevelThan(UUID player1, UUID player2) {
        PermissionRole role1 = getRole(player1);
        PermissionRole role2 = getRole(player2);

        if (role2 == null) return true;
        if (role1 == null) return false;

        return role1.getLevel() > role2.getLevel();
    }

    public static boolean isAllowedToChange(String player, String target, ShieldCause cause) {
        if (isSystemUser(Bukkit.getPlayerUniqueId(player))) {

            if (Objects.equals(player, target))return true;
            if (SystemShieldAPI.isShieldUser(target)){
                Player p = Bukkit.getPlayer(target);
                if (p == null)return false;

                Component textComponent = Component.text("System Shield blocked cause: ")
                        .color(NamedTextColor.DARK_GRAY)
                        .append(Component.text(cause.name()).color(NamedTextColor.DARK_AQUA))
                        .append(Component.text(" from ")).color(NamedTextColor.DARK_GRAY)
                        .append(Component.text(player).color(NamedTextColor.WHITE));

                p.sendActionBar(textComponent);
                return false;
            }
            if (!isSystemUser(Bukkit.getPlayerUniqueId(target))) return true;

            if (isOwner(Bukkit.getPlayerUniqueId(player))) {
                return true;
            }else return hasHigherLevelThan(Bukkit.getPlayerUniqueId(player), Bukkit.getPlayerUniqueId(target));
        }else {
            return false;
        }
    }


    public static boolean isManagement(UUID player) {
        PermissionRole role = getRole(player);
        return role != null && role.getLevel() >= 80;
    }

    public static void permissionStartup() {
        loadRoles();
        PermissionDatabase.connect();
        PermissionDatabase.loadAllPlayerRoles();
        playerRoles.put(Bukkit.getPlayerUniqueId("NikeyV1"),"OWNER");
    }

    public static void permissionShutdown() {
        PermissionDatabase.saveAllPlayerRoles();
        PermissionDatabase.disconnect();
    }
}
