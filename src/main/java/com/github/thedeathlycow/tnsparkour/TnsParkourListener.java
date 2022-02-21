package com.github.thedeathlycow.tnsparkour;

import com.github.thedeathlycow.tnsparkour.arena.ArenaManager;
import com.github.thedeathlycow.tnsparkour.arena.IntLocation;
import com.github.thedeathlycow.tnsparkour.arena.ParkourArena;
import com.github.thedeathlycow.tnsparkour.events.OneParamEventDelegate;
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

public class TnsParkourListener implements Listener {

    private final Map<Player, ParkourArena> playerArenas = new HashMap<>();
    private final OneParamEventDelegate<Player> interactWithParkour = new OneParamEventDelegate<>();

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

        Block interactedWith = event.getClickedBlock();
        if (interactedWith == null) {
            return;
        }

        Player player = event.getPlayer();
        IntLocation location = new IntLocation(interactedWith.getLocation());

        interactWithParkour.execute(player);
    }

    private void reachEnd(Player player) {

        if (!playerArenas.containsKey(player)) {
            return;
        }

        ArenaManager arenaManager = TnsParkour.getInstance().getArenaManager();
        IntLocation location = new IntLocation(player.getLocation());
        ParkourArena arena = arenaManager
                .getArenaAtLocation(location, (a) -> Collections.singleton(a.getEndLocation()));

        if (arena == null) {
            return;
        }

        endRun(player, true);
    }

    private void hitCheckpoint(Player player) {
        if (!playerArenas.containsKey(player)) {
            return;
        }

        ArenaManager arenaManager = TnsParkour.getInstance().getArenaManager();
        IntLocation location = new IntLocation(player.getLocation());
        ParkourArena arena = arenaManager
                .getArenaAtLocation(location, ParkourArena::getCheckPoints);

        if (arena == null) {
            return;
        }

        returnToCheckpoint(player);
    }

    private void startRun(Player player) {
        ArenaManager arenaManager = TnsParkour.getInstance().getArenaManager();
        IntLocation location = new IntLocation(player.getLocation());
        ParkourArena arena = arenaManager
                .getArenaAtLocation(location, (a) -> Collections.singleton(a.getStartLocation()));

        if (arena == null) {
            return;
        }

        playerArenas.put(player, arena);
        arena.getRunManager().startRun(player);

        player.sendMessage(ChatColor.AQUA + "Starting run of arena " + arena.getName() + "!");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
    }

    private void returnToCheckpoint(Player player) {
        if (playerArenas.containsKey(player)) {
            ParkourArena currArena = playerArenas.get(player);
            currArena.getRunManager().fall(player);
        }
    }

    private void endRun(Player player, boolean recordScore) {
        if (playerArenas.containsKey(player)) {
            ParkourArena currArena = playerArenas.remove(player);
            currArena.getRunManager().completeRun(player, recordScore);
        }
    }

}
