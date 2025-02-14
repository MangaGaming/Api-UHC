package com.mguhc.listeners;

import com.mguhc.UhcAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ColorCommand implements Listener {

    private Map<Player, ChatColor> selectedPlayerColor = new HashMap<>();
    private List<Player> playerList;

    @EventHandler
    private void OnCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String[] args = event.getMessage().split(" ");
        if (args.length != 1 && args[0].equals("/color")) {
            playerList = new ArrayList<>();
            for (String arg : args) {
                if (!arg.equals(args[0])) {
                    Player target = Bukkit.getPlayer(arg);
                    if (target != null) {
                        playerList.add(target);
                    }
                    else {
                        player.sendMessage("§c Un des joueurs n'est pas en ligne");
                        break;
                    }
                }
            }
            if (!playerList.isEmpty()) {
                openColorGui(player);
            }
        }
    }

    private void openColorGui(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9, "§9 Choisir une couleur");

        ItemStack green = new ItemStack(Material.WOOL, 1, (short) 5);
        ItemMeta greenMeta = green.getItemMeta();
        if (greenMeta != null) {
            greenMeta.setDisplayName("§a Vert");
            green.setItemMeta(greenMeta);
        }
        inventory.setItem(0, green);

        ItemStack red = new ItemStack(Material.WOOL, 1, (short) 14);
        ItemMeta redMeta = red.getItemMeta();
        if (redMeta != null) {
            redMeta.setDisplayName("§c Rouge");
            red.setItemMeta(redMeta);
        }
        inventory.setItem(1, red);

        ItemStack yellow = new ItemStack(Material.WOOL, 1, (short) 4);
        ItemMeta yellowMeta = yellow.getItemMeta();
        if (yellowMeta != null) {
            yellowMeta.setDisplayName("§e Yellow");
            yellow.setItemMeta(yellowMeta);
        }
        inventory.setItem(2, yellow);

        ItemStack white = new ItemStack(Material.WOOL, 1, (short) 0);
        ItemMeta whiteMeta = yellow.getItemMeta();
        if (whiteMeta != null) {
            whiteMeta.setDisplayName("§e Yellow");
            white.setItemMeta(yellowMeta);
        }
        inventory.setItem(3, white);

        player.openInventory(inventory);
    }

    @EventHandler
    private void OnInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        Inventory inventory = event.getInventory();
        if (inventory.getName().equals("§9 Choisir une couleur")) {
            event.setCancelled(true);
            if (item.getType().equals(Material.WOOL)) {
                switch (item.getDurability()) {
                    case 5:
                        for (Player p : playerList) {
                            selectedPlayerColor.put(p, ChatColor.GREEN);
                        }
                        player.sendMessage("§aCouleur mis à verte");
                        break;
                    case 14:
                        for (Player p : playerList) {
                            selectedPlayerColor.put(p, ChatColor.RED);
                        }
                        player.sendMessage("§cCouleur mise à rouge");
                        break;
                    case 4:
                        for (Player p : playerList) {
                            selectedPlayerColor.put(p, ChatColor.YELLOW);
                        }
                        player.sendMessage("§eCouleur mise à jaune");
                        break;
                    case 0:
                        for (Player p : playerList) {
                            selectedPlayerColor.put(p, ChatColor.WHITE);
                        }
                        player.sendMessage("§fCouleur mise à blanc");
                        break;
                }
                playerList = new ArrayList<>();
                player.closeInventory();
                UhcAPI.getInstance().putInColorMap(player, selectedPlayerColor);
            }
        }
    }
}
