package com.github.thedeathlycow.tnsparkour;

import com.google.gson.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.lang.reflect.Type;

/**
 * Stores information relating to a parkour run,
 * such as the runner's name, their start time,
 * end time, and number of failures.
 *
 */
public class ParkourRun {

    /**
     * The name of the runner.
     */
    private final String runnerName;
    /**
     * The arena the runner is running.
     */
    private final ParkourArena runningFor;
    /**
     * The time the runner started the run, in milliseconds.
     */
    private final long startTime;
    /**
     * The time the runner ended the run, in milliseconds.
     * This value is -1 until the run is completed.
     */
    private long endTime = -1;
    /**
     * The number of failures the runner had during this run.
     */
    private int fails;

    /**
     * Starts a run with a runner. Sets the start time
     * to the system's current time, in milliseconds.
     *
     * @param runner The player running.
     * @param runningFor The arena the player is running for.
     */
    public ParkourRun(Player runner, ParkourArena runningFor) {
        this(runner, runningFor, System.currentTimeMillis());
    }

    /**
     * Starts a run with a runner and a start time.
     * Specifying the start time may lead to greater
     * consistency, though it is optional.
     *
     *
     * @param runner The player running.
     * @param runningFor The arena the player is running for.
     * @param startTime The time at which the player began running.
     */
    public ParkourRun(Player runner, ParkourArena runningFor, long startTime) {
        this.runnerName = runner.getName();
        this.runningFor = runningFor;
        this.startTime = startTime;
    }

    /**
     * Adds this run to the scoreboard objective given.
     *
     * @param objective Objective to add this run to.
     */
    public void addToObjective(Objective objective) {
        String entryName = String.format("%s - %.3fs", runnerName, this.getRuntime() / 1000.0);
        objective.getScore(entryName).setScore(Integer.MAX_VALUE - this.getRuntime());
    }

    public void fail() {
        fails++;
    }

    public void endRun(long endTime) {
        this.endTime = endTime;
    }

    public void endRun() {
        endRun(System.currentTimeMillis());
    }

    public String getRunnerName() {
        return runnerName;
    }

    public boolean isCompleted() {
        return endTime != -1;
    }

    public int getRuntime() {
        if (this.isCompleted()) {
            return (int) (startTime - endTime);
        } else {
            return -1;
        }
    }



    public static class Serializer implements JsonSerializer<ParkourRun> {

        public static final Gson GSON = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeAdapter(ParkourRun.class, new Serializer())
                .create();

        @Override
        public JsonElement serialize(ParkourRun src, Type typeOfSrc, JsonSerializationContext context) {
            if (!src.isCompleted()) {
                return null;
            }
            JsonObject json = new JsonObject();
            json.addProperty("runner", src.runnerName);
            json.addProperty("arena", src.runningFor.getName());
            json.addProperty("startTime", src.startTime);
            json.addProperty("endTime", src.endTime);
            json.addProperty("fails", src.fails);
            return json;
        }
    }

}
