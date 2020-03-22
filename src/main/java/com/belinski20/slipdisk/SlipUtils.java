package com.belinski20.slipdisk;

import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class SlipUtils{

    private static Plugin plugin;

    SlipUtils(Plugin plugin)
    {
        this.plugin = plugin;
    }

    public void createUserSlipFile(String userID) throws IOException {
        FileConfiguration config = null;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "slips" + File.separator + userID + ".yml");
        if(!file.exists())
        {
            file.createNewFile();
            config = YamlConfiguration.loadConfiguration(file);
            config.set("Slip.Total", 3);
            config.set("Slip.Amount", 0);
            config.save(file);
        }
    }

    public boolean contains(String userID, Sign sign)
    {
        if(sign.getLine(1).equalsIgnoreCase(userID))
            return true;
        return false;
    }

    public void addSlip(String userID, Location player, Location block) throws IOException {
        FileConfiguration config = null;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "slips" + File.separator + userID + ".yml");
        if(file.exists())
        {
            config = YamlConfiguration.loadConfiguration(file);
            int amountSlips = (int)config.get("Slip.Amount");
            int totalSlips = (int)config.get("Slip.Total");
            if(amountSlips >= totalSlips)
                return;
            amountSlips++;
            config.set("Slip.slips." + amountSlips + ".x", block.getBlockX());
            config.set("Slip.slips." + amountSlips + ".y", block.getBlockY());
            config.set("Slip.slips." + amountSlips + ".z", block.getBlockZ());
            config.set("Slip.slips." + amountSlips + ".w", block.getWorld().getName());
            config.set("Slip.slips." + amountSlips + ".px", player.getX());
            config.set("Slip.slips." + amountSlips + ".py", player.getY());
            config.set("Slip.slips." + amountSlips + ".pz", player.getZ());
            config.set("Slip.slips." + amountSlips + ".ppitch", player.getPitch());
            config.set("Slip.slips." + amountSlips + ".pyaw", player.getYaw());
            config.set("Slip.Amount", amountSlips);
            config.save(file);
        }
    }

    private ArrayList<SlipSign> getSlips(String userID)
    {
        ArrayList<SlipSign> slips = new ArrayList<>();
        FileConfiguration config = null;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "slips" + File.separator + userID + ".yml");
        if(file.exists())
        {
            config = YamlConfiguration.loadConfiguration(file);
            int j = 0;
            for(int i = 0; i < config.getConfigurationSection("Slip.slips.").getKeys(false).size(); i++)
            {
                j++;
                SlipSign sign = new SlipSign();
                sign.setSign(getSignLocation(j, config));
                sign.setSlip(getPlayerLocation(j, config));
                slips.add(sign);
            }
        }
        return slips;
    }

    private Location getSignLocation(int index, FileConfiguration config)
    {
        int x = (int)config.get("Slip.slips." + index + ".x");
        int y = (int)config.get("Slip.slips." + index + ".y");
        int z = (int)config.get("Slip.slips." + index + ".z");
        String w = (String)config.get("Slip.slips." + index + ".w");
        World world = Bukkit.getWorld(w);
        Location location = new Location(world, x, y, z);
        return location;
    }

    private Location getPlayerLocation(int index, FileConfiguration config)
    {
        double x = (double)config.get("Slip.slips." + index + ".px");
        double y = (double)config.get("Slip.slips." + index + ".py");
        double z = (double)config.get("Slip.slips." + index + ".pz");
        double pitch = (double)config.get("Slip.slips." + index + ".ppitch");
        double yaw = (double)config.get("Slip.slips." + index + ".pyaw");
        String w = (String)config.get("Slip.slips." + index + ".w");
        World world = Bukkit.getWorld(w);
        Location location = new Location(world, x, y, z, (float)yaw, (float)pitch);
        return location;
    }

    public void removeSlip(Location location, String userID) throws IOException {
        ArrayList<SlipSign> slips = getSlips(userID);
        SlipSign slipToRemove = new SlipSign();
        slipToRemove.setSign(location);

        for(SlipSign slip: slips)
        {
            if(slip.getSign().equals(slipToRemove.getSign()))
            {
                slips.remove(slip);
                fixSlips(userID, slips);
                return;
            }
        }
    }

    public Location nextTeleport(String userID, Location location)
    {
        ArrayList<SlipSign> slips = getSlips(userID);
        for(int i = 0; i < slips.size(); i++)
        {
            if(slips.get(i).getSign().equals(location))
            {
                if(i == slips.size()-1)
                {
                    return slips.get(0).getSlip();
                }
                else
                    return slips.get(i+1).getSlip();
            }
        }
        return null;
    }

    private void fixSlips(String userID, ArrayList<SlipSign> remainingSlips) throws IOException {
        FileConfiguration config = null;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "slips" + File.separator + userID + ".yml");
        setAmountZero(userID);
        if(file.exists())
        {
            config = YamlConfiguration.loadConfiguration(file);
            config.set("Slip.slips", null);
            config.save(file);
            for(int i = 0; i <= remainingSlips.size()-1; i++)
            {
                addSlip(userID, remainingSlips.get(i).getSlip(), remainingSlips.get(i).getSign());
            }
        }
    }

    private void setAmountZero(String userID) throws IOException {
        FileConfiguration config = null;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "slips" + File.separator + userID + ".yml");
        config = YamlConfiguration.loadConfiguration(file);
        config.set("Slip.Amount", 0);
        config.save(file);
    }

    public void updateSlipData(UUID uuid, String userID)
    {
        FileConfiguration config = null;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "users" + File.separator + uuid + ".yml");

        String newUserID = "";

        if(file.exists())
        {
            config = YamlConfiguration.loadConfiguration(file);
            newUserID = (String)config.get("Player.ID");
        }

        File[] files = getSlipFiles();

        for(File slipFile : files)
        {
            if(file.getName().equals(userID + ".yml"))
            {
                config = YamlConfiguration.loadConfiguration(slipFile);
                config.set("Slip.ID", newUserID);
                //updateSigns(newUserID);
            }
        }
    }

    public String getUserIDFromSign(Sign sign)
    {
        return sign.getLine(1);
    }

    private File[] getSlipFiles()
    {
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "slips", "");
        File[] files = file.listFiles();
        return files;
    }

    public int getMaxSlip(String userID)
    {
        FileConfiguration config = null;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "slips" + File.separator + userID + ".yml");

        int totalAmountSlips = 0;
        if(file.exists())
        {
            config = YamlConfiguration.loadConfiguration(file);

            totalAmountSlips = (int)config.get("Slip.Total");
        }
        return totalAmountSlips;
    }

    public boolean hasMaxSlips(String userID)
    {
        FileConfiguration config = null;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "slips" + File.separator + userID + ".yml");

        if(file.exists())
        {
            config = YamlConfiguration.loadConfiguration(file);

            int totalAmountSlips = (int)config.get("Slip.Total");
            int currentAmountSlips = (int)config.get("Slip.Amount");
            return !(currentAmountSlips < totalAmountSlips);
        }
        return true;
    }
}
