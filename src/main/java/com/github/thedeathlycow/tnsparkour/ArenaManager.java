package com.github.thedeathlycow.tnsparkour;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Location;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void readArenas(String filePath) throws IOException {

        while (filePath.length() > 0 && filePath.endsWith("/")) {
            filePath = filePath.substring(0, filePath.length() - 1);
        }

        File arenasFile = new File(filePath + ".json");
        if (arenasFile.getParentFile().exists()) {

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(ParkourArena.class, new ParkourArena.Serializer())
                    .create();

            List<ParkourArena> readArenas = gson
                    .fromJson(new FileReader(arenasFile), new TypeToken<List<ParkourArena>>(){
                    }.getType());
            readArenas.forEach(
                    (arena -> ARENAS.put(arena.getName(), arena))
            );

        } else {
            if (arenasFile.getParentFile().mkdirs())
                arenasFile.createNewFile();
        }
    }

    public ParkourArena getArenaOfEndLocation(Location location) {
        Map.Entry<String, ParkourArena> result = ARENAS.entrySet().stream()
                .filter((entry) -> entry.getValue().getEndLocation().equals(location))
                .findFirst().orElse(null);

        return result != null ? result.getValue() : null;
    }

    public ParkourArena getArenaOfStartLocation(Location location) {
        Map.Entry<String, ParkourArena> result = ARENAS.entrySet().stream()
                .filter((entry) -> entry.getValue().getStartLocation().equals(location))
                .findFirst().orElse(null);

        return result != null ? result.getValue() : null;
    }

}
