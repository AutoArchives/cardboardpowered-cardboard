package org.cardboardpowered.mixin;

import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @deprecated Moved.
 * @see {@link org.cardboardpowered.mixin.network.MixinServerConfigurationNetworkHandler}
 */
@Deprecated
@Mixin(value = ServerConfigurationNetworkHandler.class, priority = 900)
public class MixinServerNetworkConfiguration {
}