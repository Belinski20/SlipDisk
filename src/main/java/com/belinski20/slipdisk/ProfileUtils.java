package com.belinski20.slipdisk;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ProfileUtils{

    private static Plugin plugin;

    ProfileUtils(Plugin plugin)
    {
        ProfileUtils.plugin = plugin;
    }

    public void createPlayerFile(Player player, String rank, int slipTotal) throws IOException {
        FileConfiguration config;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "users" + File.separator + player.getUniqueId() + ".yml");
        if(!file.exists())
        {
            file.createNewFile();
            config = YamlConfiguration.loadConfiguration(file);
            config.set("Player.Name", player.getName());
            config.set("Player.Rank", rank);
            config.set("Player.SlipID", appendNewCode(truncateUserName(player.getName())));
            config.set("Player.SlipTotal", slipTotal);
            config.save(file);
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "Created Player File: " + player.getUniqueId() + ".yml");
        }
    }

    public boolean resetInformation(Player player) throws IOException {
        FileConfiguration config;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "users" + File.separator + player.getUniqueId() + ".yml");
        if(file.exists())
        {
            config = YamlConfiguration.loadConfiguration(file);
            if(!config.get("Player.Name").equals(player.getName()))
            {
                config.set("Player.Name", player.getName());
                String oldSlipID = (String)config.get("Player.SlipID");
                config.set("Player.SlipID", appendOldCode(truncateUserName(player.getName()), oldSlipID));
                config.save(file);
                return true;
            }
        }
        return false;
    }

    public void updateRank(Player player, String rank, int slipTotal) throws IOException {
        FileConfiguration config;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "users" + File.separator + player.getUniqueId() + ".yml");
        if(file.exists())
        {
            config = YamlConfiguration.loadConfiguration(file);
            config.set("Player.Rank", rank);
            config.set("Player.SlipTotal", slipTotal);
            config.save(file);
        }
    }

    private String truncateUserName(String username)
    {
        return username.substring(0, Math.min(username.length(), 10));
    }

    private String generateIDCode() {
        String ID = "#";
        int numberOfFiles = getProfileFiles().length;
        for(int i = 1000; i > 0; i /= 10)
        {
            ID += ((numberOfFiles / i) % 10);
        }
        return ID;
    }

    private String getIDCodeFromID(String username)
    {
        return username.substring(username.length()-5);
    }

    private String appendNewCode(String username)
    {
        return username += generateIDCode();
    }

    private String appendOldCode(String username, String oldID)
    {
        return username += getIDCodeFromID(oldID);
    }

    public String getUserID(UUID uuid)
    {
        FileConfiguration config = null;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "users" + File.separator + uuid + ".yml");
        if(file.exists())
        {
            config = YamlConfiguration.loadConfiguration(file);
            return (String)config.get("Player.SlipID");
        }
        return "";
    }

    private File[] getProfileFiles()
    {
       File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "users", "");
       File[] files = file.listFiles();
       return files;
    }

    public void increaseSlipAmount() {
    }

    public String getRank(UUID uuid)
    {
        FileConfiguration config = null;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "users" + File.separator + uuid + ".yml");
        if(file.exists())
        {
            config = YamlConfiguration.loadConfiguration(file);
            return (String)config.get("Player.Rank");
        }
        return "";
    }
}
