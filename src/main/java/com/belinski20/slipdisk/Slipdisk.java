package com.belinski20.slipdisk;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Slipdisk extends JavaPlugin {

    private static Plugin plugin;
    private Utils slipUtils;
    private Utils profileUtils;

    @Override
    public void onEnable() {
        plugin = this;
        slipUtils = new SlipUtils(getPlugin());
        profileUtils = new ProfileUtils(getPlugin());
        registerEvents(this, new SlipEvents(slipUtils, profileUtils));
        File userDirectory = new File(getDataFolder(), "users");
        File slipDirectory = new File(getDataFolder(), "slips");
        if(!userDirectory.exists())
        {
            if(userDirectory.mkdirs())
            {
                System.out.println("User Directory Created");
            }
            else
                System.out.println("User Directory not Created");
        }
        if(!slipDirectory.exists())
        {
            if(slipDirectory.mkdirs())
            {
                System.out.println("Slip Directory Created");
            }
            else
                System.out.println("Slip Directory not Created");
        }
    }

    @Override
    public void onDisable() {
        plugin = null;
    }

    public boolean onCommand(final CommandSender sender, final Command cmd, final String lable, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        final Player player = (Player)sender;
        if (cmd.getName().equalsIgnoreCase("slipdisk")) {
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
        else
            return false;
    }

    public void createRankFile() throws IOException {
        FileConfiguration config = null;
        File rankFile = new File("plugins" + File.separator + "slipdisk" +  File.separator + "Ranks.yml");

        if(!rankFile.exists())
        {
            rankFile.createNewFile();
            config = YamlConfiguration.loadConfiguration(rankFile);
            config.set("Ranks.Member", 3);
            config.set("Ranks.Admin", 3);
            config.set("Ranks.Owner", 4);
            config.save(rankFile);
        }
    }

    public static void registerEvents(org.bukkit.plugin.Plugin plugin, Listener... listeners){
        for(Listener listener: listeners)
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public static Plugin getPlugin()
    {
        return plugin;
    }
}
