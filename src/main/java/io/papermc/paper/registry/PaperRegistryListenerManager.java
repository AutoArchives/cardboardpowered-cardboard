package io.papermc.paper.registry;

import com.google.common.base.Preconditions;
import com.javazilla.bukkitfabric.BukkitFabricMod;
import com.mojang.serialization.Lifecycle;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
// import io.papermc.paper.plugin.entrypoint.Entrypoint;
// import io.papermc.paper.plugin.entrypoint.LaunchEntryPointHandler;
// import io.papermc.paper.plugin.lifecycle.event.LifecycleEventRunner;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEventType;
import io.papermc.paper.registry.PaperRegistries;
import io.papermc.paper.registry.PaperRegistryAccess;
import io.papermc.paper.registry.PaperRegistryBuilder;
import io.papermc.paper.registry.RegistryBuilder;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.WritableCraftRegistry;
import io.papermc.paper.registry.data.util.Conversions;
import io.papermc.paper.registry.entry.RegistryEntry;
import io.papermc.paper.registry.entry.RegistryEntryInfo;
// import io.papermc.paper.registry.event.RegistryEntryAddEventImpl;
// import io.papermc.paper.registry.event.RegistryEventMap;
import io.papermc.paper.registry.event.RegistryEventProvider;
import io.papermc.paper.registry.event.RegistryFreezeEvent;
// import io.papermc.paper.registry.event.RegistryFreezeEventImpl;
import io.papermc.paper.registry.event.type.RegistryEntryAddEventType;
// import io.papermc.paper.registry.event.type.RegistryEntryAddEventTypeImpl;
// import io.papermc.paper.registry.event.type.RegistryLifecycleEventType;
import java.util.Optional;
import net.kyori.adventure.key.Key;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
// import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.bukkit.Keyed;
import org.cardboardpowered.Registries_Bridge;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PaperRegistryListenerManager {
    public static final PaperRegistryListenerManager INSTANCE = new PaperRegistryListenerManager();
    
    // public final RegistryEventMap valueAddEventTypes = new RegistryEventMap("value add");
    // public final RegistryEventMap freezeEventTypes = new RegistryEventMap("freeze");

    private PaperRegistryListenerManager() {
    }

    public <M> M registerWithListeners(Registry<M> registry, String id, M nms) {
        return this.registerWithListeners(registry, Identifier.ofVanilla(id), nms);
    }

    public <M> M registerWithListeners(Registry<M> registry, Identifier loc, M nms) {
        return this.registerWithListeners(registry, RegistryKey.of(registry.getKey(), loc), nms);
    }

    public <M> M registerWithListeners(Registry<M> registry, RegistryKey<M> key, M nms) {
        return (M)this.registerWithListeners(registry, key, nms, net.minecraft.registry.entry.RegistryEntryInfo.DEFAULT, PaperRegistryListenerManager::registerWithInstance, Registries_Bridge.BUILT_IN_CONVERSIONS);
    }

    public <M> net.minecraft.registry.entry.RegistryEntry.Reference<M> registerForHolderWithListeners(Registry<M> registry, Identifier loc, M nms) {
        return this.registerForHolderWithListeners(registry, RegistryKey.of(registry.getKey(), loc), nms);
    }

    public <M> net.minecraft.registry.entry.RegistryEntry.Reference<M> registerForHolderWithListeners(Registry<M> registry, RegistryKey<M> key, M nms) {
        return this.registerWithListeners(registry, key, nms, net.minecraft.registry.entry.RegistryEntryInfo.DEFAULT, MutableRegistry::add, Registries_Bridge.BUILT_IN_CONVERSIONS);
    }

    public <M> void registerWithListeners(Registry<M> registry, RegistryKey<M> key, M nms, net.minecraft.registry.entry.RegistryEntryInfo registrationInfo, Conversions conversions) {
        this.registerWithListeners(registry, key, nms, registrationInfo, MutableRegistry::add, conversions);
    }

    public <M, T extends Keyed, B extends PaperRegistryBuilder<M, T>, R> R registerWithListeners(Registry<M> registry, RegistryKey<M> key, M nms, net.minecraft.registry.entry.RegistryEntryInfo registrationInfo, RegisterMethod<M, R> registerMethod, Conversions conversions) {
        // Preconditions.checkState((boolean)LaunchEntryPointHandler.INSTANCE.hasEntered(Entrypoint.BOOTSTRAPPER), (Object)(String.valueOf(registry.getKey()) + " tried to run modification listeners before bootstrappers have been called"));
        
        @Nullable RegistryEntry<M, T> entry = PaperRegistries.getEntry(registry.getKey());
        // if (!RegistryEntry.Modifiable.isModifiable(entry) || !this.valueAddEventTypes.hasHandlers(entry.apiKey())) {
            return (R) registerMethod.register((MutableRegistry)registry, key, nms, registrationInfo);
        // }
        // RegistryEntry.Modifiable modifiableEntry = RegistryEntry.Modifiable.asModifiable(entry);
        // TypedKey typedKey = TypedKey.create(entry.apiKey(), (Key)Key.key((String)key.getValue().getNamespace(), (String)key.getValue().getPath()));
        // B builder = (B) modifiableEntry.fillBuilder(conversions, typedKey, nms);
        // return (R) this.registerWithListeners(registry, modifiableEntry, key, nms, builder, registrationInfo, registerMethod, conversions);
    }

    <M, T extends Keyed, B extends PaperRegistryBuilder<M, T>> void registerWithListeners(MutableRegistry<M> registry, RegistryEntryInfo<M, T> entry, RegistryKey<M> key, B builder, net.minecraft.registry.entry.RegistryEntryInfo registrationInfo, Conversions conversions) {
        // if (!RegistryEntry.Modifiable.isModifiable(entry) || !this.valueAddEventTypes.hasHandlers(entry.apiKey())) {
            registry.add(key, builder.build(), registrationInfo);
            return;
        // }
        // this.registerWithListeners(registry, RegistryEntry.Modifiable.asModifiable(entry), key, null, builder, registrationInfo, MutableRegistry::add, conversions);
    }

    public <M, T extends Keyed, B extends PaperRegistryBuilder<M, T>, R> R registerWithListeners(Registry<M> registry, RegistryEntry.Modifiable<M, T, B> entry, RegistryKey<M> key, @Nullable M oldNms, B builder, net.minecraft.registry.entry.RegistryEntryInfo registrationInfo, RegisterMethod<M, R> registerMethod, Conversions conversions) {
        // Identifier beingAdded = key.getValue();
        // TypedKey typedKey = TypedKey.create(entry.apiKey(), (Key)Key.key((String)beingAdded.getNamespace(), (String)beingAdded.getPath()));
        
        // RegistryEntryAddEventImpl<T, B> event = entry.createEntryAddEvent(typedKey, builder, conversions);
        // LifecycleEventRunner.INSTANCE.callEvent(this.valueAddEventTypes.getEventType(entry.apiKey()), event);
        
        /*
        if (oldNms != null) {
            ((SimpleRegistry)registry).clearIntrusiveHolder(oldNms);
        }
        Object newNms = ((PaperRegistryBuilder)event.builder()).build();
        if (oldNms != null && !newNms.equals(oldNms)) {
            registrationInfo = new net.minecraft.registry.entry.RegistryEntryInfo(Optional.empty(), Lifecycle.experimental());
        }
        */
        M newNms = oldNms;
        
        return (R) registerMethod.register((MutableRegistry)registry, key, newNms, registrationInfo);
    }

    private static <M> M registerWithInstance(MutableRegistry<M> writableRegistry, RegistryKey<M> key, M value, net.minecraft.registry.entry.RegistryEntryInfo registrationInfo) {
        writableRegistry.add(key, value, registrationInfo);
        return value;
    }

    public <M, T extends Keyed, B extends PaperRegistryBuilder<M, T>> void runFreezeListeners(RegistryKey<? extends Registry<M>> resourceKey, Conversions conversions) {
        /*
    	@Nullable RegistryEntry<M, T> entry = PaperRegistries.getEntry(resourceKey);
        if (!RegistryEntry.Addable.isAddable(entry) || !this.freezeEventTypes.hasHandlers(entry.apiKey())) {
            return;
        }
        RegistryEntry.Addable writableEntry = RegistryEntry.Addable.asAddable(entry);
        WritableCraftRegistry writableRegistry = PaperRegistryAccess.instance().getWritableRegistry(entry.apiKey());
        RegistryFreezeEventImpl event = writableEntry.createFreezeEvent(writableRegistry, conversions);
        LifecycleEventRunner.INSTANCE.callEvent(this.freezeEventTypes.getEventType(entry.apiKey()), event);
        */
    }

    /*
    public <T, B extends RegistryBuilder<T>> RegistryEntryAddEventType<T, B> getRegistryValueAddEventType(RegistryEventProvider<T, B> type) {
        if (!RegistryEntry.Modifiable.isModifiable(PaperRegistries.getEntry(type.registryKey()))) {
            throw new IllegalArgumentException(String.valueOf(type.registryKey()) + " does not support RegistryEntryAddEvent");
        }
        return this.valueAddEventTypes.getOrCreate(type.registryKey(), RegistryEntryAddEventTypeImpl::new);
    }
    */

    public <T, B extends RegistryBuilder<T>> LifecycleEventType.Prioritizable<BootstrapContext, RegistryFreezeEvent<T, B>> getRegistryFreezeEventType(RegistryEventProvider<T, B> type) {
        if (!RegistryEntry.Addable.isAddable(PaperRegistries.getEntry(type.registryKey()))) {
            throw new IllegalArgumentException(String.valueOf(type.registryKey()) + " does not support RegistryFreezeEvent");
        }
  
        BukkitFabricMod.LOGGER.info("Debug: Crap.");
        return null;
        // return this.freezeEventTypes.getOrCreate(type.registryKey(), RegistryLifecycleEventType::new);
    }

    @FunctionalInterface
    public static interface RegisterMethod<M, R> {
        public R register(MutableRegistry<M> var1, RegistryKey<M> var2, M var3, net.minecraft.registry.entry.RegistryEntryInfo var4);
    }
}
