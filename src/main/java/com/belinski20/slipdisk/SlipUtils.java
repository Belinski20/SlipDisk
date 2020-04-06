package com.belinski20.slipdisk;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

class SlipUtils{

    private static Plugin plugin;

    SlipUtils(Plugin plugin)
    {
        SlipUtils.plugin = plugin;
    }

    public void createUserSlipFile(String userID, String rank) throws IOException {
        FileConfiguration config;
        FileConfiguration rankConfig;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "slips" + File.separator + userID + ".yml");
        File rankFile = new File("plugins" + File.separator + "slipdisk"  + File.separator + "Ranks.yml");
        rankConfig = YamlConfiguration.loadConfiguration(rankFile);
        int slipTotal = (int)rankConfig.get("Ranks." + rank);
        if(!file.exists())
        {
            file.createNewFile();
            config = YamlConfiguration.loadConfiguration(file);
            config.set("Slip.Total", slipTotal);
            config.set("Slip.Amount", 0);
            config.save(file);
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "Created Slip File: " + userID + ".yml");
        }
    }

    public boolean contains(String userID, Sign sign)
    {
        return sign.getLine(1).equalsIgnoreCase(userID);
    }

    public boolean fileContains(String userID, Sign sign)
    {
        if(getSlips(userID) == null)
            return false;
        for(Slip slip: getSlips(userID))
        {
            if(slip.getSignLocation().equals(sign.getLocation()))
                return true;
        }
        return false;
    }

    public void addSlip(String userID, Location player, Location block) throws IOException {
        FileConfiguration config;
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

    private ArrayList<Slip> getSlips(String userID)
    {
        ArrayList<Slip> slips = new ArrayList<>();
        FileConfiguration config;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "slips" + File.separator + userID + ".yml");
        if(file.exists())
        {
            config = YamlConfiguration.loadConfiguration(file);
            if(config.getConfigurationSection("Slip.slips") == null)
                return null;
            for(String string : config.getConfigurationSection("Slip.slips").getKeys(false))
            {
                int index = Integer.valueOf(string);
                Slip sign = new Slip();
                sign.setSignLocation(getSignLocation(index, config));
                sign.setPlayerLocation(getPlayerLocation(index, config));
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
        return new Location(world, x, y, z);
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
        return new Location(world, x, y, z, (float)yaw, (float)pitch);
    }

    public void removeSlip(Location location, String userID) throws IOException {
        ArrayList<Slip> slips = getSlips(userID);
        Slip slipToRemove = new Slip();
        slipToRemove.setSignLocation(location);

        for(Slip slip: slips)
        {
            if(slip.getSignLocation().equals(slipToRemove.getSignLocation()))
            {
                slips.remove(slip);
                fixSlips(userID, slips);
                return;
            }
        }
    }

    public Location nextTeleport(String userID, Location location)
    {
        ArrayList<Slip> slips = getSlips(userID);
        for(int i = 0; i < slips.size(); i++)
        {
            if(slips.get(i).getSignLocation().equals(location))
            {
                if(i == slips.size()-1)
                {
                    return slips.get(0).getPlayerLocation();
                }
                else
                    return slips.get(i+1).getPlayerLocation();
            }
        }
        return null;
    }

    private void fixSlips(String userID, ArrayList<Slip> remainingSlips) throws IOException {
        FileConfiguration config;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "slips" + File.separator + userID + ".yml");
        setAmountZero(userID);
        if(file.exists())
        {
            config = YamlConfiguration.loadConfiguration(file);
            config.set("Slip.slips", null);
            config.save(file);
            for(int i = 0; i <= remainingSlips.size()-1; i++)
            {
                addSlip(userID, remainingSlips.get(i).getPlayerLocation(), remainingSlips.get(i).getSignLocation());
            }
        }
    }

    private void setAmountZero(String userID) throws IOException {
        FileConfiguration config;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "slips" + File.separator + userID + ".yml");
        config = YamlConfiguration.loadConfiguration(file);
        config.set("Slip.Amount", 0);
        config.save(file);
    }

    public void updateSlipData(UUID uuid, String userID) throws IOException {
        FileConfiguration config;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "users" + File.separator + uuid + ".yml");
        String newUserID = "";

        if(file.exists())
        {
            config = YamlConfiguration.loadConfiguration(file);
            newUserID = (String)config.get("Player.SlipID");
        }

        File[] files = getSlipFiles();

        for(File slipFile : files)
        {
            if(userID.equals(newUserID))
            {
                updateSigns(newUserID);
                return;
            }
            if(slipFile.getName().equals(userID + ".yml"))
            {
                config = YamlConfiguration.loadConfiguration(slipFile);
                File newFile = new File("plugins" + File.separator + "slipdisk" + File.separator + "slips" + File.separator + newUserID + ".yml");
                slipFile.renameTo(newFile);
                config.save(slipFile);
                slipFile.delete();
                updateSigns(newUserID);
            }
        }
    }

    public void updateSlipFile(String userID, int total) throws IOException {
        FileConfiguration config;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "slips" + File.separator + userID + ".yml");
        config = YamlConfiguration.loadConfiguration(file);
        config.set("Slip.Total", total);
        config.save(file);
    }

    private void updateSigns(String userID) throws IOException {
        FileConfiguration config;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "slips" + File.separator + userID + ".yml");
        config = YamlConfiguration.loadConfiguration(file);

        int j = 0;
        if(!config.contains("Slip.slips"))
            return;
        for(int i = 0; i < config.getConfigurationSection("Slip.slips").getKeys(false).size(); i++)
        {
            j++;
            int x = (int)config.get("Slip.slips." + j + ".x");
            int y = (int)config.get("Slip.slips." + j + ".y");
            int z = (int)config.get("Slip.slips." + j + ".z");
            String w = (String)config.get("Slip.slips." + j + ".w");
            if(plugin.getServer().getWorld(w).getBlockAt(x, y, z).getState() instanceof Sign)
            {
                Sign sign = (Sign)plugin.getServer().getWorld(w).getBlockAt(x, y, z).getState();
                sign.setLine(1, userID);
                sign.update();
            }
            else
                removeSlip(getSignLocation(j, config), userID);
        }
    }

    public String getUserIDFromSign(Sign sign)
    {
        return sign.getLine(1);
    }

    private File[] getSlipFiles()
    {
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "slips", "");
        return file.listFiles();
    }

    public int getMaxSlip(String userID)
    {
        FileConfiguration config;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "slips" + File.separator + userID + ".yml");

        int totalAmountSlips = 0;
        if(file.exists())
        {
            config = YamlConfiguration.loadConfiguration(file);

            totalAmountSlips = (int)config.get("Slip.Total");
        }
        return totalAmountSlips;
    }

    public int getCurrentSlipAmount(String userID)
    {
        FileConfiguration config;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "slips" + File.separator + userID + ".yml");

        int currentAmountSlips = 0;
        if(file.exists())
        {
            config = YamlConfiguration.loadConfiguration(file);

            currentAmountSlips = (int)config.get("Slip.Amount");
        }
        return currentAmountSlips;
    }

    public boolean hasMaxSlips(String userID)
    {
        FileConfiguration config;
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

    public void checkSlipsExist(String userID) throws IOException {
        for(Slip slip : getSlips(userID))
        {
            Block block = slip.getSignLocation().getBlock();
            if(block.getState() instanceof Sign)
            {
                Sign sign = (Sign)block.getState();
                if(sign.getLine(1).equals(userID))
                    continue;
            }
                removeSlip(slip.getSignLocation(), userID);
        }
    }
}
