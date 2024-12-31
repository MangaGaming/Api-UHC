package com.mguhc.listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import com.mguhc.UhcAPI;
import com.mguhc.game.GamePhase;
import com.mguhc.game.UhcGame;
import com.mguhc.player.PlayerManager;
import com.mguhc.roles.RoleManager;
import com.mguhc.roles.UhcRole;
import com.mguhc.scenario.Scenario;
import com.mguhc.scenario.ScenarioManager;
import com.mguhc.scoreboard.UHCScoreboard;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;

public class PlayerListener implements Listener {
    
    private UhcGame uhcgame;
    private LuckPerms luckPerms;
    private Map<Player, String> playerInputState = new HashMap<>();
    
    public PlayerListener(UhcGame uhcgame) {
        this.uhcgame = uhcgame;
        this.luckPerms = LuckPermsProvider.get();
    }

    @EventHandler
    private void OnJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World world = Bukkit.getWorld("world");
        UHCScoreboard uhcscoreboard = new UHCScoreboard();
        uhcscoreboard.createScoreboard(player);
        GamePhase currentphase = uhcgame.getCurrentPhase();
        RoleManager roleManager = UhcAPI.getInstance().getRoleManager();
        PlayerManager playerManager = UhcAPI.getInstance().getPlayerManager();
        
        if (currentphase.getName().equals("Waiting")) {
            player.setMaxHealth(20);
            player.setHealth(20);
            player.setSaturation(20);
            player.getInventory().clear();
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
            UhcAPI.getInstance().getEffectManager().removeEffects(player);
            player.setGameMode(GameMode.ADVENTURE);
            player.teleport(new Location(world, 0, 201, 0));
            if(roleManager.getRole(playerManager.getPlayer(player)) != null) {
                roleManager.removeRole(playerManager.getPlayer(player));
            }
            playerManager.addPlayer(player);
            
            // Vérifier si le joueur a la permission ou est op
            if (player.hasPermission("api.host") || player.isOp() && currentphase.getName().equals("Waiting")) {
                // Créer une étoile du Nether nommée "config"
                ItemStack netherStar = new ItemStack(Material.NETHER_STAR);
                ItemMeta meta = netherStar.getItemMeta();
                meta.setDisplayName(ChatColor.RED + "Config");
                netherStar.setItemMeta(meta);
                player.getInventory().addItem(netherStar); // Donner l'étoile du Nether au joueur
                player.updateInventory();
            }
            
            if(player.hasPermission("api.mod")) {
            	if(!player.isOp()) {
                	giveModItems(player);
            	}
            }
        }
    }
    
    @EventHandler
    private void OnLeave(PlayerQuitEvent event) {
    	Player player = event.getPlayer();
    	if(UhcAPI.getInstance().getUhcGame().getCurrentPhase().getName().equals("Waiting")) {
    		UhcAPI.getInstance().getPlayerManager().getPlayers().remove(UhcAPI.getInstance().getPlayerManager().getPlayer(player), player);
    	}
    }
    
    @EventHandler
    private void OnRespawn(PlayerRespawnEvent event) {
    	if(UhcAPI.getInstance().getUhcGame().getCurrentPhase().getName().equals("Playing")) {
    		event.getPlayer().setGameMode(GameMode.SPECTATOR);
    	}
    }
    
    @EventHandler
    private void OnInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        // Vérifier si l'item est présent et a un nom d'affichage
        if (item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            // Vérifier si le nom d'affichage est "Config"
            if (item.getItemMeta().getDisplayName().equals(ChatColor.RED + "Config")) {
                // Ouvrir l'inventaire de configuration
                openConfigInventory(player);
            }
        }
    }

    private void openConfigInventory(Player player) {
        // Créer un inventaire de configuration de 36 slots
        Inventory configInventory = Bukkit.createInventory(null, 36, ChatColor.GREEN + "Configuration");
        
        // Créer l'item pour mettre en mode meetup
        ItemStack meetupItem = new ItemStack(Material.DIAMOND_SWORD, 1);
        ItemMeta meetupMeta = meetupItem.getItemMeta();
        if(meetupMeta != null) {
            meetupMeta.setDisplayName(ChatColor.GREEN + "Mode Mettup");
            meetupItem.setItemMeta(meetupMeta);
        }

        // Créer l'item pour lancer la partie
        ItemStack startGameItem = new ItemStack(Material.WOOL, 1, (short) 5); // 5 correspond à la couleur verte
        ItemMeta startGameMeta = startGameItem.getItemMeta();
        if (startGameMeta != null) {
            startGameMeta.setDisplayName(ChatColor.GREEN + "Lancer la Partie");
            startGameItem.setItemMeta(startGameMeta);
        }

        // Créer l'item pour le mode de jeu
        ItemStack gameModeItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta gameModeMeta = gameModeItem.getItemMeta();
        if (gameModeMeta != null) {
            gameModeMeta.setDisplayName(ChatColor.RED + "Mode de jeu");
            gameModeItem.setItemMeta(gameModeMeta);
        }

        // Créer l'item pour la bordure
        ItemStack borderItem = new ItemStack(Material.BEACON);
        ItemMeta borderMeta = borderItem.getItemMeta();
        if (borderMeta != null) {
            borderMeta.setDisplayName(ChatColor.BLUE + "Bordure");
            borderItem.setItemMeta(borderMeta);
        }

        // Créer l'item pour ouvrir le menu des scénarios
        ItemStack scenarioItem = new ItemStack(Material.BOOK);
        ItemMeta scenarioMeta = scenarioItem.getItemMeta();
        if (scenarioMeta != null) {
            scenarioMeta.setDisplayName(ChatColor.GOLD + "Scénarios");
            scenarioMeta.setLore(Collections.singletonList(ChatColor.GRAY + "Cliquez pour gérer les scénarios"));
            scenarioItem.setItemMeta(scenarioMeta);
        }
        
        // Créer l'item pour les hosts
        ItemStack hostItem = new ItemStack(Material.FEATHER);
        ItemMeta hostMeta = hostItem.getItemMeta();
        if (hostMeta != null) {
        	hostMeta.setDisplayName(ChatColor.GOLD + "Host");
        	hostItem.setItemMeta(hostMeta);
        }
        
        // Créer l'item pour les mods
        ItemStack modItem = new ItemStack(Material.ANVIL);
        ItemMeta modMeta = modItem.getItemMeta();
        if(modMeta != null) {
        	modMeta.setDisplayName(ChatColor.BLUE + "Mod");
        	modItem.setItemMeta(modMeta);
        }

        // Ajouter les items à l'inventaire
        configInventory.setItem(3, meetupItem);
        configInventory.setItem(4, startGameItem);
        configInventory.setItem(13, borderItem);
        configInventory.setItem(22, scenarioItem);
        configInventory.setItem(31, gameModeItem);
        
        // Host & Mod
        configInventory.setItem(30, hostItem);
        configInventory.setItem(32, modItem);
        

        // Ouvrir l'inventaire pour le joueur
        player.openInventory(configInventory);
    }

    @EventHandler
    private void onConfigInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GREEN + "Configuration")) {
            event.setCancelled(true); // Annuler l'événement pour éviter de déplacer les items

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            
            if(clickedItem != null && clickedItem.getType() == Material.DIAMOND_SWORD && 
               clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Mode Mettup")) {
            	if (uhcgame.getMettup()) {
            		uhcgame.setMettup(false);
            		player.sendMessage("Meetup Désactivé");
            	}
            	else if (!uhcgame.getMettup()) {
            		uhcgame.setMettup(true);
            		player.sendMessage("Meetup Activé");
            	}
            }

            // Vérifier si l'item cliqué est celui pour lancer la partie
            if (clickedItem != null && clickedItem.getType() == Material.WOOL && 
                clickedItem.getDurability() == 5 && // Vérifier que la couleur est verte
                clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Lancer la Partie")) {
            	player.sendMessage(ChatColor.GREEN + "La partie est lancé");
            	uhcgame.startGame();
                player.closeInventory(); // Fermer l'inventaire après avoir lancé la partie
            }
            if (clickedItem != null && clickedItem.getType() == Material.NETHER_STAR && 
                clickedItem.getItemMeta().getDisplayName().equals(ChatColor.RED + "Mode de jeu")) {
            	openGameModeInventory(player);
            }
            if (clickedItem != null && clickedItem.getType() == Material.BEACON && 
                clickedItem.getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Bordure")) {
                openBorderInventory(player);
            }
            if (clickedItem != null && clickedItem.getType() == Material.BOOK && 
                    clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Scénarios")) {
                    openScenarioInventory(player);
            }
            
            if (clickedItem != null && clickedItem.getType() == Material.FEATHER &&
            		clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Host")){
                openHostInventory(player);
            }
            
            if(clickedItem != null && clickedItem.getType() == Material.ANVIL &&
            		clickedItem.getItemMeta().getDisplayName().equals(ChatColor.BLUE + "Mod")) {
            	openModInventory(player);
            }
        }
    }
    
    private void openModInventory(Player player) {
        // Créer un inventaire de 54 slots
        Inventory modInventory = Bukkit.createInventory(null, 54, ChatColor.BLUE + "Sélectionner les Modérateurs");

        // Récupérer tous les joueurs en ligne
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            // Créer un item pour chaque joueur
            ItemStack playerItem = new ItemStack(Material.SKULL_ITEM, 1, (short) 3); // Utiliser une tête de joueur
            ItemMeta meta = playerItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(onlinePlayer.getName()); // Nom du joueur
                List<String> lore = new ArrayList<>();
                // Vérifier si le joueur a déjà la permission
                if (onlinePlayer.hasPermission("api.mod")) {
                    lore.add(ChatColor.RED + "Déjà Mod");
                } else {
                    lore.add(ChatColor.GREEN + "Cliquez pour lui donner les permissions de modérateurs");
                }
                meta.setLore(lore);
                playerItem.setItemMeta(meta);
            }
            // Ajouter l'item à l'inventaire
            modInventory.addItem(playerItem);
        }
        // Ouvrir l'inventaire pour le joueur
        player.openInventory(modInventory);
	}

	private void openHostInventory(Player player) {
        // Créer un inventaire de 54 slots
        Inventory hostInventory = Bukkit.createInventory(null, 54, ChatColor.GREEN + "Sélectionner un Host");

        // Récupérer tous les joueurs en ligne
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            // Créer un item pour chaque joueur
            ItemStack playerItem = new ItemStack(Material.SKULL_ITEM, 1, (short) 3); // Utiliser une tête de joueur
            ItemMeta meta = playerItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(onlinePlayer.getName()); // Nom du joueur
                List<String> lore = new ArrayList<>();
                // Vérifier si le joueur a déjà la permission
                if (onlinePlayer.hasPermission("api.host")) {
                    lore.add(ChatColor.RED + "Déjà Host");
                } else {
                    lore.add(ChatColor.GREEN + "Cliquez pour donner le statut de Host");
                }
                meta.setLore(lore);
                playerItem.setItemMeta(meta);
            }
            // Ajouter l'item à l'inventaire
            hostInventory.addItem(playerItem);
        }

        // Ouvrir l'inventaire pour le joueur
        player.openInventory(hostInventory);
    }

	private void openScenarioInventory(Player player) {
        Inventory scenarioInventory = Bukkit.createInventory(null, 27, ChatColor.GREEN + "Gérer les Scénarios");

        // Ajouter chaque scénario à l'inventaire
        for (Scenario scenario : UhcAPI.getInstance().getScenarioManager().getScenarios()) {
            ItemStack scenarioItem = new ItemStack(Material.PAPER);
            ItemMeta meta = scenarioItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(scenario.getName());
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Cliquez pour " + (UhcAPI.getInstance().getScenarioManager().isScenarioActive(scenario.getName()) ? "désactiver" : "activer"));
                scenarioItem.setItemMeta(meta);
            }
            scenarioInventory.addItem(scenarioItem);
        }

        // Ouvrir l'inventaire pour le joueur
        player.openInventory(scenarioInventory);
    }

    private void openBorderInventory(Player player) {
        // Créer un inventaire de configuration de 27 slots
        Inventory borderInventory = Bukkit.createInventory(null, 27, ChatColor.GREEN + "Configurer la Bordure");

        // Créer un item pour définir la taille de la bordure
        ItemStack borderSizeItem = new ItemStack(Material.DIAMOND);
        ItemMeta borderSizeMeta = borderSizeItem.getItemMeta();
        if (borderSizeMeta != null) {
            borderSizeMeta.setDisplayName(ChatColor.YELLOW + "Taille de la Bordure");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Cliquez pour définir la taille de la bordure.");
            borderSizeMeta.setLore(lore);
            borderSizeItem.setItemMeta(borderSizeMeta);
        }
        borderInventory.setItem(11, borderSizeItem); // Position 11

        // Créer un item pour définir le timer de la bordure
        ItemStack borderTimerItem = new ItemStack(Material.COMPASS);
        ItemMeta borderTimerMeta = borderTimerItem.getItemMeta();
        if (borderTimerMeta != null) {
            borderTimerMeta.setDisplayName(ChatColor.YELLOW + "Timer de la Bordure");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Cliquez pour définir le timer de la bordure.");
            borderTimerMeta.setLore(lore);
            borderTimerItem.setItemMeta(borderTimerMeta);
        }
        borderInventory.setItem(15, borderTimerItem); // Position 15

        // Ouvrir l'inventaire pour le joueur
        player.openInventory(borderInventory);
    }

	private void openGameModeInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.GREEN + "Configurer les Rôles");

        for (UhcRole role : UhcAPI.getInstance().getRoleManager().getValidRoles()) {
            ItemStack roleItem = new ItemStack(Material.PAPER); // Utilisez un item approprié
            ItemMeta meta = roleItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(role.getName()); // Assurez-vous que UhcRole a une méthode getName()
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Cliquez pour " + (UhcAPI.getInstance().getRoleManager().getActiveRoles().contains(role) ? "désactiver" : "activer"));
                meta.setLore(lore);
                roleItem.setItemMeta(meta);
            }
            inventory.addItem(roleItem);
        }
        player.openInventory(inventory);
	}
	
	@EventHandler
    private void onHostInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GREEN + "Sélectionner un Host")) {
            event.setCancelled(true); // Annuler l'événement pour éviter de déplacer les items

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            // Vérifier si l'item cliqué est un item de joueur
            if (clickedItem != null && clickedItem.getType() == Material.SKULL_ITEM) {
                String playerName = clickedItem.getItemMeta().getDisplayName();
                Player selectedPlayer = Bukkit.getPlayer(playerName);

                if (selectedPlayer != null) {
                	User user = luckPerms.getUserManager().getUser (selectedPlayer.getUniqueId());
                    if (selectedPlayer.hasPermission("api.host")) {
                    	// Retirer la permission api.mod
                        user.data().remove(Node.builder("api.host").build());
                        luckPerms.getUserManager().saveUser (user); // Sauvegarder l'utilisateur

                        player.sendMessage(ChatColor.RED + selectedPlayer.getName() + " n'est plus Host.");
                        selectedPlayer.sendMessage(ChatColor.RED + "Vous avez été retiré du statut de Host.");
                        
                    } else {
                        if (user != null) {
                            // Ajouter la permission api.host
                            user.data().add(Node.builder("api.host").build());
                            luckPerms.getUserManager().saveUser (user); // Sauvegarder l'utilisateur

                            player.sendMessage(ChatColor.GREEN + selectedPlayer.getName() + " a maintenant le statut de Host.");
                            selectedPlayer.sendMessage(ChatColor.GREEN + "Vous avez été promu au statut de Host.");
                            
                            ItemStack netherStar = new ItemStack(Material.NETHER_STAR);
                            ItemMeta meta = netherStar.getItemMeta();
                            meta.setDisplayName(ChatColor.RED + "Config");
                            netherStar.setItemMeta(meta);
                            selectedPlayer.getInventory().addItem(netherStar); // Donner l'étoile du Nether au joueur
                            selectedPlayer.updateInventory();
                        } else {
                            player.sendMessage(ChatColor.RED + "Erreur : Impossible de récupérer les données de permission pour " + selectedPlayer.getName());
                        }
                    }
                }
            }
        }
    }
	
	@EventHandler
	private void onModInventoryClick(InventoryClickEvent event) {
		PlayerManager playerManager = UhcAPI.getInstance().getPlayerManager();
	    if (event.getView().getTitle().equals(ChatColor.BLUE + "Sélectionner les Modérateurs")) {
	        event.setCancelled(true); // Annuler l'événement pour éviter de déplacer les items

	        Player player = (Player) event.getWhoClicked();
	        ItemStack clickedItem = event.getCurrentItem();

	        // Vérifier si l'item cliqué est un item de joueur
	        if (clickedItem != null && clickedItem.getType() == Material.SKULL_ITEM) {
	            String playerName = clickedItem.getItemMeta().getDisplayName();
	            Player selectedPlayer = Bukkit.getPlayer(playerName);

	            if (selectedPlayer != null) {
	                User user = luckPerms.getUserManager().getUser (selectedPlayer.getUniqueId());

	                if (user != null) {
	                    // Vérifier si le joueur a déjà la permission
	                    if (selectedPlayer.hasPermission("api.mod")) {
	                        // Retirer la permission api.mod
	                        user.data().remove(Node.builder("api.mod").build());
	                        luckPerms.getUserManager().saveUser (user); // Sauvegarder l'utilisateur
	                        playerManager.getPlayers().remove(selectedPlayer, playerManager.getPlayer(selectedPlayer));
	                        player.sendMessage(ChatColor.RED + selectedPlayer.getName() + " n'est plus Mod.");
	                        selectedPlayer.sendMessage(ChatColor.RED + "Vous avez été retiré du statut de Mod.");
	                    } else {
	                        // Ajouter la permission api.mod
	                        user.data().add(Node.builder("api.mod").build());
	                        luckPerms.getUserManager().saveUser (user); // Sauvegarder l'utilisateur
	                        playerManager.getPlayers().put(selectedPlayer, playerManager.getPlayer(selectedPlayer));
	                        player.sendMessage(ChatColor.GREEN + selectedPlayer.getName() + " a maintenant le statut de Mod.");
	                        selectedPlayer.sendMessage(ChatColor.GREEN + "Vous avez été promu au statut de Mod.");
	                        giveModItems(selectedPlayer);
	                    }
	                } else {
	                    player.sendMessage(ChatColor.RED + "Erreur : Impossible de récupérer les données de permission pour " + selectedPlayer.getName());
	                }
	            }
	        }
	    }
	}

	private void giveModItems(Player player) {
	    // Créer une Nether Star nommée "Vanish"
	    ItemStack vanishItem = new ItemStack(Material.NETHER_STAR);
	    ItemMeta vanishMeta = vanishItem.getItemMeta();
	    if (vanishMeta != null) {
	        vanishMeta.setDisplayName(ChatColor.GREEN + "Vanish");
	        vanishItem.setItemMeta(vanishMeta);
	    }

	    // Créer une boussole nommée "Tp"
	    ItemStack tpItem = new ItemStack(Material.COMPASS);
	    ItemMeta tpMeta = tpItem.getItemMeta();
	    if (tpMeta != null) {
	        tpMeta.setDisplayName(ChatColor.GOLD + "Tp");
	        tpItem.setItemMeta(tpMeta);
	    }

	    // Créer une épée nommée "Warn"
	    ItemStack warnItem = new ItemStack(Material.DIAMOND_SWORD);
	    ItemMeta warnMeta = warnItem.getItemMeta();
	    if (warnMeta != null) {
	        warnMeta.setDisplayName(ChatColor.RED + "Warn");
	        warnItem.setItemMeta(warnMeta);
	    }

	    // Donner les items au joueur
	    player.getInventory().addItem(vanishItem, tpItem, warnItem);
	    player.updateInventory(); // Mettre à jour l'inventaire du joueur
	    player.setGameMode(GameMode.CREATIVE);
	}

	@EventHandler
    private void onGameModeInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GREEN + "Configurer les Rôles")) {
            event.setCancelled(true); // Annuler l'événement pour éviter de déplacer les items

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            List<UhcRole> activeRoles = UhcAPI.getInstance().getRoleManager().getActiveRoles();

            if (clickedItem != null && clickedItem.getType() == Material.PAPER) {
                String roleName = clickedItem.getItemMeta().getDisplayName();
                UhcRole clickedRole = findRoleByName(roleName); // Méthode pour trouver le rôle par son nom

                if (clickedRole != null) {
                    if (activeRoles.contains(clickedRole)) {
                        activeRoles.remove(clickedRole);
                        player.sendMessage(ChatColor.RED + "Rôle " + roleName + " désactivé.");
                    } else {
                        activeRoles.add(clickedRole);
                        player.sendMessage(ChatColor.GREEN + "Rôle " + roleName + " activé.");
                    }

                    // Mettre à jour l'item dans l'inventaire
                    updateRoleItem(clickedItem, clickedRole);
                }
            }
        }
    }
    
    @EventHandler
    private void OnScenariInventoryoClick(InventoryClickEvent event) {
    	if (event.getView().getTitle().equals(ChatColor.GREEN + "Gérer les Scénarios")) {
            event.setCancelled(true); // Annuler l'événement pour éviter de déplacer les items
            
            ScenarioManager scenarioManager = UhcAPI.getInstance().getScenarioManager();
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            // Gérer les clics sur les items des scénarios
            if (clickedItem != null && clickedItem.getType() == Material.PAPER) {
                String scenarioName = clickedItem.getItemMeta().getDisplayName();
                if (scenarioManager.isScenarioActive(scenarioName)) {
                	scenarioManager.deactivateScenario(scenarioName);
                    player.sendMessage(ChatColor.RED + scenarioName + " désactivé !");
                } else {
                	scenarioManager.activateScenario(scenarioName);
                    player.sendMessage(ChatColor.GREEN + scenarioName + " activé !");
                }
                openScenarioInventory(player); // Réouvrir le menu des scénarios pour mettre à jour l'état
            }
        }
    }
    
    @EventHandler
    private void onBorderInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GREEN + "Configurer la Bordure")) {
            event.setCancelled(true); // Annuler l'événement pour éviter de déplacer les items

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            // Vérifier si l'item cliqué est celui pour définir la taille de la bordure
            if (clickedItem != null && clickedItem.getType() == Material.DIAMOND && 
                clickedItem.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Taille de la Bordure")) {
                player.sendMessage(ChatColor.GREEN + "Veuillez entrer la taille de la bordure dans le chat (en blocs) :");
            }

            // Vérifier si l'item cliqué est celui pour définir le timer de la bordure
            if (clickedItem != null && clickedItem.getType() == Material.COMPASS && 
                clickedItem.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Timer de la Bordure")) {
                player.sendMessage(ChatColor.GREEN + "Veuillez entrer le timer de la bordure en secondes :");
            }
        }
    }
    
    @EventHandler
    private void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // Vérifier si le joueur est en train de définir la taille de la bordure
        if (playerInputState.containsKey(player) && playerInputState.get(player).equals("borderSize")) {
            try {
                int borderSize = Integer.parseInt(message);
                uhcgame.setborderSize(borderSize); // Mettre à jour la taille de la bordure
                player.sendMessage(ChatColor.GREEN + "Taille de la bordure définie à " + borderSize + " blocs.");
                playerInputState.remove(player); // Réinitialiser l'état
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Veuillez entrer un nombre valide pour la taille de la bordure.");
            }
            event.setCancelled(true); // Annuler l'événement pour éviter d'afficher le message dans le chat
        }

        // Vérifier si le joueur est en train de définir le timer de la bordure
        if (playerInputState.containsKey(player) && playerInputState.get(player).equals("borderTimer")) {
            try {
                int borderTimer = Integer.parseInt(message);
                uhcgame.setborderTimer(borderTimer); // Mettre à jour le timer de la bordure
                player.sendMessage(ChatColor.GREEN + "Timer de la bordure défini à " + borderTimer + " secondes.");
                playerInputState.remove(player); // Réinitialiser l'état
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Veuillez entrer un nombre valide pour le timer de la bordure.");
            }
            event.setCancelled(true); // Annuler l'événement pour éviter d'afficher le message dans le chat
        }
    }

    private UhcRole findRoleByName(String name) {
    	List<UhcRole> validRoles = UhcAPI.getInstance().getRoleManager().getValidRoles();
        for (UhcRole role : validRoles) {
            if (role.getName().equals(name)) {
                return role;
            }
        }
        return null;
    }

    private void updateRoleItem(ItemStack item, UhcRole role) {
        ItemMeta meta = item.getItemMeta();
        List<UhcRole> activeRoles = UhcAPI.getInstance().getRoleManager().getActiveRoles();
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            // Ajouter le statut du rôle dans la lore
            lore.add(ChatColor.GRAY + "Cliquez pour " + (activeRoles.contains(role) ? "désactiver" : "activer"));
            lore.add(activeRoles.contains(role) ? ChatColor.GREEN + "Rôle activé" : ChatColor.RED + "Rôle désactivé");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }
}