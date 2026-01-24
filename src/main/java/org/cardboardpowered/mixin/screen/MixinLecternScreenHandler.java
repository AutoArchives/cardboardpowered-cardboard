package org.cardboardpowered.mixin.screen;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.cardboardpowered.impl.inventory.CardboardInventoryView;
import org.cardboardpowered.impl.inventory.CardboardLecternInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.cardboardpowered.interfaces.IMixinEntity;

@Mixin(LecternMenu.class)
public class MixinLecternScreenHandler extends MixinScreenHandler {

    @Shadow
    public Container lectern;

    @Shadow
    public ContainerData lecternData;

    private CardboardInventoryView bukkitEntity = null;
    private Player player;

    @Inject(method = "<init>(ILnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;)V", at = @At("TAIL"))
    public void setPlayerInv(int i, Container iinventory, ContainerData icontainerproperties, CallbackInfo ci) {
        this.player = (Player)((IMixinEntity)((Inventory)iinventory).player).getBukkitEntity();
    }

    @Override
    public CardboardInventoryView getBukkitView() {
        if (bukkitEntity != null) return bukkitEntity;

        CardboardLecternInventory inventory = new CardboardLecternInventory(this.lectern);
        bukkitEntity = new CardboardInventoryView(this.player, inventory, (LecternMenu)(Object)this);
        return bukkitEntity;
    }

    /**
     * @reason .
     * @author .
     */
    @Overwrite
    public boolean clickMenuButton(net.minecraft.world.entity.player.Player entityhuman, int i) {
        int j;

        if (i >= 100) {
            j = i - 100;
            ((LecternMenu)(Object)this).setData(0, j);
            return true;
        } else {
            switch (i) {
                case 1:
                    j = this.lecternData.get(0);
                    ((LecternMenu)(Object)this).setData(0, j - 1);
                    return true;
                case 2:
                    j = this.lecternData.get(0);
                    ((LecternMenu)(Object)this).setData(0, j + 1);
                    return true;
                case 3:
                    if (!entityhuman.mayBuild()) return false;

                    PlayerTakeLecternBookEvent event = new PlayerTakeLecternBookEvent(player, ((CardboardLecternInventory) getBukkitView().getTopInventory()).getHolder());
                    Bukkit.getServer().getPluginManager().callEvent(event);
                    if (event.isCancelled()) return false;

                    ItemStack itemstack = this.lectern.removeItemNoUpdate(0);
                    this.lectern.setChanged();
                    if (!entityhuman.getInventory().add(itemstack))  entityhuman.drop(itemstack, false);

                    return true;
                default:
                    return false;
            }
        }
    }

}