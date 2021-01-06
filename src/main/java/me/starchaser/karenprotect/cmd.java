package me.starchaser.karenprotect;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static me.starchaser.karenprotect.colorystarry.*;

public class cmd implements CommandExecutor {
    private static void send_help(CommandSender s, ProtectedRegion rgn) {
        StringBuilder flag_list = new StringBuilder();
        StringBuilder member_list = new StringBuilder("§f");
        StringBuilder owner_list = new StringBuilder("§f");
        if (rgn != null) {
            if (rgn.getFlags() != null) {
                for (Map.Entry<Flag<?>, Object> ra : rgn.getFlags().entrySet()) {
                    String flag_key;
                    if (ra.getValue().toString().equalsIgnoreCase("allow")) {
                        flag_key = ra.getValue().toString().toLowerCase();
                        flag_key = flag_key.replaceAll("§", "&");
                        flag_key = "§a" + flag_key;
                    } else if (ra.getValue().toString().equalsIgnoreCase("deny")) {
                        flag_key = ra.getValue().toString().toLowerCase();
                        flag_key = flag_key.replaceAll("§", "&");
                        flag_key = "§c" + flag_key;
                    } else {
                        flag_key = ra.getValue().toString();
                        flag_key = flag_key.replaceAll("§", "&");
                        flag_key = "§e" + flag_key;
                    }
                    flag_list.append("§f, §b").append(ra.getKey().getName()).append("§f: ").append(flag_key);
                }
            }
            boolean a = true;
            if (rgn.getMembers() != null) {
                for (String pl : wrapper.getPlayers(rgn.getMembers())) {
                    if (a) {
                        member_list.append(", §f").append(pl);
                        a = false;
                    } else {
                        member_list.append(", §a").append(pl);
                        a = true;
                    }
                }
            }
            a = true;
            if (rgn.getOwners() != null) {
                for (String pl : wrapper.getPlayers(rgn.getOwners())) {
                    if (a) {
                        owner_list.append(", §f").append(pl);
                        a = false;
                    } else {
                        owner_list.append(", §b").append(pl);
                        a = true;
                    }
                }
            }
        }
        owner_list = new StringBuilder(owner_list.toString().replaceFirst(", ", ""));
        member_list = new StringBuilder(member_list.toString().replaceFirst(", ", ""));
        flag_list = new StringBuilder(flag_list.toString().replaceFirst(", ", ""));
        String rgn_id;
        String Priority;
        if (rgn == null) {
            String em = getMessage("empty_data", true);
            owner_list = new StringBuilder(em);
            member_list = new StringBuilder(em);
            flag_list = new StringBuilder(em);
            rgn_id = em;
            Priority = em;
        } else {
            rgn_id = rgn.getId();
            Priority = String.valueOf(rgn.getPriority());
        }
        for (String text : colorystarry.help_page) {
            s.sendMessage(text
                    .replaceAll("&", "§")
                    .replaceAll("<priority>", Priority)
                    .replaceAll("<id>", rgn_id)
                    .replaceAll("<flags>", flag_list.toString())
                    .replaceAll("<members>", member_list.toString())
                    .replaceAll("<owners>", owner_list.toString())
                    .replaceAll("<ver_worldguard>", Bukkit.getPluginManager().getPlugin("WorldGuard").getDescription().getVersion())
                    .replaceAll("<ver_worldedit>", Bukkit.getPluginManager().getPlugin("WorldEdit").getDescription().getVersion())
                    .replaceAll("<version>", conffetibox.getDescription().getVersion()));
            //.replaceAll("<version>", "1.3"));
        }
    }

    private static boolean hasAccess(ProtectedRegion region, Player p, LocalPlayer lp, boolean canBeMember) {
        if (region == null) {
            // Region is not valid
            return false;
        }
        return p.isOp() || p.hasPermission("karenprotect.*") || p.hasPermission("karenprotect.admin") || p.hasPermission("karenprotect.superowner") || region.isOwner(lp) || (canBeMember && region.isMember(lp));
    }

    @SuppressWarnings("Duplicates")
    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("karenprotect") || cmd.getName().equalsIgnoreCase("ps") || cmd.getName().equalsIgnoreCase("karenprotect") || cmd.getName().equalsIgnoreCase("kp")) {
            //conffetibox.getLogger().info("KarenProtect: FROM " + s.getClass());
            if ((s instanceof ConsoleCommandSender) || (s instanceof RemoteConsoleCommandSender)) {
                if (args.length < 1) {
                    s.sendMessage("KarenProtect: on console only allow use two command");
                    s.sendMessage("/kp give <id> <player>");
                    s.sendMessage("/kp reload");
                    return true;
                } else {
                    if (args[0].equalsIgnoreCase("reload")) {
                        return colorystarry.reload(s);
                    }
                    if (args[0].equalsIgnoreCase("give") && args.length == 3) {
                        Player target = colorystarry.getPlayer(args[2]);
                        if (target == null) {
                            s.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("target_not_online", true).replaceAll("<player>", args[2]));
                            return true;
                        }
                        String target_id = "#NONE#";
                        for (KPBlock kp : colorystarry.block_list) {
                            if (kp.getBlock_id().equalsIgnoreCase(args[1])) {
                                target_id = kp.getBlock_id();
                                break;
                            } else if (kp.getBlock_name().replaceAll("§", "&").contains(args[1])) {
                                target_id = kp.getBlock_id();
                            } else if (kp.getBlock_id().contains(args[1])) {
                                target_id = kp.getBlock_id();
                            }
                        }
                        if (!target_id.equalsIgnoreCase("#NONE#")) {
                            colorystarry.give_block(target_id.toUpperCase(), target, target.getLocation());
                            s.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("block_give", true).replaceAll("<id>", target_id).replaceAll("<player>", target.getName()));
                            return true;
                        } else {
                            s.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("block_not_found", true).replaceAll("<id>", args[1]));
                            return true;
                        }
                    } else {
                        s.sendMessage("KarenProtect: on console only allow use two command");
                        s.sendMessage("/kp give <id> <player>");
                        s.sendMessage("/kp reload");
                        return true;
                    }
                }
            }
            Player p = (Player) s;
            WorldGuardPlugin wg = (WorldGuardPlugin) colorystarry.wgd;
//            RegionManager rgm = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(p.getWorld()));
            if (debug) Bukkit.broadcastMessage(wrapper.getClass().getName());
            RegionManager rgm = wrapper.getRegionManager(p.getWorld());
            LocalPlayer localPlayer = wg.wrapPlayer(p);
            int usex = 0, usey = 0, usez = 0;
            double x = p.getLocation().getX();
            double y = p.getLocation().getY();
            double z = p.getLocation().getZ();
            String id = colorystarry.getRGN(rgm, null, getID_List(x,y,z,rgm), p);
            if (id.length() > 5 && id.substring(0, 5).equals("karen")) {
                int indexX = id.indexOf("x");
                int indexY = id.indexOf("y");
                int indexZ = id.length() - 1;
                double krx = Double.parseDouble(id.substring(5, indexX));
                double kry = Double.parseDouble(id.substring(indexX + 1, indexY));
                double krz = Double.parseDouble(id.substring(indexY + 1, indexZ));
                usex = (int) krx;
                usey = (int) kry;
                usez = (int) krz;
            }
            ProtectedRegion rgn = rgm.getRegion(id);
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("help")) {
                    send_help(s, rgn);
                    return true;
                }
                if (args[0].equalsIgnoreCase("stats")) {
                    if (!s.hasPermission("karenprotect.stats.others") && !s.hasPermission("karenprotect.stats") && !s.hasPermission("karenprotect.admin") && !s.hasPermission("karenprotect.*") && !s.isOp()) {
                        s.sendMessage(getMessage("plugin_prefix", true) + getMessage("no_perm", true));
                        return true;
                    }
                    String playerName;
                    if (args.length > 1) {
                        if (!s.hasPermission("karenprotect.stats.others") && !s.hasPermission("karenprotect.admin") && !s.hasPermission("karenprotect.*") && !s.isOp()) {
                         playerName = s.getName();
                        }else {
                            playerName = args[1];
                        }
                    } else {
                        playerName = s.getName();
                    }
                    OfflinePlayer ofp = Bukkit.getOfflinePlayer(playerName);
                    if (ofp != null && ofp.getFirstPlayed() > 0L) {
                        long firstPlayed = (System.currentTimeMillis() - Bukkit.getOfflinePlayer(playerName).getFirstPlayed()) / 86400000L;
                        long lastPlayed = (System.currentTimeMillis() - Bukkit.getOfflinePlayer(playerName).getLastPlayed()) / 86400000L;
                        String banMessage;
                        if (Bukkit.getOfflinePlayer(playerName).isBanned()) {
                            banMessage = getMessage("banded", true);
                        } else {
                            banMessage = getMessage("not_banded", true);
                        }
                        int count = 0;
                        boolean is_mema = false,is_own = false;
                        for (World w : Bukkit.getWorlds()) {
                            RegionManager rgmm = colorystarry.wrapper.getRegionManager(w);
                            for (ProtectedRegion pro : rgmm.getRegions().values()){
                                for (String member : pro.getMembers().getPlayers()) {
                                    if (member.equalsIgnoreCase(ofp.getName())) {
                                        is_mema = true;
                                        break;
                                    }
                                }
                                for (String member : pro.getOwners().getPlayers()) {
                                    if (member.equalsIgnoreCase(ofp.getName())) {
                                        is_own = true;
                                        break;
                                    }
                                }
                                if (is_mema || is_own) count++;
                                is_mema = false;
                                is_own = false;
                            }
                        }
                        for (String str : colorystarry.stats_page) {
                            String rgn_name = "";
                            String status = "";
                            int xu,yu,zu;
                            if (str.equalsIgnoreCase("<region_data>")) {
                                s.sendMessage(getMessage("stats_format_world", true));
                                for (World w : Bukkit.getWorlds()) {
                                    s.sendMessage(getMessage("stats_format_world_display", true).replaceAll("<world_name>", w.getName()));
                                    RegionManager regionManager = colorystarry.wrapper.getRegionManager(w);
                                    for (ProtectedRegion protectedRegion : regionManager.getRegions().values()) {
                                        boolean is_mem = false;
                                        if (protectedRegion.getId().startsWith("karen")) {
                                            rgn_name = protectedRegion.getId();
                                            for (String member : protectedRegion.getMembers().getPlayers()) {
                                                if (member.equalsIgnoreCase(ofp.getName())) {
                                                    is_mem = true;
                                                    status = getMessage("stats_format_member", true);
                                                    break;
                                                }
                                            }
                                            for (String member : protectedRegion.getOwners().getPlayers()) {
                                                if (member.equalsIgnoreCase(ofp.getName())) {
                                                    is_mem = true;
                                                    status = getMessage("stats_format_owner", true);
                                                    break;
                                                }
                                            }
                                        }
                                        if (!is_mem) continue;
                                        Location location_kp = colorystarry.getKPProtectedLocection(protectedRegion, w);
                                        xu = (int) location_kp.getX();
                                        yu = (int) location_kp.getY();
                                        zu = (int) location_kp.getZ();
                                        s.sendMessage(getMessage("stats_format_data", true)
                                                .replaceAll("<id>", rgn_name)
                                                .replaceAll("<status>", status)
                                                .replaceAll("<x>", String.valueOf(xu))
                                                .replaceAll("<y>", String.valueOf(yu))
                                                .replaceAll("<z>", String.valueOf(zu))
                                        );
                                    }
                                }
                            } else {
                                s.sendMessage(
                                        str.replaceAll("<player>", ofp.getName())
                                                .replaceAll("<ban>", banMessage)
                                                .replaceAll("<first_played>", String.valueOf(firstPlayed))
                                                .replaceAll("<last_played>", String.valueOf(lastPlayed))
                                                .replaceAll("<regions_count>", String.valueOf(count))
                                                .replaceAll("&", "§")
                                );
                            }
                        }
                    } else {
                        p.sendMessage(getMessage("plugin_prefix", true) + getMessage("stats_player_notfound", true).replaceAll("<player>" , args[1]));
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("hide")) {
                    if (hasAccess(rgn,p,localPlayer,false)) {
                        if (!s.hasPermission("karenprotect.hide")&& !s.hasPermission("karenprotect.admin") && !s.hasPermission("karenprotect.*") && !s.isOp()) {
                            s.sendMessage(getMessage("plugin_prefix", true) + getMessage("hide_no_perm", true));
                            return true;
                        }
                    }else {
                        s.sendMessage(getMessage("plugin_prefix", true) + getMessage("hide_deny", true));
                        return true;
                    }
                    Location loc  = getKPProtectedLocection(rgn , ((Player) s).getWorld());
                    File file1 = new File(colorystarry.conffetibox.getDataFolder().getAbsolutePath()+File.separator+"hide_protection"+".yml");
                    if (!file1.exists()) {
                        try {
                            FileWriter writer = new FileWriter(file1);
                            writer.write("created_time: " + System.currentTimeMillis());
                            writer.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                            colorystarry.conffetibox.getLogger().severe("Error on saving hide data...");
                        }
                    }
                    YamlReader hide_file = new YamlReader(file1.getAbsolutePath());
                    if (loc.getBlock().getType() == Material.AIR) {
                        s.sendMessage(getMessage("plugin_prefix", true) + getMessage("hide_already", true));
                        return true;
                    }
                    for (KPBlock kpBlock : colorystarry.block_list) {
                        if (kpBlock.getMaterial().equals(loc.getBlock().getType()) && hide_file.getConfigSelection("hide_list." + rgn.getId()) != null) {
                            s.sendMessage(getMessage("plugin_prefix", true) + getMessage("hide_already", true));
                            return true;
                        }
                    }
                    hide_file.set("hide_list." + rgn.getId() + ".id" , loc.getBlock().getType().toString());
                    hide_file.set("hide_list." + rgn.getId() + ".x" , (int) loc.getBlock().getX());
                    hide_file.set("hide_list." + rgn.getId() + ".y" , (int) loc.getBlock().getY());
                    hide_file.set("hide_list." + rgn.getId() + ".z" , (int) loc.getBlock().getZ());
                    loc.getBlock().setType(Material.AIR);
                    s.sendMessage(getMessage("plugin_prefix", true) + getMessage("hide", true));
                    return true;
                }
                if (args[0].equalsIgnoreCase("unhide")) {
                    if (hasAccess(rgn,p,localPlayer,false)) {
                        if (!s.hasPermission("karenprotect.unhide")&& !s.hasPermission("karenprotect.admin") && !s.hasPermission("karenprotect.*") && !s.isOp()) {
                            s.sendMessage(getMessage("plugin_prefix", true) + getMessage("unhide_no_perm", true));
                            return true;
                        }
                    }else {
                        s.sendMessage(getMessage("plugin_prefix", true) + getMessage("unhide_deny", true));
                        return true;
                    }
                    Location loc;
                    File file1 = new File(colorystarry.conffetibox.getDataFolder().getAbsolutePath()+File.separator+"hide_protection"+".yml");
                    if (!file1.exists()) {
                        try {
                            FileWriter writer = new FileWriter(file1);
                            writer.write("created_time: " + System.currentTimeMillis());
                            writer.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                            colorystarry.conffetibox.getLogger().severe("Error on saving hide data...");
                        }
                    }
                    YamlReader hide_file = new YamlReader(file1.getAbsolutePath());
                    ConfigurationSection hide_list = hide_file.getConfigSelection("hide_list");
                    Material material;
                    if (hide_list == null || hide_list.getString(rgn.getId()+".id") == null || Material.valueOf(hide_list.getString(rgn.getId()+".id").toUpperCase()) == null) {
                        s.sendMessage(getMessage("plugin_prefix", true) + getMessage("unhide_already", true));
                        return true;
                    }
                    material = Material.valueOf(hide_list.getString(rgn.getId()+".id"));
                    loc = new Location(p.getWorld() , hide_file.getInt("hide_list." + rgn.getId() + ".x") , hide_file.getInt("hide_list." + rgn.getId() + ".y") , hide_file.getInt("hide_list." + rgn.getId() + ".z"));
                    loc.getBlock().setType(material);
                    hide_file.set("hide_list." + rgn.getId() , null);
                    s.sendMessage(getMessage("plugin_prefix", true) + getMessage("unhide", true));
                    return true;
                }
                if (args[0].equalsIgnoreCase("cleanup")) {
                    if (!s.hasPermission("karenprotect.cleanup") && !s.hasPermission("karenprotect.admin") && !s.hasPermission("karenprotect.*") && !s.isOp()) {
                        s.sendMessage(getMessage("plugin_prefix", true) + getMessage("no_perm", true));
                        return true;
                    }
                    if (args.length < 2) {
                        s.sendMessage(getMessage("plugin_prefix", true) + getMessage("cleanup_usage", true));
                        return true;
                    }
                    int auto_remove;
                    try {
                        auto_remove = Integer.parseInt(args[1]);
                    } catch (Exception exx) {
                        auto_remove = 0;
                    }
                    if (auto_remove < 1) {
                        s.sendMessage(getMessage("plugin_prefix", true) + getMessage("cleanup_usage", true));
                        return true;
                    }
                    int i = 0;
                    for (World world : Bukkit.getWorlds()) {
                        for (ProtectedRegion pt : wrapper.getRegionManager(world).getRegions().values()) {
                            long max_time = 0;
                            for (String str_player : pt.getOwners().getPlayers()) {
                                OfflinePlayer ofp = Bukkit.getOfflinePlayer(str_player);
                                if (ofp != null) {
                                    long last_login = ofp.getLastPlayed();
                                    if (last_login > max_time) max_time = last_login;
                                }
                            }
                            if (max_time == 0) continue;
                            if (System.currentTimeMillis() > (max_time + dayToMiliseconds(auto_remove))) {
                                String ida = pt.getId();
                                int indexX = id.indexOf("x");
                                int indexY = id.indexOf("y");
                                int indexZ = id.length() - 1;
                                double krx = Double.parseDouble(ida.substring(5, indexX));
                                double kry = Double.parseDouble(ida.substring(indexX + 1, indexY));
                                double krz = Double.parseDouble(ida.substring(indexY + 1, indexZ));
                                int usexa = (int) krx;
                                int useya = (int) kry;
                                int useza = (int) krz;
                                world.getBlockAt(new Location(world, usexa, useya, useza)).setType(Material.AIR);
                                conffetibox.getLogger().info("Removed protection id:" + pt.getId() + " (Cleanup)");
                                wrapper.getRegionManager(world).removeRegion(pt.getId());
                                i++;
                            }
                        }
                    }
                    s.sendMessage(getMessage("plugin_prefix", true) + getMessage("cleanup", true).replaceAll("<value>", String.valueOf(i)));
                    colorystarry.conffetibox.getLogger().info(getMessage("plugin_prefix", true) + getMessage("cleanup", true).replaceAll("<value>", String.valueOf(i)));
                    try {
                        rgm.save();
                    } catch (Exception e) {
                        System.out.println("KarenProtect: WorldGuard Error [" + e + "] during Region File Save");
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("give")) {
                    if (s.hasPermission("karenprotect.give") || s.isOp() || s.hasPermission("karenprotect.*")) {
                        if (args.length < 2) {
                            s.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("give_help", true));
                            return true;
                        }
                        Player target;
                        if (args.length > 2) {
                            target = colorystarry.getPlayer(args[2]);
                            if (target == null) {
                                s.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("target_not_online", true).replaceAll("<player>", args[2]));
                                return true;
                            }
                        } else {
                            target = (Player) s;
                        }
                        String target_id = "#NONE#";
                        for (KPBlock kp : colorystarry.block_list) {
                            if (kp.getBlock_id().equalsIgnoreCase(args[1])) {
                                target_id = kp.getBlock_id();
                                break;
                            } else if (kp.getBlock_name().replaceAll("§", "&").contains(args[1])) {
                                target_id = kp.getBlock_id();
                            } else if (kp.getBlock_id().contains(args[1])) {
                                target_id = kp.getBlock_id();
                            }
                        }
                        if (!target_id.equalsIgnoreCase("#NONE#")) {
                            colorystarry.give_block(target_id.toUpperCase(), target, target.getLocation());
                            s.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("block_give", true).replaceAll("<id>", target_id).replaceAll("<player>", target.getName()));
                            return true;
                        } else {
                            s.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("block_not_found", true).replaceAll("<id>", args[1]));
                            return true;
                        }
                    } else {
                        s.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("no_perm", true));
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("flag")) {
                    if (s.hasPermission("karenprotect.flags")) {
                        if (!hasAccess(rgn, p, localPlayer, false)) {
                            p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("flag_deny", true));
                            return true;
                        }
                        if (args.length >= 3) {
                            if (colorystarry.flags.contains(args[1].toLowerCase()) || s.hasPermission("karenprotect.flag." + args[1].toLowerCase()) || s.hasPermission("karenprotect.flag.*")) {
                                FlagHandler fh = new FlagHandler();
                                fh.setFlag(args, rgm.getRegion(id), p, true);
                            } else {
                                s.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("flag_flags_no_perm", true));
                            }
                        } else {
                            s.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("flag_help", true));
                        }
                    } else {
                        s.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("flag_no_perm", true));
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    if (p.hasPermission("karenprotect.reload") || p.hasPermission("karenprotect.admin") || p.hasPermission("karenprotect.*") || p.isOp()) {
                        colorystarry.reload(s);
                        return true;
                    } else {
                        p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("no_perm", true));
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("take")) {
                    if (rgn == null) {
                        s.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("protection_take_deny", true));
                        return true;
                    }
                    if (!p.hasPermission("karenprotect.takeany") && !p.hasPermission("karenprotect.take") && !p.hasPermission("karenprotect.admin") && !p.hasPermission("karenprotect.*") && !p.isOp()) {
                        p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("protection_take_no_perm", true));
                        return true;
                    }
                    if (!hasAccess(rgn, p, localPlayer, false) && !p.hasPermission("karenprotect.admin") && !p.hasPermission("karenprotect.*") && !p.isOp() && !p.hasPermission("karenprotect.takeany") && !p.hasPermission("karenprotect.take")) {
                        p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("protection_take_deny", true));
                        return true;
                    }
                    if (!hasAccess(rgn, p, localPlayer, false) && !p.hasPermission("karenprotect.takeany")) {
                        p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("protection_take_deny", true));
                        return true;
                    }
                    Material id_is = p.getWorld().getBlockAt(new Location(p.getWorld(), usex, usey, usez)).getType();
                    if (!colorystarry.KPID_List().contains(id_is.toString())) {
                        boolean bypass_check;
                        File file1 = new File(colorystarry.conffetibox.getDataFolder().getAbsolutePath()+File.separator+"hide_protection"+".yml");
                        Material mar = null;
                        if (file1.exists()) {
                            YamlReader hide_file = new YamlReader(file1.getAbsolutePath());
                            if (hide_file.getConfigSelection("hide_list." + rgn.getId()) != null){
                                bypass_check = true;
                            }else {
                                bypass_check = false;
                            }
                            String mat_str = hide_file.getString("hide_list." + rgn.getId() + ".id");
                            Material material = null;
                            if (mat_str != null) {
                                material = Material.valueOf(mat_str);
                            }
                            if (material == null) bypass_check = false;
                            mar = material;
                        } else {
                            bypass_check = false;
                        }
                        if (bypass_check == false) {
                            s.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("protection_take_not_found", true).replaceAll("<z>", String.valueOf(usez)).replaceAll("<x>", String.valueOf(usex)).replaceAll("<y>", String.valueOf(usey)));
                        }else {
                            KPBlock kpBlock = getBlocksKP(mar.toString());
                            if (kpBlock.is_nodrop()) {
                                s.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("protection_take_nodrop", true).replaceAll("<z>", String.valueOf(usez)).replaceAll("<x>", String.valueOf(usex)).replaceAll("<y>", String.valueOf(usey)));
                            } else {
                                give_block(mar.toString().toUpperCase(), p, p.getLocation());
                                s.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("protection_take", true).replaceAll("<z>", String.valueOf(usez)).replaceAll("<x>", String.valueOf(usex)).replaceAll("<y>", String.valueOf(usey)));
                            }
                        }
                        if (bypass_check) {
                            YamlReader hide_file = new YamlReader(file1.getAbsolutePath());
                            hide_file.set("hide_list." + rgn.getId() , null);
                        }
                        rgm.removeRegion(rgn.getId());
                        return true;
                    }
                    Material ma = p.getWorld().getBlockAt(new Location(p.getWorld(), usex, usey, usez)).getType();
                    if (getBlocksKP(rgn, p).is_nodrop()) {
                        s.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("protection_take_nodrop", true).replaceAll("<z>", String.valueOf(usez)).replaceAll("<x>", String.valueOf(usex)).replaceAll("<y>", String.valueOf(usey)));
                    } else {
                        give_block(ma.toString().toUpperCase(), p, p.getLocation());
                        s.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("protection_take", true).replaceAll("<z>", String.valueOf(usez)).replaceAll("<x>", String.valueOf(usex)).replaceAll("<y>", String.valueOf(usey)));
                    }
                    p.getWorld().getBlockAt(new Location(p.getWorld(), usex, usey, usez)).setType(Material.AIR);
                    rgm.removeRegion(rgn.getId());
                    return true;
                }
                if (args[0].equalsIgnoreCase("add")) {
                    if (p.hasPermission("karenprotect.members")) {
                        if (!hasAccess(rgn, p, localPlayer, false)) {
                            p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("member_deny", true));
                            return true;
                        }
                        if (args.length < 2) {
                            p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("require_player", true));
                            return true;
                        } else {
                            String playerName = args[1];
                            DefaultDomain members = rgm.getRegion(id).getMembers();
                            members.addPlayer(playerName);
                            rgm.getRegion(id).setMembers(members);
                            try {
                                rgm.save();
                            } catch (Exception e) {
                                System.out.println("KarenProtect: WorldGuard Error [" + e + "] during Region File Save");
                            }
                            p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("member_added", true).replaceAll("<player>", String.valueOf(playerName)));
                            return true;
                        }
                    } else {
                        p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("member_no_perm", true));
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("remove")) {
                    if (p.hasPermission("karenprotect.members")) {
                        if (!hasAccess(rgn, p, localPlayer, false)) {
                            p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("member_deny", true));
                            return true;
                        }
                        if (args.length < 2) {
                            p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("require_player", true));
                            return true;
                        }
                        String playerName = args[1];
                        DefaultDomain members = rgm.getRegion(id).getMembers();
                        members.removePlayer(playerName);
                        rgm.getRegion(id).setMembers(members);
                        try {
                            rgm.save();
                        } catch (Exception e) {
                            System.out.println("KarenProtect: WorldGuard Error [" + e + "] during Region File Save");
                        }
                        p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("member_removed", true).replaceAll("<player>", String.valueOf(playerName)));
                    } else {
                        p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("member_no_perm", true));
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("addowner")) {
                    if (p.hasPermission("karenprotect.owners")) {
                        if (!hasAccess(rgn, p, localPlayer, false)) {
                            p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("owner_deny", true));
                            return true;
                        }
                        if (args.length < 2) {
                            p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("require_player", true));
                            return true;
                        } else {
                            String playerName = args[1];
                            DefaultDomain owners = rgm.getRegion(id).getOwners();
                            owners.addPlayer(playerName);
                            rgm.getRegion(id).setOwners(owners);
                            try {
                                rgm.save();
                            } catch (Exception e) {
                                System.out.println("KarenProtect: WorldGuard Error [" + e + "] during Region File Save");
                            }
                            p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("owner_added", true).replaceAll("<player>", String.valueOf(playerName)));
                            return true;
                        }
                    } else {
                        p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("owner_no_perm", true));
                        return true;
                    }
                }
                if (args[0].equalsIgnoreCase("removeowner")) {
                    if (p.hasPermission("karenprotect.owners")) {
                        if (!hasAccess(rgn, p, localPlayer, false)) {
                            p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("owner_deny", true));
                            return true;
                        }
                        if (args.length < 2) {
                            p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("require_player", true));
                            return true;
                        }
                        String playerName = args[1];
                        DefaultDomain owners = rgm.getRegion(id).getOwners();
                        owners.removePlayer(playerName);
                        rgm.getRegion(id).setOwners(owners);
                        try {
                            rgm.save();
                        } catch (Exception e) {
                            System.out.println("KarenProtect: WorldGuard Error [" + e + "] during Region File Save");
                        }
                        p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("owner_removed", true).replaceAll("<player>", String.valueOf(playerName)));
                    } else {
                        p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("owner_no_perm", true));
                    }
                    return true;
                }
                if (args[0].equalsIgnoreCase("debug")) {
                    if (args.length > 1 && args[1].equalsIgnoreCase("is_protection_block")) {
                        if (colorystarry.getItemInHand(p) == null) {
                            s.sendMessage("§7Karen: §aPlease hold an item!");
                            return true;
                        }
                        ItemStack item = colorystarry.getItemInHand(p);
                        for (KPBlock kpBlock : block_list) {
                            String itemName;
                            if (item.hasItemMeta()) {
                                s.sendMessage(item.getItemMeta().getDisplayName());
                                itemName = item.getItemMeta().getDisplayName();
                            } else {
                                itemName = "(nothing)";
                            }
                            String display_name_base = kpBlock.getBlock_name();
                            boolean b_5 = item.getItemMeta().getDisplayName().contains(display_name_base);
                            if (b_5 == false) {
                                b_5 = display_name_base.contains(item.getItemMeta().getDisplayName());
                            }
                            if (kpBlock.getBlock_id().equalsIgnoreCase(item.getType().toString()) && b_5) {
                                s.sendMessage("§7Karen: §aYep! this is protection block");
                                s.sendMessage("§7Karen: §aName: " + kpBlock.getBlock_name());
                                s.sendMessage("§7Karen: §aSize: X:" + kpBlock.getSize_x() + " Y:" + kpBlock.getSize_y() + " Z:" + kpBlock.getSize_z());
                                s.sendMessage("§7Karen: §aBlockID: " + kpBlock.getBlock_id());
                                s.sendMessage("§7Karen: §aKitName: " + kpBlock.getKit_name());
                                s.sendMessage("§7Karen: §aLore:");
                                for (String str : kpBlock.getBlock_lore()) {
                                    s.sendMessage(str);
                                }
                                return true;
                            } else {
                                s.sendMessage("§7Karen: §cNope! this not protection block!");
                                if (kpBlock.getBlock_id().equalsIgnoreCase(item.getType().toString())) {
                                    s.sendMessage("§7Karen: §ebut we found same ID...");
                                    s.sendMessage("§7Karen: §crequired:§f " + kpBlock.getBlock_name().replaceAll("[§]", "&"));
                                    s.sendMessage("§7Karen: §bfound:§f " + itemName.replaceAll("[§]", "&"));
                                }
                                return true;
                            }
                        }
                    } else if (args.length > 1 && args[1].equalsIgnoreCase("ver")) {
                        Plugin wgr = Bukkit.getPluginManager().getPlugin("WorldGuard");
                        if (wgr != null) {
                            s.sendMessage("§7Karen: §fWG: §a" + wgd.getDescription().getVersion());
                        } else {
                            s.sendMessage("§7Karen: §fWG: §cnull");
                        }
                        Plugin we = Bukkit.getPluginManager().getPlugin("WorldEdit");
                        if (we != null) {
                            s.sendMessage("§7Karen: §fWE: §a" + we.getDescription().getVersion());
                        } else {
                            s.sendMessage("§7Karen: §fWE: §cnull");
                        }
                        s.sendMessage("§7Karen: §fKP: " + conffetibox.getDescription().getVersion());
                        s.sendMessage("§7Karem: §fConffetibox: §b" + conffetibox.getConfig().getString("karen"));
                        return true;
                    }
                    s.sendMessage("§7Usage: §c/kp debug <args>");
                    return true;
                }
                if (args[0].equalsIgnoreCase("priority")) {
                    if (p.hasPermission("karenprotect.priority")) {
                        if (!hasAccess(rgn, p, localPlayer, false)) {
                            p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("priority_deny", true));
                            return true;
                        }
                        if (args.length < 2) {
                            int priority = rgm.getRegion(id).getPriority();
                            p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("priority_view", true).replaceAll("<value>", String.valueOf(priority)));
                            return true;
                        }
                        try {
                            int priority = Integer.parseInt(args[1]);
                            rgm.getRegion(id).setPriority(priority);
                        } catch (NumberFormatException ex_num) {
                            int priority = rgm.getRegion(id).getPriority();
                            p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("priority_view", true).replaceAll("<value>", String.valueOf(priority)));
                            return true;
                        }
                        try {
                            rgm.save();
                        } catch (Exception e) {
                            System.out.println("KarenProtect: WorldGuard Error [" + e + "] during Region File Save");
                        }
                        p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("priority_set", true).replaceAll("<value>", args[1]));
                    } else {
                        p.sendMessage(colorystarry.getMessage("plugin_prefix", true) + colorystarry.getMessage("priority_no_perm", true));
                    }
                    return true;
                }
                send_help(s, rgn);
                return true;
            } else {
                send_help(s, rgn);
                return true;
            }
        }
        return true;
    }
}

