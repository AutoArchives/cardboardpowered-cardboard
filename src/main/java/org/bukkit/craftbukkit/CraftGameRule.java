package org.bukkit.craftbukkit;

import io.papermc.paper.util.Holderable;
import java.util.function.Function;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.bukkit.GameRule;
import org.bukkit.NamespacedKey;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class CraftGameRule<T> extends GameRule<T> implements Holderable<net.minecraft.world.rule.GameRule<T>> {

	private final RegistryEntry<net.minecraft.world.rule.GameRule<T>> holder;

	public static GameRule<?> minecraftToBukkit(net.minecraft.world.rule.GameRule minecraft) {
		return CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.GAME_RULE);
	}

	public static <T> net.minecraft.world.rule.GameRule<T> bukkitToMinecraft(GameRule<T> bukkit) {
		return CraftRegistry.bukkitToMinecraft(bukkit);
	}

	public static <T> RegistryEntry<net.minecraft.world.rule.GameRule<T>> bukkitToMinecraftHolder(GameRule<T> bukkit) {
		return CraftRegistry.bukkitToMinecraftHolder(bukkit);
	}

	public CraftGameRule(RegistryEntry<net.minecraft.world.rule.GameRule<?>> holder) {
		this.holder = (RegistryEntry) holder;
	}

	public static <LEGACY, MODERN> GameRule<LEGACY> wrap(
			GameRule<MODERN> rule, Function<LEGACY, MODERN> fromLegacyToModern, Function<MODERN, LEGACY> toLegacyFromModern, Class<LEGACY> legacyClass
			) {
		return new CraftGameRule.LegacyGameRuleWrapper<>(((CraftGameRule)rule).getHolder(), fromLegacyToModern, toLegacyFromModern, legacyClass);
	}
	
	/*
	@SuppressWarnings("unchecked")
    public static <LEGACY, MODERN> GameRule<LEGACY> wrap(GameRule<MODERN> rule, Function<LEGACY, MODERN> fromLegacyToModern, Function<MODERN, LEGACY> toLegacyFromModern, Class<LEGACY> legacyClass) {
        return new LegacyGameRuleWrapper<>(((CraftGameRule) rule).getHolder(), fromLegacyToModern, toLegacyFromModern, legacyClass);
    }
    */

	@Override
	public RegistryEntry<net.minecraft.world.rule.GameRule<T>> getHolder() {
		return this.holder;
	}

	@Override
	public NamespacedKey getKey() {
		return Holderable.super.getKey();
	}

	@Override
	public int hashCode() {
		return Holderable.super.implHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return Holderable.super.implEquals(obj);
	}

	@Override
	public String toString() {
		return Holderable.super.implToString();
	}

	public String getName() {
		return this.holder.getIdAsString();
	}

	public Class<T> getType() {
		return (Class<T>)(switch (this.getHandle().getType()) {
		case INT -> Integer.class;
		case BOOL -> Boolean.class;
		});
	}

	public String translationKey() {
		return this.getHandle().getTranslationKey();
	}

	public static class LegacyGameRuleWrapper<LEGACY, MODERN> extends CraftGameRule<LEGACY> {
		private final Class<LEGACY> typeOverride;
		private final Function<LEGACY, MODERN> fromLegacyToModern;
		private final Function<MODERN, LEGACY> toLegacyFromModern;

		public LegacyGameRuleWrapper(
				RegistryEntry<net.minecraft.world.rule.GameRule<?>> holder,
				Function<LEGACY, MODERN> fromLegacyToModern,
				Function<MODERN, LEGACY> toLegacyFromModern,
				Class<LEGACY> typeOverride
				) {
			super(holder);
			this.fromLegacyToModern = fromLegacyToModern;
			this.toLegacyFromModern = toLegacyFromModern;
			this.typeOverride = typeOverride;
		}

		public Function<LEGACY, MODERN> getFromLegacyToModern() {
			return this.fromLegacyToModern;
		}

		public Function<MODERN, LEGACY> getToLegacyFromModern() {
			return this.toLegacyFromModern;
		}

		@Override
		public Class<LEGACY> getType() {
			return this.typeOverride;
		}
	}

}
