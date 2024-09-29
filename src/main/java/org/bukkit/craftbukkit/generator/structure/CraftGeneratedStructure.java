package org.bukkit.craftbukkit.generator.structure;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import net.minecraft.structure.StructureStart;
import org.bukkit.generator.structure.GeneratedStructure;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructurePiece;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.BoundingBox;

public class CraftGeneratedStructure implements GeneratedStructure {

    private final StructureStart handle;
    private List<StructurePiece> pieces;

    public CraftGeneratedStructure(StructureStart handle) {
        this.handle = handle;
    }

    @Override
    public BoundingBox getBoundingBox() {
        net.minecraft.util.math.BlockBox bb = this.handle.getBoundingBox();
        return new BoundingBox(bb.getMinX(), bb.getMinY(), bb.getMinZ(), bb.getMaxX(), bb.getMaxY(), bb.getMaxZ());
    }

    @Override
    public Structure getStructure() {
        return CraftStructure.minecraftToBukkit(this.handle.getStructure());
    }

    @Override
    public Collection<StructurePiece> getPieces() {
        if (this.pieces == null) { // Cache the pieces on first request
            ImmutableList.Builder<StructurePiece> builder = new ImmutableList.Builder<>();
            for (net.minecraft.structure.StructurePiece piece : this.handle.getChildren()) {
                builder.add(new CraftStructurePiece(piece));
            }

            this.pieces = builder.build();
        }

        return this.pieces;
    }

    @Override
    public PersistentDataContainer getPersistentDataContainer() {
    	// TODO
        // return this.handle.persistentDataContainer;
        return null;
    }
}
