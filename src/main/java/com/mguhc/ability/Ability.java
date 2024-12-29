package com.mguhc.ability;

import org.bukkit.entity.Player;

public interface Ability {
    void activate(Player player);
    void deactivate(Player player);
    double getCooldownDuration(); // Nouvelle méthode pour obtenir la durée de cooldown
    void setCooldownDuration(double n);
}