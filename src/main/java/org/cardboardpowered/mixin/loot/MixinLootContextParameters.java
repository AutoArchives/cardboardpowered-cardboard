package org.cardboardpowered.mixin.loot;

import org.spongepowered.asm.mixin.Mixin;

import org.cardboardpowered.interfaces.IMixinLootContextParameters;

import net.minecraft.loot.context.LootContextParameters;

@Mixin(LootContextParameters.class)
public class MixinLootContextParameters implements IMixinLootContextParameters {
    // Inherent static method from interface
}