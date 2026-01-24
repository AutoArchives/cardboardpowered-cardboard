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
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.bukkit.World;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.inventory.ItemType;
import org.cardboardpowered.impl.world.CraftWorld;
import org.cardboardpowered.interfaces.IBlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

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
        return (BlockType)CraftRegistry.minecraftToBukkit(minecraft, Registries.BLOCK);
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
        boolean bl = hasMethod = CraftBlockType.hasMethod(clazz, "useWithoutItem", BlockState.class, net.minecraft.world.level.Level.class, BlockPos.class, Player.class, BlockHitResult.class) || CraftBlockType.hasMethod(clazz, "useItemOn", ItemStack.class, BlockState.class, net.minecraft.world.level.Level.class, BlockPos.class, Player.class, InteractionHand.class, BlockHitResult.class);
        if (!hasMethod && clazz.getSuperclass() != BlockBehaviour.class) {
            clazz = clazz.getSuperclass();
            hasMethod = CraftBlockType.hasMethod(clazz, "useWithoutItem", BlockState.class, net.minecraft.world.level.Level.class, BlockPos.class, Player.class, BlockHitResult.class) || CraftBlockType.hasMethod(clazz, "useItemOn", ItemStack.class, BlockState.class, net.minecraft.world.level.Level.class, BlockPos.class, Player.class, InteractionHand.class, BlockHitResult.class);
        }
        return hasMethod;
    }

    public CraftBlockType(NamespacedKey key, Block block) {
        this.key = key;
        this.block = block;
        this.blockDataClass = (Class<B>) CraftBlockData.fromData(block.defaultBlockState()).getClass().getInterfaces()[0];
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
        return this.block.defaultBlockState().blocksMotion();
    }

    public boolean isAir() {
        return this.block.defaultBlockState().isAir();
    }

    public boolean isEnabledByFeature(@NotNull World world) {
        Preconditions.checkNotNull((Object)world, (Object)"World cannot be null");
        return this.getHandle().isEnabled(((CraftWorld)world).getHandle().enabledFeatures());
    }

    public boolean isFlammable() {
        return this.block.defaultBlockState().ignitedByLava();
    }

    public boolean isBurnable() {
    	// TODO
        return false;
    	// return ((FireBlock)Blocks.FIRE).burnChances.getOrDefault((Object)this.block, 0) > 0;
    }

    public boolean isOccluding() {
        return this.block.defaultBlockState().isRedstoneConductor(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
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
        return this.block.getExplosionResistance();
    }

    public float getSlipperiness() {
        return this.block.getFriction();
    }

    public String getTranslationKey() {
        return this.block.getDescriptionId();
    }

    public NamespacedKey getKey() {
        return this.key;
    }

    public Material asMaterial() {
        return (Material)Registry.MATERIAL.get(this.key);
    }

    public String translationKey() {
        return this.block.getDescriptionId();
    }

    public boolean hasCollision() {
        return false;
    	// TODO return this.block.collidable;
    }

	@Override
	public @Unmodifiable @NotNull Collection<B> createBlockDataStates() {
		final ImmutableList<BlockState> possibleStates = this.block.getStateDefinition().getPossibleStates();
        final ImmutableList.Builder<B> builder = ImmutableList.builderWithExpectedSize(possibleStates.size());
        for (final BlockState possibleState : possibleStates) {
        	IBlockState cardboard_state = (IBlockState) possibleState;
            builder.add(this.blockDataClass.cast(cardboard_state.createCraftBlockData()));
        }
        return builder.build();
	}
    
}
