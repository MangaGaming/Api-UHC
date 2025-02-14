package com.mguhc.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.mguhc.player.PlayerManager;
import com.mguhc.roles.Camp;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
    private GamePhase currentPhase; // Champ pour la phase actuelle
    private int timePassed;
    private int borderTimer = 3600;
    private int borderSize = 300;
	private boolean ismettup = false;


    public UhcGame() {
        this.currentPhase = new GamePhase("Waiting"); // Initialiser la phase à "Waiting"
    }

    public void startGame() {
        RoleManager roleManager = UhcAPI.getInstance().getRoleManager();
        Map<Player, UhcPlayer> players = UhcAPI.getInstance().getPlayerManager().getPlayers();
        if (roleManager.getActiveRoles().size() != players.size()) {
            Bukkit.broadcastMessage(players.size() + " / " + roleManager.getActiveRoles().size());
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
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setSaturation(20);
            UhcAPI.getInstance().getEffectManager().removeEffects(player);
            // Téléportation à un endroit aléatoire autour de (0, 0)
            teleportToRandomLocation(player);
            if (ismettup) {
                giveMeetupGear(player);
            }
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
                            player.teleport(new Location(player.getWorld(), 0, 80, 0)); // Téléportation à une position spécifique
                        }
                    }
                }
                timePassed++;
            }
        }.runTaskTimer(UhcAPI.getInstance(), 0, 20); // Exécute toutes les secondes

        Bukkit.getWorld("world").getWorldBorder().setSize(borderSize);
        Bukkit.getServer().getPluginManager().callEvent(new UhcStartEvent());
    }

    public void giveMeetupGear(Player player) {
        // Casque en fer avec Protection III
        ItemStack ironHelmet = new ItemStack(Material.IRON_HELMET);
        ItemMeta ironHelmetMeta = ironHelmet.getItemMeta();
        if (ironHelmetMeta != null) {
            ironHelmetMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, true);
            ironHelmet.setItemMeta(ironHelmetMeta);
        }
        player.getInventory().setHelmet(ironHelmet); // Mettre le casque

        // Plastron en diamant avec Protection II
        ItemStack diamondChestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemMeta diamondChestplateMeta = diamondChestplate.getItemMeta();
        if (diamondChestplateMeta != null) {
            diamondChestplateMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);
            diamondChestplate.setItemMeta(diamondChestplateMeta);
        }
        player.getInventory().setChestplate(diamondChestplate); // Mettre le plastron

        // Pantalon en fer avec Protection III
        ItemStack ironLeggings = new ItemStack(Material.IRON_LEGGINGS);
        ItemMeta ironLeggingsMeta = ironLeggings.getItemMeta();
        if (ironLeggingsMeta != null) {
            ironLeggingsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, true);
            ironLeggings.setItemMeta(ironLeggingsMeta);
        }
        player.getInventory().setLeggings(ironLeggings); // Mettre le pantalon

        // Bottes en diamant avec Protection II
        ItemStack diamondBoots = new ItemStack(Material.DIAMOND_BOOTS);
        ItemMeta diamondBootsMeta = diamondBoots.getItemMeta();
        if (diamondBootsMeta != null) {
            diamondBootsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);
            diamondBoots.setItemMeta(diamondBootsMeta);
        }
        player.getInventory().setBoots(diamondBoots); // Mettre les bottes

        // Épée en diamant avec Tranchant III
        ItemStack diamondSword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta diamondSwordMeta = diamondSword.getItemMeta();
        if (diamondSwordMeta != null) {
            diamondSwordMeta.addEnchant(Enchantment.DAMAGE_ALL, 3, true);
            diamondSword.setItemMeta(diamondSwordMeta);
        }
        player.getInventory().setItem(0, diamondSword); // Mettre l'épée en slot 1

        // Arc avec Power III
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = bow.getItemMeta();
        if (bowMeta != null) {
            bowMeta.addEnchant(Enchantment.ARROW_DAMAGE, 3, true);
            bow.setItemMeta(bowMeta);
        }
        player.getInventory().setItem(2, bow); // Mettre l'arc en slot 3

        // Flèches
        ItemStack arrows = new ItemStack(Material.ARROW, 16);
        player.getInventory().setItem(3, arrows); // Mettre les flèches en slot 4

        // Golden Apples
        ItemStack goldenApples = new ItemStack(Material.GOLDEN_APPLE, 5);
        player.getInventory().setItem(1, goldenApples); // Mettre les Golden Apples en slot 2

        // Golden Carrots
        ItemStack goldenCarrots = new ItemStack(Material.GOLDEN_CARROT, 64);
        player.getInventory().setItem(5, goldenCarrots);

        // Seau d'eau
        ItemStack waterBucket = new ItemStack(Material.WATER_BUCKET);
        player.getInventory().setItem(6, waterBucket); // Mettre le seau d'eau en slot 7

        // Seau de lave
        ItemStack lavaBucket = new ItemStack(Material.LAVA_BUCKET);
        player.getInventory().setItem(7, lavaBucket); // Mettre le seau de lave en slot 8

        // Cobblestone
        ItemStack cobblestone = new ItemStack(Material.COBBLESTONE, 64);
        player.getInventory().setItem(8, cobblestone); // Mettre la cobblestone en slot 9

        // Mettre à jour l'inventaire du joueur
        player.updateInventory();
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

    public void finishGame(Camp winner) {
        Bukkit.broadcastMessage("Le camp " + winner.getName() + " a gagné !");
        Bukkit.reload();
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer("§cVeuillez vous reconnecter");
        }
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