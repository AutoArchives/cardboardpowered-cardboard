/**
 * Cardboard - Spigot/Paper for Fabric.
 * Copyright (C) 2020-2021 Cardboard contributors
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
package org.cardboardpowered.bridge.server.level;

import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.portal.TeleportTransition.PostTeleportTransition;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.cardboardpowered.impl.entity.CraftPlayer;

import org.cardboardpowered.interfaces.IMixinEntity;
import org.jetbrains.annotations.Nullable;

public interface ServerPlayerBridge extends IMixinEntity {

    void reset();

    BlockPos getSpawnPoint(Level world);

    void closeHandledScreen();

    int cardboard$nextContainerCounter();

    void setConnectionBF(Connection connection);

    Connection getConnectionBF();

    void setBukkit(CraftPlayer plr);

    public CraftPlayer getBukkit();

	void spawnIn(ServerLevel worldserver1);

	void copyFrom_unused(ServerPlayer entityplayer, boolean flag);

	void spigot$forceSetPositionRotation(double x, double y, double z, float yaw, float pitch);

	@Nullable
	TeleportTransition findRespawnPositionAndUseSpawnBlock(boolean useCharge,
			PostTeleportTransition postTeleportTransition, @Nullable PlayerRespawnEvent.RespawnReason respawnReason);

    boolean cardboard$drop(boolean dropStack);
}