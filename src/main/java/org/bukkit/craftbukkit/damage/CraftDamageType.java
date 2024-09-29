package org.bukkit.craftbukkit.damage;

import com.google.common.base.Preconditions;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.damage.DamageEffect;
import org.bukkit.damage.DamageScaling;
import org.bukkit.damage.DamageType;
import org.bukkit.damage.DeathMessageType;

public class CraftDamageType implements DamageType, Handleable<net.minecraft.entity.damage.DamageType> {

    private final NamespacedKey key;
    private final net.minecraft.entity.damage.DamageType damageType;

    public CraftDamageType(NamespacedKey key, net.minecraft.entity.damage.DamageType damageType) {
        this.key = key;
        this.damageType = damageType;
    }

    @Override
    public net.minecraft.entity.damage.DamageType getHandle() {
        return this.damageType;
    }

    @Override
    public String getTranslationKey() {
        return this.getHandle().msgId();
    }

    @Override
    public DamageScaling getDamageScaling() {
        return CraftDamageType.damageScalingToBukkit(this.getHandle().scaling());
    }

    @Override
    public DamageEffect getDamageEffect() {
        return CraftDamageEffect.toBukkit(this.getHandle().effects());
    }

    @Override
    public DeathMessageType getDeathMessageType() {
        return CraftDamageType.deathMessageTypeToBukkit(this.getHandle().deathMessageType());
    }

    @Override
    public float getExhaustion() {
        return this.getHandle().exhaustion();
    }

    @Override
    public NamespacedKey getKey() {
        return this.key;
    }

    @Override
    public String toString() {
        return "CraftDamageType{" + "key=" + this.getKey() + ",damageScaling=" + this.getDamageScaling() + ",damageEffect=" + this.getDamageEffect() + ",deathMessageType=" + this.getDeathMessageType() + ",exhaustion=" + this.getExhaustion() + "}";
    }

    public static DeathMessageType deathMessageTypeToBukkit(net.minecraft.entity.damage.DeathMessageType deathMessageType) {
        return switch (deathMessageType) {
            case DEFAULT -> DeathMessageType.DEFAULT;
            case FALL_VARIANTS -> DeathMessageType.FALL_VARIANTS;
            case INTENTIONAL_GAME_DESIGN -> DeathMessageType.INTENTIONAL_GAME_DESIGN;
            default -> throw new IllegalArgumentException("NMS DeathMessageType." + deathMessageType + " cannot be converted to a Bukkit DeathMessageType.");
        };
    }

    public static net.minecraft.entity.damage.DeathMessageType deathMessageTypeToNMS(DeathMessageType deathMessageType) {
        return switch (deathMessageType) {
            case DEFAULT -> net.minecraft.entity.damage.DeathMessageType.DEFAULT;
            case FALL_VARIANTS -> net.minecraft.entity.damage.DeathMessageType.FALL_VARIANTS;
            case INTENTIONAL_GAME_DESIGN -> net.minecraft.entity.damage.DeathMessageType.INTENTIONAL_GAME_DESIGN;
            default -> throw new IllegalArgumentException("Bukkit DeathMessageType." + deathMessageType + " cannot be converted to a NMS DeathMessageType.");
        };
    }

    public static DamageScaling damageScalingToBukkit(net.minecraft.entity.damage.DamageScaling damageScaling) {
        return switch (damageScaling) {
            case ALWAYS -> DamageScaling.ALWAYS;
            case WHEN_CAUSED_BY_LIVING_NON_PLAYER -> DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER;
            case NEVER -> DamageScaling.NEVER;
            default -> throw new IllegalArgumentException("NMS DamageScaling." + damageScaling + " cannot be converted to a Bukkit DamageScaling");
        };
    }

    public static net.minecraft.entity.damage.DamageScaling damageScalingToNMS(DamageScaling damageScaling) {
        return switch (damageScaling) {
            case ALWAYS -> net.minecraft.entity.damage.DamageScaling.ALWAYS;
            case WHEN_CAUSED_BY_LIVING_NON_PLAYER -> net.minecraft.entity.damage.DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER;
            case NEVER -> net.minecraft.entity.damage.DamageScaling.NEVER;
            default -> throw new IllegalArgumentException("Bukkit DamageScaling." + damageScaling + " cannot be converted to a NMS DamageScaling");
        };
    }

    public static DamageType minecraftHolderToBukkit(RegistryEntry<net.minecraft.entity.damage.DamageType> minecraftHolder) {
        return CraftDamageType.minecraftToBukkit(minecraftHolder.value());
    }

    public static RegistryEntry<net.minecraft.entity.damage.DamageType> bukkitToMinecraftHolder(DamageType bukkitDamageType) {
        Preconditions.checkArgument(bukkitDamageType != null);

        net.minecraft.registry.Registry<net.minecraft.entity.damage.DamageType> registry = CraftRegistry.getMinecraftRegistry(RegistryKeys.DAMAGE_TYPE);

        if (registry.getEntry(CraftDamageType.bukkitToMinecraft(bukkitDamageType)) instanceof RegistryEntry.Reference<net.minecraft.entity.damage.DamageType> holder) {
            return holder;
        }

        throw new IllegalArgumentException("No Reference holder found for " + bukkitDamageType
                + ", this can happen if a plugin creates its own damage type with out properly registering it.");
    }

    public static net.minecraft.entity.damage.DamageType bukkitToMinecraft(DamageType bukkitDamageType) {
        return CraftRegistry.bukkitToMinecraft(bukkitDamageType);
    }

    public static DamageType minecraftToBukkit(net.minecraft.entity.damage.DamageType minecraftDamageType) {
        return CraftRegistry.minecraftToBukkit(minecraftDamageType, RegistryKeys.DAMAGE_TYPE, Registry.DAMAGE_TYPE);
    }

}