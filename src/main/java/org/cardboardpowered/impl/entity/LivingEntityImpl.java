package org.cardboardpowered.impl.entity;

//<<<<<<< HEAD
//=======
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.Chunk;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LingeringPotion;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.TippedArrow;
import org.bukkit.entity.Trident;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Consumer;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

//>>>>>>> upstream/ver/1.20
import com.destroystokyo.paper.block.TargetBlockInfo;
import com.destroystokyo.paper.block.TargetBlockInfo.FluidMode;
import com.destroystokyo.paper.entity.TargetEntityInfo;
import com.google.common.collect.Sets;
import com.javazilla.bukkitfabric.Utils;
import com.javazilla.bukkitfabric.interfaces.IMixinArrowEntity;
import com.javazilla.bukkitfabric.interfaces.IMixinEntity;
import com.javazilla.bukkitfabric.interfaces.IMixinLivingEntity;

import me.isaiah.common.ICommonMod;
import me.isaiah.common.cmixin.IMixinMinecraftServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractWindChargeEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.item.Items;
//<<<<<<< HEAD
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import org.apache.commons.lang.Validate;
import org.bukkit.Chunk;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.cardboardpowered.impl.CardboardPotionEffectType;
import org.cardboardpowered.impl.CardboardPotionUtil;
import org.cardboardpowered.impl.inventory.CardboardEntityEquipment;
import org.cardboardpowered.impl.world.WorldImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
//=======
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
//>>>>>>> upstream/ver/1.20

@SuppressWarnings({"deprecation", "removal"})
public class LivingEntityImpl extends CraftEntity implements LivingEntity {

    public net.minecraft.entity.LivingEntity nms;
    private CardboardEntityEquipment equipment;

    public LivingEntityImpl(net.minecraft.entity.Entity entity) {
        super(entity);
        this.nms = (net.minecraft.entity.LivingEntity) entity;
        if (entity instanceof MobEntity || entity instanceof ArmorStandEntity) {
            equipment = new CardboardEntityEquipment(this);
        }
    }

    public LivingEntityImpl(CraftServer server, net.minecraft.entity.Entity entity) {
        this(entity);
    }

    @Override
    public AttributeInstance getAttribute(Attribute att) {
        return ((IMixinLivingEntity) nms).cardboard_getAttr().getAttribute(att);
    }

    @Override
    public void damage(double arg0) {
        // nms.damage(DamageSource.MAGIC, (float)arg0);
    	damage(arg0, (Entity) null);
    }

    @Override
    public void damage(double arg0, Entity source) {
        // nms.damage(DamageSource.mob((net.minecraft.entity.LivingEntity) arg1), (float) arg0);
    	DamageSource reason = getHandle().getDamageSources().generic();

        if (source instanceof HumanEntity) {
            reason = getHandle().getDamageSources().playerAttack(((CraftHumanEntity) source).getHandle());
        } else if (source instanceof LivingEntity) {
            reason = getHandle().getDamageSources().mobAttack(((LivingEntityImpl) source).getHandle());
        }

        nms.damage(reason, (float) arg0);
    }

    @Override
    public double getAbsorptionAmount() {
        return nms.getAbsorptionAmount();
    }

    @Override
    public double getHealth() {
        return nms.getHealth();
    }

    @Override
    public double getMaxHealth() {
        // TODO Auto-generated method stub
        return nms.getMaxHealth();
    }

    @Override
    public void resetMaxHealth() {
        // TODO Auto-generated method stub
    }

    @Override
    public void setAbsorptionAmount(double arg0) {
        nms.setAbsorptionAmount((float)arg0);
    }

    @Override
    public void setHealth(double arg0) {
        nms.setHealth((float) arg0);
    }

    @Override
    public void setMaxHealth(double arg0) {
        // TODO Max health
        nms.setHealth((float) arg0);
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> arg0) {
        return launchProjectile(arg0, null);
    }

    @Override
    public net.minecraft.entity.LivingEntity getHandle() {
        return nms;
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> projectile, Vector velocity) {
    	return this.launchProjectile(projectile, velocity, null);
    }

    @Override
    public boolean addPotionEffect(PotionEffect effect) {
        return addPotionEffect(effect, false);
    }

    @Override
    public boolean addPotionEffect(PotionEffect effect, boolean force) {
        StatusEffect type = Registries.STATUS_EFFECT.get(effect.getType().getId());

        me.isaiah.common.cmixin.IMixinEntity ic = ((me.isaiah.common.cmixin.IMixinEntity)(Object)nms);
        ic.IC$add_status_effect(type, effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.hasParticles());
        
        // nms.addStatusEffect(new StatusEffectInstance(type, effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.hasParticles())/*, EntityPotionEffectEvent.Cause.PLUGIN*/);

        return true;
    }

    @Override
    public boolean addPotionEffects(Collection<PotionEffect> effects) {
        boolean success = true;
        for (PotionEffect effect : effects)
            success &= addPotionEffect(effect);
        return success;
    }

    @Override
    public void attack(Entity arg0) {
        nms.attackLivingEntity(((LivingEntityImpl)arg0).nms);
    }

    @Override
    public Collection<PotionEffect> getActivePotionEffects() {
        List<PotionEffect> effects = new ArrayList<>();
        for (StatusEffectInstance handle : nms.activeStatusEffects.values()) {
                // effects.add(new PotionEffect(PotionEffectType.getById(Registries.STATUS_EFFECT.getRawId(handle.getEffectType())), handle.getDuration(), handle.getAmplifier(), handle.isAmbient(), handle.shouldShowParticles()));
                effects.add(CardboardPotionUtil.toBukkit(handle));
        
        }
        return effects;
    }

    @Override
    public boolean getCanPickupItems() {
        if (getHandle() instanceof MobEntity) {
            return ((MobEntity) getHandle()).canPickUpLoot();
        }
        return true; // todo
    }

    @Override
    public EntityEquipment getEquipment() {
        return equipment;
    }

    @Override
    public double getEyeHeight() {
        return nms.getStandingEyeHeight();
    }

    @Override
    public double getEyeHeight(boolean arg0) {
        return getEyeHeight();
    }

    @Override
    public Location getEyeLocation() {
        Location loc = getLocation();
        loc.setY(loc.getY() + getEyeHeight());
        return loc;
    }

    @Override
    public Player getKiller() {
        return nms.attackingPlayer == null ? null : (Player) ((IMixinEntity)nms.attackingPlayer).getBukkitEntity();
    }

    @Override
    public double getLastDamage() {
        return nms.lastDamageTaken;
    }

    @Override
    public List<Block> getLastTwoTargetBlocks(Set<Material> arg0, int arg1) {
        return getLineOfSight(arg0, arg1, 2);
    }

    @Override
    public Entity getLeashHolder() throws IllegalStateException {
        return ((IMixinEntity)((MobEntity) nms).getLeashHolder()).getBukkitEntity();
    }

    private List<Block> getLineOfSight(Set<Material> transparent, int maxDistance, int maxLength) {
        if (transparent == null)
            transparent = Sets.newHashSet(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR);

        if (maxDistance > 120)
            maxDistance = 120;
        ArrayList<Block> blocks = new ArrayList<Block>();
        Iterator<Block> itr = new BlockIterator(this, maxDistance);
        while (itr.hasNext()) {
            Block block = itr.next();
            blocks.add(block);
            if (maxLength != 0 && blocks.size() > maxLength)
                blocks.remove(0);
            Material material = block.getType();
            if (!transparent.contains(material))
                break;
        }
        return blocks;
    }

    @Override
    public List<Block> getLineOfSight(Set<Material> transparent, int maxDistance) {
        return getLineOfSight(transparent, maxDistance, 0);
    }

    @Override
    public int getMaximumAir() {
        return nms.getMaxAir();
    }

    @Override
    public int getMaximumNoDamageTicks() {
        return nms.defaultMaxHealth;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getMemory(MemoryKey<T> arg0) {
        return (T) nms.getBrain().getOptionalMemory(Utils.fromMemoryKey(arg0)).map(Utils::fromNmsGlobalPos).orElse(null);
    }

    @Override
    public int getNoDamageTicks() {
        return nms.timeUntilRegen;
    }

    @Override
    public PotionEffect getPotionEffect(PotionEffectType arg0) {
    	
    	me.isaiah.common.cmixin.IMixinEntity ic = ((me.isaiah.common.cmixin.IMixinEntity)(Object)nms);

    	StatusEffectInstance handle = ic.IC$get_status_effect(arg0.getId());
    	
    	int typeId = ic.IC$get_status_effect_id(handle);
        return (handle == null) ? null : new PotionEffect(PotionEffectType.getById(typeId), handle.getDuration(), handle.getAmplifier(), handle.isAmbient(), handle.shouldShowParticles());
    }

    @Override
    public int getRemainingAir() {
        return nms.getAir();
    }

    @Override
    public boolean getRemoveWhenFarAway() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Block getTargetBlock(Set<Material> arg0, int arg1) {
        List<Block> blocks = getLineOfSight(arg0, arg1, 1);
        return blocks.get(0);
    }

    @Override
    public Block getTargetBlockExact(int maxDistance) {
        return this.getTargetBlockExact(maxDistance, FluidCollisionMode.NEVER);
    }

    @Override
    public Block getTargetBlockExact(int maxDistance, FluidCollisionMode fluidCollisionMode) {
        RayTraceResult hitResult = this.rayTraceBlocks(maxDistance, fluidCollisionMode);
        return (hitResult != null ? hitResult.getHitBlock() : null);
    }

    @Override
    public boolean hasAI() {
        return (this.getHandle() instanceof MobEntity) ? !((MobEntity) this.getHandle()).isAiDisabled() : false;
    }

    @Override
    public boolean hasLineOfSight(Entity arg0) {
        return nms.canSee(((CraftEntity)arg0).nms);
    }

    @Override
    public boolean hasPotionEffect(PotionEffectType arg0) {
    	me.isaiah.common.cmixin.IMixinEntity ic = ((me.isaiah.common.cmixin.IMixinEntity)(Object)nms);
    	return ic.IC$has_status_effect(Registries.STATUS_EFFECT.get(arg0.getId()));
        // return nms.hasStatusEffect(Registries.STATUS_EFFECT.get(arg0.getId()));
    }

    @Override
    public boolean isCollidable() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isGliding() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isLeashed() {
        if (!(getHandle() instanceof MobEntity))
            return false;
        return ((MobEntity) getHandle()).getLeashHolder() != null;
    }

    @Override
    public boolean isRiptiding() {
        return nms.isUsingRiptide();
    }

    @Override
    public boolean isSleeping() {
        return nms.isSleeping();
    }

    @Override
    public boolean isSwimming() {
        return nms.isSwimming();
    }

    @Override
    public RayTraceResult rayTraceBlocks(double maxDistance) {
        return this.rayTraceBlocks(maxDistance, FluidCollisionMode.NEVER);
    }

    @Override
    public RayTraceResult rayTraceBlocks(double maxDistance, FluidCollisionMode fluidCollisionMode) {
        Location eyeLocation = this.getEyeLocation();
        Vector direction = eyeLocation.getDirection();
        return this.getWorld().rayTraceBlocks(eyeLocation, direction, maxDistance, fluidCollisionMode, false);
    }

    @Override
    public void removePotionEffect(PotionEffectType type) {
    	me.isaiah.common.cmixin.IMixinEntity ic = ((me.isaiah.common.cmixin.IMixinEntity)(Object)nms);
    	
    	ic.IC$remove_status_effect( Registries.STATUS_EFFECT.get(type.getId()) );
    	
        //nms.removeStatusEffect(Registries.STATUS_EFFECT.get(type.getId())/*, EntityPotionEffectEvent.Cause.PLUGIN*/);
    }

    @Override
    public void setAI(boolean arg0) {
        if (this.getHandle() instanceof MobEntity)
            ((MobEntity) this.getHandle()).setAiDisabled(!arg0);
    }

    @Override
    public void setCanPickupItems(boolean arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setCollidable(boolean arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setGliding(boolean arg0) {
        nms.setFlag(7, arg0);
    }

    @Override
    public void setLastDamage(double arg0) {
        nms.lastDamageTaken = (float) arg0;
    }

    @Override
    public boolean setLeashHolder(Entity holder) {
        if ((nms instanceof WitherEntity) || !(nms instanceof MobEntity))
            return false;

        if (holder == null)
            return unleash();

        if (holder.isDead())
            return false;

        unleash();
        ((MobEntity) nms).attachLeash(((CraftEntity) holder).getHandle(), true);
        return true;
    }

    private boolean unleash() {
        if (!isLeashed())
            return false;
        ((MobEntity) getHandle()).detachLeash(true, false);
        return true;
    }

    @Override
    public void setMaximumAir(int arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setMaximumNoDamageTicks(int arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public <T> void setMemory(MemoryKey<T> arg0, T arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setNoDamageTicks(int arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setRemainingAir(int arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setRemoveWhenFarAway(boolean arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setSwimming(boolean arg0) {
        nms.setSwimming(arg0);
    }

    @Override
    public void swingMainHand() {
        nms.swingHand(Hand.MAIN_HAND);
    }

    @Override
    public void swingOffHand() {
        nms.swingHand(Hand.OFF_HAND);
    }

    @Override
    public Set<UUID> getCollidableExemptions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EntityCategory getCategory() {
        // TODO Auto-generated method stub
        return EntityCategory.NONE;
    }

    public void setArrowsInBody(int i) {
        // TODO
    }

    public int getArrowsInBody() {
        return -1; // TODO
    }

    public void setArrowCooldown(int i) {}
    public int getArrowCooldown() { return -1; }

    @Override
    public EntityType getType() {
        return EntityType.UNKNOWN;
    }

    // Spigot-743
    public boolean isInvisible() {
        return getHandle().isInvisible();
    }

    // Spigot-743
    public void setInvisible(boolean invisible) {
        // TODO getHandle().persistentInvisibility = invisible;
        getHandle().setFlag(5, invisible);
    }

    // PaperAPI - start
    public boolean isJumping() {
        return getHandle().jumping;
    }

    public void setJumping(boolean jumping) {
        getHandle().setJumping(jumping);
        if (jumping && getHandle() instanceof MobEntity)
            ((MobEntity) getHandle()).getJumpControl().tick();
    }

    @Override
    public boolean fromMobSpawner() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Chunk getChunk() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SpawnReason getEntitySpawnReason() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isInBubbleColumn() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isInWaterOrBubbleColumn() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isInWaterOrRain() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isInWaterOrRainOrBubbleColumn() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void clearActiveItem() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public ItemStack getActiveItem() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getArrowsStuck() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getHandRaisedTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public float getHurtDirection() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getItemUseRemainingTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getShieldBlockingDelay() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Block getTargetBlock(int arg0, FluidMode arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlockFace getTargetBlockFace(int arg0, FluidMode arg1) {
    	return this.getTargetBlockFace(arg0, arg1.bukkit);
    }

    @Override
    public TargetBlockInfo getTargetBlockInfo(int arg0, FluidMode arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Entity getTargetEntity(int arg0, boolean arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TargetEntityInfo getTargetEntityInfo(int arg0, boolean arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isHandRaised() {
    	return this.getHandle().isUsingItem();
    }

    @Override
    public void playPickupItemAnimation(Item arg0, int arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setArrowsStuck(int arg0) {
    	this.getHandle().setStuckArrowCount(arg0);
    }

    @Override
    public void setHurtDirection(float arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setKiller(Player arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setShieldBlockingDelay(int arg0) {
    	// this.getHandle().setShieldBlockingDelay(arg0);
    }
    // PaperAPI - end

    @Override
    public void registerAttribute(Attribute att) {
        ((IMixinLivingEntity) nms).cardboard_getAttr().registerAttribute(att);
    }

    @Override
    public @NotNull EquipmentSlot getHandRaised() {
        Hand hand = nms.getActiveHand();
        return hand == Hand.MAIN_HAND ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND;
    }

    @Override
    public boolean hasLineOfSight(@NotNull Location arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    // 1.17 API START
    @Override
    public boolean isClimbing() {
        return nms.isClimbing();
    }

    @Override
    public int getBeeStingerCooldown() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getBeeStingersInBody() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setBeeStingerCooldown(int i) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setBeeStingersInBody(int i) {
        // TODO Auto-generated method stub
    }
    
    // 1.19.2

	// @Override
	public <T extends Projectile> @NotNull T launchProjectile_old(@NotNull Class<? extends T> arg0, @Nullable Vector arg1,
			@Nullable Consumer<T> arg2) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// @Override
	public <T extends Projectile> T launchProjectile(Class<? extends T> projectile, Vector velocity, java.util.function.Consumer<? super T> function) {
		ServerWorld world = ((WorldImpl)this.getWorld()).getHandle();
        ProjectileEntity launch = null;
        if (Snowball.class.isAssignableFrom(projectile)) {
            launch = new SnowballEntity(world, this.getHandle());
            ((ThrownEntity)launch).setVelocity(this.getHandle(), this.getHandle().getPitch(), this.getHandle().getYaw(), 0.0f, 1.5f, 1.0f);
        } else if (Egg.class.isAssignableFrom(projectile)) {
            launch = new EggEntity(world, this.getHandle());
            ((ThrownEntity)launch).setVelocity(this.getHandle(), this.getHandle().getPitch(), this.getHandle().getYaw(), 0.0f, 1.5f, 1.0f);
        } else if (EnderPearl.class.isAssignableFrom(projectile)) {
            launch = new EnderPearlEntity(world, this.getHandle());
            ((ThrownEntity)launch).setVelocity(this.getHandle(), this.getHandle().getPitch(), this.getHandle().getYaw(), 0.0f, 1.5f, 1.0f);
        } else if (AbstractArrow.class.isAssignableFrom(projectile)) {
            if (TippedArrow.class.isAssignableFrom(projectile)) {
                launch = new ArrowEntity(world, this.getHandle(), new net.minecraft.item.ItemStack(Items.ARROW), null);
                ((Arrow)launch.getBukkitEntity()).setBasePotionType(PotionType.WATER);
            } else {
                launch = SpectralArrow.class.isAssignableFrom(projectile) ? new SpectralArrowEntity(world, this.getHandle(), new net.minecraft.item.ItemStack(Items.SPECTRAL_ARROW), null) : (Trident.class.isAssignableFrom(projectile) ? new TridentEntity(world, this.getHandle(), new net.minecraft.item.ItemStack(Items.TRIDENT)) : new ArrowEntity(world, this.getHandle(), new net.minecraft.item.ItemStack(Items.ARROW), null));
            }
            ((PersistentProjectileEntity)launch).setVelocity(this.getHandle(), this.getHandle().getPitch(), this.getHandle().getYaw(), 0.0f, Trident.class.isAssignableFrom(projectile) ? 2.5f : 3.0f, 1.0f);
        } else if (ThrownPotion.class.isAssignableFrom(projectile)) {
            if (LingeringPotion.class.isAssignableFrom(projectile)) {
                launch = new PotionEntity(world, this.getHandle());
                ((PotionEntity)launch).setItem(CraftItemStack.asNMSCopy(new ItemStack(Material.LINGERING_POTION, 1)));
            } else {
                launch = new PotionEntity(world, this.getHandle());
                ((PotionEntity)launch).setItem(CraftItemStack.asNMSCopy(new ItemStack(Material.SPLASH_POTION, 1)));
            }
            ((ThrownEntity)launch).setVelocity(this.getHandle(), this.getHandle().getPitch(), this.getHandle().getYaw(), -20.0f, 0.5f, 1.0f);
        } else if (ThrownExpBottle.class.isAssignableFrom(projectile)) {
            launch = new ExperienceBottleEntity(world, this.getHandle());
            ((ThrownEntity)launch).setVelocity(this.getHandle(), this.getHandle().getPitch(), this.getHandle().getYaw(), -20.0f, 0.7f, 1.0f);
        } else if (FishHook.class.isAssignableFrom(projectile) && this.getHandle() instanceof PlayerEntity) {
            launch = new FishingBobberEntity((PlayerEntity)this.getHandle(), world, 0, 0);
        } else if (Fireball.class.isAssignableFrom(projectile)) {
            Location location = this.getEyeLocation();
            Vector direction = location.getDirection().multiply(10);
            Vec3d vec = new Vec3d(direction.getX(), direction.getY(), direction.getZ());
            if (SmallFireball.class.isAssignableFrom(projectile)) {
                launch = new SmallFireballEntity(world, this.getHandle(), vec);
            } else if (WitherSkull.class.isAssignableFrom(projectile)) {
                launch = new WitherSkullEntity(world, this.getHandle(), vec);
            } else if (DragonFireball.class.isAssignableFrom(projectile)) {
                launch = new DragonFireballEntity(world, this.getHandle(), vec);
            } else if (AbstractWindCharge.class.isAssignableFrom(projectile)) {
                launch = BreezeWindCharge.class.isAssignableFrom(projectile)
                		? net.minecraft.entity.EntityType.BREEZE_WIND_CHARGE.create(world)
                		: net.minecraft.entity.EntityType.WIND_CHARGE.create(world);
                ((AbstractWindChargeEntity)launch).setOwner(this.getHandle());
                ((AbstractWindChargeEntity)launch).setVelocity(this.getHandle(), this.getHandle().getPitch(), this.getHandle().getYaw(), 0.0f, 1.5f, 1.0f);
            } else {
                launch = new FireballEntity(world, this.getHandle(), vec, 1);
            }
            ((ExplosiveProjectileEntity)launch).setProjectileSourceBukkit(this);
            // TODO: launch.preserveMotion = true;
            launch.refreshPositionAndAngles(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        } else if (LlamaSpit.class.isAssignableFrom(projectile)) {
            Location location = this.getEyeLocation();
            Vector direction = location.getDirection();
            launch = net.minecraft.entity.EntityType.LLAMA_SPIT.create(world);
            ((LlamaSpitEntity)launch).setOwner(this.getHandle());
            ((LlamaSpitEntity)launch).setVelocity(direction.getX(), direction.getY(), direction.getZ(), 1.5f, 10.0f);
            launch.refreshPositionAndAngles(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        } else if (ShulkerBullet.class.isAssignableFrom(projectile)) {
            Location location = this.getEyeLocation();
            launch = new ShulkerBulletEntity(world, this.getHandle(), null, null);
            launch.refreshPositionAndAngles(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        } else if (Firework.class.isAssignableFrom(projectile)) {
            Location location = this.getEyeLocation();
            
            // TODO
            
            /*
            launch = new FireworkRocketEntity(world, FireworkRocketEntity.getDefaultStack(), this.getHandle(), location.getX(), location.getY() - (double)0.15f, location.getZ(), true);
            float f2 = 0.0f;
            int projectileSize = 1;
            int i2 = 0;
            float f3 = projectileSize == 1 ? 0.0f : 2.0f * f2 / (float)(projectileSize - 1);
            float f4 = (float)((projectileSize - 1) % 2) * f3 / 2.0f;
            float f5 = 1.0f;
            float yaw = f4 + f5 * (float)((i2 + 1) / 2) * f3;
            Vec3d vec3 = this.getHandle().getOppositeRotationVector(1.0f);
            Quaternionf quaternionf = new Quaternionf().setAngleAxis((double)(yaw * ((float)Math.PI / 180)), vec3.x, vec3.y, vec3.z);
            Vec3d vec32 = this.getHandle().getRotationVec(1.0f);
            Vector3f vector3f = vec32.toVector3f().rotate((Quaternionfc)quaternionf);
            ((FireworkRocketEntity)launch).setVelocity(vector3f.x(), vector3f.y(), vector3f.z(), 1.6f, 1.0f);
            */
            
            launch = new FireworkRocketEntity(world, net.minecraft.item.ItemStack.EMPTY, getHandle());
            launch.refreshPositionAndAngles(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            
        }
        // Preconditions.checkArgument((launch != null ? 1 : 0) != 0, (String)"Projectile (%s) not supported", (Object)projectile.getName());
        if (velocity != null) {
            ((Projectile)launch.getBukkitEntity()).setVelocity(velocity);
        }
        if (function != null) {
            function.accept((T) (Projectile) launch.getBukkitEntity());
        }
        world.spawnEntity(launch);
        return (T)((Projectile)launch.getBukkitEntity());
	}

	@Override
	public @NotNull TriState getFrictionState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setFrictionState(@NotNull TriState arg0) {
		// TODO Auto-generated method stub
		
	}

	public void broadcastSlotBreak(EquipmentSlot slot) {
        this.getHandle().getWorld().sendEntityStatus(this.getHandle(), net.minecraft.entity.LivingEntity.getEquipmentBreakStatus(Utils.getNMS(slot)));
	}

	public void broadcastSlotBreak(EquipmentSlot slot, Collection<Player> players) {
		if (players.isEmpty()) {
			return;
		}
		EntityStatusS2CPacket packet = new EntityStatusS2CPacket(this.getHandle(), net.minecraft.entity.LivingEntity.getEquipmentBreakStatus( Utils.getNMS(slot)));
		players.forEach(player -> ((PlayerImpl)player).getHandle().networkHandler.sendPacket(packet));
	}

	@Override
    public boolean canBreatheUnderwater() {
        return this.getHandle().canBreatheInWater();
    }

	@Override
    public ItemStack damageItemStack(ItemStack stack, int amount) {
        net.minecraft.item.ItemStack nmsStack;
        if (stack instanceof CraftItemStack) {
            CraftItemStack craftItemStack = (CraftItemStack)stack;
            if (craftItemStack.handle == null || craftItemStack.handle.isEmpty()) {
                return stack;
            }
            nmsStack = craftItemStack.handle;
        } else {
            nmsStack = CraftItemStack.asNMSCopy(stack);
            stack = CraftItemStack.asCraftMirror(nmsStack);
        }
        this.damageItemStack0(nmsStack, amount, null);
        return stack;
    }

	@Override
    public void damageItemStack(EquipmentSlot slot, int amount) {
        net.minecraft.entity.EquipmentSlot nmsSlot = Utils.getNMS(slot);
        this.damageItemStack0(this.getHandle().getEquippedStack(nmsSlot), amount, nmsSlot);
    }
	
    private void damageItemStack0(net.minecraft.item.ItemStack nmsStack, int amount, net.minecraft.entity.EquipmentSlot slot) {
        /*nmsStack.damage(amount, this.getHandle(), livingEntity -> {
            if (slot != null) {
                livingEntity.sendEquipmentBreakStatus(slot);
            }
        });*/
        
        nmsStack.damage(amount, this.getHandle(), slot);
    }

	
	@Override
	public @Nullable Sound getDeathSound() {
		// TODO Auto-generated method stub
		return Sound.ENTITY_GENERIC_DEATH;
	}

	@Override
	public @NotNull Sound getDrinkingSound(@NotNull ItemStack arg0) {
		// TODO Auto-generated method stub
		return Sound.ENTITY_GENERIC_DRINK;
	}

	@Override
	public @NotNull Sound getEatingSound(@NotNull ItemStack arg0) {
		// TODO Auto-generated method stub
		return Sound.ENTITY_GENERIC_EAT;
	}

	@Override
	public @NotNull Sound getFallDamageSound(int arg0) {
		// TODO Auto-generated method stub
		return Sound.ENTITY_GENERIC_BIG_FALL;
	}

	@Override
	public @NotNull Sound getFallDamageSoundBig() {
		// TODO Auto-generated method stub
		return Sound.ENTITY_GENERIC_BIG_FALL;
	}

	@Override
	public @NotNull Sound getFallDamageSoundSmall() {
		// TODO Auto-generated method stub
		return Sound.ENTITY_GENERIC_SMALL_FALL;
	}

	@Override
	public @Nullable Sound getHurtSound() {
		// TODO Auto-generated method stub
		return Sound.ENTITY_GENERIC_HURT;
	}

	@Override
	public void knockback(double arg0, double arg1, double arg2) {
		 this.getHandle().takeKnockback(arg0, arg2, arg2);
	}
	
	// 1.19.4:

	@Override
    public float getBodyYaw() {
        return this.getHandle().getBodyYaw();
    }

	@Override
    public BlockFace getTargetBlockFace(int maxDistance, FluidCollisionMode fluidMode) {
        RayTraceResult result = this.rayTraceBlocks(maxDistance, fluidMode);
        return result != null ? result.getHitBlockFace() : null;
    }

	@Override
    public RayTraceResult rayTraceEntities(int maxDistance, boolean ignoreBlocks) {
        EntityHitResult rayTrace = this.rayTraceEntity(maxDistance, ignoreBlocks);
        return null;
        //return rayTrace == null ? null : new RayTraceResult(CraftVector.toBukkit(rayTrace.getPos()), ((IMixinEntity)rayTrace.getEntity()).getBukkitEntity());
    }
	
    public EntityHitResult rayTraceEntity(int maxDistance, boolean ignoreBlocks) {
        return null;
    }

	@Override
    public void setArrowsInBody(int count, boolean fireEvent) {
        // Preconditions.checkArgument((count >= 0 ? 1 : 0) != 0, (Object)"New arrow amount must be >= 0");
        if (!fireEvent) {
            this.getHandle().getDataTracker().set(net.minecraft.entity.LivingEntity.STUCK_ARROW_COUNT, count);
        } else {
            this.getHandle().setStuckArrowCount(count);
        }
    }

	@Override
	public void setBodyYaw(float arg0) {
        this.getHandle().setBodyYaw(arg0);
	}

	@Override
	public int getNoActionTicks() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setNoActionTicks(int ticks) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean clearActivePotionEffects() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void playHurtAnimation(float yaw) {
		// TODO Auto-generated method stub
		
	}
	
	// 1.20.2 API:
	@Override
    public float getSidewaysMovement() {
        return this.getHandle().sidewaysSpeed;
    }

	@Override
    public float getForwardsMovement() {
        return this.getHandle().forwardSpeed;
    }

	@Override
    public float getUpwardsMovement() {
        return this.getHandle().upwardSpeed;
    }
   
	// 1.20.4 API:
	
	@Override
    public boolean hasActiveItem() {
        return this.getHandle().isUsingItem();
    }
	
	@Override
    public EquipmentSlot getActiveItemHand() {
        return Utils.getHand(this.getHandle().getActiveHand());
    }
	
    @Override
    public void setItemInUseTicks(int ticks) {
        // TODO
    	// this.getHandle().itemUseTimeLeft = ticks;
    }
    
    @Override
    public int getItemInUseTicks() {
        return this.getHandle().getItemUseTimeLeft();
    }
    
    @Override
    public void startUsingItem(EquipmentSlot hand) {
        switch (hand) {
            case HAND: {
                this.getHandle().setCurrentHand(Hand.MAIN_HAND);
                break;
            }
            case OFF_HAND: {
                this.getHandle().setCurrentHand(Hand.OFF_HAND);
                break;
            }
            default: {
                throw new IllegalArgumentException("hand may only be HAND or OFF_HAND");
            }
        }
    }

    @Override
    public ItemStack getItemInUse() {
        net.minecraft.item.ItemStack item = this.getHandle().getActiveItem();
        return item.isEmpty() ? null : CraftItemStack.asCraftMirror(item);
    }
    
    @Override
    public void completeUsingActiveItem() {
        // TODO
    	// this.getHandle().consumeItem();
    }
    
    @Override
    public int getActiveItemRemainingTime() {
        return this.getHandle().getItemUseTimeLeft();
    }
    
    @Override
    public void setActiveItemRemainingTime(int ticks) {
    	// TODO
    	// this.getHandle().itemUseTimeLeft = ticks;
    }
    
    @Override
    public int getNextArrowRemoval() {
        return this.getHandle().stuckArrowTimer;
    }
    
    @Override
    public void setNextArrowRemoval(int ticks) {
        this.getHandle().stuckArrowTimer = ticks;
    }
    
    @Override
    public int getNextBeeStingerRemoval() {
        return this.getHandle().stuckStingerTimer;
    }
    
    @Override
    public void setNextBeeStingerRemoval(int ticks) {
        this.getHandle().stuckStingerTimer = ticks;
    }

	@Override
	public void damage(double amount, org.bukkit.damage.@NotNull DamageSource damageSource) {
		// TODO Auto-generated method stub
		
		

		// TODO this.damage(amount, ((CraftDamageSource)damageSource).getHandle());
	}

	@Override
	public int getActiveItemUsedTime() {
		return this.getHandle().getItemUseTime();
	}
	
	// 1.20.6 API:

	@Override
	public void heal(double amount, @NotNull RegainReason reason) {
		// TODO Auto-generated method stub
		this.heal(amount);
	}

	@Override
	public boolean canUseEquipmentSlot(@NotNull EquipmentSlot slot) {
		net.minecraft.entity.EquipmentSlot es = Utils.getNMS(slot);
		return this.getHandle().canUseSlot( es );
	}
	
	// 1.21:

	@Override
	public void broadcastHurtAnimation(@NotNull Collection<Player> players) {
		 for (Player player : players) {
			 ((PlayerImpl)player).sendHurtAnimation(0.0f, this);
		 }
	}

	@Override
	public void setRiptiding(boolean riptiding) {
		// this.getHandle().setLivingFlag(4, riptiding);
	}

}
