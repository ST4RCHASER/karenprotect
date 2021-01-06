package me.starchaser.karenprotect;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static me.starchaser.karenprotect.colorystarry.getID_List;
import static me.starchaser.karenprotect.colorystarry.wrapper;

public class KPBlock {
    private final String block_id;
    private final int size_x, size_y, size_z;
    private final String block_name, kit_name;
    private final ArrayList<String> block_lore;
    private final ArrayList<String> player_leave_evt;
    private final ArrayList<String> player_entry_evt;
    private final ArrayList<String> player_place_evt;
    private final ArrayList<String> player_distory_evt;
    private final boolean req_permission;
    private boolean nodrop = false;

    KPBlock(String block_id, int size_x, int size_y, int size_z, String block_name, ArrayList<String> block_lore, String kit_name, ArrayList<String> player_place_evt, ArrayList<String> player_distory_evt, ArrayList<String> player_entry_evt, ArrayList<String> player_leave_evt , boolean permssion) {
        this.block_id = block_id;
        this.size_x = size_x;
        this.size_y = size_y;
        this.size_z = size_z;
        this.block_name = block_name;
        this.block_lore = block_lore;
        this.kit_name = kit_name;
        this.player_place_evt = player_place_evt;
        this.player_distory_evt = player_distory_evt;
        this.player_entry_evt = player_entry_evt;
        this.player_leave_evt = player_leave_evt;
        this.req_permission = permssion;
    }

    String getKit_name() {
        return kit_name.replaceAll("&", "§").replaceAll("<x>", String.valueOf(size_x)).replaceAll("<y>", String.valueOf(size_y)).replaceAll("<z>", String.valueOf(size_z));
    }
    boolean RequiredPermission(){
        return req_permission;
    }
    ArrayList<String> getBlock_lore() {
        ArrayList<String> list = new ArrayList<>();
        for (String a : block_lore) {
            list.add(a.replaceAll("&", "§").replaceAll("<x>", String.valueOf(size_x)).replaceAll("<y>", String.valueOf(size_y)).replaceAll("<z>", String.valueOf(size_z)).replaceAll("<name>", getKit_name()));
        }
        return list;
    }

    String getBlock_name() {
        return block_name.replaceAll("&", "§").replaceAll("<x>", String.valueOf(size_x)).replaceAll("<y>", String.valueOf(size_y)).replaceAll("<z>", String.valueOf(size_z)).replaceAll("<name>", getKit_name());
    }

    String getBlock_id() {
        return block_id;
    }

    public Material getMaterial() {
        return Material.getMaterial(block_id);
    }

    int getSize_x() {
        return size_x;
    }

    int getSize_y() {
        return size_y;
    }

    int getSize_z() {
        return size_z;
    }

    @SuppressWarnings("Duplicates")
    int TaskPlaceAndBreak(Player p, String str , ProtectedRegion rgn) {
        StringBuilder owners = new StringBuilder("§r");
        String owner = "§r";
        if (rgn.getOwners() != null) {
            for (String pl : wrapper.getPlayers(rgn.getOwners())) {
                if (owner == "§r") owner = pl;
                owners.append(", ").append(pl);
            }
        }
        StringBuilder members = new StringBuilder("§r");
        String member = "§r";
        if (rgn.getMembers() != null) {
            for (String pl : wrapper.getPlayers(rgn.getMembers())) {
                if (member == "§r") member = pl;
                members.append(", ").append(pl);
            }
        }
        if (str.startsWith("console; ")) {
            String str2 = str.replaceFirst("console; ", "");
            String str3 = str2.replaceAll("<player>" , p.getName()).replaceAll("&", "§").replaceAll("<x>", String.valueOf(size_x)).replaceAll("<y>", String.valueOf(size_y)).replaceAll("<z>", String.valueOf(size_z)).replaceAll("<name>", getKit_name()).replaceAll("<owner>" , owner).replaceAll("<owners>" , owners.toString()).replaceAll("<member>" , member).replaceAll("<members>" , members.toString());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), str3);
        }
        if (str.startsWith("player; ")) {
            String str2 = str.replaceFirst("player; ", "");
            String str3 = str2.replaceAll("<player>" , p.getName()).replaceAll("&", "§").replaceAll("<x>", String.valueOf(size_x)).replaceAll("<y>", String.valueOf(size_y)).replaceAll("<z>", String.valueOf(size_z)).replaceAll("<name>", getKit_name()).replaceAll("<owner>" , owner).replaceAll("<owners>" , owners.toString()).replaceAll("<member>" , member).replaceAll("<members>" , members.toString());
            Bukkit.dispatchCommand(p, str3);
        }
        if (str.startsWith("text; ")) {
            String str2 = str.replaceFirst("text; ", "");
            String str3 = str2.replaceAll("<player>" , p.getName()).replaceAll("&", "§").replaceAll("<x>", String.valueOf(size_x)).replaceAll("<y>", String.valueOf(size_y)).replaceAll("<z>", String.valueOf(size_z)).replaceAll("<name>", getKit_name()).replaceAll("<owner>" , owner).replaceAll("<owners>" , owners.toString()).replaceAll("<member>" , member).replaceAll("<members>" , members.toString());
            p.sendMessage(str3);
        }
        if (str.startsWith("nodrop; ")) {
            String str2 = str.replaceFirst("nodrop; ", "");
            try {
                nodrop = Boolean.parseBoolean(str2);
            } catch (Exception ea) {
                nodrop = false;
            }
        }
        int bool = 0;
        if (str.equalsIgnoreCase("stop; " ) || str.equalsIgnoreCase("stop;")) {
            bool = 2;
        }
        if (str.startsWith("break; on_same_owner")) {
            bool = 2;
        }
        if (str.startsWith("break; on_same_protection")) {
            bool = 2;
        }
        if (str.startsWith("break; on_already_in_other_region")) {
            int count = getID_List(p.getLocation().getX() , p.getLocation().getY() , p.getLocation().getZ() , wrapper.getRegionManager(p.getLocation().getWorld())).size();
            if (count > 0) {
                bool = 2;
            }
        }
        return bool;
    }
    @SuppressWarnings("Duplicates")
    int TaskEntryAndLeave(Player p, String str, ProtectedRegion rgn, int type) {
        int bool = 0;
        Boolean is_frist = true;
        StringBuilder owners = new StringBuilder("§r");
        String owner = "§r";
        if (rgn.getOwners() != null) {
            for (String pl : wrapper.getPlayers(rgn.getOwners())) {
                if (owner == "§r") owner = pl;
                    if (is_frist) {
                        owners.append(pl);
                        is_frist = false;
                    }else {
                        owners.append(", ").append(pl);
                    }
            }
        }
        StringBuilder members = new StringBuilder("§r");
        String member = "§r";
        is_frist = true;
        if (rgn.getMembers() != null) {
            for (String pl : wrapper.getPlayers(rgn.getMembers())) {
                if (member == "§r") member = pl;
                if (is_frist) {
                    members.append(pl);
                }else {
                    members.append(", ").append(pl);
                }
            }
        }
        if (str.startsWith("cancel; ")) {
            String str2 = str.replaceFirst("cancel; ", "");
            if (str2.equalsIgnoreCase("true")) {
                bool = 1;
            }
        }
        if (str.equalsIgnoreCase("stop; " ) || str.equalsIgnoreCase("stop;")) {
            bool = 2;
        }
        if (str.startsWith("break; on_same_owner")) {
            bool = 2;
        }
        if (str.startsWith("break; on_same_protection")) {
            bool = 2;
        }
        if (str.startsWith("break; on_already_in_other_region")) {
            int count = getID_List(p.getLocation().getX() , p.getLocation().getY() , p.getLocation().getZ() , wrapper.getRegionManager(p.getLocation().getWorld())).size();
            if (type == 1) {
                if (count > 1) {
                    bool = 2;
                }
            }else if (type == 0) {
                if (count > 0) {
                    bool = 2;
                }
            }
        }
        if (str.startsWith("console; ")) {
            String str2 = str.replaceFirst("console; ", "");
            String str3 = str2.replaceAll("<player>" , p.getName()).replaceAll("&", "§").replaceAll("<x>", String.valueOf(size_x)).replaceAll("<y>", String.valueOf(size_y)).replaceAll("<z>", String.valueOf(size_z)).replaceAll("<name>", getKit_name()).replaceAll("<owner>" , owner).replaceAll("<owners>" , owners.toString()).replaceAll("<member>" , member).replaceAll("<members>" , members.toString());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), str3);
        }
        if (str.startsWith("player; ")) {
            String str2 = str.replaceFirst("player; ", "");
            String str3 = str2.replaceAll("<player>" , p.getName()).replaceAll("&", "§").replaceAll("<x>", String.valueOf(size_x)).replaceAll("<y>", String.valueOf(size_y)).replaceAll("<z>", String.valueOf(size_z)).replaceAll("<name>", getKit_name()).replaceAll("<owner>" , owner).replaceAll("<owners>" , owners.toString()).replaceAll("<member>" , member).replaceAll("<members>" , members.toString());
            Bukkit.dispatchCommand(p, str3);
        }
        if (str.startsWith("text; ")) {
            String str2 = str.replaceFirst("text; ", "");
            String str3 = str2.replaceAll("<player>" , p.getName()).replaceAll("&", "§").replaceAll("<x>", String.valueOf(size_x)).replaceAll("<y>", String.valueOf(size_y)).replaceAll("<z>", String.valueOf(size_z)).replaceAll("<name>", getKit_name()).replaceAll("<owner>" , owner).replaceAll("<owners>" , owners.toString()).replaceAll("<member>" , member).replaceAll("<members>" , members.toString());
            p.sendMessage(str3);
        }
        return bool;
    }

    void Run_PlayerPlace_Event(Player p , ProtectedRegion rgn) {
        for (String str : player_place_evt) {
            if (TaskPlaceAndBreak(p, str , rgn) == 2) break;
        }
    }

    void Run_PlayerBreak_Event(Player p , ProtectedRegion rgn) {
        for (String str : player_distory_evt) {
            if (TaskPlaceAndBreak(p, str , rgn) == 2) break;
        }
    }

    @SuppressWarnings("Duplicates")
    boolean Run_PlayerLeave_Event(Player p , ProtectedRegion rgn) {
        Boolean bool = false;
        for (String str : player_leave_evt) {
            int res = TaskEntryAndLeave(p,str , rgn , 1);
            if(res == 1) {
                bool = true;
            }else if (res == 2) {
                return  bool;
            }
        }
        return bool;
    }
    @SuppressWarnings("Duplicates")
    boolean is_nodrop () {
        boolean bool_1 = false;
        for (String str : player_place_evt) {
            if (str.startsWith("nodrop; ")) {
                try {
                    String str2 = str.replaceFirst("nodrop; ","");
                    bool_1 = Boolean.parseBoolean(str2);
                } catch (Exception ea) {
                    bool_1 = false;
                }
            }
        }
        boolean bool_2 = false;
        for (String str : player_distory_evt) {
            if (str.startsWith("nodrop; ")) {
                try {
                    String str2 = str.replaceFirst("nodrop; ","");
                    bool_2 = Boolean.parseBoolean(str2);
                } catch (Exception ea) {
                    bool_2 = false;
                }
            }
        }
        if (bool_1 || bool_2) {
            nodrop = true;
        }else {
            nodrop = false;
        }
        return nodrop;
    }
    @SuppressWarnings("Duplicates")
    boolean Run_PlayerEntry_Event (Player p , ProtectedRegion rgn){
        Boolean bool = false;
        for (String str : player_entry_evt) {
            int res = TaskEntryAndLeave(p,str , rgn , 0);
            if(res == 1) {
                bool = true;
            }else if (res == 2) {
                return bool;
            }
        }
        return bool;
    }
}

class SilkTouchItem {
    private ItemStack Item;
    private String TargetItemID;

    SilkTouchItem(ItemStack itemStack, String targetItemID) {
        this.Item = itemStack;
        this.TargetItemID = targetItemID;
    }

    String getTargetItemID() {
        return TargetItemID.toUpperCase();
    }

    ItemStack getItem() {
        return Item;
    }
}

