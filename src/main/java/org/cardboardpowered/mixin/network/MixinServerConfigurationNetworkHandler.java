package org.cardboardpowered.mixin.network;

import java.net.SocketAddress;

import org.bukkit.craftbukkit.CraftServer;
import org.cardboardpowered.interfaces.INetworkConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.c2s.config.ReadyC2SPacket;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

@Mixin(ServerConfigurationNetworkHandler.class)
public class MixinServerConfigurationNetworkHandler implements INetworkConfiguration {

	@Unique
	private ServerPlayerEntity cardboard$replacementPlayer;
	
	@Shadow
	private SyncedClientOptions syncedOptions;
	
	@Redirect(at = @At(value = "INVOKE",
	         target = "Lnet/minecraft/server/PlayerManager;checkCanJoin(Ljava/net/SocketAddress;Lnet/minecraft/server/PlayerConfigEntry;)Lnet/minecraft/text/Text;"),
	         method = "onReady(Lnet/minecraft/network/packet/c2s/config/ReadyC2SPacket;)V")
	public Text cardboard$onReady_checkCanJoin_redirect(PlayerManager man, SocketAddress a, PlayerConfigEntry b) {
		// Cardboard: Let's take over vanilla player creation.
		// TODO: check on CraftEventFactory.handleLoginResult(
		return null;
	}
	
	@Inject(at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/server/PlayerManager;checkCanJoin(Ljava/net/SocketAddress;Lnet/minecraft/server/PlayerConfigEntry;)Lnet/minecraft/text/Text;",
			shift = At.Shift.AFTER
			),
			method = "onReady(Lnet/minecraft/network/packet/c2s/config/ReadyC2SPacket;)V", cancellable = true)
	public void cardboard$onReady_checkCanJoin_after(ReadyC2SPacket packet, CallbackInfo ci) {
		// Cardboard: Let's take over Vanilla Player Creation.
		if (null != cardboard$replacementPlayer) {
			cardboard$replacementPlayer.setClientOptions(syncedOptions);
			CraftServer.console.getPlayerManager().onPlayerConnect(((ServerConfigurationNetworkHandler)(Object)this).connection, cardboard$replacementPlayer, ((ServerConfigurationNetworkHandler)(Object)this).createClientData(this.syncedOptions));
			ci.cancel();
			return;
		}
	}

	@Override
	public void cardboard_setPlayer(ServerPlayerEntity entity) {
		this.cardboard$replacementPlayer = entity;
	}
	
}
