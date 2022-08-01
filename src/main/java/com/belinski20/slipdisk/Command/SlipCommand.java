package com.belinski20.slipdisk.Command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class SlipCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            return false;
        }
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "Slipdisk is a Spinalcraft-exclusive plugin that allows "
                + "you to create a two-way \"slip\" to teleport between spawn and your base!");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "Each player may have one slip at a time. "
                + "To create a slip, you need to place a sign at each endpoint. On each sign, simply type "
                + ChatColor.RED + "#slip "
                + ChatColor.GOLD + "in the top row, and it will automatically register it in your name. "
                + "Now you and others can use your slip to instantly teleport back and forth!");
        sender.sendMessage("");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
