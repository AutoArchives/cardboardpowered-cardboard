package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import org.bukkit.entity.EntityFactory;
import org.bukkit.entity.EntitySnapshot;

public class CraftEntityFactory implements EntityFactory {

    private static final CraftEntityFactory instance = new CraftEntityFactory();

    private CraftEntityFactory() {
    }

    public EntitySnapshot createEntitySnapshot(String input) {
        NbtCompound tag;
        Preconditions.checkArgument(input != null, "Input string cannot be null");
        try {
            tag = StringNbtReader.parse(input);
        }  catch (CommandSyntaxException e2) {
            throw new IllegalArgumentException("Could not parse Entity: " + input, e2);
        }
        EntityType type = EntityType.fromNbt(tag).orElse(null);
        if (type == null) {
            throw new IllegalArgumentException("Could not parse Entity: " + input);
        }
        return CraftEntitySnapshot.create(tag, CraftEntityType.minecraftToBukkit(type));
    }

    public static CraftEntityFactory instance() {
        return instance;
    }

}
