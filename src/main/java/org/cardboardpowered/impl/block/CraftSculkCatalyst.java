package org.cardboardpowered.impl.block;

import com.google.common.base.Preconditions;
import io.papermc.paper.math.Position;
import net.minecraft.block.entity.SculkCatalystBlockEntity;
import net.minecraft.util.math.BlockPos;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.SculkCatalyst;
import org.jetbrains.annotations.NotNull;

public class CraftSculkCatalyst extends CardboardBlockEntityState<SculkCatalystBlockEntity> implements SculkCatalyst {

    public CraftSculkCatalyst(World world, SculkCatalystBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CraftSculkCatalyst(CraftSculkCatalyst state, Location location) {
        super(state, location);
    }

    public void bloom(Block block, int charge) {
        Preconditions.checkArgument((block != null ? 1 : 0) != 0, (Object)"block cannot be null");
        Preconditions.checkArgument((charge > 0 ? 1 : 0) != 0, (Object)"charge must be positive");
        this.requirePlaced();
        // TODO
        // ((SculkCatalystBlockEntity)this.getTileEntity()).getEventListener().bloom(this.world.getHandle(), this.getPosition(), this.getHandle(), this.world.getHandle().getRandom());
        ((SculkCatalystBlockEntity)this.getTileEntity()).getEventListener().getSpreadManager().spread(new BlockPos(block.getX(), block.getY(), block.getZ()), charge);
    }

    @Override
    public CraftSculkCatalyst copy() {
        return new CraftSculkCatalyst(this, null);
    }

    @Override
    public CraftSculkCatalyst copy(Location location) {
        return new CraftSculkCatalyst(this, location);
    }

    public void bloom(@NotNull Position position, int charge) {
        Preconditions.checkNotNull((Object)position);
        this.requirePlaced();
        // TODO
        // ((SculkCatalystBlockEntity)this.getTileEntity()).getEventListener().bloom(this.world.getHandle(), ((SculkCatalystBlockEntity)this.getTileEntity()).getPos(), ((SculkCatalystBlockEntity)this.getTileEntity()).getCachedState(), this.world.getHandle().getRandom());
        // ((SculkCatalystBlockEntity)this.getTileEntity()).getEventListener().getSpreadManager().spread(MCUtil.toBlockPos(position), charge);
    }
}