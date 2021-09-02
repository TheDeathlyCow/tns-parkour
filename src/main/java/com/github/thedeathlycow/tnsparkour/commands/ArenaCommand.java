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
            String subCommandstr = args[0];

            if (args.length == 1) {
                if (subCommandstr.equalsIgnoreCase("save")) {
                    try {
                        PLUGIN.getArenaManager().saveArenas();
                        sender.sendMessage(ChatColor.GREEN + "Successfully saved the current parkour scoreboard!");
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        sender.sendMessage(ChatColor.RED + "Error saving arenas, please contact a server admin for help.");
                        return false;
                    }
                } else if (subCommandstr.equalsIgnoreCase("setHub")) {
                    try {
                        return ParkourSubcommand.setHub(sender);
                    } catch (IOException e) {
                        sender.sendMessage(ChatColor.RED + "Error saving hub location, please contact an administrator.");
                        return false;
                    }
                }
                sender.sendMessage(ChatColor.RED + "Error: Insufficient arguments!");
                return false;
            }

            String arenaName = args[1];

            ParkourSubcommand subCommand;
            try {
                subCommand = ParkourSubcommand.valueOf(subCommandstr.toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage(ChatColor.RED + "Error: '" + subCommandstr + "' is not a valid argument!");
                return false;
            }
            boolean success = subCommand.executor.execute(sender, arenaName);
            try {
                if (success) {
                    PLUGIN.getArenaManager().saveArenas();
                }
            } catch (IOException e) {
                sender.sendMessage(ChatColor.RED + "Error saving parkour arenas, please contact an administrator.");
                return false;
            }
            return success;
        } else {
            sender.sendMessage(ChatColor.RED + "Error: Insufficient arguments!");
            return false;
        }
    }
}
