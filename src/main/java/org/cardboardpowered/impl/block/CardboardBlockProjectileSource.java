package org.cardboardpowered.impl.block;

import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.util.math.BlockPointer;

import org.bukkit.block.Block;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.BlockProjectileSource;
// import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

import com.javazilla.bukkitfabric.interfaces.IMixinWorld;


import com.google.common.base.Preconditions;

import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ProjectileItem;

import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LingeringPotion;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.WindCharge;

public class CardboardBlockProjectileSource implements BlockProjectileSource {

    private final DispenserBlockEntity dispenserBlock;

    public CardboardBlockProjectileSource(DispenserBlockEntity dispenserBlock) {
        this.dispenserBlock = dispenserBlock;
    }

    @Override
    public Block getBlock() {
        return ((IMixinWorld)(Object)dispenserBlock.getWorld()).getWorldImpl().getBlockAt(dispenserBlock.getPos().getX(), dispenserBlock.getPos().getY(), dispenserBlock.getPos().getZ());
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> projectile) {
        return this.launchProjectile(projectile, null);
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> projectile, Vector velocity) {
        return this.launchProjectile(projectile, velocity, null);
    }

    // @Override
    /*
    @Deprecated
    public <T extends Projectile> T launchProjectile_old(Class<? extends T> projectile, Vector velocity, Consumer<T> function) {
		return null;
    
    }*/
    
    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> projectile, Vector velocity, Consumer<? super T> function) {
        Preconditions.checkArgument((this.getBlock().getType() == Material.DISPENSER ? 1 : 0) != 0, (Object)"Block is no longer dispenser");
        Item item = null;
        if (Snowball.class.isAssignableFrom(projectile)) {
            item = Items.SNOWBALL;
        } else if (Egg.class.isAssignableFrom(projectile)) {
            item = Items.EGG;
        } else if (ThrownExpBottle.class.isAssignableFrom(projectile)) {
            item = Items.EXPERIENCE_BOTTLE;
        } else if (ThrownPotion.class.isAssignableFrom(projectile)) {
            item = LingeringPotion.class.isAssignableFrom(projectile) ? Items.LINGERING_POTION : Items.SPLASH_POTION;
        } else if (AbstractArrow.class.isAssignableFrom(projectile)) {
            item = SpectralArrow.class.isAssignableFrom(projectile) ? Items.SPECTRAL_ARROW : Items.ARROW;
        } else if (WindCharge.class.isAssignableFrom(projectile)) {
            item = Items.WIND_CHARGE;
        } else if (Firework.class.isAssignableFrom(projectile)) {
            item = Items.FIREWORK_ROCKET;
        } else if (SmallFireball.class.isAssignableFrom(projectile)) {
            item = Items.FIRE_CHARGE;
        }
        if (!(item instanceof ProjectileItem)) {
            throw new IllegalArgumentException("Projectile '%s' is not supported".formatted(projectile.getSimpleName()));
        }
        ProjectileItem projectileItem = (ProjectileItem)((Object)item);
        ProjectileItem.Settings config = projectileItem.getProjectileSettings();
        BlockState state = this.dispenserBlock.getCachedState();
        World world = this.dispenserBlock.getWorld();
        BlockPointer pointer = new BlockPointer((ServerWorld)world, this.dispenserBlock.getPos(), state, this.dispenserBlock);
        Direction facing = state.get(DispenserBlock.FACING);
        Position pos = config.positionFunction().getDispensePosition(pointer, facing);
        ProjectileEntity launch = projectileItem.createEntity(world, pos, new net.minecraft.item.ItemStack(item), facing);
        projectileItem.initializeProjectile(launch, facing.getOffsetX(), facing.getOffsetY(), facing.getOffsetZ(), config.power(), config.uncertainty());

        launch.setProjectileSourceBukkit(this);
        
        if (velocity != null) {
            ((Projectile)launch.getBukkitEntity()).setVelocity(velocity);
        }
        if (function != null) {
            function.accept((T) (Projectile) launch.getBukkitEntity());
        }
        world.spawnEntity(launch);
        return (T)((Projectile)launch.getBukkitEntity());
    }

	
	
}