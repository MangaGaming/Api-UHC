package com.mguhc.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class EnchantListener implements Listener {

    private static EnchantListener instance;
    private Map<Material, Integer> maxEnchantMap = new HashMap<>();
    private Map<Player, String> playerInputState = new HashMap<>(); // Pour suivre l'état d'entrée du joueur

    public EnchantListener() {
        instance = this;
        // Initialisez les niveaux d'enchantement maximum pour chaque type d'item ici
        maxEnchantMap.put(Material.IRON_HELMET, 3);
        maxEnchantMap.put(Material.IRON_CHESTPLATE, 3);
        maxEnchantMap.put(Material.IRON_LEGGINGS, 3);
        maxEnchantMap.put(Material.IRON_BOOTS, 3);
        maxEnchantMap.put(Material.DIAMOND_HELMET, 2);
        maxEnchantMap.put(Material.DIAMOND_CHESTPLATE, 2);
        maxEnchantMap.put(Material.DIAMOND_LEGGINGS, 2);
        maxEnchantMap.put(Material.DIAMOND_BOOTS, 2);
        maxEnchantMap.put(Material.DIAMOND_SWORD, 3);
        maxEnchantMap.put(Material.IRON_SWORD, 4);
        maxEnchantMap.put(Material.BOW, 3);
    }

    // Ouvrir l'inventaire d'enchantement
    public void openEnchantInventory(Player player) {
        Inventory enchantInventory = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Configurer les Enchantements");

        // Ajouter un item pour les pièces d'armure en fer
        ItemStack ironArmorItem = new ItemStack(Material.IRON_CHESTPLATE);
        ItemMeta ironArmorMeta = ironArmorItem.getItemMeta();
        if (ironArmorMeta != null) {
            ironArmorMeta.setDisplayName(ChatColor.RED + "Configurer Armure en Fer");
            ironArmorItem.setItemMeta(ironArmorMeta);
        }
        enchantInventory.addItem(ironArmorItem);

        // Ajouter un item pour les pièces d'armure en diamant
        ItemStack diamondArmorItem = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemMeta diamondArmorMeta = diamondArmorItem.getItemMeta();
        if (diamondArmorMeta != null) {
            diamondArmorMeta.setDisplayName(ChatColor.AQUA + "Configurer Armure en Diamant");
            diamondArmorItem.setItemMeta(diamondArmorMeta);
        }
        enchantInventory.addItem(diamondArmorItem);

        // Ajouter un item pour les épées en fer
        ItemStack ironSwordItem = new ItemStack(Material.IRON_SWORD);
        ItemMeta ironSwordMeta = ironSwordItem.getItemMeta();
        if (ironSwordMeta != null) {
            ironSwordMeta.setDisplayName(ChatColor.RED + "Configurer Épée en Fer");
            ironSwordItem.setItemMeta(ironSwordMeta);
        }
        enchantInventory.addItem(ironSwordItem);

        // Ajouter un item pour les épées en diamant
        ItemStack diamondSwordItem = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta diamondSwordMeta = diamondSwordItem.getItemMeta();
        if (diamondSwordMeta != null) {
            diamondSwordMeta.setDisplayName(ChatColor.AQUA + "Configurer Épée en Diamant");
            diamondSwordItem.setItemMeta(diamondSwordMeta);
        }
        enchantInventory.addItem(diamondSwordItem);

        // Ajouter un item pour les arcs
        ItemStack bowItem = new ItemStack(Material.BOW);
        ItemMeta bowMeta = bowItem.getItemMeta();
        if (bowMeta != null) {
            bowMeta.setDisplayName(ChatColor.GREEN + "Configurer Arc");
            bowItem.setItemMeta(bowMeta);
        }
        enchantInventory.addItem(bowItem);

        player.openInventory(enchantInventory);
    }

    @EventHandler
    private void OnEnchant(EnchantItemEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getEnchanter();
        Bukkit.getLogger().info("[Debug] " + player.getName() + " enchant an item");
        int maxEnchant = maxEnchantMap.getOrDefault(item.getType(), 0);
        if (maxEnchant == 0) {
            return; // Pas d'enchantement maximum défini pour cet item
        }
        Map<Enchantment, Integer> enchantsMap = event.getEnchantsToAdd();
        for (Map.Entry<Enchantment, Integer> entry : enchantsMap.entrySet()) {
            int level = entry.getValue();
            if (level > maxEnchant) {
                player.sendMessage(ChatColor.RED + "§cVous ne pouvez pas enchanter cet item au-delà du niveau " + maxEnchant);
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    private void OnAnvilUse(InventoryClickEvent event) {
        if (event.getView().getType() == InventoryType.ANVIL) {
            Player player = (Player) event.getWhoClicked();
            Inventory openInventory = event.getClickedInventory();
            if (openInventory == null) {
                return;
            }
            ItemStack item1 = openInventory.getItem(0); // L'item de gauche
            ItemStack item2 = openInventory.getItem(1); // L'item de droite (enchantement)

            if (item1 == null || item2 == null) {
                return;
            }

            int maxEnchant = maxEnchantMap.getOrDefault(item1.getType(), 0);
            if (isEnchantable(item1, item2)) {
                // Récupérer le niveau d'enchantement du livre
                int bookEnchantLevel = item2.getEnchantments().values().stream().findFirst().orElse(0);
                if (bookEnchantLevel > maxEnchant) {
                    player.sendMessage(ChatColor.RED + "§cVous ne pouvez pas ajouter cet enchantement au-delà du niveau " + maxEnchant);
                    event.setCancelled(true);
                    return;
                }

                // Vérifiez si l'item de gauche a des enchantements
                Map<Enchantment, Integer> itemEnchantments = item1.getEnchantments();
                for (Map.Entry<Enchantment, Integer> entry : itemEnchantments.entrySet()) {
                    int currentLevel = entry.getValue();
                    int newLevel = currentLevel + bookEnchantLevel;
                    if (newLevel > maxEnchant) {
                        player.sendMessage(ChatColor.RED + "§cVous ne pouvez pas combiner cet item au-delà du niveau " + maxEnchant);
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    private void OnEnchantInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GOLD + "Configurer les Enchantements")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                String displayName = clickedItem.getItemMeta().getDisplayName();
                player.closeInventory();
                player.sendMessage("§aVeuillez choisir la limite d'enchantement " + getName(displayName));
                playerInputState.put(player, displayName); // Enregistrer l'état d'entrée du joueur
            }
        }
    }

    @EventHandler
    private void OnPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String input = event.getMessage();

        if (playerInputState.containsKey(player)) {
            String itemType = playerInputState.get(player);
            try {
                int maxEnchantLevel = Integer.parseInt(input);
                List<Material> materials = getMaterialsFromDisplayName(itemType);
                if (materials != null) {
                    for (Material material : materials) {
                        setMaxEnchant(material, maxEnchantLevel);
                    }
                    player.sendMessage(ChatColor.GREEN + "Le niveau d'enchantement maximum " + getName(itemType) + " a été mis à jour à " + maxEnchantLevel);
                    event.setCancelled(true);
                }

            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Veuillez entrer un nombre valide.");
            } finally {
                playerInputState.remove(player); // Réinitialiser l'état d'entrée du joueur
            }
        }
    }

    private List<Material> getMaterialsFromDisplayName(String displayName) {
        displayName = ChatColor.stripColor(displayName);
        switch (displayName) {
            case "Configurer Armure en Fer":
                return Arrays.asList(Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS);
            case "Configurer Armure en Diamant":
                return Arrays.asList(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS);
            case "Configurer Épée en Fer":
                return Collections.singletonList(Material.IRON_SWORD);
            case "Configurer Épée en Diamant":
                return Collections.singletonList(Material.DIAMOND_SWORD);
            case "Configurer Arc":
                return Collections.singletonList(Material.BOW);
            default:
                return null;
        }
    }

    private String getName(String name) {
        name = ChatColor.stripColor(name);
        switch (name) {
            case "Configurer Armure en Fer":
                return "des Armures en Fers";
            case "Configurer Armure en Diamant":
                return "des Armures en Diaments";
            case "Configurer Épée en Fer":
                return "des Epées en Fer";
            case "Configurer Épée en Diamant":
                return "des Epées en Diament";
            case "Configurer Arc":
                return "des Arcs";
            default:
                return null;
        }
    }

    private boolean isEnchantable(ItemStack item1, ItemStack item2) {
        return (isIronArmor(item1) || isDiamondArmor(item1) || isSword(item1) || isBow(item1)) && item2.getType() == Material.ENCHANTED_BOOK;
    }

    public boolean isIronArmor(ItemStack item) {
        return item != null && item.getType() != Material.AIR && (
                item.getType() == Material.IRON_HELMET ||
                        item.getType() == Material.IRON_CHESTPLATE ||
                        item.getType() == Material.IRON_LEGGINGS ||
                        item.getType() == Material.IRON_BOOTS);
    }

    public boolean isDiamondArmor(ItemStack item) {
        return item != null && item.getType() != Material.AIR && (
                item.getType() == Material.DIAMOND_HELMET ||
                        item.getType() == Material.DIAMOND_CHESTPLATE ||
                        item.getType() == Material.DIAMOND_LEGGINGS ||
                        item.getType() == Material.DIAMOND_BOOTS);
    }

    public boolean isSword(ItemStack item) {
        return item != null && item.getType() != Material.AIR && (
                item.getType() == Material.IRON_SWORD ||
                        item.getType() == Material.DIAMOND_SWORD);
    }

    public boolean isBow(ItemStack item) {
        return item != null && item.getType() != Material.AIR && item.getType() == Material.BOW;
    }

    public void setMaxEnchant(Material material, int v) {
        maxEnchantMap.put(material, v);
    }

    public Map<Material, Integer> getMaxEnchantMap() {
        return maxEnchantMap;
    }

    public static EnchantListener getInstance() {
        return instance;
    }

    @EventHandler
    private void OnInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem != null) {
            Inventory openInventory = event.getClickedInventory();
            for (int slot = 0; slot < openInventory.getSize(); slot++) {
                ItemStack item = openInventory.getItem(slot);
                if (item != null && item.equals(clickedItem)) {
                    Bukkit.getLogger().info("Item cliqué trouvé dans le slot: " + slot);
                    break; // Sortir de la boucle une fois que l'item est trouvé
                }
            }
        }
    }
}