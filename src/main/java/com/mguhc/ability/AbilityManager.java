package com.mguhc.ability;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mguhc.roles.UhcRole;

public class AbilityManager {
    // Map pour stocker les capacités de chaque rôle
    private Map<UhcRole, List<Ability>> abilities;

    public AbilityManager() {
        this.abilities = new HashMap<>();
    }

    // Méthode pour enregistrer une capacité pour un rôle
    public void registerAbility(UhcRole role, List<Ability> ability) {
        abilities.put(role, ability);
    }

    // Méthode pour obtenir la capacité d'un rôle
    public List<Ability> getAbilitys(UhcRole role) {
        return abilities.get(role);
    }
}