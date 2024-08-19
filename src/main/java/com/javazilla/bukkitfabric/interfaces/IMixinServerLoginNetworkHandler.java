package com.javazilla.bukkitfabric.interfaces;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;

public interface IMixinServerLoginNetworkHandler {

    String getHostname();

    void setHostname(String hostname);

	ClientConnection cb_get_connection();

	ServerPlayerEntity cardboard$get_player();

}