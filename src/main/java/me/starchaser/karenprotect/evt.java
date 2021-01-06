package me.starchaser.karenprotect;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.starchaser.karenprotect.WGRegionEvents.events.RegionEnterEvent;
import me.starchaser.karenprotect.WGRegionEvents.events.RegionLeaveEvent;
import me.starchaser.karenprotect.adaptor.WorldGuardWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

import static me.starchaser.karenprotect.colorystarry.getID_List;
import static me.starchaser.karenprotect.colorystarry.getKPProtectedLocection;
import static me.starchaser.karenprotect.colorystarry.wrapper;

public class evt implements Listener {
    private HashMap<String, Long> player_cooldown_time = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (colorystarry.conffetibox.getConfig().getBoolean("liquid_protect.enable")) {
            if (event.getPlayer().hasPermission("karenprotect.bypass") || event.getPlayer().hasPermission("karenprotect.*") || event.getPlayer().isOp())
                return;
            RegionManager rgm = colorystarry.wrapper.getRegionManager(event.getPlayer().getWorld());
            int x = event.getBlockClicked().getX();
            int y = event.getBlockClicked().getY();
            int z = event.getBlockClicked().getZ();
            String id = colorystarry.getRGN(rgm, event.getBlockClicked(), getID_List(x, y, z, rgm), event.getPlayer());
            ProtectedRegion rgn = rgm.getRegion(id);
            if (rgn != null) return;
            if (event.getBucket() == Material.LAVA_BUCKET || event.getBucket() == Material.WATER_BUCKET) {
                event.getPlayer().sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.conffetibox.getConfig().getString("liquid_protect.message").replaceAll("&", "§"));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPistonExtend(BlockPistonExtendEvent e) {
        if (colorystarry.Piston_Check(e.getBlock(), e.getBlocks())) e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPistonRetract(BlockPistonRetractEvent e) {
        if (colorystarry.Piston_Check(e.getBlock(), e.getBlocks())) e.setCancelled(true);
    }

    @EventHandler
    public void onRegionEnter(RegionEnterEvent e) {
        Player p = e.getPlayer();
        ProtectedRegion rgn = e.getRegion();
        KPBlock kpBlock = colorystarry.getBlocksKP(rgn, p);
        if (kpBlock != null) {
            if (kpBlock.Run_PlayerEntry_Event(e.getPlayer(), rgn)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onRegionExit(RegionLeaveEvent e) {
        Player p = e.getPlayer();
        ProtectedRegion rgn = e.getRegion();
        KPBlock kpBlock = colorystarry.getBlocksKP(rgn, p);
        if (kpBlock != null) {
            if (kpBlock.Run_PlayerLeave_Event(e.getPlayer(), rgn)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent e) {
        Block pb = e.getBlock();
        Player p = e.getPlayer();
        WorldGuardPlugin wg = (WorldGuardPlugin) colorystarry.wgd;
        WorldGuardWrapper wgWrapper = colorystarry.wrapper;
        RegionManager rgm = wgWrapper.getRegionManager(p.getWorld());
        double x = pb.getLocation().getX();
        double y = pb.getLocation().getY();
        double z = pb.getLocation().getZ();
        ItemStack im = e.getItemInHand();
        LocalPlayer lp = wg.wrapPlayer(p);
        ProtectedRegion rgn = rgm.getRegion(colorystarry.getRGN(rgm, e.getBlock(), getID_List(x, y, z, rgm), e.getPlayer()));
        if (rgn == null) rgm.getRegion("__global__");
        if (!colorystarry.KPID_List().contains(im.getType().toString()) || im.getItemMeta().getDisplayName() == null || im.getItemMeta().getLore() == null) {
            Player player = e.getPlayer();
            String idb = colorystarry.getRGN(rgm, pb, getID_List(x, y, z, rgm), e.getPlayer());
            rgn = rgm.getRegion(idb);
            if (rgn == null) {
                if (!colorystarry.ActFirst_Check(player)) {
                    e.setCancelled(true);
                    return;
                }
            }
            return;
        }
        KPBlock kpBlock = colorystarry.getBlocksKP(im.getType().toString());
        String display_name = im.getItemMeta().getDisplayName();
        boolean b_1 = im.getItemMeta() != null;
        boolean b_2 = im.getItemMeta().getDisplayName() != null;
        boolean b_3 = im.getItemMeta().getLore() != null;
        boolean b_4 = kpBlock != null;
        String display_name_base = kpBlock.getBlock_name();
        boolean b_5 = display_name.contains(display_name_base);
        boolean b_6 = false;
        if (b_1 && b_2 && b_3) {
            b_6 = im.getItemMeta().getLore().containsAll(kpBlock.getBlock_lore());
        }
        if (b_5 == false) {
            b_5 = display_name_base.contains(display_name);
        }
        if (b_1 && b_2 && b_3 && b_4 && b_5 && b_6) {
            if (kpBlock.RequiredPermission()) {
                if (!p.hasPermission("karenprotect.block." + kpBlock.getBlock_id().toUpperCase()) && !p.hasPermission("karenprotect.block." + kpBlock.getBlock_id().toLowerCase()) && !p.isOp() && !p.hasPermission("karenprotect.block.admin") && !p.hasPermission("karenprotect.block.*")) {
                    p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("place_noperm", true));
                    e.setCancelled(true);
                    return;
                }
            }
            if (colorystarry.conffetibox.getConfig().getString("blockplacecooldown.enable") != null && colorystarry.conffetibox.getConfig().getString("blockplacecooldown.time") != null) {
                if (colorystarry.conffetibox.getConfig().getBoolean("blockplacecooldown.enable")) {
                    if (player_cooldown_time.get(p.getName()) != null) {
                        p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("blockplacecooldown.message", false).replaceAll("<time>", String.valueOf(player_cooldown_time.get(p.getName()) / 1000)));
                        e.setCancelled(true);
                        return;
                    } else {
                        player_cooldown_time.put(p.getName(), (long) (colorystarry.conffetibox.getConfig().getInt("blockplacecooldown.time") * 1000));
                        BukkitRunnable delay_place = new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (player_cooldown_time.get(p.getName()) == null) this.cancel();
                                if (player_cooldown_time.get(p.getName()) < 1) {
                                    player_cooldown_time.remove(p.getName());
                                    this.cancel();
                                } else {
                                    player_cooldown_time.replace(p.getName(), player_cooldown_time.get(p.getName()) - 1000);
                                }
                            }
                        };
                        delay_place.runTaskTimerAsynchronously(colorystarry.conffetibox, 20L, 20L);
                    }
                }
            }
            int count = colorystarry.wrapper.getRegionManager(p.getWorld()).getRegionCountOfPlayer(lp);
            int max = 0;
            for (PermissionAttachmentInfo rawperm : p.getEffectivePermissions()) {
                String perm = rawperm.getPermission();
                if (perm.startsWith("karenprotect.protect.limit.")) {
                    try {
                        int lim = Integer.parseInt(perm.substring(27));
                        if (lim > max) {
                            max = lim;
                        }
                    } catch (Exception er) {
                        max = 0;
                    }
                }
            }
            if (count >= max) {
                if (max != 0) {
                    e.getPlayer().sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("block_limit", true));
                    e.setCancelled(true);
                    return;
                }
            }

            for (String worldname : colorystarry.conffetibox.getConfig().getStringList("disable_worlds")) {
                if (worldname.equalsIgnoreCase(e.getPlayer().getWorld().getName())) {
                    e.getPlayer().sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("world_disable", true));
                    e.setCancelled(true);
                    return;
                }
            }
            if (colorystarry.debug) {
                Bukkit.broadcastMessage(rgm.getClass().getName());
            }
            boolean is_can_build = wgWrapper.isCanBuild(wg, e.getPlayer(), e.getBlock(), lp, rgm, rgn);
            if (is_can_build) {
                double bx = pb.getLocation().getX();
                double by = pb.getLocation().getY();
                double bz = pb.getLocation().getZ();
                double blockrage_x = colorystarry.getBlocksKP(im.getType().toString()).getSize_x();
                double blockrage_y = colorystarry.getBlocksKP(im.getType().toString()).getSize_y();
                double blockrage_z = colorystarry.getBlocksKP(im.getType().toString()).getSize_z();
                ProtectedRegion region;
                String id;
                try {
                    Vector v1, v2;
                    v1 = new Vector((bx + blockrage_x), (by + blockrage_y), (bz + blockrage_z));
                    v2 = new Vector((bx - blockrage_x), (by - blockrage_y), (bz - blockrage_z));
                    id = "karen" + (int) bx + "x" + (int) by + "y" + (int) bz + "z";
                    rgm = colorystarry.wrapper.getRegionManager(p.getWorld());
                    BlockVector bmin = v1.toBlockVector();
                    BlockVector bmax = v2.toBlockVector();
                    region = new ProtectedCuboidRegion(id, bmin, bmax);
                } catch (NoClassDefFoundError exx) {
                    Vector3 v1, v2;
                    v1 = Vector3.at((bx + blockrage_x), (by + blockrage_y), (bz + blockrage_z));
                    v2 = Vector3.at((bx - blockrage_x), (by - blockrage_y), (bz - blockrage_z));
                    id = "karen" + (int) bx + "x" + (int) by + "y" + (int) bz + "z";
                    rgm = colorystarry.wrapper.getRegionManager(p.getWorld());
                    BlockVector3 bmin = v1.toBlockPoint();
                    BlockVector3 bmax = v2.toBlockPoint();
                    region = new ProtectedCuboidRegion(id, bmin, bmax);
                }
                region.getOwners().addPlayer(p.getName());
                rgm.addRegion(region);
                boolean overLap = rgm.overlapsUnownedRegion(region, lp);
                if (overLap && !p.hasPermission("karenprotect.overlaps")) {
                    rgm.removeRegion(id);
                    try {
                        rgm.save();
                    } catch (Exception ea) {
                        System.out.println("KarenProtect: WorldGuard Error [" + e + "] during Region File Save");
                    }
                    p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("overlaps", true));
                    e.setCancelled(true);
                    return;
                }
                for (String str : colorystarry.flags) {
                    FlagHandler fh = new FlagHandler();
                    str = str.replaceAll("&", "§").replaceAll("<z>", String.valueOf(blockrage_z)).replaceAll("<y>", String.valueOf(blockrage_y)).replaceAll("<x>", String.valueOf(blockrage_x)).replaceAll("<player>", p.getName()).replaceAll("<name>", kpBlock.getKit_name());
                    String[] flag_data = str.split("[ ]");
                    ArrayList<String> flag_data2 = new ArrayList<>(Arrays.asList(flag_data));
                    flag_data2.add(0, "flag");
                    if (colorystarry.debug) {
                        for (String f : flag_data2) {
                            Bukkit.broadcastMessage(f);
                        }
                    }
                    String[] new_flag_data = ((Collection<String>) flag_data2).toArray(new String[flag_data2.size()]);
                    fh.setFlag(new_flag_data, region, p, false);
                }
                try {
                    rgm.saveChanges();
                    rgm.save();
                    p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("created", true));
                    colorystarry.getBlocksKP(colorystarry.getItemInHand(p).getType().toString().toUpperCase()).Run_PlayerPlace_Event(e.getPlayer(), region);
                } catch (StorageException e1) {
                    e1.printStackTrace();
                }
            } else {
                p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("deny_protect", true));
                e.setCancelled(true);
            }
        } else {
            if (!colorystarry.ActFirst_Check(e.getPlayer())) {
                e.setCancelled(true);
            }

        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent e) {
        if (colorystarry.getItemInHand(e.getPlayer()) != null && colorystarry.getItemInHand(e.getPlayer()).getEnchantments().size() > 0 && !e.isCancelled()) {
            if (colorystarry.getItemInHand(e.getPlayer()).getEnchantments().keySet().contains(Enchantment.SILK_TOUCH)) {
                for (SilkTouchItem silkTouchItems : colorystarry.silkTouchItems) {
                    if (silkTouchItems.getTargetItemID().equalsIgnoreCase(e.getBlock().getType().toString())) {
                        e.setCancelled(true);
                        e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), silkTouchItems.getItem());
                        e.getBlock().setType(Material.AIR);
                    }
                }
            }
        }
    }

    @SuppressWarnings("Duplicates")
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent e) {
        Block pb = e.getBlock();
        Player player = e.getPlayer();
        WorldGuardPlugin wg = (WorldGuardPlugin) colorystarry.wgd;
        RegionManager rgm = colorystarry.wrapper.getRegionManager(player.getWorld());
        double x = pb.getLocation().getX();
        double y = pb.getLocation().getY();
        double z = pb.getLocation().getZ();
        String id = colorystarry.getRGN(rgm, pb, getID_List(x, y, z, rgm), e.getPlayer());
        ProtectedRegion rgn = rgm.getRegion(id);
        String set_id = "karen" + (int) x + "x" + (int) y + "y" + (int) z + "z";
        if (rgn == null) {
            if (colorystarry.ActFirst_Check(e.getPlayer())) {
                return;
            } else {
                e.setCancelled(true);
                return;
            }
        }
        if (!rgn.getId().equals(set_id)) return;
        if (colorystarry.KPID_List().contains(e.getBlock().getType().toString()) && rgm.getRegion(id) != null) {
            LocalPlayer localPlayer = wg.wrapPlayer(player);
            boolean bypass_check = false;
            File file1 = new File(colorystarry.conffetibox.getDataFolder().getAbsolutePath() + File.separator + "hide_protection" + ".yml");
            if (file1.exists()) {
                YamlReader hide_file = new YamlReader(file1.getAbsolutePath());
                if (hide_file.getConfigSelection("hide_list." + rgn.getId()) != null) {
                    bypass_check = true;
                } else {
                    bypass_check = false;
                }
            }
            if (bypass_check == false) {
                if (rgn.isOwner(localPlayer) || player.hasPermission("karenprotect.superowner") || player.hasPermission("karenprotect.*") || player.hasPermission("karenprotect.destroy") || player.isOp()) {
                    e.setCancelled(true);
                    e.setExpToDrop(0);
                    colorystarry.getBlocksKP(rgn, player).Run_PlayerBreak_Event(e.getPlayer(), rgn);
                    if (!colorystarry.getBlocksKP(rgn, player).is_nodrop()) {
                        colorystarry.give_block(pb.getType().toString(), player, e.getBlock().getLocation());
                        player.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("remove", true));
                    } else {
                        player.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("remove_nodrop", true));
                    }
                    e.getBlock().setType(Material.AIR);
                    rgm.removeRegion(id);
                } else {
                    player.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("remove_deny", true));
                    e.setCancelled(true);
                }
            }
        }
    }

    @SuppressWarnings("Duplicates")
    @EventHandler
    public void onDispense(BlockDispenseEvent Event) {
        if (Event.getItem().getType() == Material.WATER_BUCKET || Event.getItem().getType() == Material.LAVA_BUCKET) {
            if (colorystarry.conffetibox.getConfig().getBoolean("liquid_protect.enable")) {
                Block pb = Event.getBlock();
                RegionManager rgm = colorystarry.wrapper.getRegionManager(pb.getWorld());
                double x = pb.getLocation().getX();
                double y = pb.getLocation().getY();
                double z = pb.getLocation().getZ();
                String id = colorystarry.getRGN(rgm, pb, getID_List(x, y, z, rgm), null);
                ProtectedRegion rgn = rgm.getRegion(id);
                if (rgn == null) {
                    for (Entity en : Event.getBlock().getLocation().getWorld().getNearbyEntities(Event.getBlock().getLocation(), 3, 3, 3)) {
                        if (en instanceof Player) {
                            en.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("liquid_protect.message", false));
                        }
                    }
                    Event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent evt) {
        RegionManager rgm = wrapper.getRegionManager(evt.getLocation().getWorld());
        Block block = evt.getLocation().getBlock();
        Location loc = getKPProtectedLocection(colorystarry.getRGN(rgm, block, getID_List(evt.getLocation().getX(), evt.getLocation().getY(), evt.getLocation().getZ(), rgm), null), block.getWorld());
        if (loc != null) {
            ArrayList<Block> newList = new ArrayList<>();
            for (Block block2 : evt.blockList()) {
                if (colorystarry.getBlocksKP(block2.getType().toString()) != null) {
                    if (!block2.getLocation().equals(loc)) {
                        newList.add(block2);
                    }
                }
            }
            evt.blockList().clear();
            evt.blockList().addAll(newList);
        }
    }
}