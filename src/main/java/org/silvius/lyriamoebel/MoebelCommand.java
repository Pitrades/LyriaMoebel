package org.silvius.lyriamoebel;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MoebelCommand implements CommandExecutor {
    protected static ItemStack generateItem(String s, Integer amount){
        final ItemStack item = new ItemStack(Material.STICK);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(s);
        final ArrayList<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add(ChatColor.BLACK + "(CIT) "+s);
        meta.setLore(lore);
        item.setItemMeta(meta);
        item.setAmount(amount);
        return item;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            final Player player = ((Player) commandSender).getPlayer();

            if(!player.hasPermission("lyriamoebel.moebel")){
                commandSender.sendMessage(ChatColor.RED+"Keine Berechtigung");
                return true;
            }
            if(strings.length==0){
                commandSender.sendMessage(ChatColor.RED+"Fehlende Argumente");
                return true;
            }
            if(strings.length==2){
                commandSender.sendMessage(ChatColor.RED+"Zu viele Argumente");
                return true;
            }
            if(strings[0].equals("stuhl")){
                final ItemStack stack = generateItem("Stuhl", 1);

                player.getInventory().addItem(stack);
            } else if (strings[0].equals("tisch")) {
                final ItemStack stack = generateItem("Tisch",1 );
                player.getInventory().addItem(stack);
            }


        }
        return true;
    }
}
