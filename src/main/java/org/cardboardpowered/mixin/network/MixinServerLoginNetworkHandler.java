package org.cardboardpowered.mixin.network;

import com.javazilla.bukkitfabric.interfaces.IMixinClientConnection;
import com.javazilla.bukkitfabric.interfaces.IMixinMinecraftServer;
import com.javazilla.bukkitfabric.interfaces.IMixinPlayerManager;
import com.javazilla.bukkitfabric.interfaces.IMixinServerLoginNetworkHandler;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import io.netty.channel.local.LocalAddress;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.encryption.NetworkEncryptionException;
import net.minecraft.network.encryption.NetworkEncryptionUtils;
import net.minecraft.network.packet.c2s.login.EnterConfigurationC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.network.state.ConfigurationStates;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerLoginNetworkHandler.State;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Uuids;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.Waitable;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.cardboardpowered.interfaces.INetworkConfiguration;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.PrivateKey;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("deprecation")
@Mixin(value = ServerLoginNetworkHandler.class, priority = 999)
public abstract class MixinServerLoginNetworkHandler implements IMixinServerLoginNetworkHandler {

	@Shadow @Nullable private String profileName;
	@Shadow
	abstract void startVerify(GameProfile profile);
	@Shadow private byte[] nonce = new byte[4];
	@Shadow private MinecraftServer server;
	@Shadow public ClientConnection connection;
	@Shadow private ServerLoginNetworkHandler.State state;
	@Shadow private GameProfile profile;
	
	@Shadow private static AtomicInteger NEXT_AUTHENTICATOR_THREAD_ID;
	
	public ServerPlayerEntity delayedPlayer;

	@Override
	public ClientConnection cb_get_connection() {
		return connection;
	}

	private Logger LOGGER_BF = LogManager.getLogger("Bukkit|ServerLoginNetworkHandler");
	public String hostname = ""; // Bukkit - add field
	private long theid = 0;
	
	// Cardboard: field added by bukkit
	private ServerPlayerEntity player;

	//@Shadow
	// private PlayerPublicKey.PublicKeyData publicKeyData;

	
		
	//	setBF(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/class_2535;Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V!
	//		 (Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/class_2535;ZLorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V
	//		 (Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/class_2535;Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V 
	
	//@Inject(at = @At("TAIL"), method = "<init>*")
	//public void setBF(MinecraftServer minecraftserver, ClientConnection networkmanager, boolean something, CallbackInfo ci) {
	//	BukkitFabricMod.NETWORK_CACHE.add((ServerLoginNetworkHandler) (Object) this);
	//}

	@Override
	public String getHostname() {
		return hostname;
	}

	@Override
	public void setHostname(String s) {
		this.hostname = s;
	}

	/**
	 * @author cardboard
	 * @reason Bukkit login changes
	 */
	@Overwrite
	public void onKey(LoginKeyC2SPacket packetIn) {
		Validate.validState(this.state == ServerLoginNetworkHandler.State.KEY, "Unexpected key packet");

		final String s;
		try {
			PrivateKey privatekey = this.server.getKeyPair().getPrivate();
			if(!packetIn.verifySignedNonce(this.nonce, privatekey)) {
				throw new IllegalStateException("Protocol error");
			}

			SecretKey secretKey = packetIn.decryptSecretKey(privatekey);
			Cipher cipher = NetworkEncryptionUtils.cipherFromKey(2, secretKey);
			Cipher cipher1 = NetworkEncryptionUtils.cipherFromKey(1, secretKey);
			s = (new BigInteger(NetworkEncryptionUtils.computeServerId("", this.server.getKeyPair()
					.getPublic(), secretKey))).toString(16);

			this.state = ServerLoginNetworkHandler.State.AUTHENTICATING;
			this.connection.setupEncryption(cipher, cipher1);
		} catch(NetworkEncryptionException cryptexception) {
			throw new IllegalStateException("Protocol error", cryptexception);
		}

		Thread thread = new Thread("User Authenticator #" + theid++) {
			@Override
			public void run() {
				GameProfile gameprofile = profile;
				String name = Objects.requireNonNull(profileName, "Player name not initialized");

				try {
					profile = server.getSessionService().hasJoinedServer(name, s, this.getAddress()).profile();
					if(profile != null) {
						// Fire PlayerPreLoginEvent
						if(!connection.isOpen()) return;
						fireEvents();
					} else if(server.isSingleplayer()) {
						LOGGER_BF.warn("Failed to verify username but will let them in anyway!");
						profile = Uuids.getOfflinePlayerProfile(name);
						startVerify(profile);
					} else {
						disconnect("multiplayer.disconnect.unverified_username");
						LOGGER_BF.error("Username '{}' tried to join with an invalid session", gameprofile.getName());
					}
				} catch(AuthenticationUnavailableException authenticationunavailableexception) {
					if(server.isSingleplayer()) {
						LOGGER_BF.warn("Authentication servers are down but will let them in anyway!");
						profile = Uuids.getOfflinePlayerProfile(name);
						startVerify(profile);
					} else {
						disconnect("multiplayer.disconnect.authservers_down");
						LOGGER_BF.error("Couldn't verify username because servers are unavailable");
					}
				} catch(Exception exception) {
					disconnect("Failed to verify username!");
					LOGGER_BF.log(Level.WARN, "Exception verifying " + name, exception);
				}
			}

			@Nullable
			private InetAddress getAddress() {
				SocketAddress socketaddress = connection.getAddress();
				return server.shouldPreventProxyConnections() && socketaddress instanceof InetSocketAddress ? ((InetSocketAddress) socketaddress).getAddress() : null;
			}
		};
		// TODO: thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LogManager.getLogger("BukkitServerLoginManager")));
		thread.start();
	}

	public void fireEvents() throws Exception {
		String playerName = profile.getName();
		java.net.InetAddress address;
		if(connection.getAddress() instanceof LocalAddress) {
			address = InetAddress.getLocalHost();
		} else address = ((java.net.InetSocketAddress) connection.getAddress()).getAddress();
		UUID uniqueId = profile.getId();
		final org.bukkit.craftbukkit.CraftServer server = CraftServer.INSTANCE;

		AsyncPlayerPreLoginEvent asyncEvent = new AsyncPlayerPreLoginEvent(playerName, address, uniqueId);
		server.getPluginManager().callEvent(asyncEvent);

		if(PlayerPreLoginEvent.getHandlerList().getRegisteredListeners().length != 0) {
			final PlayerPreLoginEvent event = new PlayerPreLoginEvent(playerName, address, uniqueId);
			if(asyncEvent.getResult() != PlayerPreLoginEvent.Result.ALLOWED)
				event.disallow(asyncEvent.getResult(), asyncEvent.getKickMessage());

			Waitable<PlayerPreLoginEvent.Result> waitable = new Waitable<PlayerPreLoginEvent.Result>() {
				@Override
				protected PlayerPreLoginEvent.Result evaluate() {
					server.getPluginManager().callEvent(event);
					return event.getResult();
				}
			};

			((IMixinMinecraftServer) CraftServer.server).getProcessQueue().add(waitable);
			if(waitable.get() != PlayerPreLoginEvent.Result.ALLOWED) {
				disconnect(event.getKickMessage());
				return;
			}
		} else {
			if(asyncEvent.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
				disconnect(asyncEvent.getKickMessage());
				return;
			}
		}
		LOGGER_BF.info("UUID of player {} is {}", profile.getName(), profile.getId());
		startVerify(profile);
	}

	public void disconnect(String s) {
		try {
			Text text = Text.of(s);
			LOGGER_BF.info("Disconnecting BUKKITFABRIC_TODO: " + s);
			this.connection.send(new LoginDisconnectS2CPacket(text));
			this.connection.disconnect(text);
		} catch(Exception exception) {
			LOGGER_BF.error("Error whilst disconnecting player", exception);
		}
	}

	private ServerPlayerEntity cardboard_player;
	
	@Override
	public ServerPlayerEntity cardboard$get_player() {
		
		if (null == player) {
			player = cardboard_player;
		}
		
		return player;
	}
    
    /*@Overwrite
    private static PlayerPublicKey getVerifiedPublicKey(@Nullable PlayerPublicKey.PublicKeyData publicKeyData, UUID playerUuid, SignatureVerifier servicesSignatureVerifier, boolean shouldThrowOnMissingKey) throws PlayerPublicKey.PublicKeyException {
        if (publicKeyData == null) {
        	BukkitFabricMod.LOGGER.info("PUBLIC KEY DATA IS NULL!!");
            if (shouldThrowOnMissingKey) {
                throw new PlayerPublicKey.PublicKeyException(PlayerPublicKey.EXPIRED_PUBLIC_KEY_TEXT);
            }
            return null;
        }
        return PlayerPublicKey.verifyAndDecode(servicesSignatureVerifier, playerUuid, publicKeyData, Duration.ZERO);
    }*/

	/*@Inject(at = @At("HEAD"), method = "tickVerify")
	public void cardboard$tesT() {
		
	}*/
	
	/**
	 * @author cardboard mod
	 * @reason We create the ServerPlayerEntity using attemptLogin
	 */
	@Overwrite
    private void tickVerify(GameProfile profile) {
        PlayerManager playerManager = this.server.getPlayerManager();
        Text text = null; // playerManager.checkCanJoin(this.connection.getAddress(), profile);
        
        IMixinPlayerManager pm = ((IMixinPlayerManager) this.server.getPlayerManager());
        ServerPlayerEntity s = pm.attemptLogin((ServerLoginNetworkHandler) (Object) this, this.profile, null, hostname);
        
        this.cardboard_player = s;
        this.player = s;
        
        //this.player = playerlist.canPlayerLogin(this, profile);
        //if (this.player != null) {
        
        if (text != null) {
            this.disconnect(text);
        } else {
            boolean bl;
            if (this.server.getNetworkCompressionThreshold() >= 0 && !this.connection.isLocal()) {

                this.connection.send(new LoginCompressionS2CPacket(this.server.getNetworkCompressionThreshold()), PacketCallbacks.always(() -> this.connection.setCompressionThreshold(this.server.getNetworkCompressionThreshold(), true)));
            }
            if (bl = playerManager.disconnectDuplicateLogins(profile)) {
                this.state = State.WAITING_FOR_DUPE_DISCONNECT;
            } else {
                this.sendSuccessPacket(profile);
            }
        }
    }
    
    @Shadow
    private void sendSuccessPacket(GameProfile profile) {
        this.state = State.PROTOCOL_SWITCHING;
        this.connection.send(new LoginSuccessS2CPacket(profile, true));
    }
	

	/*
	@Redirect(at = @At(value = "INVOKE",
			target = "Lnet/minecraft/server/PlayerManager;checkCanJoin(Ljava/net/SocketAddress;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/text/Text;"),
			method = "tickVerify")
	public Text acceptPlayer_checkCanJoin(PlayerManager man, SocketAddress a, GameProfile b) {
		System.out.println("CHECK CAN JOIN!");
		
		
		IMixinPlayerManager pm = ((IMixinPlayerManager) this.server.getPlayerManager());
        
		ServerPlayerEntity s = pm.attemptLogin((ServerLoginNetworkHandler) (Object) this, this.profile, null, hostname);

		cardboard_player = s;
		
		this.player = s;

		return null;
	}
	*/
	
	
	
	// Lnet/minecraft/server/PlayerManager;createPlayer(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/network/packet/c2s/common/SyncedClientOptions;)Lnet/minecraft/server/network/ServerPlayerEntity;

	
	//@Redirect(at = @At(value = "INVOKE",
	//		         target = "Lnet/minecraft/server/PlayerManager;createPlayer(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/network/packet/c2s/common/SyncedClientOptions;)Lnet/minecraft/server/network/ServerPlayerEntity;"),
	//		         method = "acceptPlayer")
	//public ServerPlayerEntity acceptPlayer_createPlayer(PlayerManager man, GameProfile a/*, PlayerPublicKey key*/) {
	//	return cardboard_player;
	//}
	
	// 1.19.2: target = "Lnet/minecraft/server/PlayerManager;createPlayer(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/network/encryption/PlayerPublicKey;)Lnet/minecraft/server/network/ServerPlayerEntity;"),
	// 1.19.4: target = "Lnet/minecraft/server/PlayerManager;createPlayer(Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/server/network/ServerPlayerEntity;"),
	// @Redirect(at = @At(value = "INVOKE",
	//         target = "Lnet/minecraft/server/PlayerManager;createPlayer(Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/server/network/ServerPlayerEntity;"),
	//         method = "acceptPlayer")
	// public ServerPlayerEntity acceptPlayer_createPlayer(PlayerManager man, GameProfile a/*, PlayerPublicKey key*/) {
	//     return cardboard_player;
	// }

	/*
	@Inject(at = @At("HEAD"), method = "onHello", cancellable = true)
	public void spigotHello1(LoginHelloC2SPacket p, CallbackInfo ci) {
		// if (null == this.publicKeyData) {
		// this.publicKeyData = p.publicKey().orElse(null);
		//}
		if(state != State.HELLO) {
			LOGGER_BF.info("Cancel onHello because state is " + state);
			startVerify(profile);
			ci.cancel();
		}
	}
	*/

	/*
    @Redirect(method = "onHello",
            at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerLoginNetworkHandler;startVerify(Lcom/mojang/authlib/GameProfile;)V",
                    ordinal = 1))
    private void cardboard$handle_hello(ServerLoginNetworkHandler instance, GameProfile gameProfile) {
        // Cardboard start
        class Handler extends Thread {

            public Handler() {
                super("User Authenticator #" + NEXT_AUTHENTICATOR_THREAD_ID.incrementAndGet());
            }

            @Override
            public void run() {
                try {
                    GameProfile gameprofile = Uuids.getOfflinePlayerProfile(profileName);

                    //callPlayerPreLoginEvents(gameprofile);
                    fireEvents();
                    
                    BukkitFabricMod.LOGGER.info("UUID of player " + gameprofile.getName() + " is " + gameprofile.getId());

                    startVerify(gameprofile);
                } catch (Exception ex) {
                    disconnect("Failed to verify username!");
                    BukkitFabricMod.LOGGER.log(java.util.logging.Level.WARNING, "Exception verifying " + profileName, ex);
                }
            }
        }
        Handler thread = new Handler();
        // thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER_BF));
        thread.start();
        // Cardboard end
    }
    */

	@Inject(at = @At("TAIL"), method = "onHello")
	public void spigotHello(LoginHelloC2SPacket packetlogininstart, CallbackInfo ci) {
		if(!(this.server.isOnlineMode() && !this.connection.isLocal())) {
			// Spigot start
			new Thread("User Authenticator #" + theid++) {
				@Override
				public void run() {
					try {
						initUUID();
						fireEvents();
					} catch(Exception ex) {
						disconnect("Failed to verify username!");
						CraftServer.INSTANCE.getLogger()
								.log(java.util.logging.Level.WARNING, "Exception verifying " + profile.getName(), ex);
					}
				}
			}.start();
			// Spigot end
		}
	}

	// Spigot start
	public void initUUID() {
		UUID uuid;
		if(((IMixinClientConnection) connection).getSpoofedUUID() != null)
			uuid = ((IMixinClientConnection) connection).getSpoofedUUID();
		else {
			// Note: PlayerEntity (1.18) -> DynamicSerializableUuid (1.19) -> Uuids (1.19.4)
			uuid = Uuids.getOfflinePlayerUuid(this.profile.getName());
		}

		this.profile = new GameProfile(uuid, this.profile.getName());

		if(((IMixinClientConnection) connection).getSpoofedProfile() != null)
			for(com.mojang.authlib.properties.Property property : ((IMixinClientConnection) connection).getSpoofedProfile())
				this.profile.getProperties().put(property.name(), property);
	}
	// Spigot end

    @Shadow
    private boolean transferred;
     
    /**
     * @author cardboard mod
     * @reason TODO: Injection here fails
     */
    @Overwrite
    public void onEnterConfiguration(EnterConfigurationC2SPacket packet) {
        Validate.validState((this.state == State.PROTOCOL_SWITCHING ? 1 : 0) != 0, (String)"Unexpected login acknowledgement packet", (Object[])new Object[0]);
        this.connection.transitionOutbound(ConfigurationStates.S2C);
        ConnectedClientData commonlistenercookie = ConnectedClientData.createDefault(Objects.requireNonNull(this.profile), this.transferred);
        ServerConfigurationNetworkHandler networkConfig = new ServerConfigurationNetworkHandler(this.server, this.connection, commonlistenercookie);
       
        System.out.println("networkConfig: setting player");
        if(cardboard_player != null) {
			((INetworkConfiguration) networkConfig).cardboard_setPlayer(cardboard_player);
		}
        
        this.connection.transitionInbound(ConfigurationStates.C2S, networkConfig);
        networkConfig.sendConfigurations();
        this.state = State.ACCEPTED;
    }

    /*
	@Inject(method = "onEnterConfiguration",
			locals = LocalCapture.CAPTURE_FAILEXCEPTION,
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/network/ClientConnection;setPacketListener(Lnet/minecraft/network/listener/PacketListener;)V"
				)
			)
	private void onCreateNetworkConfig(EnterConfigurationC2SPacket packet, CallbackInfo ci, ConnectedClientData connectedClientData, ServerConfigurationNetworkHandler networkConfig) {
		if(cardboard_player != null) {
			((INetworkConfiguration) networkConfig).cardboard_setPlayer(cardboard_player);
		}
	}
	*/

	@Shadow
	public void disconnect(Text t) {}

}
