package com.mguhc.scenario;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent; // Changer ici
import org.bukkit.inventory.ItemStack;

import com.mguhc.UhcAPI;

public class HasteyBoys extends Scenario implements Listener {

    public HasteyBoys() {
        super("HasteyBoys");
    }

    @Override
    public void onActivate() {
        System.out.println(getName() + " activated!");
        Bukkit.getPluginManager().registerEvents(this, UhcAPI.getInstance());
    }

    @Override
    public void onDeactivate() {
        System.out.println(getName() + " deactivated!");
    }

    @EventHandler
    public void onPlayerCraftItem(CraftItemEvent event) { // Changer ici
        ItemStack craftedItem = event.getCurrentItem(); // Changer ici

        // Vérifiez si l'objet fabriqué est un outil (par exemple, une pioche, une hache, etc.)
        if (craftedItem != null && isTool(craftedItem.getType())) {
            // Appliquez les enchantements
            craftedItem.addEnchantment(Enchantment.DIG_SPEED, 5); // Efficacité V
            craftedItem.addEnchantment(Enchantment.DURABILITY, 3); // Durabilité III
        }
    }

    private boolean isTool(Material material) {
        return material == Material.STONE_PICKAXE || 
               material == Material.IRON_PICKAXE || 
               material == Material.DIAMOND_PICKAXE ||
               material == Material.STONE_AXE ||
               material == Material.IRON_AXE ||
               material == Material.DIAMOND_AXE;
               // Ajoutez d'autres outils si nécessaire
    }
}