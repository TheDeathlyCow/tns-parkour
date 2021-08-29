package com.github.thedeathlycow.tnsparkour;

import com.github.thedeathlycow.tnsparkour.helpers.PrimitiveSizes;
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
import java.math.BigInteger;
import java.util.*;

public class ParkourArena {

    private final String NAME;
    private Location entrance;
    private Location startLocation;
    private Location endLocation;
    private final List<Location> checkPoints = new ArrayList<>();
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

    public void setEntrance(Location entrance) {
        this.entrance = TnsParkour.getIntLocation(entrance);
    }

    public boolean isCheckpoint(Location location) {
        return checkPoints.stream()
                .filter(checkPoint -> checkPoint.equals(location))
                .findFirst().orElse(null) != null;
    }

    public void addCheckpoint(Location location) {
        checkPoints.add(TnsParkour.getIntLocation(location));
    }

    public boolean removeCheckpoint(Location location) {
        return checkPoints.remove(TnsParkour.getIntLocation(location));
    }

    public List<Location> getCheckPoints() {
        return Collections.unmodifiableList(checkPoints);
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = TnsParkour.getIntLocation(startLocation);
    }

    public void setEndLocation(Location endLocation) {
        this.endLocation = TnsParkour.getIntLocation(endLocation);
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

    public Location getEntrance() {
        return entrance;
    }

    public void addScore(String player, Integer score) {
        SCORES.put(player, score);
        Objective objective = SCOREBOARD.getObjective(OBJECTIVE_NAME);
        assert objective != null;
        String order = getSidebarOrder(score);
        objective.getScore(order + player).setScore(0);
    }

    /**
     * Returns a non-rendered prefix that describes the order in which
     * the score should appear in the sidebar.
     *
     * Based on a resource by Nathan Franke <natfra@pm.me>, available
     * at https://www.spigotmc.org/threads/how-to-make-an-all-zeros-sidebar.465666/
     *
     * TODO: Make this work for arbitrarily large numbers.
     *
     * @param score The score to sort the scoreboard by.
     * @return Returns a non-rendered string that describes the order in which
     * the given score should appear on the sidebar.
     */
    public static String getSidebarOrder(int score) {

        StringBuilder builder = new StringBuilder();
        String prefix = "\u00A7";
        char charMask = (0xFFFF);
        int numCharsInPrefix = PrimitiveSizes.sizeof(score) / PrimitiveSizes.sizeof('a');
        for (int i = numCharsInPrefix * 8; i >= 0; i -= 8) {
            builder.append(prefix);
            int toAppend = (score >> i) & charMask;
            builder.append((char)toAppend);
        }

        return builder.toString() + ChatColor.RESET;
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
            Location entrance = Location.deserialize(gson.fromJson(
                    object.get("entrance").getAsJsonObject(),
                    new TypeToken<Map<String, Object>>() {
                    }.getType()
            ));

            arena.setStartLocation(startLoc);
            arena.setEndLocation(endLoc);
            arena.setEntrance(entrance);

            Map<String, Integer> scores = gson.fromJson(
                    object.get("scores").getAsJsonObject(),
                    new TypeToken<Map<String, Integer>>() {
                    }.getType()
            );
            scores.forEach(arena::addScore);

            JsonArray checkPoints = object.get("checkPoints").getAsJsonArray();
            checkPoints.forEach(
                    (jsonElement -> {
                        Map<String, Object> locMap = gson.fromJson(
                                jsonElement.getAsJsonObject(),
                                new TypeToken<Map<String, Object>>() {
                                }.getType()
                        );
                        Location location = Location.deserialize(locMap);
                        arena.addCheckpoint(location);
                    })
            );

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
            jsonObject.add("entrance", context.serialize(
                    src.entrance.serialize(), new TypeToken<Map<String, Object>>() {
                    }.getType()
            ));

            jsonObject.add("scores", context.serialize(
                    src.SCORES, new TypeToken<Map<String, Integer>>() {
                    }.getType()
            ));

            List<Map<String, Object>> checkpoints = new ArrayList<>();
            src.checkPoints.forEach(
                    (location -> checkpoints.add(location.serialize()))
            );
            jsonObject.add("checkPoints", context.serialize(
                    checkpoints, new TypeToken<List<Map<String, Object>>>() {
                    }.getType()
            ));

            return jsonObject;
        }
    }
}
