package com.mguhc.effect;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

public class EffectManager implements Listener {
    private Map<Player, Integer> speedEffects = new HashMap<>();
    private Map<Player, Integer> strengthEffects = new HashMap<>();
    private Map<Player, Integer> resistanceEffects = new HashMap<>();
    private Map<Player, Integer> weaknessEffects = new HashMap<>();

    // Méthode pour appliquer un effet de vitesse
    public void setSpeed(Player player, int percentage) {
        speedEffects.put(player, percentage);
    }

    // Méthode pour appliquer un effet de force
    public void setStrength(Player player, int percentage) {
        strengthEffects.put(player, percentage);
    }

    // Méthode pour appliquer un effet de résistance
    public void setResistance(Player player, int percentage) {
        resistanceEffects.put(player, percentage);
    }
    
    public void setWeakness(Player player, int percentage) {
    	weaknessEffects.put(player, percentage);
    }

    // Méthode pour supprimer un effet
    public void removeEffect(Player player, PotionEffectType effectType) {
        if (effectType == PotionEffectType.SPEED) {
            speedEffects.remove(player);
        } else if (effectType == PotionEffectType.INCREASE_DAMAGE) {
            strengthEffects.remove(player);
        } else if (effectType == PotionEffectType.DAMAGE_RESISTANCE) {
            resistanceEffects.remove(player);
        } else if (effectType == PotionEffectType.WEAKNESS) {
        	weaknessEffects.remove(player);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (speedEffects.containsKey(player)) {
            int percentage = speedEffects.get(player);
            // Appliquer l'effet de vitesse
            double speedMultiplier = 1 + (percentage / 100.0);
            player.setWalkSpeed((float) (0.2 * speedMultiplier)); // 0.2 est la vitesse normale
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            if (strengthEffects.containsKey(attacker)) {
                int percentage = strengthEffects.get(attacker);
                // Appliquer l'effet de force
                double damageMultiplier = 1 + (percentage / 100.0);
                event.setDamage(event.getDamage() * damageMultiplier);
            }
        }
        if(event.getEntity() instanceof Player) {
        	Player victim = (Player) event.getEntity();
            if (resistanceEffects.containsKey(victim)) {
            	double damage = event.getDamage();
                int percentage = resistanceEffects.get(victim);
                double damageReduction = damage * (percentage / 100.0);
                damage -= damageReduction;
                event.setDamage(damage);
            }
        }
    }

    // Méthode pour gérer la résistance
    public void applyResistance(Player player, double damage) {
        if (resistanceEffects.containsKey(player)) {
            int percentage = resistanceEffects.get(player);
            double damageReduction = damage * (percentage / 100.0);
            damage -= damageReduction;
        }
    }
    
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();

        if (command.equalsIgnoreCase("/effects")) {
            StringBuilder message = new StringBuilder("Vos effets :\n");

            if (speedEffects.containsKey(player)) {
                message.append("Vitesse : ").append(speedEffects.get(player)).append("%\n");
            }
            if (strengthEffects.containsKey(player)) {
                message.append("Force : ").append(strengthEffects.get(player)).append("%\n");
            }
            if (resistanceEffects.containsKey(player)) {
                message.append("Résistance : ").append(resistanceEffects.get(player)).append("%\n");
            }

            player.sendMessage(message.toString());
            event.setCancelled(true); // Annuler la commande pour éviter l'affichage par défaut
        }
    }
}