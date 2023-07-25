package com.belinski20.slipdisk;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SlipInfoGUI implements Listener {
    private Inventory gui;
    private Map<Integer, Slip> slotToSlip;
    private Profile profile;


    public SlipInfoGUI(Profile profile)
    {
        this.profile = profile;
        gui = Bukkit.createInventory(null, 54, profile.getUserID() + "'s Slips");
        createSlipOptions(profile);
        Bukkit.getServer().getPluginManager().registerEvents(this, Slipdisk.s);
    }

    public void openGUI(HumanEntity ent)
    {
        ent.openInventory(gui);
    }

    private void createSlipOptions(Profile profile)
    {
        int index = 0;
        int slipNum = 0;
        slotToSlip = new HashMap<>();
        List<Slip> slips = profile.getSlips();

        for(Slip slip: slips)
        {
            slipNum++;
            if(index > 35)
                break;

            if(index == 9)
                index = 27;

            ItemStack tItem = createTeleportItem();
            gui.setItem(index, tItem);
            slotToSlip.put(index, slip);

            ItemStack sItem = createSignItem(slipNum, slip);
            gui.setItem(index+=9, sItem);

            ItemStack dItem = createDeleteItem();
            gui.setItem(index+=9, dItem);
            slotToSlip.put(index, slip);

            index -= 17;
        }

    }

    private ItemStack createSignItem(int slipNum, Slip slip)
    {
        final ItemStack item = new ItemStack(Material.OAK_SIGN, 1);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Slip #" + slipNum);
        double x = slip.getSignLocation().getX();
        double y = slip.getSignLocation().getY();
        double z = slip.getSignLocation().getZ();
        List<String> lore = new LinkedList<>();
        lore.add(slip.getSignLocation().getWorld().getName());
        lore.add("X: " + x + " Y: " + y + " Z: " + z);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createTeleportItem()
    {
        final ItemStack item = new ItemStack(Material.ENDER_PEARL, 1);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Teleport");
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createDeleteItem()
    {
        final ItemStack item = new ItemStack(Material.BARRIER, 1);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Delete");
        item.setItemMeta(meta);
        return item;
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().equals(gui)) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().isAir()) return;

        final Player p = (Player) e.getWhoClicked();

        Slip slip = slotToSlip.get(e.getSlot());

        if(slip == null)
            return;

        if(clickedItem.displayName().toString().contains("Delete"))
        {
            p.sendMessage(ChatColor.RED + "You deleted a slip for " + profile.getUserID());
            Bukkit.getConsoleSender().sendMessage("A slip owned by " + profile.getUserID() + " has been deleted by " + p.getName() + ".");
            this.profile.removeSlip(slip.getSignLocation());
            slotToSlip.remove(slip);
            gui.close();
        }

        if(clickedItem.displayName().toString().contains("Teleport"))
        {
            p.teleport(slip.getPlayerLocation());
            p.sendMessage(ChatColor.GREEN + "You teleported to a slip owned by " + profile.getUserID());
            Bukkit.getConsoleSender().sendMessage("A slip owned by " + profile.getUserID() + " has been teleported to by " + p.getName() + ".");
        }


    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(gui)) {
            e.setCancelled(true);
        }
    }

}
