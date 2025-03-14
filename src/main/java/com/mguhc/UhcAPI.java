package com.mguhc;

import com.mguhc.ability.Ability;
import com.mguhc.listeners.ColorCommand;
import com.mguhc.listeners.KillListener;
import com.mguhc.permsion.PermissionManager;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.mguhc.ability.AbilityManager;
import com.mguhc.ability.CooldownManager;
import com.mguhc.effect.EffectManager;
import com.mguhc.game.UhcGame;
import com.mguhc.listeners.ModItemListener;
import com.mguhc.listeners.ConfigListener;
import com.mguhc.player.PlayerManager;
import com.mguhc.roles.RoleManager;
import com.mguhc.scenario.ScenarioManager;
import com.mguhc.listeners.EnchantListener;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class UhcAPI extends JavaPlugin implements Listener {
    private PlayerManager playermanager;
    private UhcGame uhcgame;
    private RoleManager roleManager;
    private static UhcAPI instance;
	private ScenarioManager scenariomanager;
	private CooldownManager cooldownManager;
	private String name;
	private AbilityManager abilityManager;
	private EffectManager effectManager;
    private Map<Player, Map<Player, ChatColor>> colorMap = new HashMap<>();
    private PermissionManager permissonManager;

    @Override
    public void onEnable() {
        instance = this;
        playermanager = new PlayerManager();
        uhcgame = new UhcGame();
        effectManager = new EffectManager();
        roleManager = new RoleManager();
        permissonManager = new PermissionManager();
        scenariomanager = new ScenarioManager();
        cooldownManager = new CooldownManager();
        abilityManager = new AbilityManager();
        ColorCommand colorCommand = new ColorCommand();

        saveDefaultConfig();
        
        // Enregistrer l'écouteur
        PluginManager pluginManager = Bukkit.getPluginManager();
		pluginManager.registerEvents(this, this);
		pluginManager.registerEvents(effectManager, this);
        pluginManager.registerEvents(new ConfigListener(uhcgame), this);
        pluginManager.registerEvents(new KillListener(), this);
        pluginManager.registerEvents(new ModItemListener(), this);
        pluginManager.registerEvents(new EnchantListener(), this);
        pluginManager.registerEvents(colorCommand, this);
    }
    
    public String getUhcName() {
    	return name;
    }
    
    public void setUhcName(String name) {
    	this.name = name;
    }

    public static UhcAPI getInstance() {
        return instance;
    }
    
    public UhcGame getUhcGame() {
        return uhcgame;
    }

    public PlayerManager getPlayerManager() {
        return playermanager;
    }
    
    public RoleManager getRoleManager() {
        return roleManager;
    }
    
    public ScenarioManager getScenarioManager() {
    	return scenariomanager;
    }
    
    public CooldownManager getCooldownManager() {
    	return cooldownManager;
    }
    
    public AbilityManager getAbilityManager() {
    	return abilityManager;
    }
    
    public EffectManager getEffectManager() {
    	return effectManager;
    }

    public PermissionManager getPermissonManager() {
        return permissonManager;
    }

    public FileConfiguration getConfig() {
        return super.getConfig(); // Renvoie l'objet FileConfiguration
    }

    public Map<Player,Map<Player, ChatColor>> getColorMap() {
        return colorMap;
    }

    public void putInColorMap(Player player, Map<Player, ChatColor> selectedPlayerColor) {
        colorMap.put(player, selectedPlayerColor);
    }

    public static void sendActionBar(Player player, String message) {
        if (player == null || message == null) {
            return;
        }

        IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat packet = new PacketPlayOutChat(chatBaseComponent, (byte) 2); // Type 2 = Action Bar

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public static String getFleche(Location location, Location target) {
        // Calculer la direction
        double deltaX = target.getX() - location.getX();
        double deltaZ = target.getZ() - location.getZ();

        // Déterminer la direction
        String direction;
        if (Math.abs(deltaX) > Math.abs(deltaZ)) {
            // Horizontal
            if (deltaX > 0) {
                direction = "→"; // Droite
            } else {
                direction = "←"; // Gauche
            }
        } else {
            // Vertical
            if (deltaZ > 0) {
                direction = "↓"; // Bas
            } else {
                direction = "↑"; // Haut
            }
        }

        // Retourner la direction
        return direction;
    }

    public static void sendCooldownMessage(Player player, Ability ability) {
        long l = (long) UhcAPI.getInstance().getCooldownManager().getRemainingCooldown(player, ability) / 1000;
        player.sendMessage("§cVous êtes en cooldown pour " + l + "s");
    }

    public static void setTabHeaderFooter(Player player) {
        EffectManager effectManager = UhcAPI.getInstance().getEffectManager();
        String header = "§3§l» §f§lMangaGaming Dev §3§l«\n" +
                "§7" + UhcAPI.getInstance().getUhcName() + "\n";
        int strength = effectManager.getEffect(player, PotionEffectType.INCREASE_DAMAGE);
        int resistance = effectManager.getEffect(player, PotionEffectType.DAMAGE_RESISTANCE);
        int speed = effectManager.getEffect(player, PotionEffectType.SPEED);
        String footer = "\n§c⚔ " + strength + "% §8| §7❂ " + resistance + "% §8| §b✪ " + speed + "%\n" +
                " §f \n" +
                "§3§lDiscord Boutique§8● §fdiscord.gg/XeDFVQHmbd";

        // Créer les composants de chat
        IChatBaseComponent headerComponent = new ChatComponentText(fixColors(header));
        IChatBaseComponent footerComponent = new ChatComponentText(fixColors(footer));

        // Créer le paquet
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
        try {
            // Utiliser la réflexion pour définir les champs du paquet
            Field headerField = packet.getClass().getDeclaredField("a");
            Field footerField = packet.getClass().getDeclaredField("b");
            headerField.setAccessible(true);
            footerField.setAccessible(true);
            headerField.set(packet, headerComponent);
            footerField.set(packet, footerComponent);

            // Envoyer le paquet au joueur
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String fixColors(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}