package org.silvius.lyriamoebel;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class LyriaMoebel extends JavaPlugin {
    private static LyriaMoebel plugin;
    private File locationFile = new File(getDataFolder(), "locations.yaml");
    private FileConfiguration locationsConfig = YamlConfiguration.loadConfiguration(locationFile);

    public static LyriaMoebel getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        getCommand("moebel").setExecutor(new MoebelCommand());
        final PluginManager pluginManager = Bukkit.getPluginManager();
        plugin = this;
        //pluginManager.registerEvents(new ListenersNew(), this);
        pluginManager.registerEvents(new Listeners(this), this);
        if(!locationFile.exists()){saveResource("locations.yaml", false);}

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public FileConfiguration getLocationsConfig(){
        return locationsConfig;
    }
    public File getLocationFile(){
        return locationFile;
    }
    public void saveLocationFile(){
        try{                locationsConfig.save(getLocationFile());}
        catch (IOException e){e.printStackTrace();}
    }
    public void broadcast(String s){
        this.getServer().broadcastMessage(s);
    }


}
