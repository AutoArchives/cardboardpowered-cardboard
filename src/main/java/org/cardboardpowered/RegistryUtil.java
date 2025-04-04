package org.cardboardpowered;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.potion.PotionType;

import org.cardboardpowered.CardboardMod;

import io.izzel.arclight.api.EnumHelper;
import net.minecraft.registry.Registries;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.util.Identifier;

/**
 * Registry API Util
 *
 * @since 1.21.4
 */
public class RegistryUtil {

	/**
	 * Inject Minecraft builtin registry entries into Bukkit API.
	 * 
	 * @see {@link org.bukkit.craftbukkit.CraftRegistry}
	 * @see {@link io.papermc.paper.registry.PaperRegistries}
	 */
	public static void inject_into_bukkit_registry(MinecraftDedicatedServer server) {
		register_potions();
	}
	
	public static String normalizeName(String name) {
        return name.replace(':', '_')
                .replaceAll("\\s+", "_")
                .replaceAll("\\W", "")
                .toUpperCase(Locale.ENGLISH);
    }

	private static void register_potions() {
		List<PotionType> newTypes = new ArrayList<>();

		for (var potion : Registries.POTION) {
			Identifier location = Registries.POTION.getId(potion);
			String name = normalizeName(location.toString());
			try {
				PotionType.valueOf(name);
				CardboardMod.LOGGER.info("FOUND POT for " + name);
			} catch (Exception e) {
				NamespacedKey namespacedKey = CraftNamespacedKey.fromMinecraft(location);
				
				PotionType potionType = EnumHelper.addEnum(
						PotionType.class,
						name,
						List.of(String.class),
						List.of( namespacedKey.getKey() )
					);
				newTypes.add(potionType);
				if (CardboardConfig.DEBUG_VERBOSE_CALLS) {
					CardboardMod.LOGGER.info("Registered " + location + " as potion type " + potionType);
				}
			}
		}
	}

	/**
	 * Does the Identifier's Namespace match vanilla's
	 * (Ex: "minecraft:dirt" -> true; "modid:dirt_slab" -> false)
	 */
	public static boolean isVanilla(Identifier id) {
		return id.getNamespace().equalsIgnoreCase( NamespacedKey.MINECRAFT );
	}
	
	/**
	 * Inverse of {@link #isVanilla(Identifier)}
	 * 
	 * @see {@link #isVanilla(Identifier)}
	 */
	public static boolean isModded(Identifier id) {
		return !isVanilla(id);
	}
	
}
