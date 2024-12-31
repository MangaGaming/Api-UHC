package com.mguhc.roles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack; // Assurez-vous d'importer la classe ItemStack
import org.bukkit.potion.PotionEffectType;

import com.mguhc.UhcAPI;
import com.mguhc.effect.EffectManager; // Importer l'EffectManager
import com.mguhc.player.UhcPlayer;

public class RoleManager {
    private Map<UhcPlayer, UhcRole> playerRoles;
    private Map<UhcPlayer, Camp> playerCamps;
    private List<UhcRole> validRoles;
    private List<UhcRole> activeRoles;
    private List<Camp> camps;
    private Map<UhcRole, List<ItemStack>> roleItems;
    private Map<UhcRole, Map<PotionEffectType, Integer>> roleEffects;
    private EffectManager effectManager;

    public RoleManager() {
        this.effectManager = UhcAPI.getInstance().getEffectManager();
        playerRoles = new HashMap<>();
        playerCamps = new HashMap<>();
        validRoles = new ArrayList<>();
        activeRoles = new ArrayList<>();
        camps = new ArrayList<>();
        roleItems = new HashMap<>();
        roleEffects = new HashMap<>(); // Initialiser la Map des effets par rôle
    }

    public void assignRole(UhcPlayer uhc_player, UhcRole role) {
        playerRoles.put(uhc_player, role);
        
        // Trouver le camp associé au rôle
        Camp assignedCamp = null;
        for (Camp camp : camps) {
            if (camp.getAssociatedRoles().contains(role)) {
                assignedCamp = camp;
                break;
            }
        }

        Player player = uhc_player.getPlayer();
		if (assignedCamp != null) {
            playerCamps.put(uhc_player, assignedCamp); // Assigner le camp au joueur
            player.sendMessage("Vous avez été assigné au camp : " + assignedCamp.getName());
        } else {
            player.sendMessage("Aucun camp associé à ce rôle.");
        }

        player.sendMessage(role.getDescription());

        // Donner les items associés au rôle
        List<ItemStack> itemsToGive = roleItems.get(role);
        if (itemsToGive != null) {
            for (ItemStack item : itemsToGive) {
                player.getInventory().addItem(item); // Ajouter l'item à l'inventaire du joueur
            }
        }
        
        Map<PotionEffectType, Integer> effects = roleEffects.get(role);
        if(effects != null) {
            for(Map.Entry<PotionEffectType, Integer> entry : effects.entrySet()) {
            	PotionEffectType effect = entry.getKey();
            	Integer percentage = entry.getValue();
            	if(effect.equals(PotionEffectType.INCREASE_DAMAGE)) {
            		effectManager.setStrength(player, percentage);
            	}
            	if(effect.equals(PotionEffectType.DAMAGE_RESISTANCE)) {
            		effectManager.setResistance(player, percentage);
            	}
            	if(effect.equals(PotionEffectType.SPEED)) {
            		effectManager.setSpeed(player, percentage);
            	}
            	if(effect.equals(PotionEffectType.WEAKNESS)) {
            		effectManager.setWeakness(player, percentage);
            	}
            }
        }
    }

    public UhcRole getRole(UhcPlayer player) {
        return playerRoles.get(player);
    }

    public void removeRole(UhcPlayer player) {
        playerRoles.remove(player);
        player.getPlayer().sendMessage("Votre rôle a été retiré.");
    }

    public void listRoles() {
        for (Map.Entry<UhcPlayer, UhcRole> entry : playerRoles.entrySet()) {
            UhcPlayer player = entry.getKey();
            UhcRole role = entry.getValue();
            player.getPlayer().sendMessage(player.getPlayer().getName() + " a le rôle : " + role.getName());
        }
    }

    public UhcPlayer getPlayerWithRole(String roleName) {
        for (Map.Entry<UhcPlayer, UhcRole> entry : playerRoles.entrySet()) {
            UhcPlayer player = entry.getKey();
            UhcRole role = entry.getValue();
            if (role.getName().equalsIgnoreCase(roleName)) {
                return player;
            }
        }
        return null;
    }

    public UhcRole getUhcRole(String roleName) {
        for (UhcRole role : validRoles) {
            if (role.getName().equals(roleName)) {
                return role;
            }
        }
        return null;
    }

    public List<UhcRole> getValidRoles() {
        return validRoles;
    }

    public List<UhcRole> getActiveRoles() {
        return activeRoles;
    }

    public void addRole(UhcRole role) {
        if (!validRoles.contains(role)) {
            validRoles.add(role);
        }
    }

    public void addCamp(Camp camp) {
        if (!camps.contains(camp)) {
            camps.add(camp);
        }
    }

    public void removeCamp(String campName) {
        camps.removeIf(camp -> camp.getName().equalsIgnoreCase(campName));
    }

    public void editCamp(String campName, Camp newCamp) {
        for (int i = 0; i < camps.size(); i++) {
            if (camps.get(i).getName().equalsIgnoreCase(campName)) {
                camps.set(i, newCamp);
                break;
            }
        }
    }

    public List<Camp> getCamps() {
        return camps;
    }

    public List<UhcPlayer> getPlayersInCamp(String campName) {
        List<UhcPlayer> playersInCamp = new ArrayList<>();
        for (Map.Entry<UhcPlayer, Camp> entry : playerCamps.entrySet()) {
            UhcPlayer player = entry.getKey();
            Camp playerCamp = entry.getValue();
            if (playerCamp != null && playerCamp.getName().equalsIgnoreCase(campName)) {
                playersInCamp.add(player);
            }
        }
        return playersInCamp;
    }

    public void setCamp(UhcPlayer player, Camp camp) {
        playerCamps.remove(player);
        playerCamps.put(player, camp);
    }

    public Camp getCamp(UhcPlayer player) {
        return playerCamps.get(player);
    }

    public void setItemToGive(String roleName, List<ItemStack> items) {
        for (UhcRole role : validRoles) {
            if (role.getName().equalsIgnoreCase(roleName)) {
                roleItems.put(role, items);
                break;
            }
        }
    }

    public void setEffectsToGive(String roleName, Map<PotionEffectType, Integer> effects) {
    	UhcRole role = getUhcRole(roleName);
    	roleEffects.put(role, effects);
    }
}