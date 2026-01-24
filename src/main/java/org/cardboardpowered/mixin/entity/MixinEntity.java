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
package org.cardboardpowered.mixin.entity;

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
import org.cardboardpowered.api.event.CardboardEntityMountEvent;
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
public class MixinEntity implements IMixinCommandOutput, IMixinEntity {

    public CraftEntity bukkit;
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
    public Level level;
    
    @Override
    public Level mc_world() {
    	return level;
    }

    @Shadow
    private EntityDimensions dimensions;

    public MixinEntity() {
        this.bukkit = getEntity(CraftServer.INSTANCE, (Entity)(Object)this);
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

    @Inject(at = @At(value = "HEAD"), method = "tick()V")
    public void setBukkit(CallbackInfo callbackInfo) {
        if (null == bukkit) {
            this.bukkit = getEntity(CraftServer.INSTANCE, (Entity)(Object)this);
        }
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
        return bukkit;
    }

    @Override
    public CraftEntity getBukkitEntityRaw() {
    	return bukkit;
    }
    
    @Override
    public CraftEntity getBukkitEntity() {
        if (null == bukkit) {
            this.bukkit = getEntity(CraftServer.INSTANCE, (Entity)(Object)this);
        }
        return bukkit;
    }

    @Inject(at = @At("HEAD"), method = "restoreFrom(Lnet/minecraft/world/entity/Entity;)V")
    public void cardboard$setBukkitHandleForCopy(Entity original, CallbackInfo ci) {
    	CraftEntity bukkitEntity = ((IMixinEntity) original).getBukkitEntityRaw();
        if (bukkitEntity != null) {
           bukkitEntity.setHandle((Entity) (Object) this);
           this.bukkit = bukkitEntity;
        }
    }
    
    @Override
    public void setProjectileSourceBukkit(ProjectileSource source) {
        this.projectileSource = source;
    }

    
    @Deprecated
    private static CraftEntity getEntity(CraftServer server, Entity entity) {
        /*
         * Order is *EXTREMELY* important -- keep it right! =D
         */
        // CHECKSTYLE:OFF
        if (entity instanceof LivingEntity) {
            // Players
            if (entity instanceof Player) {
                if (entity instanceof ServerPlayer) { return new CraftPlayer((ServerPlayer) entity); }
                else { return new CraftHumanEntity((Player) entity); }
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
                    	return CraftEntity.getEntity_new(server, (AbstractVillager) entity);
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
            // else if (entity instanceof FlyingEntity) {
                if (entity instanceof Ghast) { return new CardboardGhast(server, (Ghast) entity); }
                else if (entity instanceof Phantom) { return new CardboardPhantom(server, (Phantom) entity); }
            //    else { return new CardboardFlying(server, (FlyingEntity) entity); }
            // }
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
    
}
