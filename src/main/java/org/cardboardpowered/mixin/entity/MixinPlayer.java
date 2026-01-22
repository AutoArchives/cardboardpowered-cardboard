/**
 * Cardboard - Spigot/Paper API for Fabric
 * Copyright (C) 2020-2025 Cardboard contributors
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
import java.util.OptionalInt;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedMainHandEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.MainHand;
import org.cardboardpowered.CardboardConfig;
import org.cardboardpowered.CardboardMod;
import org.cardboardpowered.TeleportTargetExtra;
import org.cardboardpowered.impl.entity.CraftPlayer;
import org.cardboardpowered.impl.inventory.CardboardInventoryView;
import org.cardboardpowered.impl.world.CraftWorld;
import org.cardboardpowered.interfaces.IMixinCommandOutput;
import org.cardboardpowered.interfaces.IMixinEntity;
import org.cardboardpowered.interfaces.IMixinScreenHandler;
import org.cardboardpowered.interfaces.IMixinServerEntityPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.impl.screenhandler.Networking;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayer.RespawnConfig;
import net.minecraft.server.level.ServerPlayer.RespawnPosAngle;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

@Mixin(value = ServerPlayer.class, priority = 999)
public abstract class MixinPlayer extends MixinLivingEntity implements IMixinCommandOutput, IMixinServerEntityPlayer  {

	@Shadow
	private CommandSource commandSource;

	public CommandSource cb$get_command_output() {
		return commandSource;
	}

	public void cb$set_command_output(CommandSource out) {
		this.commandSource = out;
	}

	public void cb$set_bukkit_command_output(CommandSource out) {
		// this.commandOutput = out;
	
		this.commandSource = new CommandSource() {

			@Override
			public void sendSystemMessage(Component message) {
				out.sendSystemMessage(message);
			}

			@Override
			public boolean shouldInformAdmins() {
				return out.shouldInformAdmins();
			}

			@Override
			public boolean acceptsSuccess() {
				return out.acceptsSuccess();
			}

			@Override
			public boolean acceptsFailure() {
				// TODO Auto-generated method stub
				return false;
			}
			
			// @Override
            public CommandSender getBukkitSender(CommandSourceStack wrapper) {
                return ( (IMixinEntity)  ((ServerPlayer) (Object) this) ) .getBukkitEntity();
            }
			
		};
		
	}

    private CraftPlayer bukkit;
    public Connection connectionBF;

    @Shadow
    public int containerCounter;

    @Override
    public void setBukkit(CraftPlayer plr) {
        this.bukkit = plr;
    }

    @Override
    public CraftPlayer getBukkit() {
        return bukkit;
    }

    @Override
    public CommandSender getBukkitSender(CommandSourceStack wrapper) {
        return bukkit;
    }

    @Override
    public CraftPlayer getBukkitEntity() {
    	if (bukkit == null) {
    		bukkit = (CraftPlayer) CraftEntity.getEntity(CraftServer.INSTANCE, ((ServerPlayer) (Object) this));
    	}
        return bukkit;
    }

    @Override
    public void reset() {
        ServerPlayer thiz = (ServerPlayer) (Object) this;
    	
    	float exp = 0.0F;
        
    	/*
    	if (thiz.keepLevel) {
           exp = super.experienceProgress;
           thiz.newTotalExp = super.totalExperience;
           thiz.newLevel = super.experienceLevel;
        }
        */

        thiz.setHealth(thiz.getMaxHealth());
        thiz.stopUsingItem();
        thiz.setAirSupply(thiz.getMaxAirSupply());
        thiz.setRemainingFireTicks(0);
        thiz.fallDistance = 0.0;
        thiz.foodData = new FoodData();
        // thiz.experienceLevel = thiz.newLevel;
        // thiz.totalExperience = thiz.newTotalExp;
        thiz.experienceProgress = 0.0F;
        thiz.deathTime = 0;
        // thiz.setArrowCount(0, true);
        thiz.removeAllEffects();
        // thiz.removeAllEffects(org.bukkit.event.entity.EntityPotionEffectEvent.Cause.DEATH);
        thiz.effectsDirty = true;
        thiz.containerMenu = thiz.inventoryMenu;
        thiz.lastHurtByPlayer = null;
        thiz.lastHurtByMob = null;
        thiz.combatTracker = new CombatTracker(thiz);
        thiz.lastSentExp = -1;
        
        /*
        if (thiz.keepLevel) {
        	thiz.experienceProgress = exp;
        } else {
           thiz.addExperience(thiz.newExp);
        }

        thiz.keepLevel = false;
        */
        thiz.setDeltaMovement(0.0, 0.0, 0.0);
        thiz.skipDropExperience = false;
    }

    @Override
    public BlockPos getSpawnPoint(Level world) {
        return ((ServerLevel)world).getRespawnData().pos();
    }

    @Inject(at = @At("TAIL"), method = "disconnect")
    public void onDisconnect(CallbackInfo ci) {
        // CraftServer.INSTANCE.playerView.remove(this.bukkit);
    }
    
    private ServerLevel cb$from;
    
    @Inject(cancellable = true, at = @At(
    		value = "INVOKE",
    		target = "Lnet/minecraft/world/level/portal/TeleportTransition;newLevel()Lnet/minecraft/server/level/ServerLevel;"
    ), method = "teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/server/level/ServerPlayer;")
    public void cardboard$do_teleport_event(TeleportTransition target, CallbackInfoReturnable<ServerPlayer> ci) {
    	if (CardboardConfig.DEBUG_PLAYER) {
    		CardboardMod.LOGGER.info("DEBUG: ServerPlayerEntity.cardboard$do_teleport_event called");
    	}
    	
    	ServerPlayer thiz = (ServerPlayer) (Object) this;
    	cb$from = thiz.level(); // Cardboard - store from world

    	Location exit = CraftLocation.toBukkit(target.position(), target.newLevel().getWorld());

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
        	// Set our new TeleportTarget
        	target.newLevel = ((CraftWorld)newExit.getWorld()).getHandle();
        	target.position = CraftLocation.toVec3D(newExit);
        	target.deltaMovement = Vec3.ZERO;
        	target.yRot = newExit.getYaw();
        	target.xRot = newExit.getPitch();
        	
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
    ), method = "teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/server/level/ServerPlayer;")
    public void cardboard$do_world_change(TeleportTransition target, CallbackInfoReturnable<ServerPlayer> e) {
    	ServerPlayer thiz = (ServerPlayer) (Object) this;
    	
    	if (thiz.isRemoved()) {
    		return;
    	}
    	
    	ServerLevel serverWorld = target.newLevel();
		ResourceKey<Level> registryKey = cb$from.dimension();

		if (serverWorld.dimension() == registryKey) {
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

    @Inject(at = @At("HEAD"), method = "updateOptions")
    public void onUpdateOptions(ClientInformation options, CallbackInfo ci) {
        if(getMainArm() != options.mainHand()) {
            PlayerChangedMainHandEvent event = new PlayerChangedMainHandEvent(getBukkitEntity(), ((ServerPlayer) (Object) this).getMainArm() == HumanoidArm.LEFT ? MainHand.LEFT : MainHand.RIGHT);
            CraftServer.INSTANCE.getPluginManager().callEvent(event);
        }

        if(!this.language.equals(options.language())) {
            PlayerLocaleChangeEvent event = new PlayerLocaleChangeEvent(getBukkitEntity(), options.language());
            CraftServer.INSTANCE.getPluginManager().callEvent(event);
        }
    }

    @Shadow
    public void closeContainer() {
    }

    @Override
    public int nextContainerCounter() {
        this.containerCounter = this.containerCounter % 100 + 1;
        return containerCounter; // CraftBukkit
    }

    /**/
    @Unique
    private final ThreadLocal<AbstractContainerMenu> fabric_openedScreenHandler = new ThreadLocal<>();

    private void fabric_replaceVanillaScreenPacket_include(ServerGamePacketListenerImpl networkHandler, Packet<?> packet, MenuProvider factory) {
        if (factory instanceof ExtendedScreenHandlerFactory) {
            AbstractContainerMenu handler = fabric_openedScreenHandler.get();

            if (handler.getType() instanceof ExtendedScreenHandlerType) { // TODO: 1.20.5: check ExtendedScreenHandlerType<?>
                Networking.sendOpenPacket((ServerPlayer) (Object) this, (ExtendedScreenHandlerFactory) factory, handler, containerCounter);
            } else {
                Identifier id = BuiltInRegistries.MENU.getKey(handler.getType());
                throw new IllegalArgumentException("[Fabric] Non-extended screen handler " + id + " must not be opened with an ExtendedScreenHandlerFactory!");
            }
        } else {
            // Use vanilla logic for non-extended screen handlers
            networkHandler.send(packet);
        }
    }

    @Inject(method = "openMenu(Lnet/minecraft/world/MenuProvider;)Ljava/util/OptionalInt;", at = @At("RETURN"))
    private void fabric_clearStoredScreenHandler_include(MenuProvider factory, CallbackInfoReturnable<OptionalInt> info) {
        fabric_openedScreenHandler.remove();
    }

    /**
     * @reason Inventory Open Event
     * @author Cardboard
     */
    @Inject(at = @At("HEAD"), method = "openMenu", cancellable = true)
    public void openHandledScreen_c(MenuProvider factory, CallbackInfoReturnable<OptionalInt> ci) {
        if (factory == null) {
            ci.setReturnValue(OptionalInt.empty());
        } else {
            this.nextContainerCounter();
            AbstractContainerMenu container = factory.createMenu(this.containerCounter, ((ServerPlayer)(Object)this).inventory, ((ServerPlayer)(Object)this));

            if (container != null) {
                ((IMixinScreenHandler)container).setTitle(factory.getDisplayName());

                boolean cancelled = false;
                container = CraftEventFactory.callInventoryOpenEvent((ServerPlayer)(Object)this, container, cancelled);
                if (container == null && !cancelled) {
                    if (factory instanceof Container) {
                        ((Container) factory).stopOpen((ServerPlayer)(Object)this);
                    } else if (factory instanceof CompoundContainer)
                        ((CompoundContainer) factory).container1.stopOpen((ServerPlayer)(Object)this);

                    ci.setReturnValue(OptionalInt.empty());
                }
            }
            if (container == null) {
                ci.setReturnValue(OptionalInt.empty());
            } else {
                ((ServerPlayer)(Object)this).containerMenu = container;
                
                /*From FabricAPI*/
                if (factory instanceof ExtendedScreenHandlerFactory) {
                    fabric_openedScreenHandler.set(container);
                } else if (container.getType() instanceof ExtendedScreenHandlerType) { // TODO: 1.20.5: check ExtendedScreenHandlerType<?>
                    Identifier id = BuiltInRegistries.MENU.getKey(container.getType());
                    throw new IllegalArgumentException("[Fabric] Extended screen handler " + id + " must be opened with an ExtendedScreenHandlerFactory!");
                }
                
                fabric_replaceVanillaScreenPacket_include(((ServerPlayer)(Object)this).connection,
                        new ClientboundOpenScreenPacket(container.containerId, container.getType(), factory.getDisplayName()),
                        factory);
                /*End*/

                ((ServerPlayer)(Object)this).initMenu(container);

                fabric_openedScreenHandler.remove();
                ci.setReturnValue(OptionalInt.of(this.containerCounter));
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
    public void tellNeutralMobsThatIDied() {}

    @Shadow public abstract OptionalInt openMenu(@Nullable MenuProvider factory);
    @Shadow private String language;
    @Override
    public void setConnectionBF(Connection connection) {
        this.connectionBF = connection;
    }

    @Override
    public Connection getConnectionBF() {
        return this.connectionBF;
    }

    private int oldLevel = -1;
    private float h = 0;

    @Inject(at = @At("TAIL"), method = "doTick")
    public void doBukkitEvent_PlayerLevelChangeEvent(CallbackInfo ci) {
        //ServerPlayerEntity plr = ((ServerPlayerEntity)(Object)this);

        try {
            if (this.oldLevel == -1) this.oldLevel = ((ServerPlayer)(Object)this).experienceLevel;
            if (this.oldLevel != ((ServerPlayer)(Object)this).experienceLevel) {
                CraftEventFactory.callPlayerLevelChangeEvent(getBukkitEntity(), this.oldLevel, ((ServerPlayer)(Object)this).experienceLevel);
                this.oldLevel = ((ServerPlayer)(Object)this).experienceLevel;
            }
        } catch (Throwable throwable) {}
    }

    //@Overwrite
    @Override
    public void copyFrom_unused(ServerPlayer entityplayer, boolean flag) {
        if (flag) {
            ((ServerPlayer)(Object)this).inventory.replaceWith(entityplayer.inventory);
            ((ServerPlayer)(Object)this).setHealth(entityplayer.getHealth());
            ((ServerPlayer)(Object)this).foodData = entityplayer.foodData;
            ((ServerPlayer)(Object)this).experienceLevel = entityplayer.experienceLevel;
            ((ServerPlayer)(Object)this).totalExperience = entityplayer.totalExperience;
            ((ServerPlayer)(Object)this).experienceProgress = entityplayer.experienceProgress;
            ((ServerPlayer)(Object)this).setScore(entityplayer.getScore());
        } else if (((ServerPlayer)(Object)this).level().getGameRules().get(GameRules.KEEP_INVENTORY) || entityplayer.isSpectator()) {
            ((ServerPlayer)(Object)this).inventory.replaceWith(entityplayer.inventory);
            ((ServerPlayer)(Object)this).experienceLevel = entityplayer.experienceLevel;
            ((ServerPlayer)(Object)this).totalExperience = entityplayer.totalExperience;
            ((ServerPlayer)(Object)this).experienceProgress = entityplayer.experienceProgress;
            ((ServerPlayer)(Object)this).setScore(entityplayer.getScore());
        }
        ((ServerPlayer)(Object)this).enderChestInventory = entityplayer.enderChestInventory;
        ((ServerPlayer)(Object)this).lastSentExp = -1;
        ((ServerPlayer)(Object)this).lastSentHealth = -1.0F;
        ((ServerPlayer)(Object)this).lastSentFood = -1;
        ((ServerPlayer)(Object)this).seenCredits = entityplayer.seenCredits;
        ((ServerPlayer)(Object)this).enteredNetherPosition = entityplayer.enteredNetherPosition;
    }
    
    @Inject(at = @At("HEAD"), method = "closeContainer")
    public void cardboard_doInventoryCloseEvent(CallbackInfo ci) {
        IMixinScreenHandler handler = (IMixinScreenHandler) ((ServerPlayer)(Object)this).containerMenu;
        CardboardInventoryView view = handler.getBukkitView();
        view.setPlayerIfNotSet(getBukkit());
        InventoryCloseEvent event = new InventoryCloseEvent(view);
        Bukkit.getPluginManager().callEvent(event);
        handler.transferTo(((ServerPlayer)(Object)this).inventoryMenu, getBukkitEntity());
    }

    @Override
    public void spawnIn(ServerLevel level) {
    	if (level == null) {
    		throw new IllegalArgumentException("level can't be null");
    	} else {
    		ServerPlayer plr = ((ServerPlayer)(Object)this);
    		plr.setServerLevel(level);
    		plr.gameMode.setLevel(level);
    	}
    }

	// SPIGOT-1903, MC-98153
	@Override
	public void spigot$forceSetPositionRotation(double x, double y, double z, float yaw, float pitch) {
		((ServerPlayer)(Object)this).snapTo(x, y, z, yaw, pitch);
		((ServerPlayer)(Object)this).connection.resetPosition();
    }
	
	@Nullable
	@Override
    public TeleportTransition findRespawnPositionAndUseSpawnBlock(boolean useCharge, TeleportTransition.PostTeleportTransition postTeleportTransition, @Nullable PlayerRespawnEvent.RespawnReason respawnReason) {
		if (CardboardConfig.DEBUG_PLAYER) {
    		CardboardMod.LOGGER.info("findRespawnPosAndUseSpawnBlock");
    	}
		ServerPlayer thiz = (ServerPlayer) (Object) this;
		TeleportTransition teleportTransition;
        boolean isBedSpawn = false;
        boolean isAnchorSpawn = false;
        Runnable consumeAnchorCharge = null;
        RespawnConfig respawnConfig = thiz.getRespawnConfig();
        ServerLevel level = CraftServer.server.getLevel(RespawnConfig.getDimensionOrDefault(respawnConfig));
        if (level != null && respawnConfig != null) {
            Optional<RespawnPosAngle> optional = ServerPlayer.findRespawnAndUseSpawnBlock(level, respawnConfig, useCharge);
            if (optional.isPresent()) {
                RespawnPosAngle respawnPosAngle = optional.get();
                // isBedSpawn = respawnPosAngle.isBedSpawn();
                // isAnchorSpawn = respawnPosAngle.isAnchorSpawn();
                // consumeAnchorCharge = respawnPosAngle.consumeAnchorCharge();
                teleportTransition = new TeleportTransition(level, respawnPosAngle.position(), Vec3.ZERO, respawnPosAngle.yaw(), 0.0f, postTeleportTransition);
            } else {
                teleportTransition = TeleportTransition.missingRespawnBlock(/*thiz.getEntityWorld().getServer().getOverworld(),*/ thiz, postTeleportTransition);
            }
        } else {
            teleportTransition = TeleportTargetExtra.newTeleportTarget(CraftServer.server.overworld(), thiz, postTeleportTransition);
        }
        if (respawnReason == null) {
            return teleportTransition;
        }
        CraftPlayer respawnPlayer = this.getBukkitEntity();
        Location location = CraftLocation.toBukkit(teleportTransition.position(), (org.bukkit.World)teleportTransition.newLevel().getWorld(), teleportTransition.yRot(), teleportTransition.xRot());
        PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent((Player)respawnPlayer, location, isBedSpawn, isAnchorSpawn, teleportTransition.missingRespawnBlock(), respawnReason);
        thiz.level().getCraftServer().getPluginManager().callEvent(respawnEvent);
        
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
        
        if (CardboardConfig.DEBUG_PLAYER) {
    		CardboardMod.LOGGER.info("loc = " + location);
    	}
        
        return new TeleportTransition(((CraftWorld)location.getWorld()).getHandle(), CraftLocation.toVec3(location), teleportTransition.deltaMovement(), location.getYaw(), location.getPitch(), teleportTransition.relatives(), teleportTransition.postTeleportTransition());
    }

}
