package com.github.thedeathlycow.tnsparkour;

import net.md_5.bungee.api.chat.ScoreComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;

public class TnsParkourListener implements Listener {

    private final TnsParkour PLUGIN;
    private final Map<Player, ParkourRun> inProgressRuns;

    public TnsParkourListener(TnsParkour plugin) {
        this.PLUGIN = plugin;
        inProgressRuns = new HashMap<>();
    }

    public void activeTrigger(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.PHYSICAL)) {
            Block steppedOn = event.getClickedBlock();
            assert steppedOn != null;
            Location intrLocation = steppedOn.getLocation();
            ParkourArena startArena = PLUGIN.getArenaManager().getArenaOfStartLocation(intrLocation);
            ParkourArena endArena = PLUGIN.getArenaManager().getArenaOfEndLocation(intrLocation);
            if (startArena != null) {
                startRun(event.getPlayer(), startArena, intrLocation);
            } else if (endArena != null) {
                endRun(event.getPlayer());
            }
        }
    }

    private void startRun(Player player, ParkourArena arena, Location startLoc) {
        ParkourRun run = new ParkourRun(player, arena);
        inProgressRuns.put(player, run);
    }

    private void endRun(Player player) {
        ParkourRun run = inProgressRuns.remove(player);
        if (run != null) {
            run.endRun();
            run.addToScoreboard();
        }
    }

}
