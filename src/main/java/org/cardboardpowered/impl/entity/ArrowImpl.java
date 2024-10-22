package org.cardboardpowered.impl.entity;

import com.google.common.base.Preconditions;
import com.javazilla.bukkitfabric.interfaces.IMixinEntity;
import com.javazilla.bukkitfabric.interfaces.IMixinPersistentProjectileEntity;

import net.kyori.adventure.text.Component;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang.Validate;
import org.bukkit.Chunk;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArrowImpl extends AbstractProjectile implements AbstractArrow {

    public ArrowImpl(CraftServer server, PersistentProjectileEntity entity) {
        super(server, entity);
    }

    @Override
    public void setKnockbackStrength(int knockbackStrength) {
        // Removed in 1.21
    	
    	// Validate.isTrue(knockbackStrength >= 0, "Knockback cannot be negative");
        // getHandle().setPunch(knockbackStrength);
    }

    @Override
    public int getKnockbackStrength() {
        return ((IMixinPersistentProjectileEntity)getHandle()).getPunchBF();
    }

    @Override
    public double getDamage() {
        return getHandle().getDamage();
    }

    @Override
    public void setDamage(double damage) {
        Preconditions.checkArgument(damage >= 0, "Damage must be positive");
        getHandle().setDamage(damage);
    }

    @Override
    public int getPierceLevel() {
        return getHandle().getPierceLevel();
    }

    @Override
    public void setPierceLevel(int pierceLevel) {
        Preconditions.checkArgument(0 <= pierceLevel && pierceLevel <= Byte.MAX_VALUE, "Pierce level out of range, expected 0 < level < 127");

        getHandle().setPierceLevel((byte) pierceLevel);
    }

    @Override
    public boolean isCritical() {
        return getHandle().isCritical();
    }

    @Override
    public void setCritical(boolean critical) {
        getHandle().setCritical(critical);
    }

    @Override
    public ProjectileSource getShooter() {
        return ((IMixinEntity)getHandle()).getProjectileSourceBukkit();
    }

    @Override
    public void setShooter(ProjectileSource shooter) {
        if (shooter instanceof Entity) {
            getHandle().setOwner(((CraftEntity) shooter).getHandle());
        } else {
            getHandle().setOwner(null);
        }
        ((IMixinEntity)getHandle()).setProjectileSourceBukkit(shooter);
    }

    @Override
    public boolean isInBlock() {
        return ((IMixinPersistentProjectileEntity)getHandle()).getInGroundBF();
    }

    @Override
    public Block getAttachedBlock() {
        if (!isInBlock()) return null;

        BlockPos pos = getHandle().getBlockPos();
        return getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public PickupStatus getPickupStatus() {
        return PickupStatus.values()[getHandle().pickupType.ordinal()];
    }

    @Override
    public void setPickupStatus(PickupStatus status) {
        Preconditions.checkNotNull(status, "status");
        getHandle().pickupType = PersistentProjectileEntity.PickupPermission.fromOrdinal(status.ordinal());
    }

    @Override
    public void setTicksLived(int value) {
        super.setTicksLived(value);

        // Second field for EntityArrow
        ((IMixinPersistentProjectileEntity)getHandle()).setLifeBF(value);
    }

    @Override
    public boolean isShotFromCrossbow() {
        return getHandle().isShotFromCrossbow();
    }

    @Override
    public void setShotFromCrossbow(boolean shotFromCrossbow) {
        // Removed from 1.21
    	// getHandle().setShotFromCrossbow(shotFromCrossbow);
    }

    @Override
    public PersistentProjectileEntity getHandle() {
        return (PersistentProjectileEntity) nms;
    }

    @Override
    public String toString() {
        return "CraftArrow";
    }

    @Override
    public EntityType getType() {
        return EntityType.ARROW;
    }

    @Override
    public boolean fromMobSpawner() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public SpawnReason getEntitySpawnReason() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isInBubbleColumn() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isInWaterOrBubbleColumn() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isInWaterOrRain() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isInWaterOrRainOrBubbleColumn() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public ItemStack getItemStack() {
        // TODO Auto-generated method stub
    	// return CraftItemStack.asCraftMirror(this.getHandle().asItemStack());
        return null;
    }

    @Override
    public @Nullable Component customName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void customName(@Nullable Component arg0) {
        // TODO Auto-generated method stub
        
    }

	@Override
	public boolean hasNoPhysics() {
		return this.getHandle().isNoClip();
	}

	@Override
	public void setNoPhysics(boolean arg0) {
		this.getHandle().setNoClip(arg0);
	}

	@Override
	public @NotNull Sound getHitSound() {
		// TODO Auto-generated method stub
		return Sound.ENTITY_ARROW_SHOOT;
	}

	@Override
	public int getLifetimeTicks() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setHitSound(@NotNull Sound arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLifetimeTicks(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	// 1.20.4 API:

	@Override
	public ItemStack getItem() {
        return CraftItemStack.asBukkitCopy(this.getHandle().getItemStack());
	}

	@Override
	public void setItem(@NotNull ItemStack item) {
        // TODO
		// this.getHandle().stack = CraftItemStack.asNMSCopy(item);
	}

	@Override
	public void setItemStack(@NotNull ItemStack stack) {
		// TODO Auto-generated method stub
		// this.getHandle().stack = null;
	}

	@Override
	public ItemStack getWeapon() {
		if (this.getHandle().getWeaponStack() == null) {
			return null;
		}
		return CraftItemStack.asBukkitCopy(this.getHandle().getWeaponStack());
	}

	@Override
	public void setWeapon(@NotNull ItemStack item) {
		this.getHandle().weapon = CraftItemStack.asNMSCopy(item);
	}

	@Override
	public void setShooter(@Nullable ProjectileSource shooter, boolean resetPickupStatus) {
        if (shooter instanceof CraftEntity) {
            CraftEntity craftEntity = (CraftEntity)shooter;
            this.getHandle().setOwner(craftEntity.getHandle());
        } else {
            this.getHandle().setOwner(null);
        }
        this.getHandle().setProjectileSourceBukkit(shooter);
	}

}