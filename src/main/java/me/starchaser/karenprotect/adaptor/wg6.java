package me.starchaser.karenprotect.adaptor;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.starchaser.karenprotect.colorystarry;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class wg6 implements WorldGuardWrapper {

    public wg6(boolean force){
        if (force) {
            Bukkit.getLogger().info("KarenProtect: Wrappering to wg6 (FORCE)");
        }else {
            Bukkit.getLogger().info("KarenProtect: Wrappering to wg6");
        }
    }

    @Override
    public RegionManager getRegionManager(World world) {
        WorldGuardPlugin wg = (WorldGuardPlugin) colorystarry.wgd;
        return wg.getRegionManager(world);
    }

    @Override
    public ArrayList<String> getPlayers(DefaultDomain members) {
        ArrayList<String> list = new ArrayList<>();
        for (Object pl : members.getPlayers()) {
            list.add(pl.toString());
        }
        return list;
    }

    @Override
    public Flag<?> getFlag(String flagName) {
        return DefaultFlag.fuzzyMatchFlag(flagName); // < WG lower 6.1 6.0;
    }

    @Override
    public String getWGver() {
        return "6";
    }

    @Override
    public boolean isCanBuild(WorldGuardPlugin wg , Player player , Block block , LocalPlayer lp, RegionManager rgm , ProtectedRegion rgn) {
        try {
            return wg.canBuild(player, block.getLocation());
        }catch (Exception e1) {
            return  wg.canBuild(player, block); //DEFAULT WG6
        }
    }
}
