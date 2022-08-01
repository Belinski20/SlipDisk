package com.belinski20.slipdisk.Command;

import com.belinski20.slipdisk.Profile;
import com.belinski20.slipdisk.Slipdisk;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

public class ResetCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(args.length == 0)
            return false;

        @SuppressWarnings("deprecation")
        Profile profile = Slipdisk.s.profileUtils.getProfile(Bukkit.getOfflinePlayer(args[0]).getUniqueId());

        if(profile == null)
        {
            sender.sendMessage("Player does not exist");
            return true;
        }

        if(profile.getSlips().size() > 0)
        {
            int size = profile.getSlips().size();

            for(int i = 0; i < size; i++)
            {
                if(profile.getSlips().size() <= i)
                    break;
                Slipdisk.s.slipUtils.removeSlip(profile.getSlips().get(i).getSignLocation());
            }

            profile.clearSlips();
            sender.sendMessage(ChatColor.GREEN + "Slips for " + args[0] + " have been reset!");
        }
        else
            sender.sendMessage(ChatColor.RED + args[0] + " does not have any slips");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
