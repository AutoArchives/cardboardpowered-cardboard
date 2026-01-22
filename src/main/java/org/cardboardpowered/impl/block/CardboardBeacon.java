package org.cardboardpowered.impl.block;

import net.kyori.adventure.text.Component;
import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.LockCode;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import me.isaiah.common.cmixin.IMixinBeaconBlockEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class CardboardBeacon extends CardboardBlockEntityState<BeaconBlockEntity> implements Beacon {

    public CardboardBeacon(World world, BeaconBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CardboardBeacon(CardboardBeacon state, Location location) {
        super(state, location);
    }
	
    /*
    public CardboardBeacon(final Block block) {
        super(block, BeaconBlockEntity.class);
    }

    public CardboardBeacon(final Material material, final BeaconBlockEntity te) {
        super(material, te);
    }
    */

    @Override
    public Collection<LivingEntity> getEntitiesInRange() {
        // TODO Bukkit4Fabirc: auto-generated method stub
        return new ArrayList<LivingEntity>();
    }

    @Override
    public int getTier() {
        return this.getSnapshot().levels;
    }

    @Override
    public PotionEffect getPrimaryEffect() {
        // TODO Bukkit4Fabirc: auto-generated method stub
        return null;
    }

    @Override
    public void setPrimaryEffect(PotionEffectType effect) {
    	IMixinBeaconBlockEntity ic = (IMixinBeaconBlockEntity) (Object) this.getSnapshot();
    	ic.IC$set_primary_effect(effect.getId());
        // this.getSnapshot().primary = (effect != null) ? Registries.STATUS_EFFECT.get(effect.getId()) : null;
    }

    @Override
    public PotionEffect getSecondaryEffect() {
        // TODO Bukkit4Fabirc: auto-generated method stub
        return null;
    }

    @Override
    public void setSecondaryEffect(PotionEffectType effect) {
    	IMixinBeaconBlockEntity ic = (IMixinBeaconBlockEntity) (Object) this.getSnapshot();
    	ic.IC$set_secondary_effect(effect.getId());
        // this.getSnapshot().secondary = (effect != null) ? Registries.STATUS_EFFECT.get(effect.getId()) : null;
    }

    @Override
    public String getCustomName() {
        BeaconBlockEntity beacon = this.getSnapshot();
        return beacon.name != null ? CraftChatMessage.fromComponent(beacon.name) : null;
    }

    @Override
    public void setCustomName(String name) {
        this.getSnapshot().setCustomName(CraftChatMessage.fromStringOrNull(name));
    }

    @Override
    public boolean isLocked() {
    	return this.getSnapshot().lockKey != LockCode.NO_LOCK;
    }

    @Override
    public String getLock() {
        Optional<? extends net.minecraft.network.chat.Component> customName = this.getSnapshot().lockKey.predicate().components().exact().asPatch().get(DataComponents.CUSTOM_NAME);

        return (customName != null) ? customName.map(CraftChatMessage::fromComponent).orElse("") : "";
    }

    @Override
    public void setLock(String key) {
        if (key == null) {
            this.getSnapshot().lockKey = LockCode.NO_LOCK;
        } else {
        	DataComponentExactPredicate predicate = DataComponentExactPredicate.builder().expect(DataComponents.CUSTOM_NAME, CraftChatMessage.fromStringOrNull(key)).build();
            this.getSnapshot().lockKey = new LockCode(new ItemPredicate(Optional.empty(), MinMaxBounds.Ints.ANY, new DataComponentMatchers(predicate, Collections.emptyMap())));
        }
    }
    
    @Override
    public void setLockItem(ItemStack key) {
        if (key == null) {
            this.getSnapshot().lockKey = LockCode.NO_LOCK;
        } else {
            this.getSnapshot().lockKey = new LockCode(CraftItemStack.asCriterionConditionItem(key));
        }
    }

    @Override
    public double getEffectRange() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void resetEffectRange() {
        // TODO Auto-generated method stub
    }

    @Override
    public void setEffectRange(double arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public @Nullable Component customName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void customName(@Nullable Component arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public CardboardBeacon copy() {
        return new CardboardBeacon(this, null);
    }

    @Override
    public CardboardBeacon copy(Location location) {
        return new CardboardBeacon(this, location);
    }


}
