package com.mguhc.listeners;

import com.mguhc.UhcAPI;
import com.mguhc.events.UhcDeathEvent;
import com.mguhc.game.UhcGame;
import com.mguhc.player.PlayerManager;
import com.mguhc.player.UhcPlayer;
import com.mguhc.roles.Camp;
import com.mguhc.roles.RoleManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KillListener implements Listener {

    private final Map<Player, List<ItemStack>> dropsMap = new HashMap<>();
    private final Map<Player, Location> locationMap = new HashMap<>();
    private final Map<Player, Player> killerMap = new HashMap<>();

    @EventHandler
    private void OnDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        if (UhcAPI.getInstance().getUhcGame().getCurrentPhase().getName().equals("Playing")) {
            Player player = event.getEntity().getPlayer();
            Player killer = event.getEntity().getKiller();
            if (killer != null) {
                killerMap.put(player, killer);
            }
            locationMap.put(player, player.getLocation());
            List<ItemStack> drops = new ArrayList<>(event.getDrops()); // Créer une nouvelle liste à partir des drops
            dropsMap.put(player, drops);

            // Supprimer les drops de l'événement
            event.getDrops().clear(); // Effacer les drops de l'événement
        }
    }
    
    @EventHandler
    private void OnRespawn(PlayerRespawnEvent event) {
        if (UhcAPI.getInstance().getUhcGame().getCurrentPhase().getName().equals("Playing")) {
            Player player = event.getPlayer();
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.getLocation().getY() > 150) {
                        Player killer = killerMap.getOrDefault(player, null);
                        player.setGameMode(GameMode.SPECTATOR);
                        if (killer != null) {
                            UhcAPI.getInstance().getPlayerManager().addKill(player);
                            player.teleport(killer);
                        }
                        List<ItemStack> drops = dropsMap.get(player);
                        Location location = locationMap.get(player);
                        Bukkit.getPluginManager().callEvent(new UhcDeathEvent(player, killer, drops, location));
                        for (ItemStack item : drops) {
                            if (item != null) {
                                killer.getWorld().dropItem(location, item);
                            }
                        }
                    }
                }
            }.runTaskLater(UhcAPI.getInstance(), 5*20);
        }
    }

    @EventHandler
    private void OnUhcDeath(UhcDeathEvent event) {
        Player player = event.getPlayer();
        RoleManager roleManager = UhcAPI.getInstance().getRoleManager();
        PlayerManager playerManager = UhcAPI.getInstance().getPlayerManager();
        Bukkit.broadcastMessage("§7__________\n" +
                "§6" + player.getName() + "est mort il était : \n" +
                "§c" + roleManager.getRole(playerManager.getPlayer(player)).getName() + "\n" +
                "§7__________");
    }

    @EventHandler
    private void OnWin(UhcDeathEvent event) {
        Player player = event.getPlayer();
        UhcGame uhcgame = UhcAPI.getInstance().getUhcGame();
        PlayerManager playerManager = UhcAPI.getInstance().getPlayerManager();
        RoleManager roleManager = UhcAPI.getInstance().getRoleManager();
        if (uhcgame.getCurrentPhase().getName().equals("Playing")) {
            Map<Player, UhcPlayer> players = playerManager.getPlayers();
            players.remove(player); // Retirer le joueur décédé

            // Vérifier si tous les joueurs restants sont dans le même camp
            if (!players.isEmpty()) { // S'assurer qu'il reste des joueurs
                Camp firstCamp = roleManager.getCamp(players.values().iterator().next()); // Obtenir le camp du premier joueur
                boolean allSameCamp = true;

                for (UhcPlayer uhcPlayer : players.values()) {
                    Camp currentCamp = roleManager.getCamp(uhcPlayer);
                    if (!currentCamp.equals(firstCamp)) {
                        allSameCamp = false; // Si un joueur n'est pas dans le même camp, mettre à jour le drapeau
                        break;
                    }
                }

                // Si tous les joueurs sont dans le même camp, finir le jeu
                if (allSameCamp) {
                    uhcgame.finishGame(firstCamp);
                }
            }
        }
    }
}
