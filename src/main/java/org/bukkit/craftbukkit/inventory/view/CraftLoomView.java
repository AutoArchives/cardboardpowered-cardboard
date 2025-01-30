package org.bukkit.craftbukkit.inventory.view;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.LoomScreenHandler;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.block.banner.CraftPatternType;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.LoomInventory;
import org.bukkit.inventory.view.LoomView;

public class CraftLoomView extends CraftInventoryView<LoomScreenHandler, LoomInventory> implements LoomView {

    public CraftLoomView(final HumanEntity player, final LoomInventory viewing, final LoomScreenHandler container) {
        super(player, viewing, container);
    }

    @Override
    public List<PatternType> getSelectablePatterns() {
        final List<RegistryEntry<BannerPattern>> selectablePatterns = ((LoomScreenHandler)this.container).getBannerPatterns();
        final List<PatternType> patternTypes = new ArrayList<>(selectablePatterns.size());
        for (final RegistryEntry<BannerPattern> selectablePattern : selectablePatterns) {
            patternTypes.add(CraftPatternType.minecraftHolderToBukkit(selectablePattern));
        }
        return patternTypes;
    }

    @Override
    public int getSelectedPatternIndex() {
        return ((LoomScreenHandler)this.container).getSelectedPattern();
    }
}
