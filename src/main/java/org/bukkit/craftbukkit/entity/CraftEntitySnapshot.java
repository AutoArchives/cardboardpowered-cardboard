package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import com.javazilla.bukkitfabric.interfaces.IMixinEntity;

import java.util.function.Function;
import net.minecraft.nbt.NbtCompound;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.cardboardpowered.impl.world.WorldImpl;

public class CraftEntitySnapshot implements EntitySnapshot {

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
        return this.data.asString();
    }

    private net.minecraft.entity.Entity createInternal(World world) {
        net.minecraft.world.World nms = ((WorldImpl) world).getHandle();
        net.minecraft.entity.Entity internal = net.minecraft.entity.EntityType.loadEntityWithPassengers(this.data, nms, Function.identity());
        if (internal == null) { // Try creating by type
            internal = CraftEntityType.bukkitToMinecraft(this.type).create(nms);
        }

        Preconditions.checkArgument(internal != null, "Error creating new entity."); // This should only fail if the stored NBTTagCompound is malformed.
        internal.readNbt(this.data);

        return internal;
    }

    public NbtCompound getData() {
        return this.data;
    }

    public static CraftEntitySnapshot create(CraftEntity entity) {
        NbtCompound tag = new NbtCompound();
        if (!entity.getHandle().saveSelfNbt(tag)) {
            return null;
        }

        return new CraftEntitySnapshot(tag, entity.getType());
    }

    public static CraftEntitySnapshot create(NbtCompound tag, EntityType type) {
        if (tag == null || tag.isEmpty() || type == null) {
            return null;
        }

        return new CraftEntitySnapshot(tag, type);
    }

    public static CraftEntitySnapshot create(NbtCompound tag) {
        EntityType type = net.minecraft.entity.EntityType.fromNbt(tag).map(CraftEntityType::minecraftToBukkit).orElse(null);
        return CraftEntitySnapshot.create(tag, type);
    }

}