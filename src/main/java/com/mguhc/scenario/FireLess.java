package com.mguhc.scenario;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.mguhc.UhcAPI;

public class FireLess extends Scenario implements Listener {

	public FireLess() {
		super("FireLess");
	}

	@Override
	public void onActivate() {
		Bukkit.getPluginManager().registerEvents(this, UhcAPI.getInstance());
	}
	
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof org.bukkit.entity.Player) {
            DamageCause cause = event.getCause();

            // Empêche les dégâts de feu et de lave
            if (cause == DamageCause.FIRE || cause == DamageCause.LAVA || cause == DamageCause.FIRE_TICK) {
                event.setCancelled(true);
            }
        }
    }

	@Override
	public void onDeactivate() {
		
	}

}
