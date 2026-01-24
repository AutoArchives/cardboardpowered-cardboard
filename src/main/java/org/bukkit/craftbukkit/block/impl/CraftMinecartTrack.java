/**
 * Automatically generated file, changes will be lost.
 */
package org.bukkit.craftbukkit.block.impl;

import net.minecraft.world.level.block.state.properties.BooleanProperty;

public final class CraftMinecartTrack extends org.bukkit.craftbukkit.block.data.CraftBlockData implements org.bukkit.block.data.Rail {

    public CraftMinecartTrack() {
        super();
    }

    public CraftMinecartTrack(net.minecraft.world.level.block.state.BlockState state) {
        super(state);
    }

    // org.bukkit.craftbukkit.block.data.CraftRail

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> SHAPE = getEnum(net.minecraft.world.level.block.RailBlock.class, "shape");

    @Override
    public Shape getShape() {
        return get(SHAPE, Shape.class);
    }

    @Override
    public void setShape(Shape shape) {
        set(SHAPE, shape);
    }

    @Override
    public java.util.Set<Shape> getShapes() {
        return getValues(SHAPE, Shape.class);
    }

    // org.bukkit.craftbukkit.block.data.CraftWaterlogged

    private static final BooleanProperty WATERLOGGED = getBoolean(net.minecraft.world.level.block.RailBlock.class, "waterlogged");

    @Override
    public boolean isWaterlogged() {
        return get(CraftMinecartTrack.WATERLOGGED);
    }

    @Override
    public void setWaterlogged(boolean waterlogged) {
        set(CraftMinecartTrack.WATERLOGGED, waterlogged);
    }

}
