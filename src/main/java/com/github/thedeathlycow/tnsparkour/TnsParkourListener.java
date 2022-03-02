package com.github.thedeathlycow.tnsparkour;

import com.github.thedeathlycow.tnsparkour.arena.ArenaManager;
import com.github.thedeathlycow.tnsparkour.arena.CustomNBTItemTags;
import com.github.thedeathlycow.tnsparkour.arena.IntLocation;
import com.github.thedeathlycow.tnsparkour.arena.ParkourArena;
import com.github.thedeathlycow.tnsparkour.arena.runs.ParkourRun;
import com.github.thedeathlycow.tnsparkour.events.TwoParamEventDelegate;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TnsParkourListener implements Listener {

    private final Map<UUID, ParkourArena> playerArenas = new HashMap<>();
    private final TwoParamEventDelegate<Player, IntLocation> interactWithParkour = new TwoParamEventDelegate<>();

    public TnsParkourListener() {
        interactWithParkour.register(this::startRun);
        interactWithParkour.register(this::hitCheckpoint);
        interactWithParkour.register(this::reachEnd);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (playerArenas.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event) {
        returnToStart(event.getPlayer());
        endRun(event.getPlayer(), false);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        returnToCheckpoint(player);
    }

    @EventHandler
    public void onRightClickItem(PlayerInteractEvent event) {
        if (!(event.getAction().equals(Action.RIGHT_CLICK_AIR)
                || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            return;
        }
        if (!event.hasItem()) {
            return;
        }
        Player player = event.getPlayer();
        if (!playerArenas.containsKey(player.getUniqueId())) {
            return;
        }

        ItemStack item = event.getItem();
        assert item != null;
        ItemMeta meta = item.getItemMeta();
        assert meta != null;

        returnToStartEvent(player, meta);
        fallEvent(player, meta);
    }

    @EventHandler
    public void onRightClickBlock(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        Player player = event.getPlayer();
        Block interactedWith = event.getClickedBlock();
        if (interactedWith == null) {
            return;
        }
        IntLocation location = new IntLocation(interactedWith.getLocation());

        pressExitButton(player, location);
    }

    @EventHandler
    public void onPhysicalInteraction(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.PHYSICAL)) {
            return;
        }
        Block interactedWith = event.getClickedBlock();
        if (interactedWith == null) {
            return;
        }

        Player player = event.getPlayer();
        IntLocation location = new IntLocation(interactedWith.getLocation());

        interactWithParkour.execute(player, location);
    }

    private void pressExitButton(Player player, IntLocation buttonLocation) {
        if (!playerArenas.containsKey(player.getUniqueId())) {
            return;
        }

        ArenaManager arenaManager = TnsParkour.getInstance().getArenaManager();
        ParkourArena arena = arenaManager
                .getArenaAtLocation(buttonLocation,
                        (a) -> Collections.singleton(a.getExitButtonLocation()),
                        ArenaManager.LocationType.EXIT_LOCATION);

        if (arena != null) {
            endRun(player, false);
        }
    }

    private void reachEnd(Player player, IntLocation location) {
        if (!playerArenas.containsKey(player.getUniqueId())) {
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
        if (!playerArenas.containsKey(player.getUniqueId())) {
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
        ArenaManager arenaManager = TnsParkour.getInstance().getArenaManager();
        ParkourArena arena = arenaManager
                .getArenaAtLocation(location,
                        (a) -> Collections.singleton(a.getStartLocation()),
                        ArenaManager.LocationType.START_LOCATION);

        if (arena == null) {
            return;
        }

        playerArenas.put(player.getUniqueId(), arena);
        arena.getRunManager().startRun(player);

        String authors = arena.getAuthorsJoined();
        String startMsg = String.format("Starting run of %s, by %s", arena.getName(), authors);
        player.sendMessage(ChatColor.AQUA + startMsg);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
    }

    private void returnToCheckpoint(Player player) {
        if (playerArenas.containsKey(player.getUniqueId())) {
            ParkourArena currArena = playerArenas.get(player.getUniqueId());
            currArena.getRunManager().fall(player);
            String msg = String.format("Returned to last checkpoint (added %.3f seconds)!",
                    ParkourRun.getTimeToAddOnFall() / 1000.0);
            player.sendMessage(ChatColor.GREEN + msg);
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
        }
    }

    private void returnToStart(Player player) {
        if (playerArenas.containsKey(player.getUniqueId())) {
            ParkourArena arena = playerArenas.get(player.getUniqueId());
            arena.getRunManager().restart(player);

            player.sendMessage(ChatColor.GREEN + "Returned to start!");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
        }
    }

    private void endRun(Player player, boolean recordScore) {
        if (playerArenas.containsKey(player.getUniqueId())) {
            ParkourArena currArena = playerArenas.remove(player.getUniqueId());
            int time = currArena.getRunManager().completeRun(player, recordScore);

            if (recordScore) {
                String msg = String.format("Run completed in %.3f seconds!", time / 1000.0);
                player.sendMessage(ChatColor.GREEN + msg);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
            }
        }
    }

    private void returnToStartEvent(Player player, ItemMeta meta) {
        NamespacedKey key = new NamespacedKey(TnsParkour.getInstance(), CustomNBTItemTags.RETURN_TO_START_TAG);
        if (meta.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
            returnToStart(player);
        }
    }

    private void fallEvent(Player player, ItemMeta meta) {
        NamespacedKey key = new NamespacedKey(TnsParkour.getInstance(), CustomNBTItemTags.FALL_TAG);
        if (meta.getPersistentDataContainer().has(key, PersistentDataType.BYTE)) {
            returnToCheckpoint(player);
        }
    }

}
