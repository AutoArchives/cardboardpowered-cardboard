package org.cardboardpowered.mixin;

import net.minecraft.server.level.ServerBossEvent;
import org.cardboardpowered.bridge.server.level.ServerBossEventBridge;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerBossEvent.class)
public class MixinServerBossBar implements ServerBossEventBridge {

   /* @Override
    public void sendPacketBF(Type updateName) {
        sendPacket(updateName);
    }

    @Shadow
    public void sendPacket(Type updateName) {
    }*/
    // TODO 1.17ify

}
