package org.cardboardpowered.mixin.entity.block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.javazilla.bukkitfabric.interfaces.IMixinSignBlockEntity;

import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Mixin(SignBlockEntity.class)
public class MixinSignBlockEntity implements IMixinSignBlockEntity {

   // @Shadow
   // public Text[] texts;

    @Override
    public Text[] getTextBF() {
    	SignBlockEntity e = (SignBlockEntity)(Object)this;
    	return e.getFrontText().getMessages(false);
    	
        //return texts;
    }
    
    // public boolean isPlayerFacingFront(PlayerEntity player) {
    //    return this.isFacingFrontText(player.getX(), player.getZ());
    //}

    @Override
    public boolean cardboard$isFacingFrontText(double x, double z) {
    	SignBlockEntity thiz = (SignBlockEntity) (Object) this;
    	
    	
        Block block = thiz.getCachedState().getBlock();
        if (block instanceof AbstractSignBlock) {
            AbstractSignBlock blocksign = (AbstractSignBlock)block;
            Vec3d vec3d = blocksign.getCenter(thiz.getCachedState());
            double d0 = x - ((double)thiz.getPos().getX() + vec3d.x);
            double d1 = z - ((double)thiz.getPos().getZ() + vec3d.z);
            float f2 = blocksign.getRotationDegrees(thiz.getCachedState());
            return MathHelper.angleBetween(f2, (float)(MathHelper.atan2(d1, d0) * 57.2957763671875) - 90.0f) <= 90.0f;
        }
        return false;
    }

}