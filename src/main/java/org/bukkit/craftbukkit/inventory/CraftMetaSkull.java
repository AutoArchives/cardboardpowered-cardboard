package org.bukkit.craftbukkit.inventory;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.DynamicOps;

import me.isaiah.common.ICommonMod;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftMetaItem;
import org.bukkit.craftbukkit.inventory.SerializableMeta;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.meta.SkullMeta;
import org.cardboardpowered.impl.entity.PlayerImpl;
import org.jetbrains.annotations.Nullable;

@DelegateDeserialization(value=SerializableMeta.class)
class CraftMetaSkull
extends CraftMetaItem
implements SkullMeta {
    private static final Set<Material> SKULL_MATERIALS = Sets.newHashSet(
    		new Material[]{
    				Material.CREEPER_HEAD, Material.CREEPER_WALL_HEAD, Material.DRAGON_HEAD,
    				Material.DRAGON_WALL_HEAD, Material.PIGLIN_HEAD, Material.PIGLIN_WALL_HEAD,
    				Material.PLAYER_HEAD, Material.PLAYER_WALL_HEAD, Material.SKELETON_SKULL,
    				Material.SKELETON_WALL_SKULL, Material.WITHER_SKELETON_SKULL, Material.WITHER_SKELETON_WALL_SKULL,
    				Material.ZOMBIE_HEAD, Material.ZOMBIE_WALL_HEAD
    			}
    	);
    static final CraftMetaItem.ItemMetaKeyType<ProfileComponent> SKULL_PROFILE = new CraftMetaItem.ItemMetaKeyType<ProfileComponent>(DataComponentTypes.PROFILE, "SkullProfile");
    static final CraftMetaItem.ItemMetaKey SKULL_OWNER = new CraftMetaItem.ItemMetaKey("skull-owner");
    static final CraftMetaItem.ItemMetaKey BLOCK_ENTITY_TAG = new CraftMetaItem.ItemMetaKey("BlockEntityTag");
    static final CraftMetaItem.ItemMetaKeyType<Identifier> NOTE_BLOCK_SOUND = new CraftMetaItem.ItemMetaKeyType<Identifier>(DataComponentTypes.NOTE_BLOCK_SOUND, "note_block_sound");
    static final int MAX_OWNER_LENGTH = 16;
    private GameProfile profile;
    private Identifier noteBlockSound;

    CraftMetaSkull(CraftMetaItem meta) {
        super(meta);
        if (!(meta instanceof CraftMetaSkull)) {
            return;
        }
        CraftMetaSkull skullMeta = (CraftMetaSkull)meta;
        this.setProfile(skullMeta.profile);
        this.noteBlockSound = skullMeta.noteBlockSound;
    }

    CraftMetaSkull(ComponentChanges tag, Set<DataComponentType<?>> extraHandledDcts) {
        super(tag, extraHandledDcts);
        CraftMetaSkull.getOrEmpty(tag, SKULL_PROFILE).ifPresent(resolvableProfile -> this.setProfile(resolvableProfile.gameProfile()));
        CraftMetaSkull.getOrEmpty(tag, NOTE_BLOCK_SOUND).ifPresent(minecraftKey -> {
            this.noteBlockSound = minecraftKey;
        });
    }

    CraftMetaSkull(Map<String, Object> map) {
        super(map);
        Object object;
        if (this.profile == null) {
            object = map.get(CraftMetaSkull.SKULL_OWNER.BUKKIT);
            if (object instanceof org.bukkit.profile.PlayerProfile) {
                this.setOwnerProfile((org.bukkit.profile.PlayerProfile)object);
            } else {
                this.setOwner(SerializableMeta.getString(map, CraftMetaSkull.SKULL_OWNER.BUKKIT, true));
            }
        }
        if (this.noteBlockSound == null && (object = map.get(CraftMetaSkull.NOTE_BLOCK_SOUND.BUKKIT)) != null) {
            this.setNoteBlockSound(NamespacedKey.fromString((String)object.toString()));
        }
    }

    @Override
    void deserializeInternal(NbtCompound tag, Object context) {
        NbtCompound nbtTagCompound;
        super.deserializeInternal(tag, context);
        if (tag.contains(CraftMetaSkull.SKULL_PROFILE.NBT, 10)) {
            NbtCompound skullTag = tag.getCompound(CraftMetaSkull.SKULL_PROFILE.NBT);
            if (skullTag.contains("Id", 8)) {
                UUID uuid = UUID.fromString(skullTag.getString("Id"));
                skullTag.putUuid("Id", uuid);
            }
            this.setProfile(((ProfileComponent)ProfileComponent.CODEC.parse((DynamicOps)NbtOps.INSTANCE, skullTag).result().get()).gameProfile());
        }
        if (tag.contains(CraftMetaSkull.BLOCK_ENTITY_TAG.NBT, 10) && (nbtTagCompound = tag.getCompound(CraftMetaSkull.BLOCK_ENTITY_TAG.NBT).copy()).contains(CraftMetaSkull.NOTE_BLOCK_SOUND.NBT, 8)) {
            this.noteBlockSound = Identifier.tryParse(nbtTagCompound.getString(CraftMetaSkull.NOTE_BLOCK_SOUND.NBT));
        }
    }

    private void setProfile(GameProfile profile) {
        this.profile = profile;
    }

    @Override
    void applyToItem(CraftMetaItem.Applicator tag) {
        super.applyToItem(tag);
        if (this.profile != null) {
            tag.put(SKULL_PROFILE, new ProfileComponent(this.profile));
            CraftPlayerProfile ownerProfile = new CraftPlayerProfile(this.profile);
            if (ownerProfile.getTextures().isEmpty()) {
                ownerProfile.update().thenAcceptAsync(filledProfile -> {
                    this.setOwnerProfile((org.bukkit.profile.PlayerProfile)filledProfile);
                    tag.skullCallback(this.profile);
                }, (Executor)((CraftServer)Bukkit.getServer()).getServer());
            }
        }
        if (this.noteBlockSound != null) {
            tag.put(NOTE_BLOCK_SOUND, this.noteBlockSound);
        }
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && this.isSkullEmpty();
    }

    boolean isSkullEmpty() {
        return this.profile == null && this.noteBlockSound == null;
    }

    @Override
    boolean applicableTo(Material type) {
        return SKULL_MATERIALS.contains(type);
    }

    @Override
    public CraftMetaSkull clone() {
        return (CraftMetaSkull)super.clone();
    }

    public boolean hasOwner() {
        return this.profile != null && !this.profile.getName().isEmpty();
    }

    public String getOwner() {
        return this.hasOwner() ? this.profile.getName() : null;
    }

    public void setPlayerProfile(@Nullable PlayerProfile profile) {
        this.setProfile(profile == null ? null : com.destroystokyo.paper.profile.CraftPlayerProfile.asAuthlibCopy(profile));
    }

    @Nullable
    public PlayerProfile getPlayerProfile() {
        return this.profile != null ? com.destroystokyo.paper.profile.CraftPlayerProfile.asBukkitCopy(this.profile) : null;
    }

    public OfflinePlayer getOwningPlayer() {
        if (this.hasOwner()) {
            if (!this.profile.getId().equals(Util.NIL_UUID)) {
                return Bukkit.getOfflinePlayer((UUID)this.profile.getId());
            }
            if (!this.profile.getName().isEmpty()) {
                return Bukkit.getOfflinePlayer((String)this.profile.getName());
            }
        }
        return null;
    }

    public boolean setOwner(String name) {
        if (name != null && name.length() > 16) {
            return false;
        }
        if (name == null) {
            this.setProfile(null);
        } else {
            GameProfile newProfile = null;
            ServerPlayerEntity player = ICommonMod.getIServer().getMinecraft().getPlayerManager().getPlayer(name);
            if (player != null) {
                newProfile = player.getGameProfile();
            }
            if (newProfile == null) {
                newProfile = new GameProfile(Util.NIL_UUID, name);
            }
            this.setProfile(newProfile);
        }
        return true;
    }

    public boolean setOwningPlayer(OfflinePlayer owner) {
        if (owner == null) {
            this.setProfile(null);
        } else if (owner instanceof PlayerImpl) {
            this.setProfile(((PlayerImpl)owner).getProfile());
        } else {
            this.setProfile(new GameProfile(owner.getUniqueId(), owner.getName()));
        }
        return true;
    }

    @Deprecated
    public org.bukkit.profile.PlayerProfile getOwnerProfile() {
        if (!this.hasOwner()) {
            return null;
        }
        return new CraftPlayerProfile(this.profile);
    }

    @Deprecated
    public void setOwnerProfile(org.bukkit.profile.PlayerProfile profile) {
        if (profile == null) {
            this.setProfile(null);
        } else {
        	
        	CraftPlayerProfile prof = (CraftPlayerProfile) profile;
        	
        	prof.buildGameProfile();
        	
            this.setProfile(CraftPlayerProfile.validateSkullProfile(((CraftPlayerProfile)profile).buildGameProfile()));
        }
    }

    public void setNoteBlockSound(NamespacedKey noteBlockSound) {
        this.noteBlockSound = noteBlockSound == null ? null : CraftNamespacedKey.toMinecraft(noteBlockSound);
    }

    public NamespacedKey getNoteBlockSound() {
        return this.noteBlockSound == null ? null : CraftNamespacedKey.fromMinecraft(this.noteBlockSound);
    }

    @Override
    int applyHash() {
        int original;
        int hash = original = super.applyHash();
        if (this.hasOwner()) {
            hash = 61 * hash + this.profile.hashCode();
        }
        if (this.noteBlockSound != null) {
            hash = 61 * hash + this.noteBlockSound.hashCode();
        }
        return original != hash ? CraftMetaSkull.class.hashCode() ^ hash : hash;
    }

    @Override
    boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaSkull) {
            CraftMetaSkull that = (CraftMetaSkull)meta;
            return (this.profile != null ? that.profile != null && this.profile.equals(that.profile) && this.profile.getProperties().equals(that.profile.getProperties()) : that.profile == null) && Objects.equals(this.noteBlockSound, that.noteBlockSound);
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaSkull || this.isSkullEmpty());
    }

    @Override
    ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
        super.serialize(builder);
        if (this.profile != null) {
            return builder.put(CraftMetaSkull.SKULL_OWNER.BUKKIT, new com.destroystokyo.paper.profile.CraftPlayerProfile(this.profile));
        }
        NamespacedKey namespacedKeyNB = this.getNoteBlockSound();
        if (namespacedKeyNB != null) {
            return builder.put(CraftMetaSkull.NOTE_BLOCK_SOUND.BUKKIT, namespacedKeyNB.toString());
        }
        return builder;
    }
}

