package org.bukkit.craftbukkit;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import org.bukkit.GameEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.craftbukkit.util.Handleable;
import org.jetbrains.annotations.NotNull;

public class CraftGameEvent extends GameEvent implements Handleable<net.minecraft.world.level.gameevent.GameEvent> {

    private final NamespacedKey key;
    private final ResourceKey<net.minecraft.world.level.gameevent.GameEvent> handleKey;
    private final net.minecraft.world.level.gameevent.GameEvent handle;

    public static GameEvent minecraftToBukkit(net.minecraft.world.level.gameevent.GameEvent minecraft) {
        return (GameEvent)CraftRegistry.minecraftToBukkit(minecraft, Registries.GAME_EVENT);
    }

    public static net.minecraft.world.level.gameevent.GameEvent bukkitToMinecraft(GameEvent bukkit) {
        return (net.minecraft.world.level.gameevent.GameEvent)CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public CraftGameEvent(NamespacedKey key, net.minecraft.world.level.gameevent.GameEvent handle) {
        this.key = key;
        this.handleKey = ResourceKey.create(Registries.GAME_EVENT, CraftNamespacedKey.toMinecraft(key));
        this.handle = handle;
    }

    @Override
    public net.minecraft.world.level.gameevent.GameEvent getHandle() {
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
        return VibrationSystem.getGameEventFrequency(this.handleKey);
	}

}
