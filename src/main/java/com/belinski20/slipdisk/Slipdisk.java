package com.belinski20.slipdisk;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public final class Slipdisk extends JavaPlugin {

    private SlipUtils slipUtils;
    private ProfileUtils profileUtils;
    private PermissionIntegration permissionIntegration;
    private Timer time;

    @Override
    public void onEnable() {
        slipUtils = new SlipUtils(this);
        profileUtils = new ProfileUtils(this);
        permissionIntegration = new PermissionIntegration(this);
        registerEvents(this, new SlipEvents(slipUtils, profileUtils, permissionIntegration, this));
        createDirectories();
        time = new Timer();
        time.schedule(new FileBackup(this), 0, TimeUnit.HOURS.toMillis(4));
    }

    private void createDirectories()
    {
        File userDirectory = new File(getDataFolder(), "users");
        File slipDirectory = new File(getDataFolder(), "slips");
        File backUpDirectory = new File(getDataFolder(), "backup");
        if(!userDirectory.exists())
        {
            if(userDirectory.mkdirs())
            {
                getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "User Directory Created");
            }
            else
                getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "Failed To Create User Directory");
        }
        if(!slipDirectory.exists())
        {
            if(slipDirectory.mkdirs())
            {
                getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Slip Directory Created");
            }
            else
                getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "Failed To Create Slip Directory");
        }
        if(!backUpDirectory.exists())
        {
            if(backUpDirectory.mkdirs())
            {
                getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Backup Directory Created");
            }
            else
                getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "Failed To Create Backup Directory");
        }
        try {
            permissionIntegration.createRankFile();
        } catch (IOException e) {
            getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "Permission Integration File was not created! Error Below");
            e.printStackTrace();
            getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "End of Permission Integration Error!");
        }
    }

    @Override
    public void onDisable() {

    }

    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        final Player player = (Player)sender;
        if (cmd.getName().equalsIgnoreCase("slip")) {
            player.sendMessage("");
            player.sendMessage(ChatColor.GOLD
                    + "Slipdisk is a Spinalcraft-exclusive plugin that allows "
                    + "you to create a two-way \"slip\" to teleport between spawn and your base!");
            player.sendMessage("");
            player.sendMessage(ChatColor.GOLD
                    + "Each player may have one slip at a time. "
                    + "To create a slip, you need to place a sign at each endpoint. On each sign, simply type "
                    + ChatColor.RED
                    + "#slip "
                    + ChatColor.GOLD
                    + "in the top row, and it will automatically register it in your name. "
                    + "Now you and others can use your slip to instantly teleport back and forth!");
            player.sendMessage("");
            return true;
        }
        if(cmd.getName().equalsIgnoreCase("update"))
        {
            player.sendMessage(ChatColor.GREEN + "Started to update player ranks...");
            int count = 0;
            for(Player players : getServer().getOnlinePlayers())
            {
                String rank = permissionIntegration.getUserRank(players);
                int total = permissionIntegration.getSlipTotal(rank);
                try {
                    profileUtils.updateRank(players, rank, total);
                    slipUtils.updateSlipFile(profileUtils.getUserID(players.getUniqueId()), total);
                    count += 1;
                } catch (IOException e) {
                    getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "Update Command Error! Error Below");
                    e.printStackTrace();
                    getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "End of Update Command Error!");            }
            }
            player.sendMessage(ChatColor.GOLD + "" + count + " players updated!");
            return true;
        }
        else
            return false;
    }

    public static void registerEvents(org.bukkit.plugin.Plugin plugin, Listener... listeners){
        for(Listener listener: listeners)
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
    }
}
