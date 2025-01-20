package org.cardboardpowered.mixin.entity;

import org.bukkit.command.CommandSender;
import org.spongepowered.asm.mixin.Mixin;

import com.javazilla.bukkitfabric.interfaces.IMixinCommandOutput;
import com.javazilla.bukkitfabric.interfaces.IMixinEntity;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(targets = "net/minecraft/server/network/ServerPlayerEntity$3")
public class MixinServerPlayerEntityCommandSender implements IMixinCommandOutput {

	/**
	 */
	@Override
    public CommandSender getBukkitSender(ServerCommandSource source) {
		System.out.println("DEBUG: getBukkitSender!");
		
		ServerPlayerEntity plr = source.getPlayer();
		
		return ((IMixinEntity) plr) .getBukkitEntity();
		
		// return ( (IMixinEntity)  ((ServerPlayerEntity) (Object) this) ) .getBukkitEntity();
    }
	
}
