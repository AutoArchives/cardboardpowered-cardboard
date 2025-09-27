package org.cardboardpowered.impl;

import io.papermc.paper.InternalAPIBridge;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.world.damagesource.CombatEntry;
import io.papermc.paper.world.damagesource.FallLocationType;
import io.papermc.paper.world.damagesource.PaperCombatEntryWrapper;
import io.papermc.paper.world.damagesource.PaperCombatTrackerWrapper;
import java.util.function.Predicate;

import net.minecraft.command.PermissionLevelSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageRecord;
import net.minecraft.entity.damage.FallLocation;
import net.minecraft.util.Nullables;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.block.CraftBiome;
import org.bukkit.craftbukkit.damage.CraftDamageEffect;
import org.bukkit.craftbukkit.damage.CraftDamageSource;
// import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.damage.DamageEffect;
import org.bukkit.damage.DamageSource;
import org.cardboardpowered.impl.entity.LivingEntityImpl;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class PaperServerInternalAPIBridge implements InternalAPIBridge {

    public static final PaperServerInternalAPIBridge INSTANCE = new PaperServerInternalAPIBridge();

    public DamageEffect getDamageEffect(String key) {
        return CraftDamageEffect.getById(key);
    }

    public Biome constructLegacyCustomBiome() {
        class Holder {
            static final Biome LEGACY_CUSTOM = new CraftBiome.LegacyCustomBiomeImpl();

            Holder(PaperServerInternalAPIBridge this$0) {
            }
        }
        return Holder.LEGACY_CUSTOM;
    }

    public CombatEntry createCombatEntry(org.bukkit.entity.LivingEntity entity, DamageSource damageSource, float damage) {
        LivingEntity mob = ((LivingEntityImpl)entity).getHandle();
        FallLocation fallLocation = FallLocation.fromEntity(mob);
        return this.createCombatEntry(((CraftDamageSource)damageSource).getHandle(), damage, fallLocation, (float)mob.fallDistance);
    }

    public CombatEntry createCombatEntry(DamageSource damageSource, float damage, @Nullable FallLocationType fallLocationType, float fallDistance) {
        return this.createCombatEntry(((CraftDamageSource)damageSource).getHandle(), damage, Nullables.map(fallLocationType, PaperCombatTrackerWrapper::paperToMinecraft), fallDistance);
    }

    private CombatEntry createCombatEntry(net.minecraft.entity.damage.DamageSource damageSource, float damage, @Nullable FallLocation fallLocation, float fallDistance) {
        return new PaperCombatEntryWrapper(new DamageRecord(damageSource, damage, fallLocation, fallDistance));
    }

    public Predicate<CommandSourceStack> restricted(Predicate<CommandSourceStack> predicate) {
        record RestrictedPredicate(Predicate<CommandSourceStack> predicate) implements Predicate<CommandSourceStack>
        // , PermissionLevelSource.RestrictedMarker
        {
            @Override
            public boolean test(CommandSourceStack commandSourceStack) {
                return this.predicate.test(commandSourceStack);
            }
        }
        return new RestrictedPredicate(predicate);
    }

}