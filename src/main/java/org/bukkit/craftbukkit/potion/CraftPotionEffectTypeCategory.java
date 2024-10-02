package org.bukkit.craftbukkit.potion;

import com.google.common.base.Preconditions;
import net.minecraft.entity.effect.StatusEffectCategory;
import org.bukkit.potion.PotionEffectTypeCategory;

public final class CraftPotionEffectTypeCategory {

    public static PotionEffectTypeCategory minecraftToBukkit(StatusEffectCategory minecraft) {
        Preconditions.checkArgument((minecraft != null ? 1 : 0) != 0);
        return PotionEffectTypeCategory.valueOf(minecraft.name());
    }

    public static StatusEffectCategory bukkitToMinecraft(PotionEffectTypeCategory bukkit) {
        Preconditions.checkArgument((bukkit != null ? 1 : 0) != 0);
        return StatusEffectCategory.valueOf(bukkit.name());
    }

}
