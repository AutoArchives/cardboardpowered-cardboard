package org.bukkit.craftbukkit.entity;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import org.cardboardpowered.interfaces.IItemStack;
import org.cardboardpowered.interfaces.IMixinCommandOutput;
import org.cardboardpowered.interfaces.IMixinEntity;
import org.cardboardpowered.interfaces.IMixinServerEntityPlayer;
import org.cardboardpowered.interfaces.IMixinWorld;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.logging.LogUtils;

import ca.spottedleaf.concurrentutil.executor.standard.PrioritisedExecutor;
import me.isaiah.common.entity.IRemoveReason;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.animal.cow.MushroomCow;
import net.minecraft.world.entity.animal.dolphin.Dolphin;
import net.minecraft.world.entity.animal.equine.AbstractChestedHorse;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.equine.Donkey;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.animal.equine.Llama;
import net.minecraft.world.entity.animal.equine.Mule;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.Ocelot;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.entity.animal.fish.Cod;
import net.minecraft.world.entity.animal.fish.Pufferfish;
import net.minecraft.world.entity.animal.fish.Salmon;
import net.minecraft.world.entity.animal.fish.TropicalFish;
import net.minecraft.world.entity.animal.fish.WaterAnimal;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.animal.panda.Panda;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.entity.animal.squid.Squid;
import net.minecraft.world.entity.animal.turtle.Turtle;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import net.minecraft.world.entity.monster.illager.Evoker;
import net.minecraft.world.entity.monster.illager.Illusioner;
import net.minecraft.world.entity.monster.illager.Pillager;
import net.minecraft.world.entity.monster.illager.SpellcasterIllager;
import net.minecraft.world.entity.monster.illager.Vindicator;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.entity.monster.skeleton.Stray;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;
import net.minecraft.world.entity.monster.spider.CaveSpider;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.monster.zombie.Drowned;
import net.minecraft.world.entity.monster.zombie.Husk;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.AbstractThrownPotion;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEgg;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownExperienceBottle;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartChest;
import net.minecraft.world.entity.vehicle.minecart.MinecartFurnace;
import net.minecraft.world.entity.vehicle.minecart.MinecartTNT;
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
import org.bukkit.craftbukkit.CraftSound;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataContainer;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry;
import org.bukkit.craftbukkit.util.CraftLocation;
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
import org.cardboardpowered.interfaces.IWorldChunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.cardboardpowered.CardboardMod;
import org.cardboardpowered.interfaces.IMixinCommandOutput;
import org.cardboardpowered.interfaces.IMixinEntity;
import org.cardboardpowered.interfaces.IMixinWorld;

import org.cardboardpowered.CardboardConfig;
import org.cardboardpowered.adventure.CardboardAdventure;
import org.cardboardpowered.impl.entity.CraftAbstractVillager;
import org.cardboardpowered.impl.entity.CraftAnimals;
import org.cardboardpowered.impl.entity.CraftArmorStand;
import org.cardboardpowered.impl.entity.CardboardAmbient;
import org.cardboardpowered.impl.entity.CardboardBat;
import org.cardboardpowered.impl.entity.CardboardBlaze;
import org.cardboardpowered.impl.entity.CardboardCat;
import org.cardboardpowered.impl.entity.CardboardCaveSpider;
import org.cardboardpowered.impl.entity.CardboardComplexPart;
import org.cardboardpowered.impl.entity.CardboardDolphin;
import org.cardboardpowered.impl.entity.CardboardDonkey;
import org.cardboardpowered.impl.entity.CardboardDragonPart;
import org.cardboardpowered.impl.entity.CardboardDrowned;
import org.cardboardpowered.impl.entity.CardboardEnderPearl;
import org.cardboardpowered.impl.entity.CardboardEnderdragon;
import org.cardboardpowered.impl.entity.CardboardEvoker;
import org.cardboardpowered.impl.entity.CardboardFirework;
import org.cardboardpowered.impl.entity.CardboardFish;
import org.cardboardpowered.impl.entity.CardboardFishCod;
import org.cardboardpowered.impl.entity.CardboardFishHook;
import org.cardboardpowered.impl.entity.CardboardFishPufferfish;
import org.cardboardpowered.impl.entity.CardboardFishSalmon;
import org.cardboardpowered.impl.entity.CardboardFishTropical;
import org.cardboardpowered.impl.entity.CardboardFlying;
import org.cardboardpowered.impl.entity.CardboardGhast;
import org.cardboardpowered.impl.entity.CardboardGiant;
import org.cardboardpowered.impl.entity.CardboardGuardian;
import org.cardboardpowered.impl.entity.CardboardGuardianElder;
import org.cardboardpowered.impl.entity.CardboardHanging;
import org.cardboardpowered.impl.entity.CardboardHorse;
import org.cardboardpowered.impl.entity.CardboardHusk;
import org.cardboardpowered.impl.entity.CardboardIllusioner;
import org.cardboardpowered.impl.entity.CardboardIronGolem;
import org.cardboardpowered.impl.entity.CardboardLlama;
import org.cardboardpowered.impl.entity.CardboardLlamaSpit;
import org.cardboardpowered.impl.entity.CardboardMagmaCube;
import org.cardboardpowered.impl.entity.CardboardMinecart;
import org.cardboardpowered.impl.entity.CardboardMinecartChest;
import org.cardboardpowered.impl.entity.CardboardMinecartFurnace;
import org.cardboardpowered.impl.entity.CardboardMinecartRideable;
import org.cardboardpowered.impl.entity.CardboardMule;
import org.cardboardpowered.impl.entity.CardboardPanda;
import org.cardboardpowered.impl.entity.CardboardPhantom;
import org.cardboardpowered.impl.entity.CardboardPig;
import org.cardboardpowered.impl.entity.CardboardPigZombie;
import org.cardboardpowered.impl.entity.CardboardPillager;
import org.cardboardpowered.impl.entity.CardboardShulker;
import org.cardboardpowered.impl.entity.CardboardSilverfish;
import org.cardboardpowered.impl.entity.CardboardSnowman;
import org.cardboardpowered.impl.entity.CardboardSpellcaster;
import org.cardboardpowered.impl.entity.CardboardSquid;
import org.cardboardpowered.impl.entity.CardboardThrownExpBottle;
import org.cardboardpowered.impl.entity.CardboardThrownPotion;
import org.cardboardpowered.impl.entity.CardboardTntCart;
import org.cardboardpowered.impl.entity.CardboardVex;
import org.cardboardpowered.impl.entity.CardboardVindicator;
import org.cardboardpowered.impl.entity.CardboardWaterMob;
import org.cardboardpowered.impl.entity.CardboardWitch;
import org.cardboardpowered.impl.entity.CardboardWither;
import org.cardboardpowered.impl.entity.CraftParrot;
import org.cardboardpowered.impl.entity.CraftCreature;
import org.cardboardpowered.impl.entity.CreeperImpl;
import org.cardboardpowered.impl.entity.CardboardEgg;
import org.cardboardpowered.impl.entity.ExperienceOrbImpl;
import org.cardboardpowered.impl.entity.CraftFallingBlock;
import org.cardboardpowered.impl.entity.ItemEntityImpl;
import org.cardboardpowered.impl.entity.LightningStrikeImpl;
import org.cardboardpowered.impl.entity.LivingEntityImpl;
import org.cardboardpowered.impl.entity.CraftMonster;
import org.cardboardpowered.impl.entity.CraftPlayer;
import org.cardboardpowered.impl.entity.CraftSlime;
import org.cardboardpowered.impl.entity.StrayImpl;
import org.cardboardpowered.impl.entity.TridentImpl;
import org.cardboardpowered.impl.entity.UnknownEntity;
import org.cardboardpowered.impl.entity.CraftVillager;
import org.cardboardpowered.impl.entity.WanderingTraderImpl;
import org.cardboardpowered.impl.entity.WitherSkeletonImpl;
import org.cardboardpowered.impl.world.CraftWorld;
import org.cardboardpowered.interfaces.IWorldChunk;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentType.Valued;
import io.papermc.paper.datacomponent.PaperDataComponentType;
import io.papermc.paper.entity.LookAnchor;
import io.papermc.paper.entity.TeleportFlag;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import me.isaiah.common.entity.IEntity;
import me.isaiah.common.entity.IRemoveReason;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public class CraftEntity implements Entity, CommandSender, IMixinCommandOutput {

	
	private static final Logger LOGGER = LogUtils.getLogger();
	
    protected static PermissibleBase perm;
    private static final CraftPersistentDataTypeRegistry DATA_TYPE_REGISTRY = new CraftPersistentDataTypeRegistry();

    public net.minecraft.world.entity.Entity nms;
    private final CraftPersistentDataContainer persistentDataContainer = new CraftPersistentDataContainer(DATA_TYPE_REGISTRY);

    protected final CraftServer server = CraftServer.INSTANCE;
    
    private final EntityType entityType;

    public CraftEntity(net.minecraft.world.entity.Entity entity) {
        this.nms = entity;
        this.entityType = CraftEntityType.minecraftToBukkit(entity.getType());
    }

    public net.minecraft.world.entity.Entity getHandle() {
        return nms;
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
        return nms.getName().getString();
    }

    @Override
    public void sendMessage(String message) {
    	
    	me.isaiah.common.cmixin.IMixinEntity e = (me.isaiah.common.cmixin.IMixinEntity) nms;
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
        return nms.getCustomName().getString();
    }

    @Override
    public void setCustomName(String name) {
        nms.setCustomName(ComponentUtils.fromMessage(new LiteralMessage(name)));
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
        return nms.addTag(arg0);
    }

    @Override
    public boolean eject() {
        if (isEmpty()) return false;
        nms.ejectPassengers();
        return true;
    }

    @Override
    public BoundingBox getBoundingBox() {
        AABB b = nms.getBoundingBox();
        return new BoundingBox(b.minX, b.minY, b.minZ, b.maxX, b.maxY, b.maxZ);
    }

    @Override
    public int getEntityId() {
        return nms.getId();
    }

    @Override
    public BlockFace getFacing() {
        return CraftBlock.notchToBlockFace(nms.getMotionDirection());
    }

    @Override
    public float getFallDistance() {
        return (float) nms.fallDistance;
    }

    @Override
    public int getFireTicks() {
        return nms.remainingFireTicks;
    }

    @Override
    public double getHeight() {
        return nms.getBbHeight();
    }

    @Override
    public EntityDamageEvent getLastDamageCause() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Location getLocation() {
        return new Location(getWorld(), nms.getX(), nms.getY(), nms.getZ(), nms.yRot, nms.xRot);
    }

    @Override
    public Location getLocation(Location loc) {
        if (loc != null) {
            loc.setWorld(getWorld());
            loc.setX(nms.getX());
            loc.setY(nms.getY());
            loc.setZ(nms.getZ());
            loc.setYaw(nms.yRot);
            loc.setPitch(nms.xRot);
        }
        return loc;
    }

    @Override
    public int getMaxFireTicks() {
        return nms.getFireImmuneTicks();
    }

    @Override
    public List<org.bukkit.entity.Entity> getNearbyEntities(double x, double y, double z) {
        List<net.minecraft.world.entity.Entity> notchEntityList = nms.level().getEntities(nms, nms.getBoundingBox().inflate(x, y, z), null);
        List<org.bukkit.entity.Entity> bukkitEntityList = new java.util.ArrayList<org.bukkit.entity.Entity>(notchEntityList.size());

        for (net.minecraft.world.entity.Entity e : notchEntityList)
            bukkitEntityList.add(((IMixinEntity)e).getBukkitEntity());
        return bukkitEntityList;
    }

    @Override
    public Entity getPassenger() {
        return isEmpty() ? null : ((IMixinEntity)getHandle().getFirstPassenger()).getBukkitEntity();
    }

    @Override
    public List<Entity> getPassengers() {
        return Lists.newArrayList(Lists.transform(getHandle().getPassengers(), new Function<net.minecraft.world.entity.Entity, org.bukkit.entity.Entity>() {
            @Override
            public org.bukkit.entity.Entity apply(net.minecraft.world.entity.Entity input) {
                return ((IMixinEntity)input).getBukkitEntity();
            }
        }));
    }

    @SuppressWarnings("deprecation")
    @Override
    public PistonMoveReaction getPistonMoveReaction() {
        return PistonMoveReaction.getById(nms.getPistonPushReaction().ordinal());
    }

    @Override
    public int getPortalCooldown() {
        return nms.getDimensionChangingDelay();
    }

    @Override
    public Pose getPose() {
        return Pose.values()[nms.getPose().ordinal()];
    }

    @Override
    public Set<String> getScoreboardTags() {
        return nms.getTags();
    }

    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public int getTicksLived() {
        return nms.tickCount;
    }

    @Override
    public UUID getUniqueId() {
        return nms.getUUID();
    }

    @Override
    public Entity getVehicle() {
        if (!isInsideVehicle())
            return null;
        return ((IMixinEntity)nms.getVehicle()).getBukkitEntity();
    }

    @Override
    public Vector getVelocity() {
        Vec3 vec3d = nms.getDeltaMovement();
        return new Vector(vec3d.x, vec3d.y, vec3d.z);
    }

    @Override
    public double getWidth() {
        return nms.getBbWidth();
    }

    @Override
    public World getWorld() {
        return ((IMixinWorld)nms.level()).getCraftWorld();
    }

    @Override
    public boolean hasGravity() {
        return !nms.isNoGravity();
    }

    @Override
    public boolean isCustomNameVisible() {
        return nms.isCustomNameVisible();
    }

    @Override
    public boolean isDead() {
        return !nms.isAlive();
    }

    @Override
    public boolean isEmpty() {
        return !nms.isVehicle();
    }

    @Override
    public boolean isGlowing() {
        return nms.isCurrentlyGlowing();
    }

    @Override
    public boolean isInsideVehicle() {
        return nms.isPassenger();
    }

    @Override
    public boolean isInvulnerable() {
        return nms.isInvulnerable();
    }

    @Override
    public boolean isOnGround() {
        if (nms instanceof Projectile)
            return ((Projectile) nms).onGround();

        return nms.onGround();
    }

    @Override
    public boolean isPersistent() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isSilent() {
        return nms.isSilent();
    }

    @Override
    public boolean isValid() {
        return nms.isAlive();
    }

    @Override
    public boolean leaveVehicle() {
        if (!isInsideVehicle())
            return false;
        nms.stopRiding();
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
        me.isaiah.common.cmixin.IMixinEntity common = (me.isaiah.common.cmixin.IMixinEntity)this.nms;
        common.Iremove(IRemoveReason.DISCARDED);
    }

    @Override
    public boolean removePassenger(Entity passenger) {
        ((CraftEntity) passenger).getHandle().stopRiding();
        return true;
    }

    @Override
    public boolean removeScoreboardTag(String arg0) {
        return nms.removeTag(arg0);
    }

    @Override
    public void setCustomNameVisible(boolean arg0) {
        nms.setCustomNameVisible(arg0);
    }

    @Override
    public void setFallDistance(float arg0) {
        nms.fallDistance = arg0;
    }

    @Override
    public void setFireTicks(int arg0) {
        nms.setRemainingFireTicks(arg0);
    }

    @Override
    public void setGlowing(boolean arg0) {
        nms.setGlowingTag(arg0);
    }

    @Override
    public void setGravity(boolean arg0) {
        nms.setNoGravity(!arg0);
    }

    @Override
    public void setInvulnerable(boolean arg0) {
        nms.setInvulnerable(arg0);
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
        nms.setPortalCooldown(arg0);
    }

    @Override
    public void setRotation(float yaw, float pitch) {
        yaw = Location.normalizeYaw((float)yaw);
        pitch = Location.normalizePitch((float)pitch);
        this.nms.setYRot(yaw);
        this.nms.setXRot(pitch);
        this.nms.yRotO = yaw;
        this.nms.xRotO = pitch;
        this.nms.setYHeadRot(yaw);
    }

    @Override
    public void setSilent(boolean arg0) {
        nms.setSilent(arg0);
    }

    @Override
    public void setTicksLived(int arg0) {
        nms.tickCount = arg0;
    }

    @Override
    public void setVelocity(Vector vec) {
        nms.setDeltaMovement(new Vec3(vec.getX(), vec.getY(), vec.getZ()));
        nms.hurtMarked = true;
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

        if (nms.isVehicle() || !nms.isAlive())
            return false;

        nms.stopRiding();

        if(loc.getWorld() == null || loc.getWorld().equals(getWorld())) {
            nms.absSnapTo(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
            nms.setYHeadRot(loc.getYaw());
        } else {
            nms.teleportTo(
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
        Location origin = ((IMixinEntity)getHandle()).getOriginBF();
        return origin == null ? null : origin.clone();
    }

    public boolean isTicking() {
        return true; // TODO: 1.17ify: nms.getEntityWorld().getChunkManager().shouldTickEntity(nms);
    }

    public boolean isInLava() {
        return nms.isInLava();
    }

    public boolean isInWater() {
        return nms.isUnderWater();
    }

    public boolean isInRain() {
        return nms.isInRain();
    }

    @Override
    public Chunk getChunk() {
        IWorldChunk wc = (IWorldChunk) nms.level().getChunkAt(nms.blockPosition());
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
        return nms.isInWaterOrRain();
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
        return name != null ? CardboardAdventure.asAdventure(name) : null;
    }

	@Override
    public void customName(Component customName) {
        this.getHandle().setCustomName(customName != null ? CardboardAdventure.asVanilla(customName) : null);
    }

    @Override
    public int getFreezeTicks() {
        return nms.getTicksFrozen();
    }

    @Override
    public int getMaxFreezeTicks() {
        return nms.getTicksFrozen();
    }

    @Override
    public boolean isFrozen() {
        return nms.isFullyFrozen();
    }

    @Override
    public boolean isVisualFire() {
        return nms.displayFireAnimation();
    }

    @Override
    public void setFreezeTicks(int arg0) {
        nms.setTicksFrozen(arg0);
    }

    @Override
    public void setVisualFire(boolean arg0) {
        nms.setSharedFlagOnFire(arg0);
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
        ServerLevel world = (ServerLevel) nms.level();
        TrackedEntity entityTracker = world.getChunkSource()
                .chunkMap.entityMap
                .get(this.getEntityId());
        if (entityTracker != null) {

	        for(ServerPlayerConnection connection : entityTracker.seenBy) {
		        players.add((Player) ((IMixinServerEntityPlayer) connection.getPlayer()).getBukkitEntity());
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
        AABB aabb = ((IMixinEntity)this.getHandle()).cardboad_getBoundingBoxAt(location.getX(), location.getY(), location.getZ());
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

    public static CraftEntity getEntity(CraftServer server, net.minecraft.world.entity.Entity entity) {
        /*
         * Order is *EXTREMELY* important -- keep it right! =D
         */
        // CHECKSTYLE:OFF
        if (entity instanceof LivingEntity) {
            // Players
            if (entity instanceof net.minecraft.world.entity.player.Player) {
                if (entity instanceof ServerPlayer) { return new CraftPlayer((ServerPlayer) entity); }
                else { return new CraftHumanEntity((net.minecraft.world.entity.player.Player) entity); }
            }
            // Water Animals
            else if (entity instanceof WaterAnimal) {
                if (entity instanceof Squid) { return new CardboardSquid(server, (Squid) entity); }
                else if (entity instanceof AbstractFish) {
                    if (entity instanceof Cod) { return new CardboardFishCod(server, (Cod) entity); }
                    else if (entity instanceof Pufferfish) { return new CardboardFishPufferfish(server, (Pufferfish) entity); }
                    else if (entity instanceof Salmon) { return new CardboardFishSalmon(server, (Salmon) entity); }
                    else if (entity instanceof TropicalFish) { return new CardboardFishTropical(server, (TropicalFish) entity); }
                    else { return new CardboardFish(server, (AbstractFish) entity); }
                }
                else if (entity instanceof Dolphin) { return new CardboardDolphin(server, (Dolphin) entity); }
                else { return new CardboardWaterMob(server, (WaterAnimal) entity); }
            }
            else if (entity instanceof PathfinderMob) {
                // Animals
                if (entity instanceof Animal) {
                    if (entity instanceof Chicken) { return new CraftChicken(server, (Chicken) entity); }
                    else if (entity instanceof Cow) {
                        if (entity instanceof MushroomCow) { return new CraftMushroomCow(server, (MushroomCow) entity); }
                        else { return new CraftCow(server, (Cow) entity); }
                    }
                    else if (entity instanceof Pig) { return new CardboardPig(server, (Pig) entity); }
                    else if (entity instanceof TamableAnimal) {
                        if (entity instanceof Wolf) { return new CraftWolf(server, (Wolf) entity); }
                        else if (entity instanceof Cat) { return new CardboardCat(server, (Cat) entity); }
                        else if (entity instanceof Parrot) { return new CraftParrot(server, (Parrot) entity); }
                    }
                    //else if (entity instanceof SheepEntity) { return new CraftSheep(server, (SheepEntity) entity); }
                    else if (entity instanceof AbstractHorse) {
                        if (entity instanceof AbstractChestedHorse){
                            if (entity instanceof Donkey) { return new CardboardDonkey(server, (Donkey) entity); }
                            else if (entity instanceof Mule) { return new CardboardMule(server, (Mule) entity); }
                            //else if (entity instanceof TraderLlamaEntity) { return new CardboardTraderLlama(server, (TraderLlamaEntity) entity); }
                            else if (entity instanceof Llama) { return new CardboardLlama(server, (Llama) entity); }
                        } else if (entity instanceof Horse) { return new CardboardHorse(server, (Horse) entity); }
                        //else if (entity instanceof SkeletonHorseEntity) { return new CraftSkeletonHorse(server, (SkeletonHorseEntity) entity); }
                        //else if (entity instanceof ZombieHorseEntity) { return new CraftZombieHorse(server, (ZombieHorseEntity) entity); }
                    }
                    //else if (entity instanceof RabbitEntity) { return new CraftRabbit(server, (RabbitEntity) entity); }
                    else if (entity instanceof PolarBear) { return new CraftPolarBear(server, (PolarBear) entity); }
                    else if (entity instanceof Turtle) { return new CraftTurtle(server, (Turtle) entity); }
                    else if (entity instanceof Ocelot) { return new CraftOcelot(server, (Ocelot) entity); }
                    else if (entity instanceof Panda) { return new CardboardPanda(server, (Panda) entity); }
                    //else if (entity instanceof FoxEntity) { return new CraftFox(server, (FoxEntity) entity); }
                    //else if (entity instanceof BeeEntity) { return new CraftBee(server, (BeeEntity) entity); }
                    //else if (entity instanceof HoglinEntity) { return new CraftHoglin(server, (HoglinEntity) entity); }
                    //else if (entity instanceof StriderEntity) { return new CraftStrider(server, (StriderEntity) entity); }
                    else  { return new CraftAnimals(server, (Animal) entity); }
                }
                // Monsters
                else if (entity instanceof Monster) {
                    if (entity instanceof Zombie) {
                        if (entity instanceof ZombifiedPiglin) { return new CardboardPigZombie(server, (ZombifiedPiglin) entity); }
                        else if (entity instanceof Husk) { return new CardboardHusk(server, (Husk) entity); }
                        else if (entity instanceof ZombieVillager) { return new CraftVillagerZombie(server, (ZombieVillager) entity); }
                        else if (entity instanceof Drowned) { return new CardboardDrowned(server, (Drowned) entity); }
                        else { return new CraftZombie(server, (Zombie) entity); }
                    }
                    else if (entity instanceof Creeper) { return new CreeperImpl(server, (Creeper) entity); }
                    else if (entity instanceof EnderMan) { return new CraftEnderman(server, (EnderMan) entity); }
                    else if (entity instanceof Silverfish) { return new CardboardSilverfish(server, (Silverfish) entity); }
                    else if (entity instanceof Giant) { return new CardboardGiant(server, (Giant) entity); }
                    else if (entity instanceof AbstractSkeleton) {
                        if (entity instanceof Stray) { return new StrayImpl(server, (Stray) entity); }
                        else if (entity instanceof WitherSkeleton) { return new WitherSkeletonImpl(server, (WitherSkeleton) entity); }
                        else { return new CraftSkeleton(server, (AbstractSkeleton) entity); }
                    }
                    else if (entity instanceof Blaze) { return new CardboardBlaze(server, (Blaze) entity); }
                    else if (entity instanceof Witch) { return new CardboardWitch(server, (Witch) entity); }
                    else if (entity instanceof WitherBoss) { return new CardboardWither(server, (WitherBoss) entity); }
                    else if (entity instanceof Spider) {
                        if (entity instanceof CaveSpider) { return new CardboardCaveSpider(server, (CaveSpider) entity); }
                        else { return new CraftSpider(server, (Spider) entity); }
                    }
                    else if (entity instanceof Endermite) { return new CraftEndermite(server, (Endermite) entity); }
                    else if (entity instanceof Guardian) {
                        if (entity instanceof ElderGuardian) { return new CardboardGuardianElder(server, (ElderGuardian) entity); }
                        else { return new CardboardGuardian(server, (Guardian) entity); }
                    }
                    else if (entity instanceof Vex) { return new CardboardVex(server, (Vex) entity); }
                    else if (entity instanceof AbstractIllager) {
                        if (entity instanceof SpellcasterIllager) {;
                            if (entity instanceof Evoker) { return new CardboardEvoker(server, (Evoker) entity); }
                            else if (entity instanceof Illusioner) { return new CardboardIllusioner(server, (Illusioner) entity); }
                            else {  return new CardboardSpellcaster(server, (SpellcasterIllager) entity); }
                        }
                        else if (entity instanceof Vindicator) { return new CardboardVindicator(server, (Vindicator) entity); }
                        else if (entity instanceof Pillager) { return new CardboardPillager(server, (Pillager) entity); }
                        else { return new CraftIllager(server, (AbstractIllager) entity); }
                    }
                    //else if (entity instanceof RavagerEntity) { return new CraftRavager(server, (RavagerEntity) entity); }
                    //else if (entity instanceof AbstractPiglinEntity) {
                        //if (entity instanceof PiglinEntity) return new CraftPiglin(server, (PiglinEntity) entity);
                        //else if (entity instanceof PiglinBruteEntity) { return new CraftPiglinBrute(server, (PiglinBruteEntity) entity); }
                        //else { return new CraftPiglinAbstract(server, (AbstractPiglinEntity) entity); }
                    //}
                    //else if (entity instanceof ZoglinEntity) { return new CraftZoglin(server, (ZoglinEntity) entity); }

                    else  { return new CraftMonster(server, (Monster) entity); }
                }
                else if (entity instanceof AbstractGolem) {
                    if (entity instanceof SnowGolem) { return new CardboardSnowman(server, (SnowGolem) entity); }
                    else if (entity instanceof IronGolem) { return new CardboardIronGolem(server, (IronGolem) entity); }
                    else if (entity instanceof Shulker) { return new CardboardShulker(server, (Shulker) entity); }
                }
                else if (entity instanceof AbstractVillager) {
                    if (entity instanceof Villager) { return new CraftVillager(server, (Villager) entity); }
                    else if (entity instanceof WanderingTrader) { return new WanderingTraderImpl(server, (WanderingTrader) entity); }
                    else { 
                    	return getEntity_new(server, (AbstractVillager) entity);
                    	// return new CraftAbstractVillager(server, (MerchantEntity) entity);
                    }
                }
                else { return new CraftCreature(server, (PathfinderMob) entity); }
            }
            // Slimes are a special (and broken) case
            else if (entity instanceof Slime) {
                if (entity instanceof MagmaCube) { return new CardboardMagmaCube(server, (MagmaCube) entity); }
                else { return new CraftSlime(server, (Slime) entity); }
            }
            // Flying
            
            // TODO: check 1.21.8:
            //else if (entity instanceof FlyingEntity) {
                if (entity instanceof Ghast) { return new CardboardGhast(server, (Ghast) entity); }
                else if (entity instanceof Phantom) { return new CardboardPhantom(server, (Phantom) entity); }
                // else { return new CardboardFlying(server, (FlyingEntity) entity); }
            //}
            else if (entity instanceof EnderDragon) {
                return new CardboardEnderdragon(server, (EnderDragon) entity);
            }
            // Ambient
            else if (entity instanceof AmbientCreature) {
                if (entity instanceof Bat) { return new CardboardBat(server, (Bat) entity); }
                else { return new CardboardAmbient(server, (AmbientCreature) entity); }
            }
            else if (entity instanceof ArmorStand) { return new CraftArmorStand(server, (ArmorStand) entity); }
            else  { return new LivingEntityImpl(server, (LivingEntity) entity); }
        }
        else if (entity instanceof EnderDragonPart) {
            EnderDragonPart part = (EnderDragonPart) entity;
            if (part.parentMob instanceof EnderDragon) { return new CardboardDragonPart(server, (EnderDragonPart) entity); }
            else { return new CardboardComplexPart(server, (EnderDragonPart) entity); }
        }
        else if (entity instanceof ExperienceOrb) { return new ExperienceOrbImpl(server, (ExperienceOrb) entity); }
        //else if (entity instanceof ArrowEntity) { return new CraftTippedArrow(server, (ArrowEntity) entity); }
        //else if (entity instanceof SpectralArrowEntity) { return new CraftSpectralArrow(server, (SpectralArrowEntity) entity); }
        else if (entity instanceof AbstractArrow) {
            if (entity instanceof ThrownTrident) { return new TridentImpl(server, (ThrownTrident) entity); }
            else { return new CraftAbstractArrow(server, (AbstractArrow) entity); }
        }
        //else if (entity instanceof BoatEntity) { return new CraftBoat(server, (BoatEntity) entity); }
        else if (entity instanceof ThrowableProjectile) {
            if (entity instanceof ThrownEgg) { return new CardboardEgg(server, (ThrownEgg) entity); }
            else if (entity instanceof Snowball) { return new CraftSnowball(server, (Snowball) entity); }
            else if (entity instanceof AbstractThrownPotion) { return new CardboardThrownPotion(server, (AbstractThrownPotion) entity); }
            else if (entity instanceof ThrownEnderpearl) { return new CardboardEnderPearl(server, (ThrownEnderpearl) entity); }
            else if (entity instanceof ThrownExperienceBottle) { return new CardboardThrownExpBottle(server, (ThrownExperienceBottle) entity); }
            
        }
        else if (entity instanceof FallingBlockEntity) { return new CraftFallingBlock(server, (FallingBlockEntity) entity); }
        else if (entity instanceof AbstractHurtingProjectile) {
            //if (entity instanceof SmallFireballEntity) { return new CraftSmallFireball(server, (SmallFireballEntity) entity); }
            //else if (entity instanceof FireballEntity) { return new CraftLargeFireball(server, (FireballEntity) entity); }
           // else if (entity instanceof WitherSkullEntity) { return new CraftWitherSkull(server, (WitherSkullEntity) entity); }
           // else if (entity instanceof DragonFireballEntity) { return new CraftDragonFireball(server, (DragonFireballEntity) entity); }
            //else { return new CraftFireball(server, (ExplosiveProjectileEntity) entity); }
        }
        //else if (entity instanceof EyeOfEnderEntity) { return new CraftEnderSignal(server, (EyeOfEnderEntity) entity); }
        //else if (entity instanceof EndCrystalEntity) { return new CraftEnderCrystal(server, (EndCrystalEntity) entity); }
        else if (entity instanceof FishingHook) { return new CardboardFishHook(server, (FishingHook) entity); }
        else if (entity instanceof ItemEntity) { return new ItemEntityImpl(server, (ItemEntity) entity); }
        else if (entity instanceof LightningBolt) { return new LightningStrikeImpl(server, (LightningBolt) entity); }
        else if (entity instanceof AbstractMinecart) {
            if (entity instanceof MinecartFurnace) { return new CardboardMinecartFurnace(server, (MinecartFurnace) entity); }
            else if (entity instanceof MinecartChest) { return new CardboardMinecartChest(server, (MinecartChest) entity); }
            else if (entity instanceof MinecartTNT) { return new CardboardTntCart(server, (MinecartTNT) entity); }
            //else if (entity instanceof HopperMinecartEntity) { return new CraftMinecartHopper(server, (HopperMinecartEntity) entity); }
            //else if (entity instanceof SpawnerMinecartEntity) { return new CraftMinecartMobSpawner(server, (SpawnerMinecartEntity) entity); }
            else if (entity instanceof Minecart) { return new CardboardMinecartRideable(server, (Minecart) entity); }
            //else if (entity instanceof CommandBlockMinecartEntity) { return new CraftMinecartCommand(server, (CommandBlockMinecartEntity) entity); }*/
            else return new CardboardMinecart(server, (AbstractMinecart) entity);
        } else if (entity instanceof HangingEntity) {
            //if (entity instanceof PaintingEntity) { return new CraftPainting(server, (PaintingEntity) entity); }
            //else if (entity instanceof ItemFrameEntity) { return new CraftItemFrame(server, (ItemFrameEntity) entity); }
            //else if (entity instanceof LeashKnotEntity) { return new CraftLeash(server, (LeashKnotEntity) entity); }
            //else { return new CraftHanging(server, (AbstractDecorationEntity) entity); }
            return new CardboardHanging(server, (HangingEntity) entity);
        }
        else if (entity instanceof PrimedTnt) { return new CraftTNTPrimed(server, (PrimedTnt) entity); }
        else if (entity instanceof FireworkRocketEntity) {return new CardboardFirework(server, (FireworkRocketEntity) entity); }
        //else if (entity instanceof ShulkerBulletEntity) { return new CraftShulkerBullet(server, (ShulkerBulletEntity) entity); }
        //else if (entity instanceof AreaEffectCloudEntity) { return new CraftAreaEffectCloud(server, (AreaEffectCloudEntity) entity); }
        //else if (entity instanceof EvokerFangsEntity) { return new CraftEvokerFangs(server, (EvokerFangsEntity) entity); }
        else if (entity instanceof LlamaSpit) { return new CardboardLlamaSpit(server, (LlamaSpit) entity); }
        // CHECKSTYLE:ON

        
        return (entity instanceof net.minecraft.world.entity.LivingEntity) ? new LivingEntityImpl(entity) : new UnknownEntity(entity); // TODO
        //throw new AssertionError("Unknown entity " + (entity == null ? null : entity.getClass()));
    }

	// TODO 1.19.4
	@Override
    public boolean teleport(Location location, TeleportCause cause, TeleportFlag ... flags) {
        Preconditions.checkArgument((location != null ? 1 : 0) != 0, (Object)"location cannot be null");
        location.checkFinite();
        Set<TeleportFlag> flagSet = Set.of(flags);
        boolean dismount = !flagSet.contains(TeleportFlag.EntityState.RETAIN_VEHICLE);
        boolean ignorePassengers = flagSet.contains(TeleportFlag.EntityState.RETAIN_PASSENGERS);
        if (flagSet.contains(TeleportFlag.EntityState.RETAIN_PASSENGERS) && this.nms.isVehicle() && location.getWorld() != this.getWorld()) {
            return false;
        }
        if (!dismount && this.nms.isPassenger() && location.getWorld() != this.getWorld()) {
            return false;
        }
        if (!ignorePassengers && this.nms.isVehicle() || this.nms.isRemoved()) {
            return false;
        }
        if (dismount) {
            this.nms.stopRiding();
        }
        if (location.getWorld() != null && !location.getWorld().equals(this.getWorld())) {
            // Preconditions.checkState((!this.nms.generation ? 1 : 0) != 0, (Object)"Cannot teleport entity to an other world during world generation");
            // TODO
        	// this.nms.teleportTo(((CraftWorld)location.getWorld()).getHandle(), CraftLocation.toPosition(location));
            return true;
        }
        this.nms.snapTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.nms.setYHeadRot(location.getYaw());
        return true;
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
		return nms.getX();
	}

	@Override
	public double getY() {
		return nms.getY();
	}

	@Override
	public double getZ() {
		return nms.getZ();
	}

	@Override
	public float getPitch() {
		return nms.xRot;
	}

	@Override
	public float getYaw() {
		return nms.yRot;
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
                		(Player)( (IMixinEntity)  connection.getPlayer()).getBukkitEntity()
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
        return ((IMixinEntity)copy).getBukkitEntity();
    }

    public Entity copy(Location location) {
        Preconditions.checkArgument((location.getWorld() != null ? 1 : 0) != 0, (Object)"Location has no world");
        net.minecraft.world.entity.Entity copy = this.copy(((CraftWorld)location.getWorld()).getHandle());
        Preconditions.checkArgument((copy != null ? 1 : 0) != 0, (Object)"Error creating new entity.");
        copy.setPos(location.getX(), location.getY(), location.getZ());
        return ((CraftWorld)location.getWorld()).addEntity( (Entity)((IMixinEntity)copy).getBukkitEntity() );
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
        return ((IMixinEntity)this.getHandle()).cb$getInWorld();
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
        return this.nms.get(PaperDataComponentType.bukkitToMinecraft(type));
    }

	@Override
	@Nullable
	public <T> T getDataOrDefault(@NotNull DataComponentType.Valued<? extends T> type, @Nullable T fallback) {
        return this.nms.getOrDefault(PaperDataComponentType.bukkitToMinecraft(type), fallback);
    }

	@Override
	public boolean hasData(DataComponentType type) {
		return this.nms.get(PaperDataComponentType.bukkitToMinecraft(type)) != null;
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
        return stack == null ? ItemStack.empty() : ((IItemStack) stack).asBukkitCopy();
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
		this.nms = entity;
	}

}
