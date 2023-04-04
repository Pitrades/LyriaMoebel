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
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ListenersNew implements Listener {
    private static final ArrayList<UUID> cooldownPlace = new ArrayList<>();
    private static final ArrayList<Location> cooldownDrop = new ArrayList<>();
    private final HashMap<Location, String[]> lastBrokenBlock;
    private final HashMap<Location, String[]> lastPlacedBlock;
    private final HashMap<Location, String[]> lastClickedBlock;


    public ListenersNew() {
        this.lastBrokenBlock=new HashMap<>();
        this.lastPlacedBlock=new HashMap<>();
        this.lastClickedBlock=new HashMap<>();
    }

    private static void summonChairAfterUpdate(Block block, BlockFace face){
        block.setType(Material.WHITE_BED);
        final BlockData blockData = block.getBlockData();
        if(!(blockData instanceof Directional)){return;}
        ((Directional) blockData).setFacing(face);
        block.setBlockData(blockData);
    }


    private void summonMoebel(Player player, Material material, Block block){
        //Block über dem angeklickten muss Luft sein, sonst return

        if(!cooldownPlace.contains(player.getUniqueId())){
            cooldownPlace.add(player.getUniqueId());
            LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () -> cooldownPlace.remove(player.getUniqueId()), 3L);
            if(block.getType()!=Material.AIR){return;}
            addtoBlockPlaceList(block);
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
        if(blockdata1.getPart()== Bed.Part.FOOT && blockdata2.getPart()== Bed.Part.HEAD && blockdata1.getFacing()== blockdata2.getFacing()){
            return false;
        }
        if(blockdata2.getPart()== Bed.Part.FOOT && blockdata1.getPart()== Bed.Part.HEAD && blockdata1.getFacing()== blockdata2.getFacing()){
            return false;
        }
        return true;
    }
    private static boolean isValidBed(Location location, Bed blockdata1){
        Block adjacentBlock = location.add(1, 0, 0).getBlock();
        Bed blockdata2;
        if(adjacentBlock.getType()==Material.WHITE_BED) {
            blockdata2 = (Bed) adjacentBlock.getBlockData();
            if(!isValidBedHelp(blockdata1, blockdata2)){return false;}
        }
        adjacentBlock = location.add(-2, 0, 0).getBlock();
        if(adjacentBlock.getType()==Material.WHITE_BED) {
            blockdata2 = (Bed) adjacentBlock.getBlockData();

            if(!isValidBedHelp(blockdata1, blockdata2)){return false;}
        }
        adjacentBlock = location.add(1, 0, 1).getBlock();
        if(adjacentBlock.getType()==Material.WHITE_BED) {
            blockdata2 = (Bed) adjacentBlock.getBlockData();

            if(!isValidBedHelp(blockdata1, blockdata2)){return false;}
        }
        adjacentBlock = location.add(0, 0, -2).getBlock();
        if(adjacentBlock.getType()==Material.WHITE_BED) {
            blockdata2 = (Bed) adjacentBlock.getBlockData();

            if(!isValidBedHelp(blockdata1, blockdata2)){return false;}
        }

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
    public void onRightClick(PlayerInteractEvent event){
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
    public void onBlockPlace(BlockPlaceEvent event){
        final Block block = event.getBlock();
        addtoBlockPlaceList(block);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){

        final Block block = event.getBlock();
        final Block blockAbove = block.getLocation().add(0, 1 ,0).getBlock();
        addToBlockBreakList(block);

        if(block.getType()==Material.PISTON_HEAD){
            if(!isValidPistonHead(block)){return;}
            dropMoebelItem(block.getLocation(), "Tisch",1 );
        }
        if(blockAbove.getType()==Material.PISTON_HEAD){
            if(!isValidPistonHead(blockAbove)){return;}
            dropMoebelItem(blockAbove.getLocation(), "Tisch",1 );
        }
        if(block.getBlockData() instanceof Bed){
            if(!isValidBed(block.getLocation(), (Bed) block.getBlockData())){return;}
            dropMoebelItem(event.getBlock().getLocation(), "Stuhl",1 );
        }
    }


    private void addToBlockBreakList(Block block){
        final Block blockAbove = block.getLocation().add(0, 1 ,0).getBlock();
        BlockData blockData = block.getBlockData();
        final List<Block> bedList = checkForBeds(block);

        if(blockData instanceof Bed){
            if(((Bed) blockData).getPart() == Bed.Part.HEAD){
                this.lastBrokenBlock.put(block.getLocation(), new String[]{blockData.getMaterial().toString(), blockData.getAsString(true)});
                return;
            }
        }
        if(bedList.size()>0){

            for(int i = 0; i<bedList.size(); i++){

                Block bedBlock = bedList.get(i);
                if(((Bed) bedBlock.getBlockData()).getPart()==Bed.Part.HEAD){continue;}

                this.lastBrokenBlock.put(bedBlock.getLocation(), new String[]{bedBlock.getType().toString(), bedBlock.getBlockData().getAsString(true)});

                bedBlock.setType(Material.AIR);

                LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () ->   summonChairAfterUpdate(bedBlock, bedBlock.getFace(block)), 1L);

            }
        }

        if(block.getBlockData() instanceof Door){

            final Block blockBelow = block.getLocation().add(0, -1 ,0).getBlock();
            final List<Block> bedListBelow = checkForBeds(blockBelow);
            if(bedListBelow.size()>0){

                for(int i = 0; i<bedListBelow.size(); i++){

                    Block bedBlock = bedListBelow.get(i);
                    this.lastBrokenBlock.put(bedBlock.getLocation(), new String[]{bedBlock.getType().toString(), bedBlock.getBlockData().getAsString(true)});

                    bedBlock.setType(Material.AIR);

                    LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () ->   summonChairAfterUpdate(bedBlock, bedBlock.getFace(blockBelow)), 1L);

                }
            }

            final List<Block> bedListAbove = checkForBeds(blockAbove);
            if(bedListAbove.size()>0){

                for(int i = 0; i<bedListAbove.size(); i++){

                    Block bedBlock = bedListAbove.get(i);
                    this.lastBrokenBlock.put(bedBlock.getLocation(), new String[]{bedBlock.getType().toString(), bedBlock.getBlockData().getAsString(true)});

                    bedBlock.setType(Material.AIR);

                    LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () ->   summonChairAfterUpdate(bedBlock, bedBlock.getFace(blockAbove)), 1L);

                }
            }


        }

        }


    private void addtoBlockPlaceList(Block block){
        final Block blockAbove = block.getLocation().add(0, 1 ,0).getBlock();
        final List<Block> bedList = checkForBeds(block);
        if(bedList.size()>0){

            for(int i = 0; i<bedList.size(); i++){
                Block bedBlock = bedList.get(i);
                if(((Bed) bedBlock.getBlockData()).getPart()==Bed.Part.HEAD){continue;}
                this.lastPlacedBlock.put(bedBlock.getLocation(), new String[]{bedBlock.getType().toString(), bedBlock.getBlockData().getAsString(true)});

                bedBlock.setType(Material.AIR);

                LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () ->   summonChairAfterUpdate(bedBlock, bedBlock.getFace(block)), 1L);

            }
        }

        if(block.getBlockData() instanceof Door){

            final Block blockBelow = block.getLocation().add(0, -1 ,0).getBlock();
            final List<Block> bedListBelow = checkForBeds(blockBelow);
            if(bedListBelow.size()>0){

                for(int i = 0; i<bedListBelow.size(); i++){

                    Block bedBlock = bedListBelow.get(i);
                    this.lastPlacedBlock.put(bedBlock.getLocation(), new String[]{bedBlock.getType().toString(), bedBlock.getBlockData().getAsString(true)});

                    bedBlock.setType(Material.AIR);

                    LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () ->   summonChairAfterUpdate(bedBlock, bedBlock.getFace(blockBelow)), 1L);

                }
            }

            final List<Block> bedListAbove = checkForBeds(blockAbove);
            if(bedListAbove.size()>0){

                for(int i = 0; i<bedListAbove.size(); i++){

                    Block bedBlock = bedListAbove.get(i);
                    this.lastPlacedBlock.put(bedBlock.getLocation(), new String[]{bedBlock.getType().toString(), bedBlock.getBlockData().getAsString(true)});

                    bedBlock.setType(Material.AIR);

                    LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () ->   summonChairAfterUpdate(bedBlock, bedBlock.getFace(blockAbove)), 1L);

                }
            }


        }
    }

    private void addToBlockClickList(Block block){
        final Block blockAbove = block.getLocation().add(0, 1 ,0).getBlock();
        final List<Block> bedList = checkForBeds(block);
        if(bedList.size()>0){

            for(int i = 0; i<bedList.size(); i++){
                Block bedBlock = bedList.get(i);
                if(((Bed) bedBlock.getBlockData()).getPart()==Bed.Part.HEAD){continue;}
                this.lastClickedBlock.put(bedBlock.getLocation(), new String[]{bedBlock.getType().toString(), bedBlock.getBlockData().getAsString(true)});

                bedBlock.setType(Material.AIR);

                LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () ->   summonChairAfterUpdate(bedBlock, bedBlock.getFace(block)), 1L);

            }
        }

        if(block.getBlockData() instanceof Door){

            final Block blockBelow = block.getLocation().add(0, -1 ,0).getBlock();
            final List<Block> bedListBelow = checkForBeds(blockBelow);
            if(bedListBelow.size()>0){

                for(int i = 0; i<bedListBelow.size(); i++){

                    Block bedBlock = bedListBelow.get(i);
                    this.lastClickedBlock.put(bedBlock.getLocation(), new String[]{bedBlock.getType().toString(), bedBlock.getBlockData().getAsString(true)});

                    bedBlock.setType(Material.AIR);

                    LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () ->   summonChairAfterUpdate(bedBlock, bedBlock.getFace(blockBelow)), 1L);

                }
            }

            final List<Block> bedListAbove = checkForBeds(blockAbove);
            if(bedListAbove.size()>0){

                for(int i = 0; i<bedListAbove.size(); i++){

                    Block bedBlock = bedListAbove.get(i);
                    this.lastClickedBlock.put(bedBlock.getLocation(), new String[]{bedBlock.getType().toString(), bedBlock.getBlockData().getAsString(true)});

                    bedBlock.setType(Material.AIR);

                    LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () ->   summonChairAfterUpdate(bedBlock, bedBlock.getFace(blockAbove)), 1L);

                }
            }


        }
    }



    private static List<Block> checkForBeds(Block block){
        Bed blockdata;
        final List<Block> blocklist= new ArrayList<>();
        Block adjacentBlock = block.getLocation().add(1, 0, 0).getBlock();
        if(adjacentBlock.getBlockData() instanceof Bed) {
            blockdata = (Bed) adjacentBlock.getBlockData();
            if(blockdata.getFacing()==BlockFace.WEST){
                blocklist.add(adjacentBlock);}

        }
        adjacentBlock = block.getLocation().add(-1, 0, 0).getBlock();
        if(adjacentBlock.getBlockData() instanceof Bed) {
            blockdata = (Bed) adjacentBlock.getBlockData();
            if(((Directional) blockdata).getFacing()==BlockFace.EAST){
                blocklist.add(adjacentBlock);}
        }
        adjacentBlock = block.getLocation().add(0, 0, 1).getBlock();
        if(adjacentBlock.getBlockData() instanceof Bed) {
            blockdata = (Bed) adjacentBlock.getBlockData();
            if(((Directional) blockdata).getFacing()==BlockFace.NORTH){
                blocklist.add(adjacentBlock);}
        }
        adjacentBlock = block.getLocation().add(0, 0, -1).getBlock();
        if(adjacentBlock.getBlockData() instanceof Bed) {
            blockdata = (Bed) adjacentBlock.getBlockData();
            if(((Directional) blockdata).getFacing()==BlockFace.SOUTH){
                blocklist.add(adjacentBlock);}

        }
        return blocklist;}
    private static List<Block> checkForBedsInverted(Block block){
        Bed blockdata;
        final List<Block> blocklist= new ArrayList<>();
        Block adjacentBlock = block.getLocation().add(1, 0, 0).getBlock();
        if(adjacentBlock.getBlockData() instanceof Bed) {
            blockdata = (Bed) adjacentBlock.getBlockData();
            if(blockdata.getFacing()==BlockFace.EAST){
                blocklist.add(adjacentBlock);}

        }
        adjacentBlock = block.getLocation().add(-1, 0, 0).getBlock();
        if(adjacentBlock.getBlockData() instanceof Bed) {
            blockdata = (Bed) adjacentBlock.getBlockData();
            if(((Directional) blockdata).getFacing()==BlockFace.WEST){
                blocklist.add(adjacentBlock);}
        }
        adjacentBlock = block.getLocation().add(0, 0, 1).getBlock();
        if(adjacentBlock.getBlockData() instanceof Bed) {
            blockdata = (Bed) adjacentBlock.getBlockData();
            if(((Directional) blockdata).getFacing()==BlockFace.SOUTH){
                blocklist.add(adjacentBlock);}
        }
        adjacentBlock = block.getLocation().add(0, 0, -1).getBlock();
        if(adjacentBlock.getBlockData() instanceof Bed) {
            blockdata = (Bed) adjacentBlock.getBlockData();
            if(((Directional) blockdata).getFacing()==BlockFace.NORTH){
                blocklist.add(adjacentBlock);}

        }
        return blocklist;}

    @EventHandler
    public void onBlockPhysicsUpdate(BlockPhysicsEvent event){
        final Block block = event.getBlock();

        if(this.lastBrokenBlock.containsKey(block.getLocation())){
            final String[] newBlockStrings = lastBrokenBlock.get(block.getLocation());
            final String savedBlockString = newBlockStrings[1];// get the saved block string from wherever you saved it
            final BlockData savedBlockData = Bukkit.createBlockData(savedBlockString);
            //if(!(savedBlockData instanceof Door)){lastBrokenBlock.remove(block.getLocation());}
            LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () -> lastBrokenBlock.remove(block.getLocation()), 3L);

            if(savedBlockData instanceof Bed){
                System.out.println("AH");
                final List<Block> bedList= checkForBeds(block);
                if(bedList.size()>0){
                    for(int i=0; i<bedList.size(); i++){
                        if(!isValidBedHelp((Bed) savedBlockData, (Bed) bedList.get(i).getBlockData())){return;}
                        final Integer finalI = i;
//                        bedList.get(i).setType(Material.AIR);
//                        LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () ->  summonChairAfterUpdate(bedList.get(finalI), bedList.get(finalI).getFace(block)), 1L);

                    }}
                if(((Bed) savedBlockData).getPart()==Bed.Part.HEAD){return;}
            }

            if(checkForBeds(block).size()>0){
                event.setCancelled(true);
            }
        }

        if(this.lastPlacedBlock.containsKey(block.getLocation())){
            final String[] newBlockStrings = lastPlacedBlock.get(block.getLocation());
            final String savedBlockString = newBlockStrings[1];// get the saved block string from wherever you saved it
            final BlockData savedBlockData = Bukkit.createBlockData(savedBlockString);
            //if(!(savedBlockData instanceof Door)){lastBrokenBlock.remove(block.getLocation());}
            LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () -> lastBrokenBlock.remove(block.getLocation()), 3L);
            if(checkForBeds(block).size()>0){
                event.setCancelled(true);
            }
        }

        if(this.lastClickedBlock.containsKey(block.getLocation())){
            final String[] newBlockStrings = lastClickedBlock.get(block.getLocation());
            final String savedBlockString = newBlockStrings[1];// get the saved block string from wherever you saved it
            final BlockData savedBlockData = Bukkit.createBlockData(savedBlockString);
            //if(!(savedBlockData instanceof Door)){lastBrokenBlock.remove(block.getLocation());}
            LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () -> lastBrokenBlock.remove(block.getLocation()), 3L);
            if(checkForBeds(block).size()>0){
                event.setCancelled(true);
            }
        }


    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        final Block block = event.getClickedBlock();
        addToBlockClickList(block);
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
