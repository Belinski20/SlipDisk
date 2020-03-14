package com.belinski20.slipdisk;

import java.util.UUID;

public class Profile {

    private UUID uuid;
    private String id;
    private int slipAmount;

    public void setUuid(UUID uuid)
    {
        this.uuid = uuid;
    }

    public void setID(String id)
    {
        this.id = id;
    }

    public void setSlipAmount(int slipAmount)
    {
        this.slipAmount = slipAmount;
    }

    public UUID getUuid()
    {
        return this.uuid;
    }

    public int getSlipAmount()
    {
        return slipAmount;
    }

    public String getId()
    {
        return id;
    }
}
