package org.bukkit.craftbukkit.generator.structure;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.Handleable;

public class CraftStructureType extends org.bukkit.generator.structure.StructureType implements Handleable<StructureType<?>> {

    private final NamespacedKey key;
    private final StructureType<?> structureType;

    public static org.bukkit.generator.structure.StructureType minecraftToBukkit(StructureType<?> minecraft) {
        return (org.bukkit.generator.structure.StructureType)CraftRegistry.minecraftToBukkit(minecraft, Registries.STRUCTURE_TYPE);
    }

    public static StructureType<?> bukkitToMinecraft(org.bukkit.generator.structure.StructureType bukkit) {
        return (StructureType)CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public CraftStructureType(NamespacedKey key, StructureType<?> structureType) {
        this.key = key;
        this.structureType = structureType;
    }

    @Override
    public StructureType<?> getHandle() {
        return this.structureType;
    }

    public NamespacedKey getKey() {
        return this.key;
    }

}

