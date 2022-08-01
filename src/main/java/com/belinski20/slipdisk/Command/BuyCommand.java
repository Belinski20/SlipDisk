package com.belinski20.slipdisk.Command;

import com.belinski20.slipdisk.Slipdisk;
import com.belinski20.slipdisk.Profile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;


public class BuyCommand implements TabExecutor {

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
                sender.sendMessage(ChatColor.BLUE + "A profile needs to be made for " + player.getName() + " prior to buying slips");
                player.sendMessage(ChatColor.BLUE + "You need to setup a slip prior to buying more");
                return true;
            }

            profile.incrementBoughtAmount(1);

            // Joke Message
            sender.sendMessage(ChatColor.BLUE + player.getName() + " bribed the Console for another Slip");
            player.sendMessage(ChatColor.BLUE + "You bought an additional Slip");

            return true;
        }

        Player player = (Player)sender;

        Profile profile = Slipdisk.s.profileUtils.getProfile(player.getUniqueId());

        if(profile == null)
        {
            sender.sendMessage("You need to setup a slip prior to buying more");
            return true;
        }

        profile.incrementBoughtAmount(1);
        sender.sendMessage(ChatColor.BLUE + "You bought an additional Slip");
        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
