package io.papermc.paper.datacomponent.item;

import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.registry.PaperRegistries;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.PaperRegistrySets;
import io.papermc.paper.registry.set.RegistryKeySet;
import java.util.Optional;
import java.util.function.Function;
import net.kyori.adventure.key.Key;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.cardboardpowered.Registries_Bridge;
import org.checkerframework.checker.nullness.qual.Nullable;

public record PaperEquippable(
    net.minecraft.component.type.EquippableComponent impl
) implements Equippable, Handleable<net.minecraft.component.type.EquippableComponent> {

    @Override
    public net.minecraft.component.type.EquippableComponent getHandle() {
        return this.impl;
    }

    @Override
    public EquipmentSlot slot() {
        return CraftEquipmentSlot.getSlot(this.impl.slot());
    }

    @Override
    public Key equipSound() {
        return PaperAdventure.asAdventure(this.impl.equipSound().value().id());
    }

    @Override
    public @Nullable Key assetId() {
        return this.impl.assetId()
            .map(PaperAdventure::asAdventureKey)
            .orElse(null);
    }

    @Override
    public @Nullable Key cameraOverlay() {
        return this.impl.cameraOverlay()
            .map(PaperAdventure::asAdventure)
            .orElse(null);
    }

    @Override
    public @Nullable RegistryKeySet<EntityType> allowedEntities() {
        return this.impl.allowedEntities()
            .map((set) -> PaperRegistrySets.convertToApi(RegistryKey.ENTITY_TYPE, set))
            .orElse(null);
    }

    @Override
    public boolean dispensable() {
        return this.impl.dispensable();
    }

    @Override
    public boolean swappable() {
        return this.impl.swappable();
    }

    @Override
    public boolean damageOnHurt() {
        return this.impl.damageOnHurt();
    }

    @Override
    public Builder toBuilder() {
        return new BuilderImpl(this.slot())
            .equipSound(this.equipSound())
            .assetId(this.assetId())
            .cameraOverlay(this.cameraOverlay())
            .allowedEntities(this.allowedEntities())
            .dispensable(this.dispensable())
            .swappable(this.swappable())
            .damageOnHurt(this.damageOnHurt());
    }


    static final class BuilderImpl implements Builder {

        private final net.minecraft.entity.EquipmentSlot equipmentSlot;
        private RegistryEntry<SoundEvent> equipSound = SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
        private Optional<net.minecraft.registry.RegistryKey<EquipmentAsset>> assetId = Optional.empty();
        private Optional<Identifier> cameraOverlay = Optional.empty();
        private Optional<RegistryEntryList<net.minecraft.entity.EntityType<?>>> allowedEntities = Optional.empty();
        private boolean dispensable = true;
        private boolean swappable = true;
        private boolean damageOnHurt = true;

        BuilderImpl(final EquipmentSlot equipmentSlot) {
            this.equipmentSlot = CraftEquipmentSlot.getNMS(equipmentSlot);
        }

        @Override
        public Builder equipSound(final Key sound) {
            this.equipSound = PaperAdventure.resolveSound(sound);
            return this;
        }

        @Override
        public Builder assetId(final @Nullable Key assetId) {
            this.assetId = Optional.ofNullable(assetId)
                .map(key -> PaperAdventure.asVanilla(EquipmentAssetKeys.REGISTRY_KEY, key));

            return this;
        }

        @Override
        public Builder cameraOverlay(@Nullable final Key cameraOverlay) {
            this.cameraOverlay = Optional.ofNullable(cameraOverlay)
                .map(PaperAdventure::asVanilla);

            return this;
        }

        @Override
        public Builder allowedEntities(final @Nullable RegistryKeySet<EntityType> allowedEntities) {
            this.allowedEntities = Optional.ofNullable(allowedEntities)
                .map((set) -> PaperRegistrySets.convertToNms(RegistryKeys.ENTITY_TYPE, Registries_Bridge.BUILT_IN_CONVERSIONS.lookup(), set));
            return this;
        }

        @Override
        public Builder dispensable(final boolean dispensable) {
            this.dispensable = dispensable;
            return this;
        }

        @Override
        public Builder swappable(final boolean swappable) {
            this.swappable = swappable;
            return this;
        }

        @Override
        public Builder damageOnHurt(final boolean damageOnHurt) {
            this.damageOnHurt = damageOnHurt;
            return this;
        }

        @Override
        public Equippable build() {
            return new PaperEquippable(
                new net.minecraft.component.type.EquippableComponent(
                    this.equipmentSlot,
                    this.equipSound,
                    this.assetId,
                    this.cameraOverlay,
                    this.allowedEntities,
                    this.dispensable,
                    this.swappable,
                    this.damageOnHurt
                )
            );
        }
    }
}
