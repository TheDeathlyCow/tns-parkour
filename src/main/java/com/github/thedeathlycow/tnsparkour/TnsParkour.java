package com.github.thedeathlycow.tnsparkour;

import com.github.thedeathlycow.tnsparkour.arena.ArenaManager;
import com.github.thedeathlycow.tnsparkour.commands.RefreshLeaderboards;
import com.github.thedeathlycow.tnsparkour.commands.ReloadParkour;
import com.github.thedeathlycow.tnsparkour.events.NoParamEventDelegate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.A;

import java.util.Objects;
import java.util.logging.Level;

public final class TnsParkour extends JavaPlugin {

    private static TnsParkour instance;
    public static final String NAME;


    private final ArenaManager ARENA_MANAGER;
    private Location hubLocation;
    public final NoParamEventDelegate onEnableDelegate = new NoParamEventDelegate();

    public TnsParkour() {
        instance = this;
        ARENA_MANAGER = new ArenaManager();
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager()
                .registerEvents(new TnsParkourListener(), this);
        try {
            if (!this.getDataFolder().exists() && !this.getDataFolder().mkdirs()) {
                Bukkit.getLogger().log(Level.SEVERE, "Could not make data folder!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Objects.requireNonNull(this.getCommand("reloadParkour")).setExecutor(new ReloadParkour());
        Objects.requireNonNull(this.getCommand("refreshLeaderboards")).setExecutor(new RefreshLeaderboards());

        this.setDefaultConfig();

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

    private void setDefaultConfig() {
        FileConfiguration config = this.getConfig();
        config.addDefault("time_added_on_fall_ms", 1500);
        config.addDefault("leaderboard_tag_prefix", "tnsparkour_leaderboard_");
        config.addDefault("run_objective_prefix", "tnsparkour.runs.");
        config.options().copyDefaults(true);
        this.saveDefaultConfig();
        this.saveConfig();
    }

    static {
        NAME = "TNS-Parkour";
    }
}
