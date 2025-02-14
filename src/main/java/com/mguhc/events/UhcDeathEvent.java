package com.mguhc.events;

import com.avaje.ebean.validation.NotNull;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class UhcDeathEvent extends Event {
    private final Player player;
    private final Player killer;
    private final List<ItemStack> drops;
    private final Location location;
    private static final HandlerList handlers = new HandlerList();

    public UhcDeathEvent(Player player, Player killer, List<ItemStack> drops, Location location) {
        this.player = player;
        this.killer = killer;
        this.drops = drops;
        this.location = location;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public @NotNull Player getKiller() {
        return killer;
    }

    public List<ItemStack> getDrops() {
        return drops;
    }

    public Location getLocation() {
        return location;
    }
}
