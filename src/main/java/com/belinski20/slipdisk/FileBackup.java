package com.belinski20.slipdisk;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import static org.bukkit.Bukkit.getServer;

public class FileBackup extends TimerTask {

    private Plugin plugin;
    private File workingDirectory;
    private File backUpDirectory;

    FileBackup(Plugin plugin)
    {
        this.plugin = plugin;
        workingDirectory = new File(plugin.getDataFolder(), "slips");
        backUpDirectory = new File(plugin.getDataFolder(), "backup");
    }

    public void run()
    {
        try
        {
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Backing Up Slip information!");
            oldFileCheck();
            backUp();
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "BackUp Finished!");
        }
        catch(Exception e)
        {
            System.out.println("Error running thread " + e.getMessage());
        }
    }

    private void oldFileCheck()
    {
        long dayInMillis = System.currentTimeMillis() - (24 * 60 * 60 * 1000); //hours minutes seconds millisecs
        File[] backUpFiles = backUpDirectory.listFiles();
        if(backUpFiles.length == 0)
            return;
        for(File file : backUpFiles)
        {
            if(file.lastModified() < dayInMillis)
            {
                String name = file.getName();
                deleteFiles(file);
                getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Deleted directory " + name);
            }
        }
    }

    private void deleteFiles(File file)
    {
        if(!file.exists())
            return;

        if(file.isDirectory())
        {
            for(File f : file.listFiles())
                deleteFiles(f);
        }

        file.delete();
    }

    private void backUp()
    {
        DateFormat dateFormat = new SimpleDateFormat("HH-mm");
        Date date = new Date();
        String dateString = "back-up at " + dateFormat.format(date);
        File backup = new File(backUpDirectory, dateString);
        try
        {
            FileUtils.copyDirectory(workingDirectory, backup);
            backup.setLastModified(System.currentTimeMillis());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

}
