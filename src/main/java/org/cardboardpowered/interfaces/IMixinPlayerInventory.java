package org.cardboardpowered.interfaces;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import org.cardboardpowered.bridge.world.ContainerBridge;

public interface IMixinPlayerInventory extends ContainerBridge {

    int canHold(ItemStack itemstack);

	List<ItemStack> getArmorContents();

	List<ItemStack> getExtraContent();

}