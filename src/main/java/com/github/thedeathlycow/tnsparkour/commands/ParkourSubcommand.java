package com.github.thedeathlycow.tnsparkour.commands;

import com.github.thedeathlycow.tnsparkour.ParkourArena;
import com.github.thedeathlycow.tnsparkour.TnsParkour;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum ParkourSubcommand {

    DEFINE(ParkourSubcommand::defineArena),
    SETSTARTLOCATION(ParkourSubcommand::setStartLocation),
    SETENDLOCATION(ParkourSubcommand::setEndLocation),
    SETENTRANCE(ParkourSubcommand::setEntrance),
    DELETE(ParkourSubcommand::removeArena),
    ADDCHECKPOINT(ParkourSubcommand::addCheckpoint),
    REMOVECHECKPOINT(ParkourSubcommand::removeCheckpoint),
    JOIN(ParkourSubcommand::joinPlayer);

    public final Executor executor;
    public static TnsParkour PLUGIN;

    ParkourSubcommand(Executor executor) {
        this.executor = executor;
    }

    public interface Executor {
        boolean execute(CommandSender sender, String arenaName);
    }

    private static boolean defineArena(CommandSender sender, String arenaName) {

        if (!PLUGIN.getArenaManager().arenaExists(arenaName)) {
            ParkourArena arena = new ParkourArena(arenaName);
            PLUGIN.getArenaManager().addArena(arena);

            sender.sendMessage(ChatColor.GREEN + "Successfully created arena '" + arenaName + "'! " +
                    "You must now define the start and end points of the arena with /tnsparkour "
                    + arenaName + " setStartLocation and /tnsparkour" + arenaName + " setEndLocation.");

            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            } else {
                sender.sendMessage("Note that in order to define start and end locations, you must be logged in!");
            }

            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Error: The arena '" + arenaName + "' already exists!");
            return false;
        }
    }

    private static boolean removeCheckpoint(CommandSender sender, String arenaName) {
        if (PLUGIN.getArenaManager().getArena(arenaName) != null && sender instanceof Player) {
            Player player = (Player) sender;

            ParkourArena arena = PLUGIN.getArenaManager().getArena(arenaName);

            Location loc = player.getLocation();
            boolean removed = arena.removeCheckpoint(loc);

            if (removed) {
                player.sendMessage(ChatColor.GREEN + "Successfully removed the checkpoint from the arena '"
                        + arenaName + "' at your current position!");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            } else {
                player.sendMessage(ChatColor.RED + "There is no checkpoint that belongs to the arena '"
                        + arenaName + "' at your current position!");
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1, 0.7f);
            }

            return true;
        }
        if (PLUGIN.getArenaManager().getArena(arenaName) == null) {
            sender.sendMessage(ChatColor.RED + "Error: Specified arena '" + arenaName
                    + "' does not exist!");
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be logged in to define locations!");
        }
        return false;
    }

    private static boolean addCheckpoint(CommandSender sender, String arenaName) {
        if (PLUGIN.getArenaManager().getArena(arenaName) != null && sender instanceof Player) {
            Player player = (Player) sender;

            ParkourArena arena = PLUGIN.getArenaManager().getArena(arenaName);

            Location loc = player.getLocation();
            arena.addCheckpoint(loc);

            player.sendMessage(ChatColor.GREEN + "Successfully added a checkpoint to arena '"
                    + arenaName + "' at your current position!");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            return true;
        }
        if (PLUGIN.getArenaManager().getArena(arenaName) == null) {
            sender.sendMessage(ChatColor.RED + "Error: Specified arena '" + arenaName
                    + "' does not exist!");
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be logged in to define locations!");
        }
        return false;
    }

    private static boolean setStartLocation(CommandSender sender, String arenaName) {
        if (PLUGIN.getArenaManager().getArena(arenaName) != null && sender instanceof Player) {
            Player player = (Player) sender;

            ParkourArena arena = PLUGIN.getArenaManager().getArena(arenaName);

            Location loc = player.getLocation();
            arena.setStartLocation(loc);

            player.sendMessage(ChatColor.GREEN + "Successfully set the start location of arena '"
                    + arenaName + "' to your current position!");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            return true;
        }

        if (PLUGIN.getArenaManager().getArena(arenaName) == null) {
            sender.sendMessage(ChatColor.RED + "Error: Specified arena '" + arenaName
                    + "' does not exist!");
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be logged in to define locations!");
        }

        return false;
    }

    private static boolean setEndLocation(CommandSender sender, String arenaName) {
        if (PLUGIN.getArenaManager().getArena(arenaName) != null && sender instanceof Player) {
            Player player = (Player) sender;

            ParkourArena arena = PLUGIN.getArenaManager().getArena(arenaName);
            Location loc = player.getLocation();
            arena.setEndLocation(loc);

            player.sendMessage(ChatColor.GREEN + "Successfully set the end location of arena '"
                    + arenaName + "' to your current position!");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            return true;
        }

        if (PLUGIN.getArenaManager().getArena(arenaName) == null) {
            sender.sendMessage(ChatColor.RED + "Error: Specified arena '" + arenaName
                    + "' does not exist!");
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be logged in to define locations!");
        }

        return false;
    }

    private static boolean setEntrance(CommandSender sender, String arenaName) {
        ParkourArena arena = PLUGIN.getArenaManager().getArena(arenaName);

        if (arena != null && sender instanceof Player) {
            Player player = (Player) sender;
            Location loc = player.getLocation();
            arena.setEntrance(loc);
            player.sendMessage(ChatColor.GREEN + "Successfully set the entrance of arena '"
                    + arenaName + "' to your current position!");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            return true;
        }

        if (PLUGIN.getArenaManager().getArena(arenaName) == null) {
            sender.sendMessage(ChatColor.RED + "Error: Specified arena '" + arenaName
                    + "' does not exist!");
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be logged in to define locations!");
        }

        return false;
    }

    private static boolean removeArena(CommandSender sender, String arenaName) {
        ParkourArena arena = PLUGIN.getArenaManager().remove(arenaName);
        if (arena != null) {
            sender.sendMessage(ChatColor.GREEN + "Sucessfully removed arena '" + arenaName + "'!");
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Error: Unable to remove arena '" + arenaName + "'.");
            return false;
        }
    }

    public static boolean setHub(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location loc = TnsParkour.getIntLocation(player.getLocation());
            PLUGIN.setHubLocation(loc);
            player.sendMessage(ChatColor.GREEN + "Successfully set the hub of the server "
                    + " to your current position!");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
            return true;
        }
        sender.sendMessage(ChatColor.RED + "You must be logged in to define locations!");
        return false;
    }

    private static boolean joinPlayer(CommandSender sender, String arenaName) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            ParkourArena arena = PLUGIN.getArenaManager().getArena(arenaName);
            arena.onPlayerJoin(player);
            player.sendMessage(ChatColor.GREEN + "You have joined the parkour arena '" + arena.getName() + "'!");
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Error: Only players may join parkour arenas!");
            return false;
        }
    }
}
