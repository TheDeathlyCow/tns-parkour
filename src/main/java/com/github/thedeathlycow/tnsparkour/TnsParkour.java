package com.github.thedeathlycow.tnsparkour;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

public final class TnsParkour extends JavaPlugin {

    public static final String NAME = "TNS-Parkour";

    private final ArenaManager ARENA_MANAGER = new ArenaManager();
    private static String filePath = "arenas";

    @Override
    public void onEnable() {
        this.getServer().getPluginManager()
                .registerEvents(new TnsParkourListener(this), this);

        FileConfiguration config = this.getConfig();
        config.addDefault("arenas-file-location", filePath);
        filePath = config.getString("arenas-file-location");

        try {
            ARENA_MANAGER.readArenas(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error reading arenas file!");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ArenaManager getArenaManager() {
        return ARENA_MANAGER;
    }
}
