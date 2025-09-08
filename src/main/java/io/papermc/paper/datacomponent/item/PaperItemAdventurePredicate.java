package io.papermc.paper.datacomponent.item;

import io.papermc.paper.block.BlockPredicate;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.util.Conversions;
import io.papermc.paper.registry.set.PaperRegistrySets;
import io.papermc.paper.util.MCUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.component.type.BlockPredicatesComponent;
import net.minecraft.predicate.component.ComponentsPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import org.bukkit.craftbukkit.util.Handleable;
import org.cardboardpowered.Registries_Bridge;

public record PaperItemAdventurePredicate(
		BlockPredicatesComponent impl
) implements ItemAdventurePredicate, Handleable<BlockPredicatesComponent> {

    private static List<BlockPredicate> convert(final BlockPredicatesComponent nmsModifiers) {
        return MCUtil.transformUnmodifiable(nmsModifiers.predicates, nms -> BlockPredicate.predicate()
            .blocks(nms.blocks().map(blocks -> PaperRegistrySets.convertToApi(RegistryKey.BLOCK, blocks)).orElse(null)).build());
    }

    @Override
    public BlockPredicatesComponent getHandle() {
        return this.impl;
    }

    @Override
    public boolean showInTooltip() {
        return false; // this.impl.showInTooltip();
    }

    @Override
    public PaperItemAdventurePredicate showInTooltip(final boolean showInTooltip) {
        return new PaperItemAdventurePredicate(this.impl/*.withShowInTooltip(showInTooltip)*/);
    }

    @Override
    public List<BlockPredicate> predicates() {
        return convert(this.impl);
    }

    static final class BuilderImpl implements ItemAdventurePredicate.Builder {

        private final List<net.minecraft.predicate.BlockPredicate> predicates = new ObjectArrayList<>();
        private boolean showInTooltip = true;

        @Override
        public ItemAdventurePredicate.Builder addPredicate(BlockPredicate predicate) {
            this.predicates.add(new net.minecraft.predicate.BlockPredicate(Optional.ofNullable(predicate.blocks()).map(blocks -> PaperRegistrySets.convertToNms(RegistryKeys.BLOCK, Conversions.global().lookup(), blocks)), Optional.empty(), Optional.empty(), ComponentsPredicate.EMPTY));
            return this;
        }

        @Override
        public io.papermc.paper.datacomponent.item.ItemAdventurePredicate.Builder addPredicates(final List<BlockPredicate> predicates) {
            for (final BlockPredicate predicate : predicates) {
                this.addPredicate(predicate);
            }
            return this;
        }

        @Override
        public ItemAdventurePredicate.Builder showInTooltip(final boolean showInTooltip) {
            this.showInTooltip = showInTooltip;
            return this;
        }

        @Override
        public ItemAdventurePredicate build() {
            return new PaperItemAdventurePredicate(new BlockPredicatesComponent(new ObjectArrayList<>(this.predicates)/*, this.showInTooltip*/));
        }
    }
}
