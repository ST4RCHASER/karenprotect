package me.starchaser.karenprotect;

import org.bukkit.Bukkit;

import java.util.Arrays;

import static me.starchaser.karenprotect.colorystarry.conffetibox;

class conf {
    static void in() {
        conffetibox.getConfig().options().copyDefaults(true);
        conffetibox.getConfig().options().copyHeader(true);
        conffetibox.saveDefaultConfig();
        conffetibox.reloadConfig();
        if (Integer.valueOf(conffetibox.getConfig().getString("karen")) < 6) {
            Bukkit.getLogger().severe("########################################################");
            Bukkit.getLogger().severe("########################################################");
            Bukkit.getLogger().severe("KarenProtect need to reset config file");
            Bukkit.getLogger().severe("TODO: Remove config file and start karenprotect again!");
            Bukkit.getLogger().severe("########################################################");
            Bukkit.getLogger().severe("########################################################");
            Bukkit.getPluginManager().disablePlugin(conffetibox);
        }
        if (Integer.valueOf(conffetibox.getConfig().getString("karen")) == 6) {
            conffetibox.getConfig().set("messages.place_noperm" , "&cYou not have permission to place that block!");
            conffetibox.getConfig().set("messages.cleanup" , "&aCleanup complete! &7&o(<value>)");
            conffetibox.getConfig().set("messages.banded" , "&cBanned");
            conffetibox.getConfig().set("messages.not_banded" , "&aNot banned");
            conffetibox.getConfig().set("messages.hide" , "&aThis protection is now disappear!");
            conffetibox.getConfig().set("messages.hide_already" , "&cThis protection is already hide");
            conffetibox.getConfig().set("messages.unhide" , "&aThis protection is now reappear");
            conffetibox.getConfig().set("messages.unhide_already" , "&cThis protection is already unhide");
            conffetibox.getConfig().set("messages.hide_no_perm" , "&cYou do not have permission to use hide command. &7(&akarenprotect&7.&chide&7)");
            conffetibox.getConfig().set("messages.hide_deny" , "&cYou cannot use hide command in this area!");
            conffetibox.getConfig().set("messages.unhide_no_perm" , "&cYou do not have permission to use hide command. &7(&akarenprotect&7.&cunhide&7)");
            conffetibox.getConfig().set("messages.unhide_deny" , "&cYou cannot use unhide command in this area!");
            conffetibox.getConfig().set("messages.stats_format_world" , "   &bWorlds:");
            conffetibox.getConfig().set("messages.stats_format_world_display" , "    &e<world_name>:");
            conffetibox.getConfig().set("messages.stats_format_data" , "     &7ID: &b<id> &8x&b<x> &8y&b<y> &8z&b<z> &7Status: <status>");
            conffetibox.getConfig().set("messages.stats_format_member" , "&aMember");
            conffetibox.getConfig().set("messages.stats_format_owner" , "&bOwner");
            conffetibox.getConfig().set("messages.stats_player_notfound" , "&cThat player (<player>) not found!");
            conffetibox.getConfig().set("messages.stats" , Arrays.asList("&9&l=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-="
                    , "    &e&lKaren &b&lProtect &7&lStats Page"
                    , "&r"
                    , "  &bInfo:"
                    , "   &dPlayer: &b<player>"
                    , "   &bBan: <ban>"
                    , "   &aLastPlayed: &e<last_played> day(s) ago."
                    , "   &aFirstPlayed: &e<first_played> day(s) ago."
                    , "  &bRegions list &f(&a<regions_count>&f)"
                    , "<region_data>"
                    , "&r"
                    , "&9&l=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-="
            ));
            conffetibox.getConfig().set("karen" , 7);
            conffetibox.getLogger().info("Config updated! (We recommend to make new config for stable)");
        }
        conffetibox.reloadConfig();
    }
}
