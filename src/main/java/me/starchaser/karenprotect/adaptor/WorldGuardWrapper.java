package me.starchaser.karenprotect.adaptor;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public interface WorldGuardWrapper {
    RegionManager getRegionManager(World world);
    ArrayList<String> getPlayers(DefaultDomain members);
    Flag<?> getFlag(String flagName);
    String getWGver();
    boolean isCanBuild(WorldGuardPlugin wg , Player player , Block block , LocalPlayer lp, RegionManager rgm , ProtectedRegion rgn);
}
