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

import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public interface IMixinPlayerManager {

	/**
	 * Replaced by {@link #respawn(ServerPlayerEntity, boolean, RemovalReason, RespawnReason, Location)}
	 * 
	 * @deprecated Use respawn instead
	 */
    ServerPlayerEntity moveToWorld(ServerPlayerEntity player, ServerWorld world, boolean flag, Location location, boolean avoidSuffocation);

    ServerPlayerEntity attemptLogin(ServerLoginNetworkHandler loginlistener, GameProfile gameprofile, PlayerPublicKey profilepublickey, String hostname);

    void sendScoreboardBF(ServerScoreboard newboard, ServerPlayerEntity handle);

    /**
     * Replaces {@link #moveToWorld(ServerPlayerEntity, ServerWorld, boolean, Location, boolean)}
     */
	ServerPlayerEntity respawn(ServerPlayerEntity player, boolean keepInventory, RemovalReason reason,
			RespawnReason eventReason, Location location);

	/**
	 * paper login api
	 */
	PlayerManager_LoginResult cardboard$canPlayerLogin(Text vanilla, PlayerConfigEntry nameAndId);

}