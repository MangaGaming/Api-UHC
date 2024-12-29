package com.mguhc.scenario;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.mguhc.UhcAPI;

public class NoStoneVariant extends Scenario implements Listener {

	public NoStoneVariant() {
		super("NoStoneVariant");
	}

	@Override
	public void onActivate() {
		Bukkit.getPluginManager().registerEvents(this, UhcAPI.getInstance());
	}
	
	@EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Vérifie si le bloc est de la pierre avec les data values spécifiques
        Material blockType = event.getBlock().getType();
        @SuppressWarnings("deprecation")
		byte data = event.getBlock().getData(); // Récupère la data value du bloc

        if (blockType == Material.STONE && (data == 1 || data == 3 || data == 5)) {
            // Supprimer les drops normaux
            event.getBlock().getDrops().clear();
            // Ajouter la cobblestone
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.COBBLESTONE));
        }
    }

	@Override
	public void onDeactivate() {
		
	}

}
