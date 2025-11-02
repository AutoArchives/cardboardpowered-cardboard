/**
 * CardboardPowered - Bukkit/Spigot for Fabric
 * Copyright (C) CardboardPowered.org and contributors
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either 
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.javazilla.bukkitfabric;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.inventory.EquipmentSlot;
import org.cardboardpowered.CardboardMod;
import org.cardboardpowered.impl.world.CraftWorld;
import org.cardboardpowered.interfaces.IMixinWorld;

import me.isaiah.common.cmixin.IMixinGlobalPos;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;

public class Utils {

	@Deprecated
    public static EquipmentSlot getHand(Hand h) {
        return CraftEquipmentSlot.getHand(h);
    }

    public static UUID getWorldUUID(File baseDir) {
        File file1 = new File(baseDir, "uid.dat");
        if (file1.exists()) {
            DataInputStream dis = null;
            try {
                dis = new DataInputStream(new FileInputStream(file1));
                return new UUID(dis.readLong(), dis.readLong());
            } catch (IOException ex) {
                CardboardMod.LOGGER.warning("Failed to read " + file1 + ", generating new random UUID. " + ex.getMessage());
            } finally { if (dis != null) try { dis.close(); } catch (IOException ex) {/*NOOP*/} }
        }
        UUID uuid = UUID.randomUUID();
        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(new FileOutputStream(file1));
            dos.writeLong(uuid.getMostSignificantBits());
            dos.writeLong(uuid.getLeastSignificantBits());
        } catch (IOException ex) {
            CardboardMod.LOGGER.warning("Failed to write " + file1.getAbsolutePath() + ", " + ex.getMessage());
        } finally { if (dos != null) try {dos.close();} catch (IOException ex) {/*NOOP*/} }
        return uuid;
    }

    @SuppressWarnings("unchecked")
    public static <T, U> MemoryModuleType<U> fromMemoryKey(MemoryKey<T> memoryKey) {
        return (MemoryModuleType<U>) Registries.MEMORY_MODULE_TYPE.get(CraftNamespacedKey.toMinecraft(memoryKey.getKey()));
    }

    public static <T, U> MemoryKey<?> toMemoryKey(MemoryModuleType<T> memoryModuleType) {
        return MemoryKey.getByKey(CraftNamespacedKey.fromMinecraft(Registries.MEMORY_MODULE_TYPE.getId(memoryModuleType)));
    }

    public static Object fromNmsGlobalPos(Object object) {
        if (object instanceof GlobalPos) return fromNmsGlobalPos((GlobalPos) object);
        else if (object instanceof Long) return object;
        else if (object instanceof UUID) return object;
        else if (object instanceof Boolean) return object;
        throw new UnsupportedOperationException("Do not know how to map " + object);
    }

    public static Object toNmsGlobalPos(Object object) {
        if (object == null) return null;
        else if (object instanceof Location) return toNmsGlobalPos((Location) object);
        else if (object instanceof Long)     return object;
        else if (object instanceof UUID)     return object;
        else if (object instanceof Boolean)  return object;
        throw new UnsupportedOperationException("Do not know how to map " + object);
    }

    @SuppressWarnings("unchecked")
	public static Location fromNmsGlobalPos(GlobalPos globalPos) {
    	
    	IMixinGlobalPos ipos = (IMixinGlobalPos) (Object) globalPos;
    	
        return new org.bukkit.Location(((IMixinWorld) Objects.requireNonNull(CraftServer.INSTANCE.getServer().getWorld((RegistryKey<World>) ipos.IC$get_dimension()))).getCraftWorld(), ipos.IC$get_pos().getX(), ipos.IC$get_pos().getY(), ipos.IC$get_pos().getZ());
    }

    public static GlobalPos toNmsGlobalPos(Location location) {
        return GlobalPos.create(((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle().getRegistryKey(), BlockPos.ofFloored(location.getX(), location.getY(), location.getZ()));
    }

}