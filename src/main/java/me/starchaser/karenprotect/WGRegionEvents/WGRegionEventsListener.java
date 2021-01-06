package me.starchaser.karenprotect.WGRegionEvents;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import me.starchaser.karenprotect.WGRegionEvents.events.RegionLeaveEvent;
import me.starchaser.karenprotect.WGRegionEvents.events.RegionLeftEvent;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import me.starchaser.karenprotect.colorystarry;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class WGRegionEventsListener implements org.bukkit.event.Listener {
    private WorldGuardPlugin wgPlugin;
    private colorystarry plugin;
    private Map<Player, Set<ProtectedRegion>> playerRegions;

    public WGRegionEventsListener(colorystarry plugin, WorldGuardPlugin wgPlugin) {
        this.plugin = plugin;
        this.wgPlugin = wgPlugin;

        playerRegions = new java.util.HashMap();
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        Set<ProtectedRegion> regions = (Set) playerRegions.remove(e.getPlayer());
        if (regions != null) {
            for (ProtectedRegion region : regions) {
                RegionLeaveEvent leaveEvent = new RegionLeaveEvent(region, e.getPlayer(), MovementWay.DISCONNECT, e);
                RegionLeftEvent leftEvent = new RegionLeftEvent(region, e.getPlayer(), MovementWay.DISCONNECT, e);

                plugin.getServer().getPluginManager().callEvent(leaveEvent);
                plugin.getServer().getPluginManager().callEvent(leftEvent);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Set<ProtectedRegion> regions = (Set) playerRegions.remove(e.getPlayer());
        if (regions != null) {
            for (ProtectedRegion region : regions) {
                RegionLeaveEvent leaveEvent = new RegionLeaveEvent(region, e.getPlayer(), MovementWay.DISCONNECT, e);
                RegionLeftEvent leftEvent = new RegionLeftEvent(region, e.getPlayer(), MovementWay.DISCONNECT, e);

                plugin.getServer().getPluginManager().callEvent(leaveEvent);
                plugin.getServer().getPluginManager().callEvent(leftEvent);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        e.setCancelled(updateRegions(e.getPlayer(), MovementWay.MOVE, e.getTo(), e));
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        e.setCancelled(updateRegions(e.getPlayer(), MovementWay.TELEPORT, e.getTo(), e));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        updateRegions(e.getPlayer(), MovementWay.SPAWN, e.getPlayer().getLocation(), e);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        updateRegions(e.getPlayer(), MovementWay.SPAWN, e.getRespawnLocation(), e);
    }


    private synchronized boolean updateRegions(final Player player, final MovementWay movement, Location to, final PlayerEvent event) {
        Set<ProtectedRegion> regions;
        if (playerRegions.get(player) == null) {
            regions = new HashSet();
        } else {
            regions = new HashSet((Collection) playerRegions.get(player));
        }

        Set<ProtectedRegion> oldRegions = new HashSet(regions);

        com.sk89q.worldguard.protection.managers.RegionManager rm = colorystarry.wrapper.getRegionManager(to.getWorld());

        if (rm == null) {
            return false;
        }

        ApplicableRegionSet appRegions;
        try {
            double x = to.getX();
            double y = to.getY();
            double z = to.getZ();
            Vector v = new Vector(x, y, z);
            appRegions = rm.getApplicableRegions(v);
        } catch (NoClassDefFoundError ea) {
            double x = to.getX();
            double y = to.getY();
            double z = to.getZ();
            BlockVector3 v = BlockVector3.at(x, y, z);
            appRegions = rm.getApplicableRegions(v);
        }

        for (final ProtectedRegion region : appRegions) {
            if (!regions.contains(region)) {
                me.starchaser.karenprotect.WGRegionEvents.events.RegionEnterEvent e = new me.starchaser.karenprotect.WGRegionEvents.events.RegionEnterEvent(region, player, movement, event);

                plugin.getServer().getPluginManager().callEvent(e);

                if (e.isCancelled()) {
                    regions.clear();
                    regions.addAll(oldRegions);

                    return true;
                }


                org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

                    public void run() {

                        me.starchaser.karenprotect.WGRegionEvents.events.RegionEnteredEvent e = new me.starchaser.karenprotect.WGRegionEvents.events.RegionEnteredEvent(region, player, movement, event);

                        plugin.getServer().getPluginManager().callEvent(e);
                    }
                }, 1L);
                regions.add(region);
            }
        }


        Collection<ProtectedRegion> app = appRegions.getRegions();
        Object itr = regions.iterator();
        while (((Iterator) itr).hasNext()) {
            final ProtectedRegion region = (ProtectedRegion) ((Iterator) itr).next();
            if (!app.contains(region)) {
                if (rm.getRegion(region.getId()) != region) {
                    ((Iterator) itr).remove();
                } else {
                    RegionLeaveEvent e = new RegionLeaveEvent(region, player, movement, event);

                    plugin.getServer().getPluginManager().callEvent(e);

                    if (e.isCancelled()) {
                        regions.clear();
                        regions.addAll(oldRegions);
                        return true;
                    }


                    org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

                        public void run() {
                            RegionLeftEvent e = new RegionLeftEvent(region, player, movement, event);

                            plugin.getServer().getPluginManager().callEvent(e);
                        }
                    }, 1L);
                    ((Iterator) itr).remove();
                }
            }
        }
        playerRegions.put(player, regions);
        return false;
    }
}
