package com.belinski20.slipdisk;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Profile {

    private UUID uuid;
    private String truncatedName;
    private int idNumber;
    private int rankAmount;
    private int boughtAmount;
    private List<Slip> slips;
    private boolean isPublic;
    private List<UUID> trustedMembers;

    public Profile(Player player, int rankAmount, int boughtAmount, String truncatedName, int id)
    {
        this.uuid = player.getUniqueId();
        this.slips = new LinkedList<>();
        this.trustedMembers = new LinkedList<>();
        this.rankAmount = rankAmount;
        this.boughtAmount = boughtAmount;
        this.truncatedName = truncatedName;
        this.idNumber = id;
        this.isPublic = true;
    }

    public Profile(String uuid, List<Slip> slips, List<UUID> trustedMembers, int rankAmount, int boughtAmount, String truncatedName, int id, boolean isPublic)
    {
        this.uuid = UUID.fromString(uuid);
        this.slips = slips;
        this.trustedMembers = trustedMembers;
        this.rankAmount = rankAmount;
        this.boughtAmount = boughtAmount;
        this.truncatedName = truncatedName;
        this.idNumber = id;
        this.isPublic = isPublic;
    }

    public void updateProfile(String truncatedName)
    {
        this.truncatedName = truncatedName;
    }

    public boolean isProfile(UUID uuid)
    {
        return this.uuid.equals(uuid);
    }

    public void addTrustedMember(UUID uuid)
    {
        trustedMembers.add(uuid);
    }

    public void removeTrustedMember(UUID uuid)
    {
        trustedMembers.remove(uuid);
    }

    public List<UUID> getTrustedMembersUUIDList()
    {
        return trustedMembers;
    }

    public Slip getNextSlip(Location signLocation)
    {
        boolean getSlip = false;
        Slip returnSlip = slips.get(0);

        for(Slip slip : slips)
        {
            if(getSlip)
            {
                returnSlip = slip;
                break;
            }
            if(slip.getSignLocation().equals(signLocation))
                getSlip = true;
        }
        return returnSlip;
    }

    public boolean canAddSlip()
    {
        return slips.size() < rankAmount + boughtAmount;
    }

    public void addSlip(Slip slip)
    {
        slips.add(slip);
    }

    public List<Slip> getSlips()
    {
        return slips;
    }

    public int getAmountOfSlips()
    {
        return slips.size();
    }

    public int getMaxSlipAmount()
    {
        return rankAmount + boughtAmount;
    }

    public UUID getUUID()
    {
        return uuid;
    }

    public int getRankAmount()
    {
        return rankAmount;
    }

    public void setRankAmount(int i)
    {
        this.rankAmount = i;
    }

    public int getBoughtAmount()
    {
        return boughtAmount;
    }

    public void incrementBoughtAmount(int i)
    {
        this.boughtAmount += i;
    }

    public void decrementBoughtAmount(int i)
    {
        this.boughtAmount -= i;
    }

    public String getUserID()
    {
        return truncatedName + "#" + idNumber;
    }

    public String getTruncatedName()
    {
        return truncatedName;
    }

    public int getIdNumber()
    {
        return idNumber;
    }

    public void clearSlips()
    {
        slips.clear();
    }
    public boolean getIsPublic()
    {
        return isPublic;
    }

    public boolean isTrusted(UUID uuid)
    {
        return trustedMembers.contains(uuid);
    }

    public void setIsPublic(boolean isPublic)
    {
        this.isPublic = isPublic;
    }

    public boolean contains(Location loc)
    {
        for(Slip slip: slips)
        {
            if(slip.getSignLocation().equals(loc))
                return true;
        }
        return false;
    }

    public boolean removeSlip(Location loc)
    {
        for(Slip slip: slips)
        {
            if(slip.getSignLocation().equals(loc))
                return slips.remove(slip);
        }
        return false;
    }

    public List<String> getTrustedMemberNames()
    {
        List<String> playerNames = new LinkedList<>();
        for(UUID uuid : getTrustedMembersUUIDList())
        {
            OfflinePlayer member = Bukkit.getOfflinePlayer(uuid);
            playerNames.add(member.getName());
        }
        return playerNames;
    }

    public Slip getRandomSlip()
    {
        Random randomSlip = new Random();
        int slipId = randomSlip.nextInt(slips.size());
        return slips.get(slipId);
    }
}
