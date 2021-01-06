package me.starchaser.karenprotect;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.starchaser.karenprotect.colorystarry.conffetibox;
import static me.starchaser.karenprotect.colorystarry.getID_List;
import static me.starchaser.karenprotect.colorystarry.wrapper;

public class t_complete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
        ArrayList<String> list = new ArrayList();
        try {
            if (cmd.getName().equalsIgnoreCase("karenprotect") || cmd.getName().equalsIgnoreCase("ps") || cmd.getName().equalsIgnoreCase("karenprotect") || cmd.getName().equalsIgnoreCase("kp")) {
                if ((s instanceof ConsoleCommandSender) || (s instanceof RemoteConsoleCommandSender)) {
                    if (args.length > 0) {
                        if (args[0].equalsIgnoreCase("")) {
                            list.add("give");
                            list.add("reload");
                        }
                        if (args.length == 2) {
                            if (args[0].equalsIgnoreCase("give")) {
                                for (KPBlock kpBlock : colorystarry.block_list) {
                                    list.add(kpBlock.getBlock_id());
                                }
                            }
                        }
                        if (args.length == 3) {
                            if (args[0].equalsIgnoreCase("give")) {
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    list.add(p.getName());
                                }
                            }
                        }
                    }
                } else {
                    if (args.length > 0 && args.length < 2) {
                        if (s.isOp() || s.hasPermission("karenprotect.*") || s.hasPermission("karenprotect.admin")) {
                            String cmd_args = "give,flag,reload,take,add,remove,addowner,removeowner,priority,stats,hide,unhide";
                            list.addAll(Arrays.asList(cmd_args.split(",")));
                        } else {
                            if (s.hasPermission("karenprotect.give")) {
                                list.add("give");
                            }
                            if (s.hasPermission("karenprotect.flags")) {
                                list.add("flag");
                            }
                            if (s.hasPermission("karenprotect.reload")) {
                                list.add("reload");
                            }
                            if (s.hasPermission("karenprotect.takeany") || s.hasPermission("karenprotect.take")) {
                                list.add("take");
                            }
                            if (s.hasPermission("karenprotect.members")) {
                                list.add("add");
                                list.add("remove");
                            }
                            if (s.hasPermission("karenprotect.owners")) {
                                list.add("addowner");
                                list.add("removeowner");
                            }
                            if (s.hasPermission("karenprotect.priority")) {
                                list.add("priority");
                            }
                            if (s.hasPermission("karenprotect.stats")) {
                                list.add("stats");
                            }
                            if (s.hasPermission("karenprotect.hide")) {
                                list.add("hide");
                            }
                            if (s.hasPermission("karenprotect.unhide")) {
                                list.add("unhide");
                            }
                        }
                    } else if (args.length > 1 && args.length < 3) {
                        boolean is_op = s.isOp();
                        boolean is_admin = false;
                        if (s.hasPermission("karenprotect.admin") || s.hasPermission("karenprotect.*")) is_admin = true;
                        if (args[0].equalsIgnoreCase("give")) {
                            if (s.hasPermission("karenprotect.give") || is_op || is_admin) {
                                for (KPBlock kpBlock : colorystarry.block_list) {
                                    list.add(kpBlock.getBlock_id());
                                }
                            }
                        }
                        if (args[0].equalsIgnoreCase("flag")) {
                            if (s.hasPermission("karenprotect.flags") || is_admin || is_op) {
                                for (String str : conffetibox.getConfig().getStringList("enable_flags")) {
                                    list.add(str);
                                }
                            }
                        }
                        if (args[0].equalsIgnoreCase("addowner")) {
                            if (s.hasPermission("karenprotect.owners") || is_admin || is_op) {
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    list.add(p.getName());
                                }
                            }
                        }
                        if (args[0].equalsIgnoreCase("add")) {
                            if (s.hasPermission("karenprotect.members") || is_admin || is_op) {
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    list.add(p.getName());
                                }
                            }
                        }
                        if (args[0].equalsIgnoreCase("remove")) {
                            if (s.hasPermission("karenprotect.members") || is_admin || is_op) {
                                Player p = (Player) s;
                                RegionManager rgm = wrapper.getRegionManager(p.getWorld());
                                String id = colorystarry.getRGN(rgm, null, getID_List(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(),rgm), p);
                                if (id.length() > 5 && id.substring(0, 5).equals("karen")) {
                                    ProtectedRegion rgn = rgm.getRegion(id);
                                    if (rgn.getOwners().getPlayers().contains(p.getName().toLowerCase())) {
                                        for (String p_str : rgn.getMembers().getPlayers()) {
                                            list.add(p_str);
                                        }
                                    }
                                }
                            }
                        }
                        if (args[0].equalsIgnoreCase("removeowner")) {
                            if (s.hasPermission("karenprotect.members") || is_admin || is_op) {
                                Player p = (Player) s;
                                RegionManager rgm = wrapper.getRegionManager(p.getWorld());
                                String id = colorystarry.getRGN(rgm, null, getID_List(p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ(),rgm), p);
                                if (id.length() > 5 && id.substring(0, 5).equals("karen")) {
                                    ProtectedRegion rgn = rgm.getRegion(id);
                                    if (rgn.getOwners().getPlayers().contains(p.getName().toLowerCase())) {
                                        for (String p_str : rgn.getOwners().getPlayers()) {
                                            list.add(p_str);
                                        }
                                    }
                                }
                            }
                        }// TODO: LAST TAB COMPLETE
                    } else if (args.length > 2 && args.length < 4) {
                        boolean is_op = s.isOp();
                        boolean is_admin = false;
                        if (args[0].equalsIgnoreCase("give")) {
                            if (s.hasPermission("karenprotect.give") || is_admin || is_op) {
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    list.add(p.getName());
                                }
                            }
                        }
                        if (args[0].equalsIgnoreCase("flag")) {
                            if (s.hasPermission("karenprotect.flags") || is_admin || is_op) {
                                if (!args[1].equalsIgnoreCase("greeting") && !args[1].equalsIgnoreCase("farewell")) {
                                    list.add("allow");
                                    list.add("deny");
                                }
                            }
                        }
                    }
                }
            }
            return list;
        }catch (Exception ex){
            return list;
        }
    }
}
