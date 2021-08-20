package com.github.thedeathlycow.tnsparkour;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ParkourArena {

    private final String NAME;
    private Location startLocation;
    private Location endLocation;
    private final Map<String, Integer> SCORES = new HashMap<>();
    private transient final Scoreboard SCOREBOARD;
    private static transient final String OBJECTIVE_NAME = "runtimes";

    public ParkourArena(String name) {
        this.NAME = name;
        this.SCOREBOARD = Objects.requireNonNull(Bukkit.getScoreboardManager())
                .getNewScoreboard();
        this.SCOREBOARD.registerNewObjective(
                OBJECTIVE_NAME,
                "dummy",
                ChatColor.YELLOW + String.format("=== %s Leaderboard ===", name)
        );
        Objective objective = this.SCOREBOARD.getObjective(OBJECTIVE_NAME);
        assert objective != null;
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
    }

    public String getName() {
        return NAME;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public Scoreboard getScoreboard() {
        return SCOREBOARD;
    }

    public void addScore(String player, Integer score) {
        SCORES.put(player, score);
        System.out.println(String.format("Run: %s : %d", player, score));
        Objective objective = SCOREBOARD.getObjective(OBJECTIVE_NAME);
        assert objective != null;
        objective.getScore(player).setScore(Integer.MAX_VALUE - score);
    }

    public void onPlayerJoin(Player player) {
        player.setScoreboard(this.SCOREBOARD);
    }

    public static class Serializer implements JsonSerializer<ParkourArena>, JsonDeserializer<ParkourArena> {

        @Override
        public ParkourArena deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            Gson gson = new GsonBuilder()
                    .create();
            JsonObject object = json.getAsJsonObject();
            String name = object.get("name").getAsString();
            ParkourArena arena = new ParkourArena(name);
            Location startLoc = Location.deserialize(gson.fromJson(
                    object.get("startLocation").getAsJsonObject(),
                    new TypeToken<Map<String, Object>>() {
                    }.getType()
            ));
            Location endLoc = Location.deserialize(gson.fromJson(
                    object.get("endLocation").getAsJsonObject(),
                    new TypeToken<Map<String, Object>>() {
                    }.getType()
            ));

            arena.setStartLocation(startLoc);
            arena.setEndLocation(endLoc);

            Map<String, Integer> scores = gson.fromJson(
                    object.get("scores").getAsJsonObject(),
                    new TypeToken<Map<String, Integer>>(){
                    }.getType()
            );
            scores.forEach(arena::addScore);

            return arena;
        }

        @Override
        public JsonElement serialize(ParkourArena src, Type typeOfSrc, JsonSerializationContext context) {

            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("name", src.getName());
            jsonObject.add("startLocation", context.serialize(
                    src.startLocation.serialize(), new TypeToken<Map<String, Object>>() {
                    }.getType()
            ));
            jsonObject.add("endLocation", context.serialize(
                    src.endLocation.serialize(), new TypeToken<Map<String, Object>>() {
                    }.getType()
            ));

            System.out.println(src.SCORES);
            jsonObject.add("scores", context.serialize(
                    src.SCORES, new TypeToken<Map<String, Integer>>() {
                    }.getType()
            ));

            return jsonObject;
        }
    }
}
