package com.belinski20.slipdisk;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ProfileUtils{

    private static Plugin plugin = Slipdisk.s;

    public int saveProfiles(List<Profile> profileList)
    {
        int savedProfiles = 0;

        for(Profile profile: profileList)
        {
            savePlayerProfile(profile);
            savedProfiles++;
        }

        return savedProfiles;
    }

    public void savePlayerProfile(Profile profile)
    {
        UUID uuid = profile.getUUID();

        FileConfiguration config;
        File file = new File(plugin.getDataFolder() + File.separator + "users", uuid + ".yml");

        try
        {
            if(file.createNewFile())
                plugin.getServer().getConsoleSender().sendMessage("Created Slip file: " + uuid + ".yml");

            // A profile and Identity should be made prior to saving for a player
            config = YamlConfiguration.loadConfiguration(file);
            config.set("Profile.TRUNCNAME", profile.getTruncatedName());
            config.set("Profile.ID", profile.getIdNumber());
            config.set("Profile.RANKAMOUNT", profile.getRankAmount());
            config.set("Profile.BOUGHTAMOUNT", profile.getBoughtAmount());

            config.set("Profile.SLIPS", null);

            // Save each slip to file
            for(int i = 0; i < profile.getSlips().size(); i++)
            {
                Slip slip =  profile.getSlips().get(i);
                Location block = slip.getSignLocation();
                Location loc = slip.getPlayerLocation();

                config.set("Profile.SLIPS." + i + ".x", block.getBlockX());
                config.set("Profile.SLIPS." + i + ".y", block.getBlockY());
                config.set("Profile.SLIPS." + i + ".z", block.getBlockZ());
                config.set("Profile.SLIPS." + i + ".w", block.getWorld().getName());
                config.set("Profile.SLIPS." + i + ".px", loc.getX());
                config.set("Profile.SLIPS." + i + ".py", loc.getY());
                config.set("Profile.SLIPS." + i + ".pz", loc.getZ());
                config.set("Profile.SLIPS." + i + ".ppitch", loc.getPitch());
                config.set("Profile.SLIPS." + i + ".pyaw", loc.getYaw());
            }

            config.save(file);
        }
        catch(IOException e)
        {
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Tried to make file: " + uuid +".yml" + " but it hit error:\n" + e );
        }
    }

    public int loadPlayerProfiles()
    {
        int loadedCount = 0;
        File[] files = new File(plugin.getDataFolder() + File.separator + "users").listFiles();

        if(files.length == 0)
            return 0;

        for(File file: files)
        {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            List<Slip> slips = new LinkedList<>();
            String uuid = getUUIDFromFileName(file.getName());
            String truncatedName = config.getString("Profile.TRUNCNAME");
            int id = config.getInt("Profile.ID");
            int rankAmount = config.getInt("Profile.RANKAMOUNT");
            int boughtAmount = config.getInt("Profile.BOUGHTAMOUNT");

            // Load slips using max amount of slips
            for(int i = 0; i < rankAmount + boughtAmount; i++)
            {
                if(!config.contains("Profile.SLIPS." + i))
                    break;

                double blockX = config.getDouble("Profile.SLIPS." + i + ".x");
                double blockY = config.getDouble("Profile.SLIPS." + i + ".y");
                double blockZ = config.getDouble("Profile.SLIPS." + i + ".z");
                World world = Bukkit.getWorld(config.getString("Profile.SLIPS." + i + ".w"));
                double px = config.getDouble("Profile.SLIPS." + i + ".px");
                double py = config.getDouble("Profile.SLIPS." + i + ".py");
                double pz = config.getDouble("Profile.SLIPS." + i + ".pz");
                double ppitch = config.getDouble("Profile.SLIPS." + i + ".ppitch");
                double pyaw = config.getDouble("Profile.SLIPS." + i + ".pyaw");

                if(world != null)
                {
                    Location sign = new Location(world, blockX, blockY, blockZ);
                    Location player = new Location(world, px, py, pz, (float)pyaw, (float)ppitch);

                    Slip slip = new Slip(player, sign);

                    slips.add(slip);
                }
            }

            Profile profile = new Profile(uuid, slips, rankAmount, boughtAmount, truncatedName, id);
            Slipdisk.s.identities.addIdentity(profile.getUserID(), UUID.fromString(uuid));
            Slipdisk.s.profileList.add(profile);
            loadedCount++;
        }
        return loadedCount;
    }

    public String getUUIDFromFileName(String fileName)
    {
        int pos = fileName.indexOf('.');
        return fileName.substring(0, pos);
    }

    public String truncateUserName(String username)
    {
        return username.substring(0, Math.min(username.length(), 10));
    }

    public Profile getProfile(UUID uuid)
    {
        for(Profile profile: Slipdisk.s.profileList)
        {
            if(profile.isProfile(uuid))
                return profile;
        }
        return null;
    }
}
