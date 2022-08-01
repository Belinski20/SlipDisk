package com.belinski20.slipdisk;

import org.bukkit.ChatColor;
import java.util.TimerTask;


public class AutoSave extends TimerTask {

    public void run()
    {
        try
        {
            Slipdisk.s.profileUtils.saveProfiles(Slipdisk.s.profileList);
        }
        catch(Exception e)
        {
            System.out.println(ChatColor.RED +  "Could not Save Error Below:");
            System.out.println("Error running thread " + e.getMessage());
        }
    }
}
