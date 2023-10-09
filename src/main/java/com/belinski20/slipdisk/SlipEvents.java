package com.belinski20.slipdisk;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.List;

class SlipEvents implements Listener {

    private Plugin plugin = Slipdisk.s;

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        String userID = Slipdisk.s.identities.getUserID(player.getUniqueId());

        if(userID == "")
            return;

        if(Slipdisk.s.identities.updateIdentity(player))
        {
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + player.getName() + " had a name change!");
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Updating slips for " + player.getName());
            int updateCount = Slipdisk.s.slipUtils.updateSlips(player.getUniqueId());
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Updated a total of " + updateCount + " slips");
        }

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "update " + player.getName());
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event){
        Player player = event.getPlayer();
        TextComponent textComponent = (TextComponent) event.line(0);
        if(!textComponent.content().equalsIgnoreCase("#slip"))
            return;

        Profile profile = null;
        for(Profile p : Slipdisk.s.profileList)
        {
            if(p.isProfile(player.getUniqueId()))
            {
                profile = p;
                break;
            }
        }

        if(profile == null)
        {
            String truncatedName = Slipdisk.s.profileUtils.truncateUserName(player.getName());
            profile = new Profile(player, Slipdisk.s.permissionIntegration.getSlipTotal(player), 0, truncatedName, Slipdisk.s.profileList.size() + 1);
            Slipdisk.s.profileList.add(profile);
            Slipdisk.s.identities.addIdentity(profile.getUserID(), profile.getUUID());
        }

        //Ghost Slip exists
        for(Slip slip :profile.getSlips())
        {
            if(slip.getSignLocation().getBlock().getState() instanceof Sign)
            {
                Sign sign = (Sign)slip.getSignLocation().getBlock().getState();
                String userID = sign.getSide(Side.FRONT).getLine(1);
                if(sign.getSide(Side.FRONT).getLine(0).equalsIgnoreCase(ChatColor.DARK_RED + "Slip"))
                    if(profile.getUserID().equals(userID))
                        continue;
            }
            profile.removeSlip(slip.getSignLocation());
            plugin.getServer().getConsoleSender().sendMessage(Component.text().content("A slip endpoint with no slip sign has removed from " + profile.getUserID() + " profile.").color(NamedTextColor.RED).build());
        }

        //Fix below messages
        if(profile.canAddSlip())
        {
            event.line(0, Component.text().content("Slip").color(NamedTextColor.DARK_RED).build());
            event.line(1, Component.text().content(profile.getUserID()).build());

            Slip slip = new Slip(player.getLocation(), event.getBlock().getLocation());
            profile.addSlip(slip);

            Sign sign = (Sign)event.getBlock().getState();
            sign.setWaxed(true);
            sign.update();

            player.sendMessage(ChatColor.GOLD + "Created a New Slip Gate (" + profile.getAmountOfSlips() + " of " + profile.getMaxSlipAmount() + ")");
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + player.getName() +  " created a new slip gate!");
        }
        else
        {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Your slip already has " + profile.getAmountOfSlips() + " of " + profile.getMaxSlipAmount() + " endpoints. Break one first!");
        }
    }

    // new code start
    @EventHandler
    public void onForceSignChange(SignChangeEvent event){
        Player player = event.getPlayer();
        if(!player.hasPermission("slipdisk.force"))
            return;
        TextComponent forceSlipComponent = (TextComponent) event.line(0);
        TextComponent usernameComponent = (TextComponent) event.line(1);
        if(!forceSlipComponent.content().equalsIgnoreCase("#forceslip"))
            return;
        if(usernameComponent.content().isEmpty())
            return;

        Profile profile = null;
        for(Profile p : Slipdisk.s.profileList)
        {
            if(Integer.toString(p.getIdNumber()).equalsIgnoreCase(usernameComponent.content()))
            {
                profile = p;
                break;
            }
        }

        if(profile == null)
        {
            player.sendMessage(ChatColor.RED + "No profile setup with slip id " + usernameComponent.content());
            return;
        }

        //Fix below messages
        if(profile.canAddSlip())
        {
            event.setLine(0, ChatColor.DARK_RED + "Slip");

            event.setLine(1, profile.getUserID());

            Slip slip = new Slip(player.getLocation(), event.getBlock().getLocation());
            profile.addSlip(slip);

            Sign sign = (Sign)event.getBlock().getState();
            sign.setWaxed(true);
            sign.update();

            player.sendMessage(ChatColor.GOLD + "Created a New Slip Gate (" + profile.getAmountOfSlips() + " of " + profile.getMaxSlipAmount() + ")");
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + player.getName() +  " created a new slip gate for " + profile.getUserID());
        }
        else
        {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + usernameComponent.content() + " already has the max amount of endpoints. Break one first!");
        }
    }

    // new code end

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        List<Sign> signs = getConnectedSigns(event.getBlock());

        if(!signs.isEmpty())
        {
            for(Sign s : signs)
            {
                Bukkit.getPluginManager().callEvent(new BlockBreakEvent(s.getBlock(), event.getPlayer()));
            }
        }

        if(event.getBlock().getState() instanceof Sign)
        {
            Sign sign = (Sign)event.getBlock().getState();
            String userID = sign.getSide(Side.FRONT).getLine(1);
            if(!sign.getSide(Side.FRONT).getLine(0).equalsIgnoreCase(ChatColor.DARK_RED + "Slip"))
                return;

            UUID uuid = Slipdisk.s.identities.getIdentity(sign.getSide(Side.FRONT).getLine(1));
            Profile profile = Slipdisk.s.profileUtils.getProfile(uuid);

            if(profile == null)
            {
                event.getPlayer().sendMessage(ChatColor.RED + "This slip is not registered in this version of SlipDisk");
                event.getBlock().getWorld().createExplosion(event.getBlock().getLocation(), 0, false);
                //event.getBlock().breakNaturally();
                return;
            }

            if(profile.contains(event.getBlock().getLocation()))
            {
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "A slip for " + userID +" was broken!");
                profile.removeSlip(event.getBlock().getLocation());
                event.getPlayer().sendMessage(ChatColor.GREEN + "Slip Un-Registered From SlipDisk!");
            }
        }

    }

    private List<Sign> getConnectedSigns(Block b)
    {
        List<Sign> signs = new LinkedList<>();
        Set<Block> blocks = new HashSet<>();
        blocks.add(b.getRelative(BlockFace.NORTH));
        blocks.add(b.getRelative(BlockFace.EAST));
        blocks.add(b.getRelative(BlockFace.WEST));
        blocks.add(b.getRelative(BlockFace.SOUTH));

        for(Block bl: blocks)
        {
            if(bl.getState().getBlockData() instanceof WallSign)
            {
                WallSign ws = (WallSign)bl.getState().getBlockData();
                Block baseBlock = bl.getRelative(ws.getFacing().getOppositeFace());
                if(b.equals(baseBlock))
                    signs.add((Sign)bl.getState());
            }
        }

        BlockState bs = b.getRelative(BlockFace.UP).getState();
        if( bs instanceof Sign && !(bs.getBlockData() instanceof WallSign))
            signs.add((Sign)bs);

        return signs;
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

        UUID uuid = Slipdisk.s.identities.getIdentity(sign.getLine(1));
        Profile profile = Slipdisk.s.profileUtils.getProfile(uuid);

        if(profile != null)
        {
            if(profile.contains(sign.getLocation()))
            {
                if(profile.getAmountOfSlips() == 1)
                {
                    event.getPlayer().sendMessage(ChatColor.RED + "This slip has nowhere to go!");
                    return;
                }
                event.getPlayer().teleport(profile.getNextSlip(sign.getLocation()).getPlayerLocation());
                return;
            }
        }

        event.getPlayer().sendMessage(ChatColor.RED + "This slip is not registered in this version of SlipDisk");
        event.getClickedBlock().getWorld().createExplosion(event.getClickedBlock().getLocation(), 0, false);
        //event.getClickedBlock().breakNaturally();
    }

    @EventHandler
    public void onFallingBlock(EntityChangeBlockEvent event) {

        Entity e = event.getEntity();

        if(e instanceof FallingBlock && event.getTo().equals(Material.AIR))
        {
            List<Sign> signs = getConnectedSigns(event.getBlock());

            if(!signs.isEmpty())
            {
                for(Sign s : signs)
                {
                    if(!s.getLine(0).equals(ChatColor.DARK_RED + "Slip"))
                        return;

                    UUID uuid = Slipdisk.s.identities.getIdentity(s.getLine(1));
                    Profile profile = Slipdisk.s.profileUtils.getProfile(uuid);

                    if(profile == null)
                        return;

                    profile.removeSlip(s.getLocation());
                    plugin.getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "A Slip For " +  profile.getUserID() + " broke due To gravity.");
                }
            }
        }

    }

    /*
    @EventHandler
    public void onPhysicsSignBreak(BlockPhysicsEvent event){
        Sign sign = null;
        BlockFace attached;
        Block block = event.getBlock();
        Block sourceBlock = event.getSourceBlock();

        if(sourceBlock.getType() != Material.AIR)
            return;

        if(!(block.getState() instanceof Sign))
            return;



        if(block.getState().getBlockData() instanceof Sign)
        {
            sign = (Sign)block.getState().getBlockData();
        }
        sign = (Sign)block.getState();

        if(sourceBlock.getType() == Material.AIR)
        {
            if(sign != null)
            {
                attached = sign.getFacing().getOppositeFace();
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

        UUID uuid = Slipdisk.s.identities.getIdentity(sign.getLine(1));
        Profile profile = Slipdisk.s.profileUtils.getProfile(uuid);

        if(profile == null)
            return;

        profile.removeSlip(sign.getLocation());
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "A Slip For " +  profile.getUserID() + " broke due To gravity.");
    }
     */
}
