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
import org.cardboardpowered.bridge.server.level.ServerPlayerBridge;
import org.cardboardpowered.interfaces.IMixinServerLoginNetworkHandler;
import org.cardboardpowered.interfaces.IMixinWorld;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;

import me.isaiah.common.ICommonMod;
//=======
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.List;
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
import org.cardboardpowered.extras.PlayerManager_LoginResult;
import org.cardboardpowered.impl.entity.CraftPlayer;
import org.cardboardpowered.impl.world.CraftWorld;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.PlayerSpawnFinder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.server.players.IpBanListEntry;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
//>>>>>>> upstream/ver/1.20
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetExperiencePacket;
import net.minecraft.network.protocol.game.ClientboundSetSimulationDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.resources.ResourceKey;

// TODO: This breaks many thing like experience drop.
@Mixin(PlayerList.class)
public abstract class MixinPlayerManager implements IMixinPlayerManager {

    @Shadow
    public List<ServerPlayer> players;
    
    @Shadow private MinecraftServer server;

    @Shadow
    public void sendPlayerPermissionLevel(ServerPlayer player) {}

    @Shadow
    public void sendLevelInfo(ServerPlayer player, ServerLevel world) {}

    @Shadow
    public void save(ServerPlayer player) {}

    @Shadow
    public void sendAllPlayerInfo(ServerPlayer player) {}

    @Shadow
    public Map<UUID, ServerPlayer> playersByUUID;
    
    @Shadow
    private UserBanList bans;

    // ServerPlayerEntity.forceSetPositionRotation
    public void Player_forceSetPositionRotation(ServerPlayer p, double x, double y, double z, float yaw, float pitch) {
        p.snapTo(x, y, z, yaw, pitch);
        p.connection.resetPosition();
    }
    
    @Override
    public ServerPlayer moveToWorld(ServerPlayer player, ServerLevel worldserver, boolean flag, Location to, boolean avoidSuffocation) {
    	return respawn(player, true, Entity.RemovalReason.CHANGED_DIMENSION, null, to);
    }
    
    @Override
    public ServerPlayer respawn(ServerPlayer player, boolean keepInventory, Entity.RemovalReason reason, PlayerRespawnEvent.RespawnReason eventReason, Location location) {
       
    	// DEBUG
    	if (CardboardConfig.DEBUG_PLAYER) {
    		CardboardMod.LOGGER.info("DEBUG: PlayerManager.respawn called");
    	}
    	
    	// ServerWorld level1;
        TeleportTransition teleportTransition;
        player.stopRiding();
        this.players.remove(player);
        // this.playersByName.remove(player.getNameForScoreboard().toLowerCase(Locale.ROOT));
        player.level().removePlayerImmediately(player, reason);
        ServerPlayer serverPlayer = player;
        ServerLevel fromWorld = player.level();
        player.wonGame = false;
        serverPlayer.connection = player.connection;
        serverPlayer.restoreFrom(player, keepInventory);
        serverPlayer.setId(player.getId());
        serverPlayer.setMainArm(player.getMainArm());
        for (String string : player.getTags()) {
            serverPlayer.addTag(string);
        }
        boolean isBedSpawn = false;
        boolean isRespawn = false;
        boolean isAnchorSpawn = false;
        if (location == null) {
            teleportTransition = ((ServerPlayerBridge) player).findRespawnPositionAndUseSpawnBlock(!keepInventory, TeleportTransition.DO_NOTHING, eventReason);
            // teleportTransition = player.getRespawnTarget(!keepInventory, TeleportTarget.NO_OP);
            
            if (!keepInventory) {
                ((ServerPlayerBridge)player).reset();
            }
            if (teleportTransition == null) {
                return player;
            }
            isRespawn = true;
            location = CraftLocation.toBukkit(teleportTransition.position(), (org.bukkit.World)teleportTransition.newLevel().getWorld(), teleportTransition.yRot(), teleportTransition.xRot());
        } else {
            teleportTransition = new TeleportTransition(((CraftWorld)location.getWorld()).getHandle(), CraftLocation.toVec3(location), Vec3.ZERO, location.getYaw(), location.getPitch(), TeleportTransition.DO_NOTHING);
        }
        if (teleportTransition == null) {
            return player;
        }
        ServerLevel level = teleportTransition.newLevel();
        ((ServerPlayerBridge)serverPlayer).spawnIn(level);
        serverPlayer.unsetRemoved();
        serverPlayer.setShiftKeyDown(false);
        Vec3 vec3 = teleportTransition.position();
        Player_forceSetPositionRotation(serverPlayer, vec3.x, vec3.y, vec3.z, teleportTransition.yRot(), teleportTransition.xRot());
        //serverPlayer.forceSetPositionRotation(vec3.x, vec3.y, vec3.z, teleportTransition.yaw(), teleportTransition.pitch());
        level.getChunkSource().addTicketWithRadius(ChunkTicketBridge.POST_TELEPORT, new ChunkPos(Mth.floor(vec3.x()) >> 4, Mth.floor(vec3.z()) >> 4), 1);
        if (teleportTransition.missingRespawnBlock()) {
        	
        	if (CardboardConfig.DEBUG_PLAYER) {
        		CardboardMod.LOGGER.info("teleportTransition#missingRespawnBlock!");
        	}
        	
            serverPlayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE, 0.0f));
            // serverPlayer.setRespawnPosition(null, false, PlayerSetSpawnEvent.Cause.PLAYER_RESPAWN);
            serverPlayer.setRespawnPosition(null, false);
        }
        byte b2 = (byte)(keepInventory ? 1 : 0);
        ServerLevel serverLevel = serverPlayer.level();
        LevelData levelData = serverLevel.getLevelData();
        serverPlayer.connection.send(new ClientboundRespawnPacket(serverPlayer.createCommonSpawnInfo(serverLevel), b2));
        // serverPlayer.networkHandler.sendPacket(new ChunkLoadDistanceS2CPacket(serverLevel.spigotConfig.viewDistance));
        // serverPlayer.networkHandler.sendPacket(new SimulationDistanceS2CPacket(serverLevel.spigotConfig.simulationDistance));
        
        // serverPlayer.networkHandler.teleport(CraftLocation.toBukkit(serverPlayer.getPos(), (org.bukkit.World)serverLevel.getWorld(), serverPlayer.getYaw(), serverPlayer.getPitch()));
        player.teleportTo(worldserver1, location.getX(), location.getY(), location.getZ(), null, 0, 0, false);
        
        serverPlayer.connection.send(new ClientboundSetDefaultSpawnPositionPacket(level.getRespawnData()));
        serverPlayer.connection.send(new ClientboundChangeDifficultyPacket(levelData.getDifficulty(), levelData.isDifficultyLocked()));
        serverPlayer.connection.send(new ClientboundSetExperiencePacket(serverPlayer.experienceProgress, serverPlayer.totalExperience, serverPlayer.experienceLevel));
        this.sendActivePlayerEffects(serverPlayer);
        this.sendLevelInfo(serverPlayer, level);
        this.sendPlayerPermissionLevel(serverPlayer);
        if (!serverPlayer.hasDisconnected()) {
        	if (CardboardConfig.DEBUG_PLAYER) {
        		CardboardMod.LOGGER.info("calling onPlayerRespawned");
        	}
            level.addRespawnedPlayer(serverPlayer);
            this.players.add(serverPlayer);
            // this.playersByName.put(serverPlayer.getNameForScoreboard().toLowerCase(Locale.ROOT), serverPlayer);
            this.playersByUUID.put(serverPlayer.getUUID(), serverPlayer);
        }
        serverPlayer.setHealth(serverPlayer.getHealth());
        ServerPlayer.RespawnConfig respawnConfig = serverPlayer.getRespawnConfig();
        if (!keepInventory && respawnConfig != null) {
        	LevelData.RespawnData respawnData = respawnConfig.respawnData();
            ServerLevel level1 = this.server.getLevel(respawnData.dimension());
        	
            if (null != level1) {
	        	BlockPos blockPos = respawnData.pos();
	            BlockState blockState = ((Level)level1).getBlockState(blockPos);
	            if (blockState.is(Blocks.RESPAWN_ANCHOR)) {
	                serverPlayer.connection.send(new ClientboundSoundPacket(SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundSource.BLOCKS, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0f, 1.0f, level.getRandom().nextLong()));
	            }
	            if (!teleportTransition.missingRespawnBlock()) {
	                if (blockState.is(BlockTags.BEDS)) {
	                    isBedSpawn = true;
	                } else if (blockState.is(Blocks.RESPAWN_ANCHOR)) {
	                    isAnchorSpawn = true;
	                }
	            }
            }
        }
        this.sendAllPlayerInfo(player);
        player.onUpdateAbilities();
        for (MobEffectInstance mobEffect : player.getActiveEffects()) {
            player.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), mobEffect, false));
        }
        player.triggerDimensionChangeTriggers(level);
        if (fromWorld != level) {
            PlayerChangedWorldEvent event = new PlayerChangedWorldEvent((Player)((ServerPlayerBridge)player).getBukkitEntity(), fromWorld.getWorld());
            CraftServer.INSTANCE.getPluginManager().callEvent(event);
        }
        if (player.hasDisconnected()) {
            this.save(player);
        }
        if (isRespawn) {
        	// TODO
            new PlayerPostRespawnEvent((Player)((ServerPlayerBridge)player).getBukkitEntity(), location, isBedSpawn, isAnchorSpawn, teleportTransition.missingRespawnBlock(), eventReason).callEvent();
        }
        return serverPlayer;
    }
    
    @Shadow void sendActivePlayerEffects( ServerPlayer player) {}
    
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

    @Inject(method = "placeNewPlayer", at = @At("HEAD"))
    public void onConnect(Connection connection, ServerPlayer player, CommonListenerCookie clientData, CallbackInfo ci) {
        this.plr = (CraftPlayer) CraftServer.INSTANCE.getPlayer(player);
    }

    @Redirect(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    public void firePlayerJoinEvent(PlayerList instance, Component message, boolean overlay) {
        CraftPlayer plr;

        if(this.plr == null) {
            instance.broadcastSystemMessage(message, overlay);
            return;
        } else {
            plr = this.plr;
            this.plr = null;
        }

        String key = "multiplayer.player.joined";
        Component name = plr.nms.getDisplayName();

        String joinMessage = ChatFormatting.YELLOW + Component.translatable(key, name).getString();

        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(plr, joinMessage);
        CraftEventFactory.callEvent(playerJoinEvent);
        IMixinPlayNetworkHandler ims = (IMixinPlayNetworkHandler)plr.nms.connection;

        if (!ims.cb_get_connection().isConnected()) {
            return;
        }

        joinMessage = playerJoinEvent.getJoinMessage();

        if (joinMessage != null && !joinMessage.isEmpty()) {
            for (Component line : CraftChatMessage.fromString(joinMessage)) {
                broadcastSystemMessage(line, entityplayer -> line, false);
            }
        }

    }

    @Inject(at = @At("HEAD"), method = "remove")
    public void firePlayerQuitEvent(ServerPlayer player, CallbackInfo ci) {
        player.closeContainer();

        PlayerQuitEvent playerQuitEvent = new PlayerQuitEvent(CraftServer.INSTANCE.getPlayer(player), "\u00A7e" + player.getDisplayName().getString() + " left the game");
        CraftServer.INSTANCE.getPluginManager().callEvent(playerQuitEvent);
        player.doTick();
    }
    
    private static final Logger cb$LOGGER = LogUtils.getLogger();
    
    /**
     * todo: update our login code to use SpawnPrepareTask instead of our attemptLogin
     */
    private Location cardboard$getPlayerSpawn(NameAndId player) {
    	Optional<ValueInput> optional;
    	ResourceKey<Level> resourceKey = null;
    	boolean[] invalidPlayerWorld = new boolean[]{false};

    	try (ProblemReporter.ScopedCollector scopedCollector = new ProblemReporter.ScopedCollector(cb$LOGGER)) {
    		optional = this.server
    				.getPlayerList()
    				.loadPlayerData(player)
    				.map(nbt -> TagValueInput.create(scopedCollector, this.server.registryAccess(), nbt));

    		bukkitData:
    			if (optional.isPresent()) {
    				ValueInput playerData = optional.get();
    				Optional<Long> worldUUIDMost = playerData.getLong("WorldUUIDMost");
    				Optional<Long> worldUUIDLeast = playerData.getLong("WorldUUIDLeast");
    				Optional<String> worldName = playerData.getString("world");
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
    					resourceKey = ((CraftWorld)bWorld).getHandle().dimension();
    				} else {
    					resourceKey = Level.OVERWORLD;
    					invalidPlayerWorld[0] = true;
    				}
    			}

    		ServerPlayer.SavedPosition savedPosition = optional.<ServerPlayer.SavedPosition>flatMap(view -> view.read(ServerPlayer.SavedPosition.MAP_CODEC))
    				.orElse(ServerPlayer.SavedPosition.EMPTY);
    		LevelData.RespawnData respawnData = this.server.getWorldData().overworldData().getRespawnData();

    		if (resourceKey == null) {
    			resourceKey = savedPosition.dimension().orElse(null);
    		}

    		ServerLevel vanillaDefaultLevel = this.server.getLevel(respawnData.dimension());
    		if (vanillaDefaultLevel == null) {
    			vanillaDefaultLevel = this.server.overworld();
    		}

    		ServerLevel serverLevel1;
    		if (resourceKey == null) {
    			serverLevel1 = vanillaDefaultLevel;
    		} else {
    			serverLevel1 = this.server.getLevel(resourceKey);
    			if (serverLevel1 == null) {
    				cb$LOGGER.warn("Unknown respawn dimension {}, defaulting to overworld", resourceKey);
    				serverLevel1 = vanillaDefaultLevel;
    			}
    		}

    		ServerLevel serverLevel = serverLevel1;
    		CompletableFuture<Vec3> completableFuture = savedPosition.position()
    				.map(CompletableFuture::completedFuture)
    				.orElseGet(() -> PlayerSpawnFinder.findSpawn(serverLevel, respawnData.pos()));
    		Vec2 vec2 = savedPosition.rotation().orElse(new Vec2(respawnData.yaw(), respawnData.pitch()));
    		// this.stage = new PrepareSpawnTask.LoadPlayerChunks(serverLevel, completableFuture, vec2);
    		
    		// CraftLocation.toBukkit(null, serverLevel, 0, 0)
    		
    		try {
				Vec3 d3 = completableFuture.get();
				Location loc = CraftLocation.toBukkit(d3, serverLevel, vec2.x, vec2.y);
				return loc;
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
				return null;
			}
    		
    	}
    }

    @Override
    public ServerPlayer attemptLogin(ServerLoginPacketListenerImpl nethand, GameProfile profile, ProfilePublicKey key, String hostname) {
    	MutableComponent chatmessage;
    	
    	NameAndId pce = new NameAndId(profile);

        // Moved from processLogin
        // 1.18: UUID uuid = PlayerEntity.getUuidFromProfile(profile);
    	UUID uuid = ICommonMod.getIServer().get_uuid_from_profile(profile);
    	// UUID uuid = DynamicSerializableUuid.getUuidFromProfile(profile);
    	List<ServerPlayer> list = Lists.newArrayList();

        ServerPlayer entityplayer;

        for (int i = 0; i < this.players.size(); ++i) {
            entityplayer = (ServerPlayer) this.players.get(i);
            if (entityplayer.getUUID().equals(uuid))
                list.add(entityplayer);
        }

        Iterator<ServerPlayer> iterator = list.iterator();

        while (iterator.hasNext()) {
            entityplayer = (ServerPlayer) iterator.next();
            save(entityplayer); // Force the player's inventory to be saved
            entityplayer.connection.disconnect(Component.nullToEmpty("multiplayer.disconnect.duplicate_login"));
        }

        IMixinServerLoginNetworkHandler ims = (IMixinServerLoginNetworkHandler)nethand;
        SocketAddress address = ims.cb_get_connection().getRemoteAddress();

        // me.isaiah.common.cmixin.IMixinPlayerManager imixin = (me.isaiah.common.cmixin.IMixinPlayerManager) (Object)this;
       // ServerPlayerEntity entity = imixin.InewPlayer(CraftServer.server, CraftServer.server.getWorld(World.OVERWORLD), profile);
        
        // TODO Test
        Location spawnPos = cardboard$getPlayerSpawn(pce);
        ServerLevel spawnWorld = null != spawnPos ? ((CraftWorld) spawnPos.getWorld()).getHandle() : CraftServer.server.getLevel(Level.OVERWORLD);
        
        ServerPlayer entity = new ServerPlayer(CraftServer.server, spawnWorld, profile, ClientInformation.createDefault());
        Player player = (Player) ((ServerPlayerBridge)entity).getBukkitEntity();
        
        if (null != spawnPos) {
        	entity.snapTo(BlockPos.containing(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ()), spawnPos.getYaw(), spawnPos.getPitch());
        }

        PlayerLoginEvent event = new PlayerLoginEvent(player, hostname, ((java.net.InetSocketAddress) address).getAddress(), ((java.net.InetSocketAddress) ims.cb_get_connection().channel.remoteAddress()).getAddress());
        
        if (((PlayerList)(Object)this).getBans().isBanned(pce) /*&& !((PlayerManager)(Object)this).getUserBanList().get(gameprofile).isInvalid()*/) {
            chatmessage = Component.translatable("multiplayer.disconnect.banned.reason", new Object[]{"TODO REASON!"});
            //chatmessage.append(new TranslatableTextContent("multiplayer.disconnect.banned.expiration", new Object[] {"TODO EXPIRE!"}));

            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, CraftChatMessage.fromComponent(chatmessage));
        } else if (!((PlayerList)(Object)this).isWhiteListed(pce)) {
            chatmessage = Component.translatable("multiplayer.disconnect.not_whitelisted");
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "Server whitelisted!");
        } else if (((PlayerList)(Object)this).getIpBans().isBanned(address) /*&& !((PlayerManager)(Object)this).getIpBanList().get(socketaddress).isInvalid()*/) {
            IpBanListEntry ipbanentry = ((PlayerList)(Object)this).getIpBans().get(address);

            chatmessage = Component.translatable("multiplayer.disconnect.banned_ip.reason", new Object[]{ipbanentry.getReason()});
            //if (ipbanentry.getExpiryDate() != null)
            //    chatmessage.append(new TranslatableTextContent("multiplayer.disconnect.banned_ip.expiration", new Object[]{ipbanentry.getExpiryDate()}));

            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, CraftChatMessage.fromComponent(chatmessage));
        } else {
            if (this.players.size() >= ((PlayerList)(Object)this).getMaxPlayers() && !((PlayerList)(Object)this).canBypassPlayerLimit(pce))
                event.disallow(PlayerLoginEvent.Result.KICK_FULL, "Server is full");
        }

        CraftEventFactory.callEvent(event);
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            nethand.disconnect(Component.nullToEmpty(event.getKickMessage()));
            return null;
        }
        return entity;
    }

    @Shadow
    public void updateEntireScoreboard(ServerScoreboard scoreboardserver, ServerPlayer entityplayer) {
    }

    @Shadow public abstract void broadcastSystemMessage(Component message, Function<ServerPlayer, Component> playerMessageFactory, boolean overlay);
    @Override
    public void sendScoreboardBF(ServerScoreboard newboard, ServerPlayer handle) {
        updateEntireScoreboard(newboard, handle);
    }

    /**
     * @author Cardboard Mod
     * @reason Bukkitize respawn
     * @since 1.21.8
     */
    @Overwrite
    public ServerPlayer respawn(ServerPlayer player, boolean keepInventory, RemovalReason reason) {
    	//, RespawnReason eventReason, Location location) {
    
    	RespawnReason eventReason = null;
    	Location location = null;
    	
        player.stopRiding();
        this.players.remove(player);
        // this.playersByName.remove(player.getNameForScoreboard().toLowerCase(Locale.ROOT));
        player.level().removePlayerImmediately(player, reason);
        ServerPlayer serverPlayer = player;
        Level fromWorld = player.level();
        player.wonGame = false;
        player.connection = player.connection;
        player.restoreFrom(player, keepInventory);
        player.setId(player.getId());
        player.setMainArm(player.getMainArm());

        for (String string: player.getTags()) {
            serverPlayer.addTag(string);
        }

        boolean isBedSpawn = false;
        boolean isRespawn = false;
        boolean isAnchorSpawn = false;
        TeleportTransition teleportTransition;
        if (location == null) {
            teleportTransition = ((ServerPlayerBridge) player).findRespawnPositionAndUseSpawnBlock(!keepInventory, TeleportTransition.DO_NOTHING, eventReason);
            if (!keepInventory) {
            	((ServerPlayerBridge) player).reset();
            }

            if (teleportTransition == null) {
            	if (CardboardConfig.DEBUG_PLAYER) {
            		CardboardMod.LOGGER.info("teleportTransition is null");
            	}
                return player;
            }

            isRespawn = true;
            location = CraftLocation.toBukkit(teleportTransition.position(), teleportTransition.newLevel().getWorld(), teleportTransition.yRot(), teleportTransition.xRot());
        } else {
            teleportTransition = new TeleportTransition(
                ((CraftWorld) location.getWorld()).getHandle(),
                CraftLocation.toVec3(location),
                Vec3.ZERO,
                location.getYaw(),
                location.getPitch(),
                TeleportTransition.DO_NOTHING
            );
        }

        if (teleportTransition == null) {
        	if (CardboardConfig.DEBUG_PLAYER) {
        		CardboardMod.LOGGER.info("teleTrans is null");
        	}
            return player;
        } else {
            ServerLevel level = teleportTransition.newLevel();
            ((ServerPlayerBridge) player).spawnIn(level);
            serverPlayer.unsetRemoved();
            serverPlayer.setShiftKeyDown(false);
            Vec3 vec3 = teleportTransition.position();
            ((ServerPlayerBridge) serverPlayer).spigot$forceSetPositionRotation(vec3.x, vec3.y, vec3.z, teleportTransition.yRot(), teleportTransition.xRot());
            level.getChunkSource().addTicketWithRadius(ChunkTicketBridge.POST_TELEPORT, new ChunkPos(Mth.floor(vec3.x()) >> 4, Mth.floor(vec3.z()) >> 4), 1);
            if (teleportTransition.missingRespawnBlock()) {
            	if (CardboardConfig.DEBUG_PLAYER) {
            		CardboardMod.LOGGER.info("teleTrans missing respawn block");
            	}
            	
                serverPlayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE, 0.0F));
                // serverPlayer.r.setRespawnPosition(null, false, com.destroystokyo.paper.event.player.PlayerSetSpawnEvent.Cause.PLAYER_RESPAWN);
            }

            byte b = (byte)(keepInventory ? 1 : 0);
            ServerLevel serverLevel = serverPlayer.level();
            LevelData levelData = serverLevel.getLevelData();
            serverPlayer.connection.send(new ClientboundRespawnPacket(serverPlayer.createCommonSpawnInfo(serverLevel), b));
            // serverPlayer.networkHandler.sendPacket(new ChunkLoadDistanceS2CPacket(serverLevel.spigotConfig.viewDistance));
            // serverPlayer.networkHandler.sendPacket(new SimulationDistanceS2CPacket(serverLevel.spigotConfig.simulationDistance));
            
            serverPlayer.connection.send(new ClientboundSetChunkCacheRadiusPacket(8)); // TODO
            serverPlayer.connection.send(new ClientboundSetSimulationDistancePacket(8));
            
            IMixinPlayNetworkHandler iNetworkHandler = (IMixinPlayNetworkHandler) serverPlayer.connection;
            
            if (CardboardConfig.DEBUG_PLAYER) {
        		CardboardMod.LOGGER.info("tele to " + serverPlayer.position().toString());
        	}
            
            iNetworkHandler.teleport(CraftLocation.toBukkit(serverPlayer.position(), serverLevel.getWorld(), serverPlayer.getYRot(), serverPlayer.getXRot()));
            serverPlayer.connection.send(new ClientboundSetDefaultSpawnPositionPacket(level.getRespawnData()));
            serverPlayer.connection.send(new ClientboundChangeDifficultyPacket(levelData.getDifficulty(), levelData.isDifficultyLocked()));
            serverPlayer.connection.send(new ClientboundSetExperiencePacket(serverPlayer.experienceProgress, serverPlayer.totalExperience, serverPlayer.experienceLevel));
            this.sendActivePlayerEffects(serverPlayer);
            this.sendLevelInfo(serverPlayer, level);
            this.sendPlayerPermissionLevel(serverPlayer);

            if (!iNetworkHandler.isDisconnected()) {
            	if (CardboardConfig.DEBUG_PLAYER) {
            		CardboardMod.LOGGER.info("onplrrespawned");
            	}
            	
                level.addRespawnedPlayer(serverPlayer);
                this.players.add(serverPlayer);
                // this.playersByName.put(serverPlayer.getNameForScoreboard().toLowerCase(Locale.ROOT), serverPlayer);
                this.playersByUUID.put(serverPlayer.getUUID(), serverPlayer);
            }

            serverPlayer.setHealth(serverPlayer.getHealth());
            ServerPlayer.RespawnConfig respawnConfig = serverPlayer.getRespawnConfig();
            if (!keepInventory && respawnConfig != null) {
            	LevelData.RespawnData respawnData = respawnConfig.respawnData();
                ServerLevel level1 = this.server.getLevel(respawnData.dimension());
                if (level1 != null) {
                    BlockPos blockPos = respawnData.pos();
                    BlockState blockState = level1.getBlockState(blockPos);
                    if (blockState.is(Blocks.RESPAWN_ANCHOR)) {
                        serverPlayer.connection.send(new ClientboundSoundPacket(SoundEvents.RESPAWN_ANCHOR_DEPLETE, SoundSource.BLOCKS, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1.0F, 1.0F, level.getRandom().nextLong()));
                    }

                    if (!teleportTransition.missingRespawnBlock()) {
                        if (blockState.is(BlockTags.BEDS)) {
                            isBedSpawn = true;
                        } else if (blockState.is(Blocks.RESPAWN_ANCHOR)) {
                            isAnchorSpawn = true;
                        }
                    }
                }
            }

            this.sendAllPlayerInfo(player);
            player.onUpdateAbilities();

            for (MobEffectInstance mobEffect: player.getActiveEffects()) {
                player.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), mobEffect, false));
            }

            player.triggerDimensionChangeTriggers(level);
            if (fromWorld != level) {
                PlayerChangedWorldEvent event = new PlayerChangedWorldEvent((Player) (Player)((ServerPlayerBridge)player).getBukkitEntity(), ((IMixinWorld) fromWorld).getCraftWorld());
                CraftServer.INSTANCE.getPluginManager().callEvent(event);
            }

            if (iNetworkHandler.isDisconnected()) {
                this.save(player);
            }

            if (isRespawn) {
                new PlayerPostRespawnEvent((Player) (Player)((ServerPlayerBridge)player).getBukkitEntity(), location, isBedSpawn, isAnchorSpawn, teleportTransition.missingRespawnBlock(), eventReason).callEvent();
            }

            return serverPlayer;
        }
    }
    
    @Override
    public PlayerManager_LoginResult cardboard$canPlayerLogin(Component vanilla, NameAndId nameAndId) {
    	if (null == vanilla) {
    		return PlayerManager_LoginResult.ALLOW;
    	}

    	UserBanListEntry userBanListEntry;
    	if (this.bans.isBanned(nameAndId) && (userBanListEntry = this.bans.get(nameAndId)) != null) {
    		return new PlayerManager_LoginResult(vanilla, PlayerLoginEvent.Result.KICK_BANNED);
    	}
    	
    	return new PlayerManager_LoginResult(vanilla, PlayerLoginEvent.Result.KICK_OTHER);
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
    public ServerLevel banner$worldserver = null;
    public AtomicBoolean avoidSuffocation = new AtomicBoolean(true);
    
    // Banner start - Fix mixin by apoli
    public org.bukkit.World fromWorld;
    public PlayerRespawnEvent respawnEvent;
    public ServerLevel worldserver1;
    public LevelData worlddata;
    public ServerPlayer entityplayer_vanilla;
    // Banner end

}
