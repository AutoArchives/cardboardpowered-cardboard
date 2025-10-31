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

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
//<<<<<<< HEAD
import com.google.common.collect.Lists;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.cardboardpowered.interfaces.IMixinPlayNetworkHandler;
import org.cardboardpowered.interfaces.IMixinPlayerManager;
import org.cardboardpowered.interfaces.IMixinServerEntityPlayer;
import org.cardboardpowered.interfaces.IMixinServerLoginNetworkHandler;
import org.cardboardpowered.interfaces.IMixinWorld;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;

import me.isaiah.common.ICommonMod;
import me.isaiah.common.cmixin.IMixinMinecraftServer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.BannedIpEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity.RespawnPos;
import net.minecraft.server.network.SpawnLocating;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.Formatting;
import net.minecraft.util.UserCache;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
//=======
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Bukkit;
//>>>>>>> upstream/ver/1.20
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerRespawnEvent.RespawnFlag;
import org.bukkit.event.player.PlayerRespawnEvent.RespawnReason;
import org.cardboardpowered.CardboardConfig;
import org.cardboardpowered.CardboardMod;
import org.cardboardpowered.ChunkTicketBridge;
import org.cardboardpowered.impl.entity.CraftPlayer;
import org.cardboardpowered.impl.world.CraftWorld;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.ReadView;
import net.minecraft.world.WorldProperties;
import net.minecraft.network.packet.s2c.play.*;
//>>>>>>> upstream/ver/1.20

@Mixin(PlayerManager.class)
public abstract class MixinPlayerManager implements IMixinPlayerManager {

    @Shadow
    public List<ServerPlayerEntity> players;
    
    @Shadow private MinecraftServer server;

    @Shadow
    public void sendCommandTree(ServerPlayerEntity player) {}

    @Shadow
    public void sendWorldInfo(ServerPlayerEntity player, ServerWorld world) {}

    @Shadow
    public void savePlayerData(ServerPlayerEntity player) {}

    @Shadow
    public void sendPlayerStatus(ServerPlayerEntity player) {}

    @Shadow
    public Map<UUID, ServerPlayerEntity> playerMap;

    // ServerPlayerEntity.forceSetPositionRotation
    public void Player_forceSetPositionRotation(ServerPlayerEntity p, double x, double y, double z, float yaw, float pitch) {
        p.refreshPositionAndAngles(x, y, z, yaw, pitch);
        p.networkHandler.syncWithPlayerPosition();
    }
    
    @Override
    public ServerPlayerEntity moveToWorld(ServerPlayerEntity player, ServerWorld worldserver, boolean flag, Location to, boolean avoidSuffocation) {
    	return respawn(player, true, Entity.RemovalReason.CHANGED_DIMENSION, null, to);
    }
    
    @Override
    public ServerPlayerEntity respawn(ServerPlayerEntity player, boolean keepInventory, Entity.RemovalReason reason, PlayerRespawnEvent.RespawnReason eventReason, Location location) {
       
    	// DEBUG
    	if (CardboardConfig.DEBUG_PLAYER) {
    		CardboardMod.LOGGER.info("DEBUG: PlayerManager.respawn called");
    	}
    	
    	ServerWorld level1;
        TeleportTarget teleportTransition;
        player.stopRiding();
        this.players.remove(player);
        // this.playersByName.remove(player.getNameForScoreboard().toLowerCase(Locale.ROOT));
        player.getWorld().removePlayer(player, reason);
        ServerPlayerEntity serverPlayer = player;
        ServerWorld fromWorld = player.getWorld();
        player.notInAnyWorld = false;
        serverPlayer.networkHandler = player.networkHandler;
        serverPlayer.copyFrom(player, keepInventory);
        serverPlayer.setId(player.getId());
        serverPlayer.setMainArm(player.getMainArm());
        for (String string : player.getCommandTags()) {
            serverPlayer.addCommandTag(string);
        }
        boolean isBedSpawn = false;
        boolean isRespawn = false;
        boolean isAnchorSpawn = false;
        if (location == null) {
            teleportTransition = ((IMixinServerEntityPlayer) player).findRespawnPositionAndUseSpawnBlock(!keepInventory, TeleportTarget.NO_OP, eventReason);
            // teleportTransition = player.getRespawnTarget(!keepInventory, TeleportTarget.NO_OP);
            
            if (!keepInventory) {
                ((IMixinServerEntityPlayer)player).reset();
            }
            if (teleportTransition == null) {
                return player;
            }
            isRespawn = true;
            location = CraftLocation.toBukkit(teleportTransition.position(), (org.bukkit.World)teleportTransition.world().getWorld(), teleportTransition.yaw(), teleportTransition.pitch());
        } else {
            teleportTransition = new TeleportTarget(((CraftWorld)location.getWorld()).getHandle(), CraftLocation.toVec3(location), Vec3d.ZERO, location.getYaw(), location.getPitch(), TeleportTarget.NO_OP);
        }
        if (teleportTransition == null) {
            return player;
        }
        ServerWorld level = teleportTransition.world();
        ((IMixinServerEntityPlayer)serverPlayer).spawnIn(level);
        serverPlayer.unsetRemoved();
        serverPlayer.setSneaking(false);
        Vec3d vec3 = teleportTransition.position();
        Player_forceSetPositionRotation(serverPlayer, vec3.x, vec3.y, vec3.z, teleportTransition.yaw(), teleportTransition.pitch());
        //serverPlayer.forceSetPositionRotation(vec3.x, vec3.y, vec3.z, teleportTransition.yaw(), teleportTransition.pitch());
        level.getChunkManager().addTicket(ChunkTicketBridge.POST_TELEPORT, new ChunkPos(MathHelper.floor(vec3.getX()) >> 4, MathHelper.floor(vec3.getZ()) >> 4), 1);
        if (teleportTransition.missingRespawnBlock()) {
        	
        	if (CardboardConfig.DEBUG_PLAYER) {
        		CardboardMod.LOGGER.info("teleportTransition#missingRespawnBlock!");
        	}
        	
            serverPlayer.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.NO_RESPAWN_BLOCK, 0.0f));
            // serverPlayer.setRespawnPosition(null, false, PlayerSetSpawnEvent.Cause.PLAYER_RESPAWN);
            serverPlayer.setSpawnPoint(null, false);
        }
        byte b2 = (byte)(keepInventory ? 1 : 0);
        ServerWorld serverLevel = serverPlayer.getWorld();
        WorldProperties levelData = serverLevel.getLevelProperties();
        serverPlayer.networkHandler.sendPacket(new PlayerRespawnS2CPacket(serverPlayer.createCommonPlayerSpawnInfo(serverLevel), b2));
        // serverPlayer.networkHandler.sendPacket(new ChunkLoadDistanceS2CPacket(serverLevel.spigotConfig.viewDistance));
        // serverPlayer.networkHandler.sendPacket(new SimulationDistanceS2CPacket(serverLevel.spigotConfig.simulationDistance));
        
        // serverPlayer.networkHandler.teleport(CraftLocation.toBukkit(serverPlayer.getPos(), (org.bukkit.World)serverLevel.getWorld(), serverPlayer.getYaw(), serverPlayer.getPitch()));
        player.teleport(worldserver1, location.getX(), location.getY(), location.getZ(), null, 0, 0, false);
        
        serverPlayer.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(level.getSpawnPos(), level.getSpawnAngle()));
        serverPlayer.networkHandler.sendPacket(new DifficultyS2CPacket(levelData.getDifficulty(), levelData.isDifficultyLocked()));
        serverPlayer.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(serverPlayer.experienceProgress, serverPlayer.totalExperience, serverPlayer.experienceLevel));
        this.sendStatusEffects(serverPlayer);
        this.sendWorldInfo(serverPlayer, level);
        this.sendCommandTree(serverPlayer);
        if (!serverPlayer.isDisconnected()) {
        	if (CardboardConfig.DEBUG_PLAYER) {
        		CardboardMod.LOGGER.info("calling onPlayerRespawned");
        	}
            level.onPlayerRespawned(serverPlayer);
            this.players.add(serverPlayer);
            // this.playersByName.put(serverPlayer.getNameForScoreboard().toLowerCase(Locale.ROOT), serverPlayer);
            this.playerMap.put(serverPlayer.getUuid(), serverPlayer);
        }
        serverPlayer.setHealth(serverPlayer.getHealth());
        ServerPlayerEntity.Respawn respawnConfig = serverPlayer.getRespawn();
        if (!keepInventory && respawnConfig != null && (level1 = this.server.getWorld(respawnConfig.dimension())) != null) {
            BlockPos blockPos = respawnConfig.pos();
            BlockState blockState = ((World)level1).getBlockState(blockPos);
            if (blockState.isOf(Blocks.RESPAWN_ANCHOR)) {
                serverPlayer.networkHandler.sendPacket(new PlaySoundS2CPacket(SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE, SoundCategory.BLOCKS, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0f, 1.0f, level.getRandom().nextLong()));
            }
            if (!teleportTransition.missingRespawnBlock()) {
                if (blockState.isIn(BlockTags.BEDS)) {
                    isBedSpawn = true;
                } else if (blockState.isOf(Blocks.RESPAWN_ANCHOR)) {
                    isAnchorSpawn = true;
                }
            }
        }
        this.sendPlayerStatus(player);
        player.sendAbilitiesUpdate();
        for (StatusEffectInstance mobEffect : player.getStatusEffects()) {
            player.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(player.getId(), mobEffect, false));
        }
        player.worldChanged(level);
        if (fromWorld != level) {
            PlayerChangedWorldEvent event = new PlayerChangedWorldEvent((Player)((IMixinServerEntityPlayer)player).getBukkitEntity(), fromWorld.getWorld());
            CraftServer.INSTANCE.getPluginManager().callEvent(event);
        }
        if (player.isDisconnected()) {
            this.savePlayerData(player);
        }
        if (isRespawn) {
        	// TODO
            new PlayerPostRespawnEvent((Player)((IMixinServerEntityPlayer)player).getBukkitEntity(), location, isBedSpawn, isAnchorSpawn, teleportTransition.missingRespawnBlock(), eventReason).callEvent();
        }
        return serverPlayer;
    }
    
    @Shadow void sendStatusEffects( ServerPlayerEntity player) {}
    
    /*
    @Override
    public ServerPlayerEntity moveToWorld(ServerPlayerEntity player, ServerWorld worldserver, boolean flag, Location location, boolean avoidSuffocation) {
        boolean flag2 = false;
        BlockPos blockposition = player.getSpawnPointPosition();
        float f = player.getSpawnAngle();
        boolean flag1 = player.isSpawnForced();
        if (location == null) {
            boolean isBedSpawn = false;
            ServerWorld worldserver1 = CraftServer.server.getWorld(player.getSpawnPointDimension());
            if (worldserver1 != null) {
                Optional<?> optional;

                if (blockposition != null)
                    optional = ServerPlayerEntity.findRespawnPosition(worldserver1, blockposition, f, flag1, flag).map(RespawnPos::pos);
                else optional = Optional.empty();

                if (optional.isPresent()) {
                    BlockState iblockdata = worldserver1.getBlockState(blockposition);
                    boolean flag3 = iblockdata.isOf(Blocks.RESPAWN_ANCHOR);
                    Vec3d vec3d = (Vec3d) optional.get();
                    float f1;
                    if (!iblockdata.isIn(BlockTags.BEDS) && !flag3) {
                        f1 = f;
                    } else {
                        Vec3d vec3d1 = Vec3d.ofBottomCenter((Vec3i) blockposition).subtract(vec3d).normalize();
                        f1 = (float) MathHelper.wrapDegrees(MathHelper.atan2(vec3d1.z, vec3d1.x) * 57.2957763671875D - 90.0D);
                    }

                    player.refreshPositionAndAngles(vec3d.x, vec3d.y, vec3d.z, f1, 0.0F);
                    player.setSpawnPoint(worldserver1.getRegistryKey(), blockposition, f, flag1, false);
                    flag2 = !flag && flag3;
                    isBedSpawn = true;
                    location = new Location(((IMixinWorld)worldserver1).getCraftWorld(), vec3d.x, vec3d.y, vec3d.z);
                } else if (blockposition != null)
                    player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.NO_RESPAWN_BLOCK, 0.0F));
            }

            if (location == null) {
                worldserver1 = CraftServer.server.getWorld(World.OVERWORLD);
                blockposition = player.getSpawnPointPosition();
                location = new Location(((IMixinWorld)worldserver1).getCraftWorld(), (double) ((float) blockposition.getX() + 0.5F), (double) ((float) blockposition.getY() + 0.1F), (double) ((float) blockposition.getZ() + 0.5F));
            }

            Player respawnPlayer = CraftServer.INSTANCE.getPlayer(player);
            PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(respawnPlayer, location, isBedSpawn && !flag2, flag2);
            CraftServer.INSTANCE.getPluginManager().callEvent(respawnEvent);

            if (player.isDisconnected()) return player;

            location = respawnEvent.getRespawnLocation();
        } else location.setWorld(((IMixinWorld)worldserver).getCraftWorld());
        ServerWorld worldserver1 = ((CraftWorld) location.getWorld()).getHandle();
        World fromWorld = player.getWorld();
        
        // TODO: 1.21.4: Check this
        player.teleport(worldserver1, location.getX(), location.getY(), location.getZ(), null, 0, 0, false);

        if (fromWorld != ((CraftWorld) location.getWorld()).getHandle()) {
            PlayerChangedWorldEvent event = new PlayerChangedWorldEvent((Player) ((IMixinServerEntityPlayer)player).getBukkitEntity(), ((IMixinWorld)fromWorld).getCraftWorld());
            CraftServer.INSTANCE.getPluginManager().callEvent(event);
        }
        return player;
    }
    */

    @Unique private CraftPlayer plr;

    @Inject(method = "onPlayerConnect", at = @At("HEAD"))
    public void onConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        this.plr = (CraftPlayer) CraftServer.INSTANCE.getPlayer(player);
    }

    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"))
    public void firePlayerJoinEvent(PlayerManager instance, Text message, boolean overlay) {
        CraftPlayer plr;

        if(this.plr == null) {
            instance.broadcast(message, overlay);
            return;
        } else {
            plr = this.plr;
            this.plr = null;
        }

        String key = "multiplayer.player.joined";
        Text name = plr.nms.getDisplayName();

        String joinMessage = Formatting.YELLOW + Text.translatable(key, name).getString();

        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(plr, joinMessage);
        CraftEventFactory.callEvent(playerJoinEvent);
        IMixinPlayNetworkHandler ims = (IMixinPlayNetworkHandler)plr.nms.networkHandler;

        if (!ims.cb_get_connection().isOpen()) {
            return;
        }

        joinMessage = playerJoinEvent.getJoinMessage();

        if (joinMessage != null && !joinMessage.isEmpty()) {
            for (Text line : CraftChatMessage.fromString(joinMessage)) {
                broadcast(line, entityplayer -> line, false);
            }
        }

    }

    @Inject(at = @At("HEAD"), method = "remove")
    public void firePlayerQuitEvent(ServerPlayerEntity player, CallbackInfo ci) {
        player.closeHandledScreen();

        PlayerQuitEvent playerQuitEvent = new PlayerQuitEvent(CraftServer.INSTANCE.getPlayer(player), "\u00A7e" + player.getDisplayName().getString() + " left the game");
        CraftServer.INSTANCE.getPluginManager().callEvent(playerQuitEvent);
        player.playerTick();
    }
    
    private static final Logger cb$LOGGER = LogUtils.getLogger();
    
    /**
     * todo: update our login code to use SpawnPrepareTask instead of our attemptLogin
     */
    private ServerWorld cardboard$getPlayerSpawn(ServerPlayerEntity player) {
        String string;
        GameProfile gameProfile = player.getGameProfile();
        UserCache profileCache = this.server.getUserCache();
        if (profileCache != null) {
            Optional<GameProfile> optional = profileCache.getByUuid(gameProfile.getId());
            string = optional.map(GameProfile::getName).orElse(gameProfile.getName());
            profileCache.add(gameProfile);
        } else {
            string = gameProfile.getName();
        }
        try (ErrorReporter.Logging scopedCollector = new ErrorReporter.Logging(player.getErrorReporterContext(), cb$LOGGER);){
            ServerWorld serverLevel;
            ServerWorld level;
            boolean[] invalidPlayerWorld;
            RegistryKey<World> resourceKey;
            Optional<ReadView> loadedPlayerData;
            Optional<ReadView> optional1;
            block32: {
                org.bukkit.World bWorld;
                block34: {
                    Optional<String> worldName;
                    block33: {
                        loadedPlayerData = optional1 = this.server
                				.getPlayerManager().loadPlayerData(player, scopedCollector);
                        if (loadedPlayerData.isPresent()) {
                            string = loadedPlayerData.flatMap(view -> view.getOptionalReadView("bukkit")).flatMap(t -> t.getOptionalString("lastKnownName")).orElse(string);
                        }
                        resourceKey = null;
                        invalidPlayerWorld = new boolean[]{false};
                        if (!loadedPlayerData.isPresent()) break block32;
                        ReadView playerData = loadedPlayerData.get();
                        Optional<Long> worldUUIDMost = playerData.getOptionalLong("WorldUUIDMost");
                        Optional<Long> worldUUIDLeast = playerData.getOptionalLong("WorldUUIDLeast");
                        worldName = playerData.getOptionalString("world");
                        if (!worldUUIDMost.isPresent() || !worldUUIDLeast.isPresent()) break block33;
                        bWorld = Bukkit.getServer().getWorld(new UUID(worldUUIDMost.get(), worldUUIDLeast.get()));
                        break block34;
                    }
                    if (!worldName.isPresent()) break block32;
                    bWorld = Bukkit.getServer().getWorld(worldName.get());
                }
                if (bWorld != null) {
                    resourceKey = ((CraftWorld)bWorld).getHandle().getRegistryKey();
                } else {
                    resourceKey = World.OVERWORLD;
                    invalidPlayerWorld[0] = true;
                }
            }
            if (resourceKey == null) {
                resourceKey = loadedPlayerData.flatMap(compoundTag -> {
                    Optional<RegistryKey<World>> result = compoundTag.read("Dimension", World.CODEC);
                    invalidPlayerWorld[0] = result.isEmpty();
                    return result;
                }).orElse(World.OVERWORLD);
            }
            if ((level = this.server.getWorld(resourceKey)) == null) {
            	cb$LOGGER.warn("Unknown respawn dimension {}, defaulting to overworld", resourceKey);
                serverLevel = this.server.getOverworld();
                invalidPlayerWorld[0] = true;
            } else {
                serverLevel = level;
            }

            player.setServerWorld(serverLevel);
            if (loadedPlayerData.isEmpty() || invalidPlayerWorld[0]) {
                player.refreshPositionAndAngles(player.getWorldSpawnPos(serverLevel, serverLevel.getSpawnPos()).toBottomCenterPos(), serverLevel.getSpawnAngle(), 0.0f);
            }
            return serverLevel;
        }
    }
    
    /**
     * todo: update our login code to use SpawnPrepareTask instead of our attemptLogin
     *
    private Location cardboard$getPlayerSpawn(ServerPlayerEntity player) {
    	Optional<ReadView> optional;
    	RegistryKey<World> resourceKey = null;
    	boolean[] invalidPlayerWorld = new boolean[]{false};

    	try (ErrorReporter.Logging scopedCollector = new ErrorReporter.Logging(cb$LOGGER)) {
    		optional = this.server
    				.getPlayerManager()
    				.loadPlayerData(player, scopedCollector)
    				.map(nbt -> NbtReadView.create(scopedCollector, this.server.getRegistryManager(), nbt));

    		bukkitData:
    			if (optional.isPresent()) {
    				ReadView playerData = optional.get();
    				Optional<Long> worldUUIDMost = playerData.getOptionalLong("WorldUUIDMost");
    				Optional<Long> worldUUIDLeast = playerData.getOptionalLong("WorldUUIDLeast");
    				Optional<String> worldName = playerData.getOptionalString("world");
    				org.bukkit.World bWorld;
    				if (worldUUIDMost.isPresent() && worldUUIDLeast.isPresent()) {
    					bWorld = Bukkit.getServer().getWorld(new UUID(worldUUIDMost.get(), worldUUIDLeast.get()));
    				} else {
    					if (!worldName.isPresent()) {
    						break bukkitData;
    					}

    					bWorld = Bukkit.getServer().getWorld(worldName.get());
    				}

    				if (bWorld != null) {
    					resourceKey = ((CraftWorld)bWorld).getHandle().getRegistryKey();
    				} else {
    					resourceKey = World.OVERWORLD;
    					invalidPlayerWorld[0] = true;
    				}
    			}


    		if (resourceKey == null) {
                resourceKey = loadedPlayerData.flatMap(compoundTag -> {
                    Optional<RegistryKey<World>> result = compoundTag.read("Dimension", World.CODEC);
                    invalidPlayerWorld[0] = result.isEmpty();
                    return result;
                }).orElse(World.OVERWORLD);
            }

    		if (resourceKey == null) {
    			resourceKey = savedPosition.dimension().orElse(null);
    		}

    		ServerWorld vanillaDefaultLevel = this.server.getWorld(respawnData.getDimension());
    		if (vanillaDefaultLevel == null) {
    			vanillaDefaultLevel = this.server.getOverworld();
    		}

    		ServerWorld serverLevel1;
    		if (resourceKey == null) {
    			serverLevel1 = vanillaDefaultLevel;
    		} else {
    			serverLevel1 = this.server.getWorld(resourceKey);
    			if (serverLevel1 == null) {
    				cb$LOGGER.warn("Unknown respawn dimension {}, defaulting to overworld", resourceKey);
    				serverLevel1 = vanillaDefaultLevel;
    			}
    		}

    		ServerWorld serverLevel = serverLevel1;
    		CompletableFuture<Vec3d> completableFuture = savedPosition.position()
    				.map(CompletableFuture::completedFuture)
    				.orElseGet(() -> SpawnLocating.locateSpawnPos(serverLevel, respawnData.getPos()));
    		Vec2f vec2 = savedPosition.rotation().orElse(new Vec2f(respawnData.yaw(), respawnData.pitch()));
    		// this.stage = new PrepareSpawnTask.LoadPlayerChunks(serverLevel, completableFuture, vec2);
    		
    		// CraftLocation.toBukkit(null, serverLevel, 0, 0)
    		
    		try {
				Vec3d d3 = completableFuture.get();
				Location loc = CraftLocation.toBukkit(d3, serverLevel, vec2.x, vec2.y);
				return loc;
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				return null;
			}
    		
    	}
    }
    */

    @Override
    public ServerPlayerEntity attemptLogin(ServerLoginNetworkHandler nethand, GameProfile profile, PlayerPublicKey key, String hostname) {
    	MutableText chatmessage;

        // Moved from processLogin
        // 1.18: UUID uuid = PlayerEntity.getUuidFromProfile(profile);
    	UUID uuid = ICommonMod.getIServer().get_uuid_from_profile(profile);
    	// UUID uuid = DynamicSerializableUuid.getUuidFromProfile(profile);
    	List<ServerPlayerEntity> list = Lists.newArrayList();

        ServerPlayerEntity entityplayer;

        for (int i = 0; i < this.players.size(); ++i) {
            entityplayer = (ServerPlayerEntity) this.players.get(i);
            if (entityplayer.getUuid().equals(uuid))
                list.add(entityplayer);
        }

        Iterator<ServerPlayerEntity> iterator = list.iterator();

        while (iterator.hasNext()) {
            entityplayer = (ServerPlayerEntity) iterator.next();
            savePlayerData(entityplayer); // Force the player's inventory to be saved
            entityplayer.networkHandler.disconnect(Text.of("multiplayer.disconnect.duplicate_login"));
        }

        IMixinServerLoginNetworkHandler ims = (IMixinServerLoginNetworkHandler)nethand;
        SocketAddress address = ims.cb_get_connection().getAddress();

        // me.isaiah.common.cmixin.IMixinPlayerManager imixin = (me.isaiah.common.cmixin.IMixinPlayerManager) (Object)this;
       // ServerPlayerEntity entity = imixin.InewPlayer(CraftServer.server, CraftServer.server.getWorld(World.OVERWORLD), profile);
        
        // TODO Test
        // Location spawnPos = cardboard$getPlayerSpawn(pce);
        // ServerWorld spawnWorld = null != spawnPos ? ((CraftWorld) spawnPos.getWorld()).getHandle() : CraftServer.server.getWorld(World.OVERWORLD);
        
        ServerPlayerEntity entity = new ServerPlayerEntity(CraftServer.server, CraftServer.server.getWorld(World.OVERWORLD), profile, SyncedClientOptions.createDefault());
        Player player = (Player) ((IMixinServerEntityPlayer)entity).getBukkitEntity();
        
        ServerWorld serverLevel = cardboard$getPlayerSpawn(entity);

        /*
        if (null != spawnPos) {
        	entity.refreshPositionAndAngles(BlockPos.ofFloored(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ()), spawnPos.getYaw(), spawnPos.getPitch());
        }
        */

        PlayerLoginEvent event = new PlayerLoginEvent(player, hostname, ((java.net.InetSocketAddress) address).getAddress(), ((java.net.InetSocketAddress) ims.cb_get_connection().channel.remoteAddress()).getAddress());
        
        if (((PlayerManager)(Object)this).getUserBanList().contains(profile) /*&& !((PlayerManager)(Object)this).getUserBanList().get(gameprofile).isInvalid()*/) {
            chatmessage = Text.translatable("multiplayer.disconnect.banned.reason", new Object[]{"TODO REASON!"});
            //chatmessage.append(new TranslatableTextContent("multiplayer.disconnect.banned.expiration", new Object[] {"TODO EXPIRE!"}));

            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, CraftChatMessage.fromComponent(chatmessage));
        } else if (!((PlayerManager)(Object)this).isWhitelisted(profile)) {
            chatmessage = Text.translatable("multiplayer.disconnect.not_whitelisted");
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "Server whitelisted!");
        } else if (((PlayerManager)(Object)this).getIpBanList().isBanned(address) /*&& !((PlayerManager)(Object)this).getIpBanList().get(socketaddress).isInvalid()*/) {
            BannedIpEntry ipbanentry = ((PlayerManager)(Object)this).getIpBanList().get(address);

            chatmessage = Text.translatable("multiplayer.disconnect.banned_ip.reason", new Object[]{ipbanentry.getReason()});
            //if (ipbanentry.getExpiryDate() != null)
            //    chatmessage.append(new TranslatableTextContent("multiplayer.disconnect.banned_ip.expiration", new Object[]{ipbanentry.getExpiryDate()}));

            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, CraftChatMessage.fromComponent(chatmessage));
        } else {
            if (this.players.size() >= ((PlayerManager)(Object)this).getMaxPlayerCount() && !((PlayerManager)(Object)this).canBypassPlayerLimit(profile))
                event.disallow(PlayerLoginEvent.Result.KICK_FULL, "Server is full");
        }

        CraftEventFactory.callEvent(event);
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            nethand.disconnect(Text.of(event.getKickMessage()));
            return null;
        }
        return entity;
    }

    @Shadow
    public void sendScoreboard(ServerScoreboard scoreboardserver, ServerPlayerEntity entityplayer) {
    }

    @Shadow public abstract void broadcast(Text message, Function<ServerPlayerEntity, Text> playerMessageFactory, boolean overlay);
    @Override
    public void sendScoreboardBF(ServerScoreboard newboard, ServerPlayerEntity handle) {
        sendScoreboard(newboard, handle);
    }
    
    /*@Redirect(at = @At(value = "INVOKE", 
            target = "class=net/minecraft/server/network/ServerPlayerEntity;"),
            method = "acceptPlayer")
    public ServerPlayerEntity acceptPlayer_createPlayer(PlayerManager man, GameProfile a, PlayerPublicKey key) {
        return cardboard_player;
    }*/
    
    /**
     * @author wdog5
     * @reason bukkit
     */
    /*
    @Overwrite
    public ServerPlayerEntity respawnPlayer(ServerPlayerEntity playerIn, boolean conqueredEnd, RemovalReason removalReason) {
        playerIn.stopRiding(); // CraftBukkit
        this.players.remove(playerIn);
        
        playerIn.getWorld().removePlayer(playerIn, Entity.RemovalReason.DISCARDED);
        BlockPos blockposition = playerIn.getSpawnPointPosition();
        float f = playerIn.getSpawnAngle();
        boolean flag1 = playerIn.isSpawnForced();
        // CraftBukkit start
        // Banner start - remain original field to compat with carpet
        ServerWorld worldserver_vanilla = this.server.getWorld(playerIn.getSpawnPointDimension());
        Optional<Vec3d> optional_vanilla;

        if (worldserver_vanilla != null && blockposition != null) {
            optional_vanilla = ServerPlayerEntity.findRespawnPosition(worldserver_vanilla, blockposition, f, flag1, conqueredEnd).map(RespawnPos::pos);
        } else {
            optional_vanilla = Optional.empty();
        }

        ServerWorld worldserver_vanilla_1 = worldserver_vanilla != null && optional_vanilla.isPresent() ? worldserver_vanilla : this.server.getOverworld();
        entityplayer_vanilla = new ServerPlayerEntity(this.server, worldserver_vanilla_1, playerIn.getGameProfile(), SyncedClientOptions.createDefault());
        // Banner end

        ServerPlayerEntity entityplayer1 = playerIn;
        fromWorld = ((IMixinServerEntityPlayer)playerIn).getBukkitEntity().getWorld();
        playerIn.notInAnyWorld = false;
        // CraftBukkit end

        if (null != playerIn.networkHandler) {
        
        	//entityplayer1.networkHandler = playerIn.networkHandler;
        }
        ((IMixinServerEntityPlayer)entityplayer1).copyFrom_unused(playerIn, conqueredEnd);
        entityplayer1.setId(playerIn.getId());
        entityplayer1.setMainArm(playerIn.getMainArm());

        // for (String s : playerIn.getCommandTags()) {
        //    entityplayer1.addCommandTag(s);
        // }

        boolean flag2 = false;

        // CraftBukkit start - fire PlayerRespawnEvent
        if (banner$loc == null) {
            boolean isBedSpawn = false;
            ServerWorld worldserver1 = this.server.getWorld(playerIn.getSpawnPointDimension());
            if (worldserver1 != null) {
                if (optional_vanilla.isPresent()) {
                    BlockState iblockdata = worldserver1.getBlockState(blockposition);
                    boolean flag3 = iblockdata.isOf(Blocks.RESPAWN_ANCHOR);
                    Vec3d vec3d = (Vec3d) optional_vanilla.get();
                    float f1;

                    if (!iblockdata.isIn(BlockTags.BEDS) && !flag3) {
                        f1 = f;
                    } else {
                        Vec3d vec3d1 = Vec3d.ofBottomCenter(blockposition).subtract(vec3d).normalize();

                        f1 = (float) MathHelper.wrapDegrees(MathHelper.atan2(vec3d1.z, vec3d1.x) * 57.2957763671875D - 90.0D);
                    }
                    // Banner end
                    flag2 = !conqueredEnd && flag3;
                    isBedSpawn = true;
                    banner$loc = CraftLocation.toBukkit(vec3d, ((IMixinWorld)worldserver1).getCraftWorld(), f1, 0.0F);
                } else if (blockposition != null) {
                    entityplayer1.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.NO_RESPAWN_BLOCK, 0.0F));
                    // entityplayer1.pushChangeSpawnCause(PlayerSpawnChangeEvent.Cause.RESET);
                    entityplayer1.setSpawnPoint(null, null, 0f, false, false);
                }
            }

            if (banner$loc == null) {
                worldserver1 = this.server.getWorld(World.OVERWORLD);
                // blockposition = entityplayer1.getSpawnPoint(worldserver1);
                blockposition = entityplayer1.getSpawnPointPosition();
                if (null == blockposition) {
                	blockposition = worldserver1.getSpawnPos();
                }
                
                banner$loc = CraftLocation.toBukkit(blockposition, ((IMixinWorld)worldserver1).getCraftWorld()).add(0.5F, 0.1F, 0.5F);
            }

            Player respawnPlayer = (Player) ((IMixinServerEntityPlayer)entityplayer1).getBukkitEntity();
            respawnEvent = new PlayerRespawnEvent(respawnPlayer, banner$loc, isBedSpawn && !flag2, flag2);
            CraftServer.INSTANCE.getPluginManager().callEvent(respawnEvent);
            // Spigot Start
            // if (playerIn.networkHandler.isDisconnected()) {
            if (playerIn.isDisconnected()) {
            	System.out.println("PLAYER DISCONNECT");
                return playerIn;
            }
            // Spigot End

            banner$loc = respawnEvent.getRespawnLocation();
            if (!conqueredEnd) { // keep inventory here since inventory dropped at ServerPlayerEntity#onDeath
                ((IMixinServerEntityPlayer)playerIn).reset(); // SPIGOT-4785
            }
        } else {
            if (banner$worldserver == null) banner$worldserver = this.server.getWorld(playerIn.getSpawnPointDimension());
            banner$loc.setWorld(((IMixinWorld)banner$worldserver).getCraftWorld());
        }
        worldserver1 = ((CraftWorld) banner$loc.getWorld()).getHandle();
        
        entityplayer1.setPos(banner$loc.getX(), banner$loc.getY(), banner$loc.getZ());
        entityplayer1.setRotation(banner$loc.getYaw(), banner$loc.getPitch());
        
        //entityplayer1.forceSetPositionRotation(banner$loc.getX(), banner$loc.getY(), banner$loc.getZ(), banner$loc.getYaw(), banner$loc.getPitch());
        // CraftBukkit end

        while (avoidSuffocation.getAndSet(true) && !worldserver1.isSpaceEmpty(entityplayer1) && entityplayer1.getY() < (double) worldserver1.getTopYInclusive()) {
            entityplayer1.setPosition(entityplayer1.getX(), entityplayer1.getY() + 1.0D, entityplayer1.getZ());
        }

        // CraftBukkit start
        worlddata = worldserver1.getLevelProperties();
        
        int sim = CraftServer.INSTANCE.getSimulationDistance();
        int vd = CraftServer.INSTANCE.getViewDistance();
        
        entityplayer1.networkHandler.sendPacket(new PlayerRespawnS2CPacket(entityplayer1.createCommonPlayerSpawnInfo(entityplayer1.getWorld()), (byte) (conqueredEnd ? 1 : 0)));

        
        //entityplayer1.networkHandler.sendPacket(new PlayerRespawnS2CPacket(worldserver1.getDimensionKey(), worldserver1.getRegistryKey(),
        //				BiomeAccess.hashSeed(worldserver1.getSeed()), entityplayer1.interactionManager.getGameMode(), entityplayer1.interactionManager.getPreviousGameMode(),
        //				worldserver1.isDebugWorld(), worldserver1.isFlat(), (byte) (conqueredEnd ? 1 : 0), entityplayer1.getLastDeathPos(), entityplayer1.getPortalCooldown()));
        entityplayer1.networkHandler.sendPacket(new ChunkLoadDistanceS2CPacket((vd)));
        entityplayer1.networkHandler.sendPacket(new SimulationDistanceS2CPacket(sim));
        ((IMixinServerEntityPlayer)entityplayer1).spawnIn(worldserver1);
        entityplayer1.unsetRemoved();
        ((IMixinPlayNetworkHandler)entityplayer1.networkHandler).teleport(CraftLocation.toBukkit(entityplayer1.getPos(), ((IMixinWorld)worldserver1).getCraftWorld(), entityplayer1.getYaw(), entityplayer1.getPitch()));
        entityplayer1.setSneaking(false);
        entityplayer1.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(worldserver1.getSpawnPos(), worldserver1.getSpawnAngle()));
        entityplayer1.networkHandler.sendPacket(new DifficultyS2CPacket(worlddata.getDifficulty(), worlddata.isDifficultyLocked()));
        entityplayer1.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(entityplayer1.experienceProgress, entityplayer1.totalExperience, entityplayer1.experienceLevel));
        this.sendWorldInfo(entityplayer1, worldserver1);
        this.sendCommandTree(entityplayer1);
        if (!playerIn.isDisconnected()) {
            worldserver1.onPlayerRespawned(entityplayer1);
            this.players.add(entityplayer1);
            this.playerMap.put(entityplayer1.getUuid(), entityplayer1);
        }
        // Banner start - add for carpet compat
        if (entityplayer_vanilla == null) {
            entityplayer1.onSpawn();
        }
        // Banner end
        entityplayer1.setHealth(entityplayer1.getHealth());
        if (flag2) {
            entityplayer1.networkHandler.sendPacket(new PlaySoundS2CPacket(SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE, SoundCategory.BLOCKS, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), 1.0F, 1.0F, worldserver1.getRandom().nextLong()));
        }
        // Added from changeDimension
        this.sendPlayerStatus(playerIn); // Update health, etc...
        playerIn.sendAbilitiesUpdate();
        for (StatusEffectInstance mobEffect : playerIn.getStatusEffects()) {
        	EntityStatusEffectS2CPacket pk = ((IMixinMinecraftServer)ICommonMod.getIServer().getMinecraft()).new_status_effect_packet(playerIn.getId(), mobEffect, false);
            playerIn.networkHandler.sendPacket(pk);
        }

        // Fire advancement trigger
        playerIn.worldChanged(((CraftWorld) fromWorld).getHandle());

        // Don't fire on respawn
        if (fromWorld != banner$loc.getWorld()) {
            PlayerChangedWorldEvent event = new PlayerChangedWorldEvent( (@NotNull Player) ((IMixinServerEntityPlayer)playerIn).getBukkitEntity(), fromWorld);
            Bukkit.getPluginManager().callEvent(event);
        }

        // Save player file again if they were disconnected
        // if (playerIn.networkHandler.isDisconnected()) {
        if (playerIn.isDisconnected()) {
            this.savePlayerData(playerIn);
        }
        // CraftBukkit end
        banner$loc = null;
        banner$respawnReason = null;
        banner$worldserver = null;
        
        entityplayer1.setHealth(entityplayer1.getMaxHealth());
        
        return entityplayer1;
    }
    */
    
    /**
     * @author Cardboard Mod
     * @reason Bukkitize respawn
     * @since 1.21.8
     */
    @Overwrite
    public ServerPlayerEntity respawnPlayer(ServerPlayerEntity player, boolean keepInventory, RemovalReason reason) {
    	//, RespawnReason eventReason, Location location) {
    
    	RespawnReason eventReason = null;
    	Location location = null;
    	
        player.stopRiding();
        this.players.remove(player);
        // this.playersByName.remove(player.getNameForScoreboard().toLowerCase(Locale.ROOT));
        player.getWorld().removePlayer(player, reason);
        ServerPlayerEntity serverPlayer = player;
        World fromWorld = player.getWorld();
        player.notInAnyWorld = false;
        player.networkHandler = player.networkHandler;
        player.copyFrom(player, keepInventory);
        player.setId(player.getId());
        player.setMainArm(player.getMainArm());

        for (String string: player.getCommandTags()) {
            serverPlayer.addCommandTag(string);
        }

        boolean isBedSpawn = false;
        boolean isRespawn = false;
        boolean isAnchorSpawn = false;
        TeleportTarget teleportTransition;
        if (location == null) {
            teleportTransition = ((IMixinServerEntityPlayer) player).findRespawnPositionAndUseSpawnBlock(!keepInventory, TeleportTarget.NO_OP, eventReason);
            if (!keepInventory) {
            	((IMixinServerEntityPlayer) player).reset();
            }

            if (teleportTransition == null) {
            	if (CardboardConfig.DEBUG_PLAYER) {
            		CardboardMod.LOGGER.info("teleportTransition is null");
            	}
                return player;
            }

            isRespawn = true;
            location = CraftLocation.toBukkit(teleportTransition.position(), teleportTransition.world().getWorld(), teleportTransition.yaw(), teleportTransition.pitch());
        } else {
            teleportTransition = new TeleportTarget(
                ((CraftWorld) location.getWorld()).getHandle(),
                CraftLocation.toVec3(location),
                Vec3d.ZERO,
                location.getYaw(),
                location.getPitch(),
                TeleportTarget.NO_OP
            );
        }

        if (teleportTransition == null) {
        	if (CardboardConfig.DEBUG_PLAYER) {
        		CardboardMod.LOGGER.info("teleTrans is null");
        	}
            return player;
        } else {
            ServerWorld level = teleportTransition.world();
            ((IMixinServerEntityPlayer) player).spawnIn(level);
            serverPlayer.unsetRemoved();
            serverPlayer.setSneaking(false);
            Vec3d vec3 = teleportTransition.position();
            ((IMixinServerEntityPlayer) serverPlayer).spigot$forceSetPositionRotation(vec3.x, vec3.y, vec3.z, teleportTransition.yaw(), teleportTransition.pitch());
            level.getChunkManager().addTicket(ChunkTicketBridge.POST_TELEPORT, new ChunkPos(MathHelper.floor(vec3.getX()) >> 4, MathHelper.floor(vec3.getZ()) >> 4), 1);
            if (teleportTransition.missingRespawnBlock()) {
            	if (CardboardConfig.DEBUG_PLAYER) {
            		CardboardMod.LOGGER.info("teleTrans missing respawn block");
            	}
            	
                serverPlayer.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.NO_RESPAWN_BLOCK, 0.0F));
                // serverPlayer.r.setRespawnPosition(null, false, com.destroystokyo.paper.event.player.PlayerSetSpawnEvent.Cause.PLAYER_RESPAWN);
            }

            byte b = (byte)(keepInventory ? 1 : 0);
            ServerWorld serverLevel = serverPlayer.getWorld();
            WorldProperties levelData = serverLevel.getLevelProperties();
            serverPlayer.networkHandler.sendPacket(new PlayerRespawnS2CPacket(serverPlayer.createCommonPlayerSpawnInfo(serverLevel), b));
            // serverPlayer.networkHandler.sendPacket(new ChunkLoadDistanceS2CPacket(serverLevel.spigotConfig.viewDistance));
            // serverPlayer.networkHandler.sendPacket(new SimulationDistanceS2CPacket(serverLevel.spigotConfig.simulationDistance));
            
            serverPlayer.networkHandler.sendPacket(new ChunkLoadDistanceS2CPacket(8)); // TODO
            serverPlayer.networkHandler.sendPacket(new SimulationDistanceS2CPacket(8));
            
            IMixinPlayNetworkHandler iNetworkHandler = (IMixinPlayNetworkHandler) serverPlayer.networkHandler;

            iNetworkHandler.teleport(CraftLocation.toBukkit(serverPlayer.getPos(), serverLevel.getWorld(), serverPlayer.getYaw(), serverPlayer.getPitch()));
            serverPlayer.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(level.getSpawnPos(), level.getSpawnAngle()));

            // 1.21.10: iNetworkHandler.teleport(CraftLocation.toBukkit(serverPlayer.getEntityPos(), serverLevel.getWorld(), serverPlayer.getYaw(), serverPlayer.getPitch()));
            // serverPlayer.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(level.getSpawnPoint()));

            serverPlayer.networkHandler.sendPacket(new DifficultyS2CPacket(levelData.getDifficulty(), levelData.isDifficultyLocked()));
            serverPlayer.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(serverPlayer.experienceProgress, serverPlayer.totalExperience, serverPlayer.experienceLevel));
            this.sendStatusEffects(serverPlayer);
            this.sendWorldInfo(serverPlayer, level);
            this.sendCommandTree(serverPlayer);

            if (!iNetworkHandler.isDisconnected()) {
            	if (CardboardConfig.DEBUG_PLAYER) {
            		CardboardMod.LOGGER.info("onplrrespawned");
            	}
            	
                level.onPlayerRespawned(serverPlayer);
                this.players.add(serverPlayer);
                // this.playersByName.put(serverPlayer.getNameForScoreboard().toLowerCase(Locale.ROOT), serverPlayer);
                this.playerMap.put(serverPlayer.getUuid(), serverPlayer);
            }

            serverPlayer.setHealth(serverPlayer.getHealth());
            ServerPlayerEntity.Respawn respawnConfig = serverPlayer.getRespawn();
            if (!keepInventory && respawnConfig != null) {
                ServerWorld level1 = this.server.getWorld(respawnConfig.dimension());
                if (level1 != null) {
                    BlockPos blockPos = respawnConfig.pos();
                    BlockState blockState = level1.getBlockState(blockPos);
                    if (blockState.isOf(Blocks.RESPAWN_ANCHOR)) {
                        serverPlayer.networkHandler.sendPacket(new PlaySoundS2CPacket(SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE, SoundCategory.BLOCKS, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0F, 1.0F, level.getRandom().nextLong()));
                    }

                    if (!teleportTransition.missingRespawnBlock()) {
                        if (blockState.isIn(BlockTags.BEDS)) {
                            isBedSpawn = true;
                        } else if (blockState.isOf(Blocks.RESPAWN_ANCHOR)) {
                            isAnchorSpawn = true;
                        }
                    }
                }
            }

            this.sendPlayerStatus(player);
            player.sendAbilitiesUpdate();

            for (StatusEffectInstance mobEffect: player.getStatusEffects()) {
                player.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(player.getId(), mobEffect, false));
            }

            player.worldChanged(level);
            if (fromWorld != level) {
                PlayerChangedWorldEvent event = new PlayerChangedWorldEvent((Player) (Player)((IMixinServerEntityPlayer)player).getBukkitEntity(), ((IMixinWorld) fromWorld).getCraftWorld());
                CraftServer.INSTANCE.getPluginManager().callEvent(event);
            }

            if (iNetworkHandler.isDisconnected()) {
                this.savePlayerData(player);
            }

            if (isRespawn) {
                new PlayerPostRespawnEvent((Player) (Player)((IMixinServerEntityPlayer)player).getBukkitEntity(), location, isBedSpawn, isAnchorSpawn, teleportTransition.missingRespawnBlock(), eventReason).callEvent();
            }

            return serverPlayer;
        }
    }
    
    /**
     * @author Cardboard Mod
     * @reason Bukkitize respawn
     * @since 1.21.8
     */
    /*
    @Overwrite
    public ServerPlayerEntity respawnPlayer(ServerPlayerEntity player, boolean alive, Entity.RemovalReason removalReason) {
        this.players.remove(player);
        player.getWorld().removePlayer(player, removalReason);
        TeleportTarget lv = player.getRespawnTarget(!alive, TeleportTarget.NO_OP);
        ServerWorld lv2 = lv.world();
        ServerPlayerEntity lv3 = new ServerPlayerEntity(this.server, lv2, player.getGameProfile(), player.getClientOptions());
        lv3.networkHandler = player.networkHandler;
        lv3.copyFrom(player, alive);
        lv3.setId(player.getId());
        lv3.setMainArm(player.getMainArm());
        if (!lv.missingRespawnBlock()) {
           lv3.setSpawnPointFrom(player);
        }

        for (String string : player.getCommandTags()) {
           lv3.addCommandTag(string);
        }

        Vec3d lv4 = lv.position();
        lv3.refreshPositionAndAngles(lv4.x, lv4.y, lv4.z, lv.yaw(), lv.pitch());
        if (lv.missingRespawnBlock()) {
           lv3.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.NO_RESPAWN_BLOCK, GameStateChangeS2CPacket.DEMO_OPEN_SCREEN));
        }

        byte b = alive ? PlayerRespawnS2CPacket.KEEP_ATTRIBUTES : 0;
        ServerWorld lv5 = lv3.getWorld();
        WorldProperties lv6 = lv5.getLevelProperties();
        lv3.networkHandler.sendPacket(new PlayerRespawnS2CPacket(lv3.createCommonPlayerSpawnInfo(lv5), b));
        lv3.networkHandler.requestTeleport(lv3.getX(), lv3.getY(), lv3.getZ(), lv3.getYaw(), lv3.getPitch());
        lv3.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(lv2.getSpawnPos(), lv2.getSpawnAngle()));
        lv3.networkHandler.sendPacket(new DifficultyS2CPacket(lv6.getDifficulty(), lv6.isDifficultyLocked()));
        lv3.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(lv3.experienceProgress, lv3.totalExperience, lv3.experienceLevel));
        this.sendStatusEffects(lv3);
        this.sendWorldInfo(lv3, lv2);
        this.sendCommandTree(lv3);
        lv2.onPlayerRespawned(lv3);
        this.players.add(lv3);
        this.playerMap.put(lv3.getUuid(), lv3);
        lv3.onSpawn();
        lv3.setHealth(lv3.getHealth());
        ServerPlayerEntity.Respawn lv7 = lv3.getRespawn();
        if (!alive && lv7 != null) {
           ServerWorld lv8 = this.server.getWorld(lv7.dimension());
           if (lv8 != null) {
              BlockPos lv9 = lv7.pos();
              BlockState lv10 = lv8.getBlockState(lv9);
              if (lv10.isOf(Blocks.RESPAWN_ANCHOR)) {
                 lv3.networkHandler
                    .sendPacket(
                       new PlaySoundS2CPacket(
                          SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE,
                          SoundCategory.BLOCKS,
                          lv9.getX(),
                          lv9.getY(),
                          lv9.getZ(),
                          1.0F,
                          1.0F,
                          lv2.getRandom().nextLong()
                       )
                    );
              }
           }
        }

        return lv3;
     }
     */
    
    /**
     * @author cardbaord
     * @reason bukkit
     */
    /*
    @Overwrite
    public ServerPlayerEntity respawnPlayer(ServerPlayerEntity player, boolean keepInventory, Entity.RemovalReason reason) {
    	
    	if (CardboardConfig.DEBUG_PLAYER) {
    		CardboardMod.LOGGER.info("DEBUG: PlayerManager.respawnPlayer called");
    	}
    	
        ServerWorld level1;
        TeleportTarget teleportTransition;
        player.stopRiding();
        this.players.remove(player);
        // this.playersByName.remove(player.getNameForScoreboard().toLowerCase(Locale.ROOT));
        player.getWorld().removePlayer(player, reason);
        ServerPlayerEntity serverPlayer = player;
        ServerWorld fromWorld = player.getWorld();
        player.notInAnyWorld = false;
        serverPlayer.networkHandler = player.networkHandler;
        serverPlayer.copyFrom(player, keepInventory);
        serverPlayer.setId(player.getId());
        serverPlayer.setMainArm(player.getMainArm());
        for (String string : player.getCommandTags()) {
            serverPlayer.addCommandTag(string);
        }
        boolean isBedSpawn = false;
        boolean isRespawn = false;
        boolean isAnchorSpawn = false;
        
        Location location = null;
        
        // if (location == null) {
             // teleportTransition = player.findRespawnPositionAndUseSpawnBlock(!keepInventory, TeleportTarget.NO_OP, eventReason);
            teleportTransition = player.getRespawnTarget(!keepInventory, TeleportTarget.NO_OP);
            
            if (!keepInventory) {
            	((IMixinServerEntityPlayer)player).reset();
            }
            if (teleportTransition == null) {
                return player;
            }
            isRespawn = true;
            location = CraftLocation.toBukkit(teleportTransition.position(), (org.bukkit.World)teleportTransition.world().getWorld(), teleportTransition.yaw(), teleportTransition.pitch());
        /*
        } else {
            teleportTransition = new TeleportTarget(((CraftWorld)location.getWorld()).getHandle(), CraftLocation.toVec3(location), Vec3d.ZERO, location.getYaw(), location.getPitch(), TeleportTarget.NO_OP);
        
        *
        if (teleportTransition == null) {
            return player;
        }
        ServerWorld level = teleportTransition.world();
        ((IMixinServerEntityPlayer)serverPlayer).spawnIn(level);
        serverPlayer.unsetRemoved();
        serverPlayer.setSneaking(false);
        Vec3d vec3 = teleportTransition.position();
        Player_forceSetPositionRotation(serverPlayer, vec3.x, vec3.y, vec3.z, teleportTransition.yaw(), teleportTransition.pitch());
        //serverPlayer.forceSetPositionRotation(vec3.x, vec3.y, vec3.z, teleportTransition.yaw(), teleportTransition.pitch());
        level.getChunkManager().addTicket(ChunkTicketBridge.POST_TELEPORT, new ChunkPos(MathHelper.floor(vec3.getX()) >> 4, MathHelper.floor(vec3.getZ()) >> 4), 1);
        if (teleportTransition.missingRespawnBlock()) {
            serverPlayer.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.NO_RESPAWN_BLOCK, 0.0f));
            // serverPlayer.setRespawnPosition(null, false, PlayerSetSpawnEvent.Cause.PLAYER_RESPAWN);
            serverPlayer.setSpawnPoint(null, false);
        }
        byte b2 = (byte)(keepInventory ? 1 : 0);
        ServerWorld serverLevel = serverPlayer.getWorld();
        WorldProperties levelData = serverLevel.getLevelProperties();
        serverPlayer.networkHandler.sendPacket(new PlayerRespawnS2CPacket(serverPlayer.createCommonPlayerSpawnInfo(serverLevel), b2));
        // serverPlayer.networkHandler.sendPacket(new ChunkLoadDistanceS2CPacket(serverLevel.spigotConfig.viewDistance));
        // serverPlayer.networkHandler.sendPacket(new SimulationDistanceS2CPacket(serverLevel.spigotConfig.simulationDistance));
        
        // serverPlayer.networkHandler.teleport(CraftLocation.toBukkit(serverPlayer.getPos(), (org.bukkit.World)serverLevel.getWorld(), serverPlayer.getYaw(), serverPlayer.getPitch()));
        player.teleport(worldserver1, location.getX(), location.getY(), location.getZ(), null, 0, 0, false);
        
        serverPlayer.networkHandler.sendPacket(new PlayerSpawnPositionS2CPacket(level.getSpawnPos(), level.getSpawnAngle()));
        serverPlayer.networkHandler.sendPacket(new DifficultyS2CPacket(levelData.getDifficulty(), levelData.isDifficultyLocked()));
        serverPlayer.networkHandler.sendPacket(new ExperienceBarUpdateS2CPacket(serverPlayer.experienceProgress, serverPlayer.totalExperience, serverPlayer.experienceLevel));
        this.sendStatusEffects(serverPlayer);
        this.sendWorldInfo(serverPlayer, level);
        this.sendCommandTree(serverPlayer);
        if (!serverPlayer.isDisconnected()) {
            level.onPlayerRespawned(serverPlayer);
            this.players.add(serverPlayer);
            // this.playersByName.put(serverPlayer.getNameForScoreboard().toLowerCase(Locale.ROOT), serverPlayer);
            this.playerMap.put(serverPlayer.getUuid(), serverPlayer);
        }
        serverPlayer.setHealth(serverPlayer.getHealth());
        ServerPlayerEntity.Respawn respawnConfig = serverPlayer.getRespawn();
        if (!keepInventory && respawnConfig != null && (level1 = this.server.getWorld(respawnConfig.dimension())) != null) {
            BlockPos blockPos = respawnConfig.pos();
            BlockState blockState = ((World)level1).getBlockState(blockPos);
            if (blockState.isOf(Blocks.RESPAWN_ANCHOR)) {
                serverPlayer.networkHandler.sendPacket(new PlaySoundS2CPacket(SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE, SoundCategory.BLOCKS, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0f, 1.0f, level.getRandom().nextLong()));
            }
            if (!teleportTransition.missingRespawnBlock()) {
                if (blockState.isIn(BlockTags.BEDS)) {
                    isBedSpawn = true;
                } else if (blockState.isOf(Blocks.RESPAWN_ANCHOR)) {
                    isAnchorSpawn = true;
                }
            }
        }
        this.sendPlayerStatus(player);
        player.sendAbilitiesUpdate();
        for (StatusEffectInstance mobEffect : player.getStatusEffects()) {
            player.networkHandler.sendPacket(new EntityStatusEffectS2CPacket(player.getId(), mobEffect, false));
        }
        player.worldChanged(level);
        if (fromWorld != level) {
            PlayerChangedWorldEvent event = new PlayerChangedWorldEvent((Player)((IMixinServerEntityPlayer)player).getBukkitEntity(), fromWorld.getWorld());
            CraftServer.INSTANCE.getPluginManager().callEvent(event);
        }
        if (player.isDisconnected()) {
            this.savePlayerData(player);
        }
        if (isRespawn) {
        	// TODO
            new PlayerPostRespawnEvent((Player)((IMixinServerEntityPlayer)player).getBukkitEntity(), location, isBedSpawn, isAnchorSpawn, teleportTransition.missingRespawnBlock(), PlayerRespawnEvent.RespawnReason.DEATH).callEvent();
            // new PlayerPostRespawnEvent((Player)((IMixinServerEntityPlayer)player).getBukkitEntity(), location, isBedSpawn).callEvent();
        }
        return serverPlayer;
    }
    */
    
    private Location banner$loc = null;
    private transient RespawnFlag banner$respawnReason;
    public ServerWorld banner$worldserver = null;
    public AtomicBoolean avoidSuffocation = new AtomicBoolean(true);
    
    // Banner start - Fix mixin by apoli
    public org.bukkit.World fromWorld;
    public PlayerRespawnEvent respawnEvent;
    public ServerWorld worldserver1;
    public WorldProperties worlddata;
    public ServerPlayerEntity entityplayer_vanilla;
    // Banner end

}
