package com.mguhc.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mguhc.UhcAPI;
import com.mguhc.game.UhcGame;
import com.mguhc.player.PlayerManager;
import com.mguhc.roles.RoleManager;

public class UhcStartEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public RoleManager getRoleManager() {
    	return UhcAPI.getInstance().getRoleManager();
    }
    
    public PlayerManager getPlayerManager() {
    	return UhcAPI.getInstance().getPlayerManager();
    }
    
    public UhcGame getUhcGame() {
    	return UhcAPI.getInstance().getUhcGame();
    }
}