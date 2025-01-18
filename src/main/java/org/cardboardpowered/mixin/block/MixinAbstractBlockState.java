package org.cardboardpowered.mixin.block;

import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.cardboardpowered.interfaces.IBlockState;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.block.AbstractBlock.AbstractBlockState;

@Mixin(AbstractBlockState.class)
public class MixinAbstractBlockState implements IBlockState {

	private CraftBlockData cachedCraftBlockData;
	
	@Override
	public CraftBlockData createCraftBlockData() {
        if (this.cachedCraftBlockData == null) {
        	AbstractBlockState thiz = (AbstractBlockState) (Object) this;
            this.cachedCraftBlockData = CraftBlockData.createData(thiz.asBlockState());
        }
        return (CraftBlockData)this.cachedCraftBlockData.clone();
    }
	
}
