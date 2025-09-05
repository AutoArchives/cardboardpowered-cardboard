package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;

import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.ReadView;
import net.minecraft.util.ErrorReporter;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityFactory;
import org.bukkit.entity.EntitySnapshot;
import org.slf4j.Logger;

public class CraftEntityFactory implements EntityFactory {

	private static final Logger LOGGER = LogUtils.getLogger();
    private static final CraftEntityFactory instance = new CraftEntityFactory();

    private CraftEntityFactory() {
    }

    public EntitySnapshot createEntitySnapshot(String input) {
        NbtCompound tag;
        Preconditions.checkArgument(input != null, "Input string cannot be null");
        try {
            tag = StringNbtReader.readCompound(input);
        }  catch (CommandSyntaxException e2) {
            throw new IllegalArgumentException("Could not parse Entity: " + input, e2);
        }
        
        EntityType<?> type;
        try (ErrorReporter.Logging problemReporter = new ErrorReporter.Logging(() -> "createEntitySnapshot", LOGGER);){
            type = EntityType.fromData(NbtReadView_createGlobal(problemReporter, tag)).orElse(null);
        }

        if (type == null) {
            throw new IllegalArgumentException("Could not parse Entity: " + input);
        }
        return CraftEntitySnapshot.create(tag, CraftEntityType.minecraftToBukkit(type));
    }

    public static CraftEntityFactory instance() {
        return instance;
    }
    
    // TODO: Move to NbtReadView
    public static ReadView NbtReadView_createGlobal(ErrorReporter problemReporter, NbtCompound compoundTag) {
        return NbtReadView.create(problemReporter, CraftServer.server.getRegistryManager(), compoundTag);
    }

}
