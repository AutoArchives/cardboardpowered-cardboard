package org.cardboardpowered.mixin.registry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.registry.ReloadableRegistries;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Lifecycle;
import io.papermc.paper.registry.PaperRegistryAccess;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.loot.LootDataType;
import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Mixin(ReloadableRegistries.class)
public class MixinReloadableRegistries {

	@Shadow
    private static final Gson GSON = new GsonBuilder().create();
	
	@Shadow
    private static final RegistryEntryInfo DEFAULT_REGISTRY_ENTRY_INFO = new RegistryEntryInfo(Optional.empty(), Lifecycle.experimental());
	
	/**
	 * @author Cardboard
	 * @reason Implement Paper's "Add RegistryAccess for managing Registries".patch
	 */
	@Overwrite
    private static <T> CompletableFuture<MutableRegistry<?>> prepare(LootDataType<T> type, RegistryOps<JsonElement> ops, ResourceManager resourceManager, Executor prepareExecutor) {
        return CompletableFuture.supplyAsync(() -> {
            SimpleRegistry writableRegistry = new SimpleRegistry(type.registryKey(), Lifecycle.experimental());
            PaperRegistryAccess.instance().registerReloadableRegistry(type.registryKey(), writableRegistry);
            HashMap<Identifier, JsonElement> map = new HashMap<Identifier, JsonElement>();
            String string = RegistryKeys.getPath(type.registryKey());
            JsonDataLoader.load(resourceManager, string, GSON, map);
            map.forEach((id, json) -> type.parse((Identifier)id, ops, json).ifPresent(value -> writableRegistry.add(RegistryKey.of(type.registryKey(), id), value, DEFAULT_REGISTRY_ENTRY_INFO)));
            return writableRegistry;
        }, prepareExecutor);
    }
	
}
