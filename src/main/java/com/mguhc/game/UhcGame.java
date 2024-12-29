package com.mguhc.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.mguhc.UhcAPI;
import com.mguhc.events.RoleGiveEvent;
import com.mguhc.events.UhcStartEvent;
import com.mguhc.player.UhcPlayer;
import com.mguhc.roles.RoleManager;
import com.mguhc.roles.UhcRole;

public class UhcGame {
    private RoleManager roleManager;
    private GamePhase currentPhase; // Champ pour la phase actuelle
    private int timePassed;
    private int borderTimer;
    private int borderSize;
	private boolean ismettup = false;
    private Map<Player, UhcPlayer> players;


    public UhcGame() {
        this.currentPhase = new GamePhase("Waiting"); // Initialiser la phase à "Waiting"
    }

    public void startGame() {
        roleManager = UhcAPI.getInstance().getRoleManager();
        players = UhcAPI.getInstance().getPlayerManager().getPlayers();
        if (roleManager.getActiveRoles().size() != players.size()) {
            Bukkit.broadcastMessage("[UHC] Il vous faut autant de joueur que de rôles pour lancer la partie");
            return;
        }
        // Changer la phase actuelle à "Playing"
        this.currentPhase = new GamePhase("Playing");

        // Ajouter les joueurs à la phase actuelle
        for (Map.Entry<Player, UhcPlayer> entry : players.entrySet()) {
            UhcPlayer uhcPlayer = entry.getValue();
            currentPhase.addPlayer(uhcPlayer);
            Player player = uhcPlayer.getPlayer();
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();
            for (PotionEffect potion : player.getActivePotionEffects()) {
                PotionEffectType potiontype = potion.getType();
                player.removePotionEffect(potiontype);
            }
            // Téléportation à un endroit aléatoire autour de (0, 0)
            teleportToRandomLocation(player);
        }

        // Attribuer des rôles aux joueurs à partir du RoleManager
        List<UhcRole> activeRoles = roleManager.getActiveRoles(); // Récupérer les rôles valides
        List<UhcRole> assignedRoles = new ArrayList<>(); // Pour suivre les rôles attribués

        for (Map.Entry<Player, UhcPlayer> entry : players.entrySet()) {
            UhcPlayer player = entry.getValue();
            UhcRole roleToAssign;

            // Assigner un rôle aléatoire parmi les rôles valides
            do {
                roleToAssign = activeRoles.get((int) (Math.random() * activeRoles.size()));
            } while (assignedRoles.contains(roleToAssign)); // Éviter les doublons

            assignedRoles.add(roleToAssign); // Ajouter le rôle à la liste des rôles attribués
            roleManager.assignRole(player, roleToAssign);
        }

        // Déclencher l'événement RoleGiveEvent après que tous les rôles ont été attribués
        Bukkit.getPluginManager().callEvent(new RoleGiveEvent());

        // Démarrer le timer pour le temps de jeu
        new BukkitRunnable() {
            @Override
            public void run() {
                if (timePassed == borderTimer) {
                    // Vérifier s'il y a des joueurs dans un rayon de 600 blocs autour de (0, 0)
                    List<Player> playersInRange = new ArrayList<>();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getLocation().distance(new Location(player.getWorld(), 0, 0, 0)) <= 600) {
                            playersInRange.add(player);
                        }
                    }

                    // Si aucun joueur n'est trouvé dans la zone
                    if (playersInRange.isEmpty()) {
                        // Téléporter les joueurs les plus proches de (0, 0)
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (!playersInRange.contains(player)) {
                                player.teleport(new Location(player.getWorld(), 0, 80, 0)); // Téléportation à une position spécifique
                            }
                        }
                    }
                }
                timePassed++;
            }
        }.runTaskTimer(UhcAPI.getInstance(), 0, 20); // Exécute toutes les secondes

        if (borderSize < 1000 || borderSize == 0) {
            Bukkit.getWorld("world").getWorldBorder().setSize(1000);
        } else {
            Bukkit.getWorld("world").getWorldBorder().setSize(borderSize);
        }
        Bukkit.getServer().getPluginManager().callEvent(new UhcStartEvent());
    }
    private void teleportToRandomLocation(Player player) {
        Random random = new Random();
        // Définir la plage de téléportation (par exemple, -100 à 100)
        int range = 100;

        // Générer des coordonnées aléatoires
        int x = random.nextInt(range * 2) - range; // Valeur entre -100 et 100
        int z = random.nextInt(range * 2) - range; // Valeur entre -100 et 100

        // Trouver la hauteur (Y) la plus proche du sol
        int y = player.getWorld().getHighestBlockYAt(x, z);

        // Créer une nouvelle location
        Location randomLocation = new Location(player.getWorld(), x, y, z);

        // Téléporter le joueur
        player.teleport(randomLocation);
    }

    public GamePhase getCurrentPhase() {
        return currentPhase; // Méthode pour récupérer la phase actuelle
    }

    public int getTimePassed() {
        return timePassed;
    }
    
    public void setborderTimer(int n) {
    	borderTimer = n;
    }
    
    public void setborderSize(int n) {
    	borderSize = n;
    }
    
    public void setMettup(boolean b) {
    	ismettup = b;
    }
    
    public boolean getMettup() {
    	return ismettup;
    }
}