package com.mguhc.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.mguhc.UhcAPI;
import com.mguhc.roles.UhcRole;

public class UHCScoreboard {

    public void createScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();

        // Créer l'objectif du scoreboard avec le titre Mashle UHC
        Objective objective = scoreboard.registerNewObjective("uhc", "dummy");
        objective.setDisplayName(ChatColor.GOLD + UhcAPI.getInstance().getUhcName());
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Espace vide pour l'esthétique
        Score line1 = objective.getScore(ChatColor.DARK_GRAY + "__________________");
        line1.setScore(8);

        // Ligne 1 : Border
        Score borderLabel = objective.getScore(ChatColor.YELLOW + "Border :");
        borderLabel.setScore(7);
        final Score[] borderValue = { objective.getScore(getBorderSizeString()) };
        borderValue[0].setScore(6);

        // Espace vide pour l'esthétique
        Score line2 = objective.getScore(ChatColor.DARK_GRAY + "_________________");
        line2.setScore(5);

        // Ligne 2 : Joueurs
        Score playersLabel = objective.getScore(ChatColor.GREEN + "Joueurs :");
        playersLabel.setScore(4);
        final Score[] playersValue = { objective.getScore(getOnlinePlayersString()) };
        playersValue[0].setScore(3);

        // Espace vide pour l'esthétique
        Score line3 = objective.getScore(ChatColor.DARK_GRAY + "________________");
        line3.setScore(2);

        // Ligne 3 : Temps écoulé
        final Score[] timeValue = { objective.getScore(ChatColor.RED + "Temps: " + formatTime(UhcAPI.getInstance().getUhcGame().getTimePassed())) };
        timeValue[0].setScore(0);


        // Ligne 4 : Rôle du joueur
        Score roleLabel = objective.getScore(ChatColor.AQUA + "Role :");
        roleLabel.setScore(-1);
        final Score[] roleValue = { objective.getScore(getPlayerRole(player)) };
        roleValue[0].setScore(-2);

        // Appliquer le scoreboard initial au joueur
        player.setScoreboard(scoreboard);

        // Créer une tâche répétitive pour mettre à jour les scores
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel(); // Annuler la tâche si le joueur se déconnecte
                    return;
                }

                // Mettre à jour la taille de la border
                scoreboard.resetScores(borderValue[0].getEntry());
                borderValue[0] = objective.getScore(getBorderSizeString());
                borderValue[0].setScore(6);

                // Mettre à jour le nombre de joueurs en ligne
                scoreboard.resetScores(playersValue[0].getEntry());
                playersValue[0] = objective.getScore(getOnlinePlayersString());
                playersValue[0].setScore(3);

                // Mettre à jour le temps passé
                scoreboard.resetScores(timeValue[0].getEntry());
                timeValue[0] = objective.getScore(ChatColor.RED + "Temps: " + formatTime(UhcAPI.getInstance().getUhcGame().getTimePassed()));
                timeValue[0].setScore(0);

                // Mettre à jour le rôle du joueur
                scoreboard.resetScores(roleValue[0].getEntry());
                roleValue[0] = objective.getScore(getPlayerRole(player));
                roleValue[0].setScore(-2);

                // Rafraîchir le scoreboard du joueur
                player.setScoreboard(scoreboard);
            }
        }.runTaskTimer(UhcAPI.getInstance(), 0, 20); // Mettre à jour toutes les 5 secondes (20 ticks par seconde)
    }

    // Méthode pour obtenir la taille de la border en tant que chaîne
    private String getBorderSizeString() {
        double borderSize = Bukkit.getWorld("world").getWorldBorder().getSize();
        return ChatColor.YELLOW + String.valueOf((int) borderSize);
    }

    // Méthode pour obtenir le nombre de joueurs en ligne
    private String getOnlinePlayersString() {
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        return ChatColor.GREEN + String.valueOf(onlinePlayers);
    }

    // Méthode pour obtenir le rôle du joueur
    private String getPlayerRole(Player player) {
        UhcRole role = UhcAPI.getInstance().getRoleManager().getRole(UhcAPI.getInstance().getPlayerManager().getPlayer(player));
        if(role != null) {
            return ChatColor.AQUA + role.getName();
        }
        else {
        	return ChatColor.AQUA + "Aucun";
        }
    }
    
    private String formatTime(int time) {
        int minutes = time / 60;
        int seconds = time % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
