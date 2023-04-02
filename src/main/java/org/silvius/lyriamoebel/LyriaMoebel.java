package org.silvius.lyriamoebel;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class LyriaMoebel extends JavaPlugin {
    private static LyriaMoebel plugin;
    public static LyriaMoebel getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        getCommand("moebel").setExecutor(new MoebelCommand());
        final PluginManager pluginManager = Bukkit.getPluginManager();
        plugin = this;
        pluginManager.registerEvents(new EventListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
