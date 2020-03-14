package com.slipdisc.slipdisc;

import java.util.UUID;

public class SlipStats {
    private float x, y, z, pitch, yaw;
    private String world;

    SlipStats(float x, float y, float z, float pitch, float yaw, String world)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        this.world = world;
    }

    public Slip getSlip(UUID uuid, int ID)
    {
        
    }
}
