package com.belinski20.slipdisk;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Identities {

    Map<String, UUID> identityMap;

    public Identities()
    {
        identityMap = new HashMap<>();
    }

    public void addIdentity(String name, UUID uuid)
    {
        identityMap.put(name, uuid);
    }

    public boolean contains(String name)
    {
        return identityMap.containsKey(name);
    }

    public UUID getIdentity(String name)
    {
        return identityMap.get(name);
    }

    public int getIdentityAmount()
    {
        return identityMap.size();
    }

    public boolean updateIdentity(Player player)
    {
        UUID uuid = player.getUniqueId();
        Profile profile = Slipdisk.s.profileUtils.getProfile(uuid);

        if(profile == null)
            return false;

        for(Map.Entry<String, UUID> identity : identityMap.entrySet())
            if(identity.getValue().equals(uuid))
            {
                if(identity.getKey().equals(profile.getUserID()))
                    return false;
                identityMap.remove(identity.getKey());
                Slipdisk.s.profileList.remove(profile);
                profile.updateProfile(Slipdisk.s.profileUtils.truncateUserName(player.getName()));
                Slipdisk.s.profileList.add(profile);
                identityMap.put(profile.getUserID(), uuid);
                return true;
            }
        return false;
    }

    public String getUserID(UUID uuid)
    {
        for(Map.Entry<String, UUID> identity : identityMap.entrySet())
            if(identity.getValue().equals(uuid))
            {
                return identity.getKey();
            }
        return "";
    }

}
