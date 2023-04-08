package org.silvius.lyriamoebel;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MoebelCommand implements CommandExecutor {
    protected static ItemStack generateItem(String s){
        final ItemStack item = new ItemStack(Material.STICK);
        final ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(s));
        final ArrayList<Component> lore = new ArrayList<>();
        lore.add(Component.text(ChatColor.BLACK + "(CIT) "+s));
        meta.lore(lore);



        item.setItemMeta(meta);
        item.setAmount(1);
        return item;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
            final Player player = ((Player) commandSender).getPlayer();

            assert player != null;
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
            switch (strings[0]) {
                case "stuhl": {
                    final ItemStack stack = generateItem("Stuhl");

                    player.getInventory().addItem(stack);
                    break;
                }
                case "tisch": {
                    final ItemStack stack = generateItem("Tisch");
                    player.getInventory().addItem(stack);
                    break;
                }
                case "tür": {
                    final ItemStack stack = generateItem("Tür");
                    player.getInventory().addItem(stack);
                    break;
                }
            }


        }
        return true;
    }
}
