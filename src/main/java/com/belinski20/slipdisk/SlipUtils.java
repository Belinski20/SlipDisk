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

    /**
     * Create a File for Users which contains their slips
     * @param userID
     * @param rank
     * @throws IOException
     */
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

    /**
     * Checks to see if a sign contains a given UserID
     * @param userID
     * @param sign
     * @return
     */
    public boolean contains(String userID, Sign sign)
    {
        return sign.getLine(1).equalsIgnoreCase(userID);
    }

    /**
     * Checks to see if a sign exists in a user file
     * @param userID
     * @param sign
     * @return
     */
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

    /**
     * Adds a slip entry into the user file
     * @param userID
     * @param player
     * @param block
     * @throws IOException
     */
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

    /**
     * Gets a list of slips from the user file
     * @param userID
     * @return
     */
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

    /**
     * Gets the location of a slip from a user file
     * @param index
     * @param config
     * @return
     */
    private Location getSignLocation(int index, FileConfiguration config)
    {
        int x = config.getInt("Slip.slips." + index + ".x");
        int y = config.getInt("Slip.slips." + index + ".y");
        int z = config.getInt("Slip.slips." + index + ".z");
        String w = config.getString("Slip.slips." + index + ".w");
        World world = Bukkit.getWorld(w);
        return new Location(world, x, y, z);
    }

    /**
     * Gets the player location from a user file
     * @param index
     * @param config
     * @return
     */
    private Location getPlayerLocation(int index, FileConfiguration config)
    {
        double x = config.getDouble("Slip.slips." + index + ".px");
        double y = config.getDouble("Slip.slips." + index + ".py");
        double z = config.getDouble("Slip.slips." + index + ".pz");
        double pitch = config.getDouble("Slip.slips." + index + ".ppitch");
        double yaw = config.getDouble("Slip.slips." + index + ".pyaw");
        String w = (String)config.get("Slip.slips." + index + ".w");
        World world = Bukkit.getWorld(w);
        return new Location(world, x, y, z, (float)yaw, (float)pitch);
    }

    /**
     * Removes a given slip from a location from a user file
     * @param location
     * @param userID
     * @throws IOException
     */
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

    /**
     * Gets the next location which the player will be teleported to
     * @param userID
     * @param location
     * @return
     */
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

    /**
     * Fixes the numbering of the slips in the slip file.
     * @param userID
     * @param remainingSlips
     * @throws IOException
     */
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

    /**
     * Sets the amount of slips in a user slip file to zero
     * @param userID
     * @throws IOException
     */
    private void setAmountZero(String userID) throws IOException {
        FileConfiguration config;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "slips" + File.separator + userID + ".yml");
        config = YamlConfiguration.loadConfiguration(file);
        config.set("Slip.Amount", 0);
        config.save(file);
    }

    /**
     * Updates the slip signs for a player if a name change happens
     * @param oldUserID
     * @throws IOException
     */
    public void updateSlipData(String oldUserID, String newUserID) throws IOException {
        FileConfiguration config;

        if(!oldUserID.equals(newUserID))
        {
            updateSigns(newUserID);

            File[] files = getSlipFiles();

            for(File slipFile : files)
            {
                if(!slipFile.getName().equals(oldUserID + ".yml"))
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
    }

    public void updateSlipFile(String userID, int total) throws IOException {
        FileConfiguration config;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "slips" + File.separator + userID + ".yml");
        config = YamlConfiguration.loadConfiguration(file);
        config.set("Slip.Total", total);
        config.save(file);
    }

    /**
     * Updates the slip of a given user if there was a name change and changes name on the sign to reflect change
     * @param userID
     * @throws IOException
     */
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
            int x = config.getInt("Slip.slips." + j + ".x");
            int y = config.getInt("Slip.slips." + j + ".y");
            int z = config.getInt("Slip.slips." + j + ".z");
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

    /**
     * Gets the user User ID from a sign
     * @param sign
     * @return
     */
    public String getUserIDFromSign(Sign sign)
    {
        return sign.getLine(1);
    }

    /**
     * Gets a list of all slip files
     * @return
     */
    private File[] getSlipFiles()
    {
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "slips", "");
        return file.listFiles();
    }

    /**
     * Gets the max amount of slips that a player can have
     * @param userID
     * @return
     */
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

    /**
     * Get the current amount of slips that a player has
     * @param userID
     * @return
     */
    public int getCurrentSlipAmount(String userID)
    {
        FileConfiguration config;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "slips" + File.separator + userID + ".yml");

        int currentAmountSlips = 0;
        if(file.exists())
        {
            config = YamlConfiguration.loadConfiguration(file);

            currentAmountSlips = config.getInt("Slip.Amount");
        }
        return currentAmountSlips;
    }

    /**
     * Checks to see if a player has the total amount of slips
     * @param userID
     * @return
     */
    public boolean hasMaxSlips(String userID)
    {
        FileConfiguration config;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "slips" + File.separator + userID + ".yml");

        if(file.exists())
        {
            config = YamlConfiguration.loadConfiguration(file);

            int totalAmountSlips = config.getInt("Slip.Total");
            int currentAmountSlips = config.getInt("Slip.Amount");
            return !(currentAmountSlips < totalAmountSlips);
        }
        return true;
    }

    /**
     * Checks to see if the slip exists if not then it removes it.
     * @param userID
     * @throws IOException
     */
    public void checkSlipsExist(String userID) throws IOException {
        if(getSlips(userID) == null)
            return;
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
