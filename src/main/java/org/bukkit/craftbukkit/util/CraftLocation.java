package org.bukkit.craftbukkit.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.World;
import org.cardboardpowered.impl.world.CraftWorld;

import org.cardboardpowered.bridge.server.MinecraftServerBridge;
import org.cardboardpowered.bridge.world.level.LevelBridge;

public final class CraftLocation {

    private CraftLocation() {
    }

    public static Location toBukkit(Vec3 vec3D) {
        return toBukkit(vec3D, null);
    }

    public static Location toBukkit(Vec3 vec3D, World world) {
        return toBukkit(vec3D, world, 0.0F, 0.0F);
    }

    public static Location toBukkit(Vec3 vec3D, World world, float yaw, float pitch) {
        return new Location(world, vec3D.x(), vec3D.y(), vec3D.z(), yaw, pitch);
    }
    
    public static Location toBukkit(Vec3 vec3D, net.minecraft.world.level.Level world, float yaw, float pitch) {
        return new Location( ((LevelBridge) world).getCraftWorld(), vec3D.x(), vec3D.y(), vec3D.z(), yaw, pitch);
    }

    public static Location toBukkit(BlockPos BlockPos) {
        return toBukkit(BlockPos,  (World) null);
    }

    public static Location toBukkit(BlockPos blockPosition, net.minecraft.world.level.Level world) {
        return toBukkit(blockPosition, ((LevelBridge) world).getCraftWorld(), 0.0F, 0.0F);
    }

    public static Location toBukkit(BlockPos BlockPos, World world) {
        return toBukkit(BlockPos, world, 0.0F, 0.0F);
    }

    public static Location toBukkit(BlockPos BlockPos, World world, float yaw, float pitch) {
        return new Location(world, BlockPos.getX(), BlockPos.getY(), BlockPos.getZ(), yaw, pitch);
    }
    
    public static Location toBukkit(BlockPos BlockPos, ServerLevel world, float yaw, float pitch) {
        return new Location(((LevelBridge) world).getCraftWorld(), BlockPos.getX(), BlockPos.getY(), BlockPos.getZ(), yaw, pitch);
    }

    /*public static Location toBukkit(PositionImpl position) {
        return toBukkit(position, null, 0.0F, 0.0F);
    }

    public static Location toBukkit(PositionImpl position, World world) {
        return toBukkit(position, world, 0.0F, 0.0F);
    }

    public static Location toBukkit(PositionImpl position, World world, float yaw, float pitch) {
        return new Location(world, position.getX(), position.getY(), position.getZ(), yaw, pitch);
    }*/

    public static BlockPos toBlockPosition(Location location) {
        return new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

   // public static PositionImpl toPosition(Location location) {
    //    return new PositionImpl(location.getX(), location.getY(), location.getZ());
    //}
    
    // public static PositionImpl toPosition(Location location) {
    //    return new PositionImpl(location.getX(), location.getY(), location.getZ());
    //}
    
    public static GlobalPos toGlobalPos(Location location) {
        return GlobalPos.of(((CraftWorld)location.getWorld()).getHandle().dimension(), CraftLocation.toBlockPosition(location));
    }

    public static Location fromGlobalPos(GlobalPos globalPos) {
        BlockPos pos = globalPos.pos();
        return new Location((World) MinecraftServerBridge.getServer().getLevel(globalPos.dimension()).getWorld(), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
    }


    public static Vec3 toVec3D(Location location) {
        return new Vec3(location.getX(), location.getY(), location.getZ());
    }

	public static Vec3 toVec3(Location loc) {
		return toVec3D(loc);
	}

	public static Location toBukkit(Node node, net.minecraft.world.level.Level world) {
		return new Location(((LevelBridge) world).getCraftWorld(), node.x, node.y, node.z);
	}

}
