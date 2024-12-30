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
    private static final float MAX_WALK_SPEED = 0.2f; // Maximum allowed walk speed
    private Map<Player, Integer> speedEffects = new HashMap<>();
    private Map<Player, Integer> strengthEffects = new HashMap<>();
    private Map<Player, Integer> resistanceEffects = new HashMap<>();
    private Map<Player, Integer> weaknessEffects = new HashMap<>();

    // Method to apply a speed effect
    public void setSpeed(Player player, int percentage) {
        speedEffects.put(player, percentage);
    }

    // Method to apply a strength effect
    public void setStrength(Player player, int percentage) {
        strengthEffects.put(player, percentage);
    }

    // Method to apply a resistance effect
    public void setResistance(Player player, int percentage) {
        resistanceEffects.put(player, percentage);
    }

    public void setWeakness(Player player, int percentage) {
        weaknessEffects.put(player, percentage);
    }

    // Method to remove an effect
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
            player.setWalkSpeed((float) 0.2 * speedEffects.get(player) / 100);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            if (strengthEffects.containsKey(attacker)) {
                int percentage = strengthEffects.get(attacker);
                // Apply the strength effect
                double damageMultiplier = 1 + (percentage / 100.0);
                event.setDamage(event.getDamage() * damageMultiplier);
            }
            if (weaknessEffects.containsKey(attacker)) {
                int percentage = weaknessEffects.get(attacker);
                // Apply the weakness effect
                double damageMultiplier = 1 + (percentage / 100.0);
                event.setDamage(event.getDamage() / damageMultiplier);
            }
        }
        if (event.getEntity() instanceof Player) {
            Player victim = (Player) event.getEntity();
            if (resistanceEffects.containsKey(victim)) {
                double damage = event.getDamage();
                int percentage = resistanceEffects.get(victim);
                double damageReduction = damage * (percentage / 100.0);
                damage -= damageReduction;

                // Ensure damage does not go below zero
                if (damage < 0) {
                    damage = 0;
                }
                event.setDamage(damage);
            }
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
            if (weaknessEffects.containsKey(player)) {
                message.append("Faiblesse : ").append(weaknessEffects.get(player)).append("%\n");
            }

            player.sendMessage(message.toString());
            event.setCancelled(true); // Cancel the command to prevent default display
        }
    }

    public int getEffect(Player player, PotionEffectType effect) {
        if (effect.equals(PotionEffectType.SPEED) && speedEffects.containsKey(player)) {
            return speedEffects.get(player);
        } else if (effect.equals(PotionEffectType.INCREASE_DAMAGE) && strengthEffects.containsKey(player)) {
            return strengthEffects.get(player);
        } else if (effect.equals(PotionEffectType.DAMAGE_RESISTANCE) && resistanceEffects.containsKey(player)) {
            return resistanceEffects.get(player);
        } else if (effect.equals(PotionEffectType.WEAKNESS) && weaknessEffects.containsKey(player)) {
            return weaknessEffects.get(player);
        } else {
            return 0; // Return 0 if no effect is found
        }
    }
}