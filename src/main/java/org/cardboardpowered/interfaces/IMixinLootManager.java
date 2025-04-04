package org.cardboardpowered.interfaces;

import java.util.Map;

import net.minecraft.util.Identifier;

/**
 */
public interface IMixinLootManager {

	Map<?, Identifier> getLootTableToKeyMapBF();

}