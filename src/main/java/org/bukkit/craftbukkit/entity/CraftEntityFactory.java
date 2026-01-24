package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
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
        CompoundTag tag;
        Preconditions.checkArgument(input != null, "Input string cannot be null");
        try {
            tag = TagParser.parseCompoundFully(input);
        }  catch (CommandSyntaxException e2) {
            throw new IllegalArgumentException("Could not parse Entity: " + input, e2);
        }
        
        EntityType<?> type;
        try (ProblemReporter.ScopedCollector problemReporter = new ProblemReporter.ScopedCollector(() -> "createEntitySnapshot", LOGGER);){
            type = EntityType.by(NbtReadView_createGlobal(problemReporter, tag)).orElse(null);
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
    public static ValueInput NbtReadView_createGlobal(ProblemReporter problemReporter, CompoundTag compoundTag) {
        return TagValueInput.create(problemReporter, CraftServer.server.registryAccess(), compoundTag);
    }

}
