package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import net.minecraft.registry.RegistryKeys;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.EntityType;

public class CraftEntityType {

    public static EntityType minecraftToBukkit(net.minecraft.entity.EntityType<?> minecraft) {
        Preconditions.checkArgument(minecraft != null);

        net.minecraft.registry.Registry<net.minecraft.entity.EntityType<?>> registry = CraftRegistry.getMinecraftRegistry(RegistryKeys.ENTITY_TYPE);
        EntityType bukkit = Registry.ENTITY_TYPE.get(CraftNamespacedKey.fromMinecraft(registry.getKey(minecraft).orElseThrow().getValue()));

        Preconditions.checkArgument(bukkit != null);

        return bukkit;
    }

    public static net.minecraft.entity.EntityType<?> bukkitToMinecraft(EntityType bukkit) {
        Preconditions.checkArgument(bukkit != null);

        return CraftRegistry.getMinecraftRegistry(RegistryKeys.ENTITY_TYPE)
                .getOrEmpty(CraftNamespacedKey.toMinecraft(bukkit.getKey())).orElseThrow();
    }

}