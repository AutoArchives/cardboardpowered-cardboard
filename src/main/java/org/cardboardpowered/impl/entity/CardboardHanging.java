package org.cardboardpowered.impl.entity;

import net.kyori.adventure.text.Component;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.decoration.HangingEntity;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.jetbrains.annotations.Nullable;

public class CardboardHanging extends CraftEntity implements Hanging {

    public CardboardHanging(CraftServer server, HangingEntity entity) {
        super(entity);
    }

    @Override
    public BlockFace getAttachedFace() {
        return getFacing().getOppositeFace();
    }

    @Override
    public void setFacingDirection(BlockFace face) {
        setFacingDirection(face, false);
    }

    @Override
    public boolean setFacingDirection(BlockFace face, boolean force) {
        HangingEntity hanging = getHandle();
        Direction dir = hanging.getDirection();
        switch (face) {
            case SOUTH:
            default:
                getHandle().setDirection(Direction.SOUTH);
                break;
            case WEST:
                getHandle().setDirection(Direction.WEST);
                break;
            case NORTH:
                getHandle().setDirection(Direction.NORTH);
                break;
            case EAST:
                getHandle().setDirection(Direction.EAST);
                break;
        }
        if (!force && !hanging.survives()) {
            hanging.setDirection(dir); // Revert since it doesn't fit
            return false;
        }
        return true;
    }

    @Override
    public BlockFace getFacing() {
        Direction direction = this.getHandle().getDirection();
        if (direction == null) return BlockFace.SELF;
        return CraftBlock.notchToBlockFace(direction);
    }

    @Override
    public HangingEntity getHandle() {
        return (HangingEntity) nms;
    }

    @Override
    public String toString() {
        return "CardboardHangingEntity";
    }

    @Override
    public EntityType getType() {
        return EntityType.UNKNOWN;
    }

    @Override
    public @Nullable Component customName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void customName(@Nullable Component arg0) {
        // TODO Auto-generated method stub
        
    }

}