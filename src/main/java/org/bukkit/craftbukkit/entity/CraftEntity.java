package org.bukkit.craftbukkit.entity;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.pointer.PointersSupplier;
import net.minecraft.world.level.portal.TeleportTransition;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.cardboardpowered.impl.entity.UnknownEntity;
import org.cardboardpowered.bridge.world.item.ItemStackBridge;
import org.cardboardpowered.bridge.commands.CommandSourceBridge;
import org.cardboardpowered.bridge.world.entity.EntityBridge;
import org.cardboardpowered.bridge.server.level.ServerPlayerBridge;
import org.cardboardpowered.bridge.world.level.LevelBridge;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.logging.LogUtils;

import me.isaiah.common.entity.IRemoveReason;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkMap.TrackedEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.EntityProcessor;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataContainer;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.cardboardpowered.impl.world.CraftWorld;
import org.cardboardpowered.bridge.world.level.chunk.LevelChunkBridge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.PaperDataComponentType;
import io.papermc.paper.entity.LookAnchor;
import io.papermc.paper.entity.TeleportFlag;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;

public class CraftEntity implements Entity, CommandSender, CommandSourceBridge {

	
	protected static final Logger LOGGER = LogUtils.getLogger();
	
    protected static PermissibleBase perm;
    private static final CraftPersistentDataTypeRegistry DATA_TYPE_REGISTRY = new CraftPersistentDataTypeRegistry();

    public net.minecraft.world.entity.Entity entity;
    private final CraftPersistentDataContainer persistentDataContainer = new CraftPersistentDataContainer(DATA_TYPE_REGISTRY);
    static final PointersSupplier<Entity> POINTERS_SUPPLIER = PointersSupplier.<org.bukkit.entity.Entity>builder()
            .resolving(net.kyori.adventure.identity.Identity.DISPLAY_NAME, org.bukkit.entity.Entity::name)
            .resolving(net.kyori.adventure.identity.Identity.UUID, org.bukkit.entity.Entity::getUniqueId)
            .resolving(net.kyori.adventure.permission.PermissionChecker.POINTER, entity1 -> entity1::permissionValue)
            .build();

    protected final CraftServer server = CraftServer.INSTANCE;
    
    private final EntityType entityType;

    public CraftEntity(net.minecraft.world.entity.Entity entity) {
        this.entity = entity;
        this.entityType = CraftEntityType.minecraftToBukkit(entity.getType());
    }

    @Override
    public net.kyori.adventure.pointer.Pointers pointers() {
        return POINTERS_SUPPLIER.view(this);
    }

    public net.minecraft.world.entity.Entity getHandle() {
        return entity;
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return server.getEntityMetadata().getMetadata(this, metadataKey);
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return server.getEntityMetadata().hasMetadata(this, metadataKey);
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        server.getEntityMetadata().removeMetadata(this, metadataKey, owningPlugin);
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        server.getEntityMetadata().setMetadata(this, metadataKey, newMetadataValue);
    }

    @Override
    public String getName() {
        return entity.getName().getString();
    }

    @Override
    public void sendMessage(String message) {
    	
    	me.isaiah.common.cmixin.IMixinEntity e = (me.isaiah.common.cmixin.IMixinEntity) entity;
    	e.IsendText(net.minecraft.network.chat.Component.nullToEmpty(message), UUID.randomUUID());
    }

    @Override
    public void sendMessage(String[] arg0) {
        for (String str : arg0)
            sendMessage(str);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0) {
        return getPermissibleBase().addAttachment(arg0);
    }

    @Override
    public  PermissionAttachment addAttachment(Plugin arg0, int arg1) {
        return getPermissibleBase().addAttachment(arg0, arg1);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2) {
        return getPermissibleBase().addAttachment(arg0, arg1, arg2);
    }

    @Override
    public  PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2, int arg3) {
        return getPermissibleBase().addAttachment(arg0, arg1, arg2, arg3);
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return getPermissibleBase().getEffectivePermissions();
    }

    @Override
    public boolean hasPermission(String arg0) {
        return getPermissibleBase().hasPermission(arg0);
    }

    @Override
    public boolean hasPermission(Permission arg0) {
        return getPermissibleBase().hasPermission(arg0);
    }

    @Override
    public boolean isPermissionSet(String arg0) {
        return getPermissibleBase().isPermissionSet(arg0);
    }

    @Override
    public boolean isPermissionSet(Permission arg0) {
        return getPermissibleBase().isPermissionSet(arg0);
    }

    @Override
    public void recalculatePermissions() {
        getPermissibleBase().recalculatePermissions();
    }

    @Override
    public void removeAttachment(PermissionAttachment arg0) {
        getPermissibleBase().removeAttachment(arg0);
    }

    @Override
    public boolean isOp() {
        return getPermissibleBase().isOp();
    }

    @Override
    public void setOp(boolean arg0) {
        getPermissibleBase().setOp(arg0);
    }

    @Override
    public String getCustomName() {
        return entity.getCustomName().getString();
    }

    @Override
    public void setCustomName(String name) {
        entity.setCustomName(ComponentUtils.fromMessage(new LiteralMessage(name)));
    }

    @Override
    public PersistentDataContainer getPersistentDataContainer() {
        return persistentDataContainer;
    }

    @Override
    public boolean addPassenger(Entity arg0) {
        return ((CraftEntity) arg0).getHandle().startRiding(getHandle()); // , true);
    }

    @Override
    public boolean addScoreboardTag(String arg0) {
    	// 1.19.2: addScoreboardTag
    	// 1.19.4: addCommandTag
        return entity.addTag(arg0);
    }

    @Override
    public boolean eject() {
        if (isEmpty()) return false;
        entity.ejectPassengers();
        return true;
    }

    @Override
    public BoundingBox getBoundingBox() {
        AABB b = entity.getBoundingBox();
        return new BoundingBox(b.minX, b.minY, b.minZ, b.maxX, b.maxY, b.maxZ);
    }

    @Override
    public int getEntityId() {
        return entity.getId();
    }

    @Override
    public BlockFace getFacing() {
        return CraftBlock.notchToBlockFace(entity.getMotionDirection());
    }

    @Override
    public float getFallDistance() {
        return (float) entity.fallDistance;
    }

    @Override
    public int getFireTicks() {
        return entity.remainingFireTicks;
    }

    @Override
    public double getHeight() {
        return entity.getBbHeight();
    }

    @Override
    public EntityDamageEvent getLastDamageCause() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Location getLocation() {
        return new Location(getWorld(), entity.getX(), entity.getY(), entity.getZ(), entity.yRot, entity.xRot);
    }

    @Override
    public Location getLocation(Location loc) {
        if (loc != null) {
            loc.setWorld(getWorld());
            loc.setX(entity.getX());
            loc.setY(entity.getY());
            loc.setZ(entity.getZ());
            loc.setYaw(entity.yRot);
            loc.setPitch(entity.xRot);
        }
        return loc;
    }

    @Override
    public int getMaxFireTicks() {
        return entity.getFireImmuneTicks();
    }

    @Override
    public List<org.bukkit.entity.Entity> getNearbyEntities(double x, double y, double z) {
        List<net.minecraft.world.entity.Entity> notchEntityList = entity.level().getEntities(entity, entity.getBoundingBox().inflate(x, y, z), null);
        List<org.bukkit.entity.Entity> bukkitEntityList = new java.util.ArrayList<org.bukkit.entity.Entity>(notchEntityList.size());

        for (net.minecraft.world.entity.Entity e : notchEntityList)
            bukkitEntityList.add(((EntityBridge)e).getBukkitEntity());
        return bukkitEntityList;
    }

    @Override
    public Entity getPassenger() {
        return isEmpty() ? null : ((EntityBridge)getHandle().getFirstPassenger()).getBukkitEntity();
    }

    @Override
    public List<Entity> getPassengers() {
        return Lists.newArrayList(Lists.transform(getHandle().getPassengers(), new Function<net.minecraft.world.entity.Entity, org.bukkit.entity.Entity>() {
            @Override
            public org.bukkit.entity.Entity apply(net.minecraft.world.entity.Entity input) {
                return ((EntityBridge)input).getBukkitEntity();
            }
        }));
    }

    @SuppressWarnings("deprecation")
    @Override
    public PistonMoveReaction getPistonMoveReaction() {
        return PistonMoveReaction.getById(entity.getPistonPushReaction().ordinal());
    }

    @Override
    public int getPortalCooldown() {
        return entity.getDimensionChangingDelay();
    }

    @Override
    public Pose getPose() {
        return Pose.values()[entity.getPose().ordinal()];
    }

    @Override
    public Set<String> getScoreboardTags() {
        return entity.getTags();
    }

    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public int getTicksLived() {
        return entity.tickCount;
    }

    @Override
    public UUID getUniqueId() {
        return entity.getUUID();
    }

    @Override
    public Entity getVehicle() {
        if (!isInsideVehicle())
            return null;
        return ((EntityBridge) entity.getVehicle()).getBukkitEntity();
    }

    @Override
    public Vector getVelocity() {
        Vec3 vec3d = entity.getDeltaMovement();
        return new Vector(vec3d.x, vec3d.y, vec3d.z);
    }

    @Override
    public double getWidth() {
        return entity.getBbWidth();
    }

    @Override
    public World getWorld() {
        return ((LevelBridge) entity.level()).getCraftWorld();
    }

    @Override
    public boolean hasGravity() {
        return !entity.isNoGravity();
    }

    @Override
    public boolean isCustomNameVisible() {
        return entity.isCustomNameVisible();
    }

    @Override
    public boolean isDead() {
        return !entity.isAlive();
    }

    @Override
    public boolean isEmpty() {
        return !entity.isVehicle();
    }

    @Override
    public boolean isGlowing() {
        return entity.isCurrentlyGlowing();
    }

    @Override
    public boolean isInsideVehicle() {
        return entity.isPassenger();
    }

    @Override
    public boolean isInvulnerable() {
        return entity.isInvulnerable();
    }

    @Override
    public boolean isOnGround() {
        if (entity instanceof Projectile)
            return ((Projectile) entity).onGround();

        return entity.onGround();
    }

    @Override
    public boolean isPersistent() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isSilent() {
        return entity.isSilent();
    }

    @Override
    public boolean isValid() {
        return entity.isAlive();
    }

    @Override
    public boolean leaveVehicle() {
        if (!isInsideVehicle())
            return false;
        entity.stopRiding();
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void playEffect(EntityEffect type) {
        if (type.getApplicable().isInstance(this))
            this.getHandle().level().broadcastEntityEvent(getHandle(), type.getData());
    }

    @Override
    public void remove() {
        me.isaiah.common.cmixin.IMixinEntity common = (me.isaiah.common.cmixin.IMixinEntity)this.entity;
        common.Iremove(IRemoveReason.DISCARDED);
    }

    @Override
    public boolean removePassenger(Entity passenger) {
        ((CraftEntity) passenger).getHandle().stopRiding();
        return true;
    }

    @Override
    public boolean removeScoreboardTag(String arg0) {
        return entity.removeTag(arg0);
    }

    @Override
    public void setCustomNameVisible(boolean arg0) {
        entity.setCustomNameVisible(arg0);
    }

    @Override
    public void setFallDistance(float arg0) {
        entity.fallDistance = arg0;
    }

    @Override
    public void setFireTicks(int arg0) {
        entity.setRemainingFireTicks(arg0);
    }

    @Override
    public void setGlowing(boolean arg0) {
        entity.setGlowingTag(arg0);
    }

    @Override
    public void setGravity(boolean arg0) {
        entity.setNoGravity(!arg0);
    }

    @Override
    public void setInvulnerable(boolean arg0) {
        entity.setInvulnerable(arg0);
    }

    @Override
    public void setLastDamageCause(EntityDamageEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean setPassenger(org.bukkit.entity.Entity passenger) {
        Preconditions.checkArgument(!this.equals(passenger), "Entity cannot ride itself.");
        if (passenger instanceof CraftEntity) {
            eject();
            return ((CraftEntity) passenger).getHandle().startRiding(getHandle());
        } else return false;
    }

    @Override
    public void setPersistent(boolean arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setPortalCooldown(int arg0) {
        entity.setPortalCooldown(arg0);
    }

    @Override
    public void setRotation(float yaw, float pitch) {
        yaw = Location.normalizeYaw((float)yaw);
        pitch = Location.normalizePitch((float)pitch);
        this.entity.setYRot(yaw);
        this.entity.setXRot(pitch);
        this.entity.yRotO = yaw;
        this.entity.xRotO = pitch;
        this.entity.setYHeadRot(yaw);
    }

    @Override
    public void setSilent(boolean arg0) {
        entity.setSilent(arg0);
    }

    @Override
    public void setTicksLived(int arg0) {
        entity.tickCount = arg0;
    }

    @Override
    public void setVelocity(Vector vec) {
        entity.setDeltaMovement(new Vec3(vec.getX(), vec.getY(), vec.getZ()));
        entity.hurtMarked = true;
    }

    @Override
    public boolean teleport(Location arg0) {
        return teleport(arg0, TeleportCause.PLUGIN);
    }

    @Override
    public boolean teleport(Entity arg0) {
        return teleport(arg0, TeleportCause.PLUGIN);
    }

    @Override
    public boolean teleport(Location loc, TeleportCause arg1) {
        loc.checkFinite();

        if (entity.isVehicle() || !entity.isAlive())
            return false;

        entity.stopRiding();

        if(loc.getWorld() == null || loc.getWorld().equals(getWorld())) {
            entity.absSnapTo(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
            entity.setYHeadRot(loc.getYaw());
        } else {
            entity.teleportTo(
		            ((CraftWorld) loc.getWorld()).getHandle(),
                    loc.getX(), loc.getY(), loc.getZ(),
                    EnumSet.allOf(Relative.class),
                    loc.getYaw(), loc.getPitch(), true);
            return true;
        }
        return true;
    }

    @Override
    public boolean teleport(Entity arg0, TeleportCause arg1) {
        return teleport(arg0.getLocation(), arg1);
    }

    @Override
    public CommandSender getBukkitSender(CommandSourceStack serverCommandSource) {
        return this;
    }

    public static PermissibleBase getPermissibleBase() {
        if (perm == null) {
            perm = new PermissibleBase(new ServerOperator() {
                @Override
                public boolean isOp() {
                    return false;
                }

                @Override
                public void setOp(boolean value) {
                }
            });
        }
        return perm;
    }

    private final Entity.Spigot spigot = new Entity.Spigot(){

        @Override
        public void sendMessage(net.md_5.bungee.api.chat.BaseComponent component){
        }

        @Override
        public void sendMessage(net.md_5.bungee.api.chat.BaseComponent... components) {
        }
    };

    @Override
    public org.bukkit.entity.Entity.Spigot spigot() {
        return spigot;
    }

    protected CompoundTag save() {
        try (ProblemReporter.ScopedCollector problemReporter = new ProblemReporter.ScopedCollector(() -> "Entity#save", LOGGER);){
            TagValueOutput tagValueOutput = TagValueOutput.createWithContext(problemReporter, this.getHandle().registryAccess());
            
            // TODO: Check getSavedEntityId/getEncodeId(true)
           
            tagValueOutput.putString("id", this.getHandle().getEncodeId());
            this.getHandle().saveWithoutId(tagValueOutput);
            CompoundTag nbtCompound = tagValueOutput.buildResult();
            return nbtCompound;
        }
    }

    // SPIGOT-759
    public void sendMessage(UUID sender, String message) {
        this.sendMessage(message);
    }

    // SPIGOT-759
    public void sendMessage(UUID sender, String[] messages) {
        this.sendMessage(messages);
    }

    // PaperAPI - START
    public Location getOrigin() {
        Location origin = ((EntityBridge)getHandle()).getOriginBF();
        return origin == null ? null : origin.clone();
    }

    public boolean isTicking() {
        return true; // TODO: 1.17ify: nms.getEntityWorld().getChunkManager().shouldTickEntity(nms);
    }

    public boolean isInLava() {
        return entity.isInLava();
    }

    public boolean isInWater() {
        return entity.isUnderWater();
    }

    public boolean isInRain() {
        return entity.isInRain();
    }

    @Override
    public Chunk getChunk() {
        LevelChunkBridge wc = (LevelChunkBridge) entity.level().getChunkAt(entity.blockPosition());
        return wc.getBukkitChunk();
    }

    @Override
    public SpawnReason getEntitySpawnReason() {
        // TODO Auto-generated method stub
        return SpawnReason.DEFAULT;
    }

    @Override
    public boolean isInBubbleColumn() {
        // TODO Auto-generated method stub
    	return false; // removed from paper
    	//return nms.isInsideBubbleColumn();
    }

    @Override
    public boolean isInWaterOrBubbleColumn() {
        // TODO Auto-generated method stub
        return false; // removed from paper
    	// return nms.isInsideWaterOrBubbleColumn();
    }

    @Override
    public boolean isInWaterOrRain() {
        // TODO Auto-generated method stub
        return entity.isInWaterOrRain();
    }

    @Override
    public boolean isInWaterOrRainOrBubbleColumn() {
        // TODO Auto-generated method stub
        
    	// 1.21.8: Removed from Paper
    	
    	return false;
    	
    	// return nms.isInsideWaterOrBubbleColumn();
    }

    @Override
    public boolean fromMobSpawner() {
        // TODO Auto-generated method stub
        return false;
    }
    // PaperAPI - END

	@Override
	public Component customName() {
        net.minecraft.network.chat.Component name = this.getHandle().getCustomName();
        return name != null ? PaperAdventure.asAdventure(name) : null;
    }

	@Override
    public void customName(Component customName) {
        this.getHandle().setCustomName(customName != null ? PaperAdventure.asVanilla(customName) : null);
    }

    @Override
    public int getFreezeTicks() {
        return entity.getTicksFrozen();
    }

    @Override
    public int getMaxFreezeTicks() {
        return entity.getTicksFrozen();
    }

    @Override
    public boolean isFrozen() {
        return entity.isFullyFrozen();
    }

    @Override
    public boolean isVisualFire() {
        return entity.displayFireAnimation();
    }

    @Override
    public void setFreezeTicks(int arg0) {
        entity.setTicksFrozen(arg0);
    }

    @Override
    public void setVisualFire(boolean arg0) {
        entity.setSharedFlagOnFire(arg0);
    }
    
    @Override
    public boolean spawnAt(@NotNull Location arg0, @NotNull SpawnReason arg1) {

        return this.spawnAt(arg0);
    }

    @Override
    public Component teamDisplayName() {
        return Component.text(this.getCustomName());
    }

    @Override
    public Component name() {
        return Component.text(getName());
    }
    
    @Override
    public @NotNull Set<Player> getTrackedPlayers() {
        ImmutableSet.Builder<Player> players = ImmutableSet.builder();
        ServerLevel world = (ServerLevel) entity.level();
        TrackedEntity entityTracker = world.getChunkSource()
                .chunkMap.entityMap
                .get(this.getEntityId());
        if (entityTracker != null) {

	        for(ServerPlayerConnection connection : entityTracker.seenBy) {
		        players.add((Player) ((ServerPlayerBridge) connection.getPlayer()).getBukkitEntity());
	        }
        }

        return players.build();
    }

	@Override
	public @NotNull SpawnCategory getSpawnCategory() {
		// TODO Auto-generated method stub
		return SpawnCategory.MISC;
	}

	@Override
	public boolean isFreezeTickingLocked() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInPowderedSnow() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void lockFreezeTicks(boolean arg0) {
		// TODO Auto-generated method stub
		
	}
	
	// 1.19.2:

	@Override
    public boolean collidesAt(@NotNull Location location) {
        AABB aabb = ((EntityBridge)this.getHandle()).cardboad_getBoundingBoxAt(location.getX(), location.getY(), location.getZ());
        return !this.getHandle().level().noCollision(this.getHandle(), aabb);
    }

	@Override
	public @NotNull Sound getSwimHighSpeedSplashSound() {
		return Sound.ENTITY_GENERIC_SWIM; 
	}

	@Override
	public @NotNull Sound getSwimSound() {
		return Sound.ENTITY_GENERIC_SWIM; 
	}

	@Override
	public Sound getSwimSplashSound() {
        return Sound.ENTITY_GENERIC_SWIM; // //CraftSound.getBukkit(this.getHandle().sound);
    }
	
	@Override
	public @NotNull EntityType getType() {
		return this.entityType;
	}

	@Override
    public boolean isUnderWater() {
        return this.getHandle().isUnderWater();
    }

	// @Override
	public boolean teleport(@NotNull Location arg0, @NotNull TeleportCause arg1, boolean arg2, boolean arg3) {
		// TODO Auto-generated method stub
		return this.teleport(arg0, arg1);
	}

	@Override
    public boolean wouldCollideUsing(@NotNull BoundingBox boundingBox) {
        AABB aabb = new AABB(boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getMinZ(), boundingBox.getMaxX(), boundingBox.getMaxY(), boundingBox.getMaxZ());
        return !this.getHandle().level().noCollision(this.getHandle(), aabb);
    }
	
	// 1.19.4:

	// @Override
    public boolean isSneaking() {
        return this.getHandle().isShiftKeyDown();
    }

	// @Override
    public void setSneaking(boolean sneak) {
        this.getHandle().setShiftKeyDown(sneak);
    }
    
    public static <T extends net.minecraft.world.entity.Entity> CraftEntity getEntity_new(CraftServer server, T entity) {
        Preconditions.checkArgument(entity != null, "Unknown entity");
        /*
        if (entity instanceof PlayerEntity && !(entity instanceof ServerPlayerEntity)) {
           return new CraftHumanEntity(server, (PlayerEntity)entity);
        } else if (entity instanceof EnderDragonPart complexPart) {
           return (CraftEntity)(complexPart.owner instanceof EnderDragonEntity
              ? new CraftEnderDragonPart(server, complexPart)
              : new CraftComplexPart(server, complexPart));
        } else {
       	*/
           CraftEntityTypes.EntityTypeData<?, T> entityTypeData = CraftEntityTypes.getEntityTypeData(CraftEntityType.minecraftToBukkit(entity.getType()));
           if (entityTypeData != null) {
              return (CraftEntity)entityTypeData.convertFunction().apply(server, entity);
           } else {
              throw new AssertionError("Unknown entity " + (entity == null ? null : entity.getClass()));
           }
        // }
     }

    public static <T extends net.minecraft.world.entity.Entity> CraftEntity getEntity(CraftServer server, T entity) {
        Preconditions.checkArgument(entity != null, "Unknown entity");

        // Special case human, since bukkit use Player interface for ...
        if (entity instanceof net.minecraft.world.entity.player.Player && !(entity instanceof ServerPlayer)) {
            return new CraftHumanEntity(server, (net.minecraft.world.entity.player.Player) entity);
        }

        // Special case complex part, since there is no extra entity type for them
        if (entity instanceof EnderDragonPart complexPart) {
            if (complexPart.parentMob instanceof EnderDragon) {
                //return new CraftEnderDragonPart(server, complexPart);
            //} else { // TODO
                //return new CraftComplexPart(server, complexPart);
            }
        }

        CraftEntityTypes.EntityTypeData<?, T> entityTypeData = CraftEntityTypes.getEntityTypeData(CraftEntityType.minecraftToBukkit(entity.getType()));

        if (entityTypeData != null) {
            return (CraftEntity) entityTypeData.convertFunction().apply(server, entity);
        }

        return (entity instanceof net.minecraft.world.entity.LivingEntity) ? new CraftLivingEntity(entity) : new UnknownEntity(entity); // TODO
        //throw new AssertionError("Unknown entity " + (entity == null ? null : entity.getClass()));
    }

    @Override
    public boolean teleport(Location location, TeleportCause cause, TeleportFlag... flags) {
        Preconditions.checkArgument(location != null, "location cannot be null");
        Preconditions.checkArgument(location.getWorld() != null, "Target world cannot be null");
        //Preconditions.checkState(!this.entity.generation, "Cannot teleport entity to an other world during world generation");
        location.checkFinite();

        return this.teleport0(location, cause, flags);
    }

    protected boolean teleport0(Location location, TeleportCause cause, TeleportFlag... flags) {
        net.minecraft.world.entity.Entity entity = this.getHandle();
        if (!entity.isAlive() || !((EntityBridge)entity).isValidBF()) {
            return false;
        }

        final Set<net.minecraft.world.entity.Relative> relativeFlags = EnumSet.noneOf(net.minecraft.world.entity.Relative.class);
        for (final TeleportFlag flag : flags) {
            if (flag instanceof TeleportFlag.Relative relativeFlag) {
                relativeFlags.add(deltaRelativeToNMS(relativeFlag));
            }
        }

        return this.entity.teleport(new TeleportTransition(
                ((CraftWorld) location.getWorld()).getHandle(),
                CraftLocation.toVec3(location),
                Vec3.ZERO,
                location.getYaw(),
                location.getPitch(),
                relativeFlags,
                TeleportTransition.DO_NOTHING//,
                //cause
        )) != null;
    }

    public static net.minecraft.world.entity.Relative deltaRelativeToNMS(TeleportFlag.Relative apiFlag) {
        return switch (apiFlag) {
            case VELOCITY_X -> net.minecraft.world.entity.Relative.DELTA_X;
            case VELOCITY_Y -> net.minecraft.world.entity.Relative.DELTA_Y;
            case VELOCITY_Z -> net.minecraft.world.entity.Relative.DELTA_Z;
            case VELOCITY_ROTATION -> net.minecraft.world.entity.Relative.ROTATE_DELTA;
        };
    }

    @Override
	public boolean isVisibleByDefault() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void setVisibleByDefault(boolean arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setPose(@NotNull Pose pose, boolean fixed) {
		// TODO Auto-generated method stub
		
	}
	
    public void setPose0(net.minecraft.world.entity.Pose pose, boolean fixed) {
        final net.minecraft.world.entity.Entity handle = this.getHandle();
        // handle.fixedPose = false;
        handle.setPose(pose);
        // handle.fixedPose = fixed;
    }

	@Override
	public boolean hasFixedPose() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getX() {
		return entity.getX();
	}

	@Override
	public double getY() {
		return entity.getY();
	}

	@Override
	public double getZ() {
		return entity.getZ();
	}

	@Override
	public float getPitch() {
		return entity.xRot;
	}

	@Override
	public float getYaw() {
		return entity.yRot;
	}

	@Override
	public @NotNull EntityScheduler getScheduler() {
		// Folia API
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull String getScoreboardEntryName() {
		// TODO Auto-generated method stub
		return this.getHandle().getScoreboardName();
	}
	
	// 1.20.2 API:

	@Override
	public @NotNull Set<Player> getTrackedBy() {
        // Preconditions.checkState((!this.entity.generation ? 1 : 0) != 0, (Object)"Cannot get tracking players during world generation");
        ImmutableSet.Builder<Player> players = ImmutableSet.builder();
        ServerLevel world = ((CraftWorld)this.getWorld()).getHandle();
        ChunkMap.TrackedEntity entityTracker = (ChunkMap.TrackedEntity)world.getChunkSource().chunkMap.entityMap.get(this.getEntityId());
        if (entityTracker != null) {
            for (ServerPlayerConnection connection : entityTracker.seenBy) {
                players.add(
                		(Player)( (EntityBridge)  connection.getPlayer()).getBukkitEntity()
                	);
            }
        }
        return players.build();
	}

	// 1.20.3 API:

    public EntitySnapshot createSnapshot() {
        return CraftEntitySnapshot.create(this);
    }
	
    public Entity copy() {
        net.minecraft.world.entity.Entity copy = this.copy(this.getHandle().level());
        Preconditions.checkArgument((copy != null ? 1 : 0) != 0, (Object)"Error creating new entity.");
        return ((EntityBridge)copy).getBukkitEntity();
    }

    public Entity copy(Location location) {
        Preconditions.checkArgument((location.getWorld() != null ? 1 : 0) != 0, (Object)"Location has no world");
        net.minecraft.world.entity.Entity copy = this.copy(((CraftWorld)location.getWorld()).getHandle());
        Preconditions.checkArgument((copy != null ? 1 : 0) != 0, (Object)"Error creating new entity.");
        copy.setPos(location.getX(), location.getY(), location.getZ());
        return ((CraftWorld)location.getWorld()).addEntity( (Entity)((EntityBridge)copy).getBukkitEntity() );
    }

    private net.minecraft.world.entity.Entity copy(net.minecraft.world.level.Level level) {
    	net.minecraft.world.entity.Entity var4;
        try (ProblemReporter.ScopedCollector problemReporter = new ProblemReporter.ScopedCollector(() -> "Entity#copy", LOGGER)) {
           TagValueOutput output = TagValueOutput.createWithContext(problemReporter, level.registryAccess());
           this.getHandle().saveAsPassenger(output);
           var4 = net.minecraft.world.entity.EntityType.loadEntityRecursive(output.buildResult(), level, net.minecraft.world.entity.EntitySpawnReason.LOAD, EntityProcessor.NOP);
        }

        return var4;
     }
    
    /*
    private net.minecraft.entity.Entity copy(net.minecraft.world.World level) {
        NbtCompound compoundTag = new NbtCompound();
        this.getHandle().saveAsPassenger(compoundTag, false);
        return net.minecraft.entity.EntityType.loadEntityWithPassengers(compoundTag, level, net.minecraft.entity.SpawnReason.LOAD, java.util.function.Function.identity());
    }
    */


	@Override
    public boolean isInWorld() {
        return ((EntityBridge)this.getHandle()).cb$getInWorld();
    }
	
	// 1.20.4 API:

	@Override
	public @NotNull CompletableFuture<Boolean> teleportAsync(@NotNull Location loc, @NotNull TeleportCause cause,
			@NotNull TeleportFlag @NotNull... teleportFlags) {
		loc.checkFinite();
        Location locationClone = loc.clone();
        ServerLevel world = ((CraftWorld)locationClone.getWorld()).getHandle();
        CompletableFuture<Boolean> ret = new CompletableFuture<Boolean>();
        
        AABB box = this.getHandle().getDimensions(this.getHandle().getPose())
        		.makeBoundingBox(locationClone.getX(), locationClone.getY(), locationClone.getZ());
        
        boolean tp = this.teleport(loc, cause);
        CompletableFuture<Boolean> b = CompletableFuture.completedFuture( tp );
        return b;
        
        // TODO Async
        
        /*
        
        world.loadChunksForMoveAsync(box, this instanceof CraftPlayer ? PrioritisedExecutor.Priority.HIGHER : PrioritisedExecutor.Priority.NORMAL, list -> {
            ServerChunkManager chunkProviderServer = world.getChunkManager();
            for (Chunk chunk : list) {
                chunkProviderServer.addTicketAtLevel(ChunkTicketType.POST_TELEPORT, chunk.getPos(), 33, this.getEntityId());
            }
            MinecraftServer.getServer().scheduleOnMain(() -> {
                try {
                    ret.complete(this.teleport(locationClone, cause, teleportFlags) ? Boolean.TRUE : Boolean.FALSE);
                }
                catch (Throwable throwable) {
                    if (throwable instanceof ThreadDeath) {
                        throw (ThreadDeath)throwable;
                    }
                    MinecraftServer.LOGGER.error("Failed to teleport entity " + String.valueOf(this), throwable);
                    ret.completeExceptionally(throwable);
                }
            });
        });
        return ret;
        */
	}

	@Override
	public void setInvisible(boolean invisible) {
        //this.getHandle().persistentInvisibility = invisible;
        this.getHandle().setSharedFlag(5, invisible);
        
        this.getHandle().setInvisible(invisible);       
	}

	@Override
	public boolean isInvisible() {
		return this.getHandle().isInvisible();
	}

	@Override
	public void setNoPhysics(boolean noPhysics) {
		this.getHandle().noPhysics = noPhysics;
	}

	@Override
	public boolean hasNoPhysics() {
		return this.getHandle().noPhysics;
	}
	
	// 1.20.6 API:

	@Override
	public String getAsString() {
        try (ProblemReporter.ScopedCollector problemReporter = new ProblemReporter.ScopedCollector(() -> "Entity#toString", LOGGER);){
            TagValueOutput output = TagValueOutput.createWithContext(problemReporter, this.getHandle().registryAccess());
            // .saveAsPassenger(output, false, true, true)
            if (!this.getHandle().saveAsPassenger(output)) {
                String string = null;
                return string;
            }
            String string = output.buildResult().toString();
            return string;
        }
    }

	// 1.21
    public void broadcastHurtAnimation(Collection<Player> players) {
        Preconditions.checkArgument((!players.contains(this) ? 1 : 0) != 0, (Object)"Cannot broadcast hurt animation to self without a yaw");
        for (Player player : players) {
            ((CraftPlayer)player).sendHurtAnimation(0.0f, this);
        }
    }
    
    // 1.21.4: (moved from Player)

	@Override
	public void lookAt(double x, double y, double z, @NotNull LookAnchor entityAnchor) {
		this.getHandle().lookAt(toNmsAnchor(entityAnchor), new Vec3(x, y, z));
	}
	
	public static EntityAnchorArgument.Anchor toNmsAnchor(LookAnchor nmsAnchor) {
		return switch (nmsAnchor) {
		case EYES -> EntityAnchorArgument.Anchor.EYES;
		case FEET -> EntityAnchorArgument.Anchor.FEET;
		default -> throw new MatchException(null, null);
		};
	}
	
	// 1.21.6:

	@Override
	public <T> T getData(@NotNull DataComponentType.Valued<T> type) {
        return this.entity.get(PaperDataComponentType.bukkitToMinecraft(type));
    }

	@Override
	@Nullable
	public <T> T getDataOrDefault(@NotNull DataComponentType.Valued<? extends T> type, @Nullable T fallback) {
        return this.entity.getOrDefault(PaperDataComponentType.bukkitToMinecraft(type), fallback);
    }

	@Override
	public boolean hasData(DataComponentType type) {
		return this.entity.get(PaperDataComponentType.bukkitToMinecraft(type)) != null;
	}

	@Override
	public void setVisualFire(@NotNull TriState fire) {
		// TODO Auto-generated method stub
		// this.getHandle().visualFire = fire;
	}

	@Override
	public @NotNull TriState getVisualFire() {
		// TODO Auto-generated method stub
		return TriState.NOT_SET;
	}

	@Override
	public @NotNull ItemStack getPickItemStack() {
		net.minecraft.world.item.ItemStack stack = this.getHandle().getPickResult();
        return stack == null ? ItemStack.empty() : ((ItemStackBridge) stack).cardboard$asBukkitCopy();
	}

	@Override
	public boolean isTrackedBy(@NotNull Player player) {
		ServerLevel world = ((CraftWorld)this.getWorld()).getHandle();
		ChunkMap.TrackedEntity entityTracker = (ChunkMap.TrackedEntity)world.getChunkSource().chunkMap.entityMap.get(this.getEntityId());
		if (entityTracker == null) {
			return false;
		}
		return entityTracker.seenBy.contains(((CraftPlayer)player).getHandle().connection);
	}

	public void setHandle(net.minecraft.world.entity.Entity entity) {
		this.entity = entity;
	}

}
