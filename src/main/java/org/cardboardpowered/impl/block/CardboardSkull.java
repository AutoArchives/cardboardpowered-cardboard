package org.cardboardpowered.impl.block;

import java.util.Objects;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.cardboardpowered.impl.entity.CraftPlayer;
import org.jetbrains.annotations.Nullable;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;

import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.datacomponent.item.PaperResolvableProfile;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import me.isaiah.common.cmixin.IMixinSkullBlockEntity;
import net.kyori.adventure.text.Component;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@SuppressWarnings("deprecation")
public class CardboardSkull extends CardboardBlockEntityState<SkullBlockEntity> implements Skull {

    private static final int MAX_OWNER_LENGTH = 16;
    private ProfileComponent profile;

    public CardboardSkull(World world, SkullBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CardboardSkull(CardboardSkull state, Location location) {
        super(state, location);
    }
    
    @Override
    public CardboardSkull copy() {
        return new CardboardSkull(this, null);
    }

    @Override
    public CardboardSkull copy(Location location) {
        return new CardboardSkull(this, location);
    }


    @Override
    public void load(SkullBlockEntity skull) {
        super.load(skull);
        
        ProfileComponent owner = skull.getOwner();
        if (null != owner) {
        	this.profile = owner;
        }

        // IMixinSkullBlockEntity ic = ((IMixinSkullBlockEntity)(Object)this);
        // profile = ic.IC$get_game_profile();
    }

    static int getSkullType(SkullType type) {
        switch (type) {
            default:
            case SKELETON:
                return 0;
            case WITHER:
                return 1;
            case ZOMBIE:
                return 2;
            case PLAYER:
                return 3;
            case CREEPER:
                return 4;
            case DRAGON:
                return 5;
        }
    }

    @Override
    public boolean hasOwner() {
        return profile != null;
    }

    @Override
    public String getOwner() {
    	return this.hasOwner() ? this.profile.getName().orElse(null) : null;
    }

    /*
    @Override
    public boolean setOwner(String name) {
        if (name == null || name.length() > MAX_OWNER_LENGTH) return false;

        Optional<GameProfile> profile = CraftServer.getUC().card_findByName(name);
        if (profile.isEmpty()) return false;

        this.profile = profile.get();
        return true;
    }*/
    
    // PlayerConfigEntry.toUncompletedGameProfile
    public static GameProfile PlayerConfigEntry_toUncompletedGameProfile(PlayerConfigEntry thiz) {
        return new GameProfile(thiz.id(), thiz.name());
    }
    
    @Override
    public boolean setOwner(String name) {
        if (name != null && name.length() <= 16) {
           GameProfile profile = CraftServer.INSTANCE.getPaperFilledProfileCache().getIfCached(name);
           if (profile == null) {
              profile = CraftServer.server
                 .getApiServices()
                 .nameToIdCache()
                 .findByName(name)
                 .map(CardboardSkull::PlayerConfigEntry_toUncompletedGameProfile)
                 .orElse(null);
           }

           if (profile == null) {
              return false;
           } else {
              this.profile = ProfileComponent.ofStatic(profile);
              return true;
           }
        } else {
           return false;
        }
     }

    @Override
    public OfflinePlayer getOwningPlayer() {
    	if (this.hasOwner()) {
            GameProfile gameProfile = this.profile.getGameProfile();
            if (Objects.equals(gameProfile.id(), Util.NIL_UUID)) {
               return Bukkit.getOfflinePlayer(gameProfile.id());
            }

            if (!gameProfile.name().isEmpty()) {
               return Bukkit.getOfflinePlayer(gameProfile.name());
            }
         }
        return null;
    }

    @Override
    public void setOwningPlayer(OfflinePlayer player) {
        Preconditions.checkNotNull(player, "player");
        if (player instanceof CraftPlayer craftPlayer) {
           this.profile = ProfileComponent.ofStatic(craftPlayer.getProfile());
        } else {
           this.profile = new ProfileComponent.Dynamic(Either.right(player.getUniqueId()), SkinTextures.SkinOverride.EMPTY);
        }
     }
    
    @Override
    public BlockFace getRotation() {
        BlockData blockData = getBlockData();
        return (blockData instanceof Rotatable) ? ((Rotatable) blockData).getRotation() : ((Directional) blockData).getFacing();
    }

    @Override
    public void setRotation(BlockFace rotation) {
        BlockData blockData = getBlockData();
        if (blockData instanceof Rotatable) ((Rotatable) blockData).setRotation(rotation);
        else ((Directional) blockData).setFacing(rotation);

        setBlockData(blockData);
    }

    @Override
    public SkullType getSkullType() {
        switch (getType()) {
            case SKELETON_SKULL:
            case SKELETON_WALL_SKULL:
                return SkullType.SKELETON;
            case WITHER_SKELETON_SKULL:
            case WITHER_SKELETON_WALL_SKULL:
                return SkullType.WITHER;
            case ZOMBIE_HEAD:
            case ZOMBIE_WALL_HEAD:
                return SkullType.ZOMBIE;
            case PLAYER_HEAD:
            case PLAYER_WALL_HEAD:
                return SkullType.PLAYER;
            case CREEPER_HEAD:
            case CREEPER_WALL_HEAD:
                return SkullType.CREEPER;
            case DRAGON_HEAD:
            case DRAGON_WALL_HEAD:
                return SkullType.DRAGON;
            default:
                throw new IllegalArgumentException("Unknown SkullType for " + getType());
        }
    }

    @Override
    public void setSkullType(SkullType skullType) {
        throw new UnsupportedOperationException("Must change block type");
    }

    @Override
    public void applyTo(SkullBlockEntity skull) {
        super.applyTo(skull);
        if (this.getSkullType() == SkullType.PLAYER) {
            skull.owner = this.hasOwner() ? this.profile : null;
        }
    }

    @Override
    public PlayerProfile getPlayerProfile() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setPlayerProfile(PlayerProfile arg0) {
    	this.profile = CraftPlayerProfile.asResolvableProfileCopy(arg0);
    }

    @Override 
    public PlayerProfile getOwnerProfile() {
        if (!hasOwner()) {
            return null;
        }

        return new CraftPlayerProfile(profile);
    }

	
	@Override
    public void setOwnerProfile(org.bukkit.profile.@Nullable PlayerProfile profile) {
        if (profile == null) {
            this.profile = null;
        } else {
           //  this.profile = CraftPlayerProfile.validateSkullProfile(((CraftPlayerProfile) profile).getGameProfile());
        }
    }

	@Override
    public NamespacedKey getNoteBlockSound() {
        Identifier key = ((SkullBlockEntity)this.getSnapshot()).getNoteBlockSound();
        return key != null ? CraftNamespacedKey.fromMinecraft(key) : null;
    }

	@Override
    public void setNoteBlockSound(@Nullable NamespacedKey namespacedKey) {
        if (namespacedKey == null) {
            ((SkullBlockEntity)this.getSnapshot()).noteBlockSound = null;
            return;
        }
        ((SkullBlockEntity)this.getSnapshot()).noteBlockSound = CraftNamespacedKey.toMinecraft(namespacedKey);
    }

	@Override
	public @Nullable Component customName() {
		// SkullBlockEntity snapshot = getSnapshot();
        // return snapshot.customName == null ? null : PaperAdventure.asAdventure(snapshot.customName);
		return null;
	}

	@Override
	public void customName(@Nullable Component customName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public @Nullable ResolvableProfile getProfile() {
		return this.profile == null ? null : new PaperResolvableProfile(this.profile);
	}

	@Override
	public void setProfile(@Nullable ResolvableProfile profile) {
		if (profile == null) {
			this.profile = null;
		} else {
			this.profile = ((PaperResolvableProfile) profile).getHandle();
		}
	}

}