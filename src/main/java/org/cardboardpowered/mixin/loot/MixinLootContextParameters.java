package org.cardboardpowered.mixin.loot;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.cardboardpowered.bridge.world.level.storage.loot.parameters.LootContextParamsBridge;

@Mixin(LootContextParams.class)
public class MixinLootContextParameters implements LootContextParamsBridge {
    // Inherent static method from interface
}