package org.cardboardpowered.interfaces;

import java.util.UUID;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;

public interface IMixinServerLoginNetworkHandler {

    String getHostname();

    void setHostname(String hostname);

	ClientConnection cb_get_connection();

	ServerPlayerEntity cardboard$get_player();

	/**
	 * @since 1.21.10
	 */
	UUID cardboard$requestedUuid();

	/**
	 */
	String cardboard$profileName();

	/**
	 */
	boolean cardboard$transferred();

}