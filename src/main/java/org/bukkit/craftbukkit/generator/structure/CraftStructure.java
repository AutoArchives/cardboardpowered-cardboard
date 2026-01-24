package org.bukkit.craftbukkit.generator.structure;

import net.minecraft.core.registries.Registries;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.generator.structure.CraftStructureType;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructureType;

public class CraftStructure extends Structure implements Handleable<net.minecraft.world.level.levelgen.structure.Structure> {

    private final NamespacedKey key;
    private final net.minecraft.world.level.levelgen.structure.Structure structure;
    private final StructureType structureType;

    public static Structure minecraftToBukkit(net.minecraft.world.level.levelgen.structure.Structure minecraft) {
        return (Structure)CraftRegistry.minecraftToBukkit(minecraft, Registries.STRUCTURE);
    }

    public static net.minecraft.world.level.levelgen.structure.Structure bukkitToMinecraft(Structure bukkit) {
        return (net.minecraft.world.level.levelgen.structure.Structure)CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public CraftStructure(NamespacedKey key, net.minecraft.world.level.levelgen.structure.Structure structure) {
        this.key = key;
        this.structure = structure;
        this.structureType = CraftStructureType.minecraftToBukkit(structure.type());
    }

    @Override
    public net.minecraft.world.level.levelgen.structure.Structure getHandle() {
        return this.structure;
    }

    public StructureType getStructureType() {
        return this.structureType;
    }

    public NamespacedKey getKey() {
        return this.key;
    }

}