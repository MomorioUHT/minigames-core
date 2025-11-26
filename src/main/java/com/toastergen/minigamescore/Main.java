package com.toastergen.minigamescore;

import com.toastergen.minigamescore.gui.GameSelector;
import com.toastergen.minigamescore.locations.WorldManager;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


import java.io.File;
import java.util.ArrayList;

public class Main extends JavaPlugin {
    private ConfigData configData;
    private YamlConfiguration mapData;

    private GameSelector gameSelector;
    private WorldManager worldManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        configData = new ConfigData(getConfig());

        File mapFile = new File(getDataFolder(), "maps.yml");
        if (!mapFile.exists()) saveResource("maps.yml", false);
        mapData = YamlConfiguration.loadConfiguration(mapFile);


        worldManager = new WorldManager(this, configData);
        getLogger().info("Loading maps...");
        worldManager.initializeArenas(mapData);

        gameSelector= new GameSelector(worldManager, configData);
        getServer().getPluginManager().registerEvents(gameSelector, this);

        getCommand("leave").setExecutor(this);
        getCommand("menu").setExecutor(this);
    }

    @Override
    public void onDisable() {
        var worldsToUnload = worldManager.getActiveArenas().stream()
                .map(a -> a.getWorld().getName())
                .toList();
        worldsToUnload.forEach(worldManager::unloadGame);
        getLogger().info("Plugin disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("leave")) {
            worldManager.leaveGame(player);
            return true;
        }

        if (command.getName().equalsIgnoreCase("menu")) {
            gameSelector.openGUI(player);
            return true;
        }

        return false;
    }

    private void loadConfigData() {
        this.configData = new ConfigData(getConfig());
    }
}
