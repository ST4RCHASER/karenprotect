package me.starchaser.karenprotect.adaptor;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import javax.swing.plaf.synth.Region;
import java.util.ArrayList;


public class wg7 implements WorldGuardWrapper{
    public wg7(boolean force){
        if (force) {
            Bukkit.getLogger().info("KarenProtect: Wrappering to wg7 (FORCE)");
        }else {
            Bukkit.getLogger().info("KarenProtect: Wrappering to wg7");
        }
    }

    @Override
    public RegionManager getRegionManager(org.bukkit.World world) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
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
        return Flags.fuzzyMatchFlag(WorldGuard.getInstance().getFlagRegistry(), flagName);
    }

    @Override
    public String getWGver() {
        return "7";
    }

    @Override
    public boolean isCanBuild(WorldGuardPlugin wg , Player player , Block block , LocalPlayer lp, RegionManager rgm , ProtectedRegion rgn) {
        try {
            ApplicableRegionSet applicableRegions = rgm.getApplicableRegions(rgn);
            return applicableRegions.testState(lp, (StateFlag) getFlag("BUILD")); //WG7 MC 1.13
        }catch (Exception exa) {
            return wg.createProtectionQuery().testBlockPlace(player, block.getLocation(), block.getType());  //WG7 MC 1.13 OTHER WAY
        }
    }
}
