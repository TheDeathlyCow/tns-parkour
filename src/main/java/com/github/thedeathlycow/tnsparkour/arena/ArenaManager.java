package com.github.thedeathlycow.tnsparkour.arena;

import com.github.thedeathlycow.tnsparkour.TnsParkour;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import org.bukkit.Bukkit;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Level;

public class ArenaManager {

    private final Map<String, ParkourArena> ARENAS = new HashMap<>();
    private Map<IntLocation, ParkourArena> locationLookupCache = new HashMap<>();
    private final Gson GSON;

    public ArenaManager() {
        TnsParkour.getInstance().onEnableDelegate.register(this::readArenas);

        GSON = new GsonBuilder()
                .registerTypeAdapter(ParkourArena.class, new ParkourArena.Deserializer())
                .registerTypeAdapter(IntLocation.class, new IntLocation.Deserializer())
                .create();
    }

    public void readArenas() {
        locationLookupCache.clear();
        File arenasFolder = new File(TnsParkour.getInstance().getDataFolder(), "arenas");

        if (!arenasFolder.exists() && !arenasFolder.mkdirs()) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to create arenas folder!");
            return;
        }

        File[] files = arenasFolder.listFiles();

        if (files == null) {
            Bukkit.getLogger().log(Level.SEVERE, "Arena files failed to load!");
            return;
        }

        for (File file : files) {
            if (file.getName().endsWith(".json")) {
                loadArena(file);
            }
        }
    }

    private void loadArena(File file) {
        ParkourArena arena = null;

        try (FileReader reader = new FileReader(file)) {
            arena = GSON.fromJson(reader, ParkourArena.class);
        } catch (IOException | JsonIOException exception) {
            String msg = String.format("Arena file %s failed to load: %s", file.getName(), exception.getMessage());
            Bukkit.getLogger().log(Level.SEVERE, msg);
        } catch (JsonSyntaxException exception) {
            exception.printStackTrace();
            String msg = String.format("Arena %s failed to deserialize arena: %s", file.getName(), exception.getMessage());
            Bukkit.getLogger().log(Level.SEVERE, msg);
        }

        if (arena != null) {
            ARENAS.put(arena.getName(), arena);
        }

    }

    @Nullable
    public ParkourArena getArenaAtLocation(IntLocation location, LocationFilter compareTo) {

        if (locationLookupCache.containsKey(location)) {
            return locationLookupCache.get(location);
        }

        Map.Entry<String, ParkourArena> result = ARENAS.entrySet().stream()
                .filter((entry) -> compareTo.getLocation(entry.getValue()).contains(location))
                .findFirst().orElse(null);

        ParkourArena found = result != null ? result.getValue() : null;
        locationLookupCache.put(location, found);
        return found;
    }

    public interface LocationFilter {
        Collection<IntLocation> getLocation(ParkourArena arena);
    }

}
