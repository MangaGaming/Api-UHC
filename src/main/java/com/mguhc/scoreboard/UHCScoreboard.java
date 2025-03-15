package com.mguhc.scoreboard;

import com.mguhc.game.UhcGame;
import com.mguhc.player.PlayerManager;
import com.mguhc.player.UhcPlayer;
import com.mguhc.roles.Camp;
import com.mguhc.roles.RoleManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import com.mguhc.UhcAPI;
import com.mguhc.roles.UhcRole;

import java.util.Map;

public class UHCScoreboard {
    private final UhcAPI uhcAPI = UhcAPI.getInstance();
    private final UhcGame uhcGame = uhcAPI.getUhcGame();
    private final PlayerManager playerManager = uhcAPI.getPlayerManager();
    private final RoleManager roleManager = uhcAPI.getRoleManager();
    private final FileConfiguration config = uhcAPI.getConfig();

    private Score playerSize;
    private Score kill;
    private Score time;
    private Score role;
    private Score cycle;

    public void createScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();

        // Créer l'objectif du scoreboard avec le titre UHC
        Objective objective = scoreboard.registerNewObjective("uhc", "dummy");
        objective.setDisplayName("§4§l" + uhcAPI.getUhcName());
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Initialisation des scores
        Score vide = objective.getScore("§f ");
        vide.setScore(9);

        playerSize = objective.getScore("§f┃ §aJoueurs §f▸ §a" + getPlayers());
        playerSize.setScore(8);

        kill = objective.getScore("§f┃ §cEliminations §f▸ §c" + getKill(player));
        kill.setScore(7);

        role = objective.getScore("§f┃ §8Rôle §f▸ " + getRole(player));
        role.setScore(6);

        Score vide1 = objective.getScore("§f§f ");
        vide1.setScore(5);

        time = objective.getScore("§f┃ §8Temps §f▸ §8" + getTime());
        time.setScore(4);

        cycle = objective.getScore("§f┃ §8Cycle §f▸ " + getCycle());
        cycle.setScore(3);

        Score vide3 = objective.getScore("§f§f§f§f ");
        vide3.setScore(2);

        Score pub = objective.getScore("§f§l@MangaGaming @AxisRonflex §f");
        pub.setScore(1);

        player.setScoreboard(scoreboard);

        // Créer une tâche répétitive pour mettre à jour les scores
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    return; // Annuler la tâche si le joueur n'est plus en ligne
                }

                scoreboard.resetScores(playerSize.getEntry());
                scoreboard.resetScores(kill.getEntry());
                scoreboard.resetScores(time.getEntry());
                scoreboard.resetScores(role.getEntry());
                scoreboard.resetScores(cycle.getEntry());

                playerSize = objective.getScore("§f┃ §aJoueurs §f▸ §a" + getPlayers());
                playerSize.setScore(8);
                kill = objective.getScore("§f┃ §cEliminations §f▸ §c" + getKill(player));
                kill.setScore(7);
                role = objective.getScore("§f┃ §8Rôle §f▸ " + getRole(player));
                role.setScore(6);
                time = objective.getScore("§f┃ §8Temps §f▸ §8" + getTime());
                time.setScore(4);
                cycle = objective.getScore("§f┃ §8Cycle §f▸ " + getCycle());
                cycle.setScore(3);

                // Mettre à jour le scoreboard pour le joueur
                player.setScoreboard(scoreboard);
            }
        }.runTaskTimer(uhcAPI, 0, 20);
    }

    private void updateColors(Player player, Scoreboard scoreboard) {
        Map<Player, ChatColor> colorMap = uhcAPI.getColorMap().get(player);
        if (colorMap != null) {
            for (Map.Entry<Player, ChatColor> entry : colorMap.entrySet()) {
                Player p = entry.getKey();
                ChatColor color = entry.getValue();
                switch (color) {
                    case GREEN:
                        Team green = scoreboard.getTeam("GreenColor");
                        if (green == null) {
                            scoreboard.registerNewTeam("GreenColor");
                        }
                        if (green != null) {
                            green.setPrefix("§a");
                            green.addEntry(p.getName());
                        }
                        break;
                    case RED:
                        Team red = scoreboard.getTeam("RedColor");
                        if (red == null) {
                            scoreboard.registerNewTeam("RedColor");
                        }
                        if (red != null) {
                            red.setPrefix("§c");
                            red.addEntry(p.getName());
                        }
                        break;
                    case YELLOW:
                        Team yellow = scoreboard.getTeam("YellowColor");
                        if (yellow == null) {
                            scoreboard.registerNewTeam("YellowColor");
                        }
                        if (yellow != null) {
                            yellow.setPrefix("§e");
                            yellow.addEntry(p.getName());
                        }
                        break;
                    case WHITE:
                        Team white = scoreboard.getTeam("WhiteColor");
                        if (white == null) {
                            scoreboard.registerNewTeam("WhiteColor");
                        }
                        if (white != null) {
                            white.setPrefix("§f");
                            white.addEntry(p.getName());
                        }
                        break;
                }
            }
        }
    }

    private String getPlayers() {
        int onlinePlayers = playerManager.getPlayers().size();
        return String.valueOf(onlinePlayers);
    }

    private String getCycle() {
        World world = Bukkit.getWorld("world");
        long time = world.getTime();

        if (time >= 0 && time < 12300) {
            return "§eJour";
        } else {
            return "§1Nuit";
        }
    }

    private String getKill(Player player) {
        int kill = playerManager.getKill(player);
        return String.valueOf(kill);
    }
    
    private String getTime() {
        int time = uhcGame.getTimePassed();

        int minutes = time / 60;
        int seconds = time % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private String getRole(Player player) {
        UhcPlayer uhcPlayer = playerManager.getPlayer(player);
        UhcRole role = roleManager.getRole(uhcPlayer);
        if(role != null) {
            Camp camp = roleManager.getCamp(uhcPlayer);
            String color = "§f";
            if (camp != null) {
                String string = config.getString("camp." + camp.getName().toLowerCase());
                if (string != null) {
                    color = "§" + string;
                }
            }
            return color + role.getName();
        }
        else {
            return "Aucun";
        }
    }
}
