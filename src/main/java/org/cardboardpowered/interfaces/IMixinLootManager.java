package org.cardboardpowered.interfaces;

import java.util.Map;
import net.minecraft.resources.Identifier;

/**
 */
public interface IMixinLootManager {

	Map<?, Identifier> getLootTableToKeyMapBF();

}