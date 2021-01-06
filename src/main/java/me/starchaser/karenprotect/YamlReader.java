package me.starchaser.karenprotect;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class YamlReader {
    final String Loc;
    public YamlReader(String Location) {
        Loc = Location;
    }
    public ConfigurationSection getConfigSelection(String key) {
        File customYml = new File(Loc);
        FileConfiguration customConfig = YamlConfiguration.loadConfiguration(customYml);
        ConfigurationSection cfg_list = customConfig.getConfigurationSection(key);
        return cfg_list;
    }
    public String getString(String key) {
        String str;
        File customYml = new File(Loc);
        FileConfiguration customConfig = YamlConfiguration.loadConfiguration(customYml);
        str = customConfig.getString(key);
        return str;
    }
    public Long getLong(String key) {
        long l;
        File customYml = new File(Loc);
        FileConfiguration customConfig = YamlConfiguration.loadConfiguration(customYml);
        l = customConfig.getLong(key);
        return l;
    }
    public List<String> getStringList(String key) {
        List<String> str;
        File customYml = new File(Loc);
        FileConfiguration customConfig = YamlConfiguration.loadConfiguration(customYml);
        str = customConfig.getStringList(key);
        return str;
    }
    public int getInt(String key) {
        int i;
        File customYml = new File(Loc);
        FileConfiguration customConfig = YamlConfiguration.loadConfiguration(customYml);
        i = customConfig.getInt(key);
        return i;
    }
    public void set(String key , Object value) {
        File customYml = new File(Loc);
        FileConfiguration customConfig = YamlConfiguration.loadConfiguration(customYml);
        customConfig.set(key,value);
        try {
            customConfig.save(Loc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
