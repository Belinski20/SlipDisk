package com.belinski20.slipdisk;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.File;
import java.io.IOException;

class PermissionIntegration {

    private Plugin plugin;
    private Permission perms;

    PermissionIntegration(Plugin plugin)
    {
        this.plugin = plugin;
        setupPermissions();
    }

    private void setupPermissions()
    {
        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        if(perms != null)
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Found permissions!");
        else
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "Permissions not found!");
    }

    private Permission getPermissions() {
        return perms;
    }

    private String[] getRanks()
    {
        return getPermissions().getGroups();
    }

    public void createRankFile() throws IOException {
        FileConfiguration config = null;
        File rankFile = new File("plugins" + File.separator + "slipdisk" +  File.separator + "Ranks.yml");

        if(!rankFile.exists())
        {
            rankFile.createNewFile();
            config = YamlConfiguration.loadConfiguration(rankFile);
            for(String rank : getRanks())
            {
                config.set("Ranks." + rank, 2);
            }
            config.save(rankFile);
        }
    }

    public int getSlipTotal(String rank)
    {
        FileConfiguration config;
        File rankFile = new File("plugins" + File.separator + "slipdisk" +  File.separator + "Ranks.yml");
        config = YamlConfiguration.loadConfiguration(rankFile);
        if(config.get("Ranks." + rank) != null)
        {
            int total = (int)config.get("Ranks." + rank);
            return total;
        }
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Rank (" + rank + ") not found in Ranks.yml!");
        return -1;
    }

    public String getUserRank(Player player)
    {
        return getPermissions().getPrimaryGroup(player);
    }
}
