package org.cardboardpowered.mixin.network;

import java.net.SocketAddress;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.authlib.GameProfile;

import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

@Mixin(ServerConfigurationNetworkHandler.class)
public class MixinServerConfigurationNetworkHandler {

	
	@Redirect(at = @At(value = "INVOKE",
	         target = "Lnet/minecraft/server/PlayerManager;checkCanJoin(Ljava/net/SocketAddress;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/text/Text;"),
	         method = "Lnet/minecraft/network/listener/ServerConfigurationPacketListener;onReady(Lnet/minecraft/network/packet/c2s/config/ReadyC2SPacket;)V")
	public Text cardboard$onReady_checkCanJoin(PlayerManager man, SocketAddress a, GameProfile b) {
		
		// cardboard - bukkit: login checks already completed by now
		
		return null;
	}
	
}
