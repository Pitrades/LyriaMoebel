package org.silvius.lyriamoebel;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.type.Flags;
import me.angeschossen.lands.api.land.LandWorld;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PermissionChecks {
    final LandsIntegration api = LandsIntegration.of(LyriaMoebel.getPlugin());

    protected static boolean hasBreakPermission(Player player, Block block) {
        final LandWorld world = api.getWorld(player.getWorld());
        final Location location = block.getLocation();
        final Material material = block.getType();
        if (world == null) {
            return false;
        }

        final LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        final RegionManager manager = container.get(BukkitAdapter.adapt(location.getWorld()));
        assert manager != null;
        final ApplicableRegionSet set = manager.getApplicableRegions(BukkitAdapter.asBlockVector(location));

        return (world.hasRoleFlag(player, location, Flags.BLOCK_BREAK, material, false) && set.testState(localPlayer, com.sk89q.worldguard.protection.flags.Flags.BUILD));
    }

    protected static boolean hasPlacePermission(Player player, Block block) {
        final LandWorld world = api.getWorld(player.getWorld());
        final Location location = block.getLocation();
        final Material material = block.getType();
        if (world == null) {
            return false;
        }

        final LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        final RegionManager manager = container.get(BukkitAdapter.adapt(location.getWorld()));
        assert manager != null;
        final ApplicableRegionSet set = manager.getApplicableRegions(BukkitAdapter.asBlockVector(location));

        return (world.hasRoleFlag(player, location, Flags.BLOCK_PLACE, material, false) && set.testState(localPlayer, com.sk89q.worldguard.protection.flags.Flags.BUILD));
    }

    protected static boolean hasInteractPermission(Player player, Block block) {
        LandWorld world = api.getWorld(player.getWorld());
        final Location location = block.getLocation();
        final Material material = block.getType();
        if (world == null) {
            return false;
        }

        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager manager = container.get(BukkitAdapter.adapt(location.getWorld()));
        assert manager != null;
        ApplicableRegionSet set = manager.getApplicableRegions(BukkitAdapter.asBlockVector(location));


        return (world.hasRoleFlag(player, location, Flags.INTERACT_DOOR, material, false) && !set.testState(localPlayer, com.sk89q.worldguard.protection.flags.Flags.INTERACT));
    }
}
