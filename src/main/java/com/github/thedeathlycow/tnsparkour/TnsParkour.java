package com.github.thedeathlycow.tnsparkour;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public final class TnsParkour extends JavaPlugin {

    public static final String NAME = "TNS-Parkour";

    public static final Set<ParkourArena> ARENAS = new HashSet<>();

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
        return ARENAS.stream()
                .filter((arena) -> arena.getEndLocation().equals(location))
                .findFirst().orElse(null);
    }

    public ParkourArena getArenaOfStartLocation(Location location) {
        return ARENAS.stream()
                .filter((arena) -> arena.getStartLocation().equals(location))
                .findFirst().orElse(null);
    }
}
