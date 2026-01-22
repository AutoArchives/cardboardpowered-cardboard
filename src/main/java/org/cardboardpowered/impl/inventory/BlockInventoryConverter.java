package org.cardboardpowered.impl.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.SmokerBlockEntity;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.cardboardpowered.impl.inventory.CardboardBrewerInventory;
import org.cardboardpowered.impl.inventory.CardboardFurnaceInventory;

public abstract class BlockInventoryConverter implements InventoryCreator.InventoryConverter {

    public abstract Container getTileEntity();

    @Override
    public CraftInventory createInventory(InventoryHolder holder, InventoryType type) {
        return getInventory(getTileEntity());
    }

    @Override
    public CraftInventory createInventory(InventoryHolder holder, InventoryType type, String title) {
        Container inventory = getTileEntity();
        if (inventory instanceof RandomizableContainerBlockEntity)
            ((RandomizableContainerBlockEntity) inventory).name = (CraftChatMessage.fromStringOrNull(title));
        return getInventory(inventory);
    }

    public CraftInventory getInventory(Container tileEntity) {
        return new CraftInventory(tileEntity);
    }

    public static class Furnace extends BlockInventoryConverter {

        @Override
        public Container getTileEntity() {
            AbstractFurnaceBlockEntity furnace = new FurnaceBlockEntity(BlockPos.ZERO, null);
            furnace.setLevel(CraftServer.server.getLevel(Level.OVERWORLD));
            return furnace;
        }

        @Override
        public CraftInventory createInventory(InventoryHolder owner, InventoryType type, String title) {
            Container tileEntity = getTileEntity();
            ((AbstractFurnaceBlockEntity) tileEntity).name = (CraftChatMessage.fromStringOrNull(title));
            return getInventory(tileEntity);
        }

        @Override
        public CraftInventory getInventory(Container tileEntity) {
            return new CardboardFurnaceInventory((AbstractFurnaceBlockEntity) tileEntity);
        }
    }

    public static class BrewingStand extends BlockInventoryConverter {

        @Override
        public Container getTileEntity() {
            return new BrewingStandBlockEntity(BlockPos.ZERO, null);
        }

        @Override
        public CraftInventory createInventory(InventoryHolder holder, InventoryType type, String title) {
            Container tileEntity = getTileEntity();
            if (tileEntity instanceof BrewingStandBlockEntity)
                ((BrewingStandBlockEntity) tileEntity).name = (CraftChatMessage.fromStringOrNull(title));
            return getInventory(tileEntity);
        }

        @Override
        public CraftInventory getInventory(Container tileEntity) {
            return new CardboardBrewerInventory(tileEntity);
        }
    }

    public static class Dispenser extends BlockInventoryConverter {
        @Override
        public Container getTileEntity() {
            return new DispenserBlockEntity(BlockPos.ZERO, null);
        }
    }

    public static class Dropper extends BlockInventoryConverter {
        @Override
        public Container getTileEntity() {
            return new DropperBlockEntity(BlockPos.ZERO, null);
        }
    }

    public static class Hopper extends BlockInventoryConverter {
        @Override
        public Container getTileEntity() {
            return new HopperBlockEntity(BlockPos.ZERO, null);
        }
    }

    public static class BlastFurnace extends BlockInventoryConverter {
        @Override
        public Container getTileEntity() {
            return new BlastFurnaceBlockEntity(BlockPos.ZERO, null);
        }
    }

    public static class Lectern extends BlockInventoryConverter {
        @Override
        public Container getTileEntity() {
            return new LecternBlockEntity(BlockPos.ZERO, null).bookAccess;
        }
    }

    public static class Smoker extends BlockInventoryConverter {
        @Override
        public Container getTileEntity() {
            return new SmokerBlockEntity(BlockPos.ZERO, null);
        }
    }

}