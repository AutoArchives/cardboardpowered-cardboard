package org.cardboardpowered.mixin.network;

import org.cardboardpowered.interfaces.IMixinClientConnection;
import org.cardboardpowered.interfaces.IMixinServerLoginNetworkHandler;

import me.isaiah.common.GameVersion;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.handshake.ClientIntent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.server.network.ServerHandshakePacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ServerHandshakePacketListenerImpl.class)
public class MixinServerHandshakeNetworkHandler {

    private static final com.google.gson.Gson gson = new com.google.gson.Gson(); // Spigot

    @Shadow
    public Connection connection;

    @Inject(at = @At("TAIL"), method = "handleIntention")
    public void onHandshake_Bungee(ClientIntentionPacket packet, CallbackInfo ci) {
    	if (packet.intention() == ClientIntent.LOGIN) {
            GameVersion ver = GameVersion.INSTANCE;

            if (packet.protocolVersion() > ver.getProtocolVersion()) {
            } else if (packet.protocolVersion() < ver.getProtocolVersion()) {
            } else {
                if (org.spigotmc.SpigotConfig.bungee) {
                    String[] split = packet.hostName.split("\00");
                    if ( split.length == 3 || split.length == 4 ) {
                       // TODO 1.17ify packethandshakinginsetprotocol.address = split[0];
                        connection.address = new java.net.InetSocketAddress(split[1], ((java.net.InetSocketAddress) connection.getRemoteAddress()).getPort());
                        ((IMixinClientConnection)connection).setSpoofedUUID(fromString( split[2] ));
                    } else {
                        return;
                    }
                    if ( split.length == 4 ) ((IMixinClientConnection)connection).setSpoofedProfile(gson.fromJson(split[3], com.mojang.authlib.properties.Property[].class));
                }
                ((IMixinServerLoginNetworkHandler)((ServerLoginPacketListenerImpl) this.connection.getPacketListener())).setHostname(packet.hostName + ":" + packet.port()); // Bukkit - set hostname
            }
        }
    }

    private UUID fromString(final String input) {
        return UUID.fromString(input.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }

}
