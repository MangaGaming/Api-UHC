package com.mguhc.roles;

import java.util.ArrayList;
import java.util.List;

public class Camp {
    private String name;
    private String location;
    private List<UhcRole> associatedRoles;

    public Camp(String name, String location) {
        this.name = name;
        this.location = location;
        this.associatedRoles = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public void addRole(UhcRole role) {
        if (!associatedRoles.contains(role)) {
            associatedRoles.add(role);
        }
    }

    public void removeRole(UhcRole role) {
        associatedRoles.remove(role);
    }

    public List<UhcRole> getAssociatedRoles() {
        return associatedRoles;
    }
}