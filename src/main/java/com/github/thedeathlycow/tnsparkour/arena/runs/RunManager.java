package com.github.thedeathlycow.tnsparkour.arena.runs;

import com.github.thedeathlycow.tnsparkour.TnsParkour;
import com.github.thedeathlycow.tnsparkour.arena.IntLocation;
import com.github.thedeathlycow.tnsparkour.arena.ParkourArena;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class RunManager {

    private Objective objective;
    private final ParkourArena arena;
    private final Map<Player, ParkourRun> inProgressRuns = new HashMap<>();

    public RunManager(Scoreboard scoreboard, final ParkourArena arena) {
        this.arena = arena;

        String objectivePrefix = TnsParkour.getInstance()
                .getConfig().getString("run_objective_prefix", "tnsparkour.runs.");
        String objectiveName = objectivePrefix + arena.getName();
        try {
            objective = scoreboard.registerNewObjective(objectiveName, "dummy", objectiveName);
        } catch (IllegalArgumentException ex) {
            objective = scoreboard.getObjective(objectiveName);
            String msg = String.format("Error registering objective: %s", ex.getMessage());
            Bukkit.getLogger().log(Level.WARNING, msg);
        }

    }

    public void checkpoint(Player player, IntLocation location) {
        if (inProgressRuns.containsKey(player)) {
            ParkourRun run = inProgressRuns.get(player);
            run.checkpoint(location);
        }
    }

    public void startRun(Player player) {
        inProgressRuns.remove(player);
        ParkourRun run = new ParkourRun(player, arena);
        inProgressRuns.put(player, run);
        run.start();
    }

    public void fall(Player player) {
        if (inProgressRuns.containsKey(player)) {
            ParkourRun run = inProgressRuns.get(player);
            run.fall();
        }
    }

    public List<Score> getScores() {
        Scoreboard scoreboard = objective.getScoreboard();
        List<Score> scores = new ArrayList<>();
        assert scoreboard != null;
        for (String entry : scoreboard.getEntries()) {
            Score score = objective.getScore(entry);
            if (score.getScore() != 0) {
                scores.add(score);
            }
        }

        return scores;
    }

    public int completeRun(Player player, boolean recordScore) {

        if (!inProgressRuns.containsKey(player)) {
            String msg = String.format("Attempted to end a run on %s for %s, but they were not running that arena.",
                    arena.getName(), player.getName());
            Bukkit.getLogger().log(Level.INFO, msg);
            return -1;
        }

        ParkourRun run = inProgressRuns.remove(player);
        run.end();

        int time = run.getRuntime();
        if (recordScore) {
            Score current = objective.getScore(player.getName());
            if (current.getScore() == 0 || time < current.getScore()) {
                current.setScore(time);
            }
            arena.refreshLeaderboard();
        }

        return time;
    }

    public void completeRun(Player player) {
        completeRun(player, false);
    }

    public Objective getObjective() {
        return objective;
    }
}
