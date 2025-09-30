/**
 * Cardboard - Spigot/Paper API for Fabric
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
package org.cardboardpowered.mixin.entity;

import java.util.Optional;
//<<<<<<< HEAD
//=======
import java.util.OptionalInt;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.cardboardpowered.CardboardConfig;
import org.cardboardpowered.CardboardMod;
import org.cardboardpowered.impl.entity.CraftPlayer;
import org.cardboardpowered.impl.inventory.CardboardInventoryView;
import org.cardboardpowered.impl.world.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedMainHandEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.MainHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//>>>>>>> upstream/ver/1.20
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.cardboardpowered.interfaces.IMixinCommandOutput;
import org.cardboardpowered.interfaces.IMixinEntity;
import org.cardboardpowered.interfaces.IMixinScreenHandler;
import org.cardboardpowered.interfaces.IMixinServerEntityPlayer;
import org.cardboardpowered.interfaces.IMixinWorld;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.impl.screenhandler.Networking;
//<<<<<<< HEAD
//=======
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
//>>>>>>> upstream/ver/1.20
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity.Respawn;
import net.minecraft.server.network.ServerPlayerEntity.RespawnPos;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
//<<<<<<< HEAD
//=======
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Vec3d;
//>>>>>>> upstream/ver/1.20
import net.minecraft.world.GameRules;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedMainHandEvent;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.MainHand;
import org.cardboardpowered.impl.entity.CraftPlayer;
import org.cardboardpowered.impl.inventory.CardboardInventoryView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(value = ServerPlayerEntity.class, priority = 999)
public abstract class MixinPlayer extends MixinLivingEntity implements IMixinCommandOutput, IMixinServerEntityPlayer  {

	
	@Shadow
	private CommandOutput commandOutput;
	
	public CommandOutput cb$get_command_output() {
		return commandOutput;
	}
	
	public void cb$set_command_output(CommandOutput out) {
		this.commandOutput = out;
	}
	
	public void cb$set_bukkit_command_output(CommandOutput out) {
		// this.commandOutput = out;
		
		this.commandOutput = new CommandOutput() {

			@Override
			public void sendMessage(Text message) {
				out.sendMessage(message);
			}

			@Override
			public boolean shouldBroadcastConsoleToOps() {
				return out.shouldBroadcastConsoleToOps();
			}

			@Override
			public boolean shouldReceiveFeedback() {
				return out.shouldReceiveFeedback();
			}

			@Override
			public boolean shouldTrackOutput() {
				// TODO Auto-generated method stub
				return false;
			}
			
			// @Override
            public CommandSender getBukkitSender(ServerCommandSource wrapper) {
                return ( (IMixinEntity)  ((ServerPlayerEntity) (Object) this) ) .getBukkitEntity();
            	// return ServerPlayerEntity.this.getBukkitEntity();
            }
			
		};
		
	}
	
    private CraftPlayer bukkit;

    public ClientConnection connectionBF;

    @Shadow
    public int screenHandlerSyncId;

    @Override
    public void setBukkit(CraftPlayer plr) {
        this.bukkit = plr;
    }

    @Override
    public CraftPlayer getBukkit() {
        return bukkit;
    }

    @Override
    public CommandSender getBukkitSender(ServerCommandSource wrapper) {
        return bukkit;
    }

    @Override
    public CraftPlayer getBukkitEntity() {
    	if (bukkit == null) {
    		bukkit = (CraftPlayer) CraftEntity.getEntity(CraftServer.INSTANCE, ((ServerPlayerEntity) (Object) this));
    	}
        return bukkit;
    }

    @Override
    public void reset() {
        // TODO Bukkit4Fabric: Auto-generated method stub
    }

    @Override
    public BlockPos getSpawnPoint(World world) {
        return ((ServerWorld)world).getSpawnPos();
    }

    @Inject(at = @At("TAIL"), method = "onDisconnect")
    public void onDisconnect(CallbackInfo ci) {
        // CraftServer.INSTANCE.playerView.remove(this.bukkit);
    }
    
    private ServerWorld cb$from;
    
    @Inject(cancellable = true, at = @At(
    		value = "INVOKE",
    		target = "Lnet/minecraft/world/TeleportTarget;world()Lnet/minecraft/server/world/ServerWorld;"
    ), method = "Lnet/minecraft/server/network/ServerPlayerEntity;teleportTo(Lnet/minecraft/world/TeleportTarget;)Lnet/minecraft/server/network/ServerPlayerEntity;")
    public void cardboard$do_teleport_event(TeleportTarget target, CallbackInfoReturnable<ServerPlayerEntity> ci) {
    	
    	if (CardboardConfig.DEBUG_PLAYER) {
    		CardboardMod.LOGGER.info("DEBUG: ServerPlayerEntity.cardboard$do_teleport_event called");
    	}
    	
    	ServerPlayerEntity thiz = (ServerPlayerEntity) (Object) this;
    	//ServerWorld serverWorld = target.world();
    	// ServerWorld serverWorld2 = thiz.getServerWorld();
    	cb$from = thiz.getWorld(); // Cardboard - store from world

    	Location exit = CraftLocation.toBukkit(target.position(), target.world().getWorld());

    	PlayerTeleportEvent tpEvent = new PlayerTeleportEvent(
    			this.getBukkitEntity(),
    			this.getBukkitEntity().getLocation(),
    			exit,
    			PlayerTeleportEvent.TeleportCause.UNKNOWN
    	);
        Bukkit.getPluginManager().callEvent(tpEvent);

        Location newExit = tpEvent.getTo();
        
        if (tpEvent.isCancelled() || null == newExit) {
        	
        	if (CardboardConfig.DEBUG_PLAYER) {
        		CardboardMod.LOGGER.info("DEBUG: Teleport: EventCanceled?=" + tpEvent.isCancelled() + ", newExit=" + newExit);
        	}
        	
            ci.setReturnValue(null);
            return;
        }
        
        if (!newExit.equals(exit)) {
            // worldserver = ((CraftWorld)newExit.getWorld()).getHandle();
        	
        	// Set our new TeleportTarget
        	target.world = ((CraftWorld)newExit.getWorld()).getHandle();
        	target.position = CraftLocation.toVec3D(newExit);
        	target.velocity = Vec3d.ZERO;
        	target.yaw = newExit.getYaw();
        	target.pitch = newExit.getPitch();
        	
        	if (CardboardConfig.DEBUG_PLAYER) {
        		CardboardMod.LOGGER.info("DEBUG: Teleport: Target=" + target);
        	}
        	
        	/*
            target = new TeleportTarget(
            		((CraftWorld)newExit.getWorld()).getHandle(),
            		CraftLocation.toVec3D(newExit),
            		Vec3d.ZERO,
            		newExit.getYaw(),
            		newExit.getPitch(),
            		// target.missingRespawnBlock(),
            		// target.asPassenger(),
            		// Set.of(),
            		target.postTeleportTransition() // ,
            		// target.cause()
            );
            */
        }
    }
    
    @Inject(at = @At(
    		value = "RETURN"
    ), method = "Lnet/minecraft/server/network/ServerPlayerEntity;teleportTo(Lnet/minecraft/world/TeleportTarget;)Lnet/minecraft/server/network/ServerPlayerEntity;")
    public void cardboard$do_world_change(TeleportTarget target, CallbackInfoReturnable<ServerPlayerEntity> e) {
    	ServerPlayerEntity thiz = (ServerPlayerEntity) (Object) this;
    	
    	if (thiz.isRemoved()) {
    		return;
    	}
    	
    	ServerWorld serverWorld = target.world();
		RegistryKey<World> registryKey = cb$from.getRegistryKey();

		if (serverWorld.getRegistryKey() == registryKey) {
			return;
		}

		PlayerChangedWorldEvent changeEvent = new PlayerChangedWorldEvent((Player)this.getBukkitEntity(), cb$from.getWorld());
        CraftServer.INSTANCE.getPluginManager().callEvent(changeEvent);
    }

    /*
    @Inject(at = @At("HEAD"), method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDFF)V", cancellable = true)
    public void teleport1(ServerWorld worldserver, double x, double y, double z, float f, float f1, CallbackInfo ci) {
        PlayerTeleportEvent event = new PlayerTeleportEvent(this.getBukkitEntity(), this.getBukkitEntity().getLocation(), new Location(((IMixinWorld)worldserver).getCraftWorld(), x,y,z,f,f1), PlayerTeleportEvent.TeleportCause.UNKNOWN);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }
    */

    // World Standard
    public String locale_BF = "en_us";

    @Inject(at = @At("HEAD"), method = "setClientOptions")
    public void onUpdateOptions(SyncedClientOptions options, CallbackInfo ci) {
        if(getMainArm() != options.mainArm()) {
            PlayerChangedMainHandEvent event = new PlayerChangedMainHandEvent(getBukkitEntity(), ((ServerPlayerEntity) (Object) this).getMainArm() == Arm.LEFT ? MainHand.LEFT : MainHand.RIGHT);
            CraftServer.INSTANCE.getPluginManager().callEvent(event);
        }

        if(!this.language.equals(options.language())) {
            PlayerLocaleChangeEvent event = new PlayerLocaleChangeEvent(getBukkitEntity(), options.language());
            CraftServer.INSTANCE.getPluginManager().callEvent(event);
        }
    }

    @Shadow
    public void closeHandledScreen() {
    }

    @Override
    public int nextContainerCounter() {
        this.screenHandlerSyncId = this.screenHandlerSyncId % 100 + 1;
        return screenHandlerSyncId; // CraftBukkit
    }

    /**/
    @Unique
    private final ThreadLocal<ScreenHandler> fabric_openedScreenHandler = new ThreadLocal<>();

    private void fabric_replaceVanillaScreenPacket_include(ServerPlayNetworkHandler networkHandler, Packet<?> packet, NamedScreenHandlerFactory factory) {
        if (factory instanceof ExtendedScreenHandlerFactory) {
            ScreenHandler handler = fabric_openedScreenHandler.get();

            if (handler.getType() instanceof ExtendedScreenHandlerType) { // TODO: 1.20.5: check ExtendedScreenHandlerType<?>
                Networking.sendOpenPacket((ServerPlayerEntity) (Object) this, (ExtendedScreenHandlerFactory) factory, handler, screenHandlerSyncId);
            } else {
                Identifier id = Registries.SCREEN_HANDLER.getId(handler.getType());
                throw new IllegalArgumentException("[Fabric] Non-extended screen handler " + id + " must not be opened with an ExtendedScreenHandlerFactory!");
            }
        } else {
            // Use vanilla logic for non-extended screen handlers
            networkHandler.sendPacket(packet);
        }
    }

    @Inject(method = "openHandledScreen(Lnet/minecraft/screen/NamedScreenHandlerFactory;)Ljava/util/OptionalInt;", at = @At("RETURN"))
    private void fabric_clearStoredScreenHandler_include(NamedScreenHandlerFactory factory, CallbackInfoReturnable<OptionalInt> info) {
        fabric_openedScreenHandler.remove();
    }

    /**
     * @reason Inventory Open Event
     * @author Cardboard
     */
    @Inject(at = @At("HEAD"), method = "openHandledScreen", cancellable = true)
    public void openHandledScreen_c(NamedScreenHandlerFactory factory, CallbackInfoReturnable<OptionalInt> ci) {
        if (factory == null) {
            ci.setReturnValue(OptionalInt.empty());
        } else {
            this.nextContainerCounter();
            ScreenHandler container = factory.createMenu(this.screenHandlerSyncId, ((ServerPlayerEntity)(Object)this).inventory, ((ServerPlayerEntity)(Object)this));

            if (container != null) {
                ((IMixinScreenHandler)container).setTitle(factory.getDisplayName());

                boolean cancelled = false;
                container = CraftEventFactory.callInventoryOpenEvent((ServerPlayerEntity)(Object)this, container, cancelled);
                if (container == null && !cancelled) {
                    if (factory instanceof Inventory) {
                        ((Inventory) factory).onClose((ServerPlayerEntity)(Object)this);
                    } else if (factory instanceof DoubleInventory)
                        ((DoubleInventory) factory).first.onClose((ServerPlayerEntity)(Object)this);

                    ci.setReturnValue(OptionalInt.empty());
                }
            }
            if (container == null) {
                ci.setReturnValue(OptionalInt.empty());
            } else {
                ((ServerPlayerEntity)(Object)this).currentScreenHandler = container;
                
                /*From FabricAPI*/
                if (factory instanceof ExtendedScreenHandlerFactory) {
                    fabric_openedScreenHandler.set(container);
                } else if (container.getType() instanceof ExtendedScreenHandlerType) { // TODO: 1.20.5: check ExtendedScreenHandlerType<?>
                    Identifier id = Registries.SCREEN_HANDLER.getId(container.getType());
                    throw new IllegalArgumentException("[Fabric] Extended screen handler " + id + " must be opened with an ExtendedScreenHandlerFactory!");
                }
                
                fabric_replaceVanillaScreenPacket_include(((ServerPlayerEntity)(Object)this).networkHandler,
                        new OpenScreenS2CPacket(container.syncId, container.getType(), factory.getDisplayName()),
                        factory);
                /*End*/

                //if ( CraftServer.INSTANCE.getMinecraftVersion().contains("1.16") ) {
                    // 1.16.5
                //    container.addListener((ScreenHandlerListener) this);
                //} else {
                    // 1.17
                    ((ServerPlayerEntity)(Object)this).onScreenHandlerOpened(container);
                //}

                fabric_openedScreenHandler.remove();
                ci.setReturnValue(OptionalInt.of(this.screenHandlerSyncId));
            }
        }
        ci.cancel();
    }

    // TODO: 1.19
    /*@Inject(at = @At("HEAD"), method = "onDeath", cancellable = true)
    public void bukkitizeDeath(DamageSource damagesource, CallbackInfo ci) {
        boolean flag = this.world.getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES);
        if (((ServerPlayerEntity)(Object)this).isRemoved()) {
            ci.cancel();
            return;
        }

        java.util.List<org.bukkit.inventory.ItemStack> loot = new java.util.ArrayList<org.bukkit.inventory.ItemStack>(((ServerPlayerEntity)(Object)this).inventory.size());
        boolean keepInventory = this.world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || ((ServerPlayerEntity)(Object)this).isSpectator();

        if (!keepInventory)
            for (DefaultedList<ItemStack> items : ((ServerPlayerEntity)(Object)this).inventory.combinedInventory)
                for (ItemStack item : items)
                    if (!item.isEmpty() && !EnchantmentHelper.hasVanishingCurse(item))
                        loot.add(CraftItemStack.asCraftMirror(item));

        // SPIGOT-5071: manually add player loot tables (SPIGOT-5195 - ignores keepInventory rule)
        this.dropLoot(damagesource, ((ServerPlayerEntity)(Object)this).playerHitTimer > 0);
        for (org.bukkit.inventory.ItemStack item : ((IMixinEntity)this).cardboard_getDrops()) loot.add(item);
        ((IMixinEntity)this).cardboard_getDrops().clear(); // SPIGOT-5188: make sure to clear

        Text defaultMessage = ((ServerPlayerEntity)(Object)this).getDamageTracker().getDeathMessage();

        String deathmessage = defaultMessage.getString();
        org.bukkit.event.entity.PlayerDeathEvent event = CraftEventFactory.callPlayerDeathEvent(((ServerPlayerEntity)(Object)this), loot, deathmessage, keepInventory);

        // SPIGOT-943 - only call if they have an inventory open
        if (((ServerPlayerEntity)(Object)this).currentScreenHandler != ((ServerPlayerEntity)(Object)this).playerScreenHandler) this.closeHandledScreen();

        String deathMessage = event.getDeathMessage();
        ServerPlayerEntity plr = ((ServerPlayerEntity)(Object)this);

        if ((deathMessage = event.getDeathMessage()) != null && deathMessage.length() > 0 && flag) {
            Text ichatbasecomponent = deathMessage.equals(deathmessage) ? plr.getDamageTracker().getDeathMessage() : CraftChatMessage.fromStringOrNull(deathMessage);
            plr.networkHandler.sendPacket(new DeathMessageS2CPacket(plr.getDamageTracker(), ichatbasecomponent), future -> {
                if (!future.isSuccess()) {
                    boolean flag1 = true;
                    String s = ichatbasecomponent.asTruncatedString(256);
                    TranslatableText chatmessage = new TranslatableText("death.attack.message_too_long", new LiteralText(s).formatted(Formatting.YELLOW));
                    MutableText ichatmutablecomponent = new TranslatableText("death.attack.even_more_magic", plr.getDisplayName()).styled(chatmodifier -> chatmodifier.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, chatmessage)));
                    plr.networkHandler.sendPacket(new DeathMessageS2CPacket(plr.getDamageTracker(), ichatmutablecomponent));
                }
            });
            AbstractTeam scoreboardteambase = plr.getScoreboardTeam();
            if (scoreboardteambase != null && scoreboardteambase.getDeathMessageVisibilityRule() != AbstractTeam.VisibilityRule.ALWAYS) {
                if (scoreboardteambase.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.HIDE_FOR_OTHER_TEAMS) {
                    plr.server.getPlayerManager().sendToTeam(plr, ichatbasecomponent);
                } else if (scoreboardteambase.getDeathMessageVisibilityRule() == AbstractTeam.VisibilityRule.HIDE_FOR_OWN_TEAM) {
                    plr.server.getPlayerManager().sendToOtherTeams(plr, ichatbasecomponent);
                }
            } else {
                plr.server.getPlayerManager().broadcast(ichatbasecomponent, MessageType.SYSTEM, Util.NIL_UUID);
            }
        } else {
            plr.networkHandler.sendPacket(new DeathMessageS2CPacket(plr.getDamageTracker(), LiteralText.EMPTY));
        }
        ((ServerPlayerEntity)(Object)this).dropShoulderEntities();
        if (this.world.getGameRules().getBoolean(GameRules.FORGIVE_DEAD_PLAYERS)) this.forgiveMobAnger();

        // SPIGOT-5478 must be called manually now
        ((ServerPlayerEntity)(Object)this).dropXp();
        // we clean the player's inventory after the EntityDeathEvent is called so plugins can get the exact state of the inventory.
        if (!event.getKeepInventory())  ((ServerPlayerEntity)(Object)this).inventory.clear();

        ((ServerPlayerEntity)(Object)this).setCameraEntity(((ServerPlayerEntity)(Object)this)); // Remove spectated target
        // CraftBukkit end

        // CraftBukkit - Get our scores instead
       // this.world.getServer().getScoreboard().get.getScoreboardScores(ScoreboardCriterion.DEATH_COUNT, ((ServerPlayerEntity)(Object)this).getEntityName(), ScoreboardPlayerScore::incrementScore);
        LivingEntity entityliving = ((ServerPlayerEntity)(Object)this).getPrimeAdversary();

        if (entityliving != null) {
            entityliving.updateKilledAdvancementCriterion(((ServerPlayerEntity)(Object)this), ((ServerPlayerEntity)(Object)this).scoreAmount, damagesource);
            ((ServerPlayerEntity)(Object)this).onKilledBy(entityliving);
        }

        this.world.sendEntityStatus(((ServerPlayerEntity)(Object)this), (byte) 3);

        ((ServerPlayerEntity)(Object)this).extinguish();
        ((ServerPlayerEntity)(Object)this).setFlag(0, false);
        ((ServerPlayerEntity)(Object)this).getDamageTracker().update();
        ci.cancel();
        return;
    }*/

    @Shadow
    public void forgiveMobAnger() {}

    @Shadow public abstract OptionalInt openHandledScreen(@Nullable NamedScreenHandlerFactory factory);
    @Shadow private String language;
    @Override
    public void setConnectionBF(ClientConnection connection) {
        this.connectionBF = connection;
    }

    @Override
    public ClientConnection getConnectionBF() {
        return this.connectionBF;
    }

    private int oldLevel = -1;
    private float h = 0;

    @Inject(at = @At("TAIL"), method = "playerTick")
    public void doBukkitEvent_PlayerLevelChangeEvent(CallbackInfo ci) {
        //ServerPlayerEntity plr = ((ServerPlayerEntity)(Object)this);

        // Avoid suffocation on join
        /*BlockPos saved = bukkit.posAtLogin;
        if (null != saved && plr.age > 8) {
            if (plr.age < 60) {
                if (h == 0) h = plr.getHealth();
                 plr.setInvulnerable(true);
                BlockPos pos = plr.getBlockPos();
                if (Math.abs(saved.x-pos.x) <= 1 && Math.abs(saved.z-pos.z) <= 1) {
                    if (!plr.getServerWorld().getBlockState(new BlockPos(pos.x, pos.y+1, pos.z)).isAir()) {
                        int ty = saved.getY();
                        while (!plr.getServerWorld().getBlockState(new BlockPos(pos.x, ty, pos.z)).isAir()) { ty++; }
                        plr.teleport(saved.x, ty, saved.z);
                    }
                }
                plr.setHealth(h);
            } else if (plr.age < 80) {
                plr.setInvulnerable(bukkit.in);
            }
        }*/
        // end

        try {
            if (this.oldLevel == -1) this.oldLevel = ((ServerPlayerEntity)(Object)this).experienceLevel;
            if (this.oldLevel != ((ServerPlayerEntity)(Object)this).experienceLevel) {
                CraftEventFactory.callPlayerLevelChangeEvent(getBukkitEntity(), this.oldLevel, ((ServerPlayerEntity)(Object)this).experienceLevel);
                this.oldLevel = ((ServerPlayerEntity)(Object)this).experienceLevel;
            }
        } catch (Throwable throwable) {}
    }

    //@Overwrite
    @Override
    public void copyFrom_unused(ServerPlayerEntity entityplayer, boolean flag) {
        if (flag) {
            ((ServerPlayerEntity)(Object)this).inventory.clone(entityplayer.inventory);
            ((ServerPlayerEntity)(Object)this).setHealth(entityplayer.getHealth());
            ((ServerPlayerEntity)(Object)this).hungerManager = entityplayer.hungerManager;
            ((ServerPlayerEntity)(Object)this).experienceLevel = entityplayer.experienceLevel;
            ((ServerPlayerEntity)(Object)this).totalExperience = entityplayer.totalExperience;
            ((ServerPlayerEntity)(Object)this).experienceProgress = entityplayer.experienceProgress;
            ((ServerPlayerEntity)(Object)this).setScore(entityplayer.getScore());
            // TODO
            //((ServerPlayerEntity)(Object)this).lastNetherPortalPosition = entityplayer.lastNetherPortalPosition;
        } else if (((ServerPlayerEntity)(Object)this).getWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY) || entityplayer.isSpectator()) {
            ((ServerPlayerEntity)(Object)this).inventory.clone(entityplayer.inventory);
            ((ServerPlayerEntity)(Object)this).experienceLevel = entityplayer.experienceLevel;
            ((ServerPlayerEntity)(Object)this).totalExperience = entityplayer.totalExperience;
            ((ServerPlayerEntity)(Object)this).experienceProgress = entityplayer.experienceProgress;
            ((ServerPlayerEntity)(Object)this).setScore(entityplayer.getScore());
        }
        ((ServerPlayerEntity)(Object)this).enderChestInventory = entityplayer.enderChestInventory;
        ((ServerPlayerEntity)(Object)this).getDataTracker().set(ServerPlayerEntity.PLAYER_MODEL_PARTS, entityplayer.getDataTracker().get(ServerPlayerEntity.PLAYER_MODEL_PARTS));
        ((ServerPlayerEntity)(Object)this).syncedExperience = -1;
        ((ServerPlayerEntity)(Object)this).syncedHealth = -1.0F;
        ((ServerPlayerEntity)(Object)this).syncedFoodLevel = -1;
        //((ServerPlayerEntity)(Object)this).removedEntities.addAll(entityplayer.removedEntities);
        ((ServerPlayerEntity)(Object)this).seenCredits = entityplayer.seenCredits;
        ((ServerPlayerEntity)(Object)this).enteredNetherPos = entityplayer.enteredNetherPos;
        //((ServerPlayerEntity)(Object)this).setShoulderEntityLeft(entityplayer.getShoulderEntityLeft());
        //((ServerPlayerEntity)(Object)this).setShoulderEntityRight(entityplayer.getShoulderEntityRight());

    }
    
    @Inject(at = @At("HEAD"), method = "closeHandledScreen")
    public void cardboard_doInventoryCloseEvent(CallbackInfo ci) {
        IMixinScreenHandler handler = (IMixinScreenHandler) ((ServerPlayerEntity)(Object)this).currentScreenHandler;
        CardboardInventoryView view = handler.getBukkitView();
        view.setPlayerIfNotSet(getBukkit());
        InventoryCloseEvent event = new InventoryCloseEvent(view);
        Bukkit.getPluginManager().callEvent(event);
        handler.transferTo(((ServerPlayerEntity)(Object)this).playerScreenHandler, getBukkitEntity());
    }
    
    public void spawnIn(World world) {
        /*this.setWorld(world);
        if (world == null) {
            this.unsetRemoved();
            Vec3d position = null;
            if (this.spawnPointDimension != null && (world = this.server.getWorld(this.spawnPointDimension)) != null && this.getSpawnPointPosition() != null) {
                position = PlayerEntity.findRespawnPosition((ServerWorld)world, this.getSpawnPointPosition(), this.getSpawnAngle(), false, false).orElse(null);
            }
            if (world == null || position == null) {
                world = ((CraftWorld)Bukkit.getServer().getWorlds().get(0)).getHandle();
                position = Vec3d.ofCenter(world.getSpawnPos());
            }
            this.setWorld(world);
            this.setPos(position.getX(), position.getY(), position.getZ());
        }
        this.interactionManager.setWorld((ServerWorld)world);*/
    }

	@Override
	public void spawnIn(ServerWorld world) {
		ServerPlayerEntity plr = ((ServerPlayerEntity)(Object)this);
		
		plr.setServerWorld(world);
        if (world == null) {
        	plr.unsetRemoved();
            Vec3d position = null;
            
            RegistryKey<World> rw = plr.getRespawn().dimension();

            if (rw != null && (world = plr.getServer().getWorld(rw)) != null && plr.getRespawn().pos() != null) {
                position = ServerPlayerEntity.findRespawnPosition((ServerWorld)world, plr.getRespawn(), false)
                		.map(RespawnPos::pos).orElse(null);
            }
            if (world == null || position == null) {
                world = ((CraftWorld)Bukkit.getServer().getWorlds().get(0)).getHandle();
                position = Vec3d.ofCenter(world.getSpawnPos());
            }
            plr.setServerWorld(world);
            plr.setPos(position.getX(), position.getY(), position.getZ());
        }
        plr.interactionManager.setWorld((ServerWorld)world);
	}
	
	// SPIGOT-1903, MC-98153
	@Override
	public void spigot$forceSetPositionRotation(double x, double y, double z, float yaw, float pitch) {
		((ServerPlayerEntity)(Object)this).refreshPositionAndAngles(x, y, z, yaw, pitch);
		((ServerPlayerEntity)(Object)this).networkHandler.syncWithPlayerPosition();
    }
	
	@Nullable
	@Override
    public TeleportTarget findRespawnPositionAndUseSpawnBlock(boolean useCharge, TeleportTarget.PostDimensionTransition postTeleportTransition, @Nullable PlayerRespawnEvent.RespawnReason respawnReason) {
        ServerPlayerEntity thiz = (ServerPlayerEntity) (Object) this;
		TeleportTarget teleportTransition;
        boolean isBedSpawn = false;
        boolean isAnchorSpawn = false;
        Runnable consumeAnchorCharge = null;
        Respawn respawnConfig = thiz.getRespawn();
        ServerWorld level = thiz.getServer().getWorld(Respawn.getDimension(respawnConfig));
        if (level != null && respawnConfig != null) {
            Optional<RespawnPos> optional = ServerPlayerEntity.findRespawnPosition(level, respawnConfig, useCharge);
            if (optional.isPresent()) {
                RespawnPos respawnPosAngle = optional.get();
                // isBedSpawn = respawnPosAngle.isBedSpawn();
                // isAnchorSpawn = respawnPosAngle.isAnchorSpawn();
                // consumeAnchorCharge = respawnPosAngle.consumeAnchorCharge();
                teleportTransition = new TeleportTarget(level, respawnPosAngle.pos(), Vec3d.ZERO, respawnPosAngle.yaw(), 0.0f, postTeleportTransition);
            } else {
                teleportTransition = TeleportTarget.missingSpawnBlock(thiz.getServer().getOverworld(), thiz, postTeleportTransition);
            }
        } else {
            teleportTransition = new TeleportTarget(CraftServer.server.getOverworld(), thiz, postTeleportTransition);
        }
        if (respawnReason == null) {
            return teleportTransition;
        }
        CraftPlayer respawnPlayer = this.getBukkitEntity();
        Location location = CraftLocation.toBukkit(teleportTransition.position(), (org.bukkit.World)teleportTransition.world().getWorld(), teleportTransition.yaw(), teleportTransition.pitch());
        PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent((Player)respawnPlayer, location, isBedSpawn, isAnchorSpawn, teleportTransition.missingRespawnBlock(), respawnReason);
        thiz.getWorld().getCraftServer().getPluginManager().callEvent(respawnEvent);
        
        /*
        if (this.networkHandler.isDisconnected()) {
            return null;
        }
        */
        
        if (location.equals(respawnEvent.getRespawnLocation()) && consumeAnchorCharge != null) {
            consumeAnchorCharge.run();
        }
        location = respawnEvent.getRespawnLocation();

        TeleportCause cause = TeleportCause.UNKNOWN;
        
        return new TeleportTarget(((CraftWorld)location.getWorld()).getHandle(), CraftLocation.toVec3(location), teleportTransition.velocity(), location.getYaw(), location.getPitch(), teleportTransition.relatives(), teleportTransition.postTeleportTransition());
    }

}
