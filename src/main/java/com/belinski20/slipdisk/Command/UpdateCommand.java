package com.belinski20.slipdisk.Command;

import com.belinski20.slipdisk.Profile;
import com.belinski20.slipdisk.Slipdisk;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class UpdateCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if(args.length == 0)
            return false;

        sender.sendMessage(ChatColor.GREEN + "Trying to update: " + args[0]);

        Player player = Slipdisk.s.getServer().getPlayer(args[0]);

        if(player == null)
        {
            sender.sendMessage(args[0] + " does not exist on server");
            return true;
        }

        Profile profile = Slipdisk.s.profileUtils.getProfile(player.getUniqueId());

        if(profile == null)
        {
            sender.sendMessage(args[0] + " has not placed a slip yet.");
            return true;
        }

        String rank = Slipdisk.s.permissionIntegration.getUserRank(player);
        int total = Slipdisk.s.permissionIntegration.getSlipTotal(rank);

        if(profile.getRankAmount() == total)
        {
            sender.sendMessage(args[0] + " does not need to be updated");
            return true;
        }

        int index = Slipdisk.s.profileList.indexOf(profile);

        Slipdisk.s.profileList.get(index).setRankAmount(total);

        sender.sendMessage(args[0] + " has been updated");

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
