package org.cardboardpowered.impl.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftMerchant;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.Merchant;

public abstract class CraftAbstractVillager extends CraftAgeable implements CraftMerchant, AbstractVillager, InventoryHolder {

	public CraftAbstractVillager(CraftServer server, MerchantEntity entity) {
		super(server, entity);
	}

	public MerchantEntity getHandle() {
		return (VillagerEntity) super.nms;
	}

	@Override
	public Merchant getMerchant() {
		return this.getHandle();
	}

	public Inventory getInventory() {
		return new CraftInventory(this.getHandle().getInventory());
	}

	public void resetOffers() {
		// this.getHandle().resetOffers();
	}

}