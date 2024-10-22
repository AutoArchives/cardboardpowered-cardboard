package io.papermc.paper.registry.data.util;

import com.mojang.serialization.JavaOps;
// import io.papermc.paper.adventure.WrapperAwareSerializer;
import net.kyori.adventure.text.Component;
import net.minecraft.registry.RegistryOps;
import net.minecraft.text.Text;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.Contract;

import org.cardboardpowered.adventure.WrapperAwareSerializer;

@DefaultQualifier(value=NonNull.class)
public class Conversions {

    private final RegistryOps.RegistryInfoGetter lookup;
    private final WrapperAwareSerializer serializer;

    public Conversions(RegistryOps.RegistryInfoGetter lookup) {
        this.lookup = lookup;
        this.serializer = new WrapperAwareSerializer(() -> RegistryOps.of(JavaOps.INSTANCE, lookup));
    }

    public RegistryOps.RegistryInfoGetter lookup() {
        return this.lookup;
    }

    @Contract(value="null -> null; !null -> !null")
    public @Nullable Text asVanilla(@Nullable Component adventure) {
        if (adventure == null) {
            return null;
        }
        return this.serializer.serialize(adventure);
    }

    public Component asAdventure(@Nullable Text vanilla) {
        return vanilla == null ? Component.empty() : this.serializer.deserialize(vanilla);
    }

}