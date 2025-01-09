package org.bukkit.craftbukkit.inventory.components;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.CraftSound;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.bukkit.craftbukkit.inventory.SerializableMeta;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.components.EquippableComponent;
import org.cardboardpowered.impl.tag.EntityTagImpl;

@SerializableAs("Equippable")
public final class CraftEquippableComponent implements EquippableComponent {

    private net.minecraft.component.type.EquippableComponent handle;

    public CraftEquippableComponent(net.minecraft.component.type.EquippableComponent handle) {
        this.handle = handle;
    }

    public CraftEquippableComponent(CraftEquippableComponent craft) {
        this.handle = craft.handle;
    }

    public CraftEquippableComponent(Map<String, Object> map) {
        net.minecraft.entity.EquipmentSlot slot = CraftEquipmentSlot.getNMS(EquipmentSlot.valueOf(SerializableMeta.getString(map, "slot", false)));

        Sound equipSound = null;
        String snd = SerializableMeta.getString(map, "equip-sound", true);
        if (snd != null) {
            equipSound = Registry.SOUNDS.get(NamespacedKey.fromString(snd));
        }

        String model = SerializableMeta.getString(map, "model", true);
        String cameraOverlay = SerializableMeta.getString(map, "camera-overlay", true);

        RegistryEntryList<net.minecraft.entity.EntityType<?>> allowedEntities = null;
        Object allowed = SerializableMeta.getObject(Object.class, map, "allowed-entities", true);
        if (allowed != null) {
            allowedEntities = CraftHolderUtil.parse(allowed, RegistryKeys.ENTITY_TYPE, Registries.ENTITY_TYPE);
        }

        Boolean dispensable = SerializableMeta.getObject(Boolean.class, map, "dispensable", true);
        Boolean swappable = SerializableMeta.getObject(Boolean.class, map, "swappable", true);
        Boolean damageOnHurt = SerializableMeta.getObject(Boolean.class, map, "damage-on-hurt", true);

        this.handle = new net.minecraft.component.type.EquippableComponent(slot,
                (equipSound != null) ? CraftSound.bukkitToMinecraftHolder(equipSound) : SoundEvents.ITEM_ARMOR_EQUIP_GENERIC,
                Optional.ofNullable(model).map(Identifier::of).map((k) -> RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, k)),
                Optional.ofNullable(cameraOverlay).map(Identifier::of),
                Optional.ofNullable(allowedEntities),
                (dispensable != null) ? dispensable : true,
                (swappable != null) ? swappable : true,
                (damageOnHurt != null) ? damageOnHurt : true
        );
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("slot", this.getSlot().name());
        result.put("equip-sound", this.getEquipSound().getKey().toString());

        NamespacedKey model = this.getModel();
        if (model != null) {
            result.put("model", model.toString());
        }

        NamespacedKey cameraOverlay = this.getCameraOverlay();
        if (cameraOverlay != null) {
            result.put("camera-overlay", cameraOverlay.toString());
        }

        Optional<RegistryEntryList<net.minecraft.entity.EntityType<?>>> allowed = this.handle.allowedEntities();
        if (allowed.isPresent()) {
            CraftHolderUtil.serialize(result, "allowed-entities", allowed.get());
        }

        result.put("dispensable", this.isDispensable());
        result.put("swappable", this.isSwappable());
        result.put("damage-on-hurt", this.isDamageOnHurt());

        return result;
    }

    public net.minecraft.component.type.EquippableComponent getHandle() {
        return this.handle;
    }

    @Override
    public EquipmentSlot getSlot() {
        return CraftEquipmentSlot.getSlot(this.handle.slot());
    }

    @Override
    public void setSlot(EquipmentSlot slot) {
        this.handle = new net.minecraft.component.type.EquippableComponent(CraftEquipmentSlot.getNMS(slot), this.handle.equipSound(), this.handle.assetId(), this.handle.cameraOverlay(), this.handle.allowedEntities(), this.handle.dispensable(), this.handle.swappable(), this.handle.damageOnHurt());
    }

    @Override
    public Sound getEquipSound() {
        return CraftSound.minecraftToBukkit(this.handle.equipSound().value());
    }

    @Override
    public void setEquipSound(Sound sound) {
        this.handle = new net.minecraft.component.type.EquippableComponent(this.handle.slot(), (sound != null) ? CraftSound.bukkitToMinecraftHolder(sound) : SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, this.handle.assetId(), this.handle.cameraOverlay(), this.handle.allowedEntities(), this.handle.dispensable(), this.handle.swappable(), this.handle.damageOnHurt());
    }

    @Override
    public NamespacedKey getModel() {
        return this.handle.assetId().map((a) -> CraftNamespacedKey.fromMinecraft(a.getValue())).orElse(null);
    }

    @Override
    public void setModel(NamespacedKey key) {
        this.handle = new net.minecraft.component.type.EquippableComponent(this.handle.slot(), this.handle.equipSound(), Optional.ofNullable(key).map(CraftNamespacedKey::toMinecraft).map((k) -> RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, k)), this.handle.cameraOverlay(), this.handle.allowedEntities(), this.handle.dispensable(), this.handle.swappable(), this.handle.damageOnHurt());
    }

    @Override
    public NamespacedKey getCameraOverlay() {
        return this.handle.cameraOverlay().map(CraftNamespacedKey::fromMinecraft).orElse(null);
    }

    @Override
    public void setCameraOverlay(NamespacedKey key) {
        this.handle = new net.minecraft.component.type.EquippableComponent(this.handle.slot(), this.handle.equipSound(), this.handle.assetId(), Optional.ofNullable(key).map(CraftNamespacedKey::toMinecraft), this.handle.allowedEntities(), this.handle.dispensable(), this.handle.swappable(), this.handle.damageOnHurt());
    }

    @Override
    public Collection<EntityType> getAllowedEntities() {
        return this.handle.allowedEntities().map(RegistryEntryList::stream).map((stream) -> stream.map(RegistryEntry::value).map(CraftEntityType::minecraftToBukkit).collect(Collectors.toList())).orElse(null);
    }

    @Override
    public void setAllowedEntities(EntityType entities) {
        this.handle = new net.minecraft.component.type.EquippableComponent(this.handle.slot(), this.handle.equipSound(), this.handle.assetId(), this.handle.cameraOverlay(),
                (entities != null) ? Optional.of(RegistryEntryList.of(CraftEntityType.bukkitToMinecraftHolder(entities))) : Optional.empty(),
                this.handle.dispensable(), this.handle.swappable(), this.handle.damageOnHurt()
        );
    }

    @Override
    public void setAllowedEntities(Collection<EntityType> entities) {
        this.handle = new net.minecraft.component.type.EquippableComponent(this.handle.slot(), this.handle.equipSound(), this.handle.assetId(), this.handle.cameraOverlay(),
                (entities != null) ? Optional.of(RegistryEntryList.of(entities.stream().map(CraftEntityType::bukkitToMinecraftHolder).collect(Collectors.toList()))) : Optional.empty(),
                this.handle.dispensable(), this.handle.swappable(), this.handle.damageOnHurt()
        );
    }

    @Override
    public void setAllowedEntities(Tag<EntityType> tag) {
        Preconditions.checkArgument(tag == null || tag instanceof EntityTagImpl, "tag must be an entity tag"); // Paper

        this.handle = new net.minecraft.component.type.EquippableComponent(this.handle.slot(), this.handle.equipSound(), this.handle.assetId(), this.handle.cameraOverlay(),
                (tag != null) ? Optional.of(((EntityTagImpl) tag).getHandle()) : Optional.empty(),
                this.handle.dispensable(), this.handle.swappable(), this.handle.damageOnHurt()
        );
    }

    @Override
    public boolean isDispensable() {
        return this.handle.dispensable();
    }

    @Override
    public void setDispensable(boolean dispensable) {
        this.handle = new net.minecraft.component.type.EquippableComponent(this.handle.slot(), this.handle.equipSound(), this.handle.assetId(), this.handle.cameraOverlay(), this.handle.allowedEntities(), dispensable, this.handle.swappable(), this.handle.damageOnHurt());
    }

    @Override
    public boolean isSwappable() {
        return this.handle.swappable();
    }

    @Override
    public void setSwappable(boolean swappable) {
        this.handle = new net.minecraft.component.type.EquippableComponent(this.handle.slot(), this.handle.equipSound(), this.handle.assetId(), this.handle.cameraOverlay(), this.handle.allowedEntities(), this.handle.dispensable(), swappable, this.handle.damageOnHurt());
    }

    @Override
    public boolean isDamageOnHurt() {
        return this.handle.damageOnHurt();
    }

    @Override
    public void setDamageOnHurt(boolean damage) {
        this.handle = new net.minecraft.component.type.EquippableComponent(this.handle.slot(), this.handle.equipSound(), this.handle.assetId(), this.handle.cameraOverlay(), this.handle.allowedEntities(), this.handle.dispensable(), this.handle.swappable(), damage);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final CraftEquippableComponent other = (CraftEquippableComponent) obj;
        return Objects.equals(this.handle, other.handle);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.handle);
        return hash;
    }

    @Override
    public String toString() {
        return "CraftEquippableComponent{" + "handle=" + this.handle + '}';
    }
}
