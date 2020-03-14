package com.slipdisc.slipdisc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Slipdisc extends JavaPlugin implements Listener {
    ConsoleCommandSender console;
    private YamlConfig yamlConfig;

    @Override
    public void onEnable() {
        console = Bukkit.getConsoleSender();
        yamlConfig = new YamlConfig();

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("slipdisc"))
        {
            if(sender instanceof Player)
            {
                Player player = (Player) sender;
                player.sendMessage("");
                player.sendMessage(ChatColor.GOLD
                                + "SlipDisc is an exclusive plugin that allows " +
                        "you to create a two-way \"slip\" to teleport between spawn and your base!");
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event)
    {
        if(!event.getPlayer().hasPermission("slipdisc.access"))
        {
            event.getPlayer().sendMessage(ChatColor.RED + "You're not allowed to create slips!");
            return;
        }

        if (!(event.getLine(0).equalsIgnoreCase("#slip") || event.getLine(1)
                .equalsIgnoreCase("#slip")) && !event.getLine(0).equalsIgnoreCase("#slipuser"))
            return;

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
