package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.DataComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.block.banner.CraftPatternType;
import org.bukkit.craftbukkit.inventory.CraftMetaItem;
import org.bukkit.craftbukkit.inventory.SerializableMeta;
import org.bukkit.inventory.meta.BannerMeta;
import org.jetbrains.annotations.Nullable;

@DelegateDeserialization(value=SerializableMeta.class)
public class CraftMetaBanner
extends CraftMetaItem
implements BannerMeta {
    private static final Set<Material> BANNER_MATERIALS = Sets.newHashSet(new Material[]{Material.BLACK_BANNER, Material.BLACK_WALL_BANNER, Material.BLUE_BANNER, Material.BLUE_WALL_BANNER, Material.BROWN_BANNER, Material.BROWN_WALL_BANNER, Material.CYAN_BANNER, Material.CYAN_WALL_BANNER, Material.GRAY_BANNER, Material.GRAY_WALL_BANNER, Material.GREEN_BANNER, Material.GREEN_WALL_BANNER, Material.LIGHT_BLUE_BANNER, Material.LIGHT_BLUE_WALL_BANNER, Material.LIGHT_GRAY_BANNER, Material.LIGHT_GRAY_WALL_BANNER, Material.LIME_BANNER, Material.LIME_WALL_BANNER, Material.MAGENTA_BANNER, Material.MAGENTA_WALL_BANNER, Material.ORANGE_BANNER, Material.ORANGE_WALL_BANNER, Material.PINK_BANNER, Material.PINK_WALL_BANNER, Material.PURPLE_BANNER, Material.PURPLE_WALL_BANNER, Material.RED_BANNER, Material.RED_WALL_BANNER, Material.WHITE_BANNER, Material.WHITE_WALL_BANNER, Material.YELLOW_BANNER, Material.YELLOW_WALL_BANNER});
    static final CraftMetaItem.ItemMetaKeyType<BannerPatternsComponent> PATTERNS = new CraftMetaItem.ItemMetaKeyType<BannerPatternsComponent>(DataComponentTypes.BANNER_PATTERNS, "patterns");
    private List<Pattern> patterns = new ArrayList<Pattern>();

    CraftMetaBanner(CraftMetaItem meta) {
        super(meta);
        if (!(meta instanceof CraftMetaBanner)) {
            return;
        }
        CraftMetaBanner banner = (CraftMetaBanner)meta;
        this.patterns = new ArrayList<Pattern>(banner.patterns);
    }

    CraftMetaBanner(ComponentChanges tag, Set<DataComponentType<?>> extraHandledDcts) {
        super(tag, extraHandledDcts);
        CraftMetaBanner.getOrEmpty(tag, PATTERNS).ifPresent(entityTag -> {
            List<BannerPatternsComponent.Layer> patterns = entityTag.layers();
            for (int i2 = 0; i2 < Math.min(patterns.size(), 20); ++i2) {
                BannerPatternsComponent.Layer p = patterns.get(i2);
                DyeColor color = DyeColor.getByWoolData((byte)((byte)p.color().getId()));
                PatternType pattern = CraftPatternType.minecraftHolderToBukkit(p.pattern());
                if (color == null || pattern == null) continue;
                this.patterns.add(new Pattern(color, pattern));
            }
        });
    }

    CraftMetaBanner(Map<String, Object> map) {
        super(map);
        Iterable rawPatternList = SerializableMeta.getObject(Iterable.class, map, CraftMetaBanner.PATTERNS.BUKKIT, true);
        if (rawPatternList == null) {
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
        if (this.patterns.isEmpty()) {
            return;
        }
        ArrayList<BannerPatternsComponent.Layer> newPatterns = new ArrayList<BannerPatternsComponent.Layer>();
        for (Pattern p : this.patterns) {
            newPatterns.add(new BannerPatternsComponent.Layer(CraftPatternType.bukkitToMinecraftHolder(p.getPattern()), net.minecraft.util.DyeColor.byId(p.getColor().getWoolData())));
        }
        tag.put(PATTERNS, new BannerPatternsComponent(newPatterns));
    }

    public List<Pattern> getPatterns() {
        return new ArrayList<Pattern>(this.patterns);
    }

    public void setPatterns(List<Pattern> patterns) {
        this.patterns = new ArrayList<Pattern>(patterns);
    }

    public void addPattern(Pattern pattern) {
        this.patterns.add(pattern);
    }

    public Pattern getPattern(int i2) {
        return this.patterns.get(i2);
    }

    public Pattern removePattern(int i2) {
        return this.patterns.remove(i2);
    }

    public void setPattern(int i2, Pattern pattern) {
        this.patterns.set(i2, pattern);
    }

    public int numberOfPatterns() {
        return this.patterns.size();
    }

    @Override
    ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
        super.serialize(builder);
        if (!this.patterns.isEmpty()) {
            builder.put(CraftMetaBanner.PATTERNS.BUKKIT, ImmutableList.copyOf(this.patterns));
        }
        return builder;
    }

    @Override
    int applyHash() {
        int original;
        int hash = original = super.applyHash();
        if (!this.patterns.isEmpty()) {
            hash = 31 * hash + this.patterns.hashCode();
        }
        return original != hash ? CraftMetaBanner.class.hashCode() ^ hash : hash;
    }

    @Override
    public boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaBanner) {
            CraftMetaBanner that = (CraftMetaBanner)meta;
            return this.patterns.equals(that.patterns);
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaBanner || this.patterns.isEmpty());
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && this.patterns.isEmpty();
    }

    @Override
    boolean applicableTo(Material type) {
        return BANNER_MATERIALS.contains(type);
    }

    @Override
    public CraftMetaBanner clone() {
        CraftMetaBanner meta = (CraftMetaBanner)super.clone();
        meta.patterns = new ArrayList<Pattern>(this.patterns);
        return meta;
    }

	@Override
	public @Nullable DyeColor getBaseColor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBaseColor(@Nullable DyeColor arg0) {
		// TODO Auto-generated method stub
		
	}
}

