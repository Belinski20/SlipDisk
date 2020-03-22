package com.belinski20.slipdisk;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Set;

public class PermissionIntegration {

    private Plugin plugin;
    private File permissionPath;
    private FileConfiguration enteredPermissionPath;
    private String foundPermPath;
    private File permission;
    private FileConfiguration permFile;

    PermissionIntegration(Plugin plugin)
    {
        this.plugin = plugin;
        this.permissionPath = new File(plugin.getDataFolder(), "permission.yml");
        this.enteredPermissionPath = (FileConfiguration) YamlConfiguration.loadConfiguration(this.permissionPath);
        this.foundPermPath = enteredPermissionPath.getString("PermissionLocation");
        this.permission = new File(this.foundPermPath, "permissions.yml");
        this.permFile = (FileConfiguration) YamlConfiguration.loadConfiguration(this.permission);
    }

    public void getPermissionsFile()
    {

    }

    /*public void getRankFromPermissions(Player player)
    {
        Set<String> roles = this.dataConfig.getDatabaseConfig().getConfigurationSection("databaseroles.").getKeys(false);
        String playerCred = searchUsernameAndUUID(uuid, player);
        String playerRole = getPermissionsFile().getString("users." + playerCred + ".group");
        String[] playerRanks = playerRole.split("\\s+");
        for (int i = 0; i < playerRanks.length; i++)
            playerRanks[i] = playerRanks[i].replaceAll("[^\\w]", "");
        for (String role : roles) {
            String rankString = this.dataConfig.getDatabaseConfig().getString("databaseroles." + role + ".permission_roles");
            String[] ranks = rankString.split("\\s+");
            for (int j = 0; j < ranks.length; j++)
                ranks[j] = ranks[j].replaceAll("[^\\w]", "");
            for (String rank : ranks) {
                rank.substring(1, rank.length() - 1);
                for (String playerRank : playerRanks) {
                    if (playerRank.equals(rank))
                        return role;
                }
            }
        }
        return "user";
    }*/
}
