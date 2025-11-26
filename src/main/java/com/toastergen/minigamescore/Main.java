package com.toastergen.minigamescore;

import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


import java.io.File;

public class Main extends JavaPlugin {
    private ConfigData configData;
    private YamlConfiguration mapData;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        loadConfigData();
        loadConfigData();

        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        File mapdataFile = new File(getDataFolder(), "maps.yml");
        if (!mapdataFile.exists()) {
            saveResource("maps.yml", false);
        }
        mapData = YamlConfiguration.loadConfiguration(mapdataFile);

        getLogger().info("Plugin loaded!");
        getLogger().info("Current Lobby world is " + configData.LOBBY_WORLD_NAME);
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin disabled!");
    }

    private void loadConfigData() {
        this.configData = new ConfigData(getConfig());
    }
}
