package com.mguhc.player;

import com.mguhc.roles.Camp;
import com.mguhc.roles.RoleManager;
import org.bukkit.entity.Player;

import com.mguhc.UhcAPI;
import com.mguhc.roles.UhcRole;

public class UhcPlayer {
    private Player player;
    private boolean isAlive;
    private int health;
    private RoleManager roleManager = UhcAPI.getInstance().getRoleManager();

    public UhcPlayer(Player player) {
        this.player = player;
        this.isAlive = true;
        this.health = 20; // Santé par défaut
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
    
    public UhcRole getRole() {
        return roleManager.getRole(this);
    }

    public Camp getCamp() {
        return roleManager.getCamp(this);
    }
}