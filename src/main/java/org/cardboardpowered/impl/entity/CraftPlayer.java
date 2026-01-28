/**
 * CardboardPowered - Bukkit/Spigot for Fabric
 * Copyright (C) CardboardPowered.org and contributors
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either 
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.cardboardpowered.impl.entity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.stream.Collectors;

import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.*;
import net.minecraft.sounds.SoundEvent;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.BanEntry;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Input;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Note;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.ServerLinks;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.Statistic;
import org.bukkit.WeatherType;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.TileState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.sign.Side;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.craftbukkit.CraftInput;
import org.bukkit.craftbukkit.CraftOfflinePlayer;
import org.bukkit.craftbukkit.CraftParticle;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftServerLinks;
import org.bukkit.craftbukkit.CraftSound;
import org.bukkit.craftbukkit.CraftStatistic;
import org.bukkit.craftbukkit.advancement.CraftAdvancement;
import org.bukkit.craftbukkit.advancement.CraftAdvancementProgress;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.scoreboard.CardboardScoreboard;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerKickEvent.Cause;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerUnregisterChannelEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.cardboardpowered.impl.block.CardboardSign;

import com.destroystokyo.paper.ClientOption;
import com.destroystokyo.paper.Title;
import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
// import com.github.bsideup.jabel.Desugar;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.BaseEncoding;
import org.cardboardpowered.CardboardMod;

import org.cardboardpowered.impl.world.CraftWorld;
import org.cardboardpowered.bridge.network.protocol.game.ClientboundSectionBlocksUpdatePacketBridge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.NonNull;
import org.spigotmc.AsyncCatcher;

import org.cardboardpowered.interfaces.IMixinClientConnection;
import org.cardboardpowered.bridge.world.entity.EntityBridge;
import org.cardboardpowered.interfaces.IMixinMinecraftServer;
import org.cardboardpowered.interfaces.IMixinPlayNetworkHandler;
import org.cardboardpowered.interfaces.IMixinSignBlockEntity;
import com.mojang.authlib.GameProfile;

import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.connection.PlayerGameConnection;
import io.papermc.paper.entity.LookAnchor;
import io.papermc.paper.entity.PaperPlayerGiveResult;
import io.papermc.paper.entity.PlayerGiveResult;
import io.papermc.paper.math.Position;
import io.papermc.paper.util.MCUtil;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.shorts.ShortArraySet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import me.isaiah.common.GameVersion;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.util.TriState;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.common.ClientboundResourcePackPopPacket;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.network.protocol.common.ClientboundServerLinksPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.UserWhiteListEntry;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.phys.Vec3;

@DelegateDeserialization(CraftOfflinePlayer.class)
public class CraftPlayer extends CraftHumanEntity implements Player {

    private final Set<String> channels = new HashSet<String>();

    public CraftPlayer(CraftServer server, ServerPlayer entity) {
        super(server, entity);

        //this.firstPlayed = System.currentTimeMillis();
    }

    @Override
    public ServerPlayer getHandle() {
        return (ServerPlayer) this.entity;
    }

    @Override
    public UUID getUniqueId() {
        return super.getUniqueId();
    }

    @Override
    public void abandonConversation(Conversation arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void abandonConversation(Conversation arg0, ConversationAbandonedEvent arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void acceptConversationInput(String arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean beginConversation(Conversation arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isConversing() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void incrementStatistic(Statistic statistic) {
        CraftStatistic.incrementStatistic(getHandle().getStats(), statistic);
    }

    @Override
    public void decrementStatistic(Statistic statistic) {
        CraftStatistic.decrementStatistic(getHandle().getStats(), statistic);
    }

    @Override
    public int getStatistic(Statistic statistic) {
        return CraftStatistic.getStatistic(getHandle().getStats(), statistic);
    }

    @Override
    public void incrementStatistic(Statistic statistic, int amount) {
        CraftStatistic.incrementStatistic(getHandle().getStats(), statistic, amount);
    }

    @Override
    public void decrementStatistic(Statistic statistic, int amount) {
        CraftStatistic.decrementStatistic(getHandle().getStats(), statistic, amount);
    }

    @Override
    public void setStatistic(Statistic statistic, int newValue) {
        CraftStatistic.setStatistic(getHandle().getStats(), statistic, newValue);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material) {
        CraftStatistic.incrementStatistic(getHandle().getStats(), statistic, material);
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material) {
        CraftStatistic.decrementStatistic(getHandle().getStats(), statistic, material);
    }

    @Override
    public int getStatistic(Statistic statistic, Material material) {
        return CraftStatistic.getStatistic(getHandle().getStats(), statistic, material);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material, int amount) {
        CraftStatistic.incrementStatistic(getHandle().getStats(), statistic, material, amount);
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material, int amount) {
        CraftStatistic.decrementStatistic(getHandle().getStats(), statistic, material, amount);
    }

    @Override
    public void setStatistic(Statistic statistic, Material material, int newValue) {
        CraftStatistic.setStatistic(getHandle().getStats(), statistic, material, newValue);
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType) {
        CraftStatistic.incrementStatistic(getHandle().getStats(), statistic, entityType);
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType) {
        CraftStatistic.decrementStatistic(getHandle().getStats(), statistic, entityType);
    }

    @Override
    public int getStatistic(Statistic statistic, EntityType entityType) {
        return CraftStatistic.getStatistic(getHandle().getStats(), statistic, entityType);
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType, int amount) {
        CraftStatistic.incrementStatistic(getHandle().getStats(), statistic, entityType, amount);
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType, int amount) {
        CraftStatistic.decrementStatistic(getHandle().getStats(), statistic, entityType, amount);
    }

    @Override
    public void setStatistic(Statistic statistic, EntityType entityType, int newValue) {
        CraftStatistic.setStatistic(getHandle().getStats(), statistic, entityType, newValue);
    }

    @Override
    public long getFirstPlayed() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getLastPlayed() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void sendMessage(String message) {
        // this.getHandle().sendSystemMessage(new LiteralText(message), UUID.randomUUID());
    	this.getHandle().sendSystemMessage(net.minecraft.network.chat.Component.literal(message));
    }

    @Override
    public CraftPlayer getPlayer() {
        return this;
    }

    @Override
    public boolean hasPlayedBefore() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isBanned() {
        return getServer().getBanList(org.bukkit.BanList.Type.NAME).isBanned(getName());
    }

    @Override
    public String getName() {
    	return this.getHandle().getScoreboardName();
    }

    @Override
    public boolean isOnline() {
        return getServer().getPlayer(getUniqueId()) != null;
    }

    @Override
	public boolean isWhitelisted() {
		return CraftServer.server.getPlayerList().getWhiteList().isWhiteListed(this.getHandle().nameAndId());
	}

    @Override
	public void setWhitelisted(boolean value) {
		if (value) {
			CraftServer.server.getPlayerList().getWhiteList().add(new UserWhiteListEntry(this.getHandle().nameAndId()));
        } else {
        	CraftServer.server.getPlayerList().getWhiteList().remove(this.getHandle().nameAndId());
        }
	}

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("name", getName());
        return result;
    }

    public void addChannel(String channel) {
        Preconditions.checkState(channels.size() < 128, "Cannot register channel '%s'. Too many channels registered!", channel);
        channel = StandardMessenger.validateAndCorrectChannel(channel);
        if (channels.add(channel))
            server.getPluginManager().callEvent(new PlayerRegisterChannelEvent(this, channel));
    }

    public void removeChannel(String channel) {
        channel = StandardMessenger.validateAndCorrectChannel(channel);
        if (channels.remove(channel))
            server.getPluginManager().callEvent(new PlayerUnregisterChannelEvent(this, channel));
    }

    @Override
    public Set<String> getListeningPluginChannels() {
        return ImmutableSet.copyOf(channels);
    }

	public void sendSupportedChannels() {
		if(getHandle().connection == null) return;
		Set<String> listening = server.getMessenger().getIncomingChannels();

		if(!listening.isEmpty()) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();

			for(String channel : listening) {
				try {
					stream.write(channel.getBytes("UTF8"));
					stream.write((byte) 0);
				} catch(IOException ex) {
					CardboardMod.LOGGER.log(Level.SEVERE, "Could not send Plugin Channel REGISTER to " + getName(), ex);
				}
			}

			sendPayload(Identifier.withDefaultNamespace("register"), stream.toByteArray());
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void sendPluginMessage(Plugin source, String channel, byte[] message) {
		StandardMessenger.validatePluginMessage(Bukkit.getMessenger(), source, channel, message);
		if(getHandle().connection == null) return;

		if(channels.contains(channel)) {
			channel = StandardMessenger.validateAndCorrectChannel(channel);

			sendPayload(Identifier.withDefaultNamespace(channel), message);
		}
	}

	private void sendPayload(Identifier id, byte[] message) {
		/*CustomPayloadS2CPacket packet = new CustomPayloadS2CPacket(new CustomPayload() {
			@Override
			public void write(PacketByteBuf packetByteBuf) {
				packetByteBuf.writeBytes(message);
			}
			@Override
			public Identifier id() {
				return id;
			}
		});
		getHandle().networkHandler.sendPacket(packet);*/
		// TODO: 1.20.5
	}


    @Override
    public boolean canSee(Player arg0) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void chat(String message) {
        ((IMixinPlayNetworkHandler)(Object)this.getHandle().connection).chat(message, false);
    }

    @Override
    public InetSocketAddress getAddress() {
        if (this.getHandle().connection == null) return null;

        SocketAddress addr = getHandle().connection.getRemoteAddress(); //.connection.getAddress();
        return addr instanceof InetSocketAddress ? (InetSocketAddress) addr : null;
    }

    @Override
    public org.bukkit.advancement.AdvancementProgress getAdvancementProgress(org.bukkit.advancement.Advancement advancement) {
        Preconditions.checkArgument(advancement != null, "advancement");

        CraftAdvancement craft = (CraftAdvancement) advancement;
        PlayerAdvancements data = getHandle().getAdvancements();
        net.minecraft.advancements.AdvancementProgress progress = data.getOrStartProgress(craft.getHandle());

        return new CraftAdvancementProgress(craft, data, progress);
    }

    @Override
    public boolean getAllowFlight() {
        return getHandle().getAbilities().mayfly;
    }

    @Override
    public int getClientViewDistance() {
        return Bukkit.getViewDistance(); // TODO Get Client view distance not server
    }

    @Override
    public Location getCompassTarget() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDisplayName() {
    	
    	
        return (null == this.getHandle().getCustomName()) ? this.getName() : this.getHandle().getCustomName().getString();
    }

    @Override
    public float getExhaustion() {
        return this.getHandle().getFoodData().exhaustionLevel;
    }

    @Override
    public float getExp() {
        return this.getHandle().experienceProgress;
    }

    @Override
    public float getFlySpeed() {
        return (float) getHandle().getAbilities().getFlyingSpeed() * 2f;
    }

    @Override
    public int getFoodLevel() {
        return this.getHandle().getFoodData().getFoodLevel();
    }

    @Override
    public double getHealthScale() {
        // TODO Auto-generated method stub
        return 20;
    }

    @Override
    public int getLevel() {
        return this.getHandle().experienceLevel;
    }

    @Override
    public String getLocale() {
        return "en_US"; // TODO
    }

    @Override
    public String getPlayerListFooter() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPlayerListHeader() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPlayerListName() {
        return getHandle().getTabListDisplayName() == null ? getName() : CraftChatMessage.fromComponent(getHandle().getTabListDisplayName());
    }

    @Override
    public long getPlayerTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getPlayerTimeOffset() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public WeatherType getPlayerWeather() {
        // TODO Auto-generated method stub
        return WeatherType.CLEAR;
    }

    @Override
    public float getSaturation() {
        return this.getHandle().getFoodData().getSaturationLevel();
    }

    @Override
    public CardboardScoreboard getScoreboard() {
        return server.getScoreboardManager().getPlayerBoard(this);
    }

    @Override
    public Entity getSpectatorTarget() {
        net.minecraft.world.entity.Entity followed = getHandle().getCamera();
        return followed == getHandle() ? null : ((EntityBridge)followed).getBukkitEntity();
    }

    @Override
    public int getTotalExperience() {
        return this.getHandle().totalExperience;
    }

    @Override
    public float getWalkSpeed() {
    	return this.getHandle().getAbilities().walkingSpeed * 2f;
    }

    @Override
    public void giveExp(int arg0) {
        this.getHandle().giveExperiencePoints(arg0);
    }

    @Override
    public void giveExpLevels(int arg0) {
        this.getHandle().giveExperienceLevels(arg0);
    }

    @Override
    public void hidePlayer(Player arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void hidePlayer(Plugin arg0, Player arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isFlying() {
        return this.getHandle().getAbilities().flying;
    }

    @Override
    public boolean isHealthScaled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isPlayerTimeRelative() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isSleepingIgnored() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isSneaking() {
        return this.getHandle().isShiftKeyDown();
    }

    @Override
    public boolean isSprinting() {
        return this.getHandle().isSprinting();
    }

    @Override
    public void kickPlayer(String arg0) {
        this.getHandle().connection.disconnect(net.minecraft.network.chat.Component.nullToEmpty(arg0));
    }

    @Override
 	public void loadData() {
 		((IMixinMinecraftServer)CraftServer.server).getSaveHandler_BF()
        .load(this.getHandle().nameAndId())
        .map(tag -> TagValueInput.create(ProblemReporter.DISCARDING, super.server.getServer().registryAccess(), tag))
        .ifPresent(this.getHandle()::load);
 	}

    @Override
    public void openBook(ItemStack book) {
        ItemStack hand = getInventory().getItemInMainHand();
        getInventory().setItemInMainHand(book);
        getHandle().openItemGui(org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(book), net.minecraft.world.InteractionHand.MAIN_HAND);
        getInventory().setItemInMainHand(hand);
    }

    @Override
    public boolean performCommand(String command) {
        Preconditions.checkArgument(command != null, "command cannot be null");
        return this.server.dispatchCommand(this, command);
    }

    @Override
    public void playEffect(Location loc, Effect effect, int data) {
        if (getHandle().connection == null) return;

        int packetData = effect.getId();
        ClientboundLevelEventPacket packet = new ClientboundLevelEventPacket(packetData, new BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), data, false);
        getHandle().connection.send(packet);
    }

    @Override
    public <T> void playEffect(Location arg0, Effect arg1, T arg2) {
        // TODO Auto-generated method stub
    }

    @Override
    public void playNote(Location loc, byte instrument, byte note) {
        if (getHandle().connection == null) return;

        String name = null;
        switch (instrument) {
        case 0:
            name = "harp";
            break;
        case 1:
            name = "basedrum";
            break;
        case 2:
            name = "snare";
            break;
        case 3:
            name = "hat";
            break;
        case 4:
            name = "bass";
            break;
        case 5:
            name = "flute";
            break;
        case 6:
            name = "bell";
            break;
        case 7:
            name = "guitar";
            break;
        case 8:
            name = "chime";
            break;
        case 9:
            name = "xylophone";
            break;
        }

        float f = (float) Math.pow(2.0D, (note - 12.0D) / 12.0D);
     // TODO: 1.19
        //getHandle().networkHandler.sendPacket(new PlaySoundS2CPacket(CraftSound.getSoundEffect("block.note_block." + name), net.minecraft.sound.SoundCategory.RECORDS, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), 3.0f, f));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void playNote(Location loc, Instrument instrument, Note note) {
        if (getHandle().connection == null) return;

        String instrumentName = null;
        switch (instrument.ordinal()) {
            case 0:
                instrumentName = "harp";
                break;
            case 1:
                instrumentName = "basedrum";
                break;
            case 2:
                instrumentName = "snare";
                break;
            case 3:
                instrumentName = "hat";
                break;
            case 4:
                instrumentName = "bass";
                break;
            case 5:
                instrumentName = "flute";
                break;
            case 6:
                instrumentName = "bell";
                break;
            case 7:
                instrumentName = "guitar";
                break;
            case 8:
                instrumentName = "chime";
                break;
            case 9:
                instrumentName = "xylophone";
                break;
            case 10:
                instrumentName = "iron_xylophone";
                break;
            case 11:
                instrumentName = "cow_bell";
                break;
            case 12:
                instrumentName = "didgeridoo";
                break;
            case 13:
                instrumentName = "bit";
                break;
            case 14:
                instrumentName = "banjo";
                break;
            case 15:
                instrumentName = "pling";
                break;
            case 16:
                instrumentName = "xylophone";
                break;
        }
        float f = (float) Math.pow(2.0D, (note.getId() - 12.0D) / 12.0D);
        // TODO: 1.19
        //getHandle().networkHandler.sendPacket(new PlaySoundS2CPacket(CraftSound.getSoundEffect("block.note_block." + instrumentName), net.minecraft.sound.SoundCategory.RECORDS, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), 3.0f, f));
    }

    @Override
    public void playSound(Location loc, Sound sound, float volume, float pitch) {
        playSound(loc, sound, org.bukkit.SoundCategory.MASTER, volume, pitch);
    }

    @Override
    public void playSound(Location loc, String sound, float volume, float pitch) {
        playSound(loc, sound, org.bukkit.SoundCategory.MASTER, volume, pitch);
    }

    @Override
    public void resetPlayerTime() {
        // TODO Auto-generated method stub
    }

    @Override
    public void resetPlayerWeather() {
        // TODO Auto-generated method stub
    }

    @Override
    public void resetTitle() {
        this.getHandle().connection.send(new ClientboundClearTitlesPacket(true));
    }

    @Override
    public void saveData() {
        ((IMixinMinecraftServer)CraftServer.server).getSaveHandler_BF().save(this.getHandle());
    }

    @Override
    public void sendBlockChange(Location loc, BlockData block) {
        if (getHandle().connection == null) return;

        ClientboundBlockUpdatePacket packet = new ClientboundBlockUpdatePacket(new BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), ((CraftBlockData) block).getState());
        getHandle().connection.send(packet);
    }

    @Override
    public void sendBlockChange(Location loc, Material material, byte data) {
        if (getHandle().connection == null) return;

        ClientboundBlockUpdatePacket packet = new ClientboundBlockUpdatePacket(new BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), CraftMagicNumbers.getBlock(material, data));
        getHandle().connection.send(packet);
    }

    // @Override
    public boolean sendChunkChange(Location arg0, int arg1, int arg2, int arg3, byte[] arg4) {
        throw new NotImplementedException("Also not in Spigot");
    }

    @Override
    public void sendExperienceChange(float progress) {
        sendExperienceChange(progress, getLevel());
    }

    @Override
    public void sendExperienceChange(float progress, int level) {
        if (getHandle().connection == null) return;

        ClientboundSetExperiencePacket packet = new ClientboundSetExperiencePacket(progress, getTotalExperience(), level);
        getHandle().connection.send(packet);
    }

    @Override
    public void sendMap(MapView map) {
		if (getHandle().connection == null) return;

        /* 1.19: RenderData data = ((CraftMapView) map).render(this);
        Collection<MapIcon> icons = new ArrayList<MapIcon>();
        for (MapCursor cursor : data.cursors) {
            if (cursor.isVisible()) {
                icons.add(new MapIcon(MapIcon.Type.byId(cursor.getRawType()), cursor.getX(), cursor.getY(), cursor.getDirection(), CraftChatMessage.fromStringOrNull(cursor.getCaption())));
            }
        }

        MapUpdateS2CPacket packet = new MapUpdateS2CPacket(map.getId(), map.getScale().getValue(), map.isLocked(), icons, new MapState.UpdateData(0, 0, 128, 128, data.buffer));
        getHandle().networkHandler.sendPacket(packet);
		*/
      // TODO 1.17ify
        /*if (getHandle().networkHandler == null) return;

        RenderData data = ((MapViewImpl) map).render(this);
        Collection<MapIcon> icons = new ArrayList<MapIcon>();
        for (MapCursor cursor : data.cursors) {
            if (cursor.isVisible())
                icons.add(new MapIcon(MapIcon.Type.byId(cursor.getRawType()), cursor.getX(), cursor.getY(), cursor.getDirection(), CraftChatMessage.fromStringOrNull(cursor.getCaption())));
        }

        MapUpdateS2CPacket packet = new MapUpdateS2CPacket(map.getId(), map.getScale().getValue(), true, map.isLocked(), icons, data.buffer, 0, 0, 128, 128);
        getHandle().networkHandler.sendPacket(packet);*/
    }

    @Override
    public void sendRawMessage(String arg0) {
        if (getHandle().connection == null) return;

        //for (Text component : CraftChatMessage.fromString(arg0))
        //    getHandle().networkHandler.sendPacket(new GameMessageS2CPacket(component, MessageType.CHAT, Util.NIL_UUID));
        
        for (net.minecraft.network.chat.Component component : CraftChatMessage.fromString(arg0)) {
            this.getHandle().sendSystemMessage(component);
        }
    }

    @Override
    public void sendSignChange(Location loc, String[] lines) {
       sendSignChange(loc, lines, DyeColor.BLACK);
    }

    @Override
    public void sendSignChange(Location loc, String[] lines, DyeColor dyeColor) {
        if (getHandle().connection == null) return;
        if (lines == null) lines = new String[4];
        if (lines.length < 4)
            throw new IllegalArgumentException("Must have at least 4 lines");

        net.minecraft.network.chat.Component[] components = CardboardSign.sanitizeLines(lines);
        SignBlockEntity sign = new SignBlockEntity(new BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), null);
        //sign.setPos(new BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        sign.getFrontText().setColor(net.minecraft.world.item.DyeColor.byId(dyeColor.getWoolData()));
        System.arraycopy(components, 0, ((IMixinSignBlockEntity)sign).getTextBF(), 0, ((IMixinSignBlockEntity)sign).getTextBF().length);

        getHandle().connection.send(sign.getUpdatePacket());
    }

    @Override
    public void sendTitle(String arg0, String arg1) {
        sendTitle(arg0, arg1, 10, 70, 20);
    }

    @Override
    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        ClientboundSetTitlesAnimationPacket times = new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut);
        this.getHandle().connection.send(times);
        if (title != null) {
            ClientboundSetTitleTextPacket packetTitle = new ClientboundSetTitleTextPacket(CraftChatMessage.fromStringOrNull(title));
            this.getHandle().connection.send(packetTitle);
        }
        if (subtitle != null) {
            ClientboundSetSubtitleTextPacket packetSubtitle = new ClientboundSetSubtitleTextPacket(CraftChatMessage.fromStringOrNull(subtitle));
            this.getHandle().connection.send(packetSubtitle);
        }
    }

    @Override
    public void setAllowFlight(boolean arg0) {
        if (isFlying() && !arg0)
            getHandle().getAbilities().flying = false;

        getHandle().getAbilities().mayfly = arg0;
        getHandle().onUpdateAbilities();
    }

    @Override
    public void setCompassTarget(Location arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setDisplayName(String arg0) {
        this.getHandle().setCustomNameVisible(true);
        this.getHandle().setCustomName(net.minecraft.network.chat.Component.literal(arg0));
    }

    @Override
    public void setExhaustion(float arg0) {
        this.getHandle().causeFoodExhaustion(arg0);
    }

    @Override
    public void setExp(float arg0) {
        this.getHandle().setExperiencePoints((int) arg0);
    }

    @Override
    public void setFlySpeed(float arg0) throws IllegalArgumentException {
        // this.getHandle().airStrafingSpeed = arg0;
        ServerPlayer player = getHandle();
        player.getAbilities().setFlyingSpeed(arg0 / 2f);
        player.onUpdateAbilities();
    }

    @Override
    public void setFlying(boolean arg0) {
        if (!getAllowFlight() && arg0)
            throw new IllegalArgumentException("getAllowFlight() is false, cannot set player flying");

        getHandle().getAbilities().flying = arg0;
        getHandle().onUpdateAbilities();
    }

    @Override
    public void setFoodLevel(int arg0) {
        this.getHandle().getFoodData().setFoodLevel(arg0);
    }

    @Override
    public void setHealthScale(double arg0) throws IllegalArgumentException {
        // TODO Auto-generated method stub
    }

    @Override
    public void setHealthScaled(boolean arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setLevel(int level) {
        this.getHandle().setExperienceLevels(level);
    }

    @Override
    public void setPlayerListFooter(String arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setPlayerListHeader(String arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setPlayerListHeaderFooter(String arg0, String arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setPlayerListName(String arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setPlayerTime(long arg0, boolean arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setPlayerWeather(WeatherType arg0) {
        // TODO Auto-generated method stub
    }

    @Override
	public void setResourcePack(@NotNull String url) {
		sendPack(url, "null", false, null);
	}
	@Override
	public void setResourcePack(@NotNull String url, @Nullable byte[] hash) {
		sendPack(url, hash == null ? "null" : new String(hash), false, null);
	}

	private void sendPack(String url, String hash, boolean required, String text) {
		UUID id = UUID.nameUUIDFromBytes(url.getBytes(StandardCharsets.UTF_8));
		net.minecraft.network.chat.Component txt = text == null ? null : net.minecraft.network.chat.Component.literal(text);

		// TODO: 1.20.5
		// this.getHandle().networkHandler.sendPacket(new ResourcePackSendS2CPacket(id, url, hash, required, txt));
	}

    @Override
    public void setSaturation(float arg0) {
        this.getHandle().getFoodData().setSaturation(arg0);
    }

    @Override
    public void setScoreboard(Scoreboard scoreboard) {
        ServerGamePacketListenerImpl playerConnection = getHandle().connection;
        if (playerConnection == null) throw new IllegalStateException("Cannot set scoreboard yet");
        if (((IMixinPlayNetworkHandler)playerConnection).isDisconnected())
            throw new IllegalStateException("Cannot set scoreboard for invalid CraftPlayer");

        this.server.getScoreboardManager().setPlayerBoard(this, scoreboard);
    }

    @Override
    public void setSleepingIgnored(boolean arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setSneaking(boolean arg0) {
        this.getHandle().setShiftKeyDown(arg0);
    }

    @Override
    public void setSpectatorTarget(Entity arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setSprinting(boolean arg0) {
        this.getHandle().setSprinting(arg0);
    }

    @Override
    public void setTexturePack(String arg0) {
        setResourcePack(arg0);
    }

    @Override
    public void setTotalExperience(int arg0) {
        this.getHandle().totalExperience = arg0;
    }

    /*
    @Override
    public void setWalkSpeed(float arg0) throws IllegalArgumentException {
        this.getHandle().getAbilities().setWalkSpeed(arg0);
    }
    */
    
    private void validateSpeed(float value) {
        Preconditions.checkArgument(value <= 1f && value >= -1f, "Speed value (%s) need to be between -1f and 1f", value);
    }
    
    @Override
    public void setWalkSpeed(float value) {
        this.validateSpeed(value);
        ServerPlayer player = this.getHandle();
        player.getAbilities().walkingSpeed = value / 2f;
        player.onUpdateAbilities();
        this.getHandle().getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(player.getAbilities().walkingSpeed); // SPIGOT-5833: combination of the two in 1.16+
    }

    @Override
    public void showPlayer(Player arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void showPlayer(Plugin arg0, Player arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int count) {
        spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count);
    }

    @Override
    public void spawnParticle(Particle particle, double x, double y, double z, int count) {
        spawnParticle(particle, x, y, z, count, null);
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int count, T data) {
        spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, data);
    }

    @Override
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, T data) {
        spawnParticle(particle, x, y, z, count, 0, 0, 0, data);
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ) {
        spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ);
    }

    @Override
    public void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ) {
        spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, null);
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, T data) {
        spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, data);
    }

    @Override
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, T data) {
        spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, 1, data);
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra);
    }

    @Override
    public void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra, null);
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) {
        spawnParticle(particle, location.getX(), location.getY(), location.getZ(), count, offsetX, offsetY, offsetZ, extra, data);
    }

    
    
    @Override
    public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, T data) {
        if (data != null && !particle.getDataType().isInstance(data))
            throw new IllegalArgumentException("data should be " + particle.getDataType() + " got " + data.getClass());
        
        // ParticleS2CPacket packetplayoutworldparticles = new ParticleS2CPacket(CraftParticle.createParticleParam(particle, data), false, extra, x, y, z, (float)offsetX, (float)offsetY, (float)offsetZ, (float)extra, count);

        boolean force = false;
        
        ClientboundLevelParticlesPacket packetplayoutworldparticles = new ClientboundLevelParticlesPacket(CraftParticle.createParticleParam(particle, data), false, force, x, y, z, (float) offsetX, (float) offsetY, (float) offsetZ, (float) extra, count); // Paper - fix x/y/z precision loss

        
        // ParticleS2CPacket packetplayoutworldparticles = new ParticleS2CPacket(CraftParticle.toNMS(particle, data), true, (float) x, (float) y, (float) z, (float) offsetX, (float) offsetY, (float) offsetZ, (float) extra, count);
        getHandle().connection.send(packetplayoutworldparticles);

    }

    @Override
    public void playSound(Location loc, Sound sound, org.bukkit.SoundCategory category, float volume, float pitch) {
        this.playSound(loc, sound, category, volume, pitch, this.getHandle().random.nextLong());
    }

    @Override
    public void playSound(Location loc, String sound, org.bukkit.SoundCategory category, float volume, float pitch) {
        this.playSound(loc, sound, category, volume, pitch, this.getHandle().random.nextLong());
    }

    @Override
    public void playSound(Location loc, Sound sound, org.bukkit.SoundCategory category, float volume, float pitch, long seed) {
        if (loc == null || sound == null || category == null || this.getHandle().connection == null) return;

        this.playSound0(loc, CraftSound.bukkitToMinecraftHolder(sound), net.minecraft.sounds.SoundSource.valueOf(category.name()), volume, pitch, seed);
    }

    @Override
    public void playSound(Location loc, String sound, org.bukkit.SoundCategory category, float volume, float pitch, long seed) {
        if (loc == null || sound == null || category == null || this.getHandle().connection == null) return;

        this.playSound0(loc, Holder.direct(SoundEvent.createVariableRangeEvent(Identifier.parse(sound))), net.minecraft.sounds.SoundSource.valueOf(category.name()), volume, pitch, seed);
    }

    private void playSound0(Location loc, Holder<SoundEvent> soundEffectHolder, net.minecraft.sounds.SoundSource categoryNMS, float volume, float pitch, long seed) {
        Preconditions.checkArgument(loc != null, "Location cannot be null");

        if (this.getHandle().connection == null) return;

        ClientboundSoundPacket packet = new ClientboundSoundPacket(soundEffectHolder, categoryNMS, loc.getX(), loc.getY(), loc.getZ(), volume, pitch, seed);
        this.getHandle().connection.send(packet);
    }

    @Override
    public void playSound(org.bukkit.entity.Entity entity, Sound sound, org.bukkit.SoundCategory category, float volume, float pitch) {
        this.playSound(entity, sound, category, volume, pitch, this.getHandle().random.nextLong());
    }

    @Override
    public void playSound(org.bukkit.entity.Entity entity, String sound, org.bukkit.SoundCategory category, float volume, float pitch) {
        this.playSound(entity, sound, category, volume, pitch, this.getHandle().random.nextLong());
    }

    @Override
    public void playSound(org.bukkit.entity.Entity entity, Sound sound, org.bukkit.SoundCategory category, float volume, float pitch, long seed) {
        if (!(entity instanceof CraftEntity) || sound == null || category == null || this.getHandle().connection == null) return;

        this.playSound0(entity, CraftSound.bukkitToMinecraftHolder(sound), net.minecraft.sounds.SoundSource.valueOf(category.name()), volume, pitch, seed);
    }

    @Override
    public void playSound(org.bukkit.entity.Entity entity, String sound, org.bukkit.SoundCategory category, float volume, float pitch, long seed) {
        if (!(entity instanceof CraftEntity) || sound == null || category == null || this.getHandle().connection == null) return;

        this.playSound0(entity, Holder.direct(SoundEvent.createVariableRangeEvent(Identifier.parse(sound))), net.minecraft.sounds.SoundSource.valueOf(category.name()), volume, pitch, seed);
    }

    private void playSound0(org.bukkit.entity.Entity entity, Holder<SoundEvent> soundEffectHolder, net.minecraft.sounds.SoundSource categoryNMS, float volume, float pitch, long seed) {
        Preconditions.checkArgument(entity != null, "Entity cannot be null");
        Preconditions.checkArgument(soundEffectHolder != null, "Holder of SoundEffect cannot be null");
        Preconditions.checkArgument(categoryNMS != null, "SoundCategory cannot be null");

        if (this.getHandle().connection == null) return;
        if (!(entity instanceof CraftEntity craftEntity)) return;

        ClientboundSoundEntityPacket packet = new ClientboundSoundEntityPacket(soundEffectHolder, categoryNMS, craftEntity.getHandle(), volume, pitch, seed);
        this.getHandle().connection.send(packet);
    }

    @Override
    public void stopSound(String sound, org.bukkit.SoundCategory category) {
        if (this.getHandle().connection == null) return;

        this.getHandle().connection.send(new ClientboundStopSoundPacket(Identifier.parse(sound), category == null ? net.minecraft.sounds.SoundSource.MASTER : net.minecraft.sounds.SoundSource.valueOf(category.name())));
    }

    @Override
    public void stopSound(org.bukkit.SoundCategory category) {
        if (this.getHandle().connection == null) return;

        this.getHandle().connection.send(new ClientboundStopSoundPacket(null, net.minecraft.sounds.SoundSource.valueOf(category.name())));
    }

    @Override
    public void stopSound(final net.kyori.adventure.sound.SoundStop stop) {
        this.getHandle().connection.send(new ClientboundStopSoundPacket(
                io.papermc.paper.adventure.PaperAdventure.asVanillaNullable(stop.sound()),
                io.papermc.paper.adventure.PaperAdventure.asVanillaNullable(stop.source())
        ));
    }

    @Override
    public void updateCommands() {
        if (this.getHandle().connection == null) return;

        this.server.getServer().getCommands().sendCommands(this.getHandle());
    }

    @Override
    public void updateInventory() {
        this.getHandle().containerMenu.sendAllDataToRemote();
    }

    @SuppressWarnings("deprecation")
    @Override
    public GameMode getGameMode() {
        return GameMode.getByValue(getHandle().gameMode.getGameModeForPlayer().getId());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setGameMode(GameMode mode) {
        if (getHandle().connection == null) return;

        if (mode == null)
            throw new IllegalArgumentException("GameMode cannot be null");

        getHandle().setGameMode(net.minecraft.world.level.GameType.byId(mode.getValue()));
    }

    public GameProfile getProfile() {
    	return this.getHandle().getGameProfile();
    }

    @Override
    public boolean isOp() {
        try {
            // return CraftServer.server.getPlayerManager().isOperator(getProfile());
            
            return CraftServer.server.getPlayerList().isOp(this.getHandle().nameAndId());
            
        } catch (NullPointerException e) {
            try {
                return CraftServer.INSTANCE.getOperatorList().contains(getUniqueId().toString());
            } catch (IOException ex) {
            	NameAndId gp = new NameAndId(super.getUniqueId(), this.getName());
                return CraftServer.server.getPlayerList().isOp(gp);
            }
        }
    }

    @Override
	public void setOp(boolean value) {
    	if (value != this.isOp()) {
    		if (value) {
    			CraftServer.server.getPlayerList().op(this.getHandle().nameAndId());
    		} else {
    			CraftServer.server.getPlayerList().deop(this.getHandle().nameAndId());
    		}

    		super.perm.recalculatePermissions();
    	}
	}

    @Override
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause cause) {
        Preconditions.checkArgument(location != null, "location");
        Preconditions.checkArgument(location.getWorld() != null, "location.world");
        location.checkFinite();
        ServerPlayer entity = getHandle();

        if (getHealth() == 0 || entity.isRemoved() || entity.connection == null || entity.isVehicle())
            return false;

        Location from = this.getLocation();
        Location to = location;

        PlayerTeleportEvent event = new PlayerTeleportEvent(this, from, to, cause);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled())
            return false;

        entity.stopRiding();

        from = event.getFrom();
        to = event.getTo();

        ServerLevel toWorld = (ServerLevel) ((CraftWorld) to.getWorld()).getHandle();

        if (getHandle().inventory != getHandle().inventory)
            getHandle().closeContainer();
        
        System.out.println("Hello! " + from.getWorld() + " / " + to.getWorld());

        Vec3 pos = new Vec3(location.getX(), location.getY(), location.getZ());
        Vec3 velocity = new Vec3(0, 0, 0);
        float yaw = location.getYaw();
        float pitch = location.getPitch();

        TeleportTransition target = new TeleportTransition(toWorld, pos, velocity, yaw, pitch, TeleportTransition.DO_NOTHING);

        this.getHandle().teleport(target);

        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OfflinePlayer))
            return false;

        OfflinePlayer other = (OfflinePlayer) obj;
        if ((this.getUniqueId() == null) || (other.getUniqueId() == null))
            return false;

        boolean uuidEquals = this.getUniqueId().equals(other.getUniqueId());
        boolean idEquals = true;

        if (other instanceof CraftPlayer)
            idEquals = this.getEntityId() == ((CraftPlayer) other).getEntityId();

        return uuidEquals && idEquals;
    }

    public InetSocketAddress getRawAddress_BF() {
        /*
		if (Bukkit.getPluginManager().isPluginEnabled("ProtocolSupport")) {
            System.out.println("PS getRawAddress");
            try {
                Class<?> ps = ReflectionRemapper.getClassFromJPL("protocolsupport.zplatform.impl.fabric.FabricMiscUtils");
                HashMap<InetSocketAddress, InetSocketAddress> map = (HashMap<InetSocketAddress, InetSocketAddress>) ps.getField("rawAddressMap").get(null);
                return map.get(this.getAddress());
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
		*/
        IMixinPlayNetworkHandler im = (IMixinPlayNetworkHandler) this.getHandle().connection;
        return (InetSocketAddress) ((IMixinClientConnection) (im.cb_get_connection())).getRawAddress();
    }

    private final Player.Spigot spigot = new Player.Spigot() {

        int err = 0;

        @Override
        public InetSocketAddress getRawAddress() {
            try {
                return getRawAddress_BF();
            } catch (NullPointerException ex) {
                // What is going on?
                if (err > 3) {
                    System.exit(0);
                    return null;
                } 
                err++;
                ex.printStackTrace();
                return ((java.net.InetSocketAddress)CraftPlayer.this.getAddress());
            }
        }

        // @Override
        public boolean getCollidesWithEntities() {
            return CraftPlayer.this.isCollidable();
        }

        // @Override
        public void setCollidesWithEntities(boolean collides) {
            CraftPlayer.this.setCollidable(collides);
        }

        @Override
        public void respawn() {
            if (getHealth() <= 0 && isOnline()) {
                CraftPlayer.this.getHandle().level().getServer().getPlayerList().respawn( getHandle(), false, RemovalReason.KILLED );

                IMixinPlayNetworkHandler imix = (IMixinPlayNetworkHandler) CraftPlayer.this.getHandle().connection;
                imix.cardboard$spigot_player_respawn();
            }
        }

        @Override
        public Set<Player> getHiddenPlayers() {
            return java.util.Collections.emptySet();
        }

        @Override
        public void sendMessage(BaseComponent component) {
            sendMessage(new BaseComponent[] { component });
        }

        @Override
        public void sendMessage(BaseComponent... components) {
           if (null == getHandle().connection) return;

           	// TODO: 1.19
            //GameMessageS2CPacket packet = new GameMessageS2CPacket(null, MessageType.SYSTEM, this.getHandle().getUuid());
            //((IGameMessagePacket)packet).setBungeeComponents(components);
            //getHandle().networkHandler.sendPacket(packet);
            
            getHandle().sendSystemMessage( net.minecraft.network.chat.Component.literal( BaseComponent.toLegacyText(components) ) );
        }

        @Override
        public void sendMessage(net.md_5.bungee.api.ChatMessageType position, BaseComponent component) {
            sendMessage( position, new BaseComponent[] { component } );
        }

        @Override
        public void sendMessage(net.md_5.bungee.api.ChatMessageType position, BaseComponent... components) {
            if (null == getHandle().connection) return;

            // TODO: 1.19
            
           /* GameMessageS2CPacket packet = new GameMessageS2CPacket(null, MessageType.byId((byte) position.ordinal()), this.getHandle().getUuid());
            if (position == net.md_5.bungee.api.ChatMessageType.ACTION_BAR)
                components = new BaseComponent[]{new net.md_5.bungee.api.chat.TextComponent(BaseComponent.toLegacyText(components))};
            
            ((IGameMessagePacket)packet).setBungeeComponents(components);
            getHandle().networkHandler.sendPacket(packet);*/
            
            getHandle().sendSystemMessage( net.minecraft.network.chat.Component.literal( BaseComponent.toLegacyText(components) ) );

            //getHandle().networkHandler.sendPacket(new GameMessageS2CPacket(components, position == ChatMessageType.ACTION_BAR));
        }
    };

    @Override
    public org.bukkit.entity.Player.Spigot spigot() {
        return spigot;
    }

    @Override
    public Location getBedSpawnLocation() {
        /*
    	World world = ((IMixinWorld)getHandle().getServer().getWorld(getHandle().getSpawnPointDimension())).getCraftWorld();
        BlockPos bed = getHandle().getSpawnPointPosition();

        if (world != null && bed != null) {
            Optional<Vec3d> spawnLoc = ServerPlayerEntity.findRespawnPosition((ServerWorld) ((CraftWorld) world).getHandle(), bed, getHandle().getSpawnAngle(), getHandle().isSpawnForced(), true).map(RespawnPos::pos);
            if (spawnLoc.isPresent()) {
                Vec3d vec = spawnLoc.get();
                return new Location(world, vec.x, vec.y, vec.z);
            }
        }
        */
    	// TODO: test this
    	return getRespawnLocation(true);
        //return null;
    }

    @Override
    public void setBedSpawnLocation(Location location) {
        setBedSpawnLocation(location, false);
    }

    @Override
    public void setRespawnLocation(Location location) {
        this.setRespawnLocation(location, false);
    }

    @Override
    public void setBedSpawnLocation(Location location, boolean override) {
        this.setRespawnLocation(location, override);
    }

    @Override
    public void setRespawnLocation(Location location, boolean override) {
        if (location == null) {
           this.getHandle().setRespawnPosition(null, false); // , com.destroystokyo.paper.event.player.PlayerSetSpawnEvent.Cause.PLUGIN);
        } else {
           this.getHandle()
              .setRespawnPosition(
                 new ServerPlayer.RespawnConfig(
                    new LevelData.RespawnData(
                       GlobalPos.of(((CraftWorld)location.getWorld()).getHandle().dimension(), CraftLocation.toBlockPosition(location)),
                       location.getYaw(),
                       location.getPitch()
                    ),
                    override
                 ),
                 false // ,
                 // com.destroystokyo.paper.event.player.PlayerSetSpawnEvent.Cause.PLUGIN
              );
        }
     }

    public void setFirstPlayed(long modified) {
        // TODO Auto-generated method stub
    }

    @Override
    public EntityType getType() {
        return EntityType.PLAYER;
    }

    public void updateScaledHealth() {
        // TODO Auto-generated method stub
    }

    // SPIGOT-759
    public void sendRawMessage(UUID uuid, String msg) {
        this.sendRawMessage(msg);
    }

    // PaperAPI - START
    public void setTitleTimes(int fadeInTicks, int stayTicks, int fadeOutTicks) {
     // TODO 1.17ify getHandle().networkHandler.sendPacket(new TitleS2CPacket(TitleS2CPacket.Action.TIMES, null, 0, 0, 0));
    }

    public void showTitle(BaseComponent[] title) {
     // TODO 1.17ify   getHandle().networkHandler.sendPacket(new TitleS2CPacket(TitleS2CPacket.Action.TITLE, Text.of(ComponentSerializer.toString(title)), 0, 0, 0));
    }

    public void setSubtitle(BaseComponent[] subtitle) {
     // TODO 1.17ify   getHandle().networkHandler.sendPacket(new TitleS2CPacket(TitleS2CPacket.Action.SUBTITLE, Text.of(ComponentSerializer.toString(subtitle)), 0, 0, 0));
    }

    public void showTitle(BaseComponent title) {
        showTitle(new BaseComponent[]{title});
    }

    public void setSubtitle(BaseComponent subtitle) {
        showTitle(new BaseComponent[]{subtitle});
    }

    public void showTitle(BaseComponent[] title, BaseComponent[] subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        setTitleTimes(fadeInTicks, stayTicks, fadeOutTicks);
        setSubtitle(subtitle);
        showTitle(title);
    }

    public void showTitle(BaseComponent title, BaseComponent subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        setTitleTimes(fadeInTicks, stayTicks, fadeOutTicks);
        setSubtitle(subtitle);
        showTitle(title);
    }

    public void sendTitle(Title title) {
        Preconditions.checkNotNull(title, "Title is null");
        setTitleTimes(title.getFadeIn(), title.getStay(), title.getFadeOut());
        setSubtitle(title.getSubtitle() == null ? new BaseComponent[0] : title.getSubtitle());
        showTitle(title.getTitle());
    }

    public void updateTitle(Title title) {
        Preconditions.checkNotNull(title, "Title is null");
        setTitleTimes(title.getFadeIn(), title.getStay(), title.getFadeOut());
        if (title.getSubtitle() != null) {
            setSubtitle(title.getSubtitle());
        }
        showTitle(title.getTitle());
    }

    @Override
    public void sendActionBar(BaseComponent[] message) {
    	if (this.getHandle().connection != null && message != null) {
    		ClientboundSetActionBarTextPacket packet = new ClientboundSetActionBarTextPacket(CraftChatMessage.bungeeToVanilla(message));
    		this.getHandle().connection.send(packet);
    	}
    }

    @Override
    public void sendActionBar(String message) {
    	if (this.getHandle().connection != null && message != null && !message.isEmpty()) {
    		this.getHandle().connection.send(new ClientboundSetActionBarTextPacket(CraftChatMessage.fromStringOrNull(message)));
    	}
    }

    @Override
    public void sendActionBar(char alternateChar, String message) {
        if (message == null || message.isEmpty()) return;
        sendActionBar(org.bukkit.ChatColor.translateAlternateColorCodes(alternateChar, message));
    }

    public int getViewDistance() {
        throw new NotImplementedException("Was Removed from Paper");
    }

    public void setViewDistance(int viewDistance) {
        throw new NotImplementedException("Was Removed from Paper");
    }
    // PaperAPI - END

    @Override
    public long getLastLogin() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getLastSeen() {
    	if (this.isOnline()) {
    		return System.currentTimeMillis();
    	}

        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getProtocolVersion() {
        // TODO Auto-generated method stub
        return GameVersion.INSTANCE.getProtocolVersion();
    }

    @Override
    public InetSocketAddress getVirtualHost() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int applyMending(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }
    
    /*
    public int applyMending(int amount) {
        ServerPlayerEntity handle = this.getHandle();
        Optional<EnchantmentEffectContext> stackEntry = EnchantmentHelper.chooseEquipmentWith(
           EnchantmentEffectComponentTypes.REPAIR_WITH_XP, handle, net.minecraft.item.ItemStack::isDamaged
        );
        net.minecraft.item.ItemStack itemstack = stackEntry.map(EnchantmentEffectContext::stack).orElse(net.minecraft.item.ItemStack.EMPTY);
        if (!itemstack.isEmpty() && itemstack.getItem().getComponents().contains(DataComponentTypes.MAX_DAMAGE)) {
           ExperienceOrbEntity orb = net.minecraft.entity.EntityType.EXPERIENCE_ORB.create(handle.getEntityWorld(), SpawnReason.COMMAND);
           orb.setValue(amount);
           // orb.spawnReason = org.bukkit.entity.ExperienceOrb.SpawnReason.CUSTOM;
           orb.setPos(handle.getX(), handle.getY(), handle.getZ());
           int possibleDurabilityFromXp = EnchantmentHelper.getRepairWithExperience(handle.getEntityWorld(), itemstack, amount);
           int i = Math.min(possibleDurabilityFromXp, itemstack.getDamage());
           int consumedExperience = i > 0 ? i * amount / possibleDurabilityFromXp : possibleDurabilityFromXp;
           PlayerItemMendEvent event = CraftEventFactory.callPlayerItemMendEvent(handle, orb, itemstack, stackEntry.get().slot(), i, consumedExperience);
           i = event.getRepairAmount();
           orb.discard();
           if (!event.isCancelled()) {
              amount -= consumedExperience;
              itemstack.setDamage(itemstack.getDamage() - i);
           }
        }

        return amount;
     }
     */

    @Override
    public Firework boostElytra(ItemStack arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean getAffectsSpawning() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getClientBrandName() {
        // TODO Auto-generated method stub  	
        return "Vanilla";
    }

    @Override
    public <T> T getClientOption(ClientOption<T> arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public float getCooldownPeriod() {
    	return this.getHandle().getCurrentItemAttackStrengthDelay();
    }

    @Override
    public float getCooledAttackStrength(float adjustTicks) {
    	return this.getHandle().getAttackStrengthScale(adjustTicks);
    }

    @Override
    public PlayerProfile getPlayerProfile() {
    	return new CraftPlayerProfile(this);
    }

    @Override
    public String getResourcePackHash() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Status getResourcePackStatus() {
        // TODO Auto-generated method stub
        return Status.SUCCESSFULLY_LOADED;
    }

    @Override
    public void giveExp(int exp, boolean applyMending) {
    	if (applyMending) {
    		exp = this.applyMending(exp);
    	}

    	this.getHandle().giveExperiencePoints(exp);
    }

    @Override
    public boolean hasResourcePack() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void hideTitle() {
    	this.getHandle().connection.send(new ClientboundClearTitlesPacket(false));
    }

    @Override
    public void resetCooldown() {
    	this.getHandle().resetAttackStrengthTicker();
    }

    @Override
    public void setAffectsSpawning(boolean arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setPlayerListHeaderFooter(BaseComponent[] arg0, BaseComponent[] arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setPlayerListHeaderFooter(BaseComponent arg0, BaseComponent arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setPlayerProfile(PlayerProfile arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setResourcePack(String arg0, String arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public @NotNull Component displayName() {
        // TODO Auto-generated method stub
        return Component.text(this.getDisplayName());
    }

    @Override
    public void displayName(@Nullable Component arg0) {
        // TODO Auto-generated method stub
    	if (arg0 instanceof TextComponent) {
    		this.setDisplayName( ((TextComponent)arg0).content() );
    	}
    }

	@Override
	public int getPing() {
		return this.getHandle().connection.latency();
	}

    @Override
    public @NotNull Set<Player> getTrackedPlayers() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void kick(Component message) {
        this.kick(message, PlayerKickEvent.Cause.PLUGIN);
    }

    @Override
    public void kick(Component message, Cause cause) {
    	AsyncCatcher.catchOp("player kick");
        ServerGamePacketListenerImpl connection = this.getHandle().connection;
        if (connection != null) {
        	if (null != message) {
        		connection.disconnect( PaperAdventure.asVanilla(Component.empty()) );
        	}
        	connection.disconnect( PaperAdventure.asVanilla(message) );
        }
    }

    @Override
    public @NotNull Locale locale() {
        // TODO Auto-generated method stub
        return Locale.ENGLISH;
    }

    @Override
    public @Nullable Component playerListFooter() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public @Nullable Component playerListHeader() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public @Nullable Component playerListName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void playerListName(@Nullable Component arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void sendBlockDamage(@NotNull Location loc, float progress) {
        Preconditions.checkArgument(loc != null, "loc must not be null");
        Preconditions.checkArgument((double)progress >= 0.0 && (double)progress <= 1.0, "progress must be between 0.0 and 1.0 (inclusive)");
        if (this.getHandle().connection == null) {
            return;
        }
        int stage = (int)(9.0f * progress);
        ClientboundBlockDestructionPacket packet = new ClientboundBlockDestructionPacket(this.getHandle().getId(), new BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), stage);
        this.getHandle().connection.send(packet);
    }

    @Override
    public void sendOpLevel(byte level) {
    	Preconditions.checkArgument(level >= 0 && level <= 4, "Level must be within [%s, %s]", 0, 4);
        // super.server.getServer().getPlayerManager().sendPlayerPermissionLevel(this.getHandle(), level, false);
    }

    @Override
    public void sendSignChange(@NotNull Location arg0, @Nullable List<? extends Component> arg1) throws IllegalArgumentException {
        // TODO Auto-generated method stub
    }

    @Override
    public void sendSignChange(@NotNull Location arg0, @Nullable List<? extends Component> arg1, @NotNull DyeColor arg2)
            throws IllegalArgumentException {
        // TODO Auto-generated method stub
    }

    //public BlockPos posAtLogin;
    //public boolean in;

    /*
     * Save BlockPos from {@link PlayerManager#onPlayerConnect}
     *
    public void setLoginPos(BlockPos pos) {
        this.posAtLogin = pos;
        this.in = this.getHandle().isInvulnerable();
    }*/

    // 1.17 API Start

    @Override
    public boolean breakBlock(@NotNull Block b) {
        return this.getHandle().gameMode.destroyBlock(new BlockPos(b.getX(), b.getY(), b.getZ()));
    }

    @Override
    public int getNoTickViewDistance() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getSendViewDistance() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void sendSignChange(@NotNull Location arg0, @Nullable List<? extends Component> arg1, @NotNull DyeColor arg2,
            boolean arg3) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void sendSignChange(@NotNull Location arg0, @Nullable String[] arg1, @NotNull DyeColor arg2, boolean arg3)
            throws IllegalArgumentException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setNoTickViewDistance(int i) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setResourcePack(@NotNull String arg0, @NotNull String arg1, boolean arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setResourcePack(@NotNull String arg0, @NotNull String arg1, boolean arg2, @Nullable Component arg3) {
        // TODO Auto-generated method stub
        this.setResourcePack(arg0, arg1, arg2);
    }

    @Override
    public void setSendViewDistance(int i) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void stopAllSounds() {
        if (this.getHandle().connection == null) return;

        this.getHandle().connection.send(new ClientboundStopSoundPacket(null, null));
    }

    // 1.18.2 api:
    
	@Override
	public boolean canSee(@NotNull Entity arg0) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public @Nullable GameMode getPreviousGameMode() {
		net.minecraft.world.level.GameType previousGameMode = this.getHandle().gameMode.getPreviousGameModeForPlayer();
        return previousGameMode == null ? null : GameMode.getByValue((int)previousGameMode.getId());
	}

	@Override
	public int getSimulationDistance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public @Nullable WorldBorder getWorldBorder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void hideEntity(@NotNull Plugin arg0, @NotNull Entity arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isAllowingServerListings() {
		// TODO Auto-generated method stub
		return false;
	}

	private static final Component DEFAULT_KICK_COMPONENT = Component.translatable("multiplayer.disconnect.kicked");
	
	@Override
	public void kick() {
		this.kick(DEFAULT_KICK_COMPONENT);
	}

	@Override
	public void playSound(@NotNull Entity arg0, @NotNull Sound arg1, float arg2, float arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendEquipmentChange(@NotNull LivingEntity arg0, @NotNull EquipmentSlot arg1, @NotNull ItemStack arg2) {
		// TODO Auto-generated method stub
		
	}
	
	public float getScaledHealth() {
		return (float)(this.isHealthScaled() ? this.getHealth() * this.getHealthScale() / this.getMaxHealth() : this.getHealth());
	}

	@Override
	public void sendHealthUpdate() {
		FoodData foodData = this.getHandle().getFoodData();
		ClientboundSetHealthPacket packet = new ClientboundSetHealthPacket(this.getScaledHealth(), foodData.getFoodLevel(), foodData.getSaturationLevel());
		
		// if (this.getHandle().queueHealthUpdatePacket) {
		// 	this.getHandle().queuedHealthUpdatePacket = packet;
		// } else {
			this.getHandle().connection.send(packet);
		// }
	}

	@Override
	public void sendHealthUpdate(double health, int foodLevel, float saturation) {
		this.getHandle().connection.send(new ClientboundSetHealthPacket((float)health, foodLevel, saturation));
	}

	// @Override
	// public void sendMultiBlockChange(@NotNull Map<Location, BlockData> arg0, boolean arg1) {
	public void sendMultiBlockChange(@NotNull Map<? extends Position, BlockData> arg0, boolean arg1) {

		// TODO Auto-generated method stub
		
	}

	@Override
	public void setResourcePack(@NotNull String arg0, @Nullable byte[] arg1, @Nullable String arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setResourcePack(@NotNull String arg0, @Nullable byte[] arg1, boolean arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setResourcePack(@NotNull String arg0, @Nullable byte[] arg1, @Nullable String arg2, boolean arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setResourcePack(@NotNull String arg0, byte @Nullable [] arg1, @Nullable Component arg2, boolean arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSimulationDistance(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setWorldBorder(@Nullable WorldBorder arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showDemoScreen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showEntity(@NotNull Plugin arg0, @NotNull Entity arg1) {
		// TODO Auto-generated method stub
		
	}
	
	// 1.19.2

    @Override
    public void addAdditionalChatCompletions(@NonNull Collection<String> completions) {
        this.getHandle().connection.send(new net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket(
                net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket.Action.ADD,
                new ArrayList<>(completions)
        ));
    }

    @Override
    public void removeAdditionalChatCompletions(@NonNull Collection<String> completions) {
        this.getHandle().connection.send(new net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket(
                net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket.Action.REMOVE,
                new ArrayList<>(completions)
        ));
    }

	@Override
	public int getWardenTimeSinceLastWarning() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWardenWarningCooldown() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWardenWarningLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void increaseWardenWarningLevel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendBlockChanges(@NotNull Collection<BlockState> arg0, boolean arg1) {
		this.sendBlockChanges(arg0);
	}

	@Override
	public void sendBlockDamage(Location loc, float progress, int sourceId) {
        if (this.getHandle().connection == null) {
            return;
        }
        int stage = (int)(9.0f * progress);
        if (progress == 0.0f) {
            stage = -1;
        }
        ClientboundBlockDestructionPacket packet = new ClientboundBlockDestructionPacket(sourceId, CraftLocation.toBlockPosition(loc), stage);
        this.getHandle().connection.send(packet);
    }

	@Override
	public void setWardenTimeSinceLastWarning(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setWardenWarningCooldown(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setWardenWarningLevel(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showElderGuardian(boolean silent) {
		if (this.getHandle().connection != null) {
            this.getHandle().connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.GUARDIAN_ELDER_EFFECT, silent ? 0.0f : 1.0f));
        }
	}
	
	// 1.19.4:

    @Override
    public void addCustomChatCompletions(Collection<String> completions) {
        this.sendCustomChatCompletionPacket(completions, ClientboundCustomChatCompletionsPacket.Action.ADD);
    }

    @Override
    public void removeCustomChatCompletions(Collection<String> completions) {
        this.sendCustomChatCompletionPacket(completions, ClientboundCustomChatCompletionsPacket.Action.REMOVE);
    }

    @Override
    public void setCustomChatCompletions(Collection<String> completions) {
        this.sendCustomChatCompletionPacket(completions, ClientboundCustomChatCompletionsPacket.Action.SET);
    }

    private void sendCustomChatCompletionPacket(Collection<String> completions, ClientboundCustomChatCompletionsPacket.Action action) {
        if (this.getHandle().connection == null) return;

        ClientboundCustomChatCompletionsPacket packet = new ClientboundCustomChatCompletionsPacket(action, new ArrayList<>(completions));
        this.getHandle().connection.send(packet);
    }

	@Override
	public int getExpCooldown() {
        return this.getHandle().takeXpDelay;
	}

	@Override
	public @NotNull TriState hasFlyingFallDamage() {
		// TODO Auto-generated method stub
		return TriState.NOT_SET;
		//         return this.getHandle().flyingFallDamage;
	}

	@Override
	public boolean hasSeenWinScreen() {
        return this.getHandle().seenCredits;
	}

	@Override
	public void openSign(@NotNull Sign arg0, @NotNull Side arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendBlockDamage(@NotNull Location arg0, float arg1, @NotNull Entity arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendEquipmentChange(@NotNull LivingEntity arg0, @NotNull Map<EquipmentSlot, ItemStack> arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendHurtAnimation(float yaw) {
        if (this.getHandle().connection == null) {
            return;
        }
        float actualYaw = yaw + 90.0f;
        this.getHandle().connection.send(new ClientboundHurtAnimationPacket(this.getEntityId(), actualYaw));
	}

    public void sendHurtAnimation(float yaw, Entity target) {
        if (this.getHandle().connection == null) {
            return;
        }
        float actualYaw = yaw + 90.0f;
        this.getHandle().connection.send(new ClientboundHurtAnimationPacket(target.getEntityId(), actualYaw));
    }

	@Override
    public void setExpCooldown(int ticks) {
        // TODO
		// this.getHandle().experiencePickUpDelay = CraftEventFactory.callPlayerXpCooldownEvent(this.getHandle(), ticks, PlayerExpCooldownChangeEvent.ChangeReason.PLUGIN).getNewCooldown();
    }

	@Override
	public void setFlyingFallDamage(@NotNull TriState arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
    public void setHasSeenWinScreen(boolean hasSeenWinScreen) {
        this.getHandle().seenCredits = hasSeenWinScreen;
    }

	@Override
	public void showWinScreen() {
        if (this.getHandle().connection == null) {
            return;
        }
        ClientboundGameEventPacket packet = new ClientboundGameEventPacket(ClientboundGameEventPacket.WIN_GAME, 1.0f);
        this.getHandle().connection.send(packet);
	}
	
	// 1.20 API:

	@Override
	public boolean isConnected() {
		return !this.getHandle().hasDisconnected();
	}

	@Override
	public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String reason, @Nullable Date expires,
			@Nullable String source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String reason,
			@Nullable Instant expires, @Nullable String source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String reason,
			@Nullable Duration duration, @Nullable String source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @UnmodifiableView @NotNull Iterable<? extends BossBar> activeBossBars() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String reason, @Nullable Date expires,
			@Nullable String source, boolean kickPlayer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String reason,
			@Nullable Instant expires, @Nullable String source, boolean kickPlayer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E extends BanEntry<? super PlayerProfile>> @Nullable E ban(@Nullable String reason,
			@Nullable Duration duration, @Nullable String source, boolean kickPlayer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @Nullable BanEntry<InetAddress> banIp(@Nullable String reason, @Nullable Date expires,
			@Nullable String source, boolean kickPlayer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @Nullable BanEntry<InetAddress> banIp(@Nullable String reason, @Nullable Instant expires,
			@Nullable String source, boolean kickPlayer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @Nullable BanEntry<InetAddress> banIp(@Nullable String reason, @Nullable Duration duration,
			@Nullable String source, boolean kickPlayer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendBlockChanges(Collection<org.bukkit.block.BlockState> blocks) {
        Preconditions.checkArgument((blocks != null ? 1 : 0) != 0, (Object)"blocks must not be null");
        if (this.getHandle().connection == null || blocks.isEmpty()) {
            return;
        }
        HashMap<SectionPos, ChunkSectionChanges> changes = new HashMap<SectionPos, ChunkSectionChanges>();
        for (org.bukkit.block.BlockState blockState : blocks) {
            CraftBlockState cstate = (CraftBlockState)blockState;
            BlockPos blockPosition = cstate.getPosition();
            SectionPos sectionPosition = SectionPos.of(blockPosition);
            ChunkSectionChanges sectionChanges = changes.computeIfAbsent(sectionPosition, ignore -> new ChunkSectionChanges());
            sectionChanges.positions().add(SectionPos.sectionRelativePos(blockPosition));
            sectionChanges.blockData().add(cstate.getHandle());
        }
        for (Map.Entry entry : changes.entrySet()) {
            ChunkSectionChanges chunkChanges = (ChunkSectionChanges)entry.getValue();

            ClientboundSectionBlocksUpdatePacket packet = new ClientboundSectionBlocksUpdatePacket((SectionPos)entry.getKey(), chunkChanges.positions(), null);
            
            net.minecraft.world.level.block.state.BlockState[] states = (net.minecraft.world.level.block.state.BlockState[])chunkChanges.blockData().toArray(net.minecraft.world.level.block.state.BlockState[]::new);
            
            ((ClientboundSectionBlocksUpdatePacketBridge)packet).cardboard$set_block_states(states);
            this.getHandle().connection.send(packet);
        }
    }
	
	// @Desugar
	private record ChunkSectionChanges(ShortSet positions, List<net.minecraft.world.level.block.state.BlockState> blockData) {

        public ChunkSectionChanges() {
            this(new ShortArraySet(), new ArrayList<>());
        }
    }

	@Override
	public void sendMultiBlockChange(@NotNull Map<? extends Position, BlockData> blockChanges) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendBlockUpdate(@NotNull Location loc, @NotNull TileState tileState) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isListed(@NotNull Player other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean unlistPlayer(@NotNull Player other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean listPlayer(@NotNull Player other) {
		// TODO Auto-generated method stub
		return false;
	}

	/*@Override
	public boolean teleport(@NotNull Location arg0, @NotNull TeleportCause arg1, boolean arg2, boolean arg3,
			@NotNull RelativeTeleportFlag @NotNull... arg4) {
		// TODO Auto-generated method stub
		return this.teleport(arg0, arg1);
	}*/
	
	// 1.20.2 API:
	
    public void resetIdleDuration() {
        this.getHandle().resetLastActionTime();
    }

	@Override
	public @NotNull Duration getIdleDuration() {
		return Duration.ofMillis(Util.getMillis() - this.getHandle().getLastActionTime());
	}
	
	// 1.20.3 API:

	@Override
	public void setResourcePack(@NotNull UUID id, @NotNull String url, @Nullable byte[] hash, @Nullable String prompt,
			boolean force) {
		String hashStr = "";
        if (hash != null) {
            Preconditions.checkArgument(hash.length == 20, "Resource pack hash should be 20 bytes long but was %s", hash.length);
            hashStr = BaseEncoding.base16().lowerCase().encode(hash);
        }
        
        if (null == prompt) {
        	prompt = "hello";
        }
        
        Optional<net.minecraft.network.chat.Component> opt = Optional.of( CraftChatMessage.fromStringOrNull(prompt) );

        this.handlePushResourcePack(new ClientboundResourcePackPushPacket(id, url, hashStr, force, opt), true);
	}
	
    public void removeResourcePacks() {
        if (this.getHandle().connection == null) return;
        this.getHandle().connection.send(new ClientboundResourcePackPopPacket(Optional.empty()));
    }
	
    private void handlePushResourcePack(ClientboundResourcePackPushPacket resourcePackPushPacket, boolean resetBeforePush) {
        if (this.getHandle().connection == null) return;

        if (resetBeforePush) {
            this.removeResourcePacks();
        }
        this.getHandle().connection.send(resourcePackPushPacket);
    }
    
    // 1.20.4 API

	@Override
    public Location getRespawnLocation() {
        return getRespawnLocation(false);
    }
	
	public Location getRespawnLocation(boolean loadLocationAndValidate) {
		ServerPlayer.RespawnConfig respawnConfig = this.getHandle().getRespawnConfig();
		if (respawnConfig == null) {
			return null;
		} else {
			LevelData.RespawnData respawnData = respawnConfig.respawnData();
			ServerLevel world = super.server.getServer().getLevel(respawnData.dimension());
			if (world == null) {
				return null;
			} else {
				return !loadLocationAndValidate
	           ? CraftLocation.toBukkit(respawnData.pos(), world, respawnData.yaw(), respawnData.pitch())
	           : ServerPlayer.findRespawnAndUseSpawnBlock(world, respawnConfig, false)
	              .map(pos -> CraftLocation.toBukkit(pos.position(), world, pos.yaw(), pos.pitch()))
	              .orElse(null);
			}
		}
	}

	/*
	@Override
	public void setRespawnLocation(Location location) {
        this.setRespawnLocation(location, false);
    }

	@Override
    public void setRespawnLocation(Location location, boolean override) {
        if (location == null) {
            this.getHandle().setSpawnPoint(null, null, 0.0f, override, false);
        } else {
            this.getHandle().setSpawnPoint(((CraftWorld)location.getWorld()).getHandle().getRegistryKey(), CraftLocation.toBlockPosition(location), location.getYaw(), override, false);
        }
    }
    */

	@Override
	public void sendPotionEffectChange(@NotNull LivingEntity entity, @NotNull PotionEffect effect) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendPotionEffectChangeRemove(@NotNull LivingEntity entity, @NotNull PotionEffectType type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public @Range(from = 0, to = 2147483647) int calculateTotalExperiencePoints() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setExperienceLevelAndProgress(int totalExperience) {
        int level = this.calculateLevelsForExperiencePoints(totalExperience);
        int remainingPoints = totalExperience - this.calculateTotalExperiencePoints(level);
        this.getHandle().experienceLevel = level;
        this.getHandle().experienceProgress = (float)remainingPoints / (float)this.getExperiencePointsNeededForNextLevel();
        this.getHandle().lastSentExp = -1;
    }
	
	private int calculateTotalExperiencePoints(int level) {
        if (level <= 16) {
            return (int)(Math.pow(level, 2.0) + (double)(6 * level));
        }
        if (level <= 31) {
            return (int)(2.5 * Math.pow(level, 2.0) - 40.5 * (double)level + 360.0);
        }
        return (int)(4.5 * Math.pow(level, 2.0) - 162.5 * (double)level + 2220.0);
    }
	
	private int calculateLevelsForExperiencePoints(int points) {
        if (points <= 352) {
            return (int)Math.floor(Math.sqrt(points + 9) - 3.0);
        }
        if (points <= 1507) {
            return (int)Math.floor(8.1 + Math.sqrt(0.4 * ((double)points - 195.975)));
        }
        return (int)Math.floor(18.055555555555557 + Math.sqrt(0.2222222222222222 * ((double)points - 752.9861111111111)));
    }

	@Override
	public int getExperiencePointsNeededForNextLevel() {
		return this.getHandle().getXpNeededForNextLevel();
	}

	@Override
	public void setResourcePack(UUID uuid, String url, byte[] hashBytes, Component prompt, boolean force) {
        String hash;
        if (hashBytes != null) {
            hash = BaseEncoding.base16().lowerCase().encode(hashBytes);
        } else {
            hash = "";
        }
        this.getHandle().connection.send(new ClientboundResourcePackPopPacket(Optional.empty()));
        this.getHandle().connection.send(new ClientboundResourcePackPushPacket(uuid, url, hash, force, Optional.ofNullable(prompt).map(PaperAdventure::asVanilla)));
    }

	@Override
	public void addResourcePack(UUID id, String url, byte[] hash, String prompt, boolean force) {
		String hashStr = "";
        if (hash != null) {
            Preconditions.checkArgument((hash.length == 20 ? 1 : 0) != 0, (String)"Resource pack hash should be 20 bytes long but was %s", (int)hash.length);
            hashStr = BaseEncoding.base16().lowerCase().encode(hash);
        }
        this.handlePushResourcePack(new ClientboundResourcePackPushPacket(id, url, hashStr, force, CraftChatMessage.fromStringOrOptional(prompt, true)), false);
	}

	@Override
	public void removeResourcePack(@NotNull UUID id) {
		if (this.getHandle().connection == null) {
            return;
        }
        this.getHandle().connection.send(new ClientboundResourcePackPopPacket(Optional.of(id)));
	}

	@Override
	public Set<Long> getSentChunkKeys() {
        AsyncCatcher.catchOp("accessing sent chunks");
        return LongSets.EMPTY_SET;
        // return LongSets.unmodifiable((LongSet)this.getHandle().chunkLoader.getSentChunksRaw().clone());
    }

	@Override
	public Set<Chunk> getSentChunks() {
		return null;
	}

	@Override
	public boolean isChunkSent(long chunkKey) {
		// TODO Auto-generated method stub
		return false;
	}
	
	// 1.20.6 API:

	@Override
	public @Nullable InetSocketAddress getHAProxyAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isTransferred() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public @NotNull CompletableFuture<byte[]> retrieveCookie(@NotNull NamespacedKey key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void storeCookie(@NotNull NamespacedKey key, @NotNull byte[] value) {
		// TODO Auto-generated method stub
		
	}

	@Override
    public void transfer(String host, int port) {
        //Preconditions.checkArgument((host != null ? 1 : 0) != 0, (Object)"Host cannot be null");
        //Preconditions.checkState((this.getHandle().transferCookieConnection.getProtocol() == NetworkPhase.CONFIGURATION || this.getHandle().transferCookieConnection.getProtocol() == NetworkPhase.PLAY ? 1 : 0) != 0, (Object)"Can only transfer in CONFIGURATION or PLAY protocol.");
        //this.getHandle().transferCookieConnection.sendPacket(new ServerTransferS2CPacket(host, port));
    }

	@Override
	public <T> void spawnParticle(@NotNull Particle particle, @NotNull Location location, int count, double offsetX,
			double offsetY, double offsetZ, double extra, @Nullable T data, boolean force) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> void spawnParticle(@NotNull Particle particle, double x, double y, double z, int count, double offsetX,
			double offsetY, double offsetZ, double extra, @Nullable T data, boolean force) {
		if (data != null && !particle.getDataType().isInstance(data))
            throw new IllegalArgumentException("data should be " + particle.getDataType() + " got " + data.getClass());
        
        ClientboundLevelParticlesPacket packetplayoutworldparticles = new ClientboundLevelParticlesPacket(CraftParticle.createParticleParam(particle, data), false, force, x, y, z, (float) offsetX, (float) offsetY, (float) offsetZ, (float) extra, count); // Paper - fix x/y/z precision loss

        
        // ParticleS2CPacket packetplayoutworldparticles = new ParticleS2CPacket(CraftParticle.toNMS(particle, data), true, (float) x, (float) y, (float) z, (float) offsetX, (float) offsetY, (float) offsetZ, (float) extra, count);
        getHandle().connection.send(packetplayoutworldparticles);
	}
	
	@Override
    public void sendLinks(ServerLinks links) {
        if (this.getHandle().connection == null) {
            return;
        }
        Preconditions.checkArgument(links != null, "links cannot be null");
        net.minecraft.server.ServerLinks nms = ((CraftServerLinks)links).getServerLinks();
        this.getHandle().connection.send(new ClientboundServerLinksPacket(nms.untrust()));
    }

	@Override
	public void startRiptideAttack(int duration, float attackStrength, @Nullable ItemStack attackItem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendEntityEffect(EntityEffect effect, Entity target) {
        if (this.getHandle().connection == null || !effect.isApplicableTo(target)) {
            return;
        }
        this.getHandle().connection.send(new ClientboundEntityEventPacket(((CraftEntity)target).getHandle(), effect.getData()));
    }
	
	// 1.21.4:

	@Override
	public int getPlayerListOrder() {
		return this.getHandle().getTabListOrder();
	}

	@Override
	public void setPlayerListOrder(int order) {
		/*
		this.getHandle().listOrder = order;
	    // Paper start - Send update packet
	    if (getHandle().networkHandler == null) return; // Updates are possible before the player has fully joined
	    for (ServerPlayerEntity player : server.getHandle().players) {
	        if (player.getBukkitEntity().canSee(this)) {
	            player.networkHandler.sendPacket(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_LIST_ORDER, getHandle()));
	        }
	    }
	    // Paper end - Send update packet
	     */
	}

	@Override
	public @NotNull Collection<EnderPearl> getEnderPearls() {
        return this.getHandle().getEnderPearls().stream().map((e) -> (EnderPearl) (((EntityBridge)e).getBukkitEntity())).collect(Collectors.toList());
	}

	@Override
	public @NotNull Input getCurrentInput() {
        return new CraftInput(this.getHandle().getLastClientInput());
	}
	
	@Override
	public void lookAt(@NotNull Entity entity, @NotNull LookAnchor playerAnchor, @NotNull LookAnchor entityAnchor) {
		this.getHandle().lookAt(toNmsAnchor(playerAnchor), ((CraftEntity)entity).getHandle(), toNmsAnchor(entityAnchor));
	}

	@Override
	public PlayerGiveResult give(Collection<ItemStack> items, boolean dropIfFull) {
		if (items.isEmpty()) return PaperPlayerGiveResult.EMPTY; // Early opt out for empty input.
		
		final ServerPlayer handle = this.getHandle();
        final ImmutableList.Builder<Item> drops = ImmutableList.builder();
        final ImmutableList.Builder<ItemStack> leftovers = ImmutableList.builder();
		
		// Validate all items before attempting to spawn any.
        for (final ItemStack item : items) {
            Preconditions.checkArgument(item != null, "ItemStack cannot be null");
            Preconditions.checkArgument(!item.isEmpty(), "ItemStack cannot be empty");
            Preconditions.checkArgument(item.getAmount() <= item.getMaxStackSize(), "ItemStack amount cannot be greater than its max stack size");
        }
        
        for (final ItemStack item : items) {
        	final net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
            final boolean added = handle.getInventory().add(nmsStack);
        
            if (added && nmsStack.isEmpty()) continue; // Item was fully added, neither a drop nor a leftover is needed.
        
            leftovers.add(CraftItemStack.asBukkitCopy(nmsStack)); // Insert copy to avoid mutation to the dropped item from affecting leftovers
            if (!dropIfFull) continue;
            
            
            final ItemEntity entity = handle.drop(nmsStack, false, true/*, false*/);
            if (entity != null) drops.add((Item) ((EntityBridge)entity).getBukkitEntity());
        }
		
        handle.containerMenu.broadcastChanges();

        return new PaperPlayerGiveResult(leftovers.build(), drops.build());
	}

	@Override
	public int getDeathScreenScore() {
		return getHandle().getScore();
	}

	@Override
	public void setDeathScreenScore(int score) {
		getHandle().setScore(score);
	}

	@Override
	public void openVirtualSign(Position block, Side side) {
        if (this.getHandle().connection == null) {
            return;
        }
        this.getHandle().connection.send(new net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket(MCUtil.toBlockPos(block), side == Side.FRONT));
    }

	@Override
	public PlayerGameConnection getConnection() {
		return ( (IMixinPlayNetworkHandler) this.getHandle().connection).cardboard$playerGameConnection();
	}
	
	@Override
	public org.bukkit.entity.Entity getShoulderEntityRight() {
		if (!this.getHandle().getShoulderEntityRight().isEmpty()) {
			org.bukkit.entity.Entity var2;
			try (ProblemReporter.ScopedCollector scopedCollector = new ProblemReporter.ScopedCollector(this.getHandle().problemPath(), LOGGER)) {
				var2 = net.minecraft.world.entity.EntityType.create(
	              TagValueInput.create(
	                 scopedCollector.forChild(() -> ".shoulder"), this.getHandle().registryAccess(), this.getHandle().getShoulderEntityRight()
	              ),
	              this.getHandle().level(),
	              EntitySpawnReason.LOAD
	           )
	           .map(EntityBridge::getBukkitEntity)
	           .orElse(null);
	     }

	     return var2;
	  } else {
	     return null;
	  }
	}
	
	@Override
	public org.bukkit.entity.Entity getShoulderEntityLeft() {
		if (!this.getHandle().getShoulderEntityLeft().isEmpty()) {
			org.bukkit.entity.Entity var2;
			try (ProblemReporter.ScopedCollector scopedCollector = new ProblemReporter.ScopedCollector(this.getHandle().problemPath(), LOGGER)) {
				var2 = net.minecraft.world.entity.EntityType.create(
						TagValueInput.create(scopedCollector.forChild(() -> ".shoulder"), this.getHandle().registryAccess(), this.getHandle().getShoulderEntityLeft()),
						this.getHandle().level(),
						EntitySpawnReason.LOAD
					)
					.map(EntityBridge::getBukkitEntity)
					.orElse(null);
			}

			return var2;
		} else {
			return null;
		}
	}
	
	// TODO: Support player list header/footer
	private Component playerListHeader;
	private Component playerListFooter;
	
	@Override
	public void sendPlayerListHeader(Component header) {
		this.playerListHeader = header;
		this.adventure$sendPlayerListHeaderAndFooter();
	}

	@Override
	public void sendPlayerListFooter(Component footer) {
		this.playerListFooter = footer;
		this.adventure$sendPlayerListHeaderAndFooter();
	}

	@Override
	public void sendPlayerListHeaderAndFooter(Component header, Component footer) {
		this.playerListHeader = header;
		this.playerListFooter = footer;
		this.adventure$sendPlayerListHeaderAndFooter();
	}

	private void adventure$sendPlayerListHeaderAndFooter() {
		ServerGamePacketListenerImpl connection = this.getHandle().connection;
		if (connection != null) {
			ClientboundTabListPacket packet = new ClientboundTabListPacket(
					PaperAdventure.asVanillaNullToEmpty(this.playerListHeader), PaperAdventure.asVanillaNullToEmpty(this.playerListFooter)
					);
			connection.send(packet);
		}
	}
}