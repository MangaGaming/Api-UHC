package com.mguhc.listeners;

import java.lang.reflect.Field;
import java.util.*;

import com.mguhc.effect.EffectManager;
import com.mguhc.permsion.PermissionManager;
import com.mguhc.player.UhcPlayer;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
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

import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class ConfigListener implements Listener {

    private final PlayerManager playerManager;
    private final PermissionManager permissionManager;
    private final EffectManager effectManager;
    private RoleManager roleManager;
    private UhcGame uhcgame;
    private Map<Player, String> playerInputState = new HashMap<>();
    
    public ConfigListener(UhcGame uhcgame) {
        this.uhcgame = uhcgame;
        this.roleManager = UhcAPI.getInstance().getRoleManager();
        this.playerManager = UhcAPI.getInstance().getPlayerManager();
        this.permissionManager = UhcAPI.getInstance().getPermissonManager();
        this.effectManager = UhcAPI.getInstance().getEffectManager();
    }

    // Item Giver
    @EventHandler
    private void OnJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World world = Bukkit.getWorld("world");
        UHCScoreboard uhcscoreboard = new UHCScoreboard();
        uhcscoreboard.createScoreboard(player);
        new BukkitRunnable() {
            @Override
            public void run() {
                UhcAPI.setTabHeaderFooter(player);
            }
        }.runTaskTimer(UhcAPI.getInstance(), 0, 20);
        GamePhase currentphase = uhcgame.getCurrentPhase();
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
            
            // Vérifier si le joueur a la permission avec laquelle est op
            if (permissionManager.hasPermission(player, "api.host") || player.isOp() && currentphase.getName().equals("Waiting")) {
                // Créer une étoile du Nether nommée "config"
                ItemStack netherStar = new ItemStack(Material.NETHER_STAR);
                ItemMeta meta = netherStar.getItemMeta();
                meta.setDisplayName(ChatColor.RED + "Config");
                netherStar.setItemMeta(meta);
                player.getInventory().addItem(netherStar); // Donner l'étoile du Nether au joueur
                player.updateInventory();
            }
            
            if(permissionManager.hasPermission(player, "api.mod")) {
            	if(!player.isOp()) {
                	giveModItems(player);
            	}
            }
            event.setJoinMessage("§f \n" +
                    "§a│ §fLe joueur §a§l" + player.getName() + " §fvient de rejoindre. §3(§f" + playerManager.getPlayers().size() + "§b/§f40§3)");
            sendClickableMessage(player);
        }
    }

    public static void sendClickableMessage(Player player) {
        FileConfiguration config = UhcAPI.getInstance().getConfig();
        // Créer le message
        TextComponent message = new TextComponent("§f\n§f\n§3§l» §f§lMangaGaming Dev §3● §f" + UhcAPI.getInstance().getUhcName() + "\n§f\n");

        // Ajouter le texte pour le serveur professionnel
        TextComponent professional = new TextComponent("§3│ §fServeur de §eboutiquel§f. §b(§fcliquez§b)");
        professional.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/XeDFVQHmbd"));
        message.addExtra(professional);

        // Ajouter un saut de ligne
        message.addExtra("\n");

        // Ajouter le texte pour le serveur communautaire
        TextComponent community = new TextComponent("§3│ §fNotre serveur de §ccommunautaire§f. §b(§fcliquez§b)");
        community.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, config.getString("link.discord")));
        message.addExtra(community);

        // Envoyer le message au joueur
        player.spigot().sendMessage(message);
    }
    
    @EventHandler
    private void OnLeave(PlayerQuitEvent event) {
    	Player player = event.getPlayer();
    	if(uhcgame.getCurrentPhase().getName().equals("Waiting")) {
    		playerManager.getPlayers().remove(player);
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

    // Inv opener
    private void openConfigInventory(Player player) {
        // Créer un inventaire de configuration de 36 slots
        Inventory configInventory = Bukkit.createInventory(null, 54, ChatColor.GREEN + "Configuration");

        // Items de décoration
        configInventory.setItem(0, ConfigItemUtils.getBlueGlassItem());
        configInventory.setItem(1, ConfigItemUtils.getBlueGlassItem());
        configInventory.setItem(9, ConfigItemUtils.getBlueGlassItem());
        configInventory.setItem(36, ConfigItemUtils.getBlueGlassItem());
        configInventory.setItem(45, ConfigItemUtils.getBlueGlassItem());
        configInventory.setItem(46, ConfigItemUtils.getBlueGlassItem());
        configInventory.setItem(7, ConfigItemUtils.getBlueGlassItem());
        configInventory.setItem(8, ConfigItemUtils.getBlueGlassItem());
        configInventory.setItem(17, ConfigItemUtils.getBlueGlassItem());
        configInventory.setItem(52, ConfigItemUtils.getBlueGlassItem());
        configInventory.setItem(53, ConfigItemUtils.getBlueGlassItem());
        configInventory.setItem(44, ConfigItemUtils.getBlueGlassItem());

        configInventory.setItem(3, ConfigItemUtils.getAquaGlassItem());
        configInventory.setItem(5, ConfigItemUtils.getAquaGlassItem());
        configInventory.setItem(48, ConfigItemUtils.getAquaGlassItem());
        configInventory.setItem(50, ConfigItemUtils.getAquaGlassItem());

        // Autres Items
        configInventory.setItem(4, ConfigItemUtils.getUhcHead());
        configInventory.setItem(13, ConfigItemUtils.getBorderItem());
        configInventory.setItem(22, ConfigItemUtils.getStartItem());
        configInventory.setItem(21, ConfigItemUtils.getEffectItem());
        configInventory.setItem(23, ConfigItemUtils.getEnchantItem());
        configInventory.setItem(31, ConfigItemUtils.getMdjItem());
        configInventory.setItem(39, ConfigItemUtils.getModItem());
        configInventory.setItem(40, ConfigItemUtils.getScenarioItem());
        configInventory.setItem(41, ConfigItemUtils.getHostItem());
        configInventory.setItem(49, ConfigItemUtils.getMeetupItem());

        // Ouvrir l'inventaire pour le joueur
        player.openInventory(configInventory);
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
                if (permissionManager.hasPermission(onlinePlayer, "api.mod")) {
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
                if (permissionManager.hasPermission(onlinePlayer, "api.host")) {
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
        Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.GREEN + "Configurer les Rôles");

        List<UhcRole> validRoles = UhcAPI.getInstance().getRoleManager().getValidRoles();
        List<UhcRole> activeRoles = UhcAPI.getInstance().getRoleManager().getActiveRoles();
        for (UhcRole role : validRoles) {
            ItemStack roleItem = new ItemStack(Material.PAPER); // Utilisez un item approprié
            ItemMeta meta = roleItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(role.getName()); // Assurez-vous que UhcRole à une méthode getName()
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Cliquez pour " + (roleManager.getActiveRoles().contains(role) ? "désactiver" : "activer"));
                lore.add(activeRoles.contains(role) ? ChatColor.GREEN + "Rôle activé" : ChatColor.RED + "Rôle désactivé");
                meta.setLore(lore);
                roleItem.setItemMeta(meta);
            }
            inventory.addItem(roleItem);
        }
        player.openInventory(inventory);
	}

    private void openEffectInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9, "Pourcentages des effets");

        ItemStack strenght = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta strenghtMeta = (SkullMeta) strenght.getItemMeta();
        if (strenghtMeta != null) {
            strenghtMeta.setDisplayName("§c§lForce");

            // Définir la texture de la tête
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTBkZmM4YTM1NjNiZjk5NmY1YzFiNzRiMGIwMTViMmNjZWIyZDA0Zjk0YmJjZGFmYjIyOTlkOGE1OTc5ZmFjMSJ9fX0"));

            // Utiliser la réflexion pour définir le profil
            try {
                Field field = strenghtMeta.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(strenghtMeta, profile);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            strenght.setItemMeta(strenghtMeta);
        }

        ItemStack resistance = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta resistanceMeta = (SkullMeta) resistance.getItemMeta();
        if (resistanceMeta != null) {
            resistanceMeta.setDisplayName("§b§lResistance");

            // Définir la texture de la tête
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjEwZjI5NGE3Y2Q2Y2ZjNmZiYjZjZTM3NTMwMGNlOTJhNmY1YWI0YjAxMzQ5ZTRmN2EyMjIwMmQ2ZmZjNmRlMyJ9fX0"));

            // Utiliser la réflexion pour définir le profil
            try {
                Field field = resistanceMeta.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(resistanceMeta, profile);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            resistance.setItemMeta(resistanceMeta);
        }

        ItemStack speed = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
        SkullMeta speedMeta = (SkullMeta) speed.getItemMeta();
        if (speedMeta != null) {
            speedMeta.setDisplayName("§6§lSpeed");

            // Définir la texture de la tête
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzZhMTkwYTUyMzczZjNiYTQwMGM3N2U5YzcxOTQxNWRmMDhmOTRiNDRkNTJmMzM5NGFmNGIxNDdkNDQ1OGEzYSJ9fX0"));

            // Utiliser la réflexion pour définir le profil
            try {
                Field field = speedMeta.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(speedMeta, profile);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            speed.setItemMeta(speedMeta); // Appliquer les modifications à l'item
        }

        inventory.setItem(0, strenght);
        inventory.setItem(4, resistance);
        inventory.setItem(8, speed);

        player.closeInventory();
        player.openInventory(inventory);
    }

    // Inv clickEvent
    @EventHandler
    private void onConfigInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GREEN + "Configuration")) {
            event.setCancelled(true); // Annuler l'événement pour éviter de déplacer les items

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem.equals(ConfigItemUtils.getMeetupItem())) {
                if (uhcgame.getMettup()) {
                    uhcgame.setMettup(false);
                    player.sendMessage("Meetup Désactivé");
                }
                else if (!uhcgame.getMettup()) {
                    uhcgame.setMettup(true);
                    player.sendMessage("Meetup Activé");
                }
            }

            if (clickedItem.equals(ConfigItemUtils.getStartItem())) {
                player.sendMessage(ChatColor.GREEN + "La partie est lancé");
                uhcgame.startGame();
                player.closeInventory();
            }
            if (clickedItem.equals(ConfigItemUtils.getMdjItem())) {
                openGameModeInventory(player);
            }
            if (clickedItem.equals(ConfigItemUtils.getBorderItem())) {
                openBorderInventory(player);
            }
            if (clickedItem.equals(ConfigItemUtils.getScenarioItem())) {
                openScenarioInventory(player);
            }

            if (clickedItem.equals(ConfigItemUtils.getHostItem())) {
                openHostInventory(player);
            }

            if (clickedItem.equals(ConfigItemUtils.getModItem())) {
                openModInventory(player);
            }

            if (clickedItem.equals(ConfigItemUtils.getEffectItem())) {
                openEffectInventory(player);
            }

            if (clickedItem.equals(ConfigItemUtils.getEnchantItem())) {
                EnchantListener.getInstance().openEnchantInventory(player);
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
                    // Vérifier si le joueur a déjà la permission
                    if (permissionManager.hasPermission(selectedPlayer, "api.mod")) {
                        // Retirer la permission api.mod
                        permissionManager.removePermission(player, "api.mod");

                        playerManager.getPlayers().remove(selectedPlayer);
                        player.sendMessage(ChatColor.RED + selectedPlayer.getName() + " n'est plus Mod.");
                        selectedPlayer.sendMessage(ChatColor.RED + "Vous avez été retiré du statut de Mod.");
                    } else {
                        // Ajouter la permission api.mod
                        permissionManager.addPermission(selectedPlayer, "api.mod");

                        playerManager.getPlayers().put(selectedPlayer, new UhcPlayer(selectedPlayer));
                        player.sendMessage(ChatColor.GREEN + selectedPlayer.getName() + " a maintenant le statut de Mod.");
                        selectedPlayer.sendMessage(ChatColor.GREEN + "Vous avez été promu au statut de Mod.");
                        giveModItems(selectedPlayer);
                    }
                }
            }
        }
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
                    if (permissionManager.hasPermission(selectedPlayer, "api.host")) {
                        // Retirer la permission api.mod
                        permissionManager.removePermission(selectedPlayer, "api.host");

                        player.getInventory().remove(Material.NETHER_STAR);
                        player.sendMessage(ChatColor.RED + selectedPlayer.getName() + " n'est plus Host.");
                        selectedPlayer.sendMessage(ChatColor.RED + "Vous avez été retiré du statut de Host.");

                    } else {
                        // Ajouter la permission api.host
                        permissionManager.addPermission(selectedPlayer, "api.host");

                        player.sendMessage(ChatColor.GREEN + selectedPlayer.getName() + " a maintenant le statut de Host.");
                        selectedPlayer.sendMessage(ChatColor.GREEN + "Vous avez été promu au statut de Host.");

                        ItemStack netherStar = new ItemStack(Material.NETHER_STAR);
                        ItemMeta meta = netherStar.getItemMeta();
                        meta.setDisplayName(ChatColor.RED + "Config");
                        netherStar.setItemMeta(meta);
                        selectedPlayer.getInventory().addItem(netherStar); // Donner l'étoile du Nether au joueur
                        selectedPlayer.updateInventory();
                    }
                }
            }
        }
    }
    
    @EventHandler
    private void OnScenarioInventoryClick(InventoryClickEvent event) {
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
                playerInputState.put(player, "borderSize");
                player.closeInventory();
            }

            // Vérifier si l'item cliqué est celui pour définir le timer de la bordure
            if (clickedItem != null && clickedItem.getType() == Material.COMPASS && 
                clickedItem.getItemMeta().getDisplayName().equals(ChatColor.YELLOW + "Timer de la Bordure")) {
                player.sendMessage(ChatColor.GREEN + "Veuillez entrer le timer de la bordure en secondes :");
                playerInputState.put(player, "borderTimer");
                player.closeInventory();
            }
        }
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
                UhcRole clickedRole = UhcAPI.getInstance().getRoleManager().getUhcRole(roleName); // Méthode pour trouver le rôle par son nom

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
    private void OnEffectInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("Pourcentages des effets")) {
            ItemStack item = event.getCurrentItem();
            Player player = (Player) event.getWhoClicked();
            if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
                return;
            }

            if (playerInputState.containsKey(player)) {
                player.sendMessage("§cVous êtes déja en train de taper quelque chose");
                return;
            }

            String name = item.getItemMeta().getDisplayName();
            switch (name) {
                case "§c§lForce":
                    playerInputState.put(player, "strenght");
                    player.closeInventory();
                    player.sendMessage("§aVous devez taper le pourcentage voulue de l'effet au niveau 1");
                    break;
                case "§b§lResistance":
                    playerInputState.put(player, "resistance");
                    player.closeInventory();
                    player.sendMessage("§aVous devez taper le pourcentage voulue de l'effet au niveau 1");
                case "§6§lSpeed":
                    playerInputState.put(player, "speed");
                    player.closeInventory();
                    player.sendMessage("§aVous devez taper le pourcentage voulue de l'effet au niveau 1");
            }
        }
    }

    // Other methods
    @EventHandler
    private void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        String name = playerInputState.get(player);

        if (name == null) {
            return;
        }

        switch (name) {
            case "borderSize":
                try {
                    int v = Integer.parseInt(message);
                    uhcgame.setborderSize(v); // Mettre à jour la taille de la bordure
                    player.sendMessage(ChatColor.GREEN + "Taille de la bordure définie à " + v + " blocs.");
                    playerInputState.remove(player); // Réinitialiser l'état
                    event.setCancelled(true);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Veuillez entrer un nombre valide.");
                }
                break;
            case "borderTimer":
                try {
                    int v = Integer.parseInt(message);
                    uhcgame.setborderTimer(v); // Mettre à jour le timer de la bordure
                    player.sendMessage(ChatColor.GREEN + "Timer de la bordure défini à " + v + " secondes.");
                    playerInputState.remove(player); // Réinitialiser l'état
                    event.setCancelled(true);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Veuillez entrer un nombre valide.");
                }
                break;
            case "strenght":
                try {
                    int v = Integer.parseInt(message);
                    effectManager.setLevel1Effect(PotionEffectType.INCREASE_DAMAGE, v);
                    player.sendMessage(ChatColor.GREEN + "Pourcentage de l'effet défini à " + v + " %.");
                    playerInputState.remove(player);
                    event.setCancelled(true);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Veuillez entrer un nombre valide.");
                }
                break;
            case "resistance":
                try {
                    int v = Integer.parseInt(message);
                    effectManager.setLevel1Effect(PotionEffectType.DAMAGE_RESISTANCE, v);
                    player.sendMessage(ChatColor.GREEN + "Pourcentage de l'effet défini à " + v + " %.");
                    playerInputState.remove(player);
                    event.setCancelled(true);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Veuillez entrer un nombre valide.");
                }
                break;
            case "speed":
                try {
                    int v = Integer.parseInt(message);
                    effectManager.setLevel1Effect(PotionEffectType.SPEED, v);
                    player.sendMessage(ChatColor.GREEN + "Pourcentage de l'effet défini à " + v + " %.");
                    playerInputState.remove(player);
                    event.setCancelled(true);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Veuillez entrer un nombre valide.");
                }
                break;
            default:
                break;
        }
    }

    @EventHandler
    private void OnToggleFlight(PlayerToggleFlightEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
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

    private void giveModItems(Player player) {
        player.getInventory().clear();
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
}