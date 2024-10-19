package org.cardboardpowered.impl;

import net.kyori.adventure.text.Component;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Util;

import java.util.Locale;
import java.util.Set;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
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
import org.bukkit.inventory.ItemStack;
import org.cardboardpowered.adventure.CardboardAdventure;
import org.jetbrains.annotations.NotNull;

import io.papermc.paper.enchantments.EnchantmentRarity;
import me.isaiah.common.ICommonMod;

public class CardboardEnchantment extends Enchantment implements Handleable<net.minecraft.enchantment.Enchantment> {

    //private final net.minecraft.enchantment.Enchantment target;

    //private final NamespacedKey key;
    //private final net.minecraft.enchantment.Enchantment handle;
    //private final int id;
    
    private final NamespacedKey key;
    private final RegistryEntry<net.minecraft.enchantment.Enchantment> handle;
	
    public CardboardEnchantment(NamespacedKey key,  net.minecraft.enchantment.Enchantment handle) {
        this.key = key;
        this.handle = CraftRegistry.getMinecraftRegistry(RegistryKeys.ENCHANTMENT).getEntry(handle);
    }
    
    @Override
    public net.minecraft.enchantment.Enchantment getHandle() {
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
    	return this.handle.isIn(EnchantmentTags.TREASURE);
    }

    @Override
    public boolean isCursed() {
        return this.handle.isIn(EnchantmentTags.CURSE);
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        return getHandle().isAcceptableItem(CraftItemStack.asNMSCopy(item));
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

    public static net.minecraft.enchantment.Enchantment getRaw(Enchantment enchantment) {
        if (enchantment instanceof EnchantmentWrapper) enchantment = ((EnchantmentWrapper) enchantment).getEnchantment();
        if (enchantment instanceof CardboardEnchantment) return ((CardboardEnchantment) enchantment).getHandle();

        return null;
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        if (other instanceof EnchantmentWrapper) other = ((EnchantmentWrapper) other).getEnchantment();
        if (!(other instanceof CardboardEnchantment)) return false;

        CardboardEnchantment ench = (CardboardEnchantment) other;
        return !net.minecraft.enchantment.Enchantment.canBeCombined(this.handle, ench.handle);
    }

    @Override
    public @NotNull Component displayName(int lev) {
        return CardboardAdventure.asAdventure(net.minecraft.enchantment.Enchantment.getName(this.handle, lev));
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
        return this.handle.isIn(EnchantmentTags.IN_ENCHANTING_TABLE) || this.handle.isIn(EnchantmentTags.ON_RANDOM_LOOT) || this.handle.isIn(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT) || this.handle.isIn(EnchantmentTags.TRADEABLE) || this.handle.isIn(EnchantmentTags.ON_TRADED_EQUIPMENT);
    }

    @Override
    public boolean isTradeable() {
        return this.handle.isIn(EnchantmentTags.TRADEABLE);
    }

    @Override
    public String translationKey() {
        TextContent textContent = this.getHandle().description().getContent();
        if (!(textContent instanceof TranslatableTextContent)) {
            throw new UnsupportedOperationException("Description isn't translatable!");
        }
        TranslatableTextContent translatableContents = (TranslatableTextContent)textContent;
        return translatableContents.getKey();
    }

	public static void bukkitToMinecraft_old() {
	}
	
    public static net.minecraft.enchantment.Enchantment bukkitToMinecraft(Enchantment bukkit) {
    	
    	// return ( (CardboardEnchantment) bukkit ).getHandle();
    	
        return CraftRegistry.bukkitToMinecraft(bukkit);
    }

	public static Enchantment minecraftHolderToBukkit(RegistryEntry<net.minecraft.enchantment.Enchantment> id) {
        return CardboardEnchantment.minecraftToBukkit(id.value());

		
		// return CardboardEnchantment.getByKey( CraftNamespacedKey.fromMinecraft(id.getKey().get().getValue()) );
		// return minecraftToBukkit(minecraft.value());
		// return null;
	}
	
    public static Enchantment minecraftToBukkit(net.minecraft.enchantment.Enchantment minecraft) {
        return CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.ENCHANTMENT, Registry.ENCHANTMENT);
    }
    
    // 1.20.2 API: 
    
	@Override
	public int getMinModifiedCost(int level) {
		return this.getHandle().getMinPower(level);
	}

	@Override
	public int getMaxModifiedCost(int level) {
		return this.getHandle().getMaxPower(level);
	}

	// 1.20.3 API:
	
	@Override
	public NamespacedKey getKey() {
		return this.key;
	}
	
	// 1.20.4 API:

	@Override
	public String getTranslationKey() {
        return Util.createTranslationKey("enchantment", this.handle.getKey().get().getValue());
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

	public static RegistryEntry<net.minecraft.enchantment.Enchantment> bukkitToMinecraftHolder(Enchantment key2) {
        return CraftRegistry.bukkitToMinecraftHolder(key2, RegistryKeys.ENCHANTMENT);
	}


}