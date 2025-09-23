/**
 * Cardboard - Paper API for Fabric
 * Copyright (C) 2020-2025
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either 
 * version 3 of the License, or (at your option) any later version.
 */
package org.cardboardpowered.interfaces;

import java.util.Set;

import org.bukkit.Location;

import io.papermc.paper.connection.PlayerGameConnection;
import net.minecraft.entity.player.PlayerPosition;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.PositionFlag;

public interface IMixinPlayNetworkHandler {

    void chat(String message, boolean notDeprecated);

    void teleport(Location location);

    boolean isDisconnected();

	ClientConnection cb_get_connection();

	void internalTeleport(PlayerPosition positionmoverotation, Set<PositionFlag> set);

	/**
	 */
	PlayerGameConnection cardboard$playerGameConnection();

}