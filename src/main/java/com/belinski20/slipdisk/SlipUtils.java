package com.belinski20.slipdisk;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class SlipUtils{

    private static Plugin plugin = Slipdisk.s;

    /**
     * Checks to see if a sign contains a given UserID
     * @param userID
     * @param sign
     * @return
     */
    public boolean contains(String userID, Sign sign)
    {
        return sign.getLine(1).equalsIgnoreCase(userID);
    }

    /**
     * Removes a given slip from a location from a user file
     * @param location
     */
    public void removeSlip(Location location)
    {
        location.getWorld().playEffect(location, Effect.ANVIL_BREAK, 100, 1);
        location.getBlock().breakNaturally();
    }

    /**
     *  Updates the name of all slips when a name change has been detected
     * @param uuid
     * @return
     */
    public int updateSlips(UUID uuid)
    {
        Profile profile = Slipdisk.s.profileUtils.getProfile(uuid);
        int count = 0;

        for(Slip slip: profile.getSlips())
        {
            Block block = slip.getSignLocation().getBlock();
            Sign sign = (Sign)block.getState();
            sign.setLine(1, profile.getUserID());
            slip.getSignLocation().getWorld().playSound(slip.getSignLocation(), Sound.BLOCK_ANVIL_PLACE, 100, 1);
            count++;
        }

        return count;
    }

    /**
     * Gets the User ID from a sign
     * @param sign
     * @return
     */
    public String getUserIDFromSign(Sign sign)
    {
        return sign.getLine(1);
    }

}
