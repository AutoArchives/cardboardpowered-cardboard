package org.bukkit.craftbukkit.potion;

import com.google.common.base.Preconditions;
import net.minecraft.world.effect.MobEffectCategory;
import org.bukkit.potion.PotionEffectTypeCategory;

public final class CraftPotionEffectTypeCategory {

    public static PotionEffectTypeCategory minecraftToBukkit(MobEffectCategory minecraft) {
        Preconditions.checkArgument((minecraft != null ? 1 : 0) != 0);
        return PotionEffectTypeCategory.valueOf(minecraft.name());
    }

    public static MobEffectCategory bukkitToMinecraft(PotionEffectTypeCategory bukkit) {
        Preconditions.checkArgument((bukkit != null ? 1 : 0) != 0);
        return MobEffectCategory.valueOf(bukkit.name());
    }

}
