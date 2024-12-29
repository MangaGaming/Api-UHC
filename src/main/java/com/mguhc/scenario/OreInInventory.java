package com.mguhc.scenario;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.mguhc.UhcAPI;

public class OreInInventory extends Scenario implements Listener {
    public OreInInventory() {
        super("OreInInventory");
    }

    @Override
    public void onActivate() {
        // Enregistrer l'écouteur d'événements
        Bukkit.getPluginManager().registerEvents(this, UhcAPI.getInstance());
        System.out.println(getName() + " activated!");
    }

    @Override
    public void onDeactivate() {
        // Désenregistrer l'écouteur d'événements
        BlockBreakEvent.getHandlerList().unregister(this);
        System.out.println(getName() + " deactivated!");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material blockType = event.getBlock().getType();
        ItemStack itemStack = null;

        // Vérifier si le bloc miné est un minerai
        if (blockType == Material.IRON_ORE) {
            itemStack = new ItemStack(Material.IRON_INGOT, 1);
            event.getPlayer().giveExp(1); // Ajouter de l'XP
        } else if (blockType == Material.GOLD_ORE) {
            itemStack = new ItemStack(Material.GOLD_INGOT, 1);
            event.getPlayer().giveExp(1); // Ajouter de l'XP
        } else if (blockType == Material.DIAMOND_ORE) {
            itemStack = new ItemStack(Material.DIAMOND, 1);
            event.getPlayer().giveExp(1); // Ajouter de l'XP
        }

        // Si un minerai a été miné, donner l'item au joueur
        if (itemStack != null) {
            event.getPlayer().getInventory().addItem(itemStack);
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
        }
    }
}
