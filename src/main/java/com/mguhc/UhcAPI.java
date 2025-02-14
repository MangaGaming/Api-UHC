package com.mguhc;

import com.mguhc.listeners.ColorCommand;
import com.mguhc.listeners.KillListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.mguhc.ability.AbilityManager;
import com.mguhc.ability.CooldownManager;
import com.mguhc.effect.EffectManager;
import com.mguhc.game.UhcGame;
import com.mguhc.listeners.ModItemListener;
import com.mguhc.listeners.PlayerListener;
import com.mguhc.player.PlayerManager;
import com.mguhc.roles.RoleManager;
import com.mguhc.scenario.ScenarioManager;

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

    @Override
    public void onEnable() {
        instance = this;
        playermanager = new PlayerManager();
        uhcgame = new UhcGame();
        effectManager = new EffectManager();
        roleManager = new RoleManager();
        scenariomanager = new ScenarioManager();
        cooldownManager = new CooldownManager();
        abilityManager = new AbilityManager();
        ColorCommand colorCommand = new ColorCommand();
        
        // Enregistrer l'écouteur
        PluginManager pluginManager = Bukkit.getPluginManager();
		pluginManager.registerEvents(this, this);
		pluginManager.registerEvents(effectManager, this);
        pluginManager.registerEvents(new PlayerListener(uhcgame), this);
        pluginManager.registerEvents(new KillListener(), this);
        pluginManager.registerEvents(new ModItemListener(), this);
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

    public Map<Player,Map<Player, ChatColor>> getColorMap() {
        return colorMap;
    }

    public void putInColorMap(Player player, Map<Player, ChatColor> selectedPlayerColor) {
        colorMap.put(player, selectedPlayerColor);
    }
}