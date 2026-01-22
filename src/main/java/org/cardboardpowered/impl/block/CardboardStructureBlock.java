package org.cardboardpowered.impl.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.properties.StructureMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Structure;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.block.structure.UsageMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BlockVector;
import org.cardboardpowered.impl.entity.LivingEntityImpl;

public class CardboardStructureBlock extends CardboardBlockEntityState<StructureBlockEntity> implements Structure {

    private static final int MAX_SIZE = 32;

    public CardboardStructureBlock(World world, StructureBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CardboardStructureBlock(CardboardStructureBlock state, Location location) {
        super(state, location);
    }

    @Override
    public CardboardStructureBlock copy() {
        return new CardboardStructureBlock(this, null);
    }

    @Override
    public CardboardStructureBlock copy(Location location) {
        return new CardboardStructureBlock(this, location);
    }

    @Override
    public String getStructureName() {
        return getSnapshot().getStructureName();
    }

    @Override
    public void setStructureName(String name) {
        getSnapshot().setStructureName(name);
    }

    @Override
    public String getAuthor() {
        return getSnapshot().author;
    }

    @Override
    public void setAuthor(String author) {
        getSnapshot().author = author;
    }

    @Override
    public void setAuthor(LivingEntity entity) {
        getSnapshot().author = ((LivingEntityImpl) entity).getHandle().getName().getString();
    }

    @Override
    public BlockVector getRelativePosition() {
        return new BlockVector(getSnapshot().getStructurePos().getX(), getSnapshot().getStructurePos().getY(), getSnapshot().getStructurePos().getZ());
    }

    @Override
    public void setRelativePosition(BlockVector vector) {
        getSnapshot().setStructurePos(new BlockPos(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ()));
    }

    @Override
    public BlockVector getStructureSize() {
        return new BlockVector(getSnapshot().getStructureSize().getX(), getSnapshot().getStructureSize().getY(), getSnapshot().getStructureSize().getZ());
    }

    @Override
    public void setStructureSize(BlockVector vector) {
        // TODO 1.17ify getSnapshot().size = new BlockPos(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    @Override
    public void setMirror(Mirror mirror) {
        getSnapshot().mirror = net.minecraft.world.level.block.Mirror.valueOf(mirror.name());
    }

    @Override
    public Mirror getMirror() {
        return Mirror.valueOf(getSnapshot().getMirror().name());
    }

    @Override
    public void setRotation(StructureRotation rotation) {
        getSnapshot().setRotation(Rotation.valueOf(rotation.name()));
    }

    @Override
    public StructureRotation getRotation() {
        return StructureRotation.valueOf(getSnapshot().getRotation().name());
    }

    @Override
    public void setUsageMode(UsageMode mode) {
        getSnapshot().setMode(StructureMode.valueOf(mode.name()));
    }

    @Override
    public UsageMode getUsageMode() {
        return UsageMode.valueOf(getSnapshot().getMode().name());
    }

    @Override
    public void setIgnoreEntities(boolean flag) {
        getSnapshot().setIgnoreEntities(flag);
    }

    @Override
    public boolean isIgnoreEntities() {
        return getSnapshot().isIgnoreEntities();
    }

    @Override
    public void setShowAir(boolean showAir) {
        getSnapshot().setShowAir(showAir);
    }

    @Override
    public boolean isShowAir() {
        return getSnapshot().getShowAir();
    }

    @Override
    public void setBoundingBoxVisible(boolean showBoundingBox) {
        getSnapshot().showBoundingBox = showBoundingBox;
    }

    @Override
    public boolean isBoundingBoxVisible() {
        return getSnapshot().getShowBoundingBox();
    }

    @Override
    public void setIntegrity(float integrity) {
        getSnapshot().integrity = integrity;
    }

    @Override
    public float getIntegrity() {
        return getSnapshot().getIntegrity();
    }

    @Override
    public void setSeed(long seed) {
        getSnapshot().seed = seed;
    }

    @Override
    public long getSeed() {
        return getSnapshot().getSeed();
    }

    @Override
    public void setMetadata(String metadata) {
        if (getUsageMode() == UsageMode.DATA) getSnapshot().metaData = metadata;
    }

    @Override
    public String getMetadata() {
        return getSnapshot().getMetaData();
    }

    @Override
    public void applyTo(StructureBlockEntity tileEntity) {
        super.applyTo(tileEntity);
        tileEntity.setMode(tileEntity.getMode());
    }

    public static boolean isBetween(int num, int min, int max) {
        return num >= min && num <= max;
    }

    public static boolean isBetween(float num, float min, float max) {
        return num >= min && num <= max;
    }

}
