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
package org.cardboardpowered.mixin;

import org.cardboardpowered.CardboardMod;
import org.bukkit.craftbukkit.scheduler.CraftScheduler;
import org.cardboardpowered.interfaces.IMixinMinecraftServer;
import org.cardboardpowered.interfaces.IMixinWorld;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.command.DataCommandStorage;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.scoreboard.ScoreboardState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoading;
import net.minecraft.server.ServerTask;
// import net.minecraft.server.WorldGenerationProgressListener;
// import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.minecraft.village.ZombieSiegeManager;
import net.minecraft.world.Difficulty;
// import net.minecraft.world.ForcedChunkState;
import net.minecraft.world.rule.GameRules;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PlayerSaveHandler;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.ChunkLoadProgress;
import net.minecraft.world.chunk.ChunkLoadingCounter;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.*;
import net.minecraft.world.*;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.generator.CraftWorldInfo;
import org.bukkit.craftbukkit.scoreboard.CardboardScoreboardManager;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.generator.WorldInfo;
import org.cardboardpowered.interfaces.INetworkIo;
import org.cardboardpowered.interfaces.IServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.google.common.collect.ImmutableList;

import io.papermc.paper.world.PaperWorldLoader;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.function.BooleanSupplier;

@Mixin(value=MinecraftServer.class)
public abstract class MixinMinecraftServer extends ReentrantThreadExecutor<ServerTask> implements IMixinMinecraftServer {

	// public final WorldLoader.DataLoadContext worldLoaderContext;
	public SaveLoading.LoadContextSupplierContext worldLoaderContext;
	
	@Override
	public SaveLoading.LoadContextSupplierContext cardboard$worldLoaderContext() {
		return worldLoaderContext;
	}
	
    @Shadow private long tickStartTimeNanos;
    @Shadow @Final @Mutable protected SaveProperties saveProperties;
    @Shadow public abstract ServerWorld getOverworld();

    @Shadow public abstract boolean save(boolean suppressLogs, boolean flush, boolean force);

    public MixinMinecraftServer(String string) {
        super(string);
    }

    @Shadow @Final public PlayerSaveHandler saveHandler;
    @Shadow public Map<RegistryKey<net.minecraft.world.World>, ServerWorld> worlds;
    @Shadow public MinecraftServer.ResourceManagerHolder resourceManagerHolder;
    @Shadow public LevelStorage.Session session;
    @Shadow public DataCommandStorage dataCommandStorage;
    @Shadow private int ticks;

    // @Shadow public void initScoreboard(PersistentStateManager arg0) {}

    public void setDataCommandStorage(DataCommandStorage data) {
        this.dataCommandStorage = data;
    }

    @Override
    public LevelStorage.Session getSessionBF() {
        return session;
    }

    public java.util.Queue<Runnable> processQueue = new java.util.concurrent.ConcurrentLinkedQueue<Runnable>();

    private boolean forceTicks;

    @Override
    public PlayerSaveHandler getSaveHandler_BF() {
        return saveHandler;
    }

    @Inject(at = @At("HEAD"), method = "getServerModName", remap=false, cancellable = true)
    public void getServerModName_cardboard(CallbackInfoReturnable<String> ci) {
        if (null != Bukkit.getServer())
            ci.setReturnValue("Cardboard (Paper+Fabric)");
    }

    @Override
    public Map<RegistryKey<net.minecraft.world.World>, ServerWorld> getWorldMap() {
        return worlds;
    }

    @Override
    public void convertWorld(String name) {
        //getServer().upgradeWorld(name);
    }

    @Override
    public Queue<Runnable> getProcessQueue() {
        return processQueue;
    }

    @Override
    public CommandManager setCommandManager(CommandManager commandManager) {
        return (this.resourceManagerHolder.dataPackContents().commandManager = commandManager);
    }

    public MinecraftServer getServer() {
        return (MinecraftServer) (Object) this;
    }

    /**
     * Call WorldInitEvent
     * 
     * @author Cardboard
     */
    /*
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerWorldProperties;getWorldBorder()Lnet/minecraft/world/border/WorldBorder$Properties;"), method = "createWorlds")
    public void onBeginCreateWorld(WorldGenerationProgressListener p, CallbackInfo ci) {
        Collection<ServerWorld> worldz = this.worlds.values();

        for (ServerWorld world : worldz) {
            CraftServer.INSTANCE.getPluginManager().callEvent(new org.bukkit.event.world.WorldInitEvent(((IMixinWorld)world).getCraftWorld()));
        }
    }
    */

    /**
     * Enable plugins
     * Call WorldLoadEvent & ServerLoadEvent
     * 
     * @author Cardboard
     */
    @SuppressWarnings({ "resource", "deprecation" })
    @Inject(at = @At("TAIL"), method = "loadWorld")
    public void afterWorldLoad(CallbackInfo ci) {
        for (ServerWorld worldserver : ((MinecraftServer)(Object)this).getWorlds()) {
            if (worldserver != getOverworld()) {
                // TODO IMPORTANT
            	
            	// ServerWorld world, ServerWorldProperties worldProperties, boolean bonusChest, boolean debugWorld, ChunkLoadProgress loadProgress
            	setupSpawn(worldserver, worldserver.worldProperties, false, false, ((IServerWorld) worldserver).cardboard$levelLoadListener());
            	
            	// this.loadSpawn(worldserver.getChunkManager().chunkLoadingManager.worldGenerationProgressListener, worldserver);
                CraftServer.INSTANCE.getPluginManager().callEvent(new org.bukkit.event.world.WorldLoadEvent(((IMixinWorld)worldserver).getCraftWorld()));
            }
        }

        CraftServer.INSTANCE.enablePlugins(org.bukkit.plugin.PluginLoadOrder.POSTWORLD);
        CraftServer.INSTANCE.getPluginManager().callEvent(new ServerLoadEvent(ServerLoadEvent.LoadType.STARTUP));
        ((INetworkIo)(Object)getServer().getNetworkIo()).acceptConnections();

        CraftMagicNumbers.setupUnknownModdedMaterials();
        fixBukkitWorldEdit();
        CardboardMod.isAfterWorldLoad = true;
    }

    /*
    @Redirect(method = "createWorlds", at = @At(value = "NEW", args = "class=net/minecraft/server/world/ServerWorld", ordinal = 1))
    private ServerWorld cardboard$spiltListener(MinecraftServer server, Executor dispatcher,
                                             LevelStorage.Session levelStorageAccess,
                                             ServerWorldProperties serverLevelData, RegistryKey dimension,
                                             DimensionOptions levelStem, WorldGenerationProgressListener progressListener,
                                             boolean isDebug, long biomeZoomSeed, List customSpawners, boolean tickTime,
                                             RandomSequencesState randomSequences) {
        WorldGenerationProgressListener listener = this.worldGenerationProgressListenerFactory.create(11);
        return new ServerWorld(server, dispatcher, levelStorageAccess, serverLevelData,
                dimension, levelStem, listener, isDebug, biomeZoomSeed, customSpawners, tickTime, randomSequences);
    }
    
    @Inject(method = "createWorlds",
            at = @At(value = "INVOKE",
            remap = false,
            target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
            ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    private void cardboard$initWorld(WorldGenerationProgressListener worldGenerationProgressListener,
                                     CallbackInfo ci, ServerWorldProperties serverWorldProperties,
                                     boolean wat, Registry registry, GeneratorOptions generatorOptions, long bl, long l,
                                     List list,  DimensionOptions dimensionOptions,
                                     ServerWorld serverWorld) {
        cardboard$initLevel(serverWorld);
    }

    @Inject(method = "createWorlds",
            at = @At(value = "INVOKE",
                    remap = false,
                    target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    private void cardboard$initWorld0(WorldGenerationProgressListener worldGenerationProgressListener,
            CallbackInfo ci, ServerWorldProperties serverWorldProperties,
            boolean wat, Registry registry, GeneratorOptions generatorOptions, long bl, long l,
            List list,  DimensionOptions dimensionOptions,
            ServerWorld serverWorld) {
        cardboard$initLevel(serverWorld);
        cardboard$initializedLevel(serverWorld, serverWorldProperties, saveProperties, generatorOptions);
    }
    */

    @Override
    public void addLevel(ServerWorld level) {
        this.worlds.put(level.getRegistryKey(), level);
    }

    @Override
    public void removeLevel(ServerWorld level) {
        ServerWorldEvents.UNLOAD.invoker().onWorldUnload(((MinecraftServer) (Object) this), level);
        this.worlds.remove(level.getRegistryKey());
    }

    public void updateDifficulty() {
        ((MinecraftServer)(Object)this).setDifficulty(((DedicatedServer)(Object)this).getProperties().difficulty.get(), true);
    }

    /**
     * WorldEdit does not like hybrid servers.
     */
    private void fixBukkitWorldEdit() {
        try {
            if (!Bukkit.getPluginManager().isPluginEnabled("WorldEdit"))
                return;

            ClassLoader cl = Bukkit.getPluginManager().getPlugin("WorldEdit").getClass().getClassLoader();
            Class<?> ITEM_TYPE = Class.forName("com.sk89q.worldedit.world.item.ItemType", true, cl);
            Class<?> BLOCK_TYPE = Class.forName("com.sk89q.worldedit.world.block.BlockType", true, cl);

            Object REGISTRY_ITEM = ITEM_TYPE.getDeclaredField("REGISTRY").get(null);
            Method REGISTER_ITEM = null;
            for (Method m : REGISTRY_ITEM.getClass().getMethods()) {
                if (m.getName().equalsIgnoreCase("register")) {
                    REGISTER_ITEM = m;
                    break;
                }
            }

            Object REGISTRY_BLOCK = BLOCK_TYPE.getDeclaredField("REGISTRY").get(null);
            Method REGISTER_BLOCK = null;
            for (Method m : REGISTRY_BLOCK.getClass().getMethods()) {
                if (m.getName().equalsIgnoreCase("register")) {
                    REGISTER_BLOCK = m;
                    break;
                }
            }
            HashMap<String, Material> moddedMaterials = CraftMagicNumbers.getModdedMaterials();

            if (moddedMaterials.size() > 0)
                CardboardMod.LOGGER.info("Adding Modded blocks/items to WorldEdit registry...");
            for (String mid : moddedMaterials.keySet()) {
                try {
                    REGISTER_ITEM.invoke(REGISTRY_ITEM, "minecraft:" + mid.toLowerCase(), ITEM_TYPE.getConstructor(String.class).newInstance(mid));
                    REGISTER_BLOCK.invoke(REGISTRY_BLOCK, "minecraft:" + mid.toLowerCase(), BLOCK_TYPE.getConstructor(String.class).newInstance(mid));
                } catch (Exception e) {
                }
            }
            if (moddedMaterials.size() > 0) {
                CardboardMod.LOGGER.info("Added " + moddedMaterials.size() + "Modded blocks/items to WorldEdit registry.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    @Override
    public void loadSpawn(WorldGenerationProgressListener worldloadlistener, ServerWorld worldserver) {
        this.forceTicks = true;

        CardboardMod.LOGGER.info("Preparing start region for world " + worldserver.getRegistryKey().getValue());
        BlockPos blockposition = worldserver.getSpawnPos();

        worldloadlistener.start(new ChunkPos(blockposition));
        ServerChunkManager chunkproviderserver = worldserver.getChunkManager();

        //chunkproviderserver.getLightingProvider().setTaskBatchSize(500);
        this.tickStartTimeNanos = Util.getMeasuringTimeMs();
        chunkproviderserver.addTicket(ChunkTicketType.START, new ChunkPos(blockposition), 11); // , Unit.INSTANCE);

        while (chunkproviderserver.getTotalChunksLoadedCount() != 441)
            this.executeModerately();

        this.executeModerately();

        if (true) {
            ServerWorld worldserver1 = worldserver;
            ForcedChunkState forcedchunk = ((IMixinPersistentStateManager)worldserver.getPersistentStateManager()).Iget();

            if (forcedchunk != null) {
                LongIterator longiterator = forcedchunk.getChunks().iterator();

                while (longiterator.hasNext()) {
                    long i = longiterator.nextLong();
                    ChunkPos chunkcoordintpair = new ChunkPos(i);
                    worldserver1.getChunkManager().setChunkForced(chunkcoordintpair, true);
                }
            }
        }

        this.executeModerately();
        worldloadlistener.stop();
        //chunkproviderserver.getLightingProvider().setTaskBatchSize(5);
        this.updateMobSpawnOptions();

        this.forceTicks = false;
    }*/
    
    /*
    @Override
    public void loadSpawn(WorldGenerationProgressListener a, ServerWorld b) {
    	prepareLevels(a, b);
    }
    */
    
    /**
     * Prepare Levels 1.21.9
     */
    @Override
    public void cardboard$prepareLevel(ServerWorld serverLevel) {
    	this.forceTicks = true;
    	ChunkLoadingCounter chunkLoadCounter = new ChunkLoadingCounter();
    	chunkLoadCounter.load(serverLevel, () -> {
    		ChunkTicketManager ticketStorage = serverLevel.getPersistentStateManager().get(ChunkTicketManager.STATE_TYPE);
    		if (ticketStorage != null) {
    			ticketStorage.promoteToRealTickets();
    		}
    	});

    	IServerWorld world = (IServerWorld) serverLevel;

    	world.cardboard$levelLoadListener().init(ChunkLoadProgress.Stage.LOAD_INITIAL_CHUNKS, chunkLoadCounter.getTotalChunks());

    	do {
    		world.cardboard$levelLoadListener()
    		.progress(ChunkLoadProgress.Stage.LOAD_INITIAL_CHUNKS, chunkLoadCounter.getFullChunks(), chunkLoadCounter.getTotalChunks());
    		this.executeModerately();
    	} while (chunkLoadCounter.getNonFullChunks() > 0);

    	world.cardboard$levelLoadListener().finish(ChunkLoadProgress.Stage.LOAD_INITIAL_CHUNKS);
    	serverLevel.setMobSpawnOptions(serverLevel.worldProperties.getDifficulty() != Difficulty.PEACEFUL && serverLevel.getGameRules().getValue(GameRules.SPAWN_MONSTERS));
    	this.refreshSpawnPoint();
    	this.forceTicks = false;
    	new WorldLoadEvent(serverLevel.getWorld()).callEvent();
    }

    @Shadow
    public void refreshSpawnPoint() {
    	// Shadowed
    }
    
    /*
    public void prepareLevels(WorldGenerationProgressListener listener, ServerWorld serverLevel) {
        int i2;
        this.forceTicks = true;
        CardboardMod.LOGGER.info("Preparing start region for dim: " + serverLevel.getRegistryKey().getValue());
        BlockPos sharedSpawnPos = serverLevel.getSpawnPos();
        listener.start(new ChunkPos(sharedSpawnPos));
        ServerChunkManager chunkSource = serverLevel.getChunkManager();
        this.tickStartTimeNanos = Util.getMeasuringTimeNano();
        serverLevel.setSpawnPos(sharedSpawnPos, serverLevel.getSpawnAngle());
        int _int = serverLevel.getGameRules().getInt(GameRules.SPAWN_CHUNK_RADIUS);
        int n = i2 = _int > 0 ? MathHelper.square(WorldGenerationProgressListener.getStartRegionSize(_int)) : 0;
        while (chunkSource.getTotalChunksLoadedCount() < i2) {
            this.executeModerately();
        }
        this.executeModerately();
        ServerWorld serverLevel1 = serverLevel;
        ChunkTicketManager ticketStorage = serverLevel1.getPersistentStateManager().get(ChunkTicketManager.STATE_TYPE);
        if (ticketStorage != null) {
            ticketStorage.promoteToRealTickets();
        }
        this.executeModerately();
        listener.stop();
        this.updateMobSpawnOptions();
        // serverLevel.setMobSpawnOptions(serverLevel.getDifficulty() != Difficulty.PEACEFUL && ((MinecraftDedicatedServer)(Object)this).propertiesLoader.getPropertiesHandler().spawnMonsters);
        this.forceTicks = false;
    }
    */
    
    @Shadow
    private void updateMobSpawnOptions() {
    	
    }

    @Deprecated
    private void updateMobSpawnOptions_1_15_2() {
        /*
    	Iterator<ServerWorld> iterator = ((MinecraftServer)(Object)this).getWorlds().iterator();

        while (iterator.hasNext()) {
            ServerWorld worldserver = (ServerWorld) iterator.next();

            worldserver.setMobSpawnOptions(((MinecraftServer)(Object)this).isMonsterSpawningEnabled(),
                    ((MinecraftServer)(Object)this).shouldSpawnAnimals());
        }
        */
    	this.updateMobSpawnOptions();
    }

    private void executeModerately() {
        this.runTasks();
        java.util.concurrent.locks.LockSupport.parkNanos("executing tasks", 1000L);
    }

    @Inject(at = @At("HEAD"), method = "shouldKeepTicking", cancellable = true)
    public void shouldKeepTicking_BF(CallbackInfoReturnable<Boolean> ci) {
        boolean bl = this.forceTicks;
        if (bl) ci.setReturnValue(bl);
    }

    @Inject(at = @At("HEAD"), method = "tickWorlds")
    public void doBukkitRunnables(BooleanSupplier b, CallbackInfo ci) {
        ((CraftScheduler)CraftServer.INSTANCE.getScheduler()).mainThreadHeartbeat(ticks);
        while (!processQueue.isEmpty())
            processQueue.remove().run();
    }

    @Override
    public void cardboard_runOnMainThread(Runnable r) {
        System.out.print("runOnMainThread");
        processQueue.add(r);
    }

    private boolean hasStopped = false;
    private final Object stopLock = new Object();
    public final boolean hasStopped() {
        synchronized (stopLock) {
            return hasStopped;
        }
    }

    @Inject(at = @At("HEAD"), method = "shutdown")
    public void doStop(CallbackInfo ci) {
        synchronized(stopLock) {
            if (hasStopped) return;
            hasStopped = true;
        }

        if (null != CraftServer.INSTANCE)
            CraftServer.INSTANCE.getPluginManager().disablePlugins();
    }

    // public void initWorld(ServerWorld worldserver, ServerWorldProperties worldProperties, SaveProperties saveData, GeneratorOptions generatorsettings) {
    public void initWorld(ServerWorld serverLevel, LevelProperties serverLevelData, GeneratorOptions worldOptions) {
        cardboard$initLevel(serverLevel);
        cardboard$initializedLevel(serverLevel, serverLevelData, worldOptions);
    }

    private void cardboard$initLevel(ServerWorld serverWorld) {
        if (((CraftServer) Bukkit.getServer()).scoreboardManager == null) {
            ((CraftServer) Bukkit.getServer()).scoreboardManager = new CardboardScoreboardManager((MinecraftServer) (Object) this, serverWorld.getScoreboard());
        }
        // Bukkit.getPluginManager().callEvent(new WorldInitEvent(((IMixinWorld) serverWorld).getCraftWorld()));
    }

    private void cardboard$initializedLevel(ServerWorld worldserver, ServerWorldProperties worldProperties, GeneratorOptions generatorsettings) {
        boolean flag = false;
        // TODO Bukkit generators
        // WorldBorder worldborder = worldserver.getWorldBorder();

        // worldborder.load(worldProperties.getWorldBorder().get());

        this.paper$initWorldBorder(worldProperties, worldserver);
        
        Bukkit.getPluginManager().callEvent(new WorldInitEvent(((IMixinWorld) worldserver).getCraftWorld()));
        
        if (!worldProperties.isInitialized()) {
            try {
            	// TODO IMPORTANT
                setupSpawn(worldserver, worldProperties, generatorsettings.hasBonusChest(), flag, ((IServerWorld) worldserver).cardboard$levelLoadListener());
                worldProperties.setInitialized(true);
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.create(throwable, "Exception initializing level");
                throw new CrashException(crashreport);
            }

            worldProperties.setInitialized(true);
        }
        
        GlobalPos globalPos = ((MinecraftServer) (Object) this).getSpawnPos();
        ((IServerWorld) worldserver).cardboard$levelLoadListener().initSpawnPos(globalPos.dimension(), new ChunkPos(globalPos.pos()));
        /*
        if (worldProperties.getCustomBossEvents() != null) {
           this.getBossBarManager().readNbt(serverLevelData.getCustomBossEvents(), this.getRegistryManager());
        }
        */
    }
    
    private void paper$initWorldBorder(ServerWorldProperties worldProperties, ServerWorld serverLevel) {
        Optional<WorldBorder.Properties> legacyWorldBorderSettings = worldProperties.getWorldBorder();
        if (legacyWorldBorderSettings.isPresent()) {
           WorldBorder.Properties settings = legacyWorldBorderSettings.get();
           PersistentStateManager dataStorage1 = serverLevel.getPersistentStateManager();
           if (dataStorage1.get(WorldBorder.TYPE) == null) {
              double coordinateScale = serverLevel.getDimension().coordinateScale();
              WorldBorder.Properties settings1 = new WorldBorder.Properties(
                 settings.centerX() / coordinateScale,
                 settings.centerZ() / coordinateScale,
                 settings.damagePerBlock(),
                 settings.safeZone(),
                 settings.warningBlocks(),
                 settings.warningTime(),
                 settings.size(),
                 settings.lerpTime(),
                 settings.lerpTarget()
              );
              WorldBorder worldBorder = new WorldBorder(settings1);
              worldBorder.ensureInitialized(serverLevel.getTime());
              dataStorage1.set(WorldBorder.TYPE, worldBorder);
           }

           worldProperties.setWorldBorder(Optional.empty());
        }

        // TODO
        // serverLevel.getWorldBorder().world = serverLevel;
        serverLevel.getWorldBorder().setMaxRadius(this.getServer().getMaxWorldBorderRadius());
        this.getServer().getPlayerManager().setMainWorld(serverLevel);
     }

    
    @Override
    public void createLevel(
    	      DimensionOptions levelStem, PaperWorldLoader.WorldLoadingInfo loadingInfo, LevelStorage.Session levelStorageAccess, LevelProperties serverLevelData
    	   ) {
    	
    	MinecraftServer server = (MinecraftServer) (Object) this;
    	
    	GeneratorOptions worldOptions = serverLevelData.getGeneratorOptions();
        long seed = worldOptions.getSeed();
        long l = BiomeAccess.hashSeed(seed);
        List<SpecialSpawner> list = ImmutableList.of(
           new PhantomSpawner(), new PatrolSpawner(), new CatSpawner(), new ZombieSiegeManager(), new WanderingTraderManager(serverLevelData)
        );

        // ChunkGenerator chunkGenerator = this..getGenerator(loadingInfo.name());
        // BiomeProvider biomeProvider = this.server.getBiomeProvider(loadingInfo.name());

        WorldInfo worldInfo = new CraftWorldInfo(
           serverLevelData,
           levelStorageAccess,
           Environment.getEnvironment(loadingInfo.dimension()),
           levelStem.dimensionTypeEntry().value(),
           levelStem.chunkGenerator(),
           server.getRegistryManager()
        );
        /*
        if (biomeProvider == null && chunkGenerator != null) {
           biomeProvider = chunkGenerator.getDefaultBiomeProvider(worldInfo);
        }
        */

        RegistryKey<World> dimensionKey = RegistryKey.of(RegistryKeys.WORLD, loadingInfo.stemKey().getValue());
        ServerWorld serverLevel;
        if (loadingInfo.stemKey() == DimensionOptions.OVERWORLD) {
           serverLevel = new ServerWorld(
              server,
              server.workerExecutor,
              levelStorageAccess,
              serverLevelData,
              dimensionKey,
              levelStem,
              serverLevelData.isDebugWorld(),
              l,
              list,
              true,
              null
              /* ,
              Environment.getEnvironment(loadingInfo.dimension()),
              chunkGenerator,
              biomeProvider */
           );
           this.saveProperties = serverLevelData;
           this.saveProperties.setGameMode(((MinecraftDedicatedServer)(Object)this).getProperties().gameMode.get());
           PersistentStateManager dataStorage = serverLevel.getPersistentStateManager();
           // this.initScoreboard(dataStorage);
           
           
           this.getServer().getScoreboard().read(((ScoreboardState)dataStorage.getOrCreate(ScoreboardState.TYPE)).getPackedState());
           
           this.dataCommandStorage = new DataCommandStorage(dataStorage);
           CraftServer.INSTANCE.scoreboardManager = new CardboardScoreboardManager(server, serverLevel.getScoreboard());
        } else {
           List<SpecialSpawner> spawners;
           
           // Note: add useDimensionTypeForCustomSpawners (default = false)
           
           if (false && levelStem.dimensionTypeEntry().matchesKey(DimensionTypes.OVERWORLD)) {
              spawners = list;
           } else {
              spawners = Collections.emptyList();
           }

           serverLevel = new ServerWorld(
              server,
              server.workerExecutor,
              levelStorageAccess,
              serverLevelData,
              dimensionKey,
              levelStem,
              this.saveProperties.isDebugWorld(),
              l,
              spawners,
              true,
              server.getOverworld().getRandomSequences()/*,
              Environment.getEnvironment(loadingInfo.dimension()),
              chunkGenerator,
              biomeProvider
              */
           );
        }

        this.addLevel(serverLevel);
        this.initWorld(serverLevel, serverLevelData, worldOptions);
    }

    /*
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerChunkManager;getChunkGenerator()Lnet/minecraft/world/gen/chunk/ChunkGenerator;"), method = "setupSpawn")
    private static void setupSpawn_BukkitGenerators(ServerWorld world, ServerWorldProperties swp, boolean bonusChest, boolean debugWorld, CallbackInfo ci) {
        // TODO Bukkit Generators
    }
    */

    /*
    @Shadow
    private static void setupSpawn(ServerWorld world, ServerWorldProperties swp, boolean bonusChest, boolean debugWorld) {
    }
    */
    
    @Shadow
    private static void setupSpawn( ServerWorld world, ServerWorldProperties worldProperties, boolean bonusChest, boolean debugWorld, ChunkLoadProgress loadProgress) {
    	// Shadowed
    }

}
