package org.bukkit.craftbukkit.damage;

import java.util.Objects;
import net.minecraft.util.math.Vec3d;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.damage.CraftDamageType;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;

public class CraftDamageSource implements DamageSource {

    private final net.minecraft.entity.damage.DamageSource damageSource;
    private final DamageType damageType;

    public CraftDamageSource(net.minecraft.entity.damage.DamageSource damageSource) {
        this.damageSource = damageSource;
        this.damageType = CraftDamageType.minecraftHolderToBukkit(damageSource.getTypeRegistryEntry());
    }

    public net.minecraft.entity.damage.DamageSource getHandle() {
        return this.damageSource;
    }

    public World getCausingEntityWorld() {
        org.bukkit.entity.Entity causingEntity = this.getCausingEntity();
        return causingEntity != null ? causingEntity.getWorld() : null;
    }

    public Block getDirectBlock() {
        return null;
    	// return this.getHandle().getDirectBlock();
    }

    @Override
    public DamageType getDamageType() {
        return this.damageType;
    }

    @Override
    public org.bukkit.entity.Entity getCausingEntity() {
        net.minecraft.entity.Entity entity = this.getHandle().getAttacker(); // .getCausingEntity();
        return (entity != null) ? entity.getBukkitEntity() : null;
    }

    @Override
    public org.bukkit.entity.Entity getDirectEntity() {
        net.minecraft.entity.Entity entity = this.getHandle().getSource();
        return (entity != null) ? entity.getBukkitEntity() : null;
    }

    @Override
    public Location getDamageLocation() {
        Vec3d vec3D = this.getHandle().getStoredPosition();
        return (vec3D != null) ? CraftLocation.toBukkit(vec3D, this.getCausingEntityWorld()) : null;
    }

    @Override
    public Location getSourceLocation() {
        Vec3d vec3D = this.getHandle().getPosition();
        return (vec3D != null) ? CraftLocation.toBukkit(vec3D, this.getCausingEntityWorld()) : null;
    }

    @Override
    public boolean isIndirect() {
        return this.getHandle().getAttacker() != this.getHandle().getSource();

    	// return this.getHandle().getCausingEntity() != this.getHandle().getSource();
    }

    @Override
    public float getFoodExhaustion() {
        return this.damageType.getExhaustion();
    }

    @Override
    public boolean scalesWithDifficulty() {
        return this.getHandle().isScaledWithDifficulty();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof DamageSource)) {
            return false;
        }

        DamageSource other = (DamageSource) obj;
        return Objects.equals(this.getDamageType(), other.getDamageType()) && Objects.equals(this.getCausingEntity(), other.getCausingEntity())
                && Objects.equals(this.getDirectEntity(), other.getDirectEntity()) && Objects.equals(this.getDamageLocation(), other.getDamageLocation());
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + this.damageType.hashCode();
        result = 31 * result + (this.getCausingEntity() != null ? this.getCausingEntity().hashCode() : 0);
        result = 31 * result + (this.getDirectEntity() != null ? this.getDirectEntity().hashCode() : 0);
        result = 31 * result + (this.getDamageLocation() != null ? this.getDamageLocation().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DamageSource{damageType=" + this.getDamageType() + ",causingEntity=" + this.getCausingEntity() + ",directEntity=" + this.getDirectEntity() + ",damageLocation=" + this.getDamageLocation() + "}";
    }

    public static DamageSource buildFromBukkit(DamageType damageType, Entity causingEntity, Entity directEntity, Location damageLocation) {
        net.minecraft.registry.entry.RegistryEntry<net.minecraft.entity.damage.DamageType> holderDamageType = CraftDamageType.bukkitToMinecraftHolder(damageType);

        net.minecraft.entity.Entity nmsCausingEntity = null;
        if (causingEntity instanceof CraftEntity craftCausingEntity) {
            nmsCausingEntity = craftCausingEntity.getHandle();
        }

        net.minecraft.entity.Entity nmsDirectEntity = null;
        if (directEntity instanceof CraftEntity craftDirectEntity) {
            nmsDirectEntity = craftDirectEntity.getHandle();
        }

        Vec3d vec3D = (damageLocation == null) ? null : CraftLocation.toVec3D(damageLocation);

        net.minecraft.entity.damage.DamageSource sor = new net.minecraft.entity.damage.DamageSource(holderDamageType, nmsDirectEntity, nmsCausingEntity);
        // TODO: set vec3d
        return new CraftDamageSource(sor);
        
        
        // return new CraftDamageSource(new net.minecraft.entity.damage.DamageSource(holderDamageType, nmsDirectEntity, nmsCausingEntity, vec3D));
    }

}
