package com.mguhc.ability;

public class Ability {

    private final String name;
    private int cooldown;

    public Ability(String name, int cooldown) {
        this.name = name;
        this.cooldown = cooldown;
    }

    public String getName() {
        return name;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int v) {
        cooldown = v;
    }
}