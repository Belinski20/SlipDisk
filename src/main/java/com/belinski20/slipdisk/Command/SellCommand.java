package com.belinski20.slipdisk.Command;

import com.belinski20.slipdisk.Profile;
import com.belinski20.slipdisk.Slipdisk;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;


public class SellCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if (!(sender instanceof Player)) {
            if(args.length == 0)
                return false;

            if(Slipdisk.s.getServer().getPlayer(args[0]) == null)
            {
                sender.sendMessage(ChatColor.RED + args[0] + " is not a valid online player");
                return true;
            }

            Player player = Slipdisk.s.getServer().getPlayer(args[0]);
            Profile profile = Slipdisk.s.profileUtils.getProfile(player.getUniqueId());

            if(profile == null)
            {
                sender.sendMessage(ChatColor.BLUE + player.getName() + " needs to setup slips prior to trying to sell anything");
                player.sendMessage(ChatColor.BLUE + "You need to setup slips prior to selling any");
                return true;
            }

            if(profile.getBoughtAmount() <= 0)
            {
                sender.sendMessage(ChatColor.BLUE + player.getName() + " tried to sell a non-existing slip to the Console");
                player.sendMessage(ChatColor.BLUE + "You need to have a bought slip to sell");
                return true;
            }

            int slipTotal = profile.getSlips().size();
            int slipRankAmount = profile.getRankAmount();

            slipTotal -= slipRankAmount;

            if(slipTotal >= profile.getBoughtAmount())
            {
                sender.sendMessage(ChatColor.BLUE + player.getName() + " tried to sell a slip which is being used");
                player.sendMessage(ChatColor.BLUE + "All your slips have been placed, please break a slip prior to selling it");
                return true;
            }

            profile.decrementBoughtAmount(1);

            // Joke Message
            sender.sendMessage(ChatColor.BLUE + player.getName() + " sold a slip to the Console");
            player.sendMessage(ChatColor.BLUE + "You sold a slip");

            return true;
        }

        Player player = (Player)sender;

        Profile profile = Slipdisk.s.profileUtils.getProfile(player.getUniqueId());

        if(profile == null)
        {
            sender.sendMessage(ChatColor.BLUE + "You need to have a bought slip to sell");
            return true;
        }

        if(profile.getBoughtAmount() <= 0)
        {
            sender.sendMessage(ChatColor.BLUE + "You need to have a bought slip to sell");
            return true;
        }

        int slipTotal = profile.getSlips().size();
        int slipRankAmount = profile.getRankAmount();

        slipTotal -= slipRankAmount;

        if(slipTotal >= profile.getBoughtAmount())
        {
            sender.sendMessage(ChatColor.BLUE + "All your slips have been placed, please break a slip prior to selling it");
            return true;
        }

        profile.decrementBoughtAmount(1);

        sender.sendMessage(ChatColor.BLUE + "You sold a slip");
        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
