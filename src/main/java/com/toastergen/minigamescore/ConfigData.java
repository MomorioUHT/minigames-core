package com.toastergen.minigamescore;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigData {
    private final Location globalLobbyLocation;
    public final String PREFIX;
    public final String JOIN_TITLE;
    public final String JOIN_SUBTITLE;
    public final String GAME_TYPE;

    public final Sound JOIN_SOUND;

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
