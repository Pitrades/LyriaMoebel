package org.silvius.lyriamoebel;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

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
        pluginManager.registerEvents(new Listeners(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    static class CustomFilter implements Filter {
        @Override
        public boolean isLoggable(LogRecord record) {
            //System.out.println(record.getMessage());
            return false;
        }
    }}
