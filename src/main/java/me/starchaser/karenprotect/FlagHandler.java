package me.starchaser.karenprotect;

import com.google.common.base.Joiner;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.*;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static me.starchaser.karenprotect.colorystarry.conffetibox;
import static me.starchaser.karenprotect.colorystarry.wrapper;

/*****************************************************************************************************/
/*                THIS CLASS FORM PROTECTIONSTONES THANKS TO Vik1395 :D                              */
/*****************************************************************************************************/

public class FlagHandler {

    public void setFlag(String[] args, ProtectedRegion region, Player p , boolean send_message_to_player) {
        if (!conffetibox.getConfig().getStringList("enable_flags").contains(args[1])) {
            p.sendMessage(colorystarry.getMessage("plugin_prefix" , true) + colorystarry.getMessage("flag_not_found" , true).replaceAll("<flag>", args[1]));
            return;
        }
        Flag<?> rawFlag = wrapper.getFlag(args[1]);
        /*try {
            rawFlag = DefaultFlag.fuzzyMatchFlag(args[1]); // < WG lower 6.1 6.0
        }catch (NoSuchMethodError e) {
            try {
                rawFlag = DefaultFlag.fuzzyMatchFlag(wg.getFlagRegistry(), args[1]); //< WG 6.2 - WG 6.9
            }catch (Exception e2) {
                rawFlag = Flags.fuzzyMatchFlag(WorldGuard.getInstance().getFlagRegistry(), args[1]);
            }
        }*/
        if (rawFlag == null) {
            p.sendMessage(colorystarry.getMessage("plugin_prefix" , true) + colorystarry.getMessage("flag_not_found" , true).replaceAll("<flag>", args[1]));
            return;
        }
        if (rawFlag instanceof StateFlag) {
            StateFlag flag = (StateFlag) rawFlag;
            if (args[2].equalsIgnoreCase("default")) {
                region.setFlag(flag, flag.getDefault());
                region.setFlag(flag.getRegionGroupFlag(), null);
                if (send_message_to_player) {
                    p.sendMessage(colorystarry.getMessage("plugin_prefix" , true) + colorystarry.getMessage("flag_set" , true).replaceAll("<value>", args[2]).replaceAll("<flag>", args[1]));
                }
            } else {
                RegionGroup group = null;
                if (Arrays.toString(args).contains("-g")) {
                    int i = 0;
                    for (String s : args) {
                        i++;
                        if (s.equalsIgnoreCase("-g")) {
                            group = getRegionGroup(args[i]);
                        }
                    }
                }
                if (Arrays.toString(args).contains("allow")) {
                    region.setFlag(flag, StateFlag.State.ALLOW);
                    if (group != null) {
                        region.setFlag(flag.getRegionGroupFlag(), group);
                    }
                    if (send_message_to_player) {
                        p.sendMessage(colorystarry.getMessage("plugin_prefix" , true) + colorystarry.getMessage("flag_set" , true).replaceAll("<value>", args[2]).replaceAll("<flag>", args[1]));
                    }
                } else if (Arrays.toString(args).contains("deny")) {
                    region.setFlag(flag, StateFlag.State.DENY);
                    if (group != null) {
                        region.setFlag(flag.getRegionGroupFlag(), group);
                    }
                    if (send_message_to_player) {
                        p.sendMessage(colorystarry.getMessage("plugin_prefix" , true) + colorystarry.getMessage("flag_set", true).replaceAll("<value>", args[2]).replaceAll("<flag>", args[1]));
                    }
                } else {
                    if (group != null) {
                        region.setFlag(flag.getRegionGroupFlag(), group);
                        if (send_message_to_player) {
                            p.sendMessage(colorystarry.getMessage("plugin_prefix" , true) + colorystarry.getMessage("flag_set", true).replaceAll("<value>", args[2]).replaceAll("<flag>", args[1]));
                        }
                    } else {
                        if (send_message_to_player) {
                            p.sendMessage(colorystarry.getMessage("plugin_prefix" , true) + colorystarry.getMessage("flag_error", true).replaceAll("<value>", args[2]).replaceAll("<flag>", args[1]));
                        }
                    }
                }
            }
        } else if (rawFlag instanceof DoubleFlag) {
            DoubleFlag flag = (DoubleFlag) rawFlag;
            if (args[2].equalsIgnoreCase("default")) {
                region.setFlag(flag, flag.getDefault());
                region.setFlag(flag.getRegionGroupFlag(), null);
            } else {
                region.setFlag(flag, Double.parseDouble(args[1]));
            }
            if (send_message_to_player) {
                p.sendMessage(colorystarry.getMessage("plugin_prefix" , true) + colorystarry.getMessage("flag_set", true).replaceAll("<value>", args[2]).replaceAll("<flag>", args[1]));
            }
        } else if (rawFlag instanceof IntegerFlag) {
            IntegerFlag flag = (IntegerFlag) rawFlag;
            if (args[2].equalsIgnoreCase("default")) {
                region.setFlag(flag, flag.getDefault());
                region.setFlag(flag.getRegionGroupFlag(), null);
            } else {
                region.setFlag(flag, Integer.parseInt(args[1]));
            }
            if (send_message_to_player) {
                p.sendMessage(colorystarry.getMessage("plugin_prefix" , true) + colorystarry.getMessage("flag_set", true).replaceAll("<value>", args[2]).replaceAll("<flag>", args[1]));
            }
        } else if (rawFlag instanceof StringFlag) {
            StringFlag flag = (StringFlag) rawFlag;
            if (args[2].equalsIgnoreCase("default")) {
                region.setFlag(flag, flag.getDefault());
                region.setFlag(flag.getRegionGroupFlag(), null);
            } else {
                String flagValue = Joiner.on(" ").join(args).substring(args[0].length() + args[1].length() + 2);
                String msg = flagValue.replaceAll("<player>", p.getName());
                region.setFlag(flag, msg);
            }
            if (send_message_to_player) {
                p.sendMessage(colorystarry.getMessage("plugin_prefix" , true) + colorystarry.getMessage("flag_set", true).replaceAll("<value>", args[2]).replaceAll("<flag>", args[1]));
            }
        } else if (rawFlag instanceof BooleanFlag) {
            BooleanFlag flag = (BooleanFlag) rawFlag;
            if (args[2].equalsIgnoreCase("default")) {
                region.setFlag(flag, flag.getDefault());
                region.setFlag(flag.getRegionGroupFlag(), null);
                if (send_message_to_player) {
                    p.sendMessage(colorystarry.getMessage("plugin_prefix" , true) + colorystarry.getMessage("flag_set" , true).replaceAll("<value>", args[2]).replaceAll("<flag>", args[1]));
                }
            } else {
                if (args[2].equalsIgnoreCase("true")) {
                    region.setFlag(flag, true);
                    if (send_message_to_player) {
                        p.sendMessage(colorystarry.getMessage("plugin_prefix" , true) + colorystarry.getMessage("flag_set" , true).replaceAll("<value>", args[2]).replaceAll("<flag>", args[1]));
                    }
                } else if (args[2].equalsIgnoreCase("false")) {
                    region.setFlag(flag, false);
                    if (send_message_to_player) {
                        p.sendMessage(colorystarry.getMessage("plugin_prefix" , true) + colorystarry.getMessage("flag_set" , true).replaceAll("<value>", args[2]).replaceAll("<flag>", args[1]));
                    }
                }
            }
        }
    }

    private RegionGroup getRegionGroup(String arg) {
        if (arg.equalsIgnoreCase("member") || arg.equalsIgnoreCase("members")) {
            return RegionGroup.MEMBERS;
        } else if (arg.equalsIgnoreCase("nonmembers") || arg.equalsIgnoreCase("nonmember")
                || arg.equalsIgnoreCase("nomember") || arg.equalsIgnoreCase("nomembers")
                || arg.equalsIgnoreCase("non_members") || arg.equalsIgnoreCase("non_member")
                || arg.equalsIgnoreCase("no_member") || arg.equalsIgnoreCase("no_members")) {
            return RegionGroup.NON_MEMBERS;
        } else if (arg.equalsIgnoreCase("nonowners") || arg.equalsIgnoreCase("nonowner")
                || arg.equalsIgnoreCase("noowner") || arg.equalsIgnoreCase("noowners")
                || arg.equalsIgnoreCase("non_owners") || arg.equalsIgnoreCase("non_owner")
                || arg.equalsIgnoreCase("no_owner") || arg.equalsIgnoreCase("no_owners")) {
            return RegionGroup.NON_OWNERS;
        } else if (arg.equalsIgnoreCase("owner") || arg.equalsIgnoreCase("owners")) {
            return RegionGroup.OWNERS;
        } else if (arg.equalsIgnoreCase("none") || arg.equalsIgnoreCase("noone")) {
            return RegionGroup.NONE;
        } else if (arg.equalsIgnoreCase("all") || arg.equalsIgnoreCase("everyone")) {
            return RegionGroup.ALL;
        } else if (arg.endsWith("empty")) {
            return null;
        }

        return null;
    }
}
