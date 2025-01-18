package io.papermc.paper.adventure;

import java.util.Optional;

import org.cardboardpowered.adventure.CardboardAdventure;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.kyori.adventure.key.Key;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

// TODO
public class PaperAdventure extends CardboardAdventure {

	public static RegistryEntry<SoundEvent> resolveSound(Key key) {
        Identifier id = PaperAdventure.asVanilla(key);
        Optional<RegistryEntry.Reference<SoundEvent>> vanilla = Registries.SOUND_EVENT.getEntry(id);
        if (vanilla.isPresent()) {
            return vanilla.get();
        }
        return RegistryEntry.of(SoundEvent.of(id));
    }

	public static <T> RegistryKey<T> asVanilla(RegistryKey<? extends Registry<T>> registry, Key key) {
        return RegistryKey.of(registry, PaperAdventure.asVanilla(key));
    }

}