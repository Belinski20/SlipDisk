package com.belinski20.slipdisk;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class SlipUtils implements Utils{

    private static Plugin plugin;

    SlipUtils()
    {

    }

    SlipUtils(Plugin plugin)
    {
        this.plugin = plugin;
    }

    public void add(final Location loc) {
        int i;
        for (i = 0; plugin.getConfig().contains(new StringBuilder(String.valueOf(i)).toString()); ++i) {}
        plugin.getConfig().set(String.valueOf(i) + ".x", (Object)loc.getBlockX());
        plugin.getConfig().set(String.valueOf(i) + ".y", (Object)loc.getBlockY());
        plugin.getConfig().set(String.valueOf(i) + ".z", (Object)loc.getBlockZ());
        plugin.getConfig().set(String.valueOf(i) + ".w", (Object)loc.getWorld().getName());
        plugin.saveConfig();
    }

    public boolean contains(String id) throws IOException {
        FileConfiguration config = null;
        File file = new File("plugins" + File.separator + "slipdisk" + File.separator + "users" + File.separator + id + ".yml");
        if(!file.exists())
        {
            return true;
        }
        return false;
    }

    public void remove(final Location loc) {
        /*if (this.contains(loc)) {
            for (final String s : plugin.getConfig().getKeys(false)) {
                if (loc.getBlockX() == plugin.getConfig().getInt(String.valueOf(s) + ".x") &&
                        loc.getBlockY() == plugin.getConfig().getInt(String.valueOf(s) + ".y") &&
                        loc.getBlockZ() == plugin.getConfig().getInt(String.valueOf(s) + ".z") &&
                        loc.getWorld().getName().equals(plugin.getConfig().getString(String.valueOf(s) + ".w"))) {
                    plugin.getConfig().set(s, (Object)null);
                    plugin.saveConfig();
                }
            }
        }*/
    }

    public void createNewSlip(Player player) {
    }
}
