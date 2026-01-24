package org.cardboardpowered.mixin.entity;

import org.cardboardpowered.interfaces.ICreeperEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.event.CraftEventFactory;

@Mixin(Creeper.class)
public abstract class MixinCreeperEntity extends Entity implements ICreeperEntity {

    public MixinCreeperEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Shadow
    public static EntityDataAccessor<Boolean> DATA_IS_POWERED;

    @Shadow
    public int explosionRadius = 3;

    @Shadow
    public int maxSwell = 30;

    @Inject(at = @At("HEAD"), method="thunderHit", cancellable = true)
    public void invokeCreeperPowerEvent(ServerLevel worldserver, LightningBolt lightning, CallbackInfo ci) {
        super.thunderHit(worldserver, lightning);
        if (CraftEventFactory.callCreeperPowerEvent((Creeper)(Object)this, lightning, org.bukkit.event.entity.CreeperPowerEvent.PowerCause.LIGHTNING).isCancelled()) {
            ci.cancel();
            return;
        }
        this.setPowered(true);
        ci.cancel();
        return;
    }

    @Override
    public void setPowered(boolean powered) {
        this.entityData.set(DATA_IS_POWERED, powered);
    }

    @Override
    public void explodeBF() {
        explodeCreeper();
    }

    @Shadow
    public void explodeCreeper() {
    }

    @Override
    public int getExplosionRadiusBF() {
        return explosionRadius;
    }

    @Override
    public void setExplosionRadiusBF(int radius) {
        this.explosionRadius = radius;
    }

    @Override
    public void setFuseTimeBF(int ticks) {
        this.maxSwell = ticks;
    }

    @Override
    public int getFuseTimeBF() {
        return this.maxSwell;
    }

    @Override
    public boolean isPoweredBF() {
        return (Boolean) this.entityData.get(DATA_IS_POWERED);
    }


}