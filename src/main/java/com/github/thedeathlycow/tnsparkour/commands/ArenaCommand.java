package com.github.thedeathlycow.tnsparkour.commands;

import com.github.thedeathlycow.tnsparkour.ParkourArena;
import com.github.thedeathlycow.tnsparkour.TnsParkour;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class ArenaCommand implements CommandExecutor {

    private final TnsParkour PLUGIN;

    public ArenaCommand(TnsParkour plugin) {
        this.PLUGIN = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length > 0) {
            String subCommand = args[0];

            if (args.length == 1) {
                if (subCommand.equalsIgnoreCase("save")) {
                    try {
                        PLUGIN.getArenaManager().saveArenas();
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        sender.sendMessage(ChatColor.RED + "Error saving arenas, please contact a server admin for help.");
                        return false;
                    }
                }
            }

            String arenaName = args[1];

            if (subCommand.equalsIgnoreCase("define")) {
                return defineArena(sender, arenaName);
            } else if (subCommand.equalsIgnoreCase("setStartLocation")) {
                return setStartLocation(sender, arenaName);
            } else if (subCommand.equalsIgnoreCase("setEndLocation")) {
                return setEndLocation(sender, arenaName);
            } else if (subCommand.equalsIgnoreCase("delete")) {
                return removeArena(sender, arenaName);
            } else if (subCommand.equalsIgnoreCase("join")) {
                return joinPlayer(sender, arenaName);
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Error: Insufficient arguments!");
            return false;
        }

        return true;
    }

    private boolean joinPlayer(CommandSender sender, String arenaName) {

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

    private boolean removeArena(CommandSender sender, String arenaName) {
        ParkourArena arena = PLUGIN.getArenaManager().remove(arenaName);
        if (arena != null) {
            sender.sendMessage(ChatColor.GREEN + "Sucessfully removed arena '" + arenaName + "'!");
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Error: Unable to remove arena '" + arenaName + "'.");
            return false;
        }
    }

    private boolean setEndLocation(CommandSender sender, String arenaName) {
        if (PLUGIN.getArenaManager().getArena(arenaName) != null && sender instanceof Player) {
            Player player = (Player) sender;

            ParkourArena arena = PLUGIN.getArenaManager().getArena(arenaName);
            Location loc = player.getLocation();
            arena.setEndLocation(TnsParkour.getIntLocation(loc));

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

    private boolean setStartLocation(CommandSender sender, String arenaName) {
        if (PLUGIN.getArenaManager().getArena(arenaName) != null && sender instanceof Player) {
            Player player = (Player) sender;

            ParkourArena arena = PLUGIN.getArenaManager().getArena(arenaName);

            Location loc = player.getLocation();
            arena.setStartLocation(TnsParkour.getIntLocation(loc));

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

    private boolean defineArena(CommandSender sender, String arenaName) {

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
}
