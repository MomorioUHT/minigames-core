package com.toastergen.minigamescore.locations;

import com.toastergen.minigamescore.ConfigData;
import com.toastergen.minigamescore.gameinstances.GameArena;
import com.toastergen.minigamescore.gameinstances.GameState;
import com.toastergen.minigamescore.gui.LobbyItemManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public class WorldManager {
    private final JavaPlugin plugin;
    private final File templatesFolder;
    private final ConfigData configData;
    private LobbyItemManager lobbyItemManager;
    private final Map<String, GameArena> activeArenas = new HashMap<>();

    private final Map<UUID, GameArena> playerGameMap = new HashMap<>();

    public WorldManager(JavaPlugin plugin, ConfigData configData) {
        this.plugin = plugin;
        this.configData = configData;
        this.templatesFolder = new File(plugin.getDataFolder(), "templates-map");
        if (!templatesFolder.exists()) {
            templatesFolder.mkdirs();
        }
    }

    public void setLobbyItemManager(LobbyItemManager manager) {
        this.lobbyItemManager = manager;
    }

    public void initializeArenas(YamlConfiguration mapData) {
        ConfigurationSection mapsSection = mapData.getConfigurationSection("maps");
        if (mapsSection == null) return;

        for (String mapNameKey : mapsSection.getKeys(false)) {
            ConfigurationSection data = mapsSection.getConfigurationSection(mapNameKey);
            createGame(mapNameKey, data);
        }
    }

    public GameArena createGame(String templateName, ConfigurationSection data) {
        File sourceDir = new File(templatesFolder, templateName);
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            plugin.getLogger().warning("No templates " + templateName);
            return null;
        }

        Random random = new Random();
        int num1 = random.nextInt(11) + 10;
        int num2 = random.nextInt(9) + 1;
        char letter1 = (char) ('a' + random.nextInt(26));
        char letter2 = (char) ('a' + random.nextInt(26));
        String randomID = "" + num1 + letter1 + num2 + letter2;
        String newWorldName = randomID + "-" + templateName;

        File targetDir = new File(Bukkit.getWorldContainer(), newWorldName);
        plugin.getLogger().info("Creating game " + newWorldName + "...");

        if (!copyWorldFolder(sourceDir, targetDir)) {
            plugin.getLogger().severe("There is a problem copying world");
        }

        WorldCreator wc = new WorldCreator(newWorldName);
        World world = Bukkit.createWorld(wc);
        world.setAutoSave(false);

        double x = data.getDouble("lobby-location.x");
        double y = data.getDouble("lobby-location.y");
        double z = data.getDouble("lobby-location.z");
        float yaw = (float) data.getDouble("lobby-location.yaw");
        float pitch = (float) data.getDouble("lobby-location.pitch");
        Location gameSpawnLoc = new Location(world, x, y, z, yaw, pitch);

        GameArena arena = new GameArena(randomID, templateName, world, gameSpawnLoc);
        activeArenas.put(newWorldName, arena);

        plugin.getLogger().info(newWorldName + " was created successfully");
        return arena;
    }

    private boolean copyWorldFolder(File source, File target) {
        try {
            ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.lock"));
            if (!ignore.contains(source.getName())) {
                if (source.isDirectory()) {
                    if (!target.exists()) target.mkdirs();
                    String[] files = source.list();
                    if (files != null) {
                        for (String file : files) {
                            File srcFile = new File(source, file);
                            File destFile = new File(target, file);
                            copyWorldFolder(srcFile, destFile);
                        }
                    }
                } else {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                    in.close();
                    out.close();
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void unloadGame(String worldName) {
        GameArena arena = activeArenas.get(worldName);
        if (arena == null) return;

        World world = arena.getWorld();
        if (world != null) {
            for (Player p : world.getPlayers()) {
                p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            }
            Bukkit.unloadWorld(world, false);
        }

        File worldDir = new File(Bukkit.getWorldContainer(), worldName);
        deleteWorldFolder(worldDir);

        activeArenas.remove(worldName);
        plugin.getLogger().info("Game " + worldName + " was deleted!");
    }

    private boolean deleteWorldFolder(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteWorldFolder(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return path.delete();
    }

    public void joinArena(Player player, String gameId) {
        for (GameArena arena : activeArenas.values()) {
            if (arena.getGameId().equals(gameId)) {
                if (arena.getState() != GameState.WAITING && arena.getState() != GameState.STARTING) {
                    player.sendMessage(configData.PREFIX + "§cThe game §e" + arena.getGameId() + " §cis running!, please wait for open lobby!");
                    return;
                }

                leaveGame(player);
                player.teleport(arena.getSpawnLocation());
                player.getInventory().clear();
                arena.addPlayer();
                playerGameMap.put(player.getUniqueId(), arena);

                if (lobbyItemManager != null) {
                    lobbyItemManager.giveLeaveItem(player);
                }

                player.sendTitle(configData.JOIN_TITLE, configData.JOIN_SUBTITLE);
                player.playSound(player.getLocation(), configData.JOIN_SOUND, 1.0f, 1.0f);

                player.sendMessage(configData.PREFIX + "§6Map: §3" + arena.getMapName());
                player.sendMessage(configData.PREFIX + "§6Server: §3" + arena.getGameId());
                return;
            }
        }
        player.sendMessage(configData.PREFIX + "§cGame not found!");
    }

    public void leaveGame(Player player) {
        if (playerGameMap.containsKey(player.getUniqueId())) {
            GameArena arena = playerGameMap.get(player.getUniqueId());

            arena.removePlayer();
            playerGameMap.remove(player.getUniqueId());

            player.sendMessage(configData.PREFIX + "§eYou have been sent to lobby.");
        }

        player.teleport(configData.getGlobalLobbyLocation());
        player.getInventory().clear();
        if (lobbyItemManager != null) {
            lobbyItemManager.giveSelectorItem(player);
        }
    }

    public Collection<GameArena> getActiveArenas() {
        return activeArenas.values();
    }

    public GameArena getArenaByWorld(World world) {
        return activeArenas.get(world.getName());
    }
}
