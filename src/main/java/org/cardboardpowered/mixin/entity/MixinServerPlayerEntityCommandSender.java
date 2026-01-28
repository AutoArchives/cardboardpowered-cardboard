package org.cardboardpowered.mixin.entity;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.command.CommandSender;
import org.spongepowered.asm.mixin.Mixin;

import org.cardboardpowered.interfaces.IMixinCommandOutput;
import org.cardboardpowered.bridge.world.entity.EntityBridge;
import org.cardboardpowered.bridge.server.level.ServerPlayerBridge;

@Mixin(targets = "net/minecraft/server/level/ServerPlayer$3")
public class MixinServerPlayerEntityCommandSender implements IMixinCommandOutput {

	/**
	 */
	@Override
    public CommandSender getBukkitSender(CommandSourceStack source) {
		// System.out.println("DEBUG: getBukkitSender!");
		
		if (source.isPlayer()) {
			ServerPlayer plr = source.getPlayer();
			return ((ServerPlayerBridge) plr) .getBukkitEntity();
		}
		
		return ((EntityBridge) source.entity).getBukkitEntity();
		
		// return ( (IMixinEntity)  ((ServerPlayerEntity) (Object) this) ) .getBukkitEntity();
    }
	
}
