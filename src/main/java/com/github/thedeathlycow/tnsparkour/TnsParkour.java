package com.github.thedeathlycow.tnsparkour;

import com.github.thedeathlycow.tnsparkour.arena.ArenaManager;
import com.github.thedeathlycow.tnsparkour.events.NoParamEventDelegate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class TnsParkour extends JavaPlugin {

    private static TnsParkour instance;
    public static final String NAME;
    public static final String arenasFilename;

    private final ArenaManager ARENA_MANAGER = new ArenaManager();
    private Location hubLocation;
    public final NoParamEventDelegate onEnableDelegate = new NoParamEventDelegate();

    @Override
    public void onEnable() {
        instance = this;
        this.getServer().getPluginManager()
                .registerEvents(new TnsParkourListener(), this);
        try {
            if (!this.getDataFolder().exists() && !this.getDataFolder().mkdirs()) {
                Bukkit.getLogger().log(Level.SEVERE, "Could not make data folder!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        onEnableDelegate.execute();
    }

    public void setHubLocation(Location hub) {
        this.hubLocation = hub;
    }

    public Location getHubLocation() {
        return hubLocation;
    }

    @Override
    public void onDisable() {
    }

    public static TnsParkour getInstance() {
        return instance;
    }

    public ArenaManager getArenaManager() {
        return ARENA_MANAGER;
    }

    public static Location getIntLocation(Location location) {
        return new Location(location.getWorld(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ());
    }

    static {
        NAME = "TNS-Parkour";
        arenasFilename = "arenas.json";
    }
}
