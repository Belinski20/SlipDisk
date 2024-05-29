package com.belinski20.slipdisk;

import com.belinski20.slipdisk.Command.*;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
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
    public boolean aprilFools = false;
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
        initializeCommandsAndPermissions();

    }

    private void initializeCommandsAndPermissions()
    {
        Bukkit.getPluginManager().addPermission(new Permission("slipdisk.bypass"));
        Bukkit.getPluginManager().addPermission(new Permission("slipdisk.april"));
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
                    getServer().getConsoleSender().sendMessage(Messages.makeComponent(name + "Directory Created", NamedTextColor.DARK_RED));
                else
                    getServer().getConsoleSender().sendMessage(Messages.makeComponent("Failed To Create " + name + " Directory", NamedTextColor.DARK_RED));

        }
        try {
            permissionIntegration.createRankFile();
        } catch (IOException e) {
            getServer().getConsoleSender().sendMessage(Messages.makeComponent("Permission Integration File was not created! Error Below", NamedTextColor.RED));
            e.printStackTrace();
            getServer().getConsoleSender().sendMessage(Messages.makeComponent("End of Permission Integration Error!", NamedTextColor.RED));
        }
    }

    private void loadPlayerProfiles()
    {
        int loadedProfiles = profileUtils.loadPlayerProfiles();
        getServer().getConsoleSender().sendMessage(Messages.makeComponent("Loaded " + loadedProfiles + " saved slip profiles.", NamedTextColor.GOLD));
    }

    public Profile getPlayerProfile(UUID uuid)
    {
        for(Profile p : Slipdisk.s.profileList)
        {
            if(p.isProfile(uuid))
            {
               return p;
            }
        }
        return null;
    }

    @Override
    public void onDisable()
    {
        getServer().getConsoleSender().sendMessage(Messages.makeComponent("Saving Profiles...", NamedTextColor.BLUE));
        int i = profileUtils.saveProfiles(profileList);
        getServer().getConsoleSender().sendMessage(Messages.makeComponent("" + i + " profiles saved successfully.", NamedTextColor.BLUE));
    }
}
