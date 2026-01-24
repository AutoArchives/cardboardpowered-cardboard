package org.cardboardpowered.impl;

import net.kyori.adventure.text.Component;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.EntityCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.cardboardpowered.adventure.CardboardAdventure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.papermc.paper.enchantments.EnchantmentRarity;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.PaperRegistrySets;
import io.papermc.paper.registry.set.RegistryKeySet;
import me.isaiah.common.ICommonMod;

public class CardboardEnchantment extends Enchantment implements Handleable<net.minecraft.world.item.enchantment.Enchantment> {

    //private final net.minecraft.enchantment.Enchantment target;

    //private final NamespacedKey key;
    //private final net.minecraft.enchantment.Enchantment handle;
    //private final int id;
    
    private final NamespacedKey key;
    private final Holder<net.minecraft.world.item.enchantment.Enchantment> handle;
	
    public CardboardEnchantment(NamespacedKey key,  net.minecraft.world.item.enchantment.Enchantment handle) {
        this.key = key;
        this.handle = CraftRegistry.getMinecraftRegistry(Registries.ENCHANTMENT).wrapAsHolder(handle);
    }
    
    @Override
    public net.minecraft.world.item.enchantment.Enchantment getHandle() {
        return this.handle.value();
    }
    
    /*
    public CardboardEnchantment(NamespacedKey key, net.minecraft.enchantment.Enchantment handle) {
    	// super(CraftNamespacedKey.fromMinecraft(Registries.ENCHANTMENT.getId(handle)));
        this.key = key;
        this.handle = handle;
        this.id = Registries.ENCHANTMENT.getRawId(handle);
        this.target = handle;
    }
    
    @Deprecated
    public CardboardEnchantment(net.minecraft.enchantment.Enchantment target) {
    	this(CraftNamespacedKey.fromMinecraft(Registries.ENCHANTMENT.getId(target)), target);
    }*/

    @Override
    public int getMaxLevel() {
        return getHandle().getMaxLevel();
    }

    @Override
    public int getStartLevel() {
        return getHandle().getMinLevel();
    }

    @Override
    public EnchantmentTarget getItemTarget() {
    	
    	// TODO: remove compact
    	String mc_ver = ICommonMod.getIServer().getMinecraftVersion();
    	if (mc_ver.contains("1.20.4") || mc_ver.contains("1.20.1")) {
    		return EnchantmentTarget.ALL;
    	}
    	
    	throw new UnsupportedOperationException("Method no longer supported Use Tags instead.");
    }

    @Override
    public boolean isTreasure() {
    	return this.handle.is(EnchantmentTags.TREASURE);
    }

    @Override
    public boolean isCursed() {
        return this.handle.is(EnchantmentTags.CURSE);
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        return getHandle().canEnchant(CraftItemStack.asNMSCopy(item));
    }

    @Override
    public String getName() {
        String keyName;
        if (!this.getKey().getNamespace().equals("minecraft")) {
            return this.getKey().toString();
        }
        return switch (keyName = this.getKey().getKey().toUpperCase(Locale.ROOT)) {
            case "PROTECTION" -> "PROTECTION_ENVIRONMENTAL";
            case "FIRE_PROTECTION" -> "PROTECTION_FIRE";
            case "FEATHER_FALLING" -> "PROTECTION_FALL";
            case "BLAST_PROTECTION" -> "PROTECTION_EXPLOSIONS";
            case "PROJECTILE_PROTECTION" -> "PROTECTION_PROJECTILE";
            case "RESPIRATION" -> "OXYGEN";
            case "AQUA_AFFINITY" -> "WATER_WORKER";
            case "SHARPNESS" -> "DAMAGE_ALL";
            case "SMITE" -> "DAMAGE_UNDEAD";
            case "BANE_OF_ARTHROPODS" -> "DAMAGE_ARTHROPODS";
            case "LOOTING" -> "LOOT_BONUS_MOBS";
            case "EFFICIENCY" -> "DIG_SPEED";
            case "UNBREAKING" -> "DURABILITY";
            case "FORTUNE" -> "LOOT_BONUS_BLOCKS";
            case "POWER" -> "ARROW_DAMAGE";
            case "PUNCH" -> "ARROW_KNOCKBACK";
            case "FLAME" -> "ARROW_FIRE";
            case "INFINITY" -> "ARROW_INFINITE";
            case "LUCK_OF_THE_SEA" -> "LUCK";
            default -> keyName;
        };
    }

    public static net.minecraft.world.item.enchantment.Enchantment getRaw(Enchantment enchantment) {
        if (enchantment instanceof EnchantmentWrapper) enchantment = ((EnchantmentWrapper) enchantment).getEnchantment();
        if (enchantment instanceof CardboardEnchantment) return ((CardboardEnchantment) enchantment).getHandle();

        return null;
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        if (other instanceof EnchantmentWrapper) other = ((EnchantmentWrapper) other).getEnchantment();
        if (!(other instanceof CardboardEnchantment)) return false;

        CardboardEnchantment ench = (CardboardEnchantment) other;
        return !net.minecraft.world.item.enchantment.Enchantment.areCompatible(this.handle, ench.handle);
    }

    @Override
    public @NotNull Component displayName(int lev) {
        return CardboardAdventure.asAdventure(net.minecraft.world.item.enchantment.Enchantment.getFullname(this.handle, lev));
    }

    @Override
    public @NotNull Set<EquipmentSlot> getActiveSlots() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public float getDamageIncrease(int arg0, @NotNull EntityCategory arg1) {
    	throw new UnsupportedOperationException("Not supported for 1.21+");
    }

    @Override
    public EnchantmentRarity getRarity() {
    	throw new UnsupportedOperationException("Not supported for 1.20.5+");
    }

    @Override
    public boolean isDiscoverable() {
        return this.handle.is(EnchantmentTags.IN_ENCHANTING_TABLE) || this.handle.is(EnchantmentTags.ON_RANDOM_LOOT) || this.handle.is(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT) || this.handle.is(EnchantmentTags.TRADEABLE) || this.handle.is(EnchantmentTags.ON_TRADED_EQUIPMENT);
    }

    @Override
    public boolean isTradeable() {
        return this.handle.is(EnchantmentTags.TRADEABLE);
    }

    @Override
    public String translationKey() {
        ComponentContents textContent = this.getHandle().description().getContents();
        if (!(textContent instanceof TranslatableContents)) {
            throw new UnsupportedOperationException("Description isn't translatable!");
        }
        TranslatableContents translatableContents = (TranslatableContents)textContent;
        return translatableContents.getKey();
    }

	public static void bukkitToMinecraft_old() {
	}
	
    public static net.minecraft.world.item.enchantment.Enchantment bukkitToMinecraft(Enchantment bukkit) {
    	
    	// return ( (CardboardEnchantment) bukkit ).getHandle();
    	
        return CraftRegistry.bukkitToMinecraft(bukkit);
    }

	public static Enchantment minecraftHolderToBukkit(Holder<net.minecraft.world.item.enchantment.Enchantment> id) {
        return CardboardEnchantment.minecraftToBukkit(id.value());

		
		// return CardboardEnchantment.getByKey( CraftNamespacedKey.fromMinecraft(id.getKey().get().getValue()) );
		// return minecraftToBukkit(minecraft.value());
		// return null;
	}
	
    public static Enchantment minecraftToBukkit(net.minecraft.world.item.enchantment.Enchantment minecraft) {
        return CraftRegistry.minecraftToBukkit(minecraft, Registries.ENCHANTMENT);
    }
    
    // 1.20.2 API: 
    
	@Override
	public int getMinModifiedCost(int level) {
		return this.getHandle().getMinCost(level);
	}

	@Override
	public int getMaxModifiedCost(int level) {
		return this.getHandle().getMaxCost(level);
	}

	// 1.20.3 API:
	
	@Override
	public NamespacedKey getKey() {
		return this.key;
	}
	
	// 1.20.4 API:

	@Override
	public String getTranslationKey() {
        return Util.makeDescriptionId("enchantment", this.handle.unwrapKey().get().identifier());
	}
	
	// 1.20.6 API

	@Override
	public int getAnvilCost() {
		return this.getHandle().getAnvilCost();
	}

	@Override
	public float getDamageIncrease(int level, @NotNull EntityType entityType) {
		throw new UnsupportedOperationException("Not supported for 1.21+");
	}
	
	/*
    @Deprecated(forRemoval=true)
    private net.minecraft.entity.EntityType<?> guessEntityTypeFromEnchantmentCategory(EntityCategory entityCategory) {
        TagKey<net.minecraft.entity.EntityType<?>> tag;
        switch (entityCategory) {
            case ARTHROPOD: {
                tag = EntityTypeTags.SENSITIVE_TO_BANE_OF_ARTHROPODS;
                break;
            }
            case UNDEAD: {
                tag = EntityTypeTags.SENSITIVE_TO_SMITE;
                break;
            }
            case WATER: {
                tag = EntityTypeTags.SENSITIVE_TO_IMPALING;
                break;
            }
            default: {
                tag = null;
            }
        }
        if (tag == null) {
            return null;
        }
        return Registries.ENTITY_TYPE.getEntryList(tag).map(e2 -> e2.size() > 0 ? (net.minecraft.entity.EntityType)e2.get(0).value() : null).orElse(null);
    }
    */

	public static Holder<net.minecraft.world.item.enchantment.Enchantment> bukkitToMinecraftHolder(Enchantment key2) {
        return CraftRegistry.bukkitToMinecraftHolder(key2, Registries.ENCHANTMENT);
	}

	@Override
	public @NotNull Set<EquipmentSlotGroup> getActiveSlotGroups() {
        return this.getHandle().definition().slots().stream().map(CraftEquipmentSlot::getSlot).collect(Collectors.toSet());
	}

	@Override
	public @NotNull Component description() {
		return CardboardAdventure.asAdventure(this.handle.value().description());
	}

	@Override
	public @NotNull RegistryKeySet<ItemType> getSupportedItems() {
        return PaperRegistrySets.convertToApi(RegistryKey.ITEM, this.handle.value().getSupportedItems());
	}

	@Override
	public @Nullable RegistryKeySet<ItemType> getPrimaryItems() {
		Optional<HolderSet<Item>> primaryItems = this.handle.value().definition().primaryItems();
        return primaryItems.map(holders -> PaperRegistrySets.convertToApi(RegistryKey.ITEM, holders)).orElse(null);
	}

	@Override
	public int getWeight() {
		return this.handle.value().getWeight();
	}

	@Override
	public @NotNull RegistryKeySet<Enchantment> getExclusiveWith() {
		return PaperRegistrySets.convertToApi(RegistryKey.ENCHANTMENT, this.handle.value().exclusiveSet());
	}


}