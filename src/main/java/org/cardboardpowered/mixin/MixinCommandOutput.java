package org.cardboardpowered.mixin;

import org.bukkit.command.CommandSender;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;

import com.javazilla.bukkitfabric.interfaces.IMixinCommandOutput;

@Mixin(CommandOutput.class)
public interface MixinCommandOutput extends IMixinCommandOutput {

	// @Override
	// public CommandSender getBukkitSender(ServerCommandSource source);

	@Override
	default CommandSender getBukkitSender(ServerCommandSource source) {
		if (source.isExecutedByPlayer()) {
			// Cardboard Note: Redirect ServerPlayerEntity$3 to ServerPlayerEntity
			return ( (IMixinCommandOutput) source.getPlayer() ).getBukkitSender(source);
		}

		if (null != source.entity) {
			return ( (IMixinCommandOutput) source.getEntity() ).getBukkitSender(source);
		}
			
		CommandOutput output = source.output;
		
		// Memic Default Error
		String msg1 = " does not define or inherit an implementation of the resolved method 'org.bukkit.command.CommandSender";
		String msg2 = " getBukkitSender(net.minecraft.class_2168/ServerCommandSource)' of interface IMixinCommandOutput.";
		throw new AbstractMethodError(
				"Receiver class " + output.getClass().getName() + msg1 +  msg2
		);
	}

}