package com.github.thedeathlycow.tnsparkour;

import com.github.thedeathlycow.tnsparkour.commands.ArenaCommand;
import com.github.thedeathlycow.tnsparkour.commands.ParkourSubcommand;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public final class TnsParkour extends JavaPlugin {

    public static final String NAME = "TNS-Parkour";

    private final ArenaManager ARENA_MANAGER = new ArenaManager();
    private Location hubLocation;
    public static final String arenasFilename = "arenas.json";

    @Override
    public void onEnable() {
        this.getServer().getPluginManager()
                .registerEvents(new TnsParkourListener(this), this);
        ParkourSubcommand.PLUGIN = this;
        try {
            if (!this.getDataFolder().exists()) {
                this.getDataFolder().mkdirs();
            }

            File arenasFile = new File(this.getDataFolder(), arenasFilename);
            ARENA_MANAGER.readArenas(arenasFile);

            File data = new File(this.getDataFolder(), "hub.json");
            Gson gson = new GsonBuilder()
                    .disableHtmlEscaping()
                    .setPrettyPrinting()
                    .create();

            FileReader reader = new FileReader(data);
            Map<String, Object> hubLocation = gson.fromJson(
                    reader, new TypeToken<Map<String, Object>>() {
                    }.getType()
            );
            reader.close();

            this.hubLocation = Location.deserialize(hubLocation);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[TNS-Parkour]: Error reading arenas file!");
        }

        this.getCommand("tnsparkour").setExecutor(new ArenaCommand(this));
    }

    public void setHubLocation(Location hub) {
        this.hubLocation = hub;
    }

    public Location getHubLocation() {
        return hubLocation;
    }

    @Override
    public void onDisable() {
        try {
            if (!this.getDataFolder().exists()) {
                this.getDataFolder().mkdirs();
            }

            File arenasFile = new File(this.getDataFolder(), arenasFilename);
            ARENA_MANAGER.saveArenas(arenasFile);

            File data = new File(this.getDataFolder(), "hub.json");
            Gson gson = new GsonBuilder()
                    .disableHtmlEscaping()
                    .setPrettyPrinting()
                    .create();

            FileWriter writer = new FileWriter(data);
            writer.write(gson.toJson(hubLocation.serialize()));
            writer.close();

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
