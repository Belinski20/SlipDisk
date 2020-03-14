package com.slipdisc.slipdisc;

import org.bukkit.Location;

class SlipSign
{
    public Location sign, slip;
    public int sid;
}

public class Slip {
    public static final int MAX_SLIPS = 5;
    public SlipSign signs[];

    public Slip()
    {
        signs = new SlipSign[MAX_SLIPS];
        for(int i = 0; i < MAX_SLIPS; i++ )
        {
            signs[i] = null;
        }
    }

    public int numEndPoints()
    {
        int count = 0;
        for(int i = 0; i < MAX_SLIPS; i++)
        {
            if(signs[i] != null)
                count++;
        }
        return count;
    }
}
