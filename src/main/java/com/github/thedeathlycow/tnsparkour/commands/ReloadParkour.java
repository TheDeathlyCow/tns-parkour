package com.github.thedeathlycow.tnsparkour.commands;

import com.github.thedeathlycow.tnsparkour.TnsParkour;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadParkour implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        TnsParkour.getInstance().getArenaManager().readArenas();

        return true;
    }
}
