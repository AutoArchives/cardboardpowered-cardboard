package org.bukkit.craftbukkit.damage;

import net.minecraft.entity.damage.DamageEffects;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.CraftSound;
import org.bukkit.damage.DamageEffect;

public class CraftDamageEffect implements DamageEffect {

    private final DamageEffects damageEffects;

    public CraftDamageEffect(DamageEffects damageEffects) {
        this.damageEffects = damageEffects;
    }

    public DamageEffects getHandle() {
        return this.damageEffects;
    }

    @Override
    public Sound getSound() {
        return CraftSound.minecraftToBukkit(this.getHandle().getSound());
    }

    public static DamageEffect getById(String id) {
        for (DamageEffects damageEffects : DamageEffects.values()) {
            if (damageEffects.asString().equalsIgnoreCase(id)) {
                return CraftDamageEffect.toBukkit(damageEffects);
            }
        }
        return null;
    }

    public static DamageEffect toBukkit(DamageEffects damageEffects) {
        return new CraftDamageEffect(damageEffects);
    }

}