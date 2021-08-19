package com.github.thedeathlycow.tnsparkour;

import org.bukkit.Location;

public class ParkourArena {

    private final String name;
    private final Location startLocation;
    private final Location endLocation;

    public ParkourArena(String name, Location startLocation, Location endLocation) {
        this.name = name;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
    }

    public String getName() {
        return name;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public Location getEndLocation() {
        return endLocation;
    }
}
