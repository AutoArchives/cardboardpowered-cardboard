package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import com.mojang.logging.LogUtils;

import org.cardboardpowered.bridge.world.entity.EntityBridge;
import org.slf4j.Logger;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntityProcessor;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
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
	
    private final CompoundTag data;
    private final EntityType type;

    private CraftEntitySnapshot(CompoundTag data, EntityType type) {
        this.data = data;
        this.type = type;
    }

    @Override
    public EntityType getEntityType() {
        return this.type;
    }

    @Override
    public Entity createEntity(World world) {
        net.minecraft.world.entity.Entity internal = this.createInternal(world);

        return ((EntityBridge)internal).getBukkitEntity();
    }

    @Override
    public Entity createEntity(Location location) {
        Preconditions.checkArgument(location.getWorld() != null, "Location has no world");

        net.minecraft.world.entity.Entity internal = this.createInternal(location.getWorld());

        internal.setPos(location.getX(), location.getY(), location.getZ());
        return location.getWorld().addEntity(((EntityBridge)internal).getBukkitEntity());
    }

    // @Override
    public String getAsString() {
        return this.data.toString();
    }
    
	// TODO: Move to NbtReadView
    public static ValueInput NbtReadView_createGlobal(ProblemReporter problemReporter, CompoundTag compoundTag) {
        return TagValueInput.create(problemReporter, CraftServer.server.registryAccess(), compoundTag);
    }

    private net.minecraft.world.entity.Entity createInternal(World world) {
        net.minecraft.world.level.Level nms = ((CraftWorld) world).getHandle();
        net.minecraft.world.entity.Entity internal = net.minecraft.world.entity.EntityType.loadEntityRecursive(this.data, nms, net.minecraft.world.entity.EntitySpawnReason.LOAD, EntityProcessor.NOP);
        if (internal == null) { // Try creating by type
            internal = CraftEntityType.bukkitToMinecraft(this.type).create(nms, net.minecraft.world.entity.EntitySpawnReason.LOAD);
        }

        Preconditions.checkArgument(internal != null, "Error creating new entity."); // This should only fail if the stored NBTTagCompound is malformed.

        try (ProblemReporter.ScopedCollector problemReporter = new ProblemReporter.ScopedCollector(() -> "EntitySnapshot#createEntity", LOGGER);){
            internal.load(NbtReadView_createGlobal(problemReporter, this.data));
        }

        return internal;
    }

    public CompoundTag getData() {
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
        try (ProblemReporter.ScopedCollector problemReporter = new ProblemReporter.ScopedCollector(() -> "create@" + String.valueOf(entity.getUniqueId()), LOGGER);){
            TagValueOutput output = TagValueOutput.createWithContext(problemReporter, CraftRegistry.getMinecraftRegistry());
            
            // TODO: if (!entity.getHandle().saveAsPassenger(output, false, false, false)) {
            
            if (!entity.getHandle().saveAsPassenger(output)) {
                CraftEntitySnapshot craftEntitySnapshot = null;
                return craftEntitySnapshot;
            }
            CraftEntitySnapshot craftEntitySnapshot = new CraftEntitySnapshot(output.buildResult(), entity.getType());
            return craftEntitySnapshot;
        }
    }

    public static CraftEntitySnapshot create(CompoundTag tag, EntityType type) {
        if (tag == null || tag.isEmpty() || type == null) {
            return null;
        }

        return new CraftEntitySnapshot(tag, type);
    }
    
    public static CraftEntitySnapshot create(CompoundTag tag) {
        try (ProblemReporter.ScopedCollector problemReporter = new ProblemReporter.ScopedCollector(() -> "create", LOGGER);){
            EntityType type = net.minecraft.world.entity.EntityType.by(NbtReadView_createGlobal(problemReporter, tag)).map(CraftEntityType::minecraftToBukkit).orElse(null);
            CraftEntitySnapshot craftEntitySnapshot = CraftEntitySnapshot.create(tag, type);
            return craftEntitySnapshot;
        }
    }

}