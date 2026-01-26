package org.cardboardpowered.impl.entity;

import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftProjectile;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.ThrowableProjectile;
import org.bukkit.inventory.ItemStack;

import org.cardboardpowered.interfaces.IMixinThrownItemEntity;

public class ThrowableProjectileImpl extends CraftProjectile implements ThrowableProjectile {

    public ThrowableProjectileImpl(CraftServer server, Projectile entity) {
        super(server, entity);
    }

    @Override
    public ItemStack getItem() {
        if (this.getHandle().getItem().isEmpty()) {
        	return CraftItemStack.asBukkitCopy(new net.minecraft.world.item.ItemStack(((IMixinThrownItemEntity)getHandle()).getDefaultItemPublic()));
        } else {
        	return CraftItemStack.asBukkitCopy(this.getHandle().getItem());
        }
        
    }

    @Override
    public void setItem(ItemStack item) {
        getHandle().setItem(CraftItemStack.asNMSCopy(item));
    }

    @Override
    public ThrowableItemProjectile getHandle() {
        return (ThrowableItemProjectile) entity;
    }


}