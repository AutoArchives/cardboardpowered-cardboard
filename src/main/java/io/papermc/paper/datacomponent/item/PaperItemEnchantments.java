package io.papermc.paper.datacomponent.item;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.registry.entry.RegistryEntry;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.enchantments.Enchantment;
import org.cardboardpowered.impl.CardboardEnchantment;

public record PaperItemEnchantments(
    net.minecraft.component.type.ItemEnchantmentsComponent impl,
    Map<Enchantment, Integer> enchantments // API values are stored externally as the concept of a lazy key transformer map does not make much sense
) implements ItemEnchantments, Handleable<net.minecraft.component.type.ItemEnchantmentsComponent> {

    public PaperItemEnchantments(final net.minecraft.component.type.ItemEnchantmentsComponent itemEnchantments) {
        this(itemEnchantments, convert(itemEnchantments));
    }

    private static Map<Enchantment, Integer> convert(final net.minecraft.component.type.ItemEnchantmentsComponent itemEnchantments) {
        if (itemEnchantments.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<Enchantment, Integer> map = new HashMap<>(itemEnchantments.getSize());
        for (final Object2IntMap.Entry<RegistryEntry<net.minecraft.enchantment.Enchantment>> entry : itemEnchantments.getEnchantmentEntries()) {
            map.put(CardboardEnchantment.minecraftHolderToBukkit(entry.getKey()), entry.getIntValue());
        }
        return Collections.unmodifiableMap(map); // TODO look into making a "transforming" map maybe?
    }

    @Override
    public net.minecraft.component.type.ItemEnchantmentsComponent getHandle() {
        return this.impl;
    }

    static final class BuilderImpl implements ItemEnchantments.Builder {

        private final Map<Enchantment, Integer> enchantments = new Object2ObjectOpenHashMap<>();

        @Override
        public ItemEnchantments.Builder add(final Enchantment enchantment, final int level) {
            Preconditions.checkArgument(
                level >= 1 && level <= net.minecraft.enchantment.Enchantment.MAX_LEVEL,
                "level must be between %s and %s, was %s",
                1, net.minecraft.enchantment.Enchantment.MAX_LEVEL,
                level
            );
            this.enchantments.put(enchantment, level);
            return this;
        }

        @Override
        public ItemEnchantments.Builder addAll(final Map<Enchantment, Integer> enchantments) {
            enchantments.forEach(this::add);
            return this;
        }

        @Override
        public ItemEnchantments build() {
            final net.minecraft.component.type.ItemEnchantmentsComponent initialEnchantments = net.minecraft.component.type.ItemEnchantmentsComponent.DEFAULT; // .withShowInTooltip(this.showInTooltip);
            if (this.enchantments.isEmpty()) {
                return new PaperItemEnchantments(initialEnchantments);
            }

            final net.minecraft.component.type.ItemEnchantmentsComponent.Builder mutable = new net.minecraft.component.type.ItemEnchantmentsComponent.Builder(initialEnchantments);
            this.enchantments.forEach((enchantment, level) ->
                mutable.set(CardboardEnchantment.bukkitToMinecraftHolder(enchantment), level)
            );
            return new PaperItemEnchantments(mutable.build());
        }
    }
}
