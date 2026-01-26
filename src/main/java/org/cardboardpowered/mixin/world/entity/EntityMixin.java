/**
 * Cardboard - Spigot/Paper for Fabric
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.cardboardpowered.mixin.world.entity;

import org.cardboardpowered.interfaces.IMixinCommandOutput;
import org.cardboardpowered.interfaces.IMixinEntity;
// import com.llamalad7.mixinextras.sugar.Local;

import me.isaiah.common.entity.IRemoveReason;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.LlamaSpit;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.*;
import org.bukkit.entity.Pose;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPoseChangeEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.cardboardpowered.impl.entity.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(Entity.class)
public abstract class EntityMixin implements IMixinCommandOutput, IMixinEntity {

    public CraftEntity bukkitEntity;
    public org.bukkit.projectiles.ProjectileSource projectileSource;
    private ArrayList<org.bukkit.inventory.ItemStack> drops = new ArrayList<org.bukkit.inventory.ItemStack>();
    private boolean forceDrops;

    @Override
    public ArrayList<org.bukkit.inventory.ItemStack> cardboard_getDrops() {
        return drops;
    }

    @Override
    public void cardboard_setDrops(ArrayList<org.bukkit.inventory.ItemStack> drops) {
        this.drops = drops;
    }
    
    @Override
    public AABB cardboad_getBoundingBoxAt(double x2, double y2, double z2) {
        return this.dimensions.makeBoundingBox(x2, y2, z2);
    }

    @Override
    public boolean cardboard_getForceDrops() {return forceDrops;}

    @Override
    public void cardboard_setForceDrops(boolean forceDrops) {
        this.forceDrops = forceDrops;
    }

    @Shadow
    private Level level;
    
    @Override
    public Level mc_world() {
    	return level;
    }

    @Shadow
    private EntityDimensions dimensions;

    public EntityMixin() {
    }

    /*
    public void sendSystemMessage(Text message) {
        // TODO: 1.19
    	
    	((Entity) (Object) this).sendMessage(message);
    	//((Entity) (Object) this).sendSystemMessage(message, UUID.randomUUID());
    }
    */

    public boolean valid = false;
    public boolean cardboard$inWorld = false;
    public Location origin_bukkit;

    @Override
    public Location getOriginBF() {
        return origin_bukkit;
    }

    @Override
    public void setOriginBF(Location loc) {
        this.origin_bukkit = loc;
    }

    @Override
    public boolean isValidBF() {
        return valid;
    }

    @Override
    public void setValid(boolean b) {
        this.valid = b;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"),
    		method = "spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/entity/item/ItemEntity;")
    		// method = "dropStack(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/ItemEntity;")
    public boolean cardboard$mixinEntity_dropStack_EntityDropItemEvent(ServerLevel world, Entity entity, ServerLevel sworld, ItemStack itemstack, Vec3 offset) {
        if (itemstack.isEmpty())
            return false;

        boolean chick = (((Entity)(Object)this) instanceof Chicken && itemstack.getItem() == Items.EGG);
        if (((Entity)(Object)this) instanceof net.minecraft.world.entity.LivingEntity && !this.forceDrops) {
            if (!chick) {
                this.drops.add(org.bukkit.craftbukkit.inventory.CraftItemStack.asBukkitCopy(itemstack));
                return false;
            }
        }
        ItemEntity entityitem = new ItemEntity(this.level,
        		((Entity) (Object) this).getX() + offset.x,
        		((Entity) (Object) this).getY() + offset.y,
        		((Entity) (Object) this).getZ() + offset.z, itemstack);

        entityitem.setDefaultPickUpDelay();

        EntityDropItemEvent event = new EntityDropItemEvent(this.getBukkitEntity(), (org.bukkit.entity.Item) ((IMixinEntity)entityitem).getBukkitEntity());
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return false;
        return this.level.addFreshEntity(entityitem);
    }


    @Override
    public CommandSender getBukkitSender(CommandSourceStack serverCommandSource) {
        return bukkitEntity;
    }

    @Override
    public CraftEntity getBukkitEntityRaw() {
    	return bukkitEntity;
    }
    
    @Override
    public org.bukkit.craftbukkit.entity.CraftEntity getBukkitEntity() {
        if (this.bukkitEntity == null) {
            // Paper start - Folia schedulers
            synchronized (this) {
                if (this.bukkitEntity == null) {
                    return this.bukkitEntity = org.bukkit.craftbukkit.entity.CraftEntity.getEntity(CraftServer.INSTANCE, (Entity) (Object) this);
                }
            }
            // Paper end - Folia schedulers
        }
        return this.bukkitEntity;
    }

    @Inject(at = @At("HEAD"), method = "restoreFrom(Lnet/minecraft/world/entity/Entity;)V")
    public void cardboard$setBukkitHandleForCopy(Entity entity, CallbackInfo ci) {
        // Paper start - Forward CraftEntity in teleport command
        org.bukkit.craftbukkit.entity.CraftEntity bukkitEntity = ((IMixinEntity)entity).getBukkitEntityRaw();
        if (bukkitEntity != null) {
            bukkitEntity.setHandle((Entity)(Object)this);
            this.bukkitEntity = bukkitEntity;
        }
        // Paper end - Forward CraftEntity in teleport command
    }
    
    @Override
    public void setProjectileSourceBukkit(ProjectileSource source) {
        this.projectileSource = source;
    }

    @Override
    public ProjectileSource getProjectileSourceBukkit() {
        return projectileSource;
    }

    @Inject(at = @At("HEAD"), method = "setPose(Lnet/minecraft/world/entity/Pose;)V", cancellable = true)
    public void setPoseBF(net.minecraft.world.entity.Pose entitypose, CallbackInfo ci) {
        if (entitypose == ((Entity)(Object)this).getPose()) {
            ci.cancel();
            return;
        }
        Pose b = Pose.STANDING;
        switch (entitypose) {
            case CROUCHING:
                b = Pose.SNEAKING;
                break;
            case DYING:
                b = Pose.DYING;
                break;
            case FALL_FLYING:
                b = Pose.FALL_FLYING;
                break;
            case LONG_JUMPING:
                // TODO 1.17ify
                break;
            case SLEEPING:
                b = Pose.SLEEPING;
                break;
            case SPIN_ATTACK:
                b = Pose.SPIN_ATTACK;
                break;
            case STANDING:
                b = Pose.STANDING;
                break;
            case SWIMMING:
                b = Pose.SWIMMING;
                break;
            default:
                break;  
        }
        Bukkit.getPluginManager().callEvent(new EntityPoseChangeEvent(this.getBukkitEntity(), b));
    }

    @Inject(at = @At("HEAD"), method = "setAirSupply", cancellable = true)
    public void setAirBF(int i, CallbackInfo ci) {
        if (!valid) {
            ci.cancel();
            return;
        }

        EntityAirChangeEvent event = new EntityAirChangeEvent(this.getBukkitEntity(), i);
        event.getEntity().getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            ci.cancel();
            return;
        }
        i = event.getAmount();
    }

    public void removeBF() {
        ((me.isaiah.common.cmixin.IMixinEntity)this).Iremove(IRemoveReason.DISCARDED);
    }

    @Shadow
    public void move(MoverType moveType, Vec3 vec3d) {
    }
    
    /*
    @Shadow
    private TeleportTarget getTeleportTarget(ServerWorld w) {
        return null;
    }
    */

    @Shadow
    public boolean isPushable() {
        return false;
    }

    @Shadow
    public Level level() {
        return null;
    }

    /**
     * EntityCombustByBlockEvent
     * 
     * @author Arclight
     * @author Cardboard
     */
    @Redirect(method = "lavaIgnite", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;igniteForSeconds(F)V"))
    public void cardboard$mixinEntity_igniteByLava_EntityCombustByBlockEvent(Entity entity, float seconds) {
        if ((Object) this instanceof LivingEntity && ((Entity) (Object) this).remainingFireTicks <= 0) {
            org.bukkit.block.Block damager = null;
            org.bukkit.entity.Entity damagee = this.getBukkitEntity();
            EntityCombustEvent combustEvent = new EntityCombustByBlockEvent(damager, damagee, 15);
            Bukkit.getPluginManager().callEvent(combustEvent);

            if (!combustEvent.isCancelled())
                ((Entity) (Object) this).igniteForSeconds(combustEvent.getDuration());
        } else {
            // This will be called every single tick the entity is in lava, so don't throw an event
            ((Entity) (Object) this).igniteForSeconds(15);
        }
    }

	@Override
	public void cb$setInWorld(boolean b) {
		cardboard$inWorld = b;
	}

	@Override
	public boolean cb$getInWorld() {
		return cardboard$inWorld;
	}

    // TODO
    
    /*
    @Inject(method = "addPassenger", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList;isEmpty()Z"))
    private void fireCardboardEntityMountEvent(Entity passenger, CallbackInfo ci) {
        ActionResult result = CardboardEntityMountEvent.EVENT.invoker().interact(((Entity) (Object) this), passenger);

        if (result == ActionResult.FAIL) {
            ci.cancel();
        }
    }
    */
	
	/**
	 * Save Bukkit WorldUUID
	 * 
	 * @author Cardboard
	 */
	@Inject(method = "saveWithoutId", at = @At(value = "INVOKE", shift = At.Shift.AFTER, ordinal = 0, target = "Lnet/minecraft/world/level/storage/ValueOutput;store(Ljava/lang/String;Lcom/mojang/serialization/Codec;Ljava/lang/Object;)V"))
    public void cardboard$writeData_saveBukkitWorldUuid(ValueOutput output, CallbackInfo ci) {
		output.putLong("WorldUUIDLeast", this.level.getCraftWorld().getUID().getLeastSignificantBits());
		output.putLong("WorldUUIDMost", this.level.getCraftWorld().getUID().getMostSignificantBits());
    }

    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    public void isPushablePaper(CallbackInfoReturnable<Boolean> cir) {
        // Paper start - Climbing should not bypass cramming gamerule
        cir.setReturnValue(cardboard$isCollidable(false));
    }

    @Override
    public boolean cardboard$isCollidable(boolean ignoreClimbing) {
        // Paper end - Climbing should not bypass cramming gamerule
        return false;
    }

    // CraftBukkit start - collidable API
    @Override
    public boolean cardboard$canCollideWithBukkit(Entity entity) {
        return this.isPushable();
    }
    // CraftBukkit end
}
