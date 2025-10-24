package org.cardboardpowered.mixin.network.handler;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ServerPlayNetworkHandler.class, priority = 800)
@Deprecated
/**
 * @deprecated leftover 1.18.2
 */
public class MixinSPNH_PlayerCommandPreprocessEvent_1_18 {
}
