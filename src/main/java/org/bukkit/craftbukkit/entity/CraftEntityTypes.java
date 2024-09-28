package org.bukkit.craftbukkit.entity;

import com.github.bsideup.jabel.Desugar;
import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.EyeOfEnderEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MarkerEntity;
import net.minecraft.entity.OminousItemSpawnerEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.decoration.GlowItemFrameEntity;
import net.minecraft.entity.decoration.InteractionEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.BoggedEntity;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.entity.mob.CaveSpiderEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.PiglinBruteEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.ZoglinEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.passive.ArmadilloEntity;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.CamelEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.DonkeyEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.entity.passive.GlowSquidEntity;
import net.minecraft.entity.passive.GoatEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.MuleEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SnifferEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.passive.TadpoleEntity;
import net.minecraft.entity.passive.TraderLlamaEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.BreezeWindChargeEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.entity.vehicle.SpawnerMinecartEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Allay;
import org.bukkit.entity.AreaEffectCloud;
// import org.bukkit.entity.Armadillo;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Boat;
// import org.bukkit.entity.Bogged;
// import org.bukkit.entity.Breeze;
// import org.bukkit.entity.BreezeWindCharge;
import org.bukkit.entity.Camel;
import org.bukkit.entity.Cat;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.ChestBoat;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cod;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Dolphin;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.Drowned;
import org.bukkit.entity.Egg;
import org.bukkit.entity.ElderGuardian;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Firework;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Frog;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.GlowSquid;
import org.bukkit.entity.Goat;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Husk;
import org.bukkit.entity.Illusioner;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LeashHitch;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Llama;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Marker;
import org.bukkit.entity.Mule;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
// import org.bukkit.entity.OminousItemSpawner;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.PiglinBrute;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.Player;
import org.bukkit.entity.PolarBear;
import org.bukkit.entity.PufferFish;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Ravager;
import org.bukkit.entity.Salmon;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.entity.Slime;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Sniffer;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Stray;
import org.bukkit.entity.Strider;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tadpole;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.TraderLlama;
import org.bukkit.entity.Trident;
import org.bukkit.entity.TropicalFish;
import org.bukkit.entity.Turtle;
import org.bukkit.entity.Vex;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Vindicator;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.entity.Warden;
// import org.bukkit.entity.WindCharge;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zoglin;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.ZombieHorse;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.entity.minecart.SpawnerMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public final class CraftEntityTypes {
    private static final BiConsumer<SpawnData, Entity> POS = (spawnData, entity) -> entity.setPosition(spawnData.x(), spawnData.y(), spawnData.z());
    private static final BiConsumer<SpawnData, Entity> ABS_MOVE = (spawnData, entity) -> {
        entity.updatePositionAndAngles(spawnData.x(), spawnData.y(), spawnData.z(), spawnData.yaw(), spawnData.pitch());
        entity.setHeadYaw(spawnData.yaw());
    };
    private static final BiConsumer<SpawnData, Entity> MOVE = (spawnData, entity) -> entity.refreshPositionAndAngles(spawnData.x(), spawnData.y(), spawnData.z(), spawnData.yaw(), spawnData.pitch());
    private static final BiConsumer<SpawnData, Entity> MOVE_EMPTY_ROT = (spawnData, entity) -> entity.refreshPositionAndAngles(spawnData.x(), spawnData.y(), spawnData.z(), 0.0f, 0.0f);
    private static final BiConsumer<SpawnData, ExplosiveProjectileEntity> DIRECTION = (spawnData, entity) -> {
        Vector direction = spawnData.location().getDirection().multiply(10);
        // TODO
        // entity.setPower(direction.getX(), direction.getY(), direction.getZ());
    };
    private static final BiConsumer<SpawnData, Entity> ROT = (spawnData, entity) -> entity.setRotation(spawnData.yaw(), spawnData.pitch());
    private static final BiConsumer<SpawnData, Entity> CLEAR_MOVE_IF_NOT_RANDOMIZED = (spawnData, entity) -> {
        if (!spawnData.randomizeData()) {
            entity.setVelocity(Vec3d.ZERO);
        }
    };
    private static final Map<Class<?>, EntityTypeData<?, ?>> CLASS_TYPE_DATA = new HashMap();
    private static final Map<org.bukkit.entity.EntityType, EntityTypeData<?, ?>> ENTITY_TYPE_DATA = new HashMap();

    private static void register(EntityTypeData<?, ?> typeData) {
        EntityTypeData<?, ?> other = CLASS_TYPE_DATA.put(typeData.entityClass(), typeData);
        if (other != null) {
            Bukkit.getLogger().warning(String.format("Found multiple entity type data for class %s, replacing '%s' with new value '%s'", typeData.entityClass().getName(), other, typeData));
        }
        if ((other = ENTITY_TYPE_DATA.put(typeData.entityType(), typeData)) != null) {
            Bukkit.getLogger().warning(String.format("Found multiple entity type data for entity type %s, replacing '%s' with new value '%s'", typeData.entityType().getKey(), other, typeData));
        }
    }

    private static <R extends Entity> Function<SpawnData, R> fromEntityType(EntityType<R> entityTypes) {
        return spawnData -> entityTypes.create(spawnData.minecraftWorld());
    }

    private static <R extends LivingEntity> Function<SpawnData, R> createLiving(EntityType<R> entityTypes) {
        return CraftEntityTypes.combine(CraftEntityTypes.fromEntityType(entityTypes), ABS_MOVE);
    }

    private static <R extends ExplosiveProjectileEntity> Function<SpawnData, R> createFireball(EntityType<R> entityTypes) {
        return CraftEntityTypes.combine(CraftEntityTypes.createAndMove(entityTypes), DIRECTION);
    }

    private static <R extends Entity> Function<SpawnData, R> createAndMove(EntityType<R> entityTypes) {
        return CraftEntityTypes.combine(CraftEntityTypes.fromEntityType(entityTypes), MOVE);
    }

    private static <R extends Entity> Function<SpawnData, R> createAndMoveEmptyRot(EntityType<R> entityTypes) {
        return CraftEntityTypes.combine(CraftEntityTypes.fromEntityType(entityTypes), MOVE_EMPTY_ROT);
    }

    private static <R extends Entity> Function<SpawnData, R> createAndSetPos(EntityType<R> entityTypes) {
        return CraftEntityTypes.combine(CraftEntityTypes.fromEntityType(entityTypes), POS);
    }

    private static <E extends Hanging, R extends AbstractDecorationEntity> Function<SpawnData, R> createHanging(Class<E> clazz, BiFunction<SpawnData, HangingData, R> spawnFunction) {
        return spawnData -> {
            boolean randomizeData = spawnData.randomizeData();
            BlockFace face = BlockFace.SELF;
            BlockFace[] faces = new BlockFace[]{BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};
            int width = 16;
            int height = 16;
            if (ItemFrame.class.isAssignableFrom(clazz)) {
                width = 12;
                height = 12;
                faces = new BlockFace[]{BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH, BlockFace.UP, BlockFace.DOWN};
            }
            BlockPos pos = BlockPos.ofFloored(spawnData.x(), spawnData.y(), spawnData.z());
            for (BlockFace dir : faces) {
                Box bb;
                BlockState nmsBlock = spawnData.world().getBlockState(pos.offset(CraftBlock.blockFaceToNotch(dir)));
                if (!nmsBlock.isSolid() && !AbstractRedstoneGateBlock.isRedstoneGate(nmsBlock)) continue;
                boolean taken = false;
                
                Box box = bb = ItemFrame.class.isAssignableFrom(clazz)
                		? ItemFrameEntity_calculateBoundingBox(null, pos, CraftBlock.blockFaceToNotch(dir).getOpposite(), width, height)
                		: AbstractDecorationEntity_calculateBoundingBox(null, pos, CraftBlock.blockFaceToNotch(dir).getOpposite(), width, height);

                if (!spawnData.world.isSpaceEmpty(bb)) continue;
                List<Entity> list = spawnData.world().getOtherEntities(null, bb);
                Iterator<Entity> it = list.iterator();
                while (!taken && it.hasNext()) {
                    Entity e2 = it.next();
                    if (!(e2 instanceof AbstractDecorationEntity)) continue;
                    taken = true;
                }
                if (taken) continue;
                face = dir;
                break;
            }
            if (face == BlockFace.SELF) {
                face = BlockFace.SOUTH;
                randomizeData = false;
            }
            Direction dir = CraftBlock.blockFaceToNotch(face).getOpposite();
            return spawnFunction.apply(spawnData, new HangingData(randomizeData, pos, dir));
        };
    }
    
    private static Box ItemFrameEntity_calculateBoundingBox(Entity entity, BlockPos blockPosition, Direction direction, int width, int height) {
        double d0 = 0.46875;
        double locX = blockPosition.getX() + 0.5 - direction.getOffsetX() * 0.46875;
        double locY = blockPosition.getY() + 0.5 - direction.getOffsetY() * 0.46875;
        double locZ = blockPosition.getZ() + 0.5 - direction.getOffsetZ() * 0.46875;
        if (entity != null) {
            entity.setPos(locX, locY, locZ);
        }
        double d2 = width;
        double d3 = height;
        double d4 = width;
        Direction.Axis enumdirection_enumaxis = direction.getAxis();
        switch (enumdirection_enumaxis) {
            case X -> d2 = 1.0;
            case Y -> d3 = 1.0;
            case Z -> d4 = 1.0;
        }
        d2 /= 32.0;
        d3 /= 32.0;
        d4 /= 32.0;
        return new Box(locX - d2, locY - d3, locZ - d4, locX + d2, locY + d3, locZ + d4);
    }
    
    private static double a(int i) {
        return i % 32 == 0 ? 0.5D : 0.0D;
    }
    
    private static Box AbstractDecorationEntity_calculateBoundingBox(Entity entity, BlockPos blockPosition, Direction direction, int width, int height) {
        double d0 = blockPosition.getX() + 0.5;
        double d2 = blockPosition.getY() + 0.5;
        double d3 = blockPosition.getZ() + 0.5;
        double d4 = 0.46875;
        double d5 = a(width);
        double d6 = a(height);
        d0 -= direction.getOffsetX() * 0.46875;
        d3 -= direction.getOffsetZ() * 0.46875;
        d2 += d6;
        Direction enumdirection = direction.rotateYCounterclockwise();
        d0 += d5 * enumdirection.getOffsetX();
        d3 += d5 * enumdirection.getOffsetZ();
        if (entity != null) {
            entity.setPos(d0, d2, d3);
        }
        double d7 = width;
        double d8 = height;
        double d9 = width;
        if (direction.getAxis() == Direction.Axis.Z) {
            d9 = 1.0;
        } else {
            d7 = 1.0;
        }
        d7 /= 32.0;
        d8 /= 32.0;
        d9 /= 32.0;
        return new Box(d0 - d7, d2 - d8, d3 - d9, d0 + d7, d2 + d8, d3 + d9);
    }

    private static <T, R> Function<T, R> combine(Function<T, R> before, BiConsumer<T, ? super R> after) {
        return t -> {
            R r = before.apply(t);
            after.accept(t, r);
            return r;
        };
    }

    public static <E extends org.bukkit.entity.Entity, M extends Entity> EntityTypeData<E, M> getEntityTypeData(org.bukkit.entity.EntityType entityType) {
        return (EntityTypeData<E, M>) ENTITY_TYPE_DATA.get(entityType);
    }

    public static <E extends org.bukkit.entity.Entity, M extends Entity> EntityTypeData<E, M> getEntityTypeData(Class<E> entityClass) {
        return (EntityTypeData<E, M>) CLASS_TYPE_DATA.get(entityClass);
    }

    private CraftEntityTypes() {
    }

    static {
    	/*
        CraftEntityTypes.register(new EntityTypeData<ElderGuardian, ElderGuardianEntity>(org.bukkit.entity.EntityType.ELDER_GUARDIAN, ElderGuardian.class, CraftElderGuardian::new, CraftEntityTypes.createLiving(EntityType.ELDER_GUARDIAN)));
        CraftEntityTypes.register(new EntityTypeData<WitherSkeleton, WitherSkeletonEntity>(org.bukkit.entity.EntityType.WITHER_SKELETON, WitherSkeleton.class, CraftWitherSkeleton::new, CraftEntityTypes.createLiving(EntityType.WITHER_SKELETON)));
        CraftEntityTypes.register(new EntityTypeData<Stray, StrayEntity>(org.bukkit.entity.EntityType.STRAY, Stray.class, CraftStray::new, CraftEntityTypes.createLiving(EntityType.STRAY)));
        CraftEntityTypes.register(new EntityTypeData<Bogged, BoggedEntity>(org.bukkit.entity.EntityType.BOGGED, Bogged.class, CraftBogged::new, CraftEntityTypes.createLiving(EntityType.BOGGED)));
        CraftEntityTypes.register(new EntityTypeData<Husk, HuskEntity>(org.bukkit.entity.EntityType.HUSK, Husk.class, CraftHusk::new, CraftEntityTypes.createLiving(EntityType.HUSK)));
        CraftEntityTypes.register(new EntityTypeData<ZombieVillager, ZombieVillagerEntity>(org.bukkit.entity.EntityType.ZOMBIE_VILLAGER, ZombieVillager.class, CraftVillagerZombie::new, CraftEntityTypes.createLiving(EntityType.ZOMBIE_VILLAGER)));
        CraftEntityTypes.register(new EntityTypeData<SkeletonHorse, SkeletonHorseEntity>(org.bukkit.entity.EntityType.SKELETON_HORSE, SkeletonHorse.class, CraftSkeletonHorse::new, CraftEntityTypes.createLiving(EntityType.SKELETON_HORSE)));
        CraftEntityTypes.register(new EntityTypeData<ZombieHorse, ZombieHorseEntity>(org.bukkit.entity.EntityType.ZOMBIE_HORSE, ZombieHorse.class, CraftZombieHorse::new, CraftEntityTypes.createLiving(EntityType.ZOMBIE_HORSE)));
        CraftEntityTypes.register(new EntityTypeData<ArmorStand, ArmorStandEntity>(org.bukkit.entity.EntityType.ARMOR_STAND, ArmorStand.class, CraftArmorStand::new, CraftEntityTypes.createLiving(EntityType.ARMOR_STAND)));
        CraftEntityTypes.register(new EntityTypeData<Donkey, DonkeyEntity>(org.bukkit.entity.EntityType.DONKEY, Donkey.class, CraftDonkey::new, CraftEntityTypes.createLiving(EntityType.DONKEY)));
        CraftEntityTypes.register(new EntityTypeData<Mule, MuleEntity>(org.bukkit.entity.EntityType.MULE, Mule.class, CraftMule::new, CraftEntityTypes.createLiving(EntityType.MULE)));
        CraftEntityTypes.register(new EntityTypeData<Evoker, EvokerEntity>(org.bukkit.entity.EntityType.EVOKER, Evoker.class, CraftEvoker::new, CraftEntityTypes.createLiving(EntityType.EVOKER)));
        CraftEntityTypes.register(new EntityTypeData<Vex, VexEntity>(org.bukkit.entity.EntityType.VEX, Vex.class, CraftVex::new, CraftEntityTypes.createLiving(EntityType.VEX)));
        CraftEntityTypes.register(new EntityTypeData<Vindicator, VindicatorEntity>(org.bukkit.entity.EntityType.VINDICATOR, Vindicator.class, CraftVindicator::new, CraftEntityTypes.createLiving(EntityType.VINDICATOR)));
        CraftEntityTypes.register(new EntityTypeData<Illusioner, IllusionerEntity>(org.bukkit.entity.EntityType.ILLUSIONER, Illusioner.class, CraftIllusioner::new, CraftEntityTypes.createLiving(EntityType.ILLUSIONER)));
        CraftEntityTypes.register(new EntityTypeData<Creeper, CreeperEntity>(org.bukkit.entity.EntityType.CREEPER, Creeper.class, CraftCreeper::new, CraftEntityTypes.createLiving(EntityType.CREEPER)));
        CraftEntityTypes.register(new EntityTypeData<Skeleton, SkeletonEntity>(org.bukkit.entity.EntityType.SKELETON, Skeleton.class, CraftSkeleton::new, CraftEntityTypes.createLiving(EntityType.SKELETON)));
        CraftEntityTypes.register(new EntityTypeData<Spider, SpiderEntity>(org.bukkit.entity.EntityType.SPIDER, Spider.class, CraftSpider::new, CraftEntityTypes.createLiving(EntityType.SPIDER)));
        CraftEntityTypes.register(new EntityTypeData<Giant, GiantEntity>(org.bukkit.entity.EntityType.GIANT, Giant.class, CraftGiant::new, CraftEntityTypes.createLiving(EntityType.GIANT)));
        CraftEntityTypes.register(new EntityTypeData<Zombie, ZombieEntity>(org.bukkit.entity.EntityType.ZOMBIE, Zombie.class, CraftZombie::new, CraftEntityTypes.createLiving(EntityType.ZOMBIE)));
        CraftEntityTypes.register(new EntityTypeData<Slime, SlimeEntity>(org.bukkit.entity.EntityType.SLIME, Slime.class, CraftSlime::new, CraftEntityTypes.createLiving(EntityType.SLIME)));
        CraftEntityTypes.register(new EntityTypeData<Ghast, GhastEntity>(org.bukkit.entity.EntityType.GHAST, Ghast.class, CraftGhast::new, CraftEntityTypes.createLiving(EntityType.GHAST)));
        CraftEntityTypes.register(new EntityTypeData<PigZombie, ZombifiedPiglinEntity>(org.bukkit.entity.EntityType.ZOMBIFIED_PIGLIN, PigZombie.class, CraftPigZombie::new, CraftEntityTypes.createLiving(EntityType.ZOMBIFIED_PIGLIN)));
        CraftEntityTypes.register(new EntityTypeData<Enderman, EndermanEntity>(org.bukkit.entity.EntityType.ENDERMAN, Enderman.class, CraftEnderman::new, CraftEntityTypes.createLiving(EntityType.ENDERMAN)));
        CraftEntityTypes.register(new EntityTypeData<CaveSpider, CaveSpiderEntity>(org.bukkit.entity.EntityType.CAVE_SPIDER, CaveSpider.class, CraftCaveSpider::new, CraftEntityTypes.createLiving(EntityType.CAVE_SPIDER)));
        CraftEntityTypes.register(new EntityTypeData<Silverfish, SilverfishEntity>(org.bukkit.entity.EntityType.SILVERFISH, Silverfish.class, CraftSilverfish::new, CraftEntityTypes.createLiving(EntityType.SILVERFISH)));
        CraftEntityTypes.register(new EntityTypeData<Blaze, BlazeEntity>(org.bukkit.entity.EntityType.BLAZE, Blaze.class, CraftBlaze::new, CraftEntityTypes.createLiving(EntityType.BLAZE)));
        CraftEntityTypes.register(new EntityTypeData<MagmaCube, MagmaCubeEntity>(org.bukkit.entity.EntityType.MAGMA_CUBE, MagmaCube.class, CraftMagmaCube::new, CraftEntityTypes.createLiving(EntityType.MAGMA_CUBE)));
        CraftEntityTypes.register(new EntityTypeData<Wither, WitherEntity>(org.bukkit.entity.EntityType.WITHER, Wither.class, CraftWither::new, CraftEntityTypes.createLiving(EntityType.WITHER)));
        CraftEntityTypes.register(new EntityTypeData<Bat, BatEntity>(org.bukkit.entity.EntityType.BAT, Bat.class, CraftBat::new, CraftEntityTypes.createLiving(EntityType.BAT)));
        CraftEntityTypes.register(new EntityTypeData<Witch, WitchEntity>(org.bukkit.entity.EntityType.WITCH, Witch.class, CraftWitch::new, CraftEntityTypes.createLiving(EntityType.WITCH)));
        CraftEntityTypes.register(new EntityTypeData<Endermite, EndermiteEntity>(org.bukkit.entity.EntityType.ENDERMITE, Endermite.class, CraftEndermite::new, CraftEntityTypes.createLiving(EntityType.ENDERMITE)));
        CraftEntityTypes.register(new EntityTypeData<Guardian, GuardianEntity>(org.bukkit.entity.EntityType.GUARDIAN, Guardian.class, CraftGuardian::new, CraftEntityTypes.createLiving(EntityType.GUARDIAN)));
        CraftEntityTypes.register(new EntityTypeData<Shulker, ShulkerEntity>(org.bukkit.entity.EntityType.SHULKER, Shulker.class, CraftShulker::new, CraftEntityTypes.createLiving(EntityType.SHULKER)));
        CraftEntityTypes.register(new EntityTypeData<Pig, PigEntity>(org.bukkit.entity.EntityType.PIG, Pig.class, CraftPig::new, CraftEntityTypes.createLiving(EntityType.PIG)));
        CraftEntityTypes.register(new EntityTypeData<Sheep, SheepEntity>(org.bukkit.entity.EntityType.SHEEP, Sheep.class, CraftSheep::new, CraftEntityTypes.createLiving(EntityType.SHEEP)));
        CraftEntityTypes.register(new EntityTypeData<Cow, CowEntity>(org.bukkit.entity.EntityType.COW, Cow.class, CraftCow::new, CraftEntityTypes.createLiving(EntityType.COW)));
        CraftEntityTypes.register(new EntityTypeData<Chicken, ChickenEntity>(org.bukkit.entity.EntityType.CHICKEN, Chicken.class, CraftChicken::new, CraftEntityTypes.createLiving(EntityType.CHICKEN)));
        CraftEntityTypes.register(new EntityTypeData<Squid, SquidEntity>(org.bukkit.entity.EntityType.SQUID, Squid.class, CraftSquid::new, CraftEntityTypes.createLiving(EntityType.SQUID)));
        CraftEntityTypes.register(new EntityTypeData<Wolf, WolfEntity>(org.bukkit.entity.EntityType.WOLF, Wolf.class, CraftWolf::new, CraftEntityTypes.createLiving(EntityType.WOLF)));
        CraftEntityTypes.register(new EntityTypeData<MushroomCow, MooshroomEntity>(org.bukkit.entity.EntityType.MOOSHROOM, MushroomCow.class, CraftMushroomCow::new, CraftEntityTypes.createLiving(EntityType.MOOSHROOM)));
        CraftEntityTypes.register(new EntityTypeData<Snowman, SnowGolemEntity>(org.bukkit.entity.EntityType.SNOW_GOLEM, Snowman.class, CraftSnowman::new, CraftEntityTypes.createLiving(EntityType.SNOW_GOLEM)));
        CraftEntityTypes.register(new EntityTypeData<Ocelot, OcelotEntity>(org.bukkit.entity.EntityType.OCELOT, Ocelot.class, CraftOcelot::new, CraftEntityTypes.createLiving(EntityType.OCELOT)));
        CraftEntityTypes.register(new EntityTypeData<IronGolem, IronGolemEntity>(org.bukkit.entity.EntityType.IRON_GOLEM, IronGolem.class, CraftIronGolem::new, CraftEntityTypes.createLiving(EntityType.IRON_GOLEM)));
        CraftEntityTypes.register(new EntityTypeData<Horse, HorseEntity>(org.bukkit.entity.EntityType.HORSE, Horse.class, CraftHorse::new, CraftEntityTypes.createLiving(EntityType.HORSE)));
        CraftEntityTypes.register(new EntityTypeData<Rabbit, RabbitEntity>(org.bukkit.entity.EntityType.RABBIT, Rabbit.class, CraftRabbit::new, CraftEntityTypes.createLiving(EntityType.RABBIT)));
        CraftEntityTypes.register(new EntityTypeData<PolarBear, PolarBearEntity>(org.bukkit.entity.EntityType.POLAR_BEAR, PolarBear.class, CraftPolarBear::new, CraftEntityTypes.createLiving(EntityType.POLAR_BEAR)));
        CraftEntityTypes.register(new EntityTypeData<Llama, LlamaEntity>(org.bukkit.entity.EntityType.LLAMA, Llama.class, CraftLlama::new, CraftEntityTypes.createLiving(EntityType.LLAMA)));
        CraftEntityTypes.register(new EntityTypeData<Parrot, ParrotEntity>(org.bukkit.entity.EntityType.PARROT, Parrot.class, CraftParrot::new, CraftEntityTypes.createLiving(EntityType.PARROT)));
        CraftEntityTypes.register(new EntityTypeData<Villager, VillagerEntity>(org.bukkit.entity.EntityType.VILLAGER, Villager.class, CraftVillager::new, CraftEntityTypes.createLiving(EntityType.VILLAGER)));
        CraftEntityTypes.register(new EntityTypeData<Turtle, TurtleEntity>(org.bukkit.entity.EntityType.TURTLE, Turtle.class, CraftTurtle::new, CraftEntityTypes.createLiving(EntityType.TURTLE)));
        CraftEntityTypes.register(new EntityTypeData<Phantom, PhantomEntity>(org.bukkit.entity.EntityType.PHANTOM, Phantom.class, CraftPhantom::new, CraftEntityTypes.createLiving(EntityType.PHANTOM)));
        CraftEntityTypes.register(new EntityTypeData<Cod, CodEntity>(org.bukkit.entity.EntityType.COD, Cod.class, CraftCod::new, CraftEntityTypes.createLiving(EntityType.COD)));
        CraftEntityTypes.register(new EntityTypeData<Salmon, SalmonEntity>(org.bukkit.entity.EntityType.SALMON, Salmon.class, CraftSalmon::new, CraftEntityTypes.createLiving(EntityType.SALMON)));
        CraftEntityTypes.register(new EntityTypeData<PufferFish, PufferfishEntity>(org.bukkit.entity.EntityType.PUFFERFISH, PufferFish.class, CraftPufferFish::new, CraftEntityTypes.createLiving(EntityType.PUFFERFISH)));
        CraftEntityTypes.register(new EntityTypeData<TropicalFish, TropicalFishEntity>(org.bukkit.entity.EntityType.TROPICAL_FISH, TropicalFish.class, CraftTropicalFish::new, CraftEntityTypes.createLiving(EntityType.TROPICAL_FISH)));
        CraftEntityTypes.register(new EntityTypeData<Drowned, DrownedEntity>(org.bukkit.entity.EntityType.DROWNED, Drowned.class, CraftDrowned::new, CraftEntityTypes.createLiving(EntityType.DROWNED)));
        CraftEntityTypes.register(new EntityTypeData<Dolphin, DolphinEntity>(org.bukkit.entity.EntityType.DOLPHIN, Dolphin.class, CraftDolphin::new, CraftEntityTypes.createLiving(EntityType.DOLPHIN)));
        CraftEntityTypes.register(new EntityTypeData<Cat, CatEntity>(org.bukkit.entity.EntityType.CAT, Cat.class, CraftCat::new, CraftEntityTypes.createLiving(EntityType.CAT)));
        CraftEntityTypes.register(new EntityTypeData<Panda, PandaEntity>(org.bukkit.entity.EntityType.PANDA, Panda.class, CraftPanda::new, CraftEntityTypes.createLiving(EntityType.PANDA)));
        CraftEntityTypes.register(new EntityTypeData<Pillager, PillagerEntity>(org.bukkit.entity.EntityType.PILLAGER, Pillager.class, CraftPillager::new, CraftEntityTypes.createLiving(EntityType.PILLAGER)));
        CraftEntityTypes.register(new EntityTypeData<Ravager, RavagerEntity>(org.bukkit.entity.EntityType.RAVAGER, Ravager.class, CraftRavager::new, CraftEntityTypes.createLiving(EntityType.RAVAGER)));
        CraftEntityTypes.register(new EntityTypeData<TraderLlama, TraderLlamaEntity>(org.bukkit.entity.EntityType.TRADER_LLAMA, TraderLlama.class, CraftTraderLlama::new, CraftEntityTypes.createLiving(EntityType.TRADER_LLAMA)));
        CraftEntityTypes.register(new EntityTypeData<WanderingTrader, WanderingTraderEntity>(org.bukkit.entity.EntityType.WANDERING_TRADER, WanderingTrader.class, CraftWanderingTrader::new, CraftEntityTypes.createLiving(EntityType.WANDERING_TRADER)));
        CraftEntityTypes.register(new EntityTypeData<Fox, FoxEntity>(org.bukkit.entity.EntityType.FOX, Fox.class, CraftFox::new, CraftEntityTypes.createLiving(EntityType.FOX)));
        CraftEntityTypes.register(new EntityTypeData<Bee, BeeEntity>(org.bukkit.entity.EntityType.BEE, Bee.class, CraftBee::new, CraftEntityTypes.createLiving(EntityType.BEE)));
        CraftEntityTypes.register(new EntityTypeData<Hoglin, HoglinEntity>(org.bukkit.entity.EntityType.HOGLIN, Hoglin.class, CraftHoglin::new, CraftEntityTypes.createLiving(EntityType.HOGLIN)));
        CraftEntityTypes.register(new EntityTypeData<Piglin, PiglinEntity>(org.bukkit.entity.EntityType.PIGLIN, Piglin.class, CraftPiglin::new, CraftEntityTypes.createLiving(EntityType.PIGLIN)));
        CraftEntityTypes.register(new EntityTypeData<Strider, StriderEntity>(org.bukkit.entity.EntityType.STRIDER, Strider.class, CraftStrider::new, CraftEntityTypes.createLiving(EntityType.STRIDER)));
        CraftEntityTypes.register(new EntityTypeData<Zoglin, ZoglinEntity>(org.bukkit.entity.EntityType.ZOGLIN, Zoglin.class, CraftZoglin::new, CraftEntityTypes.createLiving(EntityType.ZOGLIN)));
        CraftEntityTypes.register(new EntityTypeData<PiglinBrute, PiglinBruteEntity>(org.bukkit.entity.EntityType.PIGLIN_BRUTE, PiglinBrute.class, CraftPiglinBrute::new, CraftEntityTypes.createLiving(EntityType.PIGLIN_BRUTE)));
        CraftEntityTypes.register(new EntityTypeData<Axolotl, AxolotlEntity>(org.bukkit.entity.EntityType.AXOLOTL, Axolotl.class, CraftAxolotl::new, CraftEntityTypes.createLiving(EntityType.AXOLOTL)));
        CraftEntityTypes.register(new EntityTypeData<GlowSquid, GlowSquidEntity>(org.bukkit.entity.EntityType.GLOW_SQUID, GlowSquid.class, CraftGlowSquid::new, CraftEntityTypes.createLiving(EntityType.GLOW_SQUID)));
        CraftEntityTypes.register(new EntityTypeData<Goat, GoatEntity>(org.bukkit.entity.EntityType.GOAT, Goat.class, CraftGoat::new, CraftEntityTypes.createLiving(EntityType.GOAT)));
        CraftEntityTypes.register(new EntityTypeData<Allay, AllayEntity>(org.bukkit.entity.EntityType.ALLAY, Allay.class, CraftAllay::new, CraftEntityTypes.createLiving(EntityType.ALLAY)));
        CraftEntityTypes.register(new EntityTypeData<Frog, FrogEntity>(org.bukkit.entity.EntityType.FROG, Frog.class, CraftFrog::new, CraftEntityTypes.createLiving(EntityType.FROG)));
        CraftEntityTypes.register(new EntityTypeData<Tadpole, TadpoleEntity>(org.bukkit.entity.EntityType.TADPOLE, Tadpole.class, CraftTadpole::new, CraftEntityTypes.createLiving(EntityType.TADPOLE)));
        CraftEntityTypes.register(new EntityTypeData<Warden, WardenEntity>(org.bukkit.entity.EntityType.WARDEN, Warden.class, CraftWarden::new, CraftEntityTypes.createLiving(EntityType.WARDEN)));
        CraftEntityTypes.register(new EntityTypeData<Camel, CamelEntity>(org.bukkit.entity.EntityType.CAMEL, Camel.class, CraftCamel::new, CraftEntityTypes.createLiving(EntityType.CAMEL)));
        CraftEntityTypes.register(new EntityTypeData<Sniffer, SnifferEntity>(org.bukkit.entity.EntityType.SNIFFER, Sniffer.class, CraftSniffer::new, CraftEntityTypes.createLiving(EntityType.SNIFFER)));
        CraftEntityTypes.register(new EntityTypeData<Breeze, BreezeEntity>(org.bukkit.entity.EntityType.BREEZE, Breeze.class, CraftBreeze::new, CraftEntityTypes.createLiving(EntityType.BREEZE)));
        CraftEntityTypes.register(new EntityTypeData<Armadillo, ArmadilloEntity>(org.bukkit.entity.EntityType.ARMADILLO, Armadillo.class, CraftArmadillo::new, CraftEntityTypes.createLiving(EntityType.ARMADILLO)));
        Function<SpawnData, EnderDragonEntity> dragonFunction = CraftEntityTypes.createLiving(EntityType.ENDER_DRAGON);
        CraftEntityTypes.register(new EntityTypeData<EnderDragon, EnderDragonEntity>(org.bukkit.entity.EntityType.ENDER_DRAGON, EnderDragon.class, CraftEnderDragon::new, spawnData -> {
            Preconditions.checkArgument((boolean)spawnData.normalWorld(), (String)"Cannot spawn entity %s during world generation", (Object)EnderDragon.class.getName());
            return (EnderDragonEntity)dragonFunction.apply((SpawnData)spawnData);
        }));
        CraftEntityTypes.register(new EntityTypeData<LargeFireball, FireballEntity>(org.bukkit.entity.EntityType.FIREBALL, LargeFireball.class, CraftLargeFireball::new, CraftEntityTypes.createFireball(EntityType.FIREBALL)));
        CraftEntityTypes.register(new EntityTypeData<SmallFireball, SmallFireballEntity>(org.bukkit.entity.EntityType.SMALL_FIREBALL, SmallFireball.class, CraftSmallFireball::new, CraftEntityTypes.createFireball(EntityType.SMALL_FIREBALL)));
        CraftEntityTypes.register(new EntityTypeData<WitherSkull, WitherSkullEntity>(org.bukkit.entity.EntityType.WITHER_SKULL, WitherSkull.class, CraftWitherSkull::new, CraftEntityTypes.createFireball(EntityType.WITHER_SKULL)));
        CraftEntityTypes.register(new EntityTypeData<DragonFireball, DragonFireballEntity>(org.bukkit.entity.EntityType.DRAGON_FIREBALL, DragonFireball.class, CraftDragonFireball::new, CraftEntityTypes.createFireball(EntityType.DRAGON_FIREBALL)));
        CraftEntityTypes.register(new EntityTypeData<WindCharge, WindChargeEntity>(org.bukkit.entity.EntityType.WIND_CHARGE, WindCharge.class, CraftWindCharge::new, CraftEntityTypes.createFireball(EntityType.WIND_CHARGE)));
        CraftEntityTypes.register(new EntityTypeData<BreezeWindCharge, BreezeWindChargeEntity>(org.bukkit.entity.EntityType.BREEZE_WIND_CHARGE, BreezeWindCharge.class, CraftBreezeWindCharge::new, CraftEntityTypes.createFireball(EntityType.BREEZE_WIND_CHARGE)));
        CraftEntityTypes.register(new EntityTypeData<Painting, PaintingEntity>(org.bukkit.entity.EntityType.PAINTING, Painting.class, CraftPainting::new, CraftEntityTypes.createHanging(Painting.class, (spawnData, hangingData) -> {
            PaintingEntity entity;
            if (spawnData.normalWorld && hangingData.randomize() && (entity = (PaintingEntity)PaintingEntity.placePainting(spawnData.minecraftWorld(), hangingData.position(), hangingData.direction()).orElse(null)) != null) {
                return entity;
            }
            entity = new PaintingEntity((EntityType<? extends PaintingEntity>)EntityType.PAINTING, spawnData.minecraftWorld());
            entity.updatePositionAndAngles(spawnData.x(), spawnData.y(), spawnData.z(), spawnData.yaw(), spawnData.pitch());
            entity.setFacing(hangingData.direction());
            return entity;
        })));
        CraftEntityTypes.register(new EntityTypeData<ItemFrame, ItemFrameEntity>(org.bukkit.entity.EntityType.ITEM_FRAME, ItemFrame.class, CraftItemFrame::new, CraftEntityTypes.createHanging(ItemFrame.class, (spawnData, hangingData) -> new ItemFrameEntity(spawnData.minecraftWorld(), hangingData.position(), hangingData.direction()))));
        CraftEntityTypes.register(new EntityTypeData<GlowItemFrame, GlowItemFrameEntity>(org.bukkit.entity.EntityType.GLOW_ITEM_FRAME, GlowItemFrame.class, CraftGlowItemFrame::new, CraftEntityTypes.createHanging(GlowItemFrame.class, (spawnData, hangingData) -> new GlowItemFrameEntity(spawnData.minecraftWorld(), hangingData.position(), hangingData.direction()))));
        CraftEntityTypes.register(new EntityTypeData<Arrow, ArrowEntity>(org.bukkit.entity.EntityType.ARROW, Arrow.class, CraftArrow::new, CraftEntityTypes.createAndMoveEmptyRot(EntityType.ARROW)));
        CraftEntityTypes.register(new EntityTypeData<EnderPearl, EnderPearlEntity>(org.bukkit.entity.EntityType.ENDER_PEARL, EnderPearl.class, CraftEnderPearl::new, CraftEntityTypes.createAndMoveEmptyRot(EntityType.ENDER_PEARL)));
        CraftEntityTypes.register(new EntityTypeData<ThrownExpBottle, ExperienceBottleEntity>(org.bukkit.entity.EntityType.EXPERIENCE_BOTTLE, ThrownExpBottle.class, CraftThrownExpBottle::new, CraftEntityTypes.createAndMoveEmptyRot(EntityType.EXPERIENCE_BOTTLE)));
        CraftEntityTypes.register(new EntityTypeData<SpectralArrow, SpectralArrowEntity>(org.bukkit.entity.EntityType.SPECTRAL_ARROW, SpectralArrow.class, CraftSpectralArrow::new, CraftEntityTypes.createAndMoveEmptyRot(EntityType.SPECTRAL_ARROW)));
        CraftEntityTypes.register(new EntityTypeData<EnderCrystal, EndCrystalEntity>(org.bukkit.entity.EntityType.END_CRYSTAL, EnderCrystal.class, CraftEnderCrystal::new, CraftEntityTypes.createAndMoveEmptyRot(EntityType.END_CRYSTAL)));
        CraftEntityTypes.register(new EntityTypeData<Trident, TridentEntity>(org.bukkit.entity.EntityType.TRIDENT, Trident.class, CraftTrident::new, CraftEntityTypes.createAndMoveEmptyRot(EntityType.TRIDENT)));
        CraftEntityTypes.register(new EntityTypeData<LightningStrike, LightningEntity>(org.bukkit.entity.EntityType.LIGHTNING_BOLT, LightningStrike.class, CraftLightningStrike::new, CraftEntityTypes.createAndMoveEmptyRot(EntityType.LIGHTNING_BOLT)));
        CraftEntityTypes.register(new EntityTypeData<ShulkerBullet, ShulkerBulletEntity>(org.bukkit.entity.EntityType.SHULKER_BULLET, ShulkerBullet.class, CraftShulkerBullet::new, CraftEntityTypes.createAndMove(EntityType.SHULKER_BULLET)));
        CraftEntityTypes.register(new EntityTypeData<Boat, BoatEntity>(org.bukkit.entity.EntityType.BOAT, Boat.class, CraftBoat::new, CraftEntityTypes.createAndMove(EntityType.BOAT)));
        CraftEntityTypes.register(new EntityTypeData<LlamaSpit, LlamaSpitEntity>(org.bukkit.entity.EntityType.LLAMA_SPIT, LlamaSpit.class, CraftLlamaSpit::new, CraftEntityTypes.createAndMove(EntityType.LLAMA_SPIT)));
        CraftEntityTypes.register(new EntityTypeData<ChestBoat, ChestBoatEntity>(org.bukkit.entity.EntityType.CHEST_BOAT, ChestBoat.class, CraftChestBoat::new, CraftEntityTypes.createAndMove(EntityType.CHEST_BOAT)));
        CraftEntityTypes.register(new EntityTypeData<OminousItemSpawner, OminousItemSpawnerEntity>(org.bukkit.entity.EntityType.OMINOUS_ITEM_SPAWNER, OminousItemSpawner.class, CraftOminousItemSpawner::new, CraftEntityTypes.createAndMove(EntityType.OMINOUS_ITEM_SPAWNER)));
        CraftEntityTypes.register(new EntityTypeData<Marker, MarkerEntity>(org.bukkit.entity.EntityType.MARKER, Marker.class, CraftMarker::new, CraftEntityTypes.createAndSetPos(EntityType.MARKER)));
        CraftEntityTypes.register(new EntityTypeData<BlockDisplay, Entity>(org.bukkit.entity.EntityType.BLOCK_DISPLAY, BlockDisplay.class, CraftBlockDisplay::new, CraftEntityTypes.combine(CraftEntityTypes.createAndSetPos(EntityType.BLOCK_DISPLAY), ROT)));
        CraftEntityTypes.register(new EntityTypeData<Interaction, InteractionEntity>(org.bukkit.entity.EntityType.INTERACTION, Interaction.class, CraftInteraction::new, CraftEntityTypes.createAndSetPos(EntityType.INTERACTION)));
        CraftEntityTypes.register(new EntityTypeData<ItemDisplay, Entity>(org.bukkit.entity.EntityType.ITEM_DISPLAY, ItemDisplay.class, CraftItemDisplay::new, CraftEntityTypes.combine(CraftEntityTypes.createAndSetPos(EntityType.ITEM_DISPLAY), ROT)));
        CraftEntityTypes.register(new EntityTypeData<TextDisplay, Entity>(org.bukkit.entity.EntityType.TEXT_DISPLAY, TextDisplay.class, CraftTextDisplay::new, CraftEntityTypes.combine(CraftEntityTypes.createAndSetPos(EntityType.TEXT_DISPLAY), ROT)));
        CraftEntityTypes.register(new EntityTypeData<Item, ItemEntity>(org.bukkit.entity.EntityType.ITEM, Item.class, CraftItem::new, spawnData -> {
            net.minecraft.item.ItemStack itemStack = new net.minecraft.item.ItemStack(Items.STONE);
            ItemEntity item = new ItemEntity(spawnData.minecraftWorld(), spawnData.x(), spawnData.y(), spawnData.z(), itemStack);
            item.setPickupDelay(10);
            CLEAR_MOVE_IF_NOT_RANDOMIZED.accept((SpawnData)spawnData, item);
            return item;
        }));
        CraftEntityTypes.register(new EntityTypeData<ExperienceOrb, ExperienceOrbEntity>(org.bukkit.entity.EntityType.EXPERIENCE_ORB, ExperienceOrb.class, CraftExperienceOrb::new, CraftEntityTypes.combine(CraftEntityTypes.combine(spawnData -> new ExperienceOrbEntity(spawnData.minecraftWorld(), spawnData.x(), spawnData.y(), spawnData.z(), 0, ExperienceOrb.SpawnReason.CUSTOM, null, null), CLEAR_MOVE_IF_NOT_RANDOMIZED), (spawnData, experienceOrb) -> {
            if (!spawnData.randomizeData()) {
                experienceOrb.setYaw(0.0f);
            }
        })));
        CraftEntityTypes.register(new EntityTypeData<AreaEffectCloud, AreaEffectCloudEntity>(org.bukkit.entity.EntityType.AREA_EFFECT_CLOUD, AreaEffectCloud.class, CraftAreaEffectCloud::new, CraftEntityTypes.createAndMove(EntityType.AREA_EFFECT_CLOUD)));
        CraftEntityTypes.register(new EntityTypeData<Egg, EggEntity>(org.bukkit.entity.EntityType.EGG, Egg.class, CraftEgg::new, spawnData -> new EggEntity(spawnData.minecraftWorld(), spawnData.x(), spawnData.y(), spawnData.z())));
        CraftEntityTypes.register(new EntityTypeData<LeashHitch, LeashKnotEntity>(org.bukkit.entity.EntityType.LEASH_KNOT, LeashHitch.class, CraftLeash::new, spawnData -> new LeashKnotEntity(spawnData.minecraftWorld(), BlockPos.ofFloored(spawnData.x(), spawnData.y(), spawnData.z()))));
        CraftEntityTypes.register(new EntityTypeData<Snowball, SnowballEntity>(org.bukkit.entity.EntityType.SNOWBALL, Snowball.class, CraftSnowball::new, spawnData -> new SnowballEntity(spawnData.minecraftWorld(), spawnData.x(), spawnData.y(), spawnData.z())));
        CraftEntityTypes.register(new EntityTypeData<EnderSignal, EyeOfEnderEntity>(org.bukkit.entity.EntityType.EYE_OF_ENDER, EnderSignal.class, CraftEnderSignal::new, spawnData -> new EyeOfEnderEntity(spawnData.minecraftWorld(), spawnData.x(), spawnData.y(), spawnData.z())));
        CraftEntityTypes.register(new EntityTypeData<ThrownPotion, PotionEntity>(org.bukkit.entity.EntityType.POTION, ThrownPotion.class, CraftThrownPotion::new, spawnData -> {
            PotionEntity entity = new PotionEntity(spawnData.minecraftWorld(), spawnData.x(), spawnData.y(), spawnData.z());
            entity.setItem(CraftItemStack.asNMSCopy(new ItemStack(Material.SPLASH_POTION, 1)));
            return entity;
        }));
        CraftEntityTypes.register(new EntityTypeData<TNTPrimed, Entity>(org.bukkit.entity.EntityType.TNT, TNTPrimed.class, CraftTNTPrimed::new, CraftEntityTypes.combine(spawnData -> new TntEntity(spawnData.minecraftWorld(), spawnData.x(), spawnData.y(), spawnData.z(), null), CLEAR_MOVE_IF_NOT_RANDOMIZED)));
        CraftEntityTypes.register(new EntityTypeData<FallingBlock, FallingBlockEntity>(org.bukkit.entity.EntityType.FALLING_BLOCK, FallingBlock.class, CraftFallingBlock::new, spawnData -> {
            BlockPos pos = BlockPos.ofFloored(spawnData.x(), spawnData.y(), spawnData.z());
            return new FallingBlockEntity(spawnData.minecraftWorld(), spawnData.x(), spawnData.y(), spawnData.z(), spawnData.world().getBlockState(pos));
        }));
        CraftEntityTypes.register(new EntityTypeData<Firework, FireworkRocketEntity>(org.bukkit.entity.EntityType.FIREWORK_ROCKET, Firework.class, CraftFirework::new, spawnData -> {
            FireworkRocketEntity entity = new FireworkRocketEntity(spawnData.minecraftWorld(), spawnData.x(), spawnData.y(), spawnData.z(), net.minecraft.item.ItemStack.EMPTY);
            if (!spawnData.randomizeData()) {
                entity.setVelocity(0.0, 0.05, 0.0);
                entity.lifeTime = 16;
            }
            return entity;
        }));
        CraftEntityTypes.register(new EntityTypeData<EvokerFangs, EvokerFangsEntity>(org.bukkit.entity.EntityType.EVOKER_FANGS, EvokerFangs.class, CraftEvokerFangs::new, spawnData -> new EvokerFangsEntity(spawnData.minecraftWorld(), spawnData.x(), spawnData.y(), spawnData.z(), (float)Math.toRadians(spawnData.yaw()), 0, null)));
        CraftEntityTypes.register(new EntityTypeData<CommandMinecart, CommandBlockMinecartEntity>(org.bukkit.entity.EntityType.COMMAND_BLOCK_MINECART, CommandMinecart.class, CraftMinecartCommand::new, spawnData -> new CommandBlockMinecartEntity(spawnData.minecraftWorld(), spawnData.x(), spawnData.y(), spawnData.z())));
        CraftEntityTypes.register(new EntityTypeData<RideableMinecart, MinecartEntity>(org.bukkit.entity.EntityType.MINECART, RideableMinecart.class, CraftMinecartRideable::new, spawnData -> new MinecartEntity(spawnData.minecraftWorld(), spawnData.x(), spawnData.y(), spawnData.z())));
        CraftEntityTypes.register(new EntityTypeData<StorageMinecart, ChestMinecartEntity>(org.bukkit.entity.EntityType.CHEST_MINECART, StorageMinecart.class, CraftMinecartChest::new, spawnData -> new ChestMinecartEntity(spawnData.minecraftWorld(), spawnData.x(), spawnData.y(), spawnData.z())));
        CraftEntityTypes.register(new EntityTypeData<PoweredMinecart, FurnaceMinecartEntity>(org.bukkit.entity.EntityType.FURNACE_MINECART, PoweredMinecart.class, CraftMinecartFurnace::new, spawnData -> new FurnaceMinecartEntity(spawnData.minecraftWorld(), spawnData.x(), spawnData.y(), spawnData.z())));
        CraftEntityTypes.register(new EntityTypeData<ExplosiveMinecart, TntMinecartEntity>(org.bukkit.entity.EntityType.TNT_MINECART, ExplosiveMinecart.class, CraftMinecartTNT::new, spawnData -> new TntMinecartEntity(spawnData.minecraftWorld(), spawnData.x(), spawnData.y(), spawnData.z())));
        CraftEntityTypes.register(new EntityTypeData<HopperMinecart, HopperMinecartEntity>(org.bukkit.entity.EntityType.HOPPER_MINECART, HopperMinecart.class, CraftMinecartHopper::new, spawnData -> new HopperMinecartEntity(spawnData.minecraftWorld(), spawnData.x(), spawnData.y(), spawnData.z())));
        CraftEntityTypes.register(new EntityTypeData<SpawnerMinecart, SpawnerMinecartEntity>(org.bukkit.entity.EntityType.SPAWNER_MINECART, SpawnerMinecart.class, CraftMinecartMobSpawner::new, spawnData -> new SpawnerMinecartEntity(spawnData.minecraftWorld(), spawnData.x(), spawnData.y(), spawnData.z())));
        CraftEntityTypes.register(new EntityTypeData<FishHook, FishingBobberEntity>(org.bukkit.entity.EntityType.FISHING_BOBBER, FishHook.class, CraftFishHook::new, null));
        CraftEntityTypes.register(new EntityTypeData<Player, ServerPlayerEntity>(org.bukkit.entity.EntityType.PLAYER, Player.class, CraftPlayer::new, null));
    	*/
    }

    @Desugar
    public record EntityTypeData<E extends org.bukkit.entity.Entity, M extends Entity>(org.bukkit.entity.EntityType entityType, Class<E> entityClass, BiFunction<CraftServer, M, E> convertFunction, Function<SpawnData, M> spawnFunction) {
    }

    @Desugar
    public record SpawnData(StructureWorldAccess world, Location location, boolean randomizeData, boolean normalWorld) {
        double x() {
            return this.location().getX();
        }

        double y() {
            return this.location().getY();
        }

        double z() {
            return this.location().getZ();
        }

        float yaw() {
            return this.location().getYaw();
        }

        float pitch() {
            return this.location().getPitch();
        }

        World minecraftWorld() {
            return (ServerWorld) this.world();
        	//return this.world().getMinecraftWorld();
        }
    }

    @Desugar
    private record HangingData(boolean randomize, BlockPos position, Direction direction) {
    }
}

