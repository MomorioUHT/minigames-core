package com.toastergen.minigamescore;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigData {
    private final Location globalLobbyLocation;
    public final String PREFIX;
    public final String JOIN_TITLE;
    public final String JOIN_SUBTITLE;
    public final String GAME_TYPE;

    public final Sound JOIN_SOUND;

    // Menu
    public final Material SELECTOR_MATERIAL;
    public final int SELECTOR_SLOT;
    public final String SELECTOR_NAME;

    public final Material LEAVE_MATERIAL;
    public final int LEAVE_SLOT;
    public final String LEAVE_NAME;

    public ConfigData(FileConfiguration config) {
        String worldName = config.getString("settings.lobby.world");
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            world = Bukkit.getWorlds().get(0);
        }

        double x = config.getDouble("settings.lobby.teleport-location.x");
        double y = config.getDouble("settings.lobby.teleport-location.y");
        double z = config.getDouble("settings.lobby.teleport-location.z");
        float yaw = (float) config.getDouble("settings.lobby.teleport-location.yaw");
        float pitch = (float) config.getDouble("settings.lobby.teleport-location.pitch");

        this.globalLobbyLocation = new Location(world, x, y, z, yaw, pitch);

        this.PREFIX = config.getString("settings.prefix");
        this.JOIN_TITLE = config.getString("settings.join-title");
        this.JOIN_SUBTITLE = config.getString("settings.join-subtitle");
        this.GAME_TYPE = config.getString("settings.game-type");
        this.JOIN_SOUND = praseSound(config.getString("settings.join-sound"));

        this.SELECTOR_MATERIAL = Material.getMaterial(config.getString("items.game-selector.item"));
        this.SELECTOR_SLOT = config.getInt("items.game-selector.slot");
        this.SELECTOR_NAME = config.getString("items.game-selector.name");

        this.LEAVE_MATERIAL = Material.getMaterial(config.getString("items.leave-game.item"));
        this.LEAVE_SLOT = config.getInt("items.leave-game.slot");
        this.LEAVE_NAME = config.getString("items.leave-game.name");
    }

    private Sound praseSound(String soundName) {
        try {
            return Sound.valueOf(soundName.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("[MurderMystery] Invalid sound configured: " + soundName + ". Using default.");
            return Sound.NOTE_STICKS;
        }
    }

    public Location getGlobalLobbyLocation() {
        return globalLobbyLocation;
    }
}
