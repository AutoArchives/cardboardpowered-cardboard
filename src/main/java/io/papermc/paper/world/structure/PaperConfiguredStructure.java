package io.papermc.paper.world.structure;

import io.papermc.paper.world.structure.ConfiguredStructure;
import java.util.Objects;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.structure.Structure;
import org.bukkit.NamespacedKey;
import org.bukkit.StructureType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(value=NonNull.class)
@Deprecated(forRemoval=true)
public final class PaperConfiguredStructure {

    private PaperConfiguredStructure() {
    }

    public static @Nullable ConfiguredStructure minecraftToBukkit(NamespacedKey key, Structure nms) {
        Identifier structureTypeLoc = Objects.requireNonNull(Registries.STRUCTURE_TYPE.getId(nms.getType()), "unexpected structure type " + String.valueOf(nms.getType()));
        @Nullable StructureType structureType = (StructureType)StructureType.getStructureTypes().get(structureTypeLoc.getPath());
        return structureType == null ? null : new ConfiguredStructure(key, structureType);
    }

}