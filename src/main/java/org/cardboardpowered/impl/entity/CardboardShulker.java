package org.cardboardpowered.impl.entity;

import java.lang.reflect.Field;
import net.minecraft.network.syncher.EntityDataAccessor;
import org.bukkit.DyeColor;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Shulker;
import org.jetbrains.annotations.NotNull;

public class CardboardShulker extends CardboardGolem implements Shulker {

    public CardboardShulker(CraftServer server, net.minecraft.world.entity.monster.Shulker entity) {
        super(server, entity);
    }

    @Override
    public String toString() {
        return "Shulker";
    }

    @Override
    public EntityType getType() {
        return EntityType.SHULKER;
    }

    @Override
    public net.minecraft.world.entity.monster.Shulker getHandle() {
        return (net.minecraft.world.entity.monster.Shulker) nms;
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
    @Override
    public DyeColor getColor() {
        EntityDataAccessor<Byte> d = null;
        try {
            Field f = net.minecraft.world.entity.monster.Shulker.class.getDeclaredField("field_7343");
            f.setAccessible(true);
            d = (EntityDataAccessor<Byte>) f.get(null);
        } catch (Exception e) {
        }
        return DyeColor.getByWoolData(getHandle().getEntityData().get(d));
    }

    @SuppressWarnings({ "unchecked", "deprecation" })
    @Override
    public void setColor(DyeColor color) {
        EntityDataAccessor<Byte> d = null;
        try {
            Field f = net.minecraft.world.entity.monster.Shulker.class.getDeclaredField("field_7343");
            f.setAccessible(true);
            d = (EntityDataAccessor<Byte>) f.get(null);
        } catch (Exception e) {
        }
        getHandle().getEntityData().set(d, (color == null) ? 16 : color.getWoolData());
    }

    @Override
    public @NotNull BlockFace getAttachedFace() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public float getPeek() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setAttachedFace(@NotNull BlockFace arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setPeek(float arg0) {
        // TODO Auto-generated method stub
        
    }

}