package org.cardboardpowered.impl.entity;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class CardboardMinecart extends CraftVehicle implements Minecart {

    public CardboardMinecart(CraftServer server, AbstractMinecartEntity entity) {
        super(server, entity);
    }

    @Override
    public void setDamage(double damage) {
        getHandle().setDamageWobbleStrength((float) damage);
    }

    @Override
    public double getDamage() {
        return getHandle().getDamageWobbleStrength();
    }

    @Override
    public double getMaxSpeed() {
        return -1; // TODO
    }

    @Override
    public void setMaxSpeed(double speed) {
        // TODO
    }

    @Override
    public boolean isSlowWhenEmpty() {
        return false; // TODO
    }

    @Override
    public void setSlowWhenEmpty(boolean slow) {
        // TODO
    }

    @Override
    public Vector getFlyingVelocityMod() {
        return null; // TODO
    }

    @Override
    public void setFlyingVelocityMod(Vector flying) {
        // TODO
    }

    @Override
    public Vector getDerailedVelocityMod() {
        return null; // TODO
    }

    @Override
    public void setDerailedVelocityMod(Vector derailed) {
         // TODO
    }

    @Override
    public AbstractMinecartEntity getHandle() {
        return (AbstractMinecartEntity) nms;
    }

    @Override
    public void setDisplayBlock(MaterialData material) {
        this.getHandle().setCustomBlockState(Optional.ofNullable(material).map(CraftMagicNumbers::getBlock));
    }

    @Override
    public void setDisplayBlockData(BlockData blockData) {
    	this.getHandle().setCustomBlockState(Optional.ofNullable(blockData).map(data -> ((CraftBlockData)data).getState()));
    }

    @Override
    public MaterialData getDisplayBlock() {
        BlockState blockData = getHandle().getContainedBlock();
        return CraftMagicNumbers.getMaterial(blockData);
    }

    @Override
    public BlockData getDisplayBlockData() {
        BlockState blockData = getHandle().getContainedBlock();
        return CraftBlockData.fromData(blockData);
    }

    @Override
    public void setDisplayBlockOffset(int offset) {
    	this.getHandle().setBlockOffset(offset);
    }

    @Override
    public int getDisplayBlockOffset() {
        return getHandle().getBlockOffset();
    }

    @Override
    public EntityType getType() {
        return EntityType.MINECART;
    }

    @Override
    public Material getMinecartMaterial() {
        return Material.MINECART;
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

 // Paper start - Friction API
    @org.jetbrains.annotations.NotNull
    @Override
    public net.kyori.adventure.util.TriState getFrictionState() {
        return net.kyori.adventure.util.TriState.NOT_SET;
    	//return this.getHandle().frictionState;
    }

    @Override
    public void setFrictionState(@org.jetbrains.annotations.NotNull net.kyori.adventure.util.TriState state) {
        java.util.Objects.requireNonNull(state, "state may not be null");
        // this.getHandle().frictionState = state;
    }
    // Paper end - Friction API

}