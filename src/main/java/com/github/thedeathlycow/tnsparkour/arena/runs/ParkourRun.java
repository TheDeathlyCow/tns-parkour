package com.github.thedeathlycow.tnsparkour.arena.runs;

import com.github.thedeathlycow.tnsparkour.arena.IntLocation;
import com.github.thedeathlycow.tnsparkour.arena.ParkourArena;
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
     * Reference to the player running
     */
    private transient final Player runner;

    /**
     * The arena the runner is running.
     */
    private final ParkourArena runningFor;

    /**
     * The time the runner started the run, in milliseconds.
     */
    private long startTime = -1;

    /**
     * The time the runner ended the run, in milliseconds.
     * This value is -1 until the run is completed.
     */
    private long endTime = -1;

    private IntLocation lastCheckpoint;

    /**
     * Starts a run with a runner. Sets the start time
     * to the system's current time, in milliseconds.
     *
     * @param runner     The player running.
     * @param runningFor The arena the player is running for.
     */
    public ParkourRun(Player runner, ParkourArena runningFor) {
        this.runner = runner;
        this.runningFor = runningFor;
        this.checkpoint(runningFor.getStartLocation());
    }

    /**
     * Teleports the player to their last checkpoint,
     * if they have checkpointed.
     *
     * @return Returns true if the player was teleported to their last checkpoint, false otherwise.
     */
    public void fall() {
        if (lastCheckpoint != null) {
            Location worldCheckpointLocation = lastCheckpoint.getAsLocationCentered();
            runner.teleport(worldCheckpointLocation);
        }
    }

    public void checkpoint(IntLocation checkpoint) {
        lastCheckpoint = checkpoint;
        runner.setBedSpawnLocation(lastCheckpoint.getAsLocationCentered(), true);
    }

    public void start() {
        if (!isStarted()) {
            this.startTime = System.currentTimeMillis();
        }
    }

    /**
     * Ends the run at the current time.
     */
    public void end() {
        if (!isCompleted()) {
            this.endTime = System.currentTimeMillis();
        }
    }

    /**
     * Determines whether or not this run has been completed.
     *
     * @return Returns true if the run has been completed, false otherwise.
     */
    public boolean isCompleted() {
        return endTime >= 0;
    }

    public boolean isStarted() {
        return startTime >= 0;
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

    public ParkourArena getArena() {
        return runningFor;
    }

    public Player getRunner() {
        return runner;
    }
}
