package com.toastergen.minigamescore.gameinstances;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class GameArena {
    private String gameId;
    private World world;
    private GameState state;
    private int currentPlayers;
    private int maxPlayers;
    private String mapName;
    private List<Player> playerList;
    private Location spawnLocation;

    public GameArena(String gameId, String mapName, World world, Location gameSpawnLoc) {
        this.gameId = gameId;
        this.mapName = mapName;
        this.world = world;
        this.state = GameState.WAITING;
        this.currentPlayers = 0;
        this.maxPlayers = 12;
        this.playerList = new ArrayList<>();
        this.spawnLocation = gameSpawnLoc;
    }

    public String getGameId() { return gameId; }
    public World getWorld() { return world; }
    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }
    public int getCurrentPlayers() { return currentPlayers; }
    public int getMaxPlayers() { return maxPlayers; }
    public String getMapName() { return mapName; }
    public Location getSpawnLocation() { return spawnLocation; }
    public List<Player> getPlayerList() { return playerList; }

    public void addPlayer() { currentPlayers++; }
    public void removePlayer() { currentPlayers--; }
}
