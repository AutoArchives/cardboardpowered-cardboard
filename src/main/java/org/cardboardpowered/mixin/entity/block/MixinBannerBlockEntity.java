package org.cardboardpowered.mixin.entity.block;

import net.minecraft.world.level.block.entity.BannerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BannerBlockEntity.class)
public class MixinBannerBlockEntity {

    // TODO: 1.18.2
    
    //@Shadow
    //public NbtList patternListTag;

    //@Inject(at = @At("TAIL"), method = "readNbt")
    //public void bukkit_readNbt(NbtCompound nbttagcompound, CallbackInfo ci) {
        // Bukkit - TitleEntityBanner.patch
        //while (this.patternListTag.size() > 20)
        //    this.patternListTag.remove(20);
    //}

}