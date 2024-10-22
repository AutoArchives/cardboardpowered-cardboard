package org.bukkit.craftbukkit;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.event.Vibrations;

import org.bukkit.GameEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.craftbukkit.util.Handleable;
import org.jetbrains.annotations.NotNull;

public class CraftGameEvent extends GameEvent implements Handleable<net.minecraft.world.event.GameEvent> {

    private final NamespacedKey key;
    private final RegistryKey<net.minecraft.world.event.GameEvent> handleKey;
    private final net.minecraft.world.event.GameEvent handle;

    public static GameEvent minecraftToBukkit(net.minecraft.world.event.GameEvent minecraft) {
        return (GameEvent)CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.GAME_EVENT, Registry.GAME_EVENT);
    }

    public static net.minecraft.world.event.GameEvent bukkitToMinecraft(GameEvent bukkit) {
        return (net.minecraft.world.event.GameEvent)CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public CraftGameEvent(NamespacedKey key, net.minecraft.world.event.GameEvent handle) {
        this.key = key;
        this.handleKey = RegistryKey.of(RegistryKeys.GAME_EVENT, CraftNamespacedKey.toMinecraft(key));
        this.handle = handle;
    }

    @Override
    public net.minecraft.world.event.GameEvent getHandle() {
        return this.handle;
    }

    @NotNull
    public NamespacedKey getKey() {
        return this.key;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CraftGameEvent)) {
            return false;
        }
        return this.getKey().equals((Object)((GameEvent)other).getKey());
    }

    public int hashCode() {
        return this.getKey().hashCode();
    }

    public String toString() {
        return "CraftGameEvent{key=" + String.valueOf(this.key) + "}";
    }

	@Override
	public int getRange() {
		return this.handle.notificationRadius();
	}

	@Override
	public int getVibrationLevel() {
        return Vibrations.getFrequency(this.handleKey);
	}

}
