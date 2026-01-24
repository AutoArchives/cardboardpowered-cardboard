package org.cardboardpowered.mixin.loot;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.cardboardpowered.interfaces.IMixinLootContextParameters;

@Mixin(LootContextParams.class)
public class MixinLootContextParameters implements IMixinLootContextParameters {
    // Inherent static method from interface
}