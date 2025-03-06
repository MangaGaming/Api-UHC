package com.mguhc.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
    private Map<UUID, UhcPlayer> players;
    private Map<UUID, Integer> killMap;

    public PlayerManager() {
        players = new HashMap<>();
    }

    public void addPlayer(Player player) {
        players.put(player.getUniqueId(), new UhcPlayer(player));
    }

    public UhcPlayer getPlayer(Player player) {
        return players.get(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
    }
    
    public Map<Player, UhcPlayer> getPlayers(){
    	Map<Player, UhcPlayer> playerUhcPlayerMap = new HashMap<>();
        for (Map.Entry<UUID, UhcPlayer> entry : players.entrySet()) {
            playerUhcPlayerMap.put(Bukkit.getPlayer(entry.getKey()), entry.getValue());
        }
        return playerUhcPlayerMap;
    }

    public int getKill(Player player) {
        return killMap.getOrDefault(player.getUniqueId(), 0);
    }

    public void addKill(Player player) {
        killMap.put(player.getUniqueId(), getKill(player) + 1);
    }
}