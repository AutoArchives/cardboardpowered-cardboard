package org.cardboardpowered.mixin.world;

import java.util.Comparator;

import org.cardboardpowered.interfaces.IChunkTicketType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.util.Unit;

@Mixin(ChunkTicketType.class)
public class MixinChunkTicketType implements IChunkTicketType {

    private static final ChunkTicketType PLUGIN = create("plugin", (a, b) -> 0);

    @Override
    public ChunkTicketType getBukkitPluginTicketType() {
        return PLUGIN;
    }

    @Shadow
    public static <T> ChunkTicketType create(String s, Comparator<T> comparator) {
        return null;
    }

}