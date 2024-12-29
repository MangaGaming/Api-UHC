package com.mguhc.game;

import java.util.ArrayList;
import java.util.List;
import com.mguhc.player.UhcPlayer;

public class GamePhase {
    private String name;
    private List<UhcPlayer> players;

    public GamePhase(String name) {
        this.name = name;
        this.players = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<UhcPlayer> getPlayers() {
        return players;
    }

    public void addPlayer(UhcPlayer player) {
        players.add(player);
    }

    public void removePlayer(UhcPlayer player) {
        players.remove(player);
    }

    // Ajoutez d'autres méthodes pour gérer la phase
}