package com.belinski20.slipdisk;

import com.belinski20.slipdisk.Command.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class Slipdisk extends JavaPlugin {

    public static Slipdisk s;
    public SlipUtils slipUtils;
    public ProfileUtils profileUtils;
    public Identities identities;
    public List<Profile> profileList;
    public PermissionIntegration permissionIntegration;
    private Timer time;

    public static void registerEvents(org.bukkit.plugin.Plugin plugin, Listener... listeners){
        for(Listener listener: listeners)
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    public void onEnable() {
        s = this;
        slipUtils = new SlipUtils();
        profileUtils = new ProfileUtils();
        permissionIntegration = new PermissionIntegration();
        identities = new Identities();
        profileList = new LinkedList<>();
        registerEvents(s, new SlipEvents());
        createDirectories();
        loadPlayerProfiles();
        time = new Timer();
        time.schedule(new AutoSave(), 0, TimeUnit.MINUTES.toMillis(10));
        getCommand("resetslips").setExecutor(new ResetCommand());
        getCommand("update").setExecutor(new UpdateCommand());
        getCommand("slip").setExecutor(new SlipCommand());
        getCommand("buyslip").setExecutor(new BuyCommand());
        getCommand("sellslip").setExecutor(new SellCommand());
        getCommand("slipinfo").setExecutor(new InfoCommand());
    }

    private void createDirectories(){
        String names[] = {"users"};
        for(String name : names)
        {
            File file = new File(getDataFolder(), name);
            if(!file.exists())
                if(file.mkdirs())
                    getServer().getConsoleSender().sendMessage(
                            ChatColor.GREEN + name + " Directory Created");
                else
                    getServer().getConsoleSender().sendMessage(
                            ChatColor.DARK_RED + "Failed To Create " + name + " Directory");

        }
        try {
            permissionIntegration.createRankFile();
        } catch (IOException e) {
            getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "Permission Integration File was not created! Error Below");
            e.printStackTrace();
            getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "End of Permission Integration Error!");
        }
    }

    private void loadPlayerProfiles()
    {
        int loadedProfiles = profileUtils.loadPlayerProfiles();
        getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "Loaded " + loadedProfiles + " saved slip profiles.");
    }

    @Override
    public void onDisable()
    {
        getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "Saving Profiles...");
        int i = profileUtils.saveProfiles(profileList);
        getServer().getConsoleSender().sendMessage(ChatColor.BLUE + "" + i + " profiles saved successfully.");
    }
}
