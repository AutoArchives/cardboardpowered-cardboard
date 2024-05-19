package org.cardboardpowered.mixin.network;

import com.javazilla.bukkitfabric.interfaces.IMixinClientConnection;
import com.javazilla.bukkitfabric.interfaces.IMixinServerLoginNetworkHandler;

import me.isaiah.common.ConnectionState;
import me.isaiah.common.GameVersion;
import me.isaiah.common.ICommonMod;
import me.isaiah.common.cmixin.IMixinMinecraftServer;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.server.network.ServerHandshakeNetworkHandler;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ServerHandshakeNetworkHandler.class)
public class MixinServerHandshakeNetworkHandler {

    private static final com.google.gson.Gson gson = new com.google.gson.Gson(); // Spigot

    @Shadow
    public ClientConnection connection;

    @Inject(at = @At("TAIL"), method = "onHandshake")
    public void onHandshake_Bungee(HandshakeC2SPacket packet, CallbackInfo ci) {
    	
    	IMixinMinecraftServer mc = (IMixinMinecraftServer) ICommonMod.getIServer().getMinecraft();
    	
    	if (mc.IC$get_connection_state(packet) == ConnectionState.LOGIN) {
            GameVersion ver = GameVersion.INSTANCE;

            if (packet.protocolVersion() > ver.getProtocolVersion()) {
            } else if (packet.protocolVersion() < ver.getProtocolVersion()) {
            } else {
                if (org.spigotmc.SpigotConfig.bungee) {
                    String[] split = packet.address.split("\00");
                    if ( split.length == 3 || split.length == 4 ) {
                       // TODO 1.17ify packethandshakinginsetprotocol.address = split[0];
                        connection.address = new java.net.InetSocketAddress(split[1], ((java.net.InetSocketAddress) connection.getAddress()).getPort());
                        ((IMixinClientConnection)connection).setSpoofedUUID(fromString( split[2] ));
                    } else {
                        return;
                    }
                    if ( split.length == 4 ) ((IMixinClientConnection)connection).setSpoofedProfile(gson.fromJson(split[3], com.mojang.authlib.properties.Property[].class));
                }
                ((IMixinServerLoginNetworkHandler)((ServerLoginNetworkHandler) this.connection.getPacketListener())).setHostname(packet.address + ":" + packet.port()); // Bukkit - set hostname
            }
        }
    }

    private UUID fromString(final String input) {
        return UUID.fromString(input.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }

}
