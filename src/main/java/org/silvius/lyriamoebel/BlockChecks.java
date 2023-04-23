package org.silvius.lyriamoebel;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Bed;

public class BlockChecks {
    private static boolean isValidBedHelp(Bed blockdata1, Bed blockdata2) {
        if (blockdata1.getPart() == Bed.Part.FOOT && blockdata2.getPart() == Bed.Part.HEAD && blockdata1.getFacing() == blockdata2.getFacing()) {
            return false;
        }
        return blockdata2.getPart() != Bed.Part.FOOT || blockdata1.getPart() != Bed.Part.HEAD || blockdata1.getFacing() != blockdata2.getFacing();
    }

    protected static boolean isValidBed(Location location, Bed blockdata1) {
        Block adjacentBlock = location.add(1, 0, 0).getBlock();
        Bed blockdata2;
        if (adjacentBlock.getBlockData() instanceof Bed) {
            blockdata2 = (Bed) adjacentBlock.getBlockData();
            if (!BlockChecks.isValidBedHelp(blockdata1, blockdata2)) {
                return false;
            }
        }
        adjacentBlock = location.add(-2, 0, 0).getBlock();
        if (adjacentBlock.getBlockData() instanceof Bed) {
            blockdata2 = (Bed) adjacentBlock.getBlockData();

            if (!BlockChecks.isValidBedHelp(blockdata1, blockdata2)) {
                return false;
            }
        }
        adjacentBlock = location.add(1, 0, 1).getBlock();
        if (adjacentBlock.getBlockData() instanceof Bed) {
            blockdata2 = (Bed) adjacentBlock.getBlockData();

            if (!BlockChecks.isValidBedHelp(blockdata1, blockdata2)) {
                return false;
            }
        }
        adjacentBlock = location.add(0, 0, -2).getBlock();
        if (adjacentBlock.getBlockData() instanceof Bed) {
            blockdata2 = (Bed) adjacentBlock.getBlockData();

            return BlockChecks.isValidBedHelp(blockdata1, blockdata2);
        }

        return true;
    }

    protected static boolean isValidPistonHead(Block block) {
        final Location location = block.getLocation();
        final BlockData blockdata = block.getBlockData();

        if (location.add(0, -1, 0).getBlock().getType() == Material.PISTON) {
            return false;
        }
        return ((Directional) blockdata).getFacing() == BlockFace.UP;
    }

    protected static boolean isValidTuer(Block block) {
        final Location location = block.getLocation();

        if (location.add(0, -1, 0).getBlock().getType() == Material.OAK_DOOR) {
            return false;
        }
        return location.add(0, 2, 0).getBlock().getType() != Material.OAK_DOOR;
    }
}
