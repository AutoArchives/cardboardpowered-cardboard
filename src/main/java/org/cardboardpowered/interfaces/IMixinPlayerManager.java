/**
 * Cardboard
 * Copyright (C) 2020-2023
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.cardboardpowered.interfaces;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerRespawnEvent.RespawnReason;
import org.cardboardpowered.extras.PlayerManager_LoginResult;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.ProfilePublicKey;

public interface IMixinPlayerManager {

	/**
	 * Replaced by {@link #respawn(ServerPlayer, boolean, RemovalReason, RespawnReason, Location)}
	 * 
	 * @deprecated Use respawn instead
	 */
    ServerPlayer moveToWorld(ServerPlayer player, ServerLevel world, boolean flag, Location location, boolean avoidSuffocation);

    ServerPlayer attemptLogin(ServerLoginPacketListenerImpl loginlistener, GameProfile gameprofile, ProfilePublicKey profilepublickey, String hostname);

    void sendScoreboardBF(ServerScoreboard newboard, ServerPlayer handle);

    /**
     * Replaces {@link #moveToWorld(ServerPlayer, ServerLevel, boolean, Location, boolean)}
     */
	ServerPlayer respawn(ServerPlayer player, boolean keepInventory, RemovalReason reason,
			RespawnReason eventReason, Location location);

	/**
	 * paper login api
	 */
	PlayerManager_LoginResult cardboard$canPlayerLogin(Component vanilla, NameAndId nameAndId);

}