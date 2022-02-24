package com.github.thedeathlycow.tnsparkour.arena;

import com.github.thedeathlycow.tnsparkour.TnsParkour;
import com.github.thedeathlycow.tnsparkour.arena.runs.RunManager;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class ParkourArena {

    private static final int MAX_TOP_SCORES = 3;

    private final String NAME;
    private final String[] AUTHORS;
    private final IntLocation startLocation;
    private final IntLocation endLocation;
    private final IntLocation leaderboard;
    private final Set<IntLocation> checkPoints = new HashSet<>();
    private transient final RunManager runManager;

    public ParkourArena(final String name, final String[] authors, final IntLocation start, final IntLocation end, final IntLocation leaderboard) {
        this.NAME = name;
        this.AUTHORS = authors;
        this.startLocation = start;
        this.endLocation = end;
        this.leaderboard = leaderboard;

        Scoreboard scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager())
                .getMainScoreboard();

        this.runManager = new RunManager(scoreboard, this);

        this.refreshLeaderboard();
    }

    public void refreshLeaderboard() {
        Location location = leaderboard.getAsLocationCentered();
        clearLeaderboardStands();

        List<Score> scores = runManager.getScores();
        int numTopScores = Math.min(scores.size(), MAX_TOP_SCORES);

        List<Score> bestScores = scores
                .stream()
                .sorted(Comparator.comparingInt(Score::getScore))
                .collect(Collectors.toList())
                .subList(0, numTopScores);

        String titleName = String.format("%s%s Top %d time(s) for %s%s",
                ChatColor.YELLOW, ChatColor.BOLD, numTopScores, this.NAME, ChatColor.RESET);

        putArmorStand(location, titleName);

        for (Score score : bestScores) {
            location.add(0, -0.25, 0);
            String name = String.format("%s - %.3f seconds\n",
                    score.getEntry(),
                    score.getScore() / 1000.0);
            putArmorStand(location, name);
        }
    }

    public String[] getAuthors() {
        return AUTHORS;
    }

    public String getAuthorsJoined() {
        String[] authors = AUTHORS.clone();
        String authorsStringified;
        String delimiter = ", ";
        if (authors.length == 2) {
            delimiter = " and ";
        } else if (authors.length > 2) {
            authors[authors.length - 1] = "and " + authors[authors.length - 1];
        }
        return String.join(delimiter, authors);
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

            IntLocation leaderboardLoc = gson.fromJson(object.get("leaderboard"), IntLocation.class);

            List<IntLocation> checkPoints = gson.fromJson(
                    object.get("checkPoints").getAsJsonArray(),
                    new TypeToken<List<IntLocation>>() {
                    }.getType()
            );

            ParkourArena arena = new ParkourArena(name, authors, startLoc, endLoc, leaderboardLoc);
            arena.addCheckpoints(checkPoints);
            return arena;
        }
    }

    private void clearLeaderboardStands() {
        Location leaderboardLocation = leaderboard.getAsLocationCentered();

        List<Entity> nearbyArmorStands = Objects.requireNonNull(leaderboardLocation.getWorld())
                .getEntities().stream()
                .filter((Entity e) -> {
                    String tag = getLeaderboardTag();
                    return e.getScoreboardTags().contains(tag)
                            && e.getType().equals(EntityType.ARMOR_STAND);
                })
                .collect(Collectors.toList());

        nearbyArmorStands.forEach(Entity::remove);
    }

    private void putArmorStand(Location location, String name) {
        World world = leaderboard.getWorld();
        ArmorStand stand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
        stand.setInvulnerable(true);
        stand.setMarker(true);
        stand.setInvisible(true);
        stand.addScoreboardTag(this.getLeaderboardTag());

        stand.setCustomName(name);
        stand.setCustomNameVisible(true);

    }

    private String getLeaderboardTag() {
        String tagPrefix = TnsParkour.getInstance().getConfig()
                .getString("leaderboard_tag_prefix");
        return tagPrefix + getName();
    }
}
