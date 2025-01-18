package io.papermc.paper.datacomponent.item;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.type.BlockStateComponent;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.util.Handleable;
import org.cardboardpowered.interfaces.IBlockState;

public record PaperBlockItemDataProperties(
    BlockStateComponent impl
) implements BlockItemDataProperties, Handleable<BlockStateComponent> {

    @Override
    public BlockData createBlockData(final BlockType blockType) {
        final Block block = CraftBlockType.bukkitToMinecraftNew(blockType);
        final BlockState defaultState = block.getDefaultState();
        return ( (IBlockState) this.impl.applyToState(defaultState) ).createCraftBlockData();
    }

    @Override
    public BlockData applyTo(final BlockData blockData) {
        final BlockState state = ((CraftBlockData) blockData).getState();
        return ( (IBlockState) this.impl.applyToState(state) ).createCraftBlockData();
    }

    @Override
    public BlockStateComponent getHandle() {
        return this.impl;
    }

    static final class BuilderImpl implements BlockItemDataProperties.Builder {

        private final Map<String, String> properties = new Object2ObjectOpenHashMap<>();

        // TODO when BlockProperty API is merged

        @Override
        public BlockItemDataProperties build() {
            if (this.properties.isEmpty()) {
                return new PaperBlockItemDataProperties(BlockStateComponent.DEFAULT);
            }
            return new PaperBlockItemDataProperties(new BlockStateComponent(new Object2ObjectOpenHashMap<>(this.properties)));
        }
    }
}
