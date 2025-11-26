package com.toastergen.minigamescore;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigData {
    public final String LOBBY_WORLD_NAME;

    public ConfigData(FileConfiguration config) {
        LOBBY_WORLD_NAME = config.getString("settings.lobby.world");
    }
}
