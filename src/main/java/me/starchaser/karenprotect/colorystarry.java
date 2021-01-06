package me.starchaser.karenprotect;

import com.sk89q.worldedit.Vector; //WE 7 Beta 1
import com.sk89q.worldedit.math.BlockVector3; //WE 7 Beta 2
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.starchaser.karenprotect.WGRegionEvents.WGRegionEventsListener;
import me.starchaser.karenprotect.adaptor.WorldGuardWrapper;
import me.starchaser.karenprotect.adaptor.wg6;
import me.starchaser.karenprotect.adaptor.wg6b;
import me.starchaser.karenprotect.adaptor.wg7;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class colorystarry extends JavaPlugin {
    static List<String> help_page,stats_page;
    static Plugin conffetibox;
    public static List<String> flags;
    public static Plugin wgd;
    static ArrayList<KPBlock> block_list;
    static ArrayList<SilkTouchItem> silkTouchItems;
    public static WorldGuardWrapper wrapper;
    private static Metrics metrics;
    static boolean debug = false;
    private static BukkitRunnable task_checker;
    private WGRegionEventsListener listener;
    private static int auto_remove;
    public colorystarry(){
     if (debug) {
         Bukkit.getLogger().info("[Debug] Class created!");
     }
    }
    @Override
    public void onEnable() {
        try {
            metrics = new Metrics(this);
            metrics.addCustomChart(new Metrics.SimplePie("kp_players", () -> String.valueOf(Bukkit.getOnlinePlayers().size())));
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error on sending info to metrics");
        }
        conffetibox = this;
        conf.in();
        if (getServer().getPluginManager().getPlugin("WorldGuard") != null && getServer().getPluginManager().getPlugin("WorldEdit") != null && getServer().getPluginManager().getPlugin("WorldGuard").isEnabled() && getServer().getPluginManager().getPlugin("WorldEdit").isEnabled()) {
            wgd = getServer().getPluginManager().getPlugin("WorldGuard");
            int ver_num = Integer.valueOf(wgd.getDescription().getVersion().split("[.]")[0]);
            int ver_num_sub = Integer.valueOf(wgd.getDescription().getVersion().split("[.]")[1]);
            if (debug) Bukkit.broadcastMessage("§7KP: §aTEST §b" + wgd.getDescription().getVersion() + " SUB X1 : §c" + ver_num + " §bSUB X2 : §c" + ver_num_sub);
            int force_wg_id = conffetibox.getConfig().getInt("manual_wg_change");
            if (force_wg_id != 0 && force_wg_id < 4 && force_wg_id > 0) {
                if (force_wg_id == 3) ver_num = 7;
                if (force_wg_id == 2) {
                    ver_num = 6;
                    ver_num_sub = 2;
                }
                if (force_wg_id == 1) {
                    ver_num = 6;
                    ver_num_sub = 1;
                }
            }
            switch (ver_num) {
                case 7: //Not directly use "Region Manager", Player as object :(
                    //make wg7 wrapper
                    wrapper = new wg7(force_wg_id != 0 && force_wg_id < 4 && force_wg_id > 0);
                    String we_ver_str = getServer()
                            .getPluginManager()
                            .getPlugin("WorldEdit")
                            .getDescription()
                            .getVersion()
                            .split("[.]")[0];
                    int we_ver = -1;
                    boolean fawe;
                    try {
                        Class.forName( "com.boydti.fawe.FaweAPI" );
                        fawe = true;
                    } catch( ClassNotFoundException e ) {
                        fawe = false;
                    }
                    if (fawe == false) {
                        try {
                            we_ver = Integer.valueOf(we_ver_str);
                        }catch (NumberFormatException ex) {
                            getLogger().severe("Failed to verify WE version... (" + we_ver_str + ")");
                        }
                        if (getServer().getPluginManager().getPlugin("WorldEdit").isEnabled() && we_ver < 7) {
                            getLogger().info("WE_SUB: " + we_ver);
                            getLogger().severe("WorldEdit is not equals Worldguard version");
                            getLogger().severe("Please update you WorldEdit version upper to 7.0>");
                            getLogger().severe("Disabling KarenProtect...");
                            getServer().getPluginManager().disablePlugin(this);
                            return;
                        }
                    }else {
                        getLogger().warning("FAWE Detached: Warning KarenProtect is not fully support FAWE maybe is have a bugs or errors I'm not responsible D:");
                    }
                    break;
                case 6: //Why they change flag a lot :(
                    if (ver_num_sub >= 2) { //use getfuzzyflag 2 arg...
                        //make wg6.2+ wrapper
                        wrapper = new wg6b(force_wg_id != 0 && force_wg_id < 4 && force_wg_id > 0);
                    } else {
                        //make 6.0 - 6.1 wrapper
                        wrapper = new wg6(force_wg_id != 0 && force_wg_id < 4 && force_wg_id > 0);
                    }
                    break;
                default:
                    if (ver_num < 6) {
                        getLogger().info("KarenProtect not support worldguard version " + wgd.getDescription().getVersion());
                        getLogger().info("Please update you WorldGuard version upper to 6.0>");
                        getLogger().info("Disabling KarenProtect...");
                        getServer().getPluginManager().disablePlugin(this);
                        return;
                    } else {
                        getLogger().info("KarenProtect not support worldguard version " + wgd.getDescription().getVersion());
                        getLogger().info("Please update or downgrade you WorldGuard version");
                        getLogger().info("Disabling KarenProtect...");
                        getServer().getPluginManager().disablePlugin(this);
                        return;
                    }
            }
            getLogger().info("WorldGuard Version " + wgd.getDescription().getVersion());
        } else {
            getLogger().severe("WorldGuard or WorldEdit not enabled!");
            getLogger().info("Disabling KarenProtect...");
            getServer().getPluginManager().disablePlugin(this);
        }
        try {
            metrics.addCustomChart(new Metrics.SimplePie("kp_wg_wrapper_id", () -> wrapper.getWGver()));
            if (Bukkit.getPluginManager().getPlugin("WorldEdit") != null && Bukkit.getPluginManager().getPlugin("WorldEdit").isEnabled()) {
                metrics.addCustomChart(new Metrics.SimplePie("kp_we_ver", () -> Bukkit.getPluginManager().getPlugin("WorldEdit").getDescription().getVersion()));
            }
            metrics.addCustomChart(new Metrics.SimplePie("kp_wg_nver", () -> wgd.getDescription().getVersion()));
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error on sending info to metrics");
        }
        block_list = new ArrayList<>();
        reload_list_block();
        auto_remove = conffetibox.getConfig().getInt("auto_remove");
        this.getCommand("karenprotect").setExecutor(new cmd());
        this.getCommand("karenprotect").setTabCompleter(new t_complete());
        flags = getConfig().getStringList("flags");
        help_page = getConfig().getStringList("messages.help");
        stats_page = getConfig().getStringList("messages.stats");
        getServer().getPluginManager().registerEvents(new evt(), this);
        listener = new WGRegionEventsListener(this, (WorldGuardPlugin) wgd);
        getServer().getPluginManager().registerEvents(listener, wgd);
        getLogger().info("KarenProtect " + conffetibox.getDescription().getVersion() + " has successfully started!");
        Bukkit.getScheduler().cancelTasks(conffetibox);
        task_checker = getTask_checker();
        TaskAutoCheck();
        silkTouchItems = new ArrayList<>();
        SilkTouchDropEditorReload();
        new BukkitRunnable() {
            @Override
            public void run() {
                Task_Innactive_Check();
            }
        }.runTaskTimerAsynchronously(this , 600l , 72000L);
    }
    public static Long dayToMiliseconds(int days){
        Long result = days * 86400000L;
        return result;
    }
    private static void SilkTouchDropEditorReload() {
        silkTouchItems.clear();
        for (String selection : colorystarry.conffetibox.getConfig().getConfigurationSection("silk_touch_drop_editor").getKeys(false)) {
            Material mat_target = null;
            if (colorystarry.conffetibox.getConfig().getBoolean("silk_touch_drop_editor."+selection+".enable") != true) continue;
            int amount = colorystarry.conffetibox.getConfig().getInt("silk_touch_drop_editor."+selection+".to.amount");
            if (amount == 0) amount = 1;
            String target_id = colorystarry.conffetibox.getConfig().getString("silk_touch_drop_editor."+selection+".to.item");
            for (Material material1 : Material.values()) {
                if (material1.toString().equalsIgnoreCase(target_id)) mat_target = material1;
            }
            if (mat_target == Material.AIR || mat_target == null) continue;
            boolean is_move = false;
            ItemStack itemStack;
            try {
                itemStack = new ItemStack(mat_target , amount , (byte) colorystarry.conffetibox.getConfig().getInt("silk_touch_drop_editor."+selection+".to.data"));
            }catch (Exception ee) {
                itemStack = new ItemStack(mat_target , amount);
                is_move = true;
            }
            ItemMeta itemMeta = itemStack.getItemMeta();
                if (colorystarry.conffetibox.getConfig().getString("silk_touch_drop_editor."+selection+".to.name") != null) {
                itemMeta.setDisplayName(colorystarry.conffetibox.getConfig().getString("silk_touch_drop_editor."+selection+".to.name").replaceAll("&" , "§"));
            }
            if (colorystarry.conffetibox.getConfig().getStringList("silk_touch_drop_editor."+selection+".to.lore") != null) {
                for (String str : colorystarry.conffetibox.getConfig().getStringList("silk_touch_drop_editor."+selection+".to.lore")) {
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add(str.replaceAll("&" , "§"));
                    itemMeta.setLore(lore);
                }
            }
            if (is_move) {
                Damageable dmg = (Damageable) itemMeta;
                dmg.setDamage(colorystarry.conffetibox.getConfig().getInt("silk_touch_drop_editor."+selection+".to.data"));
                itemStack.setItemMeta((ItemMeta) dmg);
            }else {
                itemStack.setItemMeta(itemMeta);
            }
            if (colorystarry.conffetibox.getConfig().get("silk_touch_drop_editor."+selection+".to.enchant") != null && colorystarry.conffetibox.getConfig().isConfigurationSection("silk_touch_drop_editor."+selection+".to.enchant")) {
                for (String string : colorystarry.conffetibox.getConfig().getConfigurationSection("silk_touch_drop_editor."+selection+".to.enchant").getKeys(false)) {
                    Enchantment enchantment = null;
                    for (Enchantment ena : Enchantment.values()) {
                        if (string.equalsIgnoreCase(ena.toString())) {
                            enchantment = ena;
                        }
                    }
                    if (enchantment == null) continue;
                    int power = colorystarry.conffetibox.getConfig().getInt("silk_touch_drop_editor."+selection+".to.enchant."+string);
                    if (power == 0) power = 1;
                    itemStack.addUnsafeEnchantment(enchantment , power);
                }
            }
            silkTouchItems.add(new SilkTouchItem(itemStack , selection));
        }
    }
    private static void TaskAutoCheck() {
        try {
            conffetibox.getConfig().getBoolean("auto_change_on_empty_data");
        } catch (Exception ee) {
            conffetibox.getLogger().info("KarenProtect: Get an error on geting config data \"auto_change_on_empty_data\"");
            return;
        }
        if (conffetibox.getConfig().getBoolean("auto_change_on_empty_data")) {
            if (task_checker == null) {
                task_checker = getTask_checker();
            }
            try {
                if (task_checker.isCancelled()) {
                    task_checker = getTask_checker();
                    task_checker.runTaskTimerAsynchronously(conffetibox, 3L, 3L);
                }
            } catch (Exception | Error e){//IllegalStateException or NoSuchMethodError
                task_checker = getTask_checker();
                task_checker.runTaskTimerAsynchronously(conffetibox, 3L, 3L);
            }
        }
    }
    @SuppressWarnings("Duplicates")
    private static void Task_Innactive_Check() {
        if (auto_remove < 1) {
            return;
        }
        if (debug) {
            conffetibox.getLogger().info("[DEBUG] Starting Innactive_Check Task");
        }
        for (World world : Bukkit.getWorlds()) {
            if (debug) {
                conffetibox.getLogger().info("[DEBUG] World: " + world.getName());
            }
            for (ProtectedRegion pt : wrapper.getRegionManager(world).getRegions().values()) {
                if (debug) {
                    conffetibox.getLogger().info("[DEBUG] Protected Region: " + pt.getId());
                }
                long max_time = 0;
                for (String str_player : pt.getOwners().getPlayers()) {
                    if (debug) {
                        conffetibox.getLogger().info("[DEBUG] Players: " + str_player);
                    }
                    OfflinePlayer ofp = Bukkit.getOfflinePlayer(str_player);
                    if (ofp != null) {
                        long last_login = ofp.getLastPlayed();
                        if (last_login > max_time) max_time = last_login;
                        }
                    }
                    if (max_time == 0) continue;
                    max_time = max_time+dayToMiliseconds(auto_remove);
                    long current_time = System.currentTimeMillis();
                if (current_time > max_time) {
                    if (debug) {
                        conffetibox.getLogger().info("[DEBUG] Current Time > MaxTime = True");
                    }
                    String id = pt.getId();
                    int indexX = id.indexOf("x");
                    int indexY = id.indexOf("y");
                    int indexZ = id.length() - 1;
                    double krx = Double.parseDouble(id.substring(5, indexX));
                    double kry = Double.parseDouble(id.substring(indexX + 1, indexY));
                    double krz = Double.parseDouble(id.substring(indexY + 1, indexZ));
                    int usex = (int) krx;
                    int usey = (int) kry;
                    int usez = (int) krz;
                    world.getBlockAt(new Location(world, usex, usey, usez)).setType(Material.AIR);
                    conffetibox.getLogger().info("Removed protection id:" + pt.getId() + " (Innactive player)");
                    wrapper.getRegionManager(world).removeRegion(pt.getId());
                }
            }
        }
        conffetibox.getLogger().info("[DEBUG] Innactive_Check Task COMPLETE");
    }
    private static BukkitRunnable getTask_checker() {
        return new BukkitRunnable() {
            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().size() > 0) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        for (ItemStack im : player.getInventory().getContents()) {
                            if (im == null) continue;
                            boolean has_item_meta = im.hasItemMeta();
                            boolean has_enchant = im.getEnchantments().size() >= 1;
                            if (has_item_meta || has_enchant) {
                                if (debug) {
                                    Logger.getLogger("KarenProtect: Debug skiping on player " + player.getName() + " on block id " + im.getType().toString());
                                }
                            } else {
                                for (KPBlock kpBlock : block_list) {
                                    if (im.getType().toString().equalsIgnoreCase(kpBlock.getBlock_id())) {
                                        ItemMeta itemMeta = im.getItemMeta();
                                        itemMeta.setDisplayName(kpBlock.getBlock_name());
                                        itemMeta.setLore(kpBlock.getBlock_lore());
                                        im.setItemMeta(itemMeta);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        };
    }
    static ItemStack getItemInHand(Player p) {
        return p.getInventory().getItem(p.getInventory().getHeldItemSlot());
    }
    static String getMessage(String path , boolean live) {
        if (live) {
            if (conffetibox.getConfig().getString("messages." + path) == null) {
                return "§cERROR: §fMESSAGE §4\"messages." + path + "\" §fNOT FOUND!";
            }
            return conffetibox.getConfig().getString("messages." + path).replaceAll("&", "§");
        }else {
            if (conffetibox.getConfig().getString(path) == null) {
                return "§cERROR: §fMESSAGE §4\"" + path + "\" §fNOT FOUND!";
            }else {
                return conffetibox.getConfig().getString(path).replaceAll("&", "§");
            }
        }
    }

    static KPBlock getBlocksKP(String id) {
        KPBlock kp_is = null;
        for (KPBlock k : block_list) {
            if (k.getBlock_id().equalsIgnoreCase(id)) {
                kp_is = k;
            }
        }
        return kp_is;
    }
    @SuppressWarnings("Duplicates")
    static KPBlock getBlocksKP(ProtectedRegion rgn , Player p) {
        if (rgn == null || p == null) {
            return null;
        }
        String id = rgn.getId();
        if (id.length() > 5 && id.substring(0, 5).equals("karen")) {
            int indexX = id.indexOf("x");
            int indexY = id.indexOf("y");
            int indexZ = id.length() - 1;
            double krx = Double.parseDouble(id.substring(5, indexX));
            double kry = Double.parseDouble(id.substring(indexX + 1, indexY));
            double krz = Double.parseDouble(id.substring(indexY + 1, indexZ));
            Block block = p.getWorld().getBlockAt(new Location(p.getWorld(), (int) krx, (int) kry, (int) krz));
            if (KPID_List().contains(block.getType().toString().toUpperCase())) {
                return getBlocksKP(block.getType().toString().toUpperCase());
            }
        }
        return null;
    }
    static ArrayList<String> KPID_List() {
        ArrayList<String> e = new ArrayList<>();
        for (KPBlock k : block_list) {
            e.add(k.getBlock_id());
        }
        return e;
    }

    static void give_block(String id, Player player, Location block_loc) {
        Material ma = Material.DIRT;
        id = id.toUpperCase();
        for (Material mat : Material.values()) {
            if (mat.toString().equalsIgnoreCase(id)) {
                ma = mat;
                break;
            }
        }
        ItemStack is = new ItemStack(ma);
        ItemMeta im = is.getItemMeta().clone();
        im.setDisplayName(colorystarry.getBlocksKP(id).getBlock_name().replaceAll("&", "§").replaceAll("<z>", String.valueOf(colorystarry.getBlocksKP(id).getSize_z())).replaceAll("<y>", String.valueOf(colorystarry.getBlocksKP(id).getSize_y())).replaceAll("<x>", String.valueOf(colorystarry.getBlocksKP(id).getSize_x())));
        ArrayList<String> lorelist = getBlocksKP(id).getBlock_lore();
        im.setLore(lorelist);
        is.setItemMeta(im);
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(block_loc, is);
        } else {
            player.getInventory().addItem(is);
        }
    }

    private static void reload_list_block() {
        colorystarry.block_list.clear();
        for (String b : conffetibox.getConfig().getConfigurationSection("blocks").getKeys(false)) {
            int x = conffetibox.getConfig().getInt("blocks." + b + ".size.x");
            int y = conffetibox.getConfig().getInt("blocks." + b + ".size.y");
            int z = conffetibox.getConfig().getInt("blocks." + b + ".size.z");
            if (Material.getMaterial(b) == null) {
                conffetibox.getLogger().severe("Block ID: " + b + " X:" + x + " Y:" + y + " Z:" + z + " not load (Material not found: " + b +")");
                continue;
            }
            ArrayList<String> block_lore = new ArrayList<>();
            for (String a : conffetibox.getConfig().getStringList("blocks." + b + ".block_infomation.lore")) {
                block_lore.add(a.replaceAll("&", "§").replaceAll("<x>", String.valueOf(x)).replaceAll("<y>", String.valueOf(y)).replaceAll("<z>", String.valueOf(z)));
            }
            ArrayList<String> place = new ArrayList<>();
            ArrayList<String> b_break = new ArrayList<>();
            ArrayList<String> entry = new ArrayList<>();
            ArrayList<String> leave = new ArrayList<>();
            if(conffetibox.getConfig().getConfigurationSection("blocks." + b + ".events") != null) {
                for (String key : conffetibox.getConfig().getConfigurationSection("blocks." + b + ".events").getKeys(false)) {
                    if (key.equalsIgnoreCase("on_entry")) {
                        if (conffetibox.getConfig().getStringList("blocks." + b + ".events."+key) != null) {
                            entry.addAll(conffetibox.getConfig().getStringList("blocks." + b + ".events." + key));
                        }
                    }
                    if (key.equalsIgnoreCase("on_leave")) {
                        if (conffetibox.getConfig().getStringList("blocks." + b + ".events."+key) != null) {
                            leave.addAll(conffetibox.getConfig().getStringList("blocks." + b + ".events." + key));
                        }
                    }
                    if (key.equalsIgnoreCase("on_place")) {
                        if (conffetibox.getConfig().getStringList("blocks." + b + ".events."+key) != null) {
                            place.addAll(conffetibox.getConfig().getStringList("blocks." + b + ".events." + key));
                        }
                    }
                    if (key.equalsIgnoreCase("on_distory")) {
                        if (conffetibox.getConfig().getStringList("blocks." + b + ".events."+key) != null) {
                            b_break.addAll(conffetibox.getConfig().getStringList("blocks." + b + ".events." + key));
                        }
                    }
                }
            }
            colorystarry.block_list.add(new KPBlock(b, x, y, z, conffetibox.getConfig().getString("blocks." + b + ".block_infomation.displayname"), block_lore, conffetibox.getConfig().getString("blocks." + b + ".name"),place,b_break,entry,leave,conffetibox.getConfig().getBoolean("blocks." + b + ".permission-required")));
            conffetibox.getLogger().info("Block ID: " + b + " X:" + x + " Y:" + y + " Z:" + z + " Loaded!");
        }
    }
    static boolean reload(CommandSender s) {
        conffetibox.getLogger().info("KarenProtect: RELOAD BROADCAST FORM " + s.getName().toUpperCase() + " RELOADING...");
        conf.in();
        reload_list_block();
        flags = conffetibox.getConfig().getStringList("flags");
        help_page = conffetibox.getConfig().getStringList("messages.help");
        stats_page = conffetibox.getConfig().getStringList("messages.stats");
        conffetibox.getLogger().info("KarenProtect " + conffetibox.getDescription().getVersion() + " has successfully reloaded!");
        s.sendMessage(colorystarry.getMessage("plugin_prefix",true) + colorystarry.getMessage("reload" , true));
        Bukkit.getScheduler().cancelTasks(conffetibox);
        TaskAutoCheck();
        silkTouchItems = new ArrayList<>();
        SilkTouchDropEditorReload();
        auto_remove = conffetibox.getConfig().getInt("auto_remove");
        return true;
    }
    static Player getPlayer(String player_name) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equalsIgnoreCase(player_name)) return p;
        }
        return null;
    }
    public static List<String> getApplicableRegionsIDs_Old(RegionManager rgm , Vector v) {
        return rgm.getApplicableRegionsIDs(v);
    }
    public static List<String> getApplicableRegionsIDs(RegionManager rgm , BlockVector3 v) {
        return rgm.getApplicableRegionsIDs(v);
    }
    @SuppressWarnings("Duplicates")
    static String getRGN(RegionManager rgm, Block pb, List<String> idList , Player p) {
        String id;
        try {
            if (idList.size() == 1) {
                id = idList.toString();
                id = id.substring(1, id.length() - 1);
            } else {
                double distanceToKP = 10000D;
                double tempToKP;
                String nameKPID = "";
                for (String currentID : idList) {
                    if (currentID.substring(0, 5).equals("karen")) {
                        currentID = currentID.replaceFirst("karen", "");
                        int indexX = currentID.indexOf("x");
                        int indexY = currentID.indexOf("y");
                        int indexZ = currentID.length() - 1;
                        double krx = Double.parseDouble(currentID.substring(0, indexX));
                        double kry = Double.parseDouble(currentID.substring(indexX + 1, indexY));
                        double krz = Double.parseDouble(currentID.substring(indexY + 1, indexZ));
                        if (pb == null) {
                            Location psLocation = new Location(p.getWorld(), krx, kry, krz);
                            tempToKP = p.getLocation().distance(psLocation);
                        }else {
                            Location psLocation = new Location(pb.getWorld(), krx, kry, krz);
                            tempToKP = pb.getLocation().distance(psLocation);
                        }
                        if (tempToKP < distanceToKP) {
                            distanceToKP = tempToKP;
                            nameKPID = "karen" + currentID;
                        }
                    }
                }
                id = nameKPID;
            }
        }catch (StringIndexOutOfBoundsException el) {
            id = "";
        }
        return id;
    }
    static Location getKPProtectedLocection(ProtectedRegion rgn , World world) {
        return getKPProtectedLocection(rgn.getId() , world);
    }
    @SuppressWarnings("Duplicates")
    static Location getKPProtectedLocection(String rgn_id , World world) {
        if (rgn_id.length() > 10 && rgn_id.substring(0, 5).equals("karen") && world != null) {
            try {
                int indexX = rgn_id.indexOf("x");
                int indexY = rgn_id.indexOf("y");
                int indexZ = rgn_id.length() - 1;
                double krx = Double.parseDouble(rgn_id.substring(5, indexX));
                double kry = Double.parseDouble(rgn_id.substring(indexX + 1, indexY));
                double krz = Double.parseDouble(rgn_id.substring(indexY + 1, indexZ));
                int usex = (int) krx;
                int usey = (int) kry;
                int usez = (int) krz;
                Location loc = new Location(world , usex , usey , usez);
                return loc;
            }catch (Exception ex) {
                return null;
            }
        }else {
            return null;
        }
    }
    static List<String> getID_List(double x, double y , double z , RegionManager rgm) {
        List<String> id_List;
        try {
            Vector v = new Vector(x, y, z);
            id_List = getApplicableRegionsIDs_Old(rgm,v);
        }catch (NoClassDefFoundError exx) {
            BlockVector3 v = BlockVector3.at(x,y,z);
            id_List = getApplicableRegionsIDs(rgm,v);
        }
        return id_List;
    }
    @SuppressWarnings("Duplicates")
    static boolean Piston_Check(Block b, List<Block> blocks) {
        if (colorystarry.conffetibox.getConfig().getBoolean("piston_protect")) {
            RegionManager rgm = colorystarry.wrapper.getRegionManager(b.getWorld());
            double x = b.getLocation().getX();
            double y = b.getLocation().getY();
            double z = b.getLocation().getZ();
            List<String> id_List = getID_List(x,y,z,rgm);
            String id = colorystarry.getRGN(rgm,b,id_List,null);
            ProtectedRegion rgn = rgm.getRegion(id);

            if (rgn == null) return false;
            if (blocks != null) {
                for (Block block : blocks) {
                    for (String id_list : colorystarry.KPID_List()) {
                        if (id_list.equalsIgnoreCase(block.getType().toString())) {
                            boolean bypass_check = false;
                            File file1 = new File(colorystarry.conffetibox.getDataFolder().getAbsolutePath()+File.separator+"hide_protection"+".yml");
                            if (file1.exists()) {
                                YamlReader hide_file = new YamlReader(file1.getAbsolutePath());
                                if (hide_file.getConfigSelection("hide_list." + rgn.getId()) != null){
                                    bypass_check = true;
                                }else {
                                    bypass_check = false;
                                }
                            }
                            return !bypass_check;
                        }
                    }
                }
            }
        }
        return false;
    }
    static boolean ActFirst_Check(Player player) {
        if (player.isOp() || player.hasPermission("karenprotect.*") || player.hasPermission("karenprotect.bypass")) return true;
        for (String worldname : colorystarry.conffetibox.getConfig().getStringList("worlds_protect_place_frist")) {
            if (worldname.equalsIgnoreCase(player.getWorld().getName())) {
                player.sendMessage(colorystarry.getMessage("plugin_prefix",true) + colorystarry.getMessage("place_first",true));
                return false;
            }
        }
        return true;
    }
}
