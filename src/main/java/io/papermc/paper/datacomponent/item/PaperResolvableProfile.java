package io.papermc.paper.datacomponent.item;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.destroystokyo.paper.profile.SharedPlayerProfile;
import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;

import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.profile.MutablePropertyMap;
import io.papermc.paper.util.MCUtil;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.object.PlayerHeadObjectContents;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.entity.player.SkinTextures.SkinOverride;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.StringHelper;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.profile.PlayerTextures.SkinModel;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

public record PaperResolvableProfile(
    net.minecraft.component.type.ProfileComponent impl
) implements ResolvableProfile, Handleable<net.minecraft.component.type.ProfileComponent> {

	static PaperResolvableProfile toApi(PlayerProfile profile) {
		return new PaperResolvableProfile(((SharedPlayerProfile)profile).buildResolvableProfile());
	}
	
	/*
    static PaperResolvableProfile toApi(final PlayerProfile profile) {
        return new PaperResolvableProfile(new net.minecraft.component.type.ProfileComponent(CraftPlayerProfile.asAuthlibCopy(profile)));
    }
    */

    @Override
    public net.minecraft.component.type.ProfileComponent getHandle() {
        return this.impl;
    }

    @Override
    public @Nullable UUID uuid() {
    	return this.impl.get().map(GameProfile::id, p -> p.id().orElse(null));
    }

    @Override
    public @Nullable String name() {
    	return this.impl.get().map(GameProfile::name, p -> p.name().orElse(null));
    }

    @Unmodifiable
    public Collection<ProfileProperty> properties() {
       return MCUtil.transformUnmodifiable(
          this.impl.get().map(GameProfile::properties, ProfileComponent.Data::properties).values(),
          input -> new ProfileProperty(input.name(), input.value(), input.signature())
       );
    }

    @Override
    public CompletableFuture<PlayerProfile> resolve() {
    	return this.impl.resolve(CraftServer.server.getApiServices().profileResolver()).thenApply(CraftPlayerProfile::asBukkitCopy);
    }

    static final class BuilderImpl implements ResolvableProfile.Builder {

        private final PropertyMap propertyMap = new MutablePropertyMap();
        private @Nullable String name;
        private @Nullable UUID uuid;
        private PaperResolvableProfile.PaperSkinPatch skinPatch = (PaperResolvableProfile.PaperSkinPatch)SkinPatch.empty();

        @Override
        public ResolvableProfile.Builder name(final @Nullable String name) {
            if (name != null) {
                Preconditions.checkArgument(name.length() <= 16, "name cannot be more than 16 characters, was %s", name.length());
                Preconditions.checkArgument(StringHelper.isValidPlayerName(name), "name cannot include invalid characters, was %s", name);
            }
            this.name = name;
            return this;
        }

        @Override
        public ResolvableProfile.Builder uuid(final @Nullable UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        @Override
        public ResolvableProfile.Builder addProperty(final ProfileProperty property) {
            // ProfileProperty constructor already has specific validations
            final Property newProperty = new Property(property.getName(), property.getValue(), property.getSignature());
            if (!this.propertyMap.containsEntry(property.getName(), newProperty)) { // underlying map is a multimap that doesn't allow duplicate key-value pair
                final int newSize = this.propertyMap.size() + 1;
                Preconditions.checkArgument(newSize <= 16, "Cannot have more than 16 properties, was %s", newSize);
            }

            this.propertyMap.put(property.getName(), newProperty);
            return this;
        }

        @Override
        public ResolvableProfile.Builder addProperties(final Collection<ProfileProperty> properties) {
        	properties.forEach(this::addProperty);
        	return this;
        }

        public io.papermc.paper.datacomponent.item.ResolvableProfile.Builder skinPatch(SkinPatch patch) {
        	Preconditions.checkArgument(patch != null, "patch cannot be null");
        	this.skinPatch = (PaperResolvableProfile.PaperSkinPatch)patch;
        	return this;
        }

        @Override
        public io.papermc.paper.datacomponent.item.ResolvableProfile.Builder skinPatch(Consumer<SkinPatchBuilder> configure) {
        	Preconditions.checkArgument(configure != null, "configure cannot be null");
        	SkinPatchBuilder builder = SkinPatch.skinPatch();
        	builder.body(this.skinPatch.body());
        	builder.cape(this.skinPatch.cape());
        	builder.elytra(this.skinPatch.elytra());
        	builder.model(this.skinPatch.model());
        	configure.accept(builder);
        	this.skinPatch = (PaperResolvableProfile.PaperSkinPatch)builder.build();
        	return this;
        }

        public ResolvableProfile build() {
        	
        	SkinOverride todoAddThis = SkinOverride.EMPTY; // this.skinPatch.asVanilla()

            return this.propertyMap.isEmpty() && this.uuid == null != (this.name == null)
               ? new PaperResolvableProfile(
                  new ProfileComponent.Dynamic(this.name != null ? Either.left(this.name) : Either.right(this.uuid), todoAddThis)
               )
               : new PaperResolvableProfile(
                  new ProfileComponent.Static(
                     Either.right(new ProfileComponent.Data(Optional.ofNullable(this.name), Optional.ofNullable(this.uuid), new PropertyMap(this.propertyMap))),
                     todoAddThis // this.skinPatch.asVanilla()
                  )
               );
         }

        /*
        @Override
        public ResolvableProfile build() {
            final PropertyMap shallowCopy = new MutablePropertyMap();
            shallowCopy.putAll(this.propertyMap);

            return new PaperResolvableProfile(new net.minecraft.component.type.ProfileComponent(
                Optional.ofNullable(this.name),
                Optional.ofNullable(this.uuid),
                shallowCopy
            ));
        }
        */
    }

    @Override
    public void applySkinToPlayerHeadContents(
    		net.kyori.adventure.text.object.PlayerHeadObjectContents.@NotNull Builder builder) {
    	if (this.dynamic()) {
    		if (this.uuid() != null) {
    			builder.id(this.uuid());
    		} else {
    			builder.name(this.name());
    		}
    	} else {
    		builder.id(this.uuid())
    		.name(this.name())
    		.profileProperties(
    				this.impl
    				.get()
    				.map(GameProfile::properties, ProfileComponent.Data::properties)
    				.values()
    				.stream()
    				.map(prop -> PlayerHeadObjectContents.property(prop.name(), prop.value(), prop.signature()))
    				.toList()
    				)
    		.texture(this.impl.getOverride().body().map(AssetInfo.TextureAssetInfo::id).map(PaperAdventure::asAdventure).orElse(null));
    	}
    }

    @Override
    public boolean dynamic() {
    	return this.impl instanceof ProfileComponent.Dynamic;
    }

    @Override
    public SkinPatch skinPatch() {
    	return PaperResolvableProfile.PaperSkinPatch.asPaper(this.getHandle().getOverride());
    }

    record PaperSkinPatch(@Nullable Key body, @Nullable Key cape, @Nullable Key elytra, @Nullable SkinModel model) implements SkinPatch {
    	static PaperResolvableProfile.PaperSkinPatch asPaper(SkinTextures.SkinOverride patch) {
    		return patch == SkinTextures.SkinOverride.EMPTY
    				? (PaperResolvableProfile.PaperSkinPatch)SkinPatch.empty()
    						: new PaperResolvableProfile.PaperSkinPatch(
    								patch.body().map(AssetInfo.TextureAssetInfo::id).map(PaperAdventure::asAdventure).orElse(null),
    								patch.cape().map(AssetInfo.TextureAssetInfo::id).map(PaperAdventure::asAdventure).orElse(null),
    								patch.elytra().map(AssetInfo.TextureAssetInfo::id).map(PaperAdventure::asAdventure).orElse(null),
    								patch.model().map(m -> m == PlayerSkinType.SLIM ? SkinModel.SLIM : SkinModel.CLASSIC).orElse(null)
    								);
    	}

    	SkinTextures.SkinOverride asVanilla() {
    		return SkinTextures.SkinOverride.create(
    				Optional.ofNullable(this.body).map(key -> new AssetInfo.TextureAssetInfo(PaperAdventure.asVanilla(key))),
    				Optional.ofNullable(this.cape).map(key -> new AssetInfo.TextureAssetInfo(PaperAdventure.asVanilla(key))),
    				Optional.ofNullable(this.elytra).map(key -> new AssetInfo.TextureAssetInfo(PaperAdventure.asVanilla(key))),
    				Optional.ofNullable(this.model).map(m -> m == SkinModel.SLIM ? PlayerSkinType.SLIM : PlayerSkinType.WIDE)
    				);
    	}
    }

    static final class SkinPatchBuilderImpl implements SkinPatchBuilder {
    	@Nullable
    	private Key body;
    	@Nullable
    	private Key cape;
    	@Nullable
    	private Key elytra;
    	@Nullable
    	private SkinModel model;

    	public SkinPatchBuilder body(@Nullable Key body) {
    		this.body = body;
    		return this;
    	}

    	public SkinPatchBuilder cape(@Nullable Key cape) {
    		this.cape = cape;
    		return this;
    	}

    	public SkinPatchBuilder elytra(@Nullable Key elytra) {
    		this.elytra = elytra;
    		return this;
    	}

    	public SkinPatchBuilder model(@Nullable SkinModel model) {
    		this.model = model;
    		return this;
    	}

    	public SkinPatch build() {
    		return (SkinPatch)(this.body == null && this.cape == null && this.elytra == null && this.model == null
    				? SkinPatch.empty()
    						: new PaperResolvableProfile.PaperSkinPatch(this.body, this.cape, this.elytra, this.model));
    	}
    }
}
