package org.bukkit.craftbukkit.entity.memory;

import java.util.UUID;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.cardboardpowered.impl.world.WorldImpl;

import com.javazilla.bukkitfabric.interfaces.IMixinWorld;

import me.isaiah.common.cmixin.IMixinGlobalPos;

public final class CraftMemoryMapper {
    private CraftMemoryMapper() {
    }

    public static Object fromNms(Object object) {
        if (object instanceof GlobalPos) {
            return CraftMemoryMapper.fromNms((GlobalPos)object);
        }
        if (object instanceof Long) {
            return (Long)object;
        }
        if (object instanceof UUID) {
            return (UUID)object;
        }
        if (object instanceof Boolean) {
            return (Boolean)object;
        }
        if (object instanceof Integer) {
            return (Integer)object;
        }
        throw new UnsupportedOperationException("Do not know how to map " + object);
    }

    public static Object toNms(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Location) {
            return CraftMemoryMapper.toNms((Location)object);
        }
        if (object instanceof Long) {
            return (Long)object;
        }
        if (object instanceof UUID) {
            return (UUID)object;
        }
        if (object instanceof Boolean) {
            return (Boolean)object;
        }
        if (object instanceof Integer) {
            return (Integer)object;
        }
        throw new UnsupportedOperationException("Do not know how to map " + object);
    }

    @SuppressWarnings("unchecked")
	public static Location fromNms(GlobalPos globalPos) {
    	IMixinGlobalPos ipos = (IMixinGlobalPos) (Object) globalPos;
        return new Location((World) ((IMixinWorld)((CraftServer)Bukkit.getServer()).getServer().getWorld((RegistryKey<net.minecraft.world.World>) ipos.IC$get_dimension())).getWorldImpl(), (double)ipos.IC$get_pos().getX(), (double)ipos.IC$get_pos().getY(), (double)ipos.IC$get_pos().getZ());
    }

   // public static GlobalPos toNms(Location location) {
   //     return GlobalPos.create(((WorldImpl)location.getWorld()).getHandle().getRegistryKey(), BlockPos.ofFloored(location.getX(), location.getY(), location.getZ()));
   // }
}

