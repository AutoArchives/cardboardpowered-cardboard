package org.cardboardpowered.mixin.entity;

import org.bukkit.command.CommandSender;
import org.spongepowered.asm.mixin.Mixin;

import org.cardboardpowered.interfaces.IMixinCommandOutput;
import org.cardboardpowered.interfaces.IMixinEntity;
import org.cardboardpowered.interfaces.IMixinServerEntityPlayer;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(targets = "net/minecraft/server/network/ServerPlayerEntity$3")
public class MixinServerPlayerEntityCommandSender implements IMixinCommandOutput {

	/**
	 */
	@Override
    public CommandSender getBukkitSender(ServerCommandSource source) {
		// System.out.println("DEBUG: getBukkitSender!");
		
		if (source.isExecutedByPlayer()) {
			ServerPlayerEntity plr = source.getPlayer();
			return ((IMixinServerEntityPlayer) plr) .getBukkit();
		}
		
		return ((IMixinEntity) source.entity).getBukkitEntity();
		
		// return ( (IMixinEntity)  ((ServerPlayerEntity) (Object) this) ) .getBukkitEntity();
    }
	
}
