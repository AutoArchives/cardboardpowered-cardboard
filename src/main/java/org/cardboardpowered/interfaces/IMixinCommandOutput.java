/**
 * The Bukkit for Fabric Project
 * Copyright (C) 2020-2025 Isaiah & Cardboard contributors
 */
package org.cardboardpowered.interfaces;

import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.command.CommandSender;

public interface IMixinCommandOutput {

	/**
	 */
    default CommandSender getBukkitSender(CommandSourceStack source) {

    	if (source.isPlayer()) {
    		// Cardboard Note: Redirect ServerPlayerEntity$3 to ServerPlayerEntity
    		return ( (IMixinCommandOutput) source.getPlayer() ).getBukkitSender(source);
    	}

    	if (null != source.entity) {
    		return ( (IMixinCommandOutput) source.getEntity() ).getBukkitSender(source);
    	}
    	
    	CommandSource output = source.source;
    	
    	// Memic Default Error
    	String msg1 = " does not define or inherit an implementation of the resolved method 'org.bukkit.command.CommandSender";
    	String msg2 = " getBukkitSender(net.minecraft.class_2168/ServerCommandSource)' of interface IMixinCommandOutput.";
    	throw new AbstractMethodError(
    			"Receiver class " + output.getClass().getName() + msg1 +  msg2
    	);
    }

}