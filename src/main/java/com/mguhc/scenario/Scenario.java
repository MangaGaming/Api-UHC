package com.mguhc.scenario;

public abstract class Scenario {
    private String name;

    public Scenario(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void onActivate();
    public abstract void onDeactivate();
}