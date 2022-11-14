package com.belinski20.slipdisk;

import org.bukkit.Bukkit;
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
            Bukkit.getLogger().info(ChatColor.RED +  "Could not Save Error Below:");
            Bukkit.getLogger().info("Error running thread " + e.getMessage());
            e.printStackTrace();
        }
    }
}
