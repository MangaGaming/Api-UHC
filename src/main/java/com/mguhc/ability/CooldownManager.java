package com.mguhc.ability;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

public class CooldownManager {
    // Map pour stocker les cooldowns des joueurs pour chaque capacité
    public Map<Player, Map<Ability, Double>> playerCooldowns;

    public CooldownManager() {
        this.playerCooldowns = new HashMap<>();
    }

    // Méthode pour démarrer un cooldown pour un joueur et une capacité spécifique
    public void startCooldown(Player player, Ability ability) {
        double duration = ability.getCooldown(); // Obtenir la durée de cooldown de l'ability
        playerCooldowns.putIfAbsent(player, new HashMap<>());
        playerCooldowns.get(player).put(ability, System.currentTimeMillis() + duration);
    }

    // Méthode pour vérifier si un joueur est en cooldown pour une capacité spécifique
    public boolean isInCooldown(Player player, Ability ability) {
        return getRemainingCooldown(player, ability) == 0;
    }

    // Méthode pour obtenir le temps restant sur le cooldown d'un joueur pour une capacité spécifique
    public double getRemainingCooldown(Player player, Ability ability) {
        // Vérifiez si le joueur a des cooldowns enregistrés
        if (!playerCooldowns.containsKey(player)) {
            return 0; // Aucun cooldown enregistré pour ce joueur
        }

        // Vérifiez si l'ability à un cooldown enregistré
        Map<Ability, Double> cooldowns = playerCooldowns.get(player);
        if (!cooldowns.containsKey(ability)) {
            return 0; // Aucun cooldown enregistré pour cette capacité
        }

        double endTime = cooldowns.get(ability);
        return endTime - System.currentTimeMillis(); // Temps restant en millisecondes
    }

    // Méthode pour retirer le cooldown d'un joueur pour une capacité spécifique manuellement
    public void removeCooldown(Player player, Ability ability) {
        if (playerCooldowns.containsKey(player)) {
            playerCooldowns.get(player).remove(ability);
        }
    }

    // Méthode pour retirer tous les cooldowns d'un joueur
    public void removeAllCooldowns(Player player) {
        playerCooldowns.remove(player);
    }
}