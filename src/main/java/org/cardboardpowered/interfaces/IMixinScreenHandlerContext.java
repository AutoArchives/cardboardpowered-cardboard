/**
 * The Bukkit for Fabric Project
 * Copyright (C) 2020-2025 CardboardPowered.org and contributors
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either 
 * version 3 of the License, or (at your option) any later version.
 */
package org.cardboardpowered.interfaces;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 */
public interface IMixinScreenHandlerContext {

    org.bukkit.Location getLocation();

    World getWorld();

    BlockPos getPosition();

}