package org.cardboardpowered.impl.entity;

import java.util.Random;
import java.util.UUID;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftProjectile;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.meta.FireworkMeta;
import org.cardboardpowered.interfaces.IMixinDataTracker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CardboardFirework extends CraftProjectile implements Firework {

    private final Random random = new Random();
    private final CraftItemStack item;

    public CardboardFirework(CraftServer server, FireworkRocketEntity entity) {
        super(server, entity);

        ItemStack item = getHandle().getEntityData().get(FireworkRocketEntity.DATA_ID_FIREWORKS_ITEM);

        if (item.isEmpty()) {
            item = new ItemStack(Items.FIREWORK_ROCKET);
            getHandle().getEntityData().set(FireworkRocketEntity.DATA_ID_FIREWORKS_ITEM, item);
        }

        this.item = CraftItemStack.asCraftMirror(item);

        // Ensure the item is a firework...
        if (this.item.getType() != Material.FIREWORK_ROCKET) {
            this.item.setType(Material.FIREWORK_ROCKET);
        }
    }

    @Override
    public FireworkRocketEntity getHandle() {
        return (FireworkRocketEntity) nms;
    }

    @Override
    public String toString() {
        return "CraftFirework";
    }

    @Override
    public EntityType getType() {
        return EntityType.FIREWORK_ROCKET;
    }

    @Override
    public FireworkMeta getFireworkMeta() {
        return (FireworkMeta) item.getItemMeta();
    }

    @Override
    public void setFireworkMeta(FireworkMeta meta) {
        item.setItemMeta(meta);

        // Copied from FireworkRocketEntity constructor, update firework lifetime/power
        getHandle().lifetime = 10 * (1 + meta.getPower()) + random.nextInt(6) + random.nextInt(7);

        ((IMixinDataTracker) getHandle().getEntityData()).markDirty(FireworkRocketEntity.DATA_ID_FIREWORKS_ITEM);
    }

    @Override
    public void detonate() {
        getHandle().lifetime = 0;
    }

    @Override
    public boolean isShotAtAngle() {
        return getHandle().isShotAtAngle();
    }

    @Override
    public void setShotAtAngle(boolean shotAtAngle) {
        getHandle().getEntityData().set(FireworkRocketEntity.DATA_SHOT_AT_ANGLE, shotAtAngle);
    }

    @Override
    public @Nullable UUID getSpawningEntity() {
        return null;//TODO
    }

    @Override
    public @Nullable LivingEntity getBoostedEntity() {
        return null;//TODO
    }

	@Override
	public org.bukkit.inventory.@NotNull ItemStack getItem() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTicksFlown() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTicksToDetonate() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setItem(org.bukkit.inventory.@Nullable ItemStack arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setTicksFlown(int arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setTicksToDetonate(int arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public @Nullable LivingEntity getAttachedTo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLife() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxLife() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isDetonated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setAttachedTo(@Nullable LivingEntity arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setLife(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setMaxLife(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}
