package org.cardboardpowered.mixin.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;
import org.cardboardpowered.impl.entity.PlayerImpl;
import org.spigotmc.SpigotConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.javazilla.bukkitfabric.interfaces.IMixinEntity;
import com.javazilla.bukkitfabric.interfaces.IMixinPlayNetworkHandler;
import com.javazilla.bukkitfabric.interfaces.IMixinServerEntityPlayer;

import io.papermc.paper.event.player.PlayerFailMoveEvent;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerPosition;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.GameMode;

@Mixin(value = ServerPlayNetworkHandler.class, priority = 801)
public class MixinServerPlayNetworkHandler_PlayerMove {

	// Cardboard - start
	private boolean hasMoved = false;
	private int lastTick = 0;
	public int allowedPlayerTicks = 1;
	private double lastPosX = Double.MAX_VALUE;
	private double lastPosY = Double.MAX_VALUE;
	private double lastPosZ = Double.MAX_VALUE;
	private float lastPitch = Float.MAX_VALUE;
	private float lastYaw = Float.MAX_VALUE;
	private boolean justTeleported = false;
	// Cardboard - end
	
	@Shadow public double lastTickX;
    @Shadow public double lastTickY;
    @Shadow public double lastTickZ;
    @Shadow public double updatedX;
    @Shadow public double updatedY;
    @Shadow public double updatedZ;
    @Shadow private boolean floating;
    @Shadow private int movePacketsCount;
    @Shadow private int lastTickMovePacketsCount;
	
	@Shadow public ServerPlayerEntity player;
	@Shadow public void syncWithPlayerPosition() {}
	@Shadow private void handleMovement(Vec3d movement) {}
	@Shadow private boolean handlePendingTeleport() {return false;}
	@Shadow private boolean isEntityOnAir(net.minecraft.entity.Entity entity) { return false; }
	@Shadow private boolean shouldCheckMovement(boolean elytra) {return false;}
	// @Shadow private boolean hasNewCollision(ServerWorld world, net.minecraft.entity.Entity entity, Box oldBox, Box newBox) {return false;}
	// @Shadow private void internalTeleport(double d0, double d1, double d2, float f, float f1) {};
	@Shadow private static boolean isMovementInvalid(double x, double y, double z, float yaw, float pitch) {return false;}
	
	private boolean hasNewCollision(ServerWorld world, net.minecraft.entity.Entity entity, Box oldBox, Box newBox) {
        /*int i;
        ArrayList<Box> collisionsBB = new ArrayList<Box>();
        ArrayList<VoxelShape> collisionsVoxel = new ArrayList<VoxelShape>();
        CollisionUtil.getCollisions(world, entity, newBox, collisionsVoxel, collisionsBB, 6, null, null);
        int len = collisionsBB.size();
        for (i = 0; i < len; ++i) {
            Box box = (Box)collisionsBB.get(i);
            if (CollisionUtil.voxelShapeIntersect(box, oldBox)) continue;
            return true;
        }
        len = collisionsVoxel.size();
        for (i = 0; i < len; ++i) {
            VoxelShape voxel = (VoxelShape)collisionsVoxel.get(i);
            if (CollisionUtil.voxelShapeIntersectNoEmpty(voxel, oldBox)) continue;
            return true;
        }*/
        return false;
    }
	
	@Shadow
	public void requestTeleport( PlayerPosition pos, Set<PositionFlag> flags) {}
	
	@Shadow
    public void requestTeleport(double d0, double d1, double d2, float f, float f1) {}
	
	@Shadow
    public int ticks;
	
	@Shadow
    public Vec3d requestedTeleportPos;
	
	public PlayerImpl getCraftPlayer() {
        return this.player == null ? null : (PlayerImpl) ((IMixinEntity)this.player).getBukkitEntity();
    }
	
	// Cardboard - Paper start
	private PlayerFailMoveEvent fireFailMove(PlayerFailMoveEvent.FailReason failReason, double toX, double toY, double toZ, float toYaw, float toPitch, boolean logWarning) {
        PlayerImpl player = this.getCraftPlayer();
        Location from = new Location(player.getWorld(), this.lastPosX, this.lastPosY, this.lastPosZ, this.lastYaw, this.lastPitch);
        Location to = new Location(player.getWorld(), toX, toY, toZ, toYaw, toPitch);
        PlayerFailMoveEvent event = new PlayerFailMoveEvent(player, failReason, false, logWarning, from, to);
        event.callEvent();
        return event;
    }
	// Cardboard - Paper end
	
	/**
	 * @author Cardboard Mod
	 * @reason Bukkit Teleport
	 */
	@Overwrite
	public void onPlayerMove(PlayerMoveC2SPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, (ServerPlayNetworkHandler)(Object)this, this.player.getServerWorld());
        if (isMovementInvalid(packet.getX(0.0), packet.getY(0.0), packet.getZ(0.0), packet.getYaw(0.0f), packet.getPitch(0.0f))) {
            // this.disconnect(Text.translatable("multiplayer.disconnect.invalid_player_movement"), PlayerKickEvent.Cause.INVALID_PLAYER_MOVEMENT);
        } else {
            ServerWorld worldserver = this.player.getServerWorld();
            if (!this.player.notInAnyWorld /*&& !this.player.isImmobile()*/) {
                if (this.ticks == 0) {
                    this.syncWithPlayerPosition();
                }
                if (!this.handlePendingTeleport() && this.player.isLoaded()) {
                    float f1;
                    float f;
                    double d2;
                    double d1;
                    double d0;
                    double toX = d0 = ServerPlayNetworkHandler.clampHorizontal(packet.getX(this.player.getX()));
                    double toY = d1 = ServerPlayNetworkHandler.clampVertical(packet.getY(this.player.getY()));
                    double toZ = d2 = ServerPlayNetworkHandler.clampHorizontal(packet.getZ(this.player.getZ()));
                    float toYaw = f = MathHelper.wrapDegrees(packet.getYaw(this.player.getYaw()));
                    float toPitch = f1 = MathHelper.wrapDegrees(packet.getPitch(this.player.getPitch()));
                    if (this.player.hasVehicle()) {
                        this.player.updatePositionAndAngles(this.player.getX(), this.player.getY(), this.player.getZ(), f, f1);
                        this.player.getServerWorld().getChunkManager().updatePosition(this.player);
                        this.allowedPlayerTicks = 20;
                    } else {
                        double prevX = this.player.getX();
                        double prevY = this.player.getY();
                        double prevZ = this.player.getZ();
                        float prevYaw = this.player.getYaw();
                        float prevPitch = this.player.getPitch();
                        double d3 = this.player.getX();
                        double d4 = this.player.getY();
                        double d5 = this.player.getZ();
                        double d6 = d0 - this.lastTickX;
                        double d7 = d1 - this.lastTickY;
                        double d8 = d2 - this.lastTickZ;
                        double d9 = this.player.getVelocity().lengthSquared();
                        double currDeltaX = toX - prevX;
                        double currDeltaY = toY - prevY;
                        double currDeltaZ = toZ - prevZ;
                        double d10 = Math.max(d6 * d6 + d7 * d7 + d8 * d8, currDeltaX * currDeltaX + currDeltaY * currDeltaY + currDeltaZ * currDeltaZ - 1.0);
                        double otherFieldX = d0 - this.updatedX;
                        double otherFieldY = d1 - this.updatedY;
                        double otherFieldZ = d2 - this.updatedZ;
                        d10 = Math.max(d10, otherFieldX * otherFieldX + otherFieldY * otherFieldY + otherFieldZ * otherFieldZ - 1.0);
                        if (this.player.isSleeping()) {
                            if (d10 > 1.0) {
                                this.requestTeleport(this.player.getX(), this.player.getY(), this.player.getZ(), f, f1);
                            }
                        } else {
                            PlayerFailMoveEvent event;
                            PlayerFailMoveEvent event2;
                            boolean didCollide;
                            boolean flag1;
                            boolean flag = this.player.isGliding();
                            if (worldserver.getTickManager().shouldTick()) {
                                PlayerFailMoveEvent event3;
                                ++this.movePacketsCount;
                                int i = this.movePacketsCount - this.lastTickMovePacketsCount;
                                this.allowedPlayerTicks = (int)((long)this.allowedPlayerTicks + (System.currentTimeMillis() / 50L - (long)this.lastTick));
                                this.allowedPlayerTicks = Math.max(this.allowedPlayerTicks, 1);
                                this.lastTick = (int)(System.currentTimeMillis() / 50L);
                                if (i > Math.max(this.allowedPlayerTicks, 5)) {
                                    // LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", (Object)this.player.getName().getString(), (Object)i);
                                    i = 1;
                                }
                                this.allowedPlayerTicks = packet.changeLook || d10 > 0.0 ? --this.allowedPlayerTicks : 20;
                                double speed = this.player.getAbilities().flying ? (double)(this.player.getAbilities().flySpeed * 20.0f) : (double)(this.player.getAbilities().walkSpeed * 10.0f);
                                /*
                                if (
                                		!(
                                				/*!this.player.getWorld().paperConfig().chunks.preventMovingIntoUnloadedChunks
                                				|| this.player.getX() == toX && this.player.getZ() == toZ ||
                                				worldserver.areChunksLoadedForMove(this.player.getBoundingBox().stretch(new Vec3d(toX, toY, toZ).subtract(this.player.getPos()))) || (event3 = this.fireFailMove(PlayerFailMoveEvent.FailReason.MOVED_INTO_UNLOADED_CHUNK, toX, toY, toZ, toYaw, toPitch, false)).isAllowed())) {
                                    this.internalTeleport(PlayerPosition.fromEntity(this.player), Collections.emptySet());
                                    return;
                                }*/
                                if (this.shouldCheckMovement(flag)) {
                                    PlayerFailMoveEvent event4;
                                    float f2;
                                    float f3 = f2 = flag ? 300.0f : 100.0f;
                                    if (d10 - d9 > Math.max((double)f2, Math.pow(SpigotConfig.movedTooQuicklyMultiplier * (double)i * speed, 2.0)) && !(event4 = this.fireFailMove(PlayerFailMoveEvent.FailReason.MOVED_TOO_QUICKLY, toX, toY, toZ, toYaw, toPitch, true)).isAllowed()) {
                                        if (event4.getLogWarning()) {
                                            // LOGGER.warn("{} moved too quickly! {},{},{}", new Object[]{this.player.getName().getString(), d6, d7, d8});
                                        }
                                        this.requestTeleport(this.player.getX(), this.player.getY(), this.player.getZ(), this.player.getYaw(), this.player.getPitch());
                                        return;
                                    }
                                }
                            }
                            Box axisalignedbb = this.player.getBoundingBox();
                            d6 = d0 - this.updatedX;
                            d7 = d1 - this.updatedY;
                            d8 = d2 - this.updatedZ;
                            boolean bl = flag1 = d7 > 0.0;
                            if (this.player.isOnGround() && !packet.isOnGround() && flag1) {
                                PlayerJumpEvent event5;
                                PlayerImpl player = this.getCraftPlayer();
                                Location from = new Location(player.getWorld(), this.lastPosX, this.lastPosY, this.lastPosZ, this.lastYaw, this.lastPitch);
                                Location to = player.getLocation().clone();
                                if (packet.changesPosition()) {
                                    to.setX(packet.x);
                                    to.setY(packet.y);
                                    to.setZ(packet.z);
                                }
                                if (packet.changesLook()) {
                                    to.setYaw(packet.yaw);
                                    to.setPitch(packet.pitch);
                                }
                                if ((event5 = new PlayerJumpEvent((Player)player, from, to)).callEvent()) {
                                    this.player.jump();
                                } else {
                                    from = event5.getFrom();
                                    ((IMixinPlayNetworkHandler)(Object)this).internalTeleport(new PlayerPosition(CraftLocation.toVec3D(from), Vec3d.ZERO, from.getYaw(), from.getPitch()), Collections.emptySet());
                                    return;
                                }
                            }
                            boolean flag2 = this.player.groundCollision;
                            this.player.move(MovementType.PLAYER, new Vec3d(d6, d7, d8));
                            this.player.onGround = packet.isOnGround();
                            boolean bl2 = didCollide = toX != this.player.getX() || toY != this.player.getY() || toZ != this.player.getZ();
                            if (this.requestedTeleportPos != null) {
                                return;
                            }
                            double d11 = d7;
                            d6 = d0 - this.player.getX();
                            d7 = d1 - this.player.getY();
                            if (d7 > -0.5 || d7 < 0.5) {
                                d7 = 0.0;
                            }
                            d8 = d2 - this.player.getZ();
                            d10 = d6 * d6 + d7 * d7 + d8 * d8;
                            boolean movedWrongly = false;
                            if (!(this.player.isInTeleportationState() || !(d10 > SpigotConfig.movedWronglyThreshold) || this.player.isSleeping() || this.player.interactionManager.isCreative() || this.player.interactionManager.getGameMode() == GameMode.SPECTATOR || (event2 = this.fireFailMove(PlayerFailMoveEvent.FailReason.MOVED_WRONGLY, toX, toY, toZ, toYaw, toPitch, true)).isAllowed())) {
                                movedWrongly = true;
                                if (event2.getLogWarning()) {
                                    // LOGGER.warn("{} moved wrongly!", (Object)this.player.getName().getString());
                                }
                            }
                            boolean teleportBack = !this.player.noClip && !this.player.isSleeping() && movedWrongly;
                            this.player.updatePositionAndAngles(d0, d1, d2, f, f1);
                            if (!(this.player.noClip || this.player.isSleeping() || teleportBack)) {
                                Box newBox = this.player.getBoundingBox();
                                if (didCollide || !axisalignedbb.equals(newBox)) {
                                    teleportBack = this.hasNewCollision(worldserver, this.player, axisalignedbb, newBox);
                                }
                            }
                            if (teleportBack && (event = this.fireFailMove(PlayerFailMoveEvent.FailReason.CLIPPED_INTO_BLOCK, toX, toY, toZ, toYaw, toPitch, false)).isAllowed()) {
                                teleportBack = false;
                            }
                            if (teleportBack) {
                                
                            	
                            	ServerPlayNetworkHandler thiz = (ServerPlayNetworkHandler)(Object)this;
                            	
                            	// thiz.teleport
                            	
                            	thiz.requestTeleport(d3, d4, d5, f, f1);
                                
                                
                                
                                this.player.handleFall(this.player.getX() - d3, this.player.getY() - d4, this.player.getZ() - d5, packet.isOnGround());
                            } else {
                                this.player.updatePositionAndAngles(prevX, prevY, prevZ, prevYaw, prevPitch);
                                PlayerImpl player = this.getCraftPlayer();
                                if (!this.hasMoved) {
                                    this.lastPosX = prevX;
                                    this.lastPosY = prevY;
                                    this.lastPosZ = prevZ;
                                    this.lastYaw = prevYaw;
                                    this.lastPitch = prevPitch;
                                    this.hasMoved = true;
                                }
                                Location from = new Location(player.getWorld(), this.lastPosX, this.lastPosY, this.lastPosZ, this.lastYaw, this.lastPitch);
                                Location to = player.getLocation().clone();
                                if (packet.changePosition) {
                                    to.setX(packet.x);
                                    to.setY(packet.y);
                                    to.setZ(packet.z);
                                }
                                if (packet.changeLook) {
                                    to.setYaw(packet.yaw);
                                    to.setPitch(packet.pitch);
                                }
                                double delta = Math.pow(this.lastPosX - to.getX(), 2.0) + Math.pow(this.lastPosY - to.getY(), 2.0) + Math.pow(this.lastPosZ - to.getZ(), 2.0);
                                float deltaAngle = Math.abs(this.lastYaw - to.getYaw()) + Math.abs(this.lastPitch - to.getPitch());
                                if ((delta > 0.00390625 || deltaAngle > 10.0f) /*&& !this.player.isImmobile()*/) {
                                    this.lastPosX = to.getX();
                                    this.lastPosY = to.getY();
                                    this.lastPosZ = to.getZ();
                                    this.lastYaw = to.getYaw();
                                    this.lastPitch = to.getPitch();
                                    Location oldTo = to.clone();
                                    PlayerMoveEvent event6 = new PlayerMoveEvent(player, from, to);
                                    CraftServer.INSTANCE.getPluginManager().callEvent(event6);
                                    if (event6.isCancelled()) {
                                        ((IMixinPlayNetworkHandler)(Object)this).teleport(from);
                                        return;
                                    }
                                    if (!oldTo.equals((Object)event6.getTo()) && !event6.isCancelled()) {
                                        ((IMixinServerEntityPlayer)this.player).getBukkitEntity().teleport(event6.getTo(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                                        return;
                                    }
                                    if (!from.equals((Object)this.getCraftPlayer().getLocation()) && this.justTeleported) {
                                        this.justTeleported = false;
                                        return;
                                    }
                                }
                                this.player.updatePositionAndAngles(d0, d1, d2, f, f1);
                                boolean flag4 = this.player.isUsingRiptide();
                                this.floating = d11 >= -0.03125 && !flag2 &&
                                		this.player.interactionManager.getGameMode() != GameMode.SPECTATOR &&
                                		!CraftServer.server.isFlightEnabled() &&
                                		!this.player.getAbilities().allowFlying &&
                                		!this.player.hasStatusEffect(StatusEffects.LEVITATION) &&
                                		!flag && !flag4 && this.isEntityOnAir(this.player);
                                this.player.getServerWorld().getChunkManager().updatePosition(this.player);
                                Vec3d vec3d = new Vec3d(this.player.getX() - d3, this.player.getY() - d4, this.player.getZ() - d5);
                                this.player.setMovement(packet.isOnGround(), packet.horizontalCollision(), vec3d);
                                this.player.handleFall(vec3d.x, vec3d.y, vec3d.z, packet.isOnGround());
                                this.player.queueBlockCollisionCheck(new Vec3d(d3, d4, d5), this.player.getPos());
                                this.handleMovement(vec3d);
                                if (flag1) {
                                    this.player.onLanding();
                                }
                                if (packet.isOnGround() || this.player.hasLandedInFluid() || this.player.isClimbing() || this.player.isSpectator() || flag || flag4) {
                                    this.player.tryClearCurrentExplosion();
                                }
                                this.player.increaseTravelMotionStats(this.player.getX() - d3, this.player.getY() - d4, this.player.getZ() - d5);
                                this.updatedX = this.player.getX();
                                this.updatedY = this.player.getY();
                                this.updatedZ = this.player.getZ();
                            }
                        }
                    }
                }
            }
        }
    }
	
}
