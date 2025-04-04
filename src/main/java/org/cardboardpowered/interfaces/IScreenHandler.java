/**
 * Cardboard Mod
 */
package org.cardboardpowered.interfaces;

import org.cardboardpowered.impl.inventory.CardboardInventoryView;

/**
 * Injection interface for ScreenHandler
 * 
 * @author Cardboard Mod
 * @see {@link net.minecraft.screen.ScreenHandler}
 * @see {@link org.cardboardpowered.interfaces.IMixinScreenHandler}
 */
public interface IScreenHandler {

	CardboardInventoryView getBukkitView();

}