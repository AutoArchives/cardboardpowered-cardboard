package org.cardboardpowered.mixin.inventory;

import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import org.bukkit.Location;
import org.bukkit.inventory.InventoryHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import org.cardboardpowered.interfaces.IMixinWorld;

@Mixin(PlayerEnderChestContainer.class)
public class MixinEnderChestInventory extends MixinSimpleInventory {

    @Shadow private EnderChestBlockEntity activeChest;

    public InventoryHolder getBukkitOwner() {
        return null; // TODO
    }

    @Override
    public Location getLocation() {
        return new Location(((IMixinWorld)this.activeChest.getLevel()).getCraftWorld(), this.activeChest.getBlockPos().getX(), this.activeChest.getBlockPos().getY(), this.activeChest.getBlockPos().getZ());
    }

}