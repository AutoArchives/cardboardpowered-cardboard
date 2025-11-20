package org.cardboardpowered.impl.tag;

import java.util.Objects;
import java.util.Optional;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;

import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;

/**
 * Cardboard Implementation of {@link org.bukkit.Tag}
 */
public abstract class CraftTag<N, B extends Keyed> implements Tag<B> {

	protected final net.minecraft.registry.Registry<N> registry;
	protected final TagKey<N> tag;
	private RegistryEntryList.Named<N> handle;

	public CraftTag(Registry<N> registry, TagKey<N> tag) {
		this.registry = registry;
		this.tag = tag;
		
		Optional< RegistryEntryList.Named<N> > handleOptional = registry.getOptional(this.tag);
		this.handle = handleOptional.orElseThrow();
	}

	public RegistryEntryList.Named<N> getHandle() {
		return handle;
	}

	@Override
	public NamespacedKey getKey() {
		return CraftNamespacedKey.fromMinecraft(tag.id());
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 59 * hash + Objects.hashCode(this.registry);
		return 59 * hash + Objects.hashCode(this.tag);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else {
			return !(obj instanceof CraftTag<?, ?> other) ? false : Objects.equals(this.registry, other.registry) && Objects.equals(this.tag, other.tag);
		}
	}

	@Override
	public String toString() {
		return "CraftTag{" + this.tag + "}";
	}

}