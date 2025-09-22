package org.cardboardpowered.mixin.entity;

import org.bukkit.projectiles.ProjectileSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.cardboardpowered.interfaces.IMixinEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

@Mixin(ProjectileEntity.class)
public class MixinProjectileEntity extends MixinEntity {

    @Inject(at = @At("TAIL"), method = "setOwner")
    public void setProjectileSource(LazyEntityReference<Entity> entity, CallbackInfo ci) {
    	cb$refreshProjectileSource(false);
    }

    @Inject(at = @At("HEAD"), method = "onCollision")
    public void fireProjectileHitEvent(HitResult hitResult, CallbackInfo ci) {
        CraftEventFactory.callProjectileHitEvent((ProjectileEntity)(Object)this, hitResult);
    }

    @Shadow
    public void onBlockHit(BlockHitResult blockHitResult) {
    }
    
    private void cb$refreshProjectileSource(boolean fillCache) {
        CraftEntity craftEntity;
        Entity owner;
        if (fillCache) {
            this.getOwner();
        }
        if ((owner = this.getOwner()) != null && this.projectileSource == null && (craftEntity = ((IMixinEntity)owner).getBukkitEntity()) instanceof ProjectileSource) {
            ProjectileSource source = (ProjectileSource)craftEntity;
            this.projectileSource = source;
        }
    }
    
    @Shadow
    public Entity getOwner() {
        return null; // Shadowed
    }

}