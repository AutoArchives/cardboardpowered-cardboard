package org.cardboardpowered.impl;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;
import org.bukkit.BanEntry;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.InetAddresses;
import com.mojang.authlib.GameProfile;

import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.ServerConfigEntry;

public class ProfileBanList implements org.bukkit.BanList {

    private final BannedPlayerList list;

    public ProfileBanList(BannedPlayerList list) {
        this.list = list;
    }

    @Override
    public org.bukkit.BanEntry getBanEntry(String target) {
        GameProfile profile = getProfile(target);
        if (profile == null) return null;

        BannedPlayerEntry entry = list.get(profile);
        return (entry == null) ? null : new ProfileBanEntry(profile, entry, list);
    }

    @Override
    public org.bukkit.BanEntry addBan(String target, String reason, Date expires, String source) {
        GameProfile profile = getProfile(target);
        if (profile == null) return null;

        BannedPlayerEntry entry = new BannedPlayerEntry(profile, new Date(), StringUtils.isBlank(source) ? null : source, expires, StringUtils.isBlank(reason) ? null : reason);
        list.add(entry);

        try {
            list.save();
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to save banned players, " + ex.getMessage());
        }

        return new ProfileBanEntry(profile, entry, list);
    }

    @Override
    public Set<org.bukkit.BanEntry> getBanEntries() {
        ImmutableSet.Builder<org.bukkit.BanEntry> builder = ImmutableSet.builder();
        for (ServerConfigEntry<?> entry : list.values())
            builder.add(new ProfileBanEntry((GameProfile) entry.getKey(), (BannedPlayerEntry) entry, list));

        return builder.build();
    }

    @Override
    public boolean isBanned(String target) {
        GameProfile profile = getProfile(target);
        return (profile == null) ? null : list.contains(profile);
    }

    @Override
    public void pardon(String target) {
        list.remove(getProfile(target));
    }

    private GameProfile getProfile(String target) {
        UUID uuid = null;
        try {
            uuid = UUID.fromString(target);
        } catch (IllegalArgumentException ex) {/**/}
        return (uuid != null) ? CraftServer.getUC().card_getByUuid(uuid).get()
                : CraftServer.getUC().card_findByName(target).get();
    }

    public static class ProfileBanEntry implements org.bukkit.BanEntry {
        private final BannedPlayerList list;
        private final GameProfile profile;
        private Date created;
        private String source;
        private Date expiration;
        private String reason;

        public ProfileBanEntry(GameProfile profile, BannedPlayerEntry entry, BannedPlayerList list) {
            this.list = list;
            this.profile = profile;
            this.created = null; // TODO Bukkit4Fabric
            this.source = entry.getSource();
            this.expiration = entry.getExpiryDate() != null ? new Date(entry.getExpiryDate().getTime()) : null;
            this.reason = entry.getReason();
        }

        @Override
        public String getTarget() {
            return this.profile.getName();
        }

        @Override
        public Date getCreated() {
            return this.created == null ? null : (Date) this.created.clone();
        }

        @Override
        public void setCreated(Date created) {
            this.created = created;
        }

        @Override
        public String getSource() {
            return this.source;
        }

        @Override
        public void setSource(String source) {
            this.source = source;
        }

        @Override
        public Date getExpiration() {
            return this.expiration == null ? null : (Date) this.expiration.clone();
        }

        @SuppressWarnings("deprecation")
        @Override
        public void setExpiration(Date expiration) {
            if (expiration != null && expiration.getTime() == new Date(0, 0, 0, 0, 0, 0).getTime()) expiration = null; // Forces "forever"
            this.expiration = expiration;
        }

        @Override
        public String getReason() {
            return this.reason;
        }

        @Override
        public void setReason(String reason) {
            this.reason = reason;
        }

        @Override
        public void save() {
            BannedPlayerEntry entry = new BannedPlayerEntry(profile, this.created, this.source, this.expiration, this.reason);
            this.list.add(entry);
            try {
                this.list.save();
            } catch (IOException ex) {
                Bukkit.getLogger().log(Level.SEVERE, "Failed to save banned-players.json, " + ex.getMessage());
            }
        }

        public PlayerProfile getBanTarget() {
            return new CraftPlayerProfile(this.profile);
        }

		public void remove() {
			this.list.remove(this.profile);
		}
    }

	@Override
	public @Nullable BanEntry getBanEntry(@NotNull Object target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @Nullable BanEntry addBan(@NotNull Object target, @Nullable String reason, @Nullable Date expires,
			@Nullable String source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @Nullable BanEntry addBan(@NotNull Object target, @Nullable String reason, @Nullable Instant expires,
			@Nullable String source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @Nullable BanEntry addBan(@NotNull Object target, @Nullable String reason, @Nullable Duration duration,
			@Nullable String source) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull Set getEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isBanned(@NotNull Object target) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void pardon(@NotNull Object target) {
		// TODO Auto-generated method stub
		
	}

}