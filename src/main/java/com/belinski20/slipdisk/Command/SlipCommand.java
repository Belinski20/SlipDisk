package com.belinski20.slipdisk.Command;

import com.belinski20.slipdisk.Messages;
import com.belinski20.slipdisk.Profile;
import com.belinski20.slipdisk.Slipdisk;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SlipCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player)sender;
        Profile profile = Slipdisk.s.getPlayerProfile(player.getUniqueId());

        switch(strings.length)
        {
            case 0:
                sendPlayerDescription(sender);
                return true;
            case 1:
                if(profile == null)
                {
                    player.sendMessage(Messages.needProfileToUseCommand());
                    return true;
                }
                oneArgument(player, profile, strings);
                return true;
            case 2:
                if(profile == null)
                {
                    player.sendMessage(Messages.needProfileToUseCommand());
                    return true;
                }
                twoArguments(player, profile, strings);
                return true;
        }

        return true;
    }

    private void oneArgument(Player player, Profile profile, String[] arguments)
    {
        switch(arguments[0])
        {

            case "private":
                player.sendMessage(Messages.slipSetPrivate());
                profile.setIsPublic(false);
                return;
            case "public":
                player.sendMessage(Messages.slipSetPublic());
                profile.setIsPublic(true);
                return;
            case "trust":
                player.sendMessage(makeTrustedMemberListString(profile.getTrustedMemberNames()));
                return;
            case "untrust":
                player.sendMessage(Messages.untrustMissingArgument());
                return;
            case "april":
                Slipdisk.s.aprilFools = !Slipdisk.s.aprilFools;
                player.sendMessage(Messages.makeComponent("April Fools Slips set to :" + Slipdisk.s.aprilFools));
                return;
        }
        player.sendMessage(Messages.invalidCommandFormat());
    }

    private void twoArguments(Player player, Profile profile, String[] arguments)
    {
        switch(arguments[0])
        {
            case "trust":

                OfflinePlayer member = Bukkit.getOfflinePlayerIfCached(arguments[1]);
                if(member == null)
                {
                    player.sendMessage(Messages.trustHasNotPlayed(member.getName()));
                    return;
                }
                if(player.getUniqueId().equals(member.getUniqueId()))
                {
                    player.sendMessage(Messages.cannotTrustYourself());
                    return;
                }
                if(profile.isTrusted(member.getUniqueId()))
                {
                    player.sendMessage(Messages.trustAlreadyTrusted(member.getName()));
                }
                else
                {
                    player.sendMessage(Messages.trustAdded(member.getName()));
                    profile.addTrustedMember(member.getUniqueId());
                }
                return;
            case "untrust":
                member = Bukkit.getOfflinePlayerIfCached(arguments[1]);
                if(member == null)
                {
                    player.sendMessage(Messages.untrustRemoveCantFind(member.getName()));
                }
                if(profile.isTrusted(member.getUniqueId()))
                {
                    player.sendMessage(Messages.trustRemoved(member.getName()));
                    profile.removeTrustedMember(member.getUniqueId());
                }
                else
                {
                    player.sendMessage(Messages.playerNotInTrustedList(member.getName()));
                }
                return;
        }
        player.sendMessage(Messages.invalidCommandFormat());
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player))
            return null;
        Player player = (Player)commandSender;
        Profile profile = Slipdisk.s.getPlayerProfile(player.getUniqueId());
        if(strings[0].equals("untrust") && strings.length == 2)
            return profile.getTrustedMemberNames().stream().filter(material -> material.toUpperCase().startsWith(strings[1].toUpperCase())).collect(Collectors.toList());
        if(strings.length == 1 && !player.hasPermission("slipdisk.aprilfools"))
            return Arrays.asList("private", "public", "trust", "untrust");
        if(strings.length == 1 && player.hasPermission("slipdisk.aprilfools"))
            return Arrays.asList("private", "public", "trust", "untrust", "april");
        return null;
    }

    private String makeTrustedMemberListString(List<String> members)
    {
        String list = "[" + members.size() + "] ";
        for(String member : members)
        {
            if(list == "")
                list += member;
            else
                list += ", " + member;
        }
        return list;
    }

    private void sendPlayerDescription(CommandSender sender)
    {
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
    }
}
