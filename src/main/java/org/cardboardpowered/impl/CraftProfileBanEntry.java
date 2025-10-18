package org.cardboardpowered.impl;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import java.time.Instant;
import java.util.Date;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.BannedPlayerList;
import net.minecraft.server.PlayerConfigEntry;
import org.bukkit.BanEntry;

public final class CraftProfileBanEntry implements BanEntry<PlayerProfile> {
   private static final Date minorDate = Date.from(Instant.parse("1899-12-31T04:00:00Z"));
   private final BannedPlayerList list;
   private final PlayerConfigEntry profile;
   private Date created;
   private String source;
   private Date expiration;
   private String reason;

   public CraftProfileBanEntry(PlayerConfigEntry profile, BannedPlayerEntry entry, BannedPlayerList list) {
      this.list = list;
      this.profile = profile;
      this.created = entry.getCreationDate() != null ? new Date(entry.getCreationDate().getTime()) : null;
      this.source = entry.getSource();
      this.expiration = entry.getExpiryDate() != null ? new Date(entry.getExpiryDate().getTime()) : null;
      this.reason = entry.getReason();
   }

   public String getTarget() {
      return this.profile.name();
   }

   public PlayerProfile getBanTarget() {
      return new CraftPlayerProfile(this.profile);
   }

   public Date getCreated() {
      return this.created == null ? null : (Date)this.created.clone();
   }

   public void setCreated(Date created) {
      this.created = created;
   }

   public String getSource() {
      return this.source;
   }

   public void setSource(String source) {
      this.source = source;
   }

   public Date getExpiration() {
      return this.expiration == null ? null : (Date)this.expiration.clone();
   }

   public void setExpiration(Date expiration) {
      if (expiration != null && expiration.getTime() == minorDate.getTime()) {
         expiration = null;
      }

      this.expiration = expiration;
   }

   public String getReason() {
      return this.reason;
   }

   public void setReason(String reason) {
      this.reason = reason;
   }

   public void save() {
      BannedPlayerEntry entry = new BannedPlayerEntry(this.profile, this.created, this.source, this.expiration, this.reason);
      this.list.add(entry);
   }

   public void remove() {
      this.list.remove(this.profile);
   }
}
