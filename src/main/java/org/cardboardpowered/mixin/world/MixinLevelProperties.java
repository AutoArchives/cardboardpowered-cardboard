package org.cardboardpowered.mixin.world;

import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.cardboardpowered.bridge.world.level.storage.PrimaryLevelDataBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// @MixinInfo(events = {"ThunderChangeEvent","WeatherChangeEvent"})
@Mixin(PrimaryLevelData.class)
public class MixinLevelProperties implements PrimaryLevelDataBridge {

    @Shadow
    private LevelSettings settings;

    @Inject(at = @At("HEAD"), method = "setThundering")
    public void thunder(boolean flag, CallbackInfo info) {
        PrimaryLevelData p = (PrimaryLevelData)(Object) this;
        if (p.isThundering() == flag)
            return;
        World world = Bukkit.getWorld(p.getLevelName());
        if (world != null) {
            ThunderChangeEvent thunder = new ThunderChangeEvent(world, flag);
            Bukkit.getServer().getPluginManager().callEvent(thunder);
            if (thunder.isCancelled())
                return;
        }
    }

    @Inject(at = @At("HEAD"), method = "setRaining")
    public void rain(boolean flag, CallbackInfo info) {
        PrimaryLevelData p = (PrimaryLevelData)(Object) this;
        if (p.isRaining() == flag)
            return;
        World world = Bukkit.getWorld(p.getLevelName());
        if (world != null) {
            WeatherChangeEvent event = new WeatherChangeEvent(world, flag);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled())
                return;
        }
    }

    @Override
    public void checkName(String name) {
    	if (!this.settings.levelName.equals(name)) {
    		this.settings.levelName = name;
    	}
    }

}