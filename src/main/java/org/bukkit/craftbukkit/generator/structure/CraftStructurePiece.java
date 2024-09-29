package org.bukkit.craftbukkit.generator.structure;

import org.bukkit.generator.structure.StructurePiece;
import org.bukkit.util.BoundingBox;

public class CraftStructurePiece implements StructurePiece {

    private final net.minecraft.structure.StructurePiece handle;

    public CraftStructurePiece(net.minecraft.structure.StructurePiece handle) {
        this.handle = handle;
    }

    @Override
    public BoundingBox getBoundingBox() {
        net.minecraft.util.math.BlockBox bb = this.handle.getBoundingBox();
        return new BoundingBox(bb.getMinX(), bb.getMinY(), bb.getMinZ(), bb.getMaxX(), bb.getMaxY(), bb.getMaxZ());
    }

}