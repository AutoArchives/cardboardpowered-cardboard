/**
 * Cardboard Mod
 */
package org.cardboardpowered.interfaces;

import org.bukkit.inventory.InventoryView;
import org.cardboardpowered.bridge.world.inventory.AbstractContainerMenuBridge;

/**
 * Injection interface for ScreenHandler
 * 
 * @author Cardboard Mod
 * @see {@link net.minecraft.world.inventory.AbstractContainerMenu}
 * @see {@link AbstractContainerMenuBridge}
 */
public interface IScreenHandler {

	InventoryView getBukkitView();

}