package org.cardboardpowered.mixin;

import org.cardboardpowered.interfaces.IMixinServerBossBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.entity.boss.ServerBossBar;

@Mixin(ServerBossBar.class)
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
