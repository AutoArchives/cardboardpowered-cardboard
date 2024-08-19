package org.cardboardpowered.mixin;

import org.bukkit.event.Event;
import org.bukkit.plugin.SimplePluginManager;
import org.cardboardpowered.CardboardConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.javazilla.bukkitfabric.BukkitFabricMod;

@Mixin(value = SimplePluginManager.class, remap = false)
public class MixinSimplePluginManager {

	
	@Inject(at = @At("HEAD"), method = "callEvent")
	public void cardboard$call_event_debug(Event event, CallbackInfo ci) {
		if (CardboardConfig.DEBUG_EVENT_CALL) {
			// Print debug info
			String name = event.getEventName();

			if (name.contains("EntityAirChangeEvent")) {
				// Avoid spam
				return;
			}

			BukkitFabricMod.LOGGER.info("debug: callEvent: " + event.getEventName());
		}
	}
	
}
