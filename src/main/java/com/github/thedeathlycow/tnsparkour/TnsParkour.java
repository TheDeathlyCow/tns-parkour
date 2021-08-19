package com.github.thedeathlycow.tnsparkour;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Predicate;

public final class TnsParkour extends JavaPlugin {

    public static final String NAME = "TNS-Parkour";

    public static final Map<String, ParkourArena> ARENAS = new HashMap<>();

    @Override
    public void onEnable() {
        this.getServer().getPluginManager()
                .registerEvents(new TnsParkourListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ParkourArena getArenaOfEndLocation(Location location) {
        Map.Entry<String, ParkourArena> result =  ARENAS.entrySet().stream()
                .filter((entry) -> entry.getValue().getEndLocation().equals(location))
                .findFirst().orElse(null);

        return result != null ? result.getValue() : null;
    }

    public ParkourArena getArenaOfStartLocation(Location location) {
        Map.Entry<String, ParkourArena> result =  ARENAS.entrySet().stream()
                .filter((entry) -> entry.getValue().getStartLocation().equals(location))
                .findFirst().orElse(null);

        return result != null ? result.getValue() : null;
    }
}
