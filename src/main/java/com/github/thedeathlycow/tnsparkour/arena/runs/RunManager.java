package com.github.thedeathlycow.tnsparkour.arena.runs;

import com.github.thedeathlycow.tnsparkour.TnsParkour;
import com.github.thedeathlycow.tnsparkour.arena.ParkourArena;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class RunManager {

    private final Objective objective;
    private final ParkourArena arena;
    private final Map<String, Integer> scores = new HashMap<>();
    private final Map<Player, ParkourRun> inProgressRuns = new HashMap<>();

    public RunManager(Scoreboard scoreboard, final ParkourArena arena) {
        this.arena = arena;

        String objectivePrefix = TnsParkour.getInstance().getConfig().getString("run_objective_prefix", "tnsparkour.runs.");
        String objectiveName = objectivePrefix + arena.getName();
        objective = scoreboard.registerNewObjective(objectiveName, "dummy", objectiveName);

    }

    public void startRun(Player player) {
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

    public void completeRun(Player player, boolean recordScore) {

        if (!inProgressRuns.containsKey(player)) {
            String msg = String.format("Attempted to end a run on %s for %s, but they were not running that arena.",
                    arena.getName(), player.getName());
            Bukkit.getLogger().log(Level.INFO, msg);
            return;
        }

        ParkourRun run = inProgressRuns.remove(player);
        run.end();

        if (recordScore) {
            int time = run.getRuntime();
            Score current = objective.getScore(player.getName());

            if (!current.isScoreSet() || time < current.getScore()) {
                current.setScore(time);
            }
        }
    }

    public void completeRun(Player player) {
        completeRun(player, false);
    }
}
