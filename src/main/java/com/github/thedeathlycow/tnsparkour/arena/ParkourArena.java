package com.github.thedeathlycow.tnsparkour.arena;

import com.github.thedeathlycow.tnsparkour.arena.runs.RunManager;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.Type;
import java.util.*;

public class ParkourArena {

    private final String NAME;
    private final String[] AUTHORS;
    private final IntLocation startLocation;
    private final IntLocation endLocation;
    private final Set<IntLocation> checkPoints = new HashSet<>();
    private transient final RunManager runManager;

    public ParkourArena(final String name, final String[] authors, final IntLocation start, final IntLocation end) {
        this.NAME = name;
        this.AUTHORS = authors;
        this.startLocation = start;
        this.endLocation = end;

        Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager())
                .getMainScoreboard();

        this.runManager = new RunManager(scoreboard, this);
    }

    public boolean isCheckpoint(IntLocation location) {
        return checkPoints.contains(location);
    }

    public void addCheckpoint(IntLocation location) {
        checkPoints.add(location);
    }

    public void addCheckpoints(Collection<? extends IntLocation> location) {
        checkPoints.addAll(location);
    }

    public boolean removeCheckpoint(IntLocation location) {
        return checkPoints.remove(location);
    }

    public Collection<IntLocation> getCheckPoints() {
        return Collections.unmodifiableSet(checkPoints);
    }

    public String getName() {
        return NAME;
    }

    public IntLocation getStartLocation() {
        return startLocation;
    }

    public IntLocation getEndLocation() {
        return endLocation;
    }

    public final RunManager getRunManager() {
        return runManager;
    }

    public static class Deserializer implements JsonDeserializer<ParkourArena> {

        @Override
        public ParkourArena deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(IntLocation.class, new IntLocation.Deserializer())
                    .create();
            JsonObject object = json.getAsJsonObject();

            String name = object.get("name").getAsString();

            String[] authors = gson.fromJson(
                    object.get("authors").getAsJsonArray(),
                    String[].class
            );

            IntLocation startLoc = gson.fromJson(object.get("start").getAsJsonObject(), IntLocation.class);

            IntLocation endLoc = gson.fromJson(object.get("end"), IntLocation.class);

            List<IntLocation> checkPoints = gson.fromJson(
                    object.get("checkPoints").getAsJsonArray(),
                    new TypeToken<List<IntLocation>>()
                    {}.getType()
            );

            ParkourArena arena = new ParkourArena(name, authors, startLoc, endLoc);
            arena.addCheckpoints(checkPoints);
            return arena;
        }
    }
}
