package org.cardboardpowered.mixin.world;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

import org.bukkit.craftbukkit.CraftServer;
import org.cardboardpowered.CardboardConfig;
import org.cardboardpowered.impl.world.WorldImpl;
import org.cardboardpowered.interfaces.IServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.javazilla.bukkitfabric.BukkitFabricMod;
import com.javazilla.bukkitfabric.Utils;
import com.javazilla.bukkitfabric.interfaces.IMixinWorld;

import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerEntityManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.entity.EntityLookup;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.SpecialSpawner;

@Mixin(ServerWorld.class)
public class MixinServerWorld extends MixinWorld implements IServerWorld {

   // @Shadow
   // public boolean inEntityTick;

    /*@Inject(at = @At("TAIL"), method = "<init>")
    public void addToBukkit( ... ,  CallbackInfo ci){
        // ((CraftServer)Bukkit.getServer()).addWorldToMap(getWorldImpl());
    }*/

	private LevelStorage.Session cardboard$session;
	private UUID cardboard$uuid;

	@Inject(method = "<init>", at = @At(value = "RETURN"))
    private void banner$initWorldServer(
    		MinecraftServer minecraftserver,
    		Executor executor,
    		LevelStorage.Session convertable_conversionsession,
    		ServerWorldProperties iworlddataserver, RegistryKey<World> resourcekey,
    		DimensionOptions worlddimension, WorldGenerationProgressListener worldloadlistener,
    		boolean flag, long i2, List<SpecialSpawner> list, boolean flag1,
    		RandomSequencesState randomsequences, CallbackInfo ci
    	) {
		
		if (CardboardConfig.DEBUG_OTHER) {
			BukkitFabricMod.LOGGER.info("Debug: getting world uuid");
		}

        this.cardboard$session = convertable_conversionsession;
        this.cardboard$uuid = Utils.getWorldUUID(cardboard$session.getWorldDirectory(((ServerWorld)(Object)this).getRegistryKey()).toFile());

    }

    @Inject(at = @At("HEAD"), method = "save")
    public void doWorldSaveEvent(ProgressListener aa, boolean bb, boolean cc, CallbackInfo ci) {
        if (!cc) {
            org.bukkit.Bukkit.getPluginManager().callEvent(new org.bukkit.event.world.WorldSaveEvent(getWorldImpl())); // WorldSaveEvent
        }
    }
    
    @Shadow 
    public ServerWorldProperties worldProperties;

    @Override
    public ServerWorldProperties cardboard_worldProperties() {
        return worldProperties;
    }
    
    @Override
    public WorldImpl getWorld() {
        return ((IMixinWorld)(Object)this).getWorldImpl();
    }

	@Override
	public CraftServer getCraftServer() {
		// TODO Auto-generated method stub
		return CraftServer.INSTANCE;
	}
	
	@Shadow
	private ServerEntityManager<Entity> entityManager;

	@Shadow
	public EntityLookup<Entity> getEntityLookup() {
		return this.entityManager.getLookup();
	}
	
	@Override
	public void cardboard$set_uuid(UUID id) {
		this.cardboard$uuid = id;
	}

	@Override
	public UUID cardboard$get_uuid() {
		return this.cardboard$uuid;
	}

    /**
     * @reason MapInitalizeEvent
     * @author BukkitFabricMod
     */
    //@Overwrite
   // public MapState getMapState(String s) {
        // TODO 1.17ify
       // return null; return (MapState) CraftServer.INSTANCE.getServer().getOverworld().getPersistentStateManager().get(() -> {
           /*MapState newMap = new MapState(s);
            MapInitializeEvent event = new MapInitializeEvent(((IMixinMapState)newMap).getMapViewBF());
            Bukkit.getServer().getPluginManager().callEvent(event);
            return newMap;
        }, s);*/
  //  }

    // TODO 1.17ify
   /* @Inject(at = @At("TAIL"), method = "unloadEntity")
    public void unvalidateEntityBF(Entity entity, CallbackInfo ci) {
        ((IMixinEntity)entity).setValid(false);
    } 

    @Inject(at = @At("TAIL"), method = "loadEntityUnchecked")
    public void validateEntityBF(Entity entity, CallbackInfo ci) {
        //if (!this.inEntityTick) {
            IMixinEntity bf = (IMixinEntity) entity;
            bf.setValid(true);
            if (null == bf.getOriginBF() && null != bf.getBukkitEntity()) {
                // Paper's Entity Origin API
                bf.setOriginBF(bf.getBukkitEntity().getLocation());
            }
       // }
    }*/ 

}