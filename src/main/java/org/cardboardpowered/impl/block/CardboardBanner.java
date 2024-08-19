package org.cardboardpowered.impl.block;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.jetbrains.annotations.Nullable;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Pair;

import me.isaiah.common.cmixin.IMixinBlockEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;

@SuppressWarnings("deprecation")
public class CardboardBanner extends CardboardBlockEntityState<BannerBlockEntity> implements Banner {

    private DyeColor base;
    private List<Pattern> patterns;

    public CardboardBanner(World world, BannerBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CardboardBanner(CardboardBanner state, Location location) {
        super(state, location);
    }
    
    @Override
    public CardboardBanner copy() {
        return new CardboardBanner(this, null);
    }

    @Override
    public CardboardBanner copy(Location location) {
        return new CardboardBanner(this, location);
    }
    
    /*
    public CardboardBanner(final Block block) {
        super(block, BannerBlockEntity.class);
    }

    public CardboardBanner(final Material material, final BannerBlockEntity te) {
        super(material, te);
    }
    */

    @Override
    public void load(BannerBlockEntity banner) {
        super.load(banner);

        base = DyeColor.getByWoolData((byte) ((AbstractBannerBlock) this.data.getBlock()).getColor().getId());
        patterns = new ArrayList<Pattern>();

        /*for (int i = 0; i < banner.getPatterns().size(); i++) {
        	Pair<RegistryEntry<BannerPattern>, net.minecraft.util.DyeColor> pair = banner.getPatterns().get(i);
            patterns.add(new Pattern(DyeColor.getByWoolData((byte) pair.getSecond().getId()), PatternType.getByIdentifier(pair.getFirst().value().getId())));
        }*/
        
        if (banner.getPatterns() != null) {
            for (int i = 0; i < banner.getPatterns().layers().size(); i++) {
            	BannerPatternsComponent.Layer p = banner.getPatterns().layers().get(i);
                // TODO this.patterns.add(new Pattern(DyeColor.getByWoolData((byte) p.color().getId()), CraftPatternType.minecraftHolderToBukkit(p.pattern())));
            	this.patterns.add(new Pattern(DyeColor.getByWoolData((byte) p.color().getId()), PatternType.getByIdentifier(p.pattern().getIdAsString()) ));
            }
        }

    }

    @Override
    public DyeColor getBaseColor() {
        return this.base;
    }

    @Override
    public void setBaseColor(DyeColor color) {
        Preconditions.checkArgument(color != null, "color");
        this.base = color;
    }

    @Override
    public List<Pattern> getPatterns() {
        return new ArrayList<Pattern>(patterns);
    }

    @Override
    public void setPatterns(List<Pattern> patterns) {
        this.patterns = new ArrayList<Pattern>(patterns);
    }

    @Override
    public void addPattern(Pattern pattern) {
        this.patterns.add(pattern);
    }

    @Override
    public Pattern getPattern(int i) {
        return this.patterns.get(i);
    }

    @Override
    public Pattern removePattern(int i) {
        return this.patterns.remove(i);
    }

    @Override
    public void setPattern(int i, Pattern pattern) {
        this.patterns.set(i, pattern);
    }

    @Override
    public int numberOfPatterns() {
        return patterns.size();
    }

    @Override
    public void applyTo(BannerBlockEntity banner) {
        super.applyTo(banner);

        banner.baseColor = net.minecraft.util.DyeColor.byId(base.getWoolData());
        NbtCompound bannerNbt = new NbtCompound();
        NbtList newPatterns = new NbtList();

        for (Pattern p : patterns) {
            NbtCompound compound = new NbtCompound();
            compound.putInt("Color", p.getColor().getWoolData());
            compound.putString("Pattern", p.getPattern().getIdentifier());
            newPatterns.add(compound);
        }
        bannerNbt.put("Patterns", newPatterns);

        IMixinBlockEntity ic = (IMixinBlockEntity) (Object) banner;
        ic.IC$read_nbt(bannerNbt);
        // banner.readNbt(bannerNbt);
    }

	
    public Component customName() {
       // return PaperAdventure.asAdventure(((BannerBlockEntity)this.getSnapshot()).getCustomName());
    	return Component.text(((BannerBlockEntity)this.getSnapshot()).getCustomName().getString());
    }

    public void customName(Component customName) {
        set_name(((BannerBlockEntity)this.getSnapshot()), Text.of(customName.toString()));

    }
    
	public static void set_name(BannerBlockEntity b, Text name) {
		
		b.customName = name;
	}

    public String getCustomName() {
        return (String)LegacyComponentSerializer.legacySection().serializeOrNull(this.customName());
    }

    public void setCustomName(String name) {
        this.customName(LegacyComponentSerializer.legacySection().deserializeOrNull(name));
    }

}