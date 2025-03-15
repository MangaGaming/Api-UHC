package com.mguhc.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerManager {
    private final Map<UUID, UhcPlayer> players = new HashMap<>();
    private final Map<UUID, Integer> killMap = new HashMap<>();

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
        // Vérifiez si le joueur est null
        if (player == null) {
            return; // Sortir si le joueur est null
        }

        // Obtenez le nombre de kills actuel
        Integer currentKills = getKill(player);

        // Si currentKills est null, initialisez-le à 0
        if (currentKills == null) {
            currentKills = 0;
        }

        // Incrémentez le nombre de kills et mettez à jour le killMap
        killMap.put(player.getUniqueId(), currentKills + 1);
    }

    public List<Player> getPlayersAround(Player player, int distance) {
        List<Player> nearbyPlayers = new ArrayList<>();
        for (Entity e : player.getNearbyEntities(distance, distance, distance)){
            if (e instanceof Player) {
                nearbyPlayers.add((Player) e);
            }
        }
        return nearbyPlayers;
    }
}