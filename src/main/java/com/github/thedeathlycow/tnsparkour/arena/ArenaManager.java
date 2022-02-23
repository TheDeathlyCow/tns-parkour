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
import java.util.Objects;
import java.util.logging.Level;

public class ArenaManager {

    private final Map<String, ParkourArena> ARENAS = new HashMap<>();
    private final Map<CacheKey, ParkourArena> locationLookupCache = new HashMap<>();
    private final Gson GSON;

    public ArenaManager() {
        TnsParkour.getInstance().onEnableDelegate.register(this::readArenas);

        GSON = new GsonBuilder()
                .registerTypeAdapter(ParkourArena.class, new ParkourArena.Deserializer())
                .registerTypeAdapter(IntLocation.class, new IntLocation.Deserializer())
                .create();
    }

    public void refreshLeaderboards() {
        for (Map.Entry<String, ParkourArena> arena : ARENAS.entrySet()) {
            arena.getValue().refreshLeaderboard();
        }
    }

    public void readArenas() {
        ARENAS.clear();
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
            String msg = String.format("Arena %s failed to deserialize: %s", file.getName(), exception.getMessage());
            Bukkit.getLogger().log(Level.SEVERE, msg);
        }

        if (arena != null) {
            ARENAS.put(arena.getName(), arena);
        }

    }

    @Nullable
    public ParkourArena getArenaAtLocation(IntLocation location, LocationCollectionProvider provider, LocationType type) {

        CacheKey cacheKey = new CacheKey(location, type);
        if (locationLookupCache.containsKey(cacheKey)) {
            return locationLookupCache.get(cacheKey);
        }

        Map.Entry<String, ParkourArena> result = ARENAS.entrySet().stream()
                .filter((entry) -> provider.getLocation(entry.getValue()).contains(location))
                .findFirst().orElse(null);

        ParkourArena found = result != null ? result.getValue() : null;
        locationLookupCache.put(cacheKey, found);
        return found;
    }

    public interface LocationCollectionProvider {
        Collection<IntLocation> getLocation(ParkourArena arena);
    }

    public enum LocationType {
        START_LOCATION,
        CHECKPOINT_LOCATION,
        END_LOCATION
    }

    private static class CacheKey {

        public final IntLocation location;
        public final LocationType type;

        private CacheKey(IntLocation location, LocationType type) {
            this.location = location;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheKey cacheKey = (CacheKey) o;
            return Objects.equals(location, cacheKey.location) && type == cacheKey.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(location, type);
        }
    }

}
