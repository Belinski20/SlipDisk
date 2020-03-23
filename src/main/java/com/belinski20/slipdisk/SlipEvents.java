package com.belinski20.slipdisk;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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

public class SlipEvents implements Listener {

    private SlipUtils slipUtils;
    private ProfileUtils profileUtils;
    private PermissionIntegration permissionIntegration;
    private Plugin plugin;

    SlipEvents(SlipUtils slipUtils, ProfileUtils profileUtils, PermissionIntegration permissionIntegration, Plugin plugin)
    {
        this.slipUtils = slipUtils;
        this.profileUtils = profileUtils;
        this.permissionIntegration = permissionIntegration;
        this.plugin = plugin;
    }

    private static final BlockFace[] SIDES = new BlockFace[] {
            BlockFace.UP,
            BlockFace.NORTH,
            BlockFace.SOUTH,
            BlockFace.WEST,
            BlockFace.EAST
    };

    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws IOException
    {
        Player player = event.getPlayer();
        String rank = permissionIntegration.getUserRank(player);
        profileUtils.createPlayerFile(player, rank, permissionIntegration.getSlipTotal(rank));
        String userID = profileUtils.getUserID(event.getPlayer().getUniqueId());
        slipUtils.createUserSlipFile(userID, rank);
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
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + player.getName() +  " created a new slip gate!");
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

        if(!sign.getLine(0).equals(ChatColor.DARK_RED + "Slip"))
            return;

        String userID = slipUtils.getUserIDFromSign(sign);

        event.getPlayer().teleport(slipUtils.nextTeleport(userID, event.getClickedBlock().getLocation()));
    }

    @EventHandler
    public void onSignBreak(BlockBreakEvent event) throws IOException {
        Block block = event.getBlock();
        for(BlockFace side: SIDES)
        {
            Block sideBlock = block.getRelative(side);
            if(sideBlock.getState() instanceof Sign)
            {
                Sign sign = (Sign)sideBlock.getState();
                if(sign.getLine(0).equals(ChatColor.DARK_RED + "Slip"))
                {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPhysicsSignBreak(BlockPhysicsEvent event) throws IOException {
        Block block = event.getBlock();
        if(block.getState() instanceof Sign)
        {
            Sign sign = (Sign)block.getState();
            if(slipUtils.contains(sign.getLine(1), sign))
                slipUtils.removeSlip(event.getBlock().getLocation(), sign.getLine(1));
        }
    }
}
