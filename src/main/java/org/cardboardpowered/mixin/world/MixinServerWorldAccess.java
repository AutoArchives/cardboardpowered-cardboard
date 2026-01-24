package org.cardboardpowered.mixin.world;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerLevelAccessor.class)
public interface MixinServerWorldAccess extends LevelAccessor {

    default boolean addAllEntities(Entity entity, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason reason) {
        entity.getSelfAndPassengers().forEach((e) -> this.addFreshEntity(e));
        return !entity.isRemoved();
    }

}