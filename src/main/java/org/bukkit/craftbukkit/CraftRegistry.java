package org.bukkit.craftbukkit;

import java.util.Iterator;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.entity.BannerPattern;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.entry.RegistryEntry;

// TODO
public class CraftRegistry<B extends Keyed, M> implements Registry<B> {

	@Override
	public Iterator<B> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @Nullable B get(@NotNull NamespacedKey arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public static <B extends Keyed> Registry<?> createRegistry(Class<B> bukkitClass, DynamicRegistryManager registryHolder) {
		// TODO Auto-generated method stub
		return null;
	}


    public static <B extends Keyed, M> RegistryEntry<M> bukkitToMinecraftHolder(B bukkit, RegistryKey<net.minecraft.registry.Registry<M>> registryKey) {
        // Preconditions.checkArgument(bukkit != null);

        //net.minecraft.registry.Registry<M> registry = CraftRegistry.getMinecraftRegistry(registryKey);

        //if (registry.wrapAsHolder(CraftRegistry.bukkitToMinecraft(bukkit)) instanceof RegistryEntry.Reference<M> holder) {
        //    return holder;
        //}

        throw new IllegalArgumentException("No Reference holder found for " + bukkit
                + ", this can happen if a plugin creates its own registry entry with out properly registering it.");
    }

	public static DynamicRegistryManager getMinecraftRegistry() {
		// TODO Auto-generated method stub
		return CraftServer.server.getRegistryManager();
	}

    public static <E> net.minecraft.registry.Registry<E> getMinecraftRegistry(RegistryKey<net.minecraft.registry.Registry<E>> key) {
        return CraftServer.server.getRegistryManager().get(key);
    }

    /**
     * Usage note: Only use this method to delegate the conversion methods from the individual Craft classes to here.
     * Do not use it in other parts of CraftBukkit, use the methods in the respective Craft classes instead.
     *
     * @param bukkit the bukkit representation
     * @return the minecraft representation of the bukkit value
     */
    /*public static <B extends Keyed, M> M bukkitToMinecraft(B bukkit) {
        // Preconditions.checkArgument(bukkit != null);
        return ((Handleable<M>) bukkit).getHandle();
    }*/

    /*private static DynamicRegistryManager registry;

    public static void setMinecraftRegistry(DynamicRegistryManager registry) {
        Preconditions.checkState(CraftRegistry.registry == null, "Registry already set");
        CraftRegistry.registry = registry;
    }

    public static DynamicRegistryManager getMinecraftRegistry() {
        return registry;
    }

    public static <E> net.minecraft.util.registry.Registry<E> getMinecraftRegistry(RegistryKey<net.minecraft.util.registry.Registry<E>> key) {
        return getMinecraftRegistry().get(key);
    }

    public static <B extends Keyed> Registry<?> createRegistry(Class<B> bukkitClass, DynamicRegistryManager registryHolder) {
        if (bukkitClass == GameEvent.class) {
            return new CraftRegistry<>(registryHolder.get(RegistryKeys.GAME_EVENT), CraftGameEvent::new);
        }
        if (bukkitClass == MusicInstrument.class) {
            return new CraftRegistry<>(registryHolder.get(RegistryKeys.INSTRUMENT), CraftMusicInstrument::new);
        }
        if (bukkitClass == Structure.class) {
            return new CraftRegistry<>(registryHolder.get(RegistryKeys.STRUCTURE), CraftStructure::new);
        }
        if (bukkitClass == StructureType.class) {
            return new CraftRegistry<>(Registries.STRUCTURE_TYPE, CraftStructureType::new);
        }
        if (bukkitClass == TrimMaterial.class) {
            return new CraftRegistry<>(registryHolder.get(RegistryKeys.TRIM_MATERIAL), CraftTrimMaterial::new);
        }
        if (bukkitClass == TrimPattern.class) {
            return new CraftRegistry<>(registryHolder.get(RegistryKeys.TRIM_PATTERN), CraftTrimPattern::new);
        }

        return null;
    }

    private final Map<NamespacedKey, B> cache = new HashMap<>();
    private final net.minecraft.registry.Registry<M> minecraftRegistry;
    private final BiFunction<NamespacedKey, M, B> minecraftToBukkit;

    public CraftRegistry(net.minecraft.util.registry.Registry<M> minecraftRegistry, BiFunction<NamespacedKey, M, B> minecraftToBukkit) {
        this.minecraftRegistry = minecraftRegistry;
        this.minecraftToBukkit = minecraftToBukkit;
    }

    @Override
    public B get(NamespacedKey namespacedKey) {
        B cached = cache.get(namespacedKey);
        if (cached != null) {
            return cached;
        }

        B bukkit = createBukkit(namespacedKey, minecraftRegistry.getOrEmpty(CraftNamespacedKey.toMinecraft(namespacedKey)).orElse(null));
        if (bukkit == null) {
            return null;
        }

        cache.put(namespacedKey, bukkit);

        return bukkit;
    }

    @NotNull
    @Override
    public Stream<B> stream() {
        return minecraftRegistry.getIds().stream().map(minecraftKey -> get(CraftNamespacedKey.fromMinecraft(minecraftKey)));
    }

    @Override
    public Iterator<B> iterator() {
        return stream().iterator();
    }

    public B createBukkit(NamespacedKey namespacedKey, M minecraft) {
        if (minecraft == null) {
            return null;
        }

        return minecraftToBukkit.apply(namespacedKey, minecraft);
    }

    public Stream<B> values() {
        return minecraftRegistry.getIds().stream().map(minecraftKey -> get(CraftNamespacedKey.fromMinecraft(minecraftKey)));
    }*/
}
