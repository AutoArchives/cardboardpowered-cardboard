package io.papermc.paper.datacomponent;

import java.util.function.Function;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Unit;
import net.minecraft.util.dynamic.NullOps;
import org.bukkit.craftbukkit.CraftRegistry;

public record DataComponentAdapter<NMS, API>(ComponentType<NMS> type, Function<API, NMS> apiToVanilla, Function<NMS, API> vanillaToApi, boolean codecValidation) {
    static final Function<Void, Unit> API_TO_UNIT_CONVERTER = $ -> Unit.INSTANCE;

    public boolean isValued() {
        return this.apiToVanilla != API_TO_UNIT_CONVERTER;
    }

    public NMS toVanilla(API value) {
        NMS nms = this.apiToVanilla.apply(value);
        if (this.codecValidation) {
            this.type.getCodecOrThrow().encodeStart(CraftRegistry.getMinecraftRegistry().getOps(NullOps.INSTANCE), nms).ifError(error -> {
                throw new IllegalArgumentException("Failed to encode data component %s (%s)".formatted(Registries.DATA_COMPONENT_TYPE.getId(this.type), error.message()));
            });
        }
        return nms;
    }

    public API fromVanilla(NMS value) {
        return this.vanillaToApi.apply(value);
    }
}

