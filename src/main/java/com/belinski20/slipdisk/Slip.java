package com.belinski20.slipdisk;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class Slip {
    private Location signLocation;
    private Location playerLocation;

    public Slip()
    {

    }

    public Slip(Location playerLocation, Location signLocation)
    {
        this.playerLocation = playerLocation;
        this.signLocation = signLocation;
    }

    public Location getSignLocation() {
        return signLocation;
    }

    public void setSignLocation(Location signLocation) {
        this.signLocation = signLocation;
    }

    public Location getPlayerLocation() {
        return playerLocation;
    }

    public void setPlayerLocation(Location playerLocation) {
        this.playerLocation = playerLocation;
    }

    public String getTeleportPosition()
    {
        Block block = playerLocation.getBlock();
        return block.getX() + " " + block.getY() + " " + block.getZ() + " " + playerLocation.getYaw() + " " + playerLocation.getPitch();
    }

}
