package com.github.thedeathlycow.tnsparkour;

import com.github.thedeathlycow.tnsparkour.arena.ArenaManager;
import com.github.thedeathlycow.tnsparkour.arena.IntLocation;
import com.github.thedeathlycow.tnsparkour.arena.ParkourArena;
import com.github.thedeathlycow.tnsparkour.events.OneParamEventDelegate;
import com.github.thedeathlycow.tnsparkour.events.TwoParamEventDelegate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class TnsParkourListener implements Listener {

    private final Map<Player, ParkourArena> playerArenas = new HashMap<>();
    private final TwoParamEventDelegate<Player, IntLocation> interactWithParkour = new TwoParamEventDelegate<>();

    public TnsParkourListener() {
        interactWithParkour.register(this::startRun);
        interactWithParkour.register(this::hitCheckpoint);
        interactWithParkour.register(this::reachEnd);
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event) {
        endRun(event.getPlayer(), false);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        returnToCheckpoint(player);
    }

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.PHYSICAL)) {
            return;
        }
        Bukkit.getLogger().log(Level.INFO, "Interact event fired");
        Block interactedWith = event.getClickedBlock();
        if (interactedWith == null) {
            return;
        }

        Player player = event.getPlayer();
        IntLocation location = new IntLocation(interactedWith.getLocation());

        interactWithParkour.execute(player, location);
    }

    private void reachEnd(Player player, IntLocation location) {
        Bukkit.getLogger().log(Level.INFO, "reach end event fired");
        if (!playerArenas.containsKey(player)) {
            return;
        }

        ArenaManager arenaManager = TnsParkour.getInstance().getArenaManager();
        ParkourArena arena = arenaManager
                .getArenaAtLocation(location,
                        (a) -> Collections.singleton(a.getEndLocation()),
                        ArenaManager.LocationType.END_LOCATION);

        if (arena == null) {
            return;
        }

        endRun(player, true);
    }

    private void hitCheckpoint(Player player, IntLocation location) {
        Bukkit.getLogger().log(Level.INFO, "checkpoint event fired");
        if (!playerArenas.containsKey(player)) {
            return;
        }

        ArenaManager arenaManager = TnsParkour.getInstance().getArenaManager();
        ParkourArena arena = arenaManager
                .getArenaAtLocation(location,
                        ParkourArena::getCheckPoints,
                        ArenaManager.LocationType.CHECKPOINT_LOCATION);

        if (arena == null) {
            return;
        }
        arena.getRunManager().checkpoint(player, location);

        player.sendMessage(ChatColor.GREEN + "Checkpointed!");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
    }

    private void startRun(Player player, IntLocation location) {
        Bukkit.getLogger().log(Level.INFO, "start event fired");
        ArenaManager arenaManager = TnsParkour.getInstance().getArenaManager();
        ParkourArena arena = arenaManager
                .getArenaAtLocation(location,
                        (a) -> Collections.singleton(a.getStartLocation()),
                        ArenaManager.LocationType.START_LOCATION);

        if (arena == null) {
            return;
        }

        playerArenas.put(player, arena);
        arena.getRunManager().startRun(player);

        String authors = arena.getAuthorsJoined();
        String startMsg = String.format("Starting run of arena %s, by %s", arena.getName(), authors);
        player.sendMessage(ChatColor.AQUA + startMsg);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
    }

    private void returnToCheckpoint(Player player) {
        if (playerArenas.containsKey(player)) {
            ParkourArena currArena = playerArenas.get(player);
            currArena.getRunManager().fall(player);
            player.sendMessage(ChatColor.GREEN + "Returned to last checkpoint!");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
        }
    }

    private void endRun(Player player, boolean recordScore) {
        if (playerArenas.containsKey(player)) {
            ParkourArena currArena = playerArenas.remove(player);
            int time = currArena.getRunManager().completeRun(player, recordScore);

            if (recordScore) {
                String msg = String.format("Run completed in %.3f seconds!", time / 1000.0);
                player.sendMessage(ChatColor.GREEN + msg);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
            }
        }
    }

}
