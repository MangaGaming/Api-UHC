package com.mguhc.roles;

public class UhcRole {
    private String name;
    private String description;

    public UhcRole(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}