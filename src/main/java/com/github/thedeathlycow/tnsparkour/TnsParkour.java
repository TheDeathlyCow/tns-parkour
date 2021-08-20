package com.github.thedeathlycow.tnsparkour;

import com.github.thedeathlycow.tnsparkour.commands.ArenaCommand;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class TnsParkour extends JavaPlugin {

    public static final String NAME = "TNS-Parkour";

    private final ArenaManager ARENA_MANAGER = new ArenaManager();
    public static final String arenasFilename = "arenas.json";

    @Override
    public void onEnable() {
        this.getServer().getPluginManager()
                .registerEvents(new TnsParkourListener(this), this);

        try {
            if (!this.getDataFolder().exists()) {
                this.getDataFolder().mkdirs();
            }

            File arenasFile = new File(this.getDataFolder(), arenasFilename);
            ARENA_MANAGER.readArenas(arenasFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error reading arenas file!");
        }

        this.getCommand("tnsparkour").setExecutor(new ArenaCommand(this));
    }

    @Override
    public void onDisable() {
        try {
            if (!this.getDataFolder().exists()) {
                this.getDataFolder().mkdirs();
            }

            File arenasFile = new File(this.getDataFolder(), arenasFilename);
            ARENA_MANAGER.saveArenas(arenasFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error saving arenas file!");
        }
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
}
