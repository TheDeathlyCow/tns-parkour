package com.github.thedeathlycow.tnsparkour;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ArenaManager {

    private final Map<String, ParkourArena> ARENAS = new HashMap<>();

    public ParkourArena remove(String arena) {
        return ARENAS.remove(arena);
    }

    public ParkourArena getArena(String arena) {
        return ARENAS.get(arena);
    }

    public void addArena(ParkourArena arena) {
        ARENAS.put(arena.getName(), arena);
    }

    public boolean arenaExists(String name) {
        return ARENAS.containsKey(name);
    }

    public void readArenas(File arenasFile) throws IOException {

        if (arenasFile.exists()) {
            Gson gson = new GsonBuilder()
                    .disableHtmlEscaping()
                    .registerTypeAdapter(ParkourArena.class, new ParkourArena.Serializer())
                    .create();

            FileReader reader = new FileReader(arenasFile);
            List<ParkourArena> readArenas = gson
                    .fromJson(reader, new TypeToken<List<ParkourArena>>(){
                    }.getType());
            reader.close();
            readArenas.forEach(
                    (arena -> ARENAS.put(arena.getName(), arena))
            );
        } else {
            if (arenasFile.getParentFile().mkdirs() && arenasFile.createNewFile())
                System.out.println("Created new arenas file at: " + arenasFile.getAbsolutePath());
        }
    }

    public void loadArenas() throws IOException {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(TnsParkour.NAME);
        File dataFolder = plugin.getDataFolder();

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        File file = new File(dataFolder, TnsParkour.arenasFilename);
        readArenas(file);
    }

    public void saveArenas() throws IOException {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(TnsParkour.NAME);
        File dataFolder = plugin.getDataFolder();

        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        File file = new File(dataFolder, TnsParkour.arenasFilename);
        saveArenas(file);
    }

    public void saveArenas(File file) throws IOException {

        FileWriter writer = new FileWriter(file);
        Gson gson = new GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .registerTypeAdapter(ParkourArena.class, new ParkourArena.Serializer())
                .create();

        List<ParkourArena> arenasList = new ArrayList<>();
        ARENAS.forEach(
                ((name, arena) -> arenasList.add(arena))
        );

        String arenasString = gson.toJson(arenasList);
        writer.write(arenasString);
        writer.close();
    }

    public ParkourArena getArenaOfCheckpoint(Location location) {
        return getArenaAtLocation((entry -> entry.getValue().isCheckpoint(location)));
    }

    public ParkourArena getArenaOfEntrace(Location location) {
        return getArenaAtLocation((entry -> entry.getValue().getEntrance().equals(location)));
    }

    public ParkourArena getArenaOfEndLocation(Location location) {
        return getArenaAtLocation((entry) -> entry.getValue().getEndLocation().equals(location));
    }

    public ParkourArena getArenaOfStartLocation(Location location) {
        return getArenaAtLocation((entry) -> entry.getValue().getStartLocation().equals(location));
    }

    public ParkourArena getArenaAtLocation(Predicate<Map.Entry<String, ParkourArena>> filter) {
        Map.Entry<String, ParkourArena> result = ARENAS.entrySet().stream()
                .filter(filter)
                .findFirst().orElse(null);
        return result != null ? result.getValue() : null;
    }

}
