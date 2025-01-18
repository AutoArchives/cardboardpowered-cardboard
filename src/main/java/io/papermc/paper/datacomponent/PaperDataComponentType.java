package io.papermc.paper.datacomponent;

import io.papermc.paper.datacomponent.DataComponentAdapter;
import io.papermc.paper.datacomponent.DataComponentAdapters;
import io.papermc.paper.datacomponent.DataComponentType;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.Handleable;
import org.jspecify.annotations.Nullable;

public abstract class PaperDataComponentType<T, NMS>
implements DataComponentType,
Handleable<ComponentType<NMS>> {
    private final NamespacedKey key;
    private final ComponentType<NMS> type;
    private final DataComponentAdapter<NMS, T> adapter;

    public static <T> ComponentType<T> bukkitToMinecraft(DataComponentType type) {
        return (ComponentType)CraftRegistry.bukkitToMinecraft(type);
    }

    public static DataComponentType minecraftToBukkit(ComponentType<?> type) {
        return (DataComponentType)CraftRegistry.minecraftToBukkit(type, RegistryKeys.DATA_COMPONENT_TYPE, Registry.DATA_COMPONENT_TYPE);
    }

    public static Set<DataComponentType> minecraftToBukkit(Set<ComponentType<?>> nmsTypes) {
        HashSet<DataComponentType> types = new HashSet<DataComponentType>(nmsTypes.size());
        for (ComponentType<?> nmsType : nmsTypes) {
            types.add(PaperDataComponentType.minecraftToBukkit(nmsType));
        }
        return Collections.unmodifiableSet(types);
    }

    public static <B, M> @Nullable B convertDataComponentValue(final ComponentMap map, final PaperDataComponentType.ValuedImpl<B, M> type) {
        final net.minecraft.component.ComponentType<M> nms = bukkitToMinecraft(type);
        final M nmsValue = map.get(nms);
        if (nmsValue == null) {
            return null;
        }
        return type.getAdapter().fromVanilla(nmsValue);
    }

    public PaperDataComponentType(NamespacedKey key, ComponentType<NMS> type, DataComponentAdapter<NMS, T> adapter) {
        this.key = key;
        this.type = type;
        this.adapter = adapter;
    }

    public NamespacedKey getKey() {
        return this.key;
    }

    public boolean isPersistent() {
        return !this.type.shouldSkipSerialization();
    }

    public DataComponentAdapter<NMS, T> getAdapter() {
        return this.adapter;
    }

    @Override
    public ComponentType<NMS> getHandle() {
        return this.type;
    }

    public static <NMS> DataComponentType of(NamespacedKey key, ComponentType<NMS> type) {
        DataComponentAdapter<?, ?> adapter = DataComponentAdapters.ADAPTERS.get(Registries.DATA_COMPONENT_TYPE.getKey(type).orElseThrow());
        if (adapter == null) {
            throw new IllegalArgumentException("No adapter found for " + String.valueOf(key));
        }
        if (adapter.isValued()) {
            return new ValuedImpl(key, type, adapter);
        }
        return new NonValuedImpl(key, type, adapter);
    }

    static {
        DataComponentAdapters.bootstrap();
    }

    public static final class ValuedImpl<T, NMS>
    extends PaperDataComponentType<T, NMS>
    implements DataComponentType.Valued<T> {
        ValuedImpl(NamespacedKey key, ComponentType<NMS> type, DataComponentAdapter<NMS, T> adapter) {
            super(key, type, adapter);
        }
    }

    public static final class NonValuedImpl<T, NMS>
    extends PaperDataComponentType<T, NMS>
    implements DataComponentType.NonValued {
        NonValuedImpl(NamespacedKey key, ComponentType<NMS> type, DataComponentAdapter<NMS, T> adapter) {
            super(key, type, adapter);
        }
    }
}

