package org.cardboardpowered.mixin;

import net.minecraft.server.level.ServerBossEvent;
import org.cardboardpowered.interfaces.IMixinServerBossBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerBossEvent.class)
public class MixinServerBossBar implements IMixinServerBossBar {

   /* @Override
    public void sendPacketBF(Type updateName) {
        sendPacket(updateName);
    }

    @Shadow
    public void sendPacket(Type updateName) {
    }*/
    // TODO 1.17ify

}
