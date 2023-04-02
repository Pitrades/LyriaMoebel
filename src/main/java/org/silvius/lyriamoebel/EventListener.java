package org.silvius.lyriamoebel;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.UUID;

public class EventListener implements Listener {
    private static final ArrayList<UUID> cooldown = new ArrayList<>();
    private static void summonChairAfterUpdate(Block block, BlockFace face){
        block.setType(Material.WHITE_BED);
        BlockData blockData = block.getBlockData();
        if(!(blockData instanceof Directional)){return;}
        ((Directional) blockData).setFacing(face);
        block.setBlockData(blockData);
    }

    private static void summonMoebel(Player player, Material material, Block block){
        //Block über dem angeklickten muss Luft sein, sonst return

        if(!cooldown.contains(player.getUniqueId())){
            cooldown.add(player.getUniqueId());
            LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () -> cooldown.remove(player.getUniqueId()), 3L);
            if(block.getType()!=Material.AIR){return;}
            checkForBeds(block, 0);
            block.setType(material);
            player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount()-1);
            if(material==Material.PISTON_HEAD){
                BlockData blockData = block.getBlockData();
                ((Directional) blockData).setFacing(BlockFace.UP);
                block.setBlockData(blockData);

            }
            if(material==Material.WHITE_BED){
                BlockData blockData = block.getBlockData();
                ((Directional) blockData).setFacing(player.getFacing());
                block.setBlockData(blockData);

            }
    }}
    private static void dropMoebelItem(Location location, String s){
        location.getWorld().dropItemNaturally(location, MoebelCommand.generateItem(s));
    }

    private static boolean isValidPistonHead(Block block){
        final Location location = block.getLocation();
        final BlockData blockdata = block.getBlockData();

        if(location.add(0, -1, 0).getBlock().getType()==Material.PISTON) {return false;}
        if(((Directional) blockdata).getFacing()!=BlockFace.UP){return false;}
        return true;
    }
    private static void oppositeFacingBedEvent(Bed blockdata1, Bed blockdata2,Block block1, Block block2){
        System.out.println("DW");
        return;
//        checkForBeds(block1, 1);
//        checkForBeds(block2, 1);
//        block1.setType(Material.AIR);
//        block2.setType(Material.AIR);
//
//
//
//        LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask( LyriaMoebel.getPlugin(), new Runnable() {
//
//            public void run() {
//                summonChairAfterUpdate(block1, ((Directional) blockdata1).getFacing());
//                summonChairAfterUpdate(block2, ((Directional) blockdata2).getFacing());
//            }
//        }, (long) 0);
    }
    private static boolean isValidBedHelp(Bed blockdata1, Bed blockdata2,Block block1, Block block2){
        if(blockdata1.getPart()== Bed.Part.FOOT && blockdata2.getPart()== Bed.Part.HEAD){
            return false;
        }
        if(blockdata2.getPart()== Bed.Part.FOOT && blockdata1.getPart()== Bed.Part.HEAD){
            return false;
        }
        if(blockdata2.getFacing()==blockdata1.getFacing().getOppositeFace()){
            oppositeFacingBedEvent(blockdata1, blockdata2, block1, block2);
            return false;}
        return true;
    }
    private static boolean isValidBed(Block block){

        Bed blockdata1 = (Bed) block.getBlockData();
        Block adjacentBlock = block.getLocation().add(1, 0, 0).getBlock();
        Bed blockdata2;
        if(adjacentBlock.getType()==Material.WHITE_BED) {
            blockdata2 = (Bed) adjacentBlock.getBlockData();

            return isValidBedHelp(blockdata1, blockdata2, block, adjacentBlock);
        }
        adjacentBlock = block.getLocation().add(-1, 0, 0).getBlock();
        if(adjacentBlock.getType()==Material.WHITE_BED) {
            blockdata2 = (Bed) adjacentBlock.getBlockData();

            return isValidBedHelp(blockdata1, blockdata2, block, adjacentBlock);
        }
        adjacentBlock = block.getLocation().add(0, 0, 1).getBlock();
        if(adjacentBlock.getType()==Material.WHITE_BED) {
            blockdata2 = (Bed) adjacentBlock.getBlockData();

            return isValidBedHelp(blockdata1, blockdata2, block, adjacentBlock);
        }
        adjacentBlock = block.getLocation().add(0, 0, -1).getBlock();
        if(adjacentBlock.getType()==Material.WHITE_BED) {
            blockdata2 = (Bed) adjacentBlock.getBlockData();

            return isValidBedHelp(blockdata1, blockdata2, block, adjacentBlock);
        }

        return true;
    }

    private static void stopChairDestroy(Block block, Integer i){
        if(!isValidBed(block)){return;}
        if(i>10){
            dropMoebelItem(block.getLocation(), "Stuhl");
            return;}
        final BlockData blockdata = block.getBlockData();
        checkForBeds(block, i);
        block.setType(Material.AIR);



        LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask( LyriaMoebel.getPlugin(), new Runnable() {

            public void run() {
                summonChairAfterUpdate(block, ((Directional) blockdata).getFacing());

            }
        }, (long) i);
    }

    private static void checkForBeds(Block block, Integer i){

        Bed blockdata;

        Block adjacentBlock = block.getLocation().add(1, 0, 0).getBlock();
        if(adjacentBlock.getType()==Material.WHITE_BED) {
            blockdata = (Bed) adjacentBlock.getBlockData();

            if(blockdata.getFacing()==BlockFace.WEST && blockdata.getPart()==Bed.Part.FOOT){
            stopChairDestroy(adjacentBlock, i+1);}
        }
        adjacentBlock = block.getLocation().add(-1, 0, 0).getBlock();
        if(adjacentBlock.getType()==Material.WHITE_BED) {
            blockdata = (Bed) adjacentBlock.getBlockData();
            if(((Directional) blockdata).getFacing()==BlockFace.EAST && blockdata.getPart()==Bed.Part.FOOT){
                stopChairDestroy(adjacentBlock, i+1);}
        }
        adjacentBlock = block.getLocation().add(0, 0, 1).getBlock();
        if(adjacentBlock.getType()==Material.WHITE_BED) {
            blockdata = (Bed) adjacentBlock.getBlockData();
            if(((Directional) blockdata).getFacing()==BlockFace.NORTH && blockdata.getPart()==Bed.Part.FOOT){
                stopChairDestroy(adjacentBlock, i+1);}
        }
        adjacentBlock = block.getLocation().add(0, 0, -1).getBlock();
        if(adjacentBlock.getType()==Material.WHITE_BED) {
            blockdata = (Bed) adjacentBlock.getBlockData();
            if(((Directional) blockdata).getFacing()==BlockFace.SOUTH && blockdata.getPart()==Bed.Part.FOOT){
                stopChairDestroy(adjacentBlock, i+1);}
        }

    }
    @EventHandler
    public static void onRightClick(PlayerInteractEvent event){
        final Player player = event.getPlayer();
        final ItemStack item = event.getItem();
        final Block block = event.getClickedBlock();
        if(!event.getAction().isRightClick()){return;}
        if(item==null){return;}
        if(block==null){return;}
        if(!item.hasItemMeta()){return;}
        if(event.getBlockFace()!=BlockFace.UP){return;}
        final ItemMeta meta = item.getItemMeta();
        if(!meta.hasLore()){return;}
        if(meta.lore().toString().contains("(CIT) Stuhl")){
            //Spawne Stuhl


            summonMoebel(player, Material.WHITE_BED, block.getLocation().add(0, 1, 0).getBlock());
        }

        if(meta.lore().toString().contains("(CIT) Tisch")){
            //Spawne Tisch
            summonMoebel(player, Material.PISTON_HEAD, block.getLocation().add(0, 1, 0).getBlock());

        }
    }
    @EventHandler
    public static void onPlayerBedEnter(PlayerInteractEvent event){
        Block block = event.getClickedBlock();
        if(block==null){return;}

        if(block.getType()==Material.WHITE_BED && isValidBed(block) && event.getAction().isRightClick()){
            event.setCancelled(true);
        }
    }


        @EventHandler
    public static void onBlockBreak(BlockBreakEvent event){
        final Block brokenBlock = event.getBlock();
        final Block blockAbove = brokenBlock.getLocation().add(0, 1 ,0).getBlock();
        if(brokenBlock.getType()==Material.PISTON_HEAD){
            if(!isValidPistonHead(brokenBlock)){return;}
            dropMoebelItem(brokenBlock.getLocation(), "Tisch");
        }
        if(blockAbove.getType()==Material.PISTON_HEAD){
            if(!isValidPistonHead(blockAbove)){return;}
            dropMoebelItem(blockAbove.getLocation(), "Tisch");
        }
        if(brokenBlock.getType()==Material.WHITE_BED){
            if(!isValidBed(brokenBlock)){return;}
            dropMoebelItem(event.getBlock().getLocation(), "Stuhl");
        }
    }
@EventHandler
    public static void onBlockPlace(BlockPlaceEvent event){
        checkForBeds(event.getBlock(), 0);

    }

    @EventHandler
    public static void onBlockPlace(BlockBreakEvent event){
        checkForBeds(event.getBlock(), 0);
    }
    @EventHandler
    public static void onBlockPhysicsEvent(BlockPhysicsEvent event){
        Block block = event.getBlock();
        if(block.getType()==Material.WHITE_BED){
            if(!isValidBed(block)){return;}
            dropMoebelItem(event.getBlock().getLocation(), "Stuhl");
        }
    }
}
