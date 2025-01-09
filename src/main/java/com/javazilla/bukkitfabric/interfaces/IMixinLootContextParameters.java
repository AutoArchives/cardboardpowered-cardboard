package com.javazilla.bukkitfabric.interfaces;

import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.Identifier;

public interface IMixinLootContextParameters {

	ContextParameter<Integer> LOOTING_MOD = new ContextParameter<>(Identifier.of("bukkit:looting_mod"));

}