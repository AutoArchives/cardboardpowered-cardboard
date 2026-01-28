package org.cardboardpowered.impl.block;

import org.bukkit.block.Block;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.BlockProjectileSource;
// import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;
import org.cardboardpowered.bridge.world.level.LevelBridge;


import com.google.common.base.Preconditions;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
        return ((LevelBridge)(Object)dispenserBlock.getLevel()).getCraftWorld().getBlockAt(dispenserBlock.getBlockPos().getX(), dispenserBlock.getBlockPos().getY(), dispenserBlock.getBlockPos().getZ());
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
        ProjectileItem.DispenseConfig config = projectileItem.createDispenseConfig();
        BlockState state = this.dispenserBlock.getBlockState();
        Level world = this.dispenserBlock.getLevel();
        BlockSource pointer = new BlockSource((ServerLevel)world, this.dispenserBlock.getBlockPos(), state, this.dispenserBlock);
        Direction facing = state.getValue(DispenserBlock.FACING);
        Position pos = config.positionFunction().getDispensePosition(pointer, facing);
        net.minecraft.world.entity.projectile.Projectile launch = projectileItem.asProjectile(world, pos, new net.minecraft.world.item.ItemStack(item), facing);
        projectileItem.shoot(launch, facing.getStepX(), facing.getStepY(), facing.getStepZ(), config.power(), config.uncertainty());

        launch.setProjectileSourceBukkit(this);
        
        if (velocity != null) {
            ((Projectile)launch.getBukkitEntity()).setVelocity(velocity);
        }
        if (function != null) {
            function.accept((T) (Projectile) launch.getBukkitEntity());
        }
        world.addFreshEntity(launch);
        return (T)((Projectile)launch.getBukkitEntity());
    }

	
	
}