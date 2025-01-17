package com.mguhc.roles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import com.mguhc.player.UhcPlayer;

public class RoleManager {
    private Map<UhcPlayer, UhcRole> playerRoles;
    private Map<UhcPlayer, Camp> playerCamps;
    private List<UhcRole> validRoles;
    private List<UhcRole> activeRoles;
    private List<Camp> camps;

    public RoleManager() {
        playerRoles = new HashMap<>();
        playerCamps = new HashMap<>();
        validRoles = new ArrayList<>();
        activeRoles = new ArrayList<>();
        camps = new ArrayList<>();
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
}