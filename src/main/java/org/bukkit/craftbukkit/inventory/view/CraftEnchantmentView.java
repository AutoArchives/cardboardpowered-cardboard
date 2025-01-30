package org.bukkit.craftbukkit.inventory.view;

import com.google.common.base.Preconditions;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.util.collection.IndexedIterable;
import org.bukkit.craftbukkit.CraftRegistry;
// import org.bukkit.craftbukkit.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.view.EnchantmentView;
import org.cardboardpowered.impl.CardboardEnchantment;
import org.jetbrains.annotations.NotNull;

public class CraftEnchantmentView extends CraftInventoryView<EnchantmentScreenHandler, EnchantingInventory> implements EnchantmentView {

    public CraftEnchantmentView(final HumanEntity player, final EnchantingInventory viewing, final EnchantmentScreenHandler container) {
        super(player, viewing, container);
    }

    @Override
    public int getEnchantmentSeed() {
        return ((EnchantmentScreenHandler)this.container).getSeed();
    }

    // Paper start - add enchantment seed update API
    @Override
    public void setEnchantmentSeed(int seed) {
    	// TODO
    	// ((EnchantmentScreenHandler)this.container).setEnchantmentSeed(seed);
    }
    // Paper end - add enchantment seed update API

    public EnchantmentScreenHandler container() {
    	return ((EnchantmentScreenHandler)this.container);
    }
    
    @NotNull
    @Override
    public EnchantmentOffer[] getOffers() {
        IndexedIterable<RegistryEntry<Enchantment>> registry = CraftRegistry.getMinecraftRegistry().getOrThrow(RegistryKeys.ENCHANTMENT).getIndexedEntries();
        EnchantmentOffer[] offers = new EnchantmentOffer[3];
        for (int i = 0; i < 3; i++) {
            org.bukkit.enchantments.Enchantment enchantment =
            		(this.container().enchantmentId[i] >= 0) ?
            				CardboardEnchantment.minecraftHolderToBukkit(
            						registry.get(this.container().enchantmentId[i])
            				) : null;
            offers[i] = (enchantment != null) ?
            		new EnchantmentOffer(
            				enchantment,
            				this.container().enchantmentLevel[i],
            				this.container().enchantmentPower[i]
            		)
            		: null;
        }
        return offers;
    }

    @Override
    public void setOffers(@NotNull final EnchantmentOffer[] offers) {
        Preconditions.checkArgument(offers.length == 3, "There must be 3 offers given");
        IndexedIterable<RegistryEntry<Enchantment>> registry = CraftRegistry.getMinecraftRegistry().getOrThrow(RegistryKeys.ENCHANTMENT).getIndexedEntries();
        for (int i = 0; i < offers.length; i++) {
            final EnchantmentOffer offer = offers[i];
            if (offer == null) {
                this.container().enchantmentId[i] = -1;
                this.container().enchantmentLevel[i] = -1;
                this.container().enchantmentPower[i] = 0;
                continue;
            }

            this.container().enchantmentId[i] = registry.getRawIdOrThrow(CardboardEnchantment.bukkitToMinecraftHolder(offer.getEnchantment()));
            this.container().enchantmentLevel[i] = offer.getEnchantmentLevel();
            this.container().enchantmentPower[i] = offer.getCost();
        }
    }
}
