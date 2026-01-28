package org.bukkit.craftbukkit;

import org.cardboardpowered.CardboardMod;
import org.cardboardpowered.interfaces.IMixinMinecraftServer;
import org.cardboardpowered.interfaces.IMixinWorldSaveHandler;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import io.papermc.paper.persistence.PaperPersistentDataContainerView;
import io.papermc.paper.persistence.PersistentDataContainerView;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.UserWhiteListEntry;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.craftbukkit.entity.memory.CraftMemoryMapper;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.profile.PlayerProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SerializableAs("Player")
public class CraftOfflinePlayer implements OfflinePlayer, ConfigurationSerializable {

	private final NameAndId nameAndId;
	
    // private final GameProfile profile;
    private final CraftServer server;
    private final PlayerDataStorage storage;

	protected CraftOfflinePlayer(CraftServer server, NameAndId nameAndId) {
		this.server = server;
		this.nameAndId = nameAndId;
		this.storage = ((IMixinMinecraftServer)server.getServer()).getSaveHandler_BF();
	}

	/*
    protected CraftOfflinePlayer(CraftServer server, GameProfile profile) {
        this.server = server;
        this.profile = profile;
        this.storage = ((IMixinMinecraftServer)server.getServer()).getSaveHandler_BF();
    }

    public GameProfile getProfile() {
        return profile;
    }
    */

    @Override
    public boolean isOnline() {
        // System.out.println("isOnline: " + profile.toString() + ", " + (getPlayer() != null));
        return getPlayer() != null;
    }

    @Override
    public String getName() {
        Player player = this.getPlayer();
        if (player != null) {
           return player.getName();
        } else if (!this.nameAndId.name().isEmpty()) {
           return this.nameAndId.name();
        } else {
           CompoundTag data = this.getBukkitData();
           return data != null ? data.getString("lastKnownName").orElse(null) : null;
        }
     }

    @Override
    public UUID getUniqueId() {
    	return this.nameAndId.id();
    }

    public Server getServer() {
        return server;
    }

    @Override
    public boolean isOp() {
        return server.getHandle().isOp(this.nameAndId);
    }

    @Override
    public void setOp(boolean value) {
        if (value == isOp()) return;

        if (value) server.getHandle().op(this.nameAndId);
        else server.getHandle().deop(this.nameAndId);
    }

    @Override
    public boolean isBanned() {
        return (getName() == null) ? false : server.getBanList(BanList.Type.NAME).isBanned(getName());
    }

    public void setBanned(boolean value) {
        if (getName() == null) {
            return;
        }

        if (value) {
        	server.getBanList(BanList.Type.PROFILE).addBan(this.getPlayerProfile(), null, (Date)null, null);
        } else {
            server.getBanList(BanList.Type.PROFILE).pardon(this.getPlayerProfile());
        }
    }

    @Override
    public boolean isWhitelisted() {
        return server.getHandle().getWhiteList().isWhiteListed(this.nameAndId);
    }

    @Override
    public void setWhitelisted(boolean value) {
        if (value) {
            server.getHandle().getWhiteList().add(new UserWhiteListEntry(this.nameAndId));
        } else {
            server.getHandle().getWhiteList().remove(this.nameAndId);
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();

        result.put("UUID", nameAndId.id().toString());

        return result;
    }

    public static OfflinePlayer deserialize(Map<String, Object> args) {
        // Backwards comparability
        if (args.get("name") != null)
            return Bukkit.getServer().getOfflinePlayer((String) args.get("name"));
        return Bukkit.getServer().getOfflinePlayer(UUID.fromString((String) args.get("UUID")));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[UUID=" + nameAndId.id() + "]";
    }

    @Override
    public Player getPlayer() {
        return server.getPlayer(getUniqueId());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof OfflinePlayer)) {
            return false;
        }

        OfflinePlayer other = (OfflinePlayer) obj;
        if ((this.getUniqueId() == null) || (other.getUniqueId() == null)) {
            return false;
        }

        return this.getUniqueId().equals(other.getUniqueId());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.getUniqueId() != null ? this.getUniqueId().hashCode() : 0);
        return hash;
    }

    private CompoundTag getData() {
        return ((IMixinWorldSaveHandler)storage).getPlayerData(getUniqueId().toString());
    }

    private CompoundTag getBukkitData() {
        CompoundTag result = getData();

        if (result != null) {
            result = result.getCompound("bukkit").orElse(null);
        }

        return result;
    }

    private File getDataFile() {
        return new File(storage.playerDir, getUniqueId() + ".dat");
    }

    @Override
    public long getFirstPlayed() {
        Player player = getPlayer();
        if (player != null) return player.getFirstPlayed();

        CompoundTag data = getBukkitData();

        if (data != null) {
            return data.getLong("firstPlayed").orElseGet(() -> {
                File file = this.getDataFile();
                return file.lastModified();
            });
        } else {
            return 0;
        }
    }

    @Override
    public long getLastPlayed() {
        Player player = getPlayer();
        if (player != null) return player.getLastPlayed();

        CompoundTag data = getBukkitData();
        if (data != null) {
            return data.getLong("lastPlayed").orElseGet(() -> {
                File file = this.getDataFile();
                return file.lastModified();
            });
        } else {
            return 0;
        }
    }

    @Override
    public boolean hasPlayedBefore() {
        return getData() != null;
    }

    @Override
    public Location getBedSpawnLocation() {
        CompoundTag data = getData();
        if (data == null) return null;

        if (data.contains("SpawnX") && data.contains("SpawnY") && data.contains("SpawnZ")) {
            String spawnWorld = data.getString("SpawnWorld").orElse(null);
            if (spawnWorld.equals(""))
                spawnWorld = server.getWorlds().get(0).getName();
            
            int x = data.getInt("SpawnX").orElse(null);
            int y = data.getInt("SpawnY").orElse(null);
            int z = data.getInt("SpawnZ").orElse(null);
            
            return new Location(server.getWorld(spawnWorld), x, y, z);
        }
        return null;
    }

    public void setMetadata(String metadataKey, MetadataValue metadataValue) {
        // TODO auto-generated method stub
    }

    public List<MetadataValue> getMetadata(String metadataKey) {
        // TODO auto-generated method stub
        return null;
    }

    public boolean hasMetadata(String metadataKey) {
        // TODO auto-generated method stub
        return false;
    }

    public void removeMetadata(String metadataKey, Plugin plugin) {
        // TODO auto-generated method stub
    }

    @Override
    public void incrementStatistic(Statistic statistic) {
        if (isOnline()) {
            getPlayer().incrementStatistic(statistic);
        } else {
            // TODO auto-generated method stub
        }
    }

    @Override
    public void decrementStatistic(Statistic statistic) {
        if (isOnline()) {
            getPlayer().decrementStatistic(statistic);
        } else {
            // TODO auto-generated method stub
        }
    }

    @Override
    public int getStatistic(Statistic statistic) {
        if (isOnline()) {
            return getPlayer().getStatistic(statistic);
        } else {
            return 0;    // TODO auto-generated method stub
        }
    }

    @Override
    public void incrementStatistic(Statistic statistic, int amount) {
        if (isOnline()) {
            getPlayer().incrementStatistic(statistic, amount);
        } else {
            // TODO auto-generated method stub
        }
    }

    @Override
    public void decrementStatistic(Statistic statistic, int amount) {
        if (isOnline()) {
            getPlayer().decrementStatistic(statistic, amount);
        } else {
            // TODO auto-generated method stub
        }
    }

    @Override
    public void setStatistic(Statistic statistic, int newValue) {
        if (isOnline()) {
            getPlayer().setStatistic(statistic, newValue);
        } else {
            // TODO auto-generated method stub
        }
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material) {
        if (isOnline()) {
            getPlayer().incrementStatistic(statistic, material);
        } else {
            // TODO auto-generated method stub
        }
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material) {
        if (isOnline()) {
            getPlayer().decrementStatistic(statistic, material);
        } else {
            // TODO auto-generated method stub
        }
    }

    @Override
    public int getStatistic(Statistic statistic, Material material) {
        if (isOnline()) {
            return getPlayer().getStatistic(statistic, material);
        } else {
            return 0;     // TODO auto-generated method stub
        }
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material, int amount) {
        if (isOnline()) {
            getPlayer().incrementStatistic(statistic, material, amount);
        } else {
            // TODO auto-generated method stub
        }
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material, int amount) {
        if (isOnline()) {
            getPlayer().decrementStatistic(statistic, material, amount);
        } else {
            // TODO auto-generated method stub
        }
    }

    @Override
    public void setStatistic(Statistic statistic, Material material, int newValue) {
        if (isOnline()) {
            getPlayer().setStatistic(statistic, material, newValue);
        } else {
            // TODO auto-generated method stub
        }
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType) {
        if (isOnline()) {
            getPlayer().incrementStatistic(statistic, entityType);
        } else {
            // TODO auto-generated method stub
        }
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType) {
        if (isOnline()) {
            getPlayer().decrementStatistic(statistic, entityType);
        } else {
            // TODO auto-generated method stub
        }
    }

    @Override
    public int getStatistic(Statistic statistic, EntityType entityType) {
        if (isOnline()) {
            return getPlayer().getStatistic(statistic, entityType);
        } else {
            // TODO auto-generated method stub
            return 0;
        }
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType, int amount) {
        if (isOnline()) {
            getPlayer().incrementStatistic(statistic, entityType, amount);
        } else {
            // TODO auto-generated method stub
        }
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType, int amount) {
        if (isOnline()) {
            getPlayer().decrementStatistic(statistic, entityType, amount);
        } else {
            // TODO auto-generated method stub
        }
    }

    @Override
    public void setStatistic(Statistic statistic, EntityType entityType, int newValue) {
        // TODO auto-generated method stub
    }

    @Override
    public long getLastLogin() {
        Player player = this.getPlayer();
        if (player != null) {
            return player.getLastLogin();
        }
        CompoundTag data = this.getPaperData();
        if (data != null) {
            return data.getLong("LastLogin").orElseGet(() -> {
                // if the player file cannot provide accurate data, this is probably the closest we can approximate
                File file = getDataFile();
                return file.lastModified();
            });
        } else {
            return 0;
        }
    }

    @Override
    public long getLastSeen() {
        Player player = this.getPlayer();
        if (player != null) {
            return player.getLastSeen();
        }
        CompoundTag data = this.getPaperData();
        if (data != null) {
            return data.getLong("LastSeen").orElseGet(() -> {
                // if the player file cannot provide accurate data, this is probably the closest we can approximate
                File file = getDataFile();
                return file.lastModified();
            });
        } else {
            return 0;
        }
    }
    
    private CompoundTag getPaperData() {
        CompoundTag result = this.getData();
        if (result != null) {
            result = result.getCompound("Paper").orElse(null);
        }

        return result;
    }

	// @Override
	public @NotNull com.destroystokyo.paper.profile.@NotNull PlayerProfile getPlayerProfile() {
		return CraftPlayerProfile.asBukkitCopy(this.nameAndId_toUncompletedGameProfile());
	}
	
	// TODO: move to nameAndId
	public GameProfile nameAndId_toUncompletedGameProfile() {
		return new GameProfile(this.nameAndId.id(), this.nameAndId.name());
	}

	@Override
	public @Nullable Location getLastDeathLocation() {
        /*
		if (this.getData().contains("LastDeathLocation", 10)) {
            return GlobalPos.CODEC.parse(NbtOps.INSTANCE, this.getData().get("LastDeathLocation")).result().map(CraftMemoryMapper::fromNms).orElse(null);
        }
        return null;
        */
        
        CompoundTag data = this.getData();
        
        if (data == null) {
            return null;
        }

        return data.read("LastDeathLocation", GlobalPos.CODEC).map(CraftLocation::fromGlobalPos).orElse(null);
	}

	@Override
	public boolean isConnected() {
		return false;
	}

	@Override
	public <E extends BanEntry<? super com.destroystokyo.paper.profile.PlayerProfile>> @Nullable E ban(
			@Nullable String reason, @Nullable Date expires, @Nullable String source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E extends BanEntry<? super com.destroystokyo.paper.profile.PlayerProfile>> @Nullable E ban(
			@Nullable String reason, @Nullable Instant expires, @Nullable String source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <E extends BanEntry<? super com.destroystokyo.paper.profile.PlayerProfile>> @Nullable E ban(
			@Nullable String reason, @Nullable Duration duration, @Nullable String source) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// 1.20.4 API:

	@Override
	public @Nullable Location getRespawnLocation() {
        /*
		NbtCompound data = this.getData();
        if (data == null) {
            return null;
        }
        if (data.contains("SpawnX") && data.contains("SpawnY") && data.contains("SpawnZ")) {
            float respawnAngle = data.getFloat("SpawnAngle");
            World spawnWorld = this.server.getWorld(data.getString("SpawnWorld"));
            if (data.contains("SpawnDimension")) {
                DataResult< RegistryKey<net.minecraft.world.World> > result =
                		net.minecraft.world.World.CODEC.parse((DynamicOps)NbtOps.INSTANCE, data.get("SpawnDimension"));
                RegistryKey<net.minecraft.world.World> levelKey =
                		result.resultOrPartial(arg_0 -> (CardboardMod.LOGGER).log(Level.FINE, "", arg_0)).orElse(net.minecraft.world.World.OVERWORLD);
                ServerWorld level = this.server.console.getWorld(levelKey);
                World world = spawnWorld = level != null ? level.getWorld() : spawnWorld;
            }
            if (spawnWorld == null) {
                return null;
            }
            return new Location(spawnWorld, (double)data.getInt("SpawnX"), (double)data.getInt("SpawnY"), (double)data.getInt("SpawnZ"), respawnAngle, 0.0f);
        }
        return null;
        */
		return getRespawnLocation(false);
	}
	
	public Location getRespawnLocation(final boolean loadLocationAdValidate) {
        final CompoundTag data = this.getData();
        if (data == null) return null;

        final ServerPlayer.RespawnConfig respawnConfig = data.read("respawn", ServerPlayer.RespawnConfig.CODEC).orElse(null);
        if (respawnConfig == null) return null;

        final ServerLevel level = this.server.console.getLevel(respawnConfig.respawnData().dimension());
        if (level == null) return null;

        //if (!loadLocationAndValidate) {
            return CraftLocation.toBukkit(respawnConfig.respawnData().pos(), level.getWorld(), respawnConfig.respawnData().yaw(), 0);
        //}

            /*
        return ServerPlayerEntity.findRespawnPosition(level, respawnConfig, false)
            .map(resolvedPos -> CraftLocation.toBukkit(resolvedPos.pos(), level.getWorld(), resolvedPos.yaw(), 0))
            .orElse(null);
        */
    }

	@Override
	public Location getLocation() {
        CompoundTag data = this.getData();
        if (data == null) {
            return null;
        }

        Vec3 pos = data.read("Pos", Vec3.CODEC).orElse(null);
        Vec2 rot = data.read("Rotation", Vec2.CODEC).orElse(null);
        if (pos != null && rot != null) {
            Long msb = data.getLong("WorldUUIDMost").orElse(null);
            Long lsb = data.getLong("WorldUUIDLeast").orElse(null);
            World world = msb != null && lsb != null ? this.server.getWorld(new UUID(msb, lsb)) : null;

            return new Location(
                world,
                pos.x(), pos.y(), pos.z(),
                rot.x, rot.y
            );
        }

        return null;
    }

	@Override
	public @NotNull PersistentDataContainerView getPersistentDataContainer() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	// Paper start - Add Offline PDC API
	private static final org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry DATA_TYPE_REGISTRY = new org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry();
	private io.papermc.paper.persistence.@org.checkerframework.checker.nullness.qual.MonotonicNonNull PersistentDataContainerView persistentDataContainerView;

	@Override
	public io.papermc.paper.persistence.PersistentDataContainerView getPersistentDataContainer() {
	    if (this.persistentDataContainerView == null) {
	        this.persistentDataContainerView = new io.papermc.paper.persistence.PaperPersistentDataContainerView(DATA_TYPE_REGISTRY) {

	            private CompoundTag getPersistentTag() {
	                return net.minecraft.Optionull.map(CraftOfflinePlayer.this.getData(), data -> data.getCompound("BukkitValues"));
	            }

	            @Override
	            public CompoundTag toTagCompound() {
	                return java.util.Objects.requireNonNullElseGet(this.getPersistentTag(), CompoundTag::new);
	            }

	            @Override
	            public net.minecraft.nbt.Tag getTag(String key) {
	                return net.minecraft.Optionull.map(this.getPersistentTag(), tag -> tag.get(key));
	            }
	        };
	    }
	    return this.persistentDataContainerView;
	}
	// Paper end - Add Offline PDC API
	*/

}