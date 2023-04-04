package org.silvius.lyriamoebel;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Listeners implements Listener {
    private static final ArrayList<UUID> cooldownPlace = new ArrayList<>();
    private static final ArrayList<Location> cooldownDrop = new ArrayList<>();
    private final HashMap<Location, String[]> lastBrokenBlock;

    public Listeners() {
        this.lastBrokenBlock=new HashMap<>();
    }

    private static void summonChairAfterUpdate(Block block, BlockFace face){
        block.setType(Material.WHITE_BED);
        final BlockData blockData = block.getBlockData();
        if(!(blockData instanceof Directional)){return;}
        ((Directional) blockData).setFacing(face);
        block.setBlockData(blockData);
    }


    private static void summonMoebel(Player player, Material material, Block block){
        //Block über dem angeklickten muss Luft sein, sonst return

        if(!cooldownPlace.contains(player.getUniqueId())){
            cooldownPlace.add(player.getUniqueId());
            LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () -> cooldownPlace.remove(player.getUniqueId()), 3L);
            if(block.getType()!=Material.AIR){return;}
            block.setType(material);

            player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount()-1);
            if(material==Material.PISTON_HEAD){
                final BlockData blockData = block.getBlockData();
                ((Directional) blockData).setFacing(BlockFace.UP);
                block.setBlockData(blockData);

            }
            if(material==Material.WHITE_BED){
                final BlockData blockData = block.getBlockData();
                ((Bed) blockData).setFacing(player.getFacing());
                block.setBlockData(blockData);

            }
    }}
    private static void dropMoebelItem(Location location, String s, Integer amount){
        if(!cooldownDrop.contains(location)){
            cooldownDrop.add(location);
            LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () -> cooldownDrop.remove(location), 1L);
        location.getWorld().dropItemNaturally(location, MoebelCommand.generateItem(s,amount));

    }}


    private static boolean isValidBedHelp(Bed blockdata1, Bed blockdata2){
        if(blockdata1.getPart()== Bed.Part.FOOT && blockdata2.getPart()== Bed.Part.HEAD){
            return false;
        }
        if(blockdata2.getPart()== Bed.Part.FOOT && blockdata1.getPart()== Bed.Part.HEAD){
            return false;
        }
        return true;
    }
    private static boolean isValidBed(Location location, Bed blockdata1){
        final List<Block> adjacentBeds = checkForBeds(location.getBlock());
        for(Integer i=0; i<adjacentBeds.size(); i++){
            if(isValidBedHelp(blockdata1, (Bed) adjacentBeds.get(i).getBlockData())){return false;}
        }
//        adjacentBlock = location.add(1, 0, 0).getBlock();
//        Bed blockdata2;
//        if(adjacentBlock.getType()==Material.WHITE_BED) {
//            blockdata2 = (Bed) adjacentBlock.getBlockData();
//
//            return isValidBedHelp(blockdata1, blockdata2);
//        }
//        adjacentBlock = location.add(-2, 0, 0).getBlock();
//        if(adjacentBlock.getType()==Material.WHITE_BED) {
//            blockdata2 = (Bed) adjacentBlock.getBlockData();
//
//            return isValidBedHelp(blockdata1, blockdata2);
//        }
//        adjacentBlock = location.add(1, 0, 1).getBlock();
//        if(adjacentBlock.getType()==Material.WHITE_BED) {
//            blockdata2 = (Bed) adjacentBlock.getBlockData();
//
//            return isValidBedHelp(blockdata1, blockdata2);
//        }
//        adjacentBlock = location.add(0, 0, -2).getBlock();
//        if(adjacentBlock.getType()==Material.WHITE_BED) {
//            blockdata2 = (Bed) adjacentBlock.getBlockData();
//
//            return isValidBedHelp(blockdata1, blockdata2);
//        }
//
        return true;
    }

    private static boolean isValidPistonHead(Block block){
        final Location location = block.getLocation();
        final BlockData blockdata = block.getBlockData();

        if(location.add(0, -1, 0).getBlock().getType()==Material.PISTON) {return false;}
        if(((Directional) blockdata).getFacing()!=BlockFace.UP){return false;}
        return true;
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
            event.getPlayer().swingMainHand();
            summonMoebel(player, Material.WHITE_BED, block.getLocation().add(0, 1, 0).getBlock());
        }

        if(meta.lore().toString().contains("(CIT) Tisch")){
            //Spawne Tisch
            event.getPlayer().swingMainHand();
            summonMoebel(player, Material.PISTON_HEAD, block.getLocation().add(0, 1, 0).getBlock());

        }
    }
    @EventHandler
    public static void onPlayerBedEnter(PlayerInteractEvent event){
        final Block block = event.getClickedBlock();
        if(block==null){return;}

        if(block.getType()==Material.WHITE_BED && isValidBed(block.getLocation(), (Bed) block.getBlockData()) && event.getAction().isRightClick()){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        final Block block = event.getBlock();
        this.lastBrokenBlock.put(event.getBlock().getLocation(), new String[]{block.getType().toString(), block.getBlockData().getAsString(true)});
        System.out.println("E1");

        final Block blockAbove = block.getLocation().add(0, 1 ,0).getBlock();

        if(block.getType()==Material.PISTON_HEAD){
            if(!isValidPistonHead(block)){return;}
            dropMoebelItem(block.getLocation(), "Tisch",1 );
        }
        if(blockAbove.getType()==Material.PISTON_HEAD){
            if(!isValidPistonHead(blockAbove)){return;}
            dropMoebelItem(blockAbove.getLocation(), "Tisch",1 );
        }
        if(block.getType()==Material.WHITE_BED){
            if(!isValidBed(block.getLocation(), (Bed) block.getBlockData())){return;}
            dropMoebelItem(event.getBlock().getLocation(), "Stuhl",1 );
        }
    }



    private static List<Block> checkForBeds(Block block){

        Bed blockdata;
        final List<Block> blocklist= new ArrayList<>();
        Block adjacentBlock = block.getLocation().add(1, 0, 0).getBlock();
        if(adjacentBlock.getType()==Material.WHITE_BED) {
            blockdata = (Bed) adjacentBlock.getBlockData();

            if(blockdata.getFacing()==BlockFace.WEST && blockdata.getPart()==Bed.Part.FOOT){
                blocklist.add(adjacentBlock);}
        }
        adjacentBlock = block.getLocation().add(-1, 0, 0).getBlock();
        if(adjacentBlock.getType()==Material.WHITE_BED) {
            blockdata = (Bed) adjacentBlock.getBlockData();
            if(((Directional) blockdata).getFacing()==BlockFace.EAST && blockdata.getPart()==Bed.Part.FOOT){
                blocklist.add(adjacentBlock);}
        }
        adjacentBlock = block.getLocation().add(0, 0, 1).getBlock();
        if(adjacentBlock.getType()==Material.WHITE_BED) {
            blockdata = (Bed) adjacentBlock.getBlockData();
            if(((Directional) blockdata).getFacing()==BlockFace.NORTH && blockdata.getPart()==Bed.Part.FOOT){
                blocklist.add(adjacentBlock);}
        }
        adjacentBlock = block.getLocation().add(0, 0, -1).getBlock();
        if(adjacentBlock.getType()==Material.WHITE_BED) {
            blockdata = (Bed) adjacentBlock.getBlockData();
            if(((Directional) blockdata).getFacing()==BlockFace.SOUTH && blockdata.getPart()==Bed.Part.FOOT){
                blocklist.add(adjacentBlock);}

        }
        return blocklist;}
    @EventHandler
    public void onBlockPhysicsUpdate(BlockPhysicsEvent event){
        final Block block = event.getBlock();
        Bed blockdata;
        System.out.println("0");

        if(this.lastBrokenBlock.containsKey(block.getLocation())){
            final String[] newBlockStrings = lastBrokenBlock.get(block.getLocation());
            final String savedBlockString = newBlockStrings[1];// get the saved block string from wherever you saved it
            final BlockData savedBlockData = Bukkit.createBlockData(savedBlockString);
            //if(!(savedBlockData instanceof Door)){lastBrokenBlock.remove(block.getLocation());}
            LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () -> lastBrokenBlock.remove(block.getLocation()), 1L);

            if(isMultiBlock(savedBlockData, block.getLocation())) {
                if(savedBlockData instanceof Bed){
                    return;
                }
                if (savedBlockData.getMaterial().hasGravity() || block.getLocation().add(0, 1, 0).getBlock().getBlockData() instanceof Ageable || savedBlockData instanceof Door) {


                    if ((checkForBeds(block).size() > 0)) {
                        dropMoebelItem(event.getBlock().getLocation(), "Stuhl", checkForBeds(block).size());
                        return;
                    }


                    if (checkForBeds(block.getLocation().add(0, -1, 0).getBlock()).size() > 0) {
                        dropMoebelItem(block.getLocation().add(0, -1, 0), "Stuhl", checkForBeds(block.getLocation().add(0, -1, 0).getBlock()).size());
                        return;}
                    //
                }


            }
        }


        if(block.getBlockData() instanceof Door && event.getSourceBlock().getBlockData() instanceof Door && event.getChangedBlockData() instanceof Door){
            if(checkForBeds(block).size() > 0){
                return;}}

        if(checkForBeds(block).size() > 0){
            event.setCancelled(true);
        }

    }

    private boolean isMultiBlock(BlockData savedBlockData, Location location) {
        if(savedBlockData instanceof Bed && !isValidBed(location, (Bed) savedBlockData)){return true;}
        if(savedBlockData instanceof Door){return true;}
        final Block blockAbove = location.add(0, 1 ,0).getBlock();
        if(blockAbove.getType().hasGravity()){
            return true;
        }
        if(blockAbove.getBlockData() instanceof Ageable){return true;}
        return false;
    }


}
