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
import org.bukkit.entity.Player;
import org.bukkit.generator.structure.GeneratedStructure;
import org.bukkit.generator.structure.Structure;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import org.cardboardpowered.interfaces.IMixinChunk;
import org.cardboardpowered.interfaces.IMixinWorld;
import com.mojang.serialization.Codec;

import io.papermc.paper.util.CoordinateUtils;
import me.isaiah.common.cmixin.IMixinHeightmap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class CardboardChunk implements Chunk {

    private WeakReference<net.minecraft.world.level.chunk.LevelChunk> weakChunk;
    private final ServerLevel worldServer;
    private final int x;
    private final int z;
    private static PalettedContainer<net.minecraft.world.level.block.state.BlockState> emptyBlockIDs;// = new ChunkSection(0).getContainer(); // TODO 1.18: ChunkSection contructor changed
    private static final byte[] emptyLight = new byte[2048];
    
    private static final byte[] FULL_LIGHT = new byte[2048];
    private static final byte[] EMPTY_LIGHT = new byte[2048];
    
    public static void setEmptyBlockIds(net.minecraft.world.level.Level world) {
        if (null == emptyBlockIDs) {
            me.isaiah.common.cmixin.IMixinWorld ic = (me.isaiah.common.cmixin.IMixinWorld) world;
            emptyBlockIDs = ic.I_emptyBlockIDs();
        }
    }

    public CardboardChunk(net.minecraft.world.level.chunk.LevelChunk chunk) {
        this.weakChunk = new WeakReference<>(chunk);

        worldServer = (ServerLevel) getHandle().getLevel();
        x = getHandle().getPos().x;
        z = getHandle().getPos().z;
    }

    public CardboardChunk(ServerLevel worldServer, int x2, int z2) {
        this.worldServer = worldServer;
        this.x = x2;
        this.z = z2;
    }

	@Override
    public CraftWorld getWorld() {
        return ((IMixinWorld)worldServer.getLevel()).getCraftWorld();
    }

    public CraftWorld getCraftWorld() {
        return (CraftWorld)this.getWorld();
    }

    public net.minecraft.world.level.chunk.LevelChunk getHandle() {
        net.minecraft.world.level.chunk.LevelChunk c = weakChunk.get();

        if (c == null) {
            c = worldServer.getChunk(x, z);
            weakChunk = new WeakReference<>(c);
        }
        return c;
    }
    
    public net.minecraft.world.level.chunk.ChunkAccess getHandle(ChunkStatus chunkStatus) {
        net.minecraft.world.level.chunk.ChunkAccess chunkAccess = worldServer.getChunk(x, z, chunkStatus);

        // SPIGOT-7332: Get unwrapped extension
        if (chunkAccess instanceof ImposterProtoChunk extension) {
            return extension.getWrapped();
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
        net.minecraft.world.level.chunk.LevelChunk chunk = getHandle();

        BlockState[] entities = new BlockState[chunk.getBlockEntities().size()];

        for (Object obj : chunk.getBlockEntities().keySet().toArray()) {
            if (!(obj instanceof BlockPos)) continue;

            BlockPos position = (BlockPos) obj;
            entities[index++] = ((IMixinWorld)(Object)worldServer).getCraftWorld().getBlockAt(position.getX(), position.getY(), position.getZ()).getState();
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

        Predicate<net.minecraft.world.level.block.state.BlockState> nms = Predicates.equalTo(((CraftBlockData) block).getState());
        for (LevelChunkSection section : getHandle().getSections())
            if (section != null && section.getStates().maybeHas(nms)) return true;
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
        net.minecraft.world.level.chunk.ChunkAccess chunk = this.getHandle(ChunkStatus.FULL);
        LevelChunkSection[] cs = chunk.getSections();
        PalettedContainer[] sectionBlockIDs = new PalettedContainer[cs.length];
        
        // TODO: Update api to 1.21.9
        boolean includeLightData = true;
        
        byte[][] sectionSkyLights = includeLightData ? new byte[cs.length][] : null;
        byte[][] sectionEmitLights = includeLightData ? new byte[cs.length][] : null;
        boolean[] sectionEmpty = new boolean[cs.length];
        PalettedContainerRO<Holder<Biome>>[] biome = !includeBiome && !includeBiomeTempRain ? null : new PalettedContainer[cs.length];

        for (int i = 0; i < cs.length; i++) {
           sectionEmpty[i] = cs[i].hasOnlyAir();
           if (!sectionEmpty[i]) {
              sectionBlockIDs[i] = cs[i].getStates().copy();
           } else {
              sectionBlockIDs[i] = emptyBlockIDs;
           }

           if (includeLightData) {
              LevelLightEngine lightEngine = this.worldServer.getLightEngine();
              DataLayer skyLightArray = lightEngine.getLayerListener(LightLayer.SKY).getDataLayerData(SectionPos.of(this.x, chunk.getSectionYFromSectionIndex(i), this.z));
              if (skyLightArray == null) {
                 sectionSkyLights[i] = this.worldServer.dimensionType().hasSkyLight() ? FULL_LIGHT : EMPTY_LIGHT;
              } else {
                 sectionSkyLights[i] = new byte[2048];
                 System.arraycopy(skyLightArray.getData(), 0, sectionSkyLights[i], 0, 2048);
              }

              DataLayer emitLightArray = lightEngine.getLayerListener(LightLayer.BLOCK)
                 .getDataLayerData(SectionPos.of(this.x, chunk.getSectionYFromSectionIndex(i), this.z));
              if (emitLightArray == null) {
                 sectionEmitLights[i] = EMPTY_LIGHT;
              } else {
                 sectionEmitLights[i] = new byte[2048];
                 System.arraycopy(emitLightArray.getData(), 0, sectionEmitLights[i], 0, 2048);
              }
           }

           if (biome != null) {
              biome[i] = cs[i].getBiomes().copy();
           }
        }

        Heightmap heightmap = null;
        if (includeMaxBlockY) {
           heightmap = new Heightmap(chunk, Heightmap.Types.MOTION_BLOCKING);
           heightmap.setRawData(chunk, Heightmap.Types.MOTION_BLOCKING, chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.MOTION_BLOCKING).getRawData());
        }

        net.minecraft.core.Registry<Biome> iregistry = worldServer.registryAccess().lookupOrThrow(Registries.BIOME);

        World world = this.getWorld();
        return new CardboardChunkSnapshot(
           this.getX(),
           this.getZ(),
           chunk.getMinY(),
           chunk.getMaxY(),
           world.getSeaLevel(),
           world.getName(),
           world.getFullTime(),
           sectionBlockIDs,
           sectionSkyLights,
           sectionEmitLights,
           sectionEmpty,
           heightmap,
           iregistry, // TODO: Check 1.21.9 removed from constructor: iregistry
           biome
        );
     }
    
    /*
    @Override
    public ChunkSnapshot getChunkSnapshot(boolean includeMaxBlockY, boolean includeBiome, boolean includeBiomeTempRain) {
        net.minecraft.world.chunk.Chunk chunk = getHandle(ChunkStatus.FULL);

        ChunkSection[] cs = chunk.getSectionArray();
        PalettedContainer[] sectionBlockIDs = new PalettedContainer[cs.length];
        byte[][] sectionSkyLights = new byte[cs.length][];
        byte[][] sectionEmitLights = new byte[cs.length][];
        boolean[] sectionEmpty = new boolean[cs.length];
        ReadableContainer<RegistryEntry<Biome>>[] biome = (includeBiome || includeBiomeTempRain) ? new PalettedContainer[cs.length] : null;

        net.minecraft.registry.Registry<Biome> iregistry = worldServer.getRegistryManager().getOrThrow(RegistryKeys.BIOME);
        // Codec<ReadableContainer<RegistryEntry<Biome>>> biomeCodec = PalettedContainer.createReadableContainerCodec(iregistry.getIndexedEntries(), iregistry.createEntryCodec(), PalettedContainer.PaletteProvider.BIOME, iregistry.entryOf(BiomeKeys.PLAINS));
        // Codec<ReadableContainer<RegistryEntry<Biome>>> biomeCodec = PalettedContainer.createReadableContainerCodec(iregistry.getIndexedEntries(), iregistry.getEntryCodec(), PalettedContainer.PaletteProvider.BIOME, iregistry.getOrThrow(BiomeKeys.PLAINS));
        Codec<ReadableContainer<RegistryEntry<Biome>>> biomeCodec = worldServer.getPalettesFactory().biomeContainerCodec();
        
        for (int i = 0; i < cs.length; i++) {
            NbtCompound data = new NbtCompound();

            data.put("block_states", SerializedChunk.CODEC.encodeStart(NbtOps.INSTANCE, cs[i].getBlockStateContainer()).getOrThrow());
            sectionBlockIDs[i] = SerializedChunk.CODEC.parse(NbtOps.INSTANCE, data.getCompoundOrEmpty("block_states")).getOrThrow(SerializedChunk.ChunkLoadingException::new);

            // data.put("block_states", SerializedChunk.CODEC.encodeStart(NbtOps.INSTANCE, cs[i].getBlockStateContainer()).get().left().get());
            // sectionBlockIDs[i] = SerializedChunk.CODEC.parse(NbtOps.INSTANCE, data.getCompound("block_states")).get().left().get();

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
                biome[i] = biomeCodec.parse(NbtOps.INSTANCE, data.getCompoundOrEmpty("biomes")).getOrThrow(ChunkLoadingException::new);
            }
        }

        Heightmap hmap = null;

        if (includeMaxBlockY) {
            hmap = new Heightmap(chunk, Heightmap.Type.MOTION_BLOCKING);
            // TODO: 1.19.4 chunk.heightmaps needs AW
            // hmap.setTo(chunk, Heightmap.Type.MOTION_BLOCKING, chunk.heightmaps.get(Heightmap.Type.MOTION_BLOCKING).asLongArray());
        }

        World world = getWorld();
        return new CardboardChunkSnapshot(getX(), getZ(), chunk.getBottomY(), chunk.getTopYInclusive(), world.getSeaLevel(), world.getName(), world.getFullTime(), sectionBlockIDs, sectionSkyLights, sectionEmitLights, sectionEmpty, hmap, iregistry, biome);
    }
    */
    
    public static ChunkSnapshot getEmptyChunkSnapshot(int x, int z, CraftWorld world, boolean includeBiome, boolean includeBiomeTempRain) {
        net.minecraft.world.level.chunk.ChunkAccess actual = world.getHandle().getChunk(x, z, (includeBiome || includeBiomeTempRain) ? ChunkStatus.BIOMES : ChunkStatus.EMPTY);

        /* Fill with empty data */
        int hSection = actual.getSectionsCount();
        PalettedContainer[] blockIDs = new PalettedContainer[hSection];
        byte[][] skyLight = new byte[hSection][];
        byte[][] emitLight = new byte[hSection][];
        boolean[] empty = new boolean[hSection];
        net.minecraft.core.Registry<Biome> iregistry = world.getHandle().registryAccess().lookupOrThrow(Registries.BIOME);
        PalettedContainer<Holder<Biome>>[] biome = (includeBiome || includeBiomeTempRain) ? new PalettedContainer[hSection] : null;
        // Codec<ReadableContainer<RegistryEntry<Biome>>> biomeCodec = PalettedContainer.createReadableContainerCodec(iregistry.getIndexedEntries(), iregistry.getEntryCodec(), PalettedContainer.PaletteProvider.BIOME, iregistry.getOrThrow(BiomeKeys.PLAINS));
        Codec<PalettedContainerRO<Holder<Biome>>> biomeCodec = world.getHandle().palettedContainerFactory().biomeContainerCodec();
        
        for (int i = 0; i < hSection; i++) {
            blockIDs[i] = emptyBlockIDs;
            skyLight[i] = emptyLight;
            emitLight[i] = emptyLight;
            empty[i] = true;

            if (biome != null) {
                // biome[i] = (PalettedContainer<RegistryEntry<Biome>>) biomeCodec.parse(NbtOps.INSTANCE, biomeCodec.encodeStart(NbtOps.INSTANCE, actual.getSection(i).getBiomeContainer()).get().left().get()).get().left().get();
                biome[i] = (PalettedContainer<Holder<Biome>>) biomeCodec.parse(NbtOps.INSTANCE, biomeCodec.encodeStart(NbtOps.INSTANCE, actual.getSection(i).getBiomes()).getOrThrow()).getOrThrow(SerializableChunkData.ChunkReadException::new);

            }
        }

        return new CardboardChunkSnapshot(x, z, world.getMinHeight(), world.getMaxHeight(), world.getSeaLevel(), world.getName(), world.getFullTime(), blockIDs, skyLight, emitLight, empty, new Heightmap(actual, Heightmap.Types.MOTION_BLOCKING), iregistry, biome);
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
            bk[i] = CraftBlockState.getBlockState(this.worldServer, e.getBlockPos());
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
        net.minecraft.world.level.chunk.ChunkAccess chunk = this.getHandle(ChunkStatus.FULL);
        ArrayList<BlockState> entities = new ArrayList<BlockState>();

        for (BlockPos position : ((IMixinChunk)chunk).cardboard_getBlockEntities().keySet()) {
            Block block = ((IMixinWorld)this.worldServer).getCraftWorld().getBlockAt(position.getX(), position.getY(), position.getZ());
            if (!blockPredicate.test(block)) continue;
            entities.add(block.getState(useSnapshot));
        }
        return entities;
    }

    @Override
    public boolean isEntitiesLoaded() {
        return this.getCraftWorld().getHandle().areEntitiesLoaded(CoordinateUtils.getChunkKey(this.x, this.z));

    }

	//@Override
	public boolean contains(org.bukkit.block.@NotNull Biome biome) {
        Preconditions.checkArgument((biome != null ? 1 : 0) != 0, (Object)"Biome cannot be null");
        net.minecraft.world.level.chunk.ChunkAccess chunk = this.getHandle(ChunkStatus.BIOMES);
        
        com.google.common.base.Predicate nms = Predicates.equalTo(CraftBlock.biomeToBiomeBase(((IMixinChunk)chunk).bridge$biomeRegistry(), biome));

        for (LevelChunkSection section : chunk.getSections()) {
            if (section == null || !section.getBiomes().maybeHas((Predicate<Holder<net.minecraft.world.level.biome.Biome>>)nms)) continue;
            return true;
        }
        return false;
    }

	@Override
    public Chunk.LoadLevel getLoadLevel() {
		if (!this.worldServer.hasChunk(this.getX(), this.getZ())) {
			return Chunk.LoadLevel.UNLOADED;
		}
		
        LevelChunk chunk = this.worldServer.getChunk(this.getX(), this.getZ()); // getChunkIfLoaded
        if (chunk == null) {
            return Chunk.LoadLevel.UNLOADED;
        }
        return Chunk.LoadLevel.values()[chunk.getFullStatus().ordinal()];
    }

	// @Override
    public boolean isGenerated() {
        net.minecraft.world.level.chunk.ChunkAccess chunk = this.getHandle(ChunkStatus.EMPTY);
        return chunk.getPersistedStatus().isOrAfter(ChunkStatus.FULL);
    }

    // 1.20.4 API
    
	@Override
	public ChunkSnapshot getChunkSnapshot(boolean includeMaxBlockY, boolean includeBiome,
			boolean includeBiomeTempRain, boolean includeLightData) {
		net.minecraft.world.level.chunk.ChunkAccess chunk = this.getHandle(ChunkStatus.FULL);
        LevelChunkSection[] cs = chunk.getSections();
        PalettedContainer[] sectionBlockIDs = new PalettedContainer[cs.length];
        byte[][] sectionSkyLights = includeLightData ? new byte[cs.length][] : null;
        byte[][] sectionEmitLights = includeLightData ? new byte[cs.length][] : null;
        boolean[] sectionEmpty = new boolean[cs.length];
        PalettedContainer[] biome = includeBiome || includeBiomeTempRain ? new PalettedContainer[cs.length] : null;
        Registry<net.minecraft.world.level.biome.Biome> iregistry = this.worldServer.registryAccess().lookupOrThrow(Registries.BIOME);
        for (int i2 = 0; i2 < cs.length; ++i2) {
            sectionEmpty[i2] = cs[i2].hasOnlyAir();
            sectionBlockIDs[i2] = !sectionEmpty[i2] ? cs[i2].getStates().copy() : emptyBlockIDs;
            if (includeLightData) {
                LevelLightEngine lightengine = this.worldServer.getLightEngine();
                DataLayer skyLightArray = lightengine.getLayerListener(LightLayer.SKY).getDataLayerData(SectionPos.of(this.x, chunk.getSectionYFromSectionIndex(i2), this.z));
                if (skyLightArray == null) {
                    sectionSkyLights[i2] = this.worldServer.dimensionType().hasSkyLight() ? FULL_LIGHT : EMPTY_LIGHT;
                } else {
                    sectionSkyLights[i2] = new byte[2048];
                    System.arraycopy(skyLightArray.getData(), 0, sectionSkyLights[i2], 0, 2048);
                }
                DataLayer emitLightArray = lightengine.getLayerListener(LightLayer.BLOCK).getDataLayerData(SectionPos.of(this.x, chunk.getSectionYFromSectionIndex(i2), this.z));
                if (emitLightArray == null) {
                    sectionEmitLights[i2] = EMPTY_LIGHT;
                } else {
                    sectionEmitLights[i2] = new byte[2048];
                    System.arraycopy(emitLightArray.getData(), 0, sectionEmitLights[i2], 0, 2048);
                }
            }
            if (biome == null) continue;
            biome[i2] = ((PalettedContainer)cs[i2].getBiomes()).copy();
        }
        Heightmap hmap = null;
        if (includeMaxBlockY) {
            hmap = new Heightmap(chunk, Heightmap.Types.MOTION_BLOCKING);
            hmap.setRawData(chunk, Heightmap.Types.MOTION_BLOCKING, chunk.getOrCreateHeightmapUnprimed(Heightmap.Types.MOTION_BLOCKING).getRawData());
        }
        World world = this.getWorld();
        return new CardboardChunkSnapshot(this.getX(), this.getZ(), chunk.getMinY(), chunk.getMaxY(), world.getSeaLevel(), world.getName(), world.getFullTime(), sectionBlockIDs, sectionSkyLights, sectionEmitLights, sectionEmpty, hmap, iregistry, biome);
	}

	@Override
	public Collection<GeneratedStructure> getStructures() {
		return this.getCraftWorld().getStructures(this.getX(), this.getZ());
	}

	@Override
	public Collection<GeneratedStructure> getStructures(@NotNull Structure structure) {
		return this.getCraftWorld().getStructures(this.getX(), this.getZ(), structure);
	}
	
	// 1.20.6 API:

	@Override
	public @NotNull Collection<Player> getPlayersSeeingChunk() {
		return this.getWorld().getPlayersSeeingChunk((Chunk)this);
	}

}