package org.cardboardpowered.mixin.network.handler;

import com.javazilla.bukkitfabric.interfaces.IMixinPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ServerPlayNetworkHandler.class, priority = 800)
@Deprecated
/**
 * @deprecated leftover 1.18.2
 */
public abstract class MixinSPNH_PlayerCommandPreprocessEvent_1_18 implements IMixinPlayNetworkHandler {

    /*
    @Inject(at = @At("HEAD"), method = "handleCommandExecution", cancellable = true)
    public void executeCommand_1_18_2(CommandExecutionC2SPacket packet, LastSeenMessageList messages, CallbackInfo ci) {
    }

    public PlayerImpl getPlayer() {
        return (PlayerImpl) ((IMixinServerEntityPlayer)(Object)this.player).getBukkitEntity();
    }
    */

}
