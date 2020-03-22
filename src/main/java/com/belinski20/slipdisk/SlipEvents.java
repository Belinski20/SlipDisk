package com.belinski20.slipdisk;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class SlipEvents implements Listener {

    private SlipUtils slipUtils;
    private ProfileUtils profileUtils;

    SlipEvents(SlipUtils slipUtils, ProfileUtils profileUtils)
    {
        this.slipUtils = slipUtils;
        this.profileUtils = profileUtils;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws IOException
    {
        profileUtils.createPlayerFile(event.getPlayer());
        String userID = profileUtils.getUserID(event.getPlayer().getUniqueId());
        slipUtils.createUserSlipFile(userID);
        if(profileUtils.resetInformation(event.getPlayer()))
        {
            slipUtils.updateSlipData(event.getPlayer().getUniqueId(), userID);
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) throws IOException {
        Player player = event.getPlayer();
        if(!event.getLine(0).equalsIgnoreCase("#slip"))
            return;
        String userID = profileUtils.getUserID(player.getUniqueId());
        if(!slipUtils.hasMaxSlips(userID))
        {
            slipUtils.addSlip(profileUtils.getUserID(player.getUniqueId()), player.getLocation(), event.getBlock().getLocation());
            event.setLine(0, ChatColor.DARK_RED + "Slip");
            event.setLine(1, userID);
            profileUtils.increaseSlipAmount();
            player.sendMessage(ChatColor.GOLD + "Created a new slip gate!");
            System.out.println(ChatColor.GOLD + player.getName() +  " created a new slip gate!");
        }
        else
        {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Your slip already has " + slipUtils.getMaxSlip(userID) + " endpoints. Break one first!");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) throws IOException {
        if(event.getBlock().getState() instanceof Sign)
        {
            String userID = profileUtils.getUserID(event.getPlayer().getUniqueId());
            if(!((Sign) event.getBlock().getState()).getLine(0).equalsIgnoreCase(ChatColor.DARK_RED + "Slip"))
                return;
            if(!slipUtils.contains(userID, (Sign)event.getBlock().getState()))
            {
                event.setCancelled(true);
            }
            else
            {
                slipUtils.removeSlip(event.getBlock().getLocation(), userID);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if(!(event.getAction() == Action.RIGHT_CLICK_BLOCK))
            return;

        if(!(event.getClickedBlock().getState() instanceof Sign))
            return;

        Sign sign = (Sign)event.getClickedBlock().getState();

        String userID = slipUtils.getUserIDFromSign(sign);

        event.getPlayer().teleport(slipUtils.nextTeleport(userID, event.getClickedBlock().getLocation()));
    }

    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event)
    {

    }
}
