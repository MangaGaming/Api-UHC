package com.mguhc.player;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager {
    private Map<Player, UhcPlayer> players;

    public PlayerManager() {
        players = new HashMap<>();
    }

    public void addPlayer(Player player) {
        players.put(player, new UhcPlayer(player));
    }

    public UhcPlayer getPlayer(Player player) {
        return players.get(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }
    
    public Map<Player, UhcPlayer> getPlayers(){
    	return players;
    }
}