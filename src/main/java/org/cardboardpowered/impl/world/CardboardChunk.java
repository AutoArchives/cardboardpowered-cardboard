/**
 * CardboardPowered - Bukkit/Spigot for Fabric
 * Copyright (C) CardboardPowered.org and contributors
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either 
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.cardboardpowered.impl.world;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.javazilla.bukkitfabric.interfaces.IMixinChunk;
import com.javazilla.bukkitfabric.interfaces.IMixinWorld;
import com.mojang.serialization.Codec;

import io.papermc.paper.util.CoordinateUtils;
import me.isaiah.common.cmixin.IMixinHeightmap;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.ChunkSerializer.ChunkLoadingException;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.PalettedContainer;
import net.minecraft.world.chunk.WrapperProtoChunk;
import net.minecraft.world.chunk.ReadableContainer;
import net.minecraft.world.chunk.light.LightingProvider;
// TODO 1.18: import net.minecraft.world.gen.ChunkRandom;

// import net.minecraft.world.chunk.ReadOnlyChunk; //.WrapperProtoChunk;
import net.minecraft.world.chunk.WorldChunk;

public class CardboardChunk implements Chunk {

    private WeakReference<net.minecraft.world.chunk.WorldChunk> weakChunk;
    private final ServerWorld worldServer;
    private final int x;
    private final int z;
    private static PalettedContainer<net.minecraft.block.BlockState> emptyBlockIDs;// = new ChunkSection(0).getContainer(); // TODO 1.18: ChunkSection contructor changed
    private static final byte[] emptyLight = new byte[2048];
    
    public static void setEmptyBlockIds(net.minecraft.world.World world) {
        if (null == emptyBlockIDs) {
            me.isaiah.common.cmixin.IMixinWorld ic = (me.isaiah.common.cmixin.IMixinWorld) world;
            emptyBlockIDs = ic.I_emptyBlockIDs();
        }
    }

    public CardboardChunk(net.minecraft.world.chunk.WorldChunk chunk) {
        this.weakChunk = new WeakReference<>(chunk);

        worldServer = (ServerWorld) getHandle().getWorld();
        x = getHandle().getPos().x;
        z = getHandle().getPos().z;
    }

    public CardboardChunk(ServerWorld worldServer, int x2, int z2) {
        this.worldServer = worldServer;
        this.x = x2;
        this.z = z2;
    }

	@Override
    public WorldImpl getWorld() {
        return ((IMixinWorld)worldServer.toServerWorld()).getWorldImpl();
    }

    public WorldImpl getCraftWorld() {
        return (WorldImpl)this.getWorld();
    }

    public net.minecraft.world.chunk.WorldChunk getHandle() {
        net.minecraft.world.chunk.WorldChunk c = weakChunk.get();

        if (c == null) {
            c = worldServer.getChunk(x, z);
            weakChunk = new WeakReference<>(c);
        }
        return c;
    }
    
    public net.minecraft.world.chunk.Chunk getHandle(ChunkStatus chunkStatus) {
        net.minecraft.world.chunk.Chunk chunkAccess = worldServer.getChunk(x, z, chunkStatus);

        // SPIGOT-7332: Get unwrapped extension
        if (chunkAccess instanceof WrapperProtoChunk extension) {
            return extension.getWrappedChunk();
        }

        return chunkAccess;
    }

    void breakLink() {
        weakChunk.clear();
    }

    @Override
    public int getX() {
        return getHandle().getPos().x;
    }

    @Override
    public int getZ() {
        return getHandle().getPos().z;
    }

    @Override
    public String toString() {
        return "BukkitChunk{" + "x=" + getX() + "z=" + getZ() + '}';
    }

    @Override
    public Block getBlock(int x, int y, int z) {
        return new CraftBlock(worldServer, new BlockPos((this.x << 4) | x, y, (this.z << 4) | z));
    }

    @Override
    public Entity[] getEntities() {
        if (!isLoaded()) getWorld().getChunkAt(x, z);
        int count = 0, index = 0;
        ArrayList<Entity> list = new ArrayList<>();
        for (Entity e : getWorld().getEntities()) {
            if (e.getChunk() == this) {
                count++;
                list.add(e);
            }
        }
        return list.toArray(new Entity[list.size()]);
    }

    @Override
    public BlockState[] getTileEntities() {
        if (!isLoaded()) getWorld().getChunkAt(x, z);

        int index = 0;
        net.minecraft.world.chunk.WorldChunk chunk = getHandle();

        BlockState[] entities = new BlockState[chunk.getBlockEntities().size()];

        for (Object obj : chunk.getBlockEntities().keySet().toArray()) {
            if (!(obj instanceof BlockPos)) continue;

            BlockPos position = (BlockPos) obj;
            entities[index++] = ((IMixinWorld)(Object)worldServer).getWorldImpl().getBlockAt(position.getX(), position.getY(), position.getZ()).getState();
        }

        return entities;
    }

    @Override
    public boolean isLoaded() {
        return getWorld().isChunkLoaded(this);
    }

    @Override
    public boolean load() {
        return getWorld().loadChunk(getX(), getZ(), true);
    }

    @Override
    public boolean load(boolean generate) {
        return getWorld().loadChunk(getX(), getZ(), generate);
    }

    @Override
    public boolean unload() {
        return getWorld().unloadChunk(getX(), getZ());
    }

    @Override
    public boolean isSlimeChunk() {
        return false; // TODO 1.18 ChunkRandom.getSlimeRandom(getX(), getZ(), getWorld().getSeed(), 987234911L).nextInt(10) == 0;
    }

    @Override
    public boolean unload(boolean save) {
        return getWorld().unloadChunk(getX(), getZ(), save);
    }

    @Override
    public boolean isForceLoaded() {
        return getWorld().isChunkForceLoaded(getX(), getZ());
    }

    @Override
    public void setForceLoaded(boolean forced) {
        getWorld().setChunkForceLoaded(getX(), getZ(), forced);
    }

    @Override
    public boolean addPluginChunkTicket(Plugin plugin) {
        return getWorld().addPluginChunkTicket(getX(), getZ(), plugin);
    }

    @Override
    public boolean removePluginChunkTicket(Plugin plugin) {
        return getWorld().removePluginChunkTicket(getX(), getZ(), plugin);
    }

    @Override
    public Collection<Plugin> getPluginChunkTickets() {
        return getWorld().getPluginChunkTickets(getX(), getZ());
    }

    @Override
    public long getInhabitedTime() {
        return getHandle().getInhabitedTime();
    }

    @Override
    public void setInhabitedTime(long ticks) {
        getHandle().setInhabitedTime(ticks);
    }

    @Override
    public boolean contains(BlockData block) {
        Preconditions.checkArgument(block != null, "Block cannot be null");

        Predicate<net.minecraft.block.BlockState> nms = Predicates.equalTo(((CraftBlockData) block).getState());
        for (ChunkSection section : getHandle().getSectionArray())
            if (section != null && section.getBlockStateContainer().hasAny(nms)) return true;
        return false;
    }

    @Override
    public ChunkSnapshot getChunkSnapshot() {
        return getChunkSnapshot(true, false, false);
    }

    /*@SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public ChunkSnapshot getChunkSnapshot(boolean includeMaxBlockY, boolean includeBiome, boolean includeBiomeTempRain) {
        net.minecraft.world.chunk.WorldChunk chunk = getHandle();

        ChunkSection[] cs = chunk.getSectionArray();
        PalettedContainer[] sectionBlockIDs = new PalettedContainer[cs.length];
        byte[][] sectionSkyLights = new byte[cs.length][];
        byte[][] sectionEmitLights = new byte[cs.length][];
        boolean[] sectionEmpty = new boolean[cs.length];

        for (int i = 0; i < cs.length; i++) {
            if (cs[i] == null) { // Section empty?
                setEmptyBlockIds(chunk.world);
                sectionBlockIDs[i] = emptyBlockIDs;
                sectionSkyLights[i] = emptyLight;
                sectionEmitLights[i] = emptyLight;
                sectionEmpty[i] = true;
            } else { // Not empty
                NbtCompound data = new NbtCompound();
                cs[i].getContainer().write(data, "Palette", "BlockStates"); // TODO 1.18 removed

                // TODO 1.18 removed ChunkSection.PALETTE
                PalettedContainer<net.minecraft.block.BlockState> blockids = new PalettedContainer<>(ChunkSection.PALETTE, net.minecraft.block.Block.STATE_IDS, NbtHelper::toBlockState, NbtHelper::fromBlockState, Blocks.AIR.getDefaultState()); // TODO: snapshot whole ChunkSection
                blockids.read(data.getList("Palette", CraftMagicNumbers.NBT.TAG_COMPOUND), data.getLongArray("BlockStates")); // TODO 1.18 removed

                sectionBlockIDs[i] = blockids;

                LightingProvider lightengine = chunk.world.getChunkManager().getLightingProvider();
                ChunkNibbleArray skyLightArray = lightengine.get(LightType.SKY).getLightSection(ChunkSectionPos.from(x, i, z));
                if (skyLightArray == null)
                    sectionSkyLights[i] = emptyLight;
                else {
                    sectionSkyLights[i] = new byte[2048];
                    System.arraycopy(skyLightArray.asByteArray(), 0, sectionSkyLights[i], 0, 2048);
                }
                ChunkNibbleArray emitLightArray = lightengine.get(LightType.BLOCK).getLightSection(ChunkSectionPos.from(x, i, z));
                if (emitLightArray == null)
                    sectionEmitLights[i] = emptyLight;
                else {
                    sectionEmitLights[i] = new byte[2048];
                    System.arraycopy(emitLightArray.asByteArray(), 0, sectionEmitLights[i], 0, 2048);
                }
            }
        }

        Heightmap hmap = null;

        if (includeMaxBlockY) {
            hmap = new Heightmap(null, Heightmap.Type.MOTION_BLOCKING);
            IMixinHeightmap map = (IMixinHeightmap) hmap;
            //map.I_setTo(chunk, Heightmap.Type.MOTION_BLOCKING, chunk.heightmaps.get(Heightmap.Type.MOTION_BLOCKING).asLongArray());
        }

        BiomeAccess.Storage biome = null;
        if (includeBiome || includeBiomeTempRain) {
            if (chunk instanceof BiomeAccess.Storage || GameVersion.INSTANCE.getReleaseTarget().contains("1.18")) {
                // 1.18
                biome = (BiomeAccess.Storage) chunk;
            } else {
                biome = chunk.getBiomeArray();
            }
        }

        World world = getWorld();
        return new CardboardChunkSnapshot(getX(), getZ(), world.getName(), world.getFullTime(), sectionBlockIDs, sectionSkyLights, sectionEmitLights, sectionEmpty, hmap, biome);
    }*/
    
    
    @Override
    public ChunkSnapshot getChunkSnapshot(boolean includeMaxBlockY, boolean includeBiome, boolean includeBiomeTempRain) {
        net.minecraft.world.chunk.Chunk chunk = getHandle(ChunkStatus.FULL);

        ChunkSection[] cs = chunk.getSectionArray();
        PalettedContainer[] sectionBlockIDs = new PalettedContainer[cs.length];
        byte[][] sectionSkyLights = new byte[cs.length][];
        byte[][] sectionEmitLights = new byte[cs.length][];
        boolean[] sectionEmpty = new boolean[cs.length];
        ReadableContainer<RegistryEntry<Biome>>[] biome = (includeBiome || includeBiomeTempRain) ? new PalettedContainer[cs.length] : null;

        net.minecraft.registry.Registry<Biome> iregistry = worldServer.getRegistryManager().get(RegistryKeys.BIOME);
        // Codec<ReadableContainer<RegistryEntry<Biome>>> biomeCodec = PalettedContainer.createReadableContainerCodec(iregistry.getIndexedEntries(), iregistry.createEntryCodec(), PalettedContainer.PaletteProvider.BIOME, iregistry.entryOf(BiomeKeys.PLAINS));
        Codec<ReadableContainer<RegistryEntry<Biome>>> biomeCodec = PalettedContainer.createReadableContainerCodec(iregistry.getIndexedEntries(), iregistry.getEntryCodec(), PalettedContainer.PaletteProvider.BIOME, iregistry.entryOf(BiomeKeys.PLAINS));
        
        for (int i = 0; i < cs.length; i++) {
            NbtCompound data = new NbtCompound();

            data.put("block_states", ChunkSerializer.CODEC.encodeStart(NbtOps.INSTANCE, cs[i].getBlockStateContainer()).getOrThrow());
            sectionBlockIDs[i] = ChunkSerializer.CODEC.parse(NbtOps.INSTANCE, data.getCompound("block_states")).getOrThrow(ChunkSerializer.ChunkLoadingException::new);

            
            
            // data.put("block_states", ChunkSerializer.CODEC.encodeStart(NbtOps.INSTANCE, cs[i].getBlockStateContainer()).get().left().get());
            // sectionBlockIDs[i] = ChunkSerializer.CODEC.parse(NbtOps.INSTANCE, data.getCompound("block_states")).get().left().get();

            LightingProvider lightengine = worldServer.getLightingProvider();
            ChunkNibbleArray skyLightArray = lightengine.get(LightType.SKY).getLightSection(ChunkSectionPos.from(x, i, z));
            if (skyLightArray == null) {
                sectionSkyLights[i] = emptyLight;
            } else {
                sectionSkyLights[i] = new byte[2048];
                System.arraycopy(skyLightArray.asByteArray(), 0, sectionSkyLights[i], 0, 2048);
            }
            ChunkNibbleArray emitLightArray = lightengine.get(LightType.BLOCK).getLightSection(ChunkSectionPos.from(x, i, z));
            if (emitLightArray == null) {
                sectionEmitLights[i] = emptyLight;
            } else {
                sectionEmitLights[i] = new byte[2048];
                System.arraycopy(emitLightArray.asByteArray(), 0, sectionEmitLights[i], 0, 2048);
            }

            if (biome != null) {
                data.put("biomes", biomeCodec.encodeStart(NbtOps.INSTANCE, cs[i].getBiomeContainer()).getOrThrow());
                biome[i] = biomeCodec.parse(NbtOps.INSTANCE, data.getCompound("biomes")).getOrThrow(ChunkLoadingException::new);
            }
        }

        Heightmap hmap = null;

        if (includeMaxBlockY) {
            hmap = new Heightmap(chunk, Heightmap.Type.MOTION_BLOCKING);
            // TODO: 1.19.4 chunk.heightmaps needs AW
            // hmap.setTo(chunk, Heightmap.Type.MOTION_BLOCKING, chunk.heightmaps.get(Heightmap.Type.MOTION_BLOCKING).asLongArray());
        }

        World world = getWorld();
        return new CardboardChunkSnapshot(getX(), getZ(), chunk.getBottomY(), chunk.getTopY(), world.getName(), world.getFullTime(), sectionBlockIDs, sectionSkyLights, sectionEmitLights, sectionEmpty, hmap, iregistry, biome);
    }
    
    public static ChunkSnapshot getEmptyChunkSnapshot(int x, int z, WorldImpl world, boolean includeBiome, boolean includeBiomeTempRain) {
        net.minecraft.world.chunk.Chunk actual = world.getHandle().getChunk(x, z, (includeBiome || includeBiomeTempRain) ? ChunkStatus.BIOMES : ChunkStatus.EMPTY);

        /* Fill with empty data */
        int hSection = actual.countVerticalSections();
        PalettedContainer[] blockIDs = new PalettedContainer[hSection];
        byte[][] skyLight = new byte[hSection][];
        byte[][] emitLight = new byte[hSection][];
        boolean[] empty = new boolean[hSection];
        net.minecraft.registry.Registry<Biome> iregistry = world.getHandle().getRegistryManager().get(RegistryKeys.BIOME);
        PalettedContainer<RegistryEntry<Biome>>[] biome = (includeBiome || includeBiomeTempRain) ? new PalettedContainer[hSection] : null;
        Codec<ReadableContainer<RegistryEntry<Biome>>> biomeCodec = PalettedContainer.createReadableContainerCodec(iregistry.getIndexedEntries(), iregistry.getEntryCodec(), PalettedContainer.PaletteProvider.BIOME, iregistry.entryOf(BiomeKeys.PLAINS));

        for (int i = 0; i < hSection; i++) {
            blockIDs[i] = emptyBlockIDs;
            skyLight[i] = emptyLight;
            emitLight[i] = emptyLight;
            empty[i] = true;

            if (biome != null) {
                // biome[i] = (PalettedContainer<RegistryEntry<Biome>>) biomeCodec.parse(NbtOps.INSTANCE, biomeCodec.encodeStart(NbtOps.INSTANCE, actual.getSection(i).getBiomeContainer()).get().left().get()).get().left().get();
                biome[i] = (PalettedContainer<RegistryEntry<Biome>>) biomeCodec.parse(NbtOps.INSTANCE, biomeCodec.encodeStart(NbtOps.INSTANCE, actual.getSection(i).getBiomeContainer()).getOrThrow()).getOrThrow(ChunkSerializer.ChunkLoadingException::new);

            }
        }

        return new CardboardChunkSnapshot(x, z, world.getMinHeight(), world.getMaxHeight(), world.getName(), world.getFullTime(), blockIDs, skyLight, emitLight, empty, new Heightmap(actual, Heightmap.Type.MOTION_BLOCKING), iregistry, biome);
    }

    static void validateChunkCoordinates(int x, int y, int z) {
        Preconditions.checkArgument(0 <= x && x <= 15, "x out of range (expected 0-15, got %s)", x);
        Preconditions.checkArgument(0 <= y && y <= 255, "y out of range (expected 0-255, got %s)", y);
        Preconditions.checkArgument(0 <= z && z <= 15, "z out of range (expected 0-15, got %s)", z);
    }

    static void validateChunkCoordinates(int minY, int maxY, int x, int y, int z) {
        Preconditions.checkArgument(0 <= x && x <= 15, "x out of range (expected 0-15, got %s)", x);
        Preconditions.checkArgument(minY <= y && y <= maxY, "y out of range (expected %s-%s, got %s)", minY, maxY, y);
        Preconditions.checkArgument(0 <= z && z <= 15, "z out of range (expected 0-15, got %s)", z);
    }

    public PersistentDataContainer getPersistentDataContainer() {
        // Added in Bukkit 1.16.3 API (Spigot Pull #672)
        return null;
    }

    /*public net.minecraft.world.chunk.Chunk getHandle(ChunkStatus chunkStatus) {
        net.minecraft.world.chunk.Chunk chunkAccess = this.worldServer.getChunk(this.x, this.z, chunkStatus);
        if (chunkAccess instanceof ReadOnlyChunk) {
        	ReadOnlyChunk extension = (ReadOnlyChunk)chunkAccess;
            return extension.getWrappedChunk();
        }
        return chunkAccess;
    }*/

    static {
        Arrays.fill(emptyLight, (byte) 0xFF);
    }

    @Override
    public BlockState[] getTileEntities(boolean arg0) {
        Map<BlockPos,BlockEntity> map = getHandle().getBlockEntities();
        BlockState[] bk = new BlockState[map.size()];
        int i = 0;
        for (BlockEntity e : map.values()) {
            bk[i] = CraftBlockState.getBlockState(this.worldServer, e.getPos());
            i++;
        }
        return bk;
    }

    @Override
    public Collection<BlockState> getTileEntities(Predicate<? super Block> blockPredicate, boolean useSnapshot) {
        Preconditions.checkNotNull(blockPredicate, (Object)"blockPredicate");
        if (!this.isLoaded()) {
            this.getWorld().getChunkAt(this.x, this.z);
        }
        net.minecraft.world.chunk.Chunk chunk = this.getHandle(ChunkStatus.FULL);
        ArrayList<BlockState> entities = new ArrayList<BlockState>();

        for (BlockPos position : ((IMixinChunk)chunk).cardboard_getBlockEntities().keySet()) {
            Block block = ((IMixinWorld)this.worldServer).getWorldImpl().getBlockAt(position.getX(), position.getY(), position.getZ());
            if (!blockPredicate.test(block)) continue;
            entities.add(block.getState(useSnapshot));
        }
        return entities;
    }

    @Override
    public boolean isEntitiesLoaded() {
        return this.getCraftWorld().getHandle().isChunkLoaded(CoordinateUtils.getChunkKey(this.x, this.z));

    }

	//@Override
	public boolean contains(org.bukkit.block.@NotNull Biome biome) {
        Preconditions.checkArgument((biome != null ? 1 : 0) != 0, (Object)"Biome cannot be null");
        net.minecraft.world.chunk.Chunk chunk = this.getHandle(ChunkStatus.BIOMES);
        
        com.google.common.base.Predicate nms = Predicates.equalTo(CraftBlock.biomeToBiomeBase(((IMixinChunk)chunk).bridge$biomeRegistry(), biome));

        for (ChunkSection section : chunk.getSectionArray()) {
            if (section == null || !section.getBiomeContainer().hasAny((Predicate<RegistryEntry<net.minecraft.world.biome.Biome>>)nms)) continue;
            return true;
        }
        return false;
    }

	@Override
    public Chunk.LoadLevel getLoadLevel() {
		if (!this.worldServer.isChunkLoaded(this.getX(), this.getZ())) {
			return Chunk.LoadLevel.UNLOADED;
		}
		
        WorldChunk chunk = this.worldServer.getChunk(this.getX(), this.getZ()); // getChunkIfLoaded
        if (chunk == null) {
            return Chunk.LoadLevel.UNLOADED;
        }
        return Chunk.LoadLevel.values()[chunk.getLevelType().ordinal()];
    }

	// @Override
    public boolean isGenerated() {
        net.minecraft.world.chunk.Chunk chunk = this.getHandle(ChunkStatus.EMPTY);
        return chunk.getStatus().isAtLeast(ChunkStatus.FULL);
    }

}