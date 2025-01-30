package org.bukkit.craftbukkit.inventory.view;

import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.BrewingStandScreenHandler;

import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.potion.CraftPotionEffectType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.BeaconInventory;
import org.bukkit.inventory.view.BeaconView;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

public class CraftBeaconView extends CraftInventoryView<BeaconScreenHandler, BeaconInventory> implements BeaconView {

    public CraftBeaconView(final HumanEntity player, final BeaconInventory viewing, final BeaconScreenHandler container) {
        super(player, viewing, container);
    }

    @Override
    public int getTier() {
        return ((BeaconScreenHandler)this.container).getProperties();
    }

    @Nullable
    @Override
    public PotionEffectType getPrimaryEffect() {
        return ((BeaconScreenHandler)this.container).getPrimaryEffect() != null ?
        		CraftPotionEffectType.minecraftHolderToBukkit(((BeaconScreenHandler)this.container).getPrimaryEffect()) : null;
    }

    @Nullable
    @Override
    public PotionEffectType getSecondaryEffect() {
        return ((BeaconScreenHandler)this.container).getSecondaryEffect() != null ?
        		CraftPotionEffectType.minecraftHolderToBukkit(((BeaconScreenHandler)this.container).getSecondaryEffect()) : null;
    }

    @Override
    public void setPrimaryEffect(@Nullable final PotionEffectType effectType) {
        this.container.setProperty(BeaconBlockEntity.PRIMARY_PROPERTY_INDEX, BeaconScreenHandler.getRawIdForStatusEffect((effectType == null) ? null : CraftPotionEffectType.bukkitToMinecraftHolder(effectType)));
    }

    @Override
    public void setSecondaryEffect(@Nullable final PotionEffectType effectType) {
        this.container.setProperty(BeaconBlockEntity.SECONDARY_PROPERTY_INDEX, BeaconScreenHandler.getRawIdForStatusEffect((effectType == null) ? null : CraftPotionEffectType.bukkitToMinecraftHolder(effectType)));
    }
}
