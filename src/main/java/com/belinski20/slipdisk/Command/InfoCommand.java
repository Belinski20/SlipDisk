package com.belinski20.slipdisk.Command;

import com.belinski20.slipdisk.Profile;
import com.belinski20.slipdisk.SlipInfoGUI;
import com.belinski20.slipdisk.Slipdisk;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class InfoCommand implements TabExecutor {

    private String createBaseMessage(Profile profile)
    {
        String message = "";
        message += "User ID: " + profile.getUserID();
        message += "\nSlips Placed: " + profile.getAmountOfSlips();
        message += "\nTotal Slips: " + profile.getMaxSlipAmount();
        message += "\nRank Slips: " + profile.getRankAmount();
        message += "\nBought Slips: " + profile.getBoughtAmount();
        return message;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player))
            return true;

        if(args.length < 1)
            return false;

        UUID uuid = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
        @SuppressWarnings("deprecation")
        Profile profile = Slipdisk.s.profileUtils.getProfile(uuid);

        if(profile == null)
        {
            sender.sendMessage("Player does not exist or has not placed a slip yet.");
            return true;
        }

        if(args.length == 2)
        {
            if(args[1].equals("gui"))
                new SlipInfoGUI(profile).openGUI((Player)sender);
        }
        else
            sender.sendMessage(createBaseMessage(profile));

        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length == 2)
            return Arrays.asList("gui");
        return null;
    }
}
