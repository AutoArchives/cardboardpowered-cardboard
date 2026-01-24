package org.cardboardpowered.impl.util;

import net.minecraft.world.level.ClipContext.Fluid;
import org.bukkit.FluidCollisionMode;

public class CardboardFluidRaytraceMode {

    public static Fluid toMc(FluidCollisionMode mode) {
        if (mode == null) return null;

        switch (mode) {
            case ALWAYS:
                return Fluid.ANY;
            case SOURCE_ONLY:
                return Fluid.SOURCE_ONLY;
            case NEVER:
                return Fluid.NONE;
            default:
                return null;
        }
    }

}
