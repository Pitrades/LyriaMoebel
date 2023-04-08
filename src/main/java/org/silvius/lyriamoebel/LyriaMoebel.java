package org.silvius.lyriamoebel;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class LyriaMoebel extends JavaPlugin {
    private static LyriaMoebel plugin;
    private final File locationFile = new File(getDataFolder(), "locations.yaml");
    private final FileConfiguration locationsConfig = YamlConfiguration.loadConfiguration(locationFile);

    public static LyriaMoebel getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        Objects.requireNonNull(getCommand("moebel")).setExecutor(new MoebelCommand());
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
        this.getServer().broadcast(Component.text(s));
    }


}
