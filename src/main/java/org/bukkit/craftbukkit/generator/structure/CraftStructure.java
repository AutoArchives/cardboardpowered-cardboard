package org.bukkit.craftbukkit.generator.structure;

import net.minecraft.registry.RegistryKeys;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.generator.structure.CraftStructureType;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructureType;

public class CraftStructure extends Structure implements Handleable<net.minecraft.world.gen.structure.Structure> {

    private final NamespacedKey key;
    private final net.minecraft.world.gen.structure.Structure structure;
    private final StructureType structureType;

    public static Structure minecraftToBukkit(net.minecraft.world.gen.structure.Structure minecraft) {
        return (Structure)CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.STRUCTURE, Registry.STRUCTURE);
    }

    public static net.minecraft.world.gen.structure.Structure bukkitToMinecraft(Structure bukkit) {
        return (net.minecraft.world.gen.structure.Structure)CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public CraftStructure(NamespacedKey key, net.minecraft.world.gen.structure.Structure structure) {
        this.key = key;
        this.structure = structure;
        this.structureType = CraftStructureType.minecraftToBukkit(structure.getType());
    }

    @Override
    public net.minecraft.world.gen.structure.Structure getHandle() {
        return this.structure;
    }

    public StructureType getStructureType() {
        return this.structureType;
    }

    public NamespacedKey getKey() {
        return this.key;
    }

}