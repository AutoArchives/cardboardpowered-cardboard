package org.cardboardpowered.interfaces;

import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;

public interface IMixinLootContextParameters {

	ContextKey<Integer> LOOTING_MOD = new ContextKey<>(Identifier.parse("bukkit:looting_mod"));

}