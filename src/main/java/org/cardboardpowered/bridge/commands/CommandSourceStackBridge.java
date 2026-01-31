/**
 * Cardboard - The Bukkit for Fabric Project
 * Copyright (C) 2020-2025 Isaiah and contributors
 */
package org.cardboardpowered.bridge.commands;

import org.bukkit.command.CommandSender;

/**
 * Injection Interface for ServerCommandSource.
 * 
 * @see {@link org.cardboardpowered.mixin.MixinServerCommandSource}
 */
public interface CommandSourceStackBridge {

	/**
	 */
    CommandSender getBukkitSender();

}