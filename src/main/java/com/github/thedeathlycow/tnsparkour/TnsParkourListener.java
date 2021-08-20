package com.github.thedeathlycow.tnsparkour;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

public class TnsParkourListener implements Listener {

    private final TnsParkour PLUGIN;
    private final Map<Player, ParkourRun> inProgressRuns;

    public TnsParkourListener(TnsParkour plugin) {
        this.PLUGIN = plugin;
        inProgressRuns = new HashMap<>();
    }

    @EventHandler
    public void activateTrigger(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.PHYSICAL)) {
            Block steppedOn = event.getClickedBlock();
            assert steppedOn != null; // step on me mommy
            Location intrLocation = TnsParkour.getIntLocation(steppedOn.getLocation());
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
        player.sendMessage(ChatColor.AQUA + "Starting run of arena " + arena.getName() + "!");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
    }

    private void endRun(Player player) {
        ParkourRun run = inProgressRuns.remove(player);
        if (run != null) {
            run.endRun();
            run.addToScoreboard();
            player.sendMessage(ChatColor.AQUA + "Completed run of arena " + run.getArena().getName()
                    + String.format(" in %.3f seconds!", run.getRuntime() / 1000.0));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
        }
    }

}
