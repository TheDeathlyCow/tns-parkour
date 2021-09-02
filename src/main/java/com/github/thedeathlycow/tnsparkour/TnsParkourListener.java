package com.github.thedeathlycow.tnsparkour;

import com.github.thedeathlycow.tnsparkour.commands.ParkourSubcommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
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
    public void onLogout(PlayerQuitEvent event) {
        inProgressRuns.remove(event.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (inProgressRuns.containsKey(player)) {
            ParkourRun run = inProgressRuns.get(player);
            boolean died = !run.fall();

            if (died) {
                run = inProgressRuns.remove(player);
                run.endRun();
                player.sendMessage(ChatColor.RED + "Sorry, you have failed this run!");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 0.7f);
            }
        }
    }

    @EventHandler
    public void activateTrigger(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.PHYSICAL)) {
            Block steppedOn = event.getClickedBlock();
            Location intrLocation = TnsParkour.getIntLocation(steppedOn.getLocation());
            ParkourArena startArena = PLUGIN.getArenaManager().getArenaOfStartLocation(intrLocation);
            ParkourArena endArena = PLUGIN.getArenaManager().getArenaOfEndLocation(intrLocation);
            ParkourArena entered = PLUGIN.getArenaManager().getArenaOfEntrace(intrLocation);
            ParkourArena checkPointed = PLUGIN.getArenaManager().getArenaOfCheckpoint(intrLocation);
            if (startArena != null) {
                startRun(event.getPlayer(), startArena);
            }
            if (endArena != null) {
                endRun(event.getPlayer());
            }
            if (entered != null) {
                event.getPlayer().setBedSpawnLocation(intrLocation.add(0, 2, 0), true);
                enter(event.getPlayer(), entered);
            }
            if (checkPointed != null) {
                checkPoint(event.getPlayer(), intrLocation);
            }

            Location hub = PLUGIN.getHubLocation();
            if (intrLocation.equals(hub)) {
                event.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                inProgressRuns.remove(event.getPlayer());
            }
        }
    }

    private void checkPoint(Player player, Location location) {
        ParkourRun run = inProgressRuns.get(player);
        if (run != null) {
            run.checkpoint(location);
        }
    }

    private void enter(Player player, ParkourArena arena) {
        ParkourSubcommand.JOIN.executor.execute(player, arena.getName());
    }

    private void startRun(Player player, ParkourArena arena) {
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
            try {
                PLUGIN.getArenaManager().saveArenas();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
