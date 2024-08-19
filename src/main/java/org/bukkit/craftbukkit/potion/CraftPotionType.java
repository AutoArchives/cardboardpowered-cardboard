package org.bukkit.craftbukkit.potion;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;

import me.isaiah.common.ICommonMod;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.potion.Potion;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.cardboardpowered.impl.CardboardPotionUtil;

public class CraftPotionType /*implements PotionType.InternalPotionData*/ {

    public static PotionType minecraftHolderToBukkit(RegistryEntry<Potion> minecraft) {
        return CraftPotionType.minecraftToBukkit(minecraft.value());
    }

    public static PotionType minecraftToBukkit(Potion minecraft) {
        Preconditions.checkArgument(minecraft != null);

       // PotionType.getByEffect( )
        
        net.minecraft.registry.Registry<Potion> registry = ICommonMod.getIServer().getMinecraft().getRegistryManager().get(RegistryKeys.POTION);

        // TODO: 1.20.6
        // PotionType bukkit = Registry.POTION.get(CraftNamespacedKey.fromMinecraft(registry.getKey(minecraft).orElseThrow().getValue()));

        // Preconditions.checkArgument(bukkit != null);

        return null;
        //return bukkit;
    }

    public static Potion bukkitToMinecraft(PotionType bukkit) {
        Preconditions.checkArgument(bukkit != null);

        net.minecraft.registry.Registry<Potion> reg = ICommonMod.getIServer().getMinecraft().getRegistryManager().get(RegistryKeys.POTION);
        
        // TODO: 1.20.6
        return null;
        // return reg.getOrEmpty(CraftNamespacedKey.toMinecraft(bukkit.getKey())).orElseThrow();
    }

    public static RegistryEntry<Potion> bukkitToMinecraftHolder(PotionType bukkit) {
        Preconditions.checkArgument(bukkit != null);

        net.minecraft.registry.Registry<Potion> registry = ICommonMod.getIServer().getMinecraft().getRegistryManager().get(RegistryKeys.POTION);

        if (registry.getEntry(CraftPotionType.bukkitToMinecraft(bukkit)) instanceof RegistryEntry.Reference<Potion> holder) {
            return holder;
        }

        throw new IllegalArgumentException("No Reference holder found for " + bukkit
                + ", this can happen if a plugin creates its own sound effect with out properly registering it.");
    }

    public static String bukkitToString(PotionType potionType) {
        Preconditions.checkArgument(potionType != null);

       // return potionType.getKey().toString();
        return "unknown!!";
    }

    public static PotionType stringToBukkit(String string) {
        Preconditions.checkArgument(string != null);

        
        return PotionType.UNCRAFTABLE;
        // return Registry.POTION.get(NamespacedKey.fromString(string));
    }

    private final NamespacedKey key;
    private final Potion potion;
    private final Supplier<List<PotionEffect>> potionEffects;
    private final Supplier<Boolean> upgradeable;
    private final Supplier<Boolean> extendable;
    private final Supplier<Integer> maxLevel;

    public CraftPotionType(NamespacedKey key, Potion potion) {
        this.key = key;
        this.potion = potion;
        this.potionEffects = Suppliers.memoize(() -> potion.getEffects().stream().map(CardboardPotionUtil::toBukkit).toList());
        
        this.upgradeable = Suppliers.ofInstance(false);
        this.extendable = Suppliers.ofInstance(false);
        
        // this.upgradeable = Suppliers.memoize(() -> Registry.POTION.get(new NamespacedKey(key.getNamespace(), "strong_" + key.getKey())) != null);
        // this.extendable = Suppliers.memoize(() -> Registry.POTION.get(new NamespacedKey(key.getNamespace(), "long_" + key.getKey())) != null);
        this.maxLevel = Suppliers.memoize(() -> this.isUpgradeable() ? 2 : 1);
    }

    //@Override
    public PotionEffectType getEffectType() {
        return this.getPotionEffects().isEmpty() ? null : this.getPotionEffects().get(0).getType();
    }

    //@Override
    public List<PotionEffect> getPotionEffects() {
        return this.potionEffects.get();
    }

    //@Override
    public boolean isInstant() {
        return this.potion.hasInstantEffect();
    }

    //@Override
    public boolean isUpgradeable() {
        return this.upgradeable.get();
    }

    //@Override
    public boolean isExtendable() {
        return this.extendable.get();
    }

    //@Override
    public int getMaxLevel() {
        return this.maxLevel.get();
    }
}
