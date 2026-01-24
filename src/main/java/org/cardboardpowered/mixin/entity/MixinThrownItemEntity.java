package org.cardboardpowered.mixin.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.cardboardpowered.interfaces.IMixinThrownItemEntity;

@Mixin(ThrowableItemProjectile.class)
public abstract class MixinThrownItemEntity implements IMixinThrownItemEntity {

    @Shadow
    public abstract Item getDefaultItem();

    @Override
    public Item getDefaultItemPublic() {
        return getDefaultItem();
    }

    @Override
    @Deprecated
    public ItemStack getItemBF() {
        return ((ThrowableItemProjectile) (Object) this).getItem();
    }

}