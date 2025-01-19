/**
 * The Bukkit for Fabric Project
 * Copyright (C) 2020-2025 Isaiah & Cardboard contributors
 */
package com.javazilla.bukkitfabric.interfaces;

import org.bukkit.command.CommandSender;

import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;

public interface IMixinCommandOutput {

	/**
	 */
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