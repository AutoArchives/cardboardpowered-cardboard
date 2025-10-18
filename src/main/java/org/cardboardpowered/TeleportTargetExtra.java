package org.cardboardpowered;

import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

// TODO: Mixin TeleportTarget
public class TeleportTargetExtra {

	public static TeleportTarget newTeleportTarget(ServerWorld level, Entity entity, TeleportTarget.PostDimensionTransition trans) {
        return new TeleportTarget(
           level,
           getWorldSpawnPos(level, entity),
           Vec3d.ZERO,
           level.getSpawnPoint().yaw(),
           level.getSpawnPoint().pitch(),
           false,
           false,
           Set.of(),
           trans
           // TeleportCause.UNKNOWN
        );
     }
    
    private static Vec3d getWorldSpawnPos(ServerWorld world, Entity entity) {
        return entity.getWorldSpawnPos(world, world.getSpawnPoint().getPos()).toBottomCenterPos();
     }
	
}
