package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import com.mojang.logging.LogUtils;

import org.cardboardpowered.interfaces.IMixinEntity;
import org.slf4j.Logger;

import java.util.function.Function;

import net.minecraft.entity.LoadedEntityProcessor;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.util.ErrorReporter;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.cardboardpowered.impl.world.CraftWorld;

public class CraftEntitySnapshot implements EntitySnapshot {

	private static final Logger LOGGER = LogUtils.getLogger();
	
    private final NbtCompound data;
    private final EntityType type;

    private CraftEntitySnapshot(NbtCompound data, EntityType type) {
        this.data = data;
        this.type = type;
    }

    @Override
    public EntityType getEntityType() {
        return this.type;
    }

    @Override
    public Entity createEntity(World world) {
        net.minecraft.entity.Entity internal = this.createInternal(world);

        return ((IMixinEntity)internal).getBukkitEntity();
    }

    @Override
    public Entity createEntity(Location location) {
        Preconditions.checkArgument(location.getWorld() != null, "Location has no world");

        net.minecraft.entity.Entity internal = this.createInternal(location.getWorld());

        internal.setPosition(location.getX(), location.getY(), location.getZ());
        return location.getWorld().addEntity(((IMixinEntity)internal).getBukkitEntity());
    }

    // @Override
    public String getAsString() {
        return this.data.toString();
    }
    
	// TODO: Move to NbtReadView
    public static ReadView NbtReadView_createGlobal(ErrorReporter problemReporter, NbtCompound compoundTag) {
        return NbtReadView.create(problemReporter, CraftServer.server.getRegistryManager(), compoundTag);
    }

    private net.minecraft.entity.Entity createInternal(World world) {
        net.minecraft.world.World nms = ((CraftWorld) world).getHandle();
        net.minecraft.entity.Entity internal = net.minecraft.entity.EntityType.loadEntityWithPassengers(this.data, nms, net.minecraft.entity.SpawnReason.LOAD, LoadedEntityProcessor.NOOP);
        if (internal == null) { // Try creating by type
            internal = CraftEntityType.bukkitToMinecraft(this.type).create(nms, net.minecraft.entity.SpawnReason.LOAD);
        }

        Preconditions.checkArgument(internal != null, "Error creating new entity."); // This should only fail if the stored NBTTagCompound is malformed.

        try (ErrorReporter.Logging problemReporter = new ErrorReporter.Logging(() -> "EntitySnapshot#createEntity", LOGGER);){
            internal.readData(NbtReadView_createGlobal(problemReporter, this.data));
        }

        return internal;
    }

    public NbtCompound getData() {
        return this.data;
    }

    /*
    public static CraftEntitySnapshot create(CraftEntity entity) {
        NbtCompound tag = new NbtCompound();
        if (!entity.getHandle().saveSelfNbt(tag)) {
            return null;
        }

        return new CraftEntitySnapshot(tag, entity.getType());
    }
    */
    
    public static CraftEntitySnapshot create(CraftEntity entity) {
        try (ErrorReporter.Logging problemReporter = new ErrorReporter.Logging(() -> "create@" + String.valueOf(entity.getUniqueId()), LOGGER);){
            NbtWriteView output = NbtWriteView.create(problemReporter, CraftRegistry.getMinecraftRegistry());
            
            // TODO: if (!entity.getHandle().saveAsPassenger(output, false, false, false)) {
            
            if (!entity.getHandle().saveSelfData(output)) {
                CraftEntitySnapshot craftEntitySnapshot = null;
                return craftEntitySnapshot;
            }
            CraftEntitySnapshot craftEntitySnapshot = new CraftEntitySnapshot(output.getNbt(), entity.getType());
            return craftEntitySnapshot;
        }
    }

    public static CraftEntitySnapshot create(NbtCompound tag, EntityType type) {
        if (tag == null || tag.isEmpty() || type == null) {
            return null;
        }

        return new CraftEntitySnapshot(tag, type);
    }
    
    public static CraftEntitySnapshot create(NbtCompound tag) {
        try (ErrorReporter.Logging problemReporter = new ErrorReporter.Logging(() -> "create", LOGGER);){
            EntityType type = net.minecraft.entity.EntityType.fromData(NbtReadView_createGlobal(problemReporter, tag)).map(CraftEntityType::minecraftToBukkit).orElse(null);
            CraftEntitySnapshot craftEntitySnapshot = CraftEntitySnapshot.create(tag, type);
            return craftEntitySnapshot;
        }
    }

}