package org.cardboardpowered.mixin.block;

import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.cardboardpowered.interfaces.IBlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockStateBase.class)
public class MixinAbstractBlockState implements IBlockState {

	private CraftBlockData cachedCraftBlockData;
	
	@Override
	public CraftBlockData createCraftBlockData() {
        if (this.cachedCraftBlockData == null) {
        	BlockStateBase thiz = (BlockStateBase) (Object) this;
            this.cachedCraftBlockData = CraftBlockData.createData(thiz.asState());
        }
        return (CraftBlockData)this.cachedCraftBlockData.clone();
    }
	
}
