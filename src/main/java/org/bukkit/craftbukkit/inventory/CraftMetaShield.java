package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import io.papermc.paper.registry.RegistryKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockState;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.bukkit.craftbukkit.block.banner.CraftPatternType;
import org.bukkit.craftbukkit.inventory.CraftMetaBanner;
import org.bukkit.craftbukkit.inventory.CraftMetaBlockState;
import org.bukkit.craftbukkit.inventory.CraftMetaItem;
import org.bukkit.craftbukkit.inventory.SerializableMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ShieldMeta;
import org.jetbrains.annotations.Nullable;

@DelegateDeserialization(value=SerializableMeta.class)
public class CraftMetaShield
extends CraftMetaItem
implements ShieldMeta,
BlockStateMeta {
    static final CraftMetaItem.ItemMetaKeyType<net.minecraft.world.item.DyeColor> BASE_COLOR = new CraftMetaItem.ItemMetaKeyType<net.minecraft.world.item.DyeColor>(DataComponents.BASE_COLOR, "Base", "base-color");
    @Nullable
    private List<Pattern> patterns;
    @Nullable
    private DyeColor baseColor;

    private boolean hasPatterns() {
        return this.patterns != null && !this.patterns.isEmpty();
    }

    CraftMetaShield(CraftMetaItem meta) {
        super(meta);
        BlockState blockState;
        CraftMetaBlockState state;
        if (meta instanceof CraftMetaShield) {
            CraftMetaShield craftMetaShield = (CraftMetaShield)meta;
            if (craftMetaShield.patterns != null) {
                this.patterns = new ArrayList<Pattern>(craftMetaShield.getPatterns());
            }
            if (craftMetaShield.baseColor != null) {
                this.baseColor = craftMetaShield.baseColor;
            }
        } else if (meta instanceof CraftMetaBlockState && (state = (CraftMetaBlockState)meta).hasBlockState() && (blockState = state.getBlockState()) instanceof Banner) {
            Banner banner = (Banner)blockState;
            this.patterns = banner.getPatterns();
            this.baseColor = banner.getBaseColor();
        }
    }

    CraftMetaShield(DataComponentPatch tag, Set<DataComponentType<?>> extraHandledDcts) {
        super(tag, extraHandledDcts);
        CraftMetaShield.getOrEmpty(tag, BASE_COLOR).ifPresent(color -> {
            this.baseColor = DyeColor.getByWoolData((byte)((byte)color.getId()));
        });
        CraftMetaShield.getOrEmpty(tag, CraftMetaBanner.PATTERNS).ifPresent(entityTag -> {
            List<BannerPatternLayers.Layer> patterns = entityTag.layers();
            for (int i2 = 0; i2 < Math.min(patterns.size(), 20); ++i2) {
                BannerPatternLayers.Layer p = patterns.get(i2);
                DyeColor color = DyeColor.getByWoolData((byte)((byte)p.color().getId()));
                PatternType pattern = CraftRegistry.unwrapAndConvertHolder(RegistryKey.BANNER_PATTERN, p.pattern()).orElse(null);
                if (color == null || pattern == null) continue;
                this.addPattern(new Pattern(color, pattern));
            }
        });
    }

    CraftMetaShield(Map<String, Object> map) {
        super(map);
        Iterable rawPatternList;
        String baseColor = org.bukkit.craftbukkit.inventory.CraftMetaItem.SerializableMeta.getString(map, CraftMetaShield.BASE_COLOR.BUKKIT, true);
        if (baseColor != null) {
            this.baseColor = DyeColor.valueOf((String)baseColor);
        }
        if ((rawPatternList = org.bukkit.craftbukkit.inventory.CraftMetaItem.SerializableMeta.getObject(Iterable.class, map, CraftMetaBanner.PATTERNS.BUKKIT, true)) == null) {
            return;
        }
        for (Object obj : rawPatternList) {
            Preconditions.checkArgument((boolean)(obj instanceof Pattern), (String)"Object (%s) in pattern list is not valid", obj.getClass());
            this.addPattern((Pattern)obj);
        }
    }

    @Override
    void applyToItem(CraftMetaItem.Applicator tag) {
        super.applyToItem(tag);
        if (this.baseColor != null) {
            tag.put(BASE_COLOR, net.minecraft.world.item.DyeColor.byId(this.baseColor.getWoolData()));
        }
        if (this.patterns != null && !this.patterns.isEmpty()) {
            ArrayList<BannerPatternLayers.Layer> newPatterns = new ArrayList<BannerPatternLayers.Layer>();
            for (Pattern p : this.patterns) {
                newPatterns.add(new BannerPatternLayers.Layer(CraftPatternType.bukkitToMinecraftHolder(p.getPattern()), net.minecraft.world.item.DyeColor.byId(p.getColor().getWoolData())));
            }
            tag.put(CraftMetaBanner.PATTERNS, new BannerPatternLayers(newPatterns));
        }
    }

    public List<Pattern> getPatterns() {
        if (this.patterns == null) {
            return new ArrayList<Pattern>();
        }
        return new ArrayList<Pattern>(this.patterns);
    }

    public void setPatterns(List<Pattern> patterns) {
        this.patterns = new ArrayList<Pattern>(patterns);
    }

    public void addPattern(Pattern pattern) {
        if (this.patterns == null) {
            this.patterns = new ArrayList<Pattern>();
        }
        this.patterns.add(pattern);
    }

    public Pattern getPattern(int i2) {
        if (this.patterns == null) {
            throw new IndexOutOfBoundsException(i2);
        }
        return this.patterns.get(i2);
    }

    public Pattern removePattern(int i2) {
        if (this.patterns == null) {
            throw new IndexOutOfBoundsException(i2);
        }
        return this.patterns.remove(i2);
    }

    public void setPattern(int i2, Pattern pattern) {
        if (this.patterns == null) {
            throw new IndexOutOfBoundsException(i2);
        }
        this.patterns.set(i2, pattern);
    }

    public int numberOfPatterns() {
        if (this.patterns == null) {
            return 0;
        }
        return this.patterns.size();
    }

    public DyeColor getBaseColor() {
        return this.baseColor;
    }

    public void setBaseColor(DyeColor baseColor) {
        this.baseColor = baseColor;
    }

    @Override
    ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
        super.serialize(builder);
        if (this.baseColor != null) {
            builder.put(CraftMetaShield.BASE_COLOR.BUKKIT, this.baseColor.toString());
        }
        if (this.hasPatterns()) {
            builder.put(CraftMetaBanner.PATTERNS.BUKKIT, this.patterns);
        }
        return builder;
    }

    @Override
    int applyHash() {
        int original;
        int hash = original = super.applyHash();
        if (this.baseColor != null) {
            hash = 61 * hash + this.baseColor.hashCode();
        }
        if (this.hasPatterns()) {
            hash = 61 * hash + this.patterns.hashCode();
        }
        return original != hash ? CraftMetaShield.class.hashCode() ^ hash : hash;
    }

    @Override
    boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaShield) {
            CraftMetaShield other = (CraftMetaShield)meta;
            return Objects.equals(this.baseColor, other.baseColor) && Objects.equals(this.patterns, other.patterns);
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaShield || this.baseColor == null && !this.hasPatterns());
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && this.baseColor == null && !this.hasPatterns();
    }

    public boolean hasBlockState() {
        return this.baseColor != null || this.hasPatterns();
    }

    public BlockState getBlockState() {
        Banner banner = CraftMetaShield.getBlockState(this.baseColor);
        if (this.patterns != null) {
            banner.setPatterns(this.patterns);
        }
        return banner;
    }

    public void setBlockState(BlockState blockState) {
        Preconditions.checkArgument((blockState != null ? 1 : 0) != 0, (Object)"blockState must not be null");
        Preconditions.checkArgument((boolean)(blockState instanceof Banner), (Object)"Invalid blockState");
        Banner banner = (Banner)blockState;
        this.baseColor = banner.getBaseColor();
        this.patterns = banner.getPatterns();
    }

    public void clearBlockState() {
        this.baseColor = null;
        this.patterns = null;
    }

    private static Banner getBlockState(DyeColor color) {
        Material stateMaterial = CraftMetaShield.shieldToBannerHack(color);
        return (Banner)CraftBlockStates.getBlockState(CraftRegistry.getMinecraftRegistry(), BlockPos.ZERO, stateMaterial, null);
    }

    @Override
    public CraftMetaShield clone() {
        CraftMetaShield meta = (CraftMetaShield)super.clone();
        meta.baseColor = this.baseColor;
        meta.patterns = this.patterns == null ? null : new ArrayList<Pattern>(this.patterns);
        return meta;
    }

    static Material shieldToBannerHack(DyeColor color) {
        if (color == null) {
            return Material.WHITE_BANNER;
        }
        return switch (color) {
            case DyeColor.WHITE -> Material.WHITE_BANNER;
            case DyeColor.ORANGE -> Material.ORANGE_BANNER;
            case DyeColor.MAGENTA -> Material.MAGENTA_BANNER;
            case DyeColor.LIGHT_BLUE -> Material.LIGHT_BLUE_BANNER;
            case DyeColor.YELLOW -> Material.YELLOW_BANNER;
            case DyeColor.LIME -> Material.LIME_BANNER;
            case DyeColor.PINK -> Material.PINK_BANNER;
            case DyeColor.GRAY -> Material.GRAY_BANNER;
            case DyeColor.LIGHT_GRAY -> Material.LIGHT_GRAY_BANNER;
            case DyeColor.CYAN -> Material.CYAN_BANNER;
            case DyeColor.PURPLE -> Material.PURPLE_BANNER;
            case DyeColor.BLUE -> Material.BLUE_BANNER;
            case DyeColor.BROWN -> Material.BROWN_BANNER;
            case DyeColor.GREEN -> Material.GREEN_BANNER;
            case DyeColor.RED -> Material.RED_BANNER;
            case DyeColor.BLACK -> Material.BLACK_BANNER;
            default -> throw new IllegalArgumentException("Unknown banner colour");
        };
    }
}

