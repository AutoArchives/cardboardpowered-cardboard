/**
 * Cardboard Mod
 */
package org.cardboardpowered.interfaces;

import org.cardboardpowered.impl.inventory.CardboardInventoryView;

/**
 * Injection interface for ScreenHandler
 * 
 * @author Cardboard Mod
 * @see {@link net.minecraft.world.inventory.AbstractContainerMenu}
 * @see {@link org.cardboardpowered.interfaces.IMixinScreenHandler}
 */
public interface IScreenHandler {

	CardboardInventoryView getBukkitView();

}