package org.cardboardpowered.interfaces;

import java.util.UUID;

import org.bukkit.craftbukkit.CraftServer;
import org.cardboardpowered.impl.world.WorldImpl;

import net.minecraft.world.level.ServerWorldProperties;

public interface IServerWorld {

    ServerWorldProperties cardboard_worldProperties();

	default WorldImpl getWorld() {
		return null;
	}

	default CraftServer getCraftServer() {
		return CraftServer.INSTANCE;
	}
	
	public void cardboard$set_uuid(UUID id);
	
	public UUID cardboard$get_uuid();

}