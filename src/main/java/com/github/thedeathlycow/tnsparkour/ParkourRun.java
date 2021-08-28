package com.github.thedeathlycow.tnsparkour;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Stores information relating to a parkour run,
 * such as the runner's name, their start time,
 * end time, and number of failures.
 */
public class ParkourRun {

    /**
     * The name of the runner.
     */
    private final String runnerName;
    private final Player runner;
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

    private Location lastCheckpoint = null;

    /**
     * Starts a run with a runner. Sets the start time
     * to the system's current time, in milliseconds.
     *
     * @param runner     The player running.
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
     * @param runner     The player running.
     * @param runningFor The arena the player is running for.
     * @param startTime  The time at which the player began running.
     */
    public ParkourRun(Player runner, ParkourArena runningFor, long startTime) {
        this.runner = runner;
        this.runnerName = runner.getName();
        this.runningFor = runningFor;
        this.startTime = startTime;
    }

    /**
     * Adds this run to this run's scoreboard.
     */
    public void addToScoreboard() {
        String entryName = String.format("%s - %.3fs", runnerName, this.getRuntime() / 1000.0);
        runningFor.addScore(entryName, this.getRuntime());
    }

    /**
     * Teleports the player to their last checkpoint,
     * if they have checkpointed.
     *
     * @return Returns true if the player was teleported to their last checkpoint, false otherwise.
     */
    public boolean fall() {
        if (lastCheckpoint != null) {
            runner.teleport(lastCheckpoint);
            runner.sendMessage(ChatColor.GREEN + "Returned to last checkpoint!");
            runner.playSound(lastCheckpoint, Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
            return true;
        }
        return false;
    }

    public void checkpoint(Location location) {
        lastCheckpoint = TnsParkour.getIntLocation(location).add(0.5, 1, 0.5);
        runner.setBedSpawnLocation(lastCheckpoint, true);
        runner.sendMessage(ChatColor.GREEN + "Checkpointed!");
        runner.playSound(runner.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
    }

    /**
     * Ends the run at a specific time.
     * <p>
     * If the end time needs to be used for anything else,
     * this may be the better method to use.
     *
     * @param endTime The UNIX epoch time (in milliseconds) at which the
     *                run ended.
     */
    public void endRun(long endTime) {
        this.endTime = endTime;
    }

    /**
     * Ends the run at the current time.
     */
    public void endRun() {
        endRun(System.currentTimeMillis());
    }

    /**
     * Returns the name of the runner.
     *
     * @return The name of the runner
     */
    public String getRunnerName() {
        return runnerName;
    }

    public ParkourArena getArena() {
        return runningFor;
    }

    /**
     * Determines whether or not this run has been completed.
     *
     * @return Returns true if the run has been completed, false otherwise.
     */
    public boolean isCompleted() {
        return endTime >= 0;
    }

    /**
     * Gets the time it took this run to complete.
     *
     * @return Returns how long it took for this run to complete,
     * in milliseconds. If the run has not been completed, returns -1.
     */
    public int getRuntime() {
        if (this.isCompleted()) {
            return (int) (endTime - startTime);
        } else {
            return -1;
        }
    }

}
