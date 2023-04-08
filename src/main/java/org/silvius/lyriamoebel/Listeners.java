package org.silvius.lyriamoebel;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.PistonHead;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Listeners implements Listener {
    private final LyriaMoebel main;
    private static final ArrayList<UUID> cooldownPlace = new ArrayList<>();
    private static final ArrayList<Location> cooldownDrop = new ArrayList<>();
    private static final ArrayList<Location> cooldownReplace = new ArrayList<>();
    private static final ArrayList<Location> newlyPlacedBed = new ArrayList<>();

    private static final ArrayList<Location> droppedBed = new ArrayList<>();
    private static final ArrayList<Location> droppedDoor = new ArrayList<>();

    private static final ArrayList<Location> usedBed = new ArrayList<>();

    private static final ArrayList<Location> doorChanged = new ArrayList<>();




    private static List<String> locationListChair = null;
    private static List<String> faceListChair = null;
    private static List<String> locationListDoor = null;
    private static List<String> faceListDoor = null;
    private static List<String> isOpenListDoor = null;
    private static List<String> powerLevelListDoor = null;
    private static List<String> hingeListDoor = null;




    public Listeners(LyriaMoebel main) {
        this.main=main;
        locationListChair = main.getLocationsConfig().getStringList("LocationsStuhl");
        faceListChair = main.getLocationsConfig().getStringList("FacesStuhl");
        locationListDoor = main.getLocationsConfig().getStringList("LocationsTür");
        faceListDoor = main.getLocationsConfig().getStringList("FacesTür");
        isOpenListDoor = main.getLocationsConfig().getStringList("OpenTür");
        powerLevelListDoor = main.getLocationsConfig().getStringList("PowersTür");
        hingeListDoor = main.getLocationsConfig().getStringList("HingesTür");





    }

    private static void summonChairAfterUpdate(Block block, BlockFace face){
        block.setType(Material.WHITE_BED);
        final BlockData blockData = block.getBlockData();
        if(!(blockData instanceof Directional)){return;}
        ((Bed) blockData).setFacing(face);
        block.setBlockData(blockData);
        final TileState meta = (TileState) block.getState();

        final PersistentDataContainer data = meta.getPersistentDataContainer();
        final NamespacedKey namespacedKey = new NamespacedKey(LyriaMoebel.getPlugin(), "isMoebel");
        data.set(namespacedKey, PersistentDataType.STRING, "Stuhl");
        meta.update();
    }



    private Location getIntersec(Player player){
        final int maxDistance=100;
        final Location loc = player.getEyeLocation();

        final Vector v = loc.getDirection().normalize().multiply(0.1);

        for(int i = 1 ; i <= maxDistance ; i++) {
            loc.add(v);
            if(loc.getBlock().getType() != Material.AIR)
                return loc;
        }
        return null;
    }
    private void summonMoebel(Player player, Material material, Block block, ItemStack item){
        //Block über dem angeklickten muss Luft sein, sonst return

        if(!cooldownPlace.contains(player.getUniqueId())){
            cooldownPlace.add(player.getUniqueId());

            LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () -> cooldownPlace.remove(player.getUniqueId()), 3L);
            if(block.getType()!=Material.AIR){return;}
            block.setType(material);

            item.setAmount(item.getAmount()-1);
            if(material==Material.PISTON_HEAD){
                final BlockData blockData = block.getBlockData();
                ((Directional) blockData).setFacing(BlockFace.UP);
                block.setBlockData(blockData);

            }
            if(material==Material.OAK_DOOR){
                final BlockData blockData = block.getBlockData();

                final int blockPower = block.getBlockPower();

                final Location intersection = getIntersec(player);
                assert intersection != null;

                Door.Hinge hinge = Door.Hinge.RIGHT;

                    if(player.getFacing()==BlockFace.WEST){
                        double dist = Math.abs(intersection.getZ()-block.getLocation().getZ());
                    if(dist>0.5){
                        hinge = Door.Hinge.LEFT;}
                    else{
                        hinge = Door.Hinge.RIGHT;}
                }

                    if(player.getFacing()==BlockFace.EAST){
                        double dist = Math.abs(intersection.getZ()-block.getLocation().getZ());
                        if(dist<0.5){
                            hinge = Door.Hinge.LEFT;}
                        else{
                            hinge = Door.Hinge.RIGHT;}
                    }

                    if(player.getFacing()==BlockFace.SOUTH){
                        double dist = Math.abs(intersection.getX()-block.getLocation().getX());
                        if(dist>0.5){
                            hinge = Door.Hinge.LEFT;}
                        else{
                            hinge = Door.Hinge.RIGHT;}
                    }

                    if(player.getFacing()==BlockFace.NORTH){
                        double dist = Math.abs(intersection.getX()-block.getLocation().getX());
                        if(dist<0.5){
                            hinge = Door.Hinge.LEFT;}
                        else{
                            hinge = Door.Hinge.RIGHT;}
                    }
                Door.Hinge finalHinge = hinge;

                    locationListDoor.add(block.getLocation().toString());
                faceListDoor.add(player.getFacing().toString());
                    hingeListDoor.add(finalHinge.toString());

                    if(blockPower==0){ isOpenListDoor.add("false");}
                else{isOpenListDoor.add("true");}

                powerLevelListDoor.add(String.valueOf(blockPower));
                LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () -> {

                    main.getLocationsConfig().set("LocationsTür", locationListDoor);
                main.getLocationsConfig().set("FacesTür", faceListDoor);
                main.getLocationsConfig().set("OpenTür", isOpenListDoor);
                main.getLocationsConfig().set("PowersTür", powerLevelListDoor);
                main.getLocationsConfig().set("HingesTür", hingeListDoor);


                    main.saveLocationFile();

                }, 1L);
                ((Door) blockData).setHinge(hinge);
                ((Door) blockData).setHalf(Bisected.Half.TOP);
                ((Directional) blockData).setFacing(player.getFacing());
                block.setBlockData(blockData);






            }
            if(material==Material.WHITE_BED){
                locationListChair.add(block.getLocation().toString());
                faceListChair.add(player.getFacing().toString());
                main.getLocationsConfig().set("LocationsStuhl", locationListChair);
                main.getLocationsConfig().set("FacesStuhl", faceListChair);

                main.saveLocationFile();

                final BlockData blockData = block.getBlockData();
                ((Bed) blockData).setFacing(player.getFacing());
                block.setBlockData(blockData);




            }
        }}
    private static void dropMoebelItem(Location location, String s){
        if(!cooldownDrop.contains(location)){
            cooldownDrop.add(location);
            LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () -> cooldownDrop.remove(location), 1L);
            location.getWorld().dropItemNaturally(location, MoebelCommand.generateItem(s, 1));

        }}

    private static boolean isValidBedHelp(Bed blockdata1, Bed blockdata2){
        if(blockdata1.getPart()== Bed.Part.FOOT && blockdata2.getPart()== Bed.Part.HEAD && blockdata1.getFacing()== blockdata2.getFacing()){
            return false;
        }
        return blockdata2.getPart() != Bed.Part.FOOT || blockdata1.getPart() != Bed.Part.HEAD || blockdata1.getFacing() != blockdata2.getFacing();
    }
    private static boolean isValidBed(Location location, Bed blockdata1){
        Block adjacentBlock = location.add(1, 0, 0).getBlock();
        Bed blockdata2;
        if(adjacentBlock.getBlockData() instanceof Bed) {
            blockdata2 = (Bed) adjacentBlock.getBlockData();
            if(!isValidBedHelp(blockdata1, blockdata2)){return false;}
        }
        adjacentBlock = location.add(-2, 0, 0).getBlock();
        if(adjacentBlock.getBlockData() instanceof Bed) {
            blockdata2 = (Bed) adjacentBlock.getBlockData();

            if(!isValidBedHelp(blockdata1, blockdata2)){return false;}
        }
        adjacentBlock = location.add(1, 0, 1).getBlock();
        if(adjacentBlock.getBlockData() instanceof Bed) {
            blockdata2 = (Bed) adjacentBlock.getBlockData();

            if(!isValidBedHelp(blockdata1, blockdata2)){return false;}
        }
        adjacentBlock = location.add(0, 0, -2).getBlock();
        if(adjacentBlock.getBlockData() instanceof Bed) {
            blockdata2 = (Bed) adjacentBlock.getBlockData();

            return isValidBedHelp(blockdata1, blockdata2);
        }

        return true;
    }

    private static boolean isValidPistonHead(Block block){
        final Location location = block.getLocation();
        final BlockData blockdata = block.getBlockData();

        if(location.add(0, -1, 0).getBlock().getType()==Material.PISTON) {return false;}
        return ((Directional) blockdata).getFacing() == BlockFace.UP;
    }
    private static boolean isValidTuer(Block block){
        final Location location = block.getLocation();

        if(location.add(0, -1, 0).getBlock().getType()==Material.OAK_DOOR) {return false;}
        return location.add(0, 2, 0).getBlock().getType() != Material.OAK_DOOR;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        final Block block = event.getBlock();

        final Block blockAbove = block.getLocation().add(0, 1 ,0).getBlock();

        if(locationListChair.contains(block.getLocation().toString())){
            faceListChair.remove(locationListChair.indexOf(block.getLocation().toString()));
            locationListChair.remove(block.getLocation().toString());
            main.getLocationsConfig().set("Locations", locationListChair);
            main.getLocationsConfig().set("Faces", faceListChair);
            main.saveLocationFile();

            dropMoebelItem(event.getBlock().getLocation(), "Stuhl");
        }

        if(locationListDoor.contains(block.getLocation().toString())){
            faceListDoor.remove(locationListDoor.indexOf(block.getLocation().toString()));
            isOpenListDoor.remove(locationListDoor.indexOf(block.getLocation().toString()));
            powerLevelListDoor.remove(locationListDoor.indexOf(block.getLocation().toString()));
            hingeListDoor.remove(locationListDoor.indexOf(block.getLocation().toString()));

            locationListDoor.remove(block.getLocation().toString());

            main.getLocationsConfig().set("OpenTür", isOpenListDoor);
            main.getLocationsConfig().set("PowersTür", powerLevelListDoor);
            main.getLocationsConfig().set("LocationsTür", locationListDoor);
            main.getLocationsConfig().set("FacesTür", faceListDoor);
            main.getLocationsConfig().set("HingesTür", hingeListDoor);

            main.saveLocationFile();

            dropMoebelItem(event.getBlock().getLocation(), "Tür");
        }

        if(block.getType()==Material.PISTON_HEAD){
            if(!isValidPistonHead(block)){return;}
            dropMoebelItem(block.getLocation(), "Tisch");
        }
        if(blockAbove.getType()==Material.PISTON_HEAD){
            if(!isValidPistonHead(blockAbove)){return;}
            dropMoebelItem(blockAbove.getLocation(), "Tisch");
        }
        if(block.getType()==Material.OAK_DOOR){
            if(!isValidTuer(block)){return;}

            dropMoebelItem(block.getLocation(), "Tür");
            event.setCancelled(true);
            block.setType(Material.AIR);

        }



        if(locationListDoor.contains(blockAbove.getLocation().toString())){
            blockAbove.setType(Material.AIR);
            faceListDoor.remove(locationListDoor.indexOf(blockAbove.getLocation().toString()));
            isOpenListDoor.remove(locationListDoor.indexOf(blockAbove.getLocation().toString()));
            powerLevelListDoor.remove(locationListDoor.indexOf(blockAbove.getLocation().toString()));
            hingeListDoor.remove(locationListDoor.indexOf(blockAbove.getLocation().toString()));

            locationListDoor.remove(blockAbove.getLocation().toString());

            main.getLocationsConfig().set("OpenTür", isOpenListDoor);
            main.getLocationsConfig().set("PowersTür", powerLevelListDoor);
            main.getLocationsConfig().set("LocationsTür", locationListDoor);
            main.getLocationsConfig().set("FacesTür", faceListDoor);
            main.getLocationsConfig().set("HingesTür", hingeListDoor);
            main.saveLocationFile();

            dropMoebelItem(blockAbove.getLocation(), "Tür");

        }
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        final Block block = event.getBlockPlaced();

        if(block.getType() != Material.WHITE_BED){
            return;}
        final Block otherBedBlock = event.getBlock().getRelative(event.getPlayer().getFacing());
        newlyPlacedBed.add(otherBedBlock.getLocation());
        otherBedBlock.setType(Material.WHITE_BED);
        Bed blockData = (Bed) block.getBlockData();
        blockData.setFacing(event.getPlayer().getFacing().getOppositeFace());
        blockData.setPart(Bed.Part.HEAD);
        block.setBlockData(blockData);
        Bed otherblockData = (Bed) otherBedBlock.getBlockData();
        otherblockData.setFacing(event.getPlayer().getFacing());
        otherblockData.setPart(Bed.Part.HEAD);
        otherBedBlock.setBlockData(otherblockData);




    }


    @EventHandler
    public void onRightClick(PlayerInteractEvent event){
        final Player player = event.getPlayer();
        final ItemStack item = event.getItem();
        final Block block = event.getClickedBlock();
        if(!event.getAction().isRightClick()){return;}
        if(block==null){return;}
        if(locationListDoor.contains(block.getLocation().toString())){
            if(player.isSneaking()){return;}
            if(!cooldownPlace.contains(player.getUniqueId())){
            isOpenListDoor.set(locationListDoor.indexOf(block.getLocation().toString()), String.valueOf(!Boolean.parseBoolean(isOpenListDoor.get(locationListDoor.indexOf(block.getLocation().toString())))));
            main.getLocationsConfig().set("OpenTür", isOpenListDoor);
            main.saveLocationFile();
                }
        }
        if(item==null){return;}

        if(!item.hasItemMeta()){return;}
        if(event.getBlockFace()!=BlockFace.UP){return;}
        final ItemMeta meta = item.getItemMeta();
        if(!meta.hasLore()){return;}
        if(meta.lore().toString().contains("(CIT) Stuhl")){
            //Spawne Stuhl
            event.getPlayer().swingMainHand();
            summonMoebel(player, Material.WHITE_BED, block.getLocation().add(0, 1, 0).getBlock(), event.getItem());
        }

        if(meta.lore().toString().contains("(CIT) Tisch")){
            //Spawne Tisch
            event.getPlayer().swingMainHand();
            summonMoebel(player, Material.PISTON_HEAD, block.getLocation().add(0, 1, 0).getBlock(), event.getItem());

        }
        if(meta.lore().toString().contains("(CIT) Tür")){
            if(!block.getLocation().add(0, 1, 0).getBlock().canPlace(Bukkit.createBlockData(Material.OAK_DOOR))){return;}
            //Spawne Tisch
            event.getPlayer().swingMainHand();
            summonMoebel(player, Material.OAK_DOOR, block.getLocation().add(0, 1, 0).getBlock(), event.getItem());

        }



    }
    @EventHandler
    public static void onPlayerBedEnter(PlayerInteractEvent event){

        final Block block = event.getClickedBlock();
        if(block==null){return;}
        if(event.getAction().isLeftClick()){return;}
        if(block.getType()==Material.WHITE_BED && ((Bed) block.getBlockData()).getPart()== Bed.Part.HEAD){
            usedBed.add(block.getLocation());
            return;}
        if(block.getType()==Material.WHITE_BED && isValidBed(block.getLocation(), (Bed) block.getBlockData()) && event.getAction().isRightClick()){
            event.setCancelled(true);
        }
    }
    @EventHandler public static void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        Block block = event.getBed();
        if(block.getType()==Material.WHITE_BED && ((Bed) block.getBlockData()).getPart()== Bed.Part.HEAD){
            usedBed.add(block.getLocation());}
    }

    @EventHandler
    public void onPistonPush(BlockPistonExtendEvent event){
        List<Block> blockList = event.getBlocks();
        for (Block value : blockList) {
            Location location = value.getLocation();
            if (locationListDoor.contains(location.toString()) || locationListChair.contains(location.toString())) {
                event.setCancelled(true);
            }
            Block block = location.add(0, 1, 0).getBlock();
            onPistonEvent(block);
        }
    }
    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event){
        List<Block> blockList = event.getBlocks();
        for (Block value : blockList) {
            Location location = value.getLocation();
            if (locationListDoor.contains(location.toString()) || locationListChair.contains(location.toString())) {
                event.setCancelled(true);
            }
            Block block = location.add(0, 1, 0).getBlock();

            onPistonEvent(block);
    }}

    public void onPistonEvent(Block block){


            if (locationListDoor.contains(block.getLocation().toString())) {
                faceListDoor.remove(locationListDoor.indexOf(block.getLocation().toString()));
                isOpenListDoor.remove(locationListDoor.indexOf(block.getLocation().toString()));
                powerLevelListDoor.remove(locationListDoor.indexOf(block.getLocation().toString()));
                hingeListDoor.remove(locationListDoor.indexOf(block.getLocation().toString()));

                locationListDoor.remove(block.getLocation().toString());

                main.getLocationsConfig().set("OpenTür", isOpenListDoor);
                main.getLocationsConfig().set("PowersTür", powerLevelListDoor);
                main.getLocationsConfig().set("LocationsTür", locationListDoor);
                main.getLocationsConfig().set("FacesTür", faceListDoor);
                main.getLocationsConfig().set("HingesTür", hingeListDoor);

                main.saveLocationFile();

                dropMoebelItem(block.getLocation(), "Tür");
            }

            if(block.getBlockData() instanceof PistonHead && isValidPistonHead(block)){
                dropMoebelItem(block.getLocation(), "Tisch");
            }
        }


    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event){
        Item item = event.getEntity();
        if(!item.getName().equals("White Bed")){return;}
        for (Location location : droppedBed) {
            if (Math.abs(event.getLocation().getX() - location.getX()) - 1 < 0.2) {
                event.setCancelled(true);
                return;
            }
            if (Math.abs(event.getLocation().getZ() - location.getZ()) - 1 < 0.2) {
                event.setCancelled(true);
                return;
            }
        }
        droppedBed.add(event.getLocation());
        LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () -> droppedBed.remove(event.getLocation()), 1L);

    }

    @EventHandler
    public void onDoorSpawn(ItemSpawnEvent event){
        Item item = event.getEntity();
        if(!item.getName().equals("Oak Door")){return;}
        LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () -> {


            for (Location location : droppedDoor) {
                if (Math.abs(Math.abs(event.getLocation().getY() - location.getY()) - 0.5) < 0.5 && item.getName().equals("Oak Door")) {
                    item.remove();
                    return;
                }
            }
        }, 1L);

        for (String locString : locationListDoor) {
            String[] parts = locString.split("[{,}=]");
            World world = Bukkit.getWorld(parts[4]);
            double x = Double.parseDouble(parts[7]);
            double y = Double.parseDouble(parts[9]);
            double z = Double.parseDouble(parts[11]);
            float pitch = Float.parseFloat(parts[13]);
            float yaw = Float.parseFloat(parts[15]);
            Location location = new Location(world, x, y, z, yaw, pitch);
            LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () -> {

                if (doorChanged.contains(location) && location.getBlock().getBoundingBox().expand(1).contains(event.getLocation().toVector())) {
                    item.remove();
                }
            }, 1L);
        }

    }

    @EventHandler
    public void onBlockPhysicsUpdate(BlockPhysicsEvent event){
        final Block block = event.getBlock();


        if(locationListChair.contains(block.getLocation().toString())){
            event.setCancelled(true);

            if(!cooldownReplace.contains(block.getLocation())){

                cooldownReplace.add(block.getLocation());
                LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () -> cooldownReplace.remove(block.getLocation()), 1L);

                block.setType(Material.AIR);
                LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () -> summonChairAfterUpdate(block, BlockFace.valueOf(faceListChair.get(locationListChair.indexOf(block.getLocation().toString())))), 0L);
        }}


        if(locationListDoor.contains(block.getLocation().toString())){
            doorChanged.add(block.getLocation());
            LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () ->
                    doorChanged.remove(block.getLocation()), 2L);
            if(!(event.getChangedBlockData() instanceof Door)){return;}
            final Door blockdata = ((Door) event.getChangedBlockData());
            blockdata.setHalf(Bisected.Half.BOTTOM);
            if(!block.canPlace(blockdata)){
                main.broadcast("Hae");
                block.setType(Material.AIR);
                droppedDoor.add(block.getLocation());
                LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () -> droppedDoor.remove(block.getLocation()), 1L);

                dropMoebelItem(block.getLocation(), "Tür");
                faceListDoor.remove(locationListDoor.indexOf(block.getLocation().toString()));
                isOpenListDoor.remove(locationListDoor.indexOf(block.getLocation().toString()));
                powerLevelListDoor.remove(locationListDoor.indexOf(block.getLocation().toString()));
                hingeListDoor.remove(locationListDoor.indexOf(block.getLocation().toString()));

                locationListDoor.remove(block.getLocation().toString());

                main.getLocationsConfig().set("OpenTür", isOpenListDoor);
                main.getLocationsConfig().set("PowersTür", powerLevelListDoor);
                main.getLocationsConfig().set("LocationsTür", locationListDoor);
                main.getLocationsConfig().set("FacesTür", faceListDoor);
                main.getLocationsConfig().set("HingesTür", hingeListDoor);

                main.saveLocationFile();
                event.setCancelled(true);
            }

        }

        if(newlyPlacedBed.contains(block.getLocation())){
            LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () -> newlyPlacedBed.remove(block.getLocation()), 1L);

            event.setCancelled(true);
        return;}

        if(usedBed.contains(block.getLocation())){
            LyriaMoebel.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(LyriaMoebel.getPlugin(), () -> usedBed.remove(block.getLocation()), 1L);

            event.setCancelled(true);
            }
//
    }
}
