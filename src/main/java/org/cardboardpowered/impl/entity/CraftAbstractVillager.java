package org.cardboardpowered.impl.entity;

import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.trading.Merchant;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftAgeable;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftMerchant;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class CraftAbstractVillager extends CraftAgeable implements CraftMerchant, AbstractVillager, InventoryHolder {

	public CraftAbstractVillager(CraftServer server, net.minecraft.world.entity.npc.villager.AbstractVillager entity) {
		super(server, entity);
	}

	public net.minecraft.world.entity.npc.villager.AbstractVillager getHandle() {
		return (Villager) super.entity;
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