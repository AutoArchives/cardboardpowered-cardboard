package org.cardboardpowered.impl.block;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Jukebox;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.inventory.JukeboxInventory;
import org.cardboardpowered.impl.inventory.CraftInventoryJukebox;
import org.cardboardpowered.impl.world.CraftWorld;
import org.jetbrains.annotations.NotNull;

public class CardboardJukebox extends CardboardBlockEntityState<JukeboxBlockEntity> implements Jukebox {

    public CardboardJukebox(World world, JukeboxBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CardboardJukebox(CardboardJukebox state, Location location) {
        super(state, location);
    }
    
    @Override
    public CardboardJukebox copy() {
        return new CardboardJukebox(this, null);
    }

    @Override
    public CardboardJukebox copy(Location location) {
        return new CardboardJukebox(this, location);
    }


    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);
        if (result && this.isPlaced() && this.getType() == Material.JUKEBOX) {
            CraftWorld world = (CraftWorld) this.getWorld();
            Material record = this.getPlaying();
            world.getHandle().setBlock(this.getPosition(), Blocks.JUKEBOX.defaultBlockState().setValue(JukeboxBlock.HAS_RECORD, !(record == Material.AIR)), 3);
            world.playEffect(this.getLocation(), Effect.RECORD_PLAY, record);
        }
        return result;
    }

    @Override
    public Material getPlaying() {
        return getRecord().getType();
    }

    @Override
    public void setPlaying(Material record) {
        if (record == null || CraftMagicNumbers.getItem(record) == null) record = Material.AIR;
        setRecord(new org.bukkit.inventory.ItemStack(record));
    }

    @Override
    public org.bukkit.inventory.ItemStack getRecord() {
        ItemStack record = this.getSnapshot().getTheItem();
        return CraftItemStack.asBukkitCopy(record);
    }

    @Override
    public void setRecord(org.bukkit.inventory.ItemStack record) {
        ItemStack nms = CraftItemStack.asNMSCopy(record);
        this.getSnapshot().setTheItem(nms);
        this.data = this.data.setValue(JukeboxBlock.HAS_RECORD, !nms.isEmpty());
    }

    @Override
    public boolean isPlaying() {
        return getHandle().getValue(JukeboxBlock.HAS_RECORD);
    }

    @Override
    public boolean eject() {
        requirePlaced();
        BlockEntity tileEntity = this.getTileEntityFromWorld();
        if (!(tileEntity instanceof JukeboxBlockEntity)) return false;

        JukeboxBlockEntity jukebox = (JukeboxBlockEntity) tileEntity;
        boolean result = !jukebox.getTheItem().isEmpty();
        jukebox.popOutTheItem();
        
        return result;
    }

    @Override
    public void stopPlaying() {
        this.requirePlaced();
        BlockEntity tileEntity = this.getTileEntityFromWorld();
        if (!(tileEntity instanceof JukeboxBlockEntity)) {
            return;
        }
        JukeboxBlockEntity jukebox = (JukeboxBlockEntity)tileEntity;
        jukebox.getSongPlayer().stop(tileEntity.getLevel(), tileEntity.getBlockState());
    }

	@Override
	public @NotNull JukeboxInventory getInventory() {
        if (!this.isPlaced()) {
            return this.getSnapshotInventory();
        }
        return new CraftInventoryJukebox((Container)this.getTileEntity());
	}

	@Override
	public @NotNull JukeboxInventory getSnapshotInventory() {
        return new CraftInventoryJukebox((Container)this.getSnapshot());
	}

	@Override
	public boolean hasRecord() {
        return this.getHandle().getValue(JukeboxBlock.HAS_RECORD) != false && !this.getPlaying().isAir();
	}

	@Override
	public boolean startPlaying() {
        this.requirePlaced();
        BlockEntity tileEntity = this.getTileEntityFromWorld();
        if (!(tileEntity instanceof JukeboxBlockEntity)) {
            return false;
        }
        JukeboxBlockEntity jukebox = (JukeboxBlockEntity)tileEntity;
        net.minecraft.world.item.ItemStack record = jukebox.getTheItem();
        if (record.isEmpty() || this.isPlaying()) {
            return false;
        }
        jukebox.tryForcePlaySong();
        return true;
	}

}