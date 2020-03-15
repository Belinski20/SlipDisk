package com.belinski20.slipdisk;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;

public class SlipEvents implements Listener {

    Utils slipUtils;
    Utils profileUtils;

    SlipEvents(Utils slipUtils, Utils profileUtils)
    {
        this.slipUtils = slipUtils;
        this.profileUtils = profileUtils;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws IOException
    {
        ((ProfileUtils)profileUtils).createPlayerFile(event.getPlayer());
        String userID = ((ProfileUtils)profileUtils).getUserID(event.getPlayer().getUniqueId());
        if(((ProfileUtils) profileUtils).resetInformation(event.getPlayer()))
        {
            ((SlipUtils)slipUtils).resetSlipData(event.getPlayer().getUniqueId(), userID);
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event)
    {
        if(!((ProfileUtils)profileUtils).hasMaxSlips(event.getPlayer().getUniqueId()))
        {
            ((SlipUtils)slipUtils).createNewSlip(event.getPlayer());
            ((ProfileUtils)profileUtils).increaseSlipAmount();
        }
        else
        {
            event.getPlayer().sendMessage(ChatColor.RED + "Please remove a slip before making another.");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if(!(event.getAction() == Action.RIGHT_CLICK_BLOCK))
            return;

        if(!(event.getClickedBlock().getState() instanceof Sign))
            return;

        Sign sign = (Sign) event.getClickedBlock().getState();

        Profile profile = ((ProfileUtils) profileUtils).getProfile(sign.getLine(1));
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event)
    {

    }
}
