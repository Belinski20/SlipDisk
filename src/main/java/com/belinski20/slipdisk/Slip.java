package com.belinski20.slipdisk;

import org.bukkit.Location;

public class Slip {
    private Location signLocation;
    private Location playerLocation;

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

}
