package org.bukkit.craftbukkit.block;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.craftbukkit.*;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;

import com.google.common.base.Preconditions;
import java.util.function.Consumer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EmptyBlockView;
import org.bukkit.World;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.inventory.ItemType;
import org.cardboardpowered.impl.world.WorldImpl;
import org.jetbrains.annotations.NotNull;

public class CraftBlockType<B extends BlockData> implements BlockType.Typed<B>, Handleable<Block> {

    private final NamespacedKey key;
    private final Block block;
    private final Class<B> blockDataClass;
    private final boolean interactable;
	
    /*
    public static Material minecraftToBukkit(Block block) {
        return CraftMagicNumbers.getMaterial(block);
    }

    public static Block bukkitToMinecraft(Material material) {
        return CraftMagicNumbers.getBlock(material);
    }
    
    public static BlockType minecraftToBukkitNew(Block minecraft) {
        return (BlockType)CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.BLOCK, Registry.BLOCK);
    }

    public static Block bukkitToMinecraftNew(BlockType bukkit) {
        return (Block)CraftRegistry.bukkitToMinecraft(bukkit);
    }*/
    
    public static Material minecraftToBukkit(Block block) {
        return CraftMagicNumbers.getMaterial(block);
    }

    public static Block bukkitToMinecraft(Material material) {
        return CraftMagicNumbers.getBlock(material);
    }

    public static BlockType minecraftToBukkitNew(Block minecraft) {
        return (BlockType)CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.BLOCK, Registry.BLOCK);
    }

    public static Block bukkitToMinecraftNew(BlockType bukkit) {
        return (Block)CraftRegistry.bukkitToMinecraft(bukkit);
    }

    private static boolean hasMethod(Class<?> clazz, String methodName, Class<?> ... params) {
        boolean hasMethod;
        try {
            hasMethod = clazz.getDeclaredMethod(methodName, params) != null;
        }
        catch (NoSuchMethodException ex) {
            hasMethod = false;
        }
        return hasMethod;
    }

    private static boolean isInteractable(Block block) {
        boolean hasMethod;
        Class<?> clazz = block.getClass();
        boolean bl = hasMethod = CraftBlockType.hasMethod(clazz, "useWithoutItem", BlockState.class, net.minecraft.world.World.class, BlockPos.class, PlayerEntity.class, BlockHitResult.class) || CraftBlockType.hasMethod(clazz, "useItemOn", ItemStack.class, BlockState.class, net.minecraft.world.World.class, BlockPos.class, PlayerEntity.class, Hand.class, BlockHitResult.class);
        if (!hasMethod && clazz.getSuperclass() != AbstractBlock.class) {
            clazz = clazz.getSuperclass();
            hasMethod = CraftBlockType.hasMethod(clazz, "useWithoutItem", BlockState.class, net.minecraft.world.World.class, BlockPos.class, PlayerEntity.class, BlockHitResult.class) || CraftBlockType.hasMethod(clazz, "useItemOn", ItemStack.class, BlockState.class, net.minecraft.world.World.class, BlockPos.class, PlayerEntity.class, Hand.class, BlockHitResult.class);
        }
        return hasMethod;
    }

    public CraftBlockType(NamespacedKey key, Block block) {
        this.key = key;
        this.block = block;
        this.blockDataClass = (Class<B>) CraftBlockData.fromData(block.getDefaultState()).getClass().getInterfaces()[0];
        this.interactable = CraftBlockType.isInteractable(block);
    }

    @Override
    public Block getHandle() {
        return this.block;
    }

    @NotNull
    public BlockType.Typed<BlockData> typed() {
        return this.typed(BlockData.class);
    }

    @NotNull
    public <Other extends BlockData> BlockType.Typed<Other> typed(@NotNull Class<Other> blockDataType) {
        if (blockDataType.isAssignableFrom(this.blockDataClass)) {
            return (BlockType.Typed<Other>) this;
        }
        throw new IllegalArgumentException("Cannot type block type " + this.key.toString() + " to blockdata type " + blockDataType.getSimpleName());
    }

    public boolean hasItemType() {
        if (this == AIR) {
            return true;
        }
        return this.block.asItem() != Items.AIR;
    }

    @NotNull
    public ItemType getItemType() {
        if (this == AIR) {
            return ItemType.AIR;
        }
        Item item = this.block.asItem();
        Preconditions.checkArgument((item != Items.AIR ? 1 : 0) != 0, (String)"The block type %s has no corresponding item type", (Object)this.getKey());
        return CraftItemType.minecraftToBukkitNew(item);
    }

    public Class<B> getBlockDataClass() {
        return this.blockDataClass;
    }

    public B createBlockData() {
        return this.createBlockData((String)null);
    }

    public B createBlockData(Consumer<? super B> consumer) {
        B data = this.createBlockData();
        if (consumer != null) {
            consumer.accept(data);
        }
        return data;
    }

    public B createBlockData(String data) {
        return (B) CraftBlockData.newData(this, data);
    }

    public boolean isSolid() {
        return this.block.getDefaultState().blocksMovement();
    }

    public boolean isAir() {
        return this.block.getDefaultState().isAir();
    }

    public boolean isEnabledByFeature(@NotNull World world) {
        Preconditions.checkNotNull((Object)world, (Object)"World cannot be null");
        return this.getHandle().isEnabled(((WorldImpl)world).getHandle().getEnabledFeatures());
    }

    public boolean isFlammable() {
        return this.block.getDefaultState().isBurnable();
    }

    public boolean isBurnable() {
    	// TODO
        return false;
    	// return ((FireBlock)Blocks.FIRE).burnChances.getOrDefault((Object)this.block, 0) > 0;
    }

    public boolean isOccluding() {
        return this.block.getDefaultState().isSolidBlock(EmptyBlockView.INSTANCE, BlockPos.ORIGIN);
    }

    public boolean hasGravity() {
        return this.block instanceof FallingBlock;
    }

    public boolean isInteractable() {
        return this.interactable;
    }

    public float getHardness() {
    	return 0; // TODO
    	
        //return this.block.getDefaultState().hardness;
    }

    public float getBlastResistance() {
        return this.block.getBlastResistance();
    }

    public float getSlipperiness() {
        return this.block.getSlipperiness();
    }

    public String getTranslationKey() {
        return this.block.getTranslationKey();
    }

    public NamespacedKey getKey() {
        return this.key;
    }

    public Material asMaterial() {
        return (Material)Registry.MATERIAL.get(this.key);
    }

    public String translationKey() {
        return this.block.getTranslationKey();
    }

    public boolean hasCollision() {
        return false;
    	// TODO return this.block.collidable;
    }
    
}
