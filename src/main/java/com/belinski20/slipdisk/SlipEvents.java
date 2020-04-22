package com.belinski20.slipdisk;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
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

class SlipEvents implements Listener {

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

    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws IOException
    {
        Player player = event.getPlayer();
        String rank = permissionIntegration.getUserRank(player);
        int slipTotal = permissionIntegration.getSlipTotal(rank);
        if(slipTotal != -1)
        {
            profileUtils.createPlayerFile(player, rank, slipTotal);
            String userID = profileUtils.getUserID(player.getUniqueId());
            slipUtils.createUserSlipFile(userID, rank);
            if(profileUtils.resetInformation(player))
            {
                slipUtils.updateSlipData(userID, profileUtils.generateUserID(player));
            }
            slipUtils.checkSlipsExist(userID);
            return;
        }
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Player File and Slip File not Updated or Made for " + player.getName());
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
            player.sendMessage(ChatColor.GOLD + "Created a New Slip Gate (" + slipUtils.getCurrentSlipAmount(userID) + " of " + slipUtils.getMaxSlip(userID) + ")");
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + player.getName() +  " created a new slip gate!");
        }
        else
        {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Your slip already has " + slipUtils.getCurrentSlipAmount(userID) + " of " + slipUtils.getMaxSlip(userID) + " endpoints. Break one first!");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) throws IOException {
        if(event.getBlock().getState() instanceof Sign)
        {
            Sign sign = (Sign)event.getBlock().getState();
            String userID = sign.getLine(1);
            if(!sign.getLine(0).equalsIgnoreCase(ChatColor.DARK_RED + "Slip"))
                return;
            if(slipUtils.fileContains(userID, (Sign)event.getBlock().getState()))
            {
                removeSlip(userID, event);
                return;
            }
        }
    }


    /**
     * Removes the slip for a player
     * @param userID
     * @param event
     * @throws IOException
     */
    private void removeSlip(String userID, BlockBreakEvent event) throws IOException {
        if(slipUtils.fileContains(userID, (Sign)event.getBlock().getState()))
            slipUtils.removeSlip(event.getBlock().getLocation(), userID);
        event.getPlayer().sendMessage(ChatColor.GREEN + "Slip Un-Registered From SlipDisk!");
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

        if(slipUtils.fileContains(userID, sign))
            event.getPlayer().teleport(slipUtils.nextTeleport(userID, event.getClickedBlock().getLocation()));
        else
            event.getPlayer().sendMessage(ChatColor.RED + "This slip is not registered in the new version of SlipDisk");
    }

    @EventHandler
    public void onPhysicsSignBreak(BlockPhysicsEvent event) throws IOException {
        WallSign wallSign = null;
        Sign sign;
        BlockFace attached;
        Block block = event.getBlock();
        Block sourceBlock = event.getSourceBlock();
        if(sourceBlock.getType() != Material.AIR)
            return;

        if(!(block.getState() instanceof Sign))
            return;

        if(block.getState().getBlockData() instanceof WallSign)
        {
            wallSign = (WallSign)block.getState().getBlockData();
        }
        sign = (Sign)block.getState();

        if(sourceBlock.getType() == Material.AIR)
        {
            if(wallSign != null)
            {
                attached = wallSign.getFacing().getOppositeFace();
                Block attachedTo = block.getRelative(attached);
                if(attachedTo.getType() != Material.AIR)
                    return;
            }
            else
            {
                Block under = block.getRelative(BlockFace.DOWN);
                if(under.getType() != Material.AIR)
                    return;
            }
        }

        if(!sign.getLine(0).equals(ChatColor.DARK_RED + "Slip"))
            return;

        String userID = sign.getLine(1);

        if(slipUtils.contains(userID, sign))
        {
            slipUtils.removeSlip(event.getBlock().getLocation(), sign.getLine(1));
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "A Slip For " +  userID + " Broke Due To Gravity.");
        }
    }
}
