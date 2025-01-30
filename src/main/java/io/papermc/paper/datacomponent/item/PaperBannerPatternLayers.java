package io.papermc.paper.datacomponent.item;

import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.util.MCUtil;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.block.banner.CraftPatternType;
import org.bukkit.craftbukkit.util.Handleable;
import org.jetbrains.annotations.Unmodifiable;

public record PaperBannerPatternLayers(
    net.minecraft.component.type.BannerPatternsComponent impl
) implements BannerPatternLayers, Handleable<net.minecraft.component.type.BannerPatternsComponent> {

    private static List<Pattern> convert(final net.minecraft.component.type.BannerPatternsComponent nmsPatterns) {
    	return MCUtil.transformUnmodifiable(nmsPatterns.layers(), input -> {
            final Optional<PatternType> type = CraftRegistry.unwrapAndConvertHolder(RegistryKey.BANNER_PATTERN, input.pattern());
            return new Pattern(Objects.requireNonNull(DyeColor.getByWoolData((byte) input.color().getId())), type.orElseThrow(() -> new IllegalStateException("Inlined banner patterns are not supported yet in the API!")));
        });
    }

    @Override
    public net.minecraft.component.type.BannerPatternsComponent getHandle() {
        return this.impl;
    }

    @Override
    public @Unmodifiable List<Pattern> patterns() {
        return convert(impl);
    }

    static final class BuilderImpl implements BannerPatternLayers.Builder {

        private final net.minecraft.component.type.BannerPatternsComponent.Builder builder = new net.minecraft.component.type.BannerPatternsComponent.Builder();

        @Override
        public BannerPatternLayers.Builder add(final Pattern pattern) {
            this.builder.add(
                CraftPatternType.bukkitToMinecraftHolder(pattern.getPattern()),
                net.minecraft.util.DyeColor.byId(pattern.getColor().getWoolData())
            );
            return this;
        }

        @Override
        public BannerPatternLayers.Builder addAll(final List<Pattern> patterns) {
            patterns.forEach(this::add);
            return this;
        }

        @Override
        public BannerPatternLayers build() {
            return new PaperBannerPatternLayers(this.builder.build());
        }
    }
}
