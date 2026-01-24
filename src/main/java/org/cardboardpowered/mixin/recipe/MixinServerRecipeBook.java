package org.cardboardpowered.mixin.recipe;

import java.util.List;
import net.minecraft.stats.ServerRecipeBook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerRecipeBook.class)
public class MixinServerRecipeBook {

	/*
    @Inject(at = @At("HEAD"), method = "sendUnlockRecipesPacket", cancellable = true)
    private void dontSendPacketBeforeLogin(ChangeUnlockedRecipesS2CPacket.Action packetplayoutrecipes_action, ServerPlayerEntity entityplayer, List<Identifier> list, CallbackInfo ci) {
        // See SPIGOT-4478
        if (entityplayer.networkHandler == null)
            ci.cancel();
    }
    */

}