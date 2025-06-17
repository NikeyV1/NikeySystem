package de.nikey.nikeysystem.Player.API;

import java.util.List;

public class PermissionRole {
    private final String name;
    private final int level;
    private final String color;
    private final List<String> permissions;

    public PermissionRole(String name, int level, String color, List<String> permissions) {
        this.name = name;
        this.level = Math.min(level, 100);
        this.color = color;
        this.permissions = permissions;
    }

    public String getName() { return name; }
    public int getLevel() { return level; }
    public String getColor() { return color; }
    public List<String> getPermissions() { return permissions; }

    public boolean hasPermission(String perm) {
        return permissions.contains("*") || permissions.contains(perm);
    }
}