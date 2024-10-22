package io.papermc.paper.registry.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import io.papermc.paper.registry.PaperRegistryBuilder;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.util.Checks;
import io.papermc.paper.registry.data.util.Conversions;
import io.papermc.paper.registry.set.PaperRegistrySets;
import io.papermc.paper.registry.set.RegistryKeySet;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import net.kyori.adventure.text.Component;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.text.Text;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.jetbrains.annotations.Range;

@DefaultQualifier(value=NonNull.class)
public class PaperEnchantmentRegistryEntry
implements EnchantmentRegistryEntry {
    protected @MonotonicNonNull Text description;
    protected @MonotonicNonNull RegistryEntryList<Item> supportedItems;
    protected @Nullable RegistryEntryList<Item> primaryItems;
    protected OptionalInt weight = OptionalInt.empty();
    protected OptionalInt maxLevel = OptionalInt.empty();
    protected Enchantment.Cost minimumCost;
    protected Enchantment.Cost maximumCost;
    protected OptionalInt anvilCost = OptionalInt.empty();
    protected @MonotonicNonNull List<AttributeModifierSlot> activeSlots;
    protected RegistryEntryList<Enchantment> exclusiveWith = RegistryEntryList.empty();
    protected ComponentMap effects;
    protected final Conversions conversions;

    public PaperEnchantmentRegistryEntry(Conversions conversions, TypedKey<org.bukkit.enchantments.Enchantment> ignoredKey, @Nullable Enchantment internal) {
        this.conversions = conversions;
        if (internal == null) {
            this.effects = ComponentMap.EMPTY;
            return;
        }
        this.description = internal.description();
        Enchantment.Definition definition = internal.definition();
        this.supportedItems = definition.supportedItems();
        this.primaryItems = definition.primaryItems().orElse(null);
        this.weight = OptionalInt.of(definition.weight());
        this.maxLevel = OptionalInt.of(definition.maxLevel());
        this.minimumCost = definition.minCost();
        this.maximumCost = definition.maxCost();
        this.anvilCost = OptionalInt.of(definition.anvilCost());
        this.activeSlots = definition.slots();
        this.exclusiveWith = internal.exclusiveSet();
        this.effects = internal.effects();
    }

    public Component description() {
        return this.conversions.asAdventure(Checks.asConfigured(this.description, "description"));
    }

    public RegistryKeySet<ItemType> supportedItems() {
        return PaperRegistrySets.convertToApi(RegistryKey.ITEM, Checks.asConfigured(this.supportedItems, "supportedItems"));
    }

    public @Nullable RegistryKeySet<ItemType> primaryItems() {
        return this.primaryItems == null ? null : PaperRegistrySets.convertToApi(RegistryKey.ITEM, this.primaryItems);
    }

    public @Range(from=1L, to=1024L) int weight() {
        return Checks.asConfigured(this.weight, "weight");
    }

    public @Range(from=1L, to=255L) int maxLevel() {
        return Checks.asConfigured(this.maxLevel, "maxLevel");
    }

    public EnchantmentRegistryEntry.EnchantmentCost minimumCost() {
        Enchantment.Cost cost = Checks.asConfigured(this.minimumCost, "minimumCost");
        return EnchantmentRegistryEntry.EnchantmentCost.of((int)cost.base(), (int)cost.perLevelAboveFirst());
    }

    public EnchantmentRegistryEntry.EnchantmentCost maximumCost() {
        Enchantment.Cost cost = Checks.asConfigured(this.maximumCost, "maximumCost");
        return EnchantmentRegistryEntry.EnchantmentCost.of((int)cost.base(), (int)cost.perLevelAboveFirst());
    }

    public @Range(from=0L, to=0x7FFFFFFFL) int anvilCost() {
        return Checks.asConfigured(this.anvilCost, "anvilCost");
    }

    public List<EquipmentSlotGroup> activeSlots() {
        return Collections.unmodifiableList(Lists.transform(Checks.asConfigured(this.activeSlots, "activeSlots"), CraftEquipmentSlot::getSlot));
    }

    public RegistryKeySet<org.bukkit.enchantments.Enchantment> exclusiveWith() {
        return PaperRegistrySets.convertToApi(RegistryKey.ENCHANTMENT, this.exclusiveWith);
    }

    public static final class PaperBuilder
    extends PaperEnchantmentRegistryEntry
    implements EnchantmentRegistryEntry.Builder,
    PaperRegistryBuilder<Enchantment, org.bukkit.enchantments.Enchantment> {
        public PaperBuilder(Conversions conversions, TypedKey<org.bukkit.enchantments.Enchantment> key, @Nullable Enchantment internal) {
            super(conversions, key, internal);
        }

        public EnchantmentRegistryEntry.Builder description(Component description) {
            this.description = this.conversions.asVanilla(Checks.asArgument(description, "description"));
            return this;
        }

        public EnchantmentRegistryEntry.Builder supportedItems(RegistryKeySet<ItemType> supportedItems) {
            this.supportedItems = PaperRegistrySets.convertToNms(RegistryKeys.ITEM, this.conversions.lookup(), Checks.asArgument(supportedItems, "supportedItems"));
            return this;
        }

        public EnchantmentRegistryEntry.Builder primaryItems(@Nullable RegistryKeySet<ItemType> primaryItems) {
            this.primaryItems = primaryItems == null ? null : PaperRegistrySets.convertToNms(RegistryKeys.ITEM, this.conversions.lookup(), primaryItems);
            return this;
        }

        public EnchantmentRegistryEntry.Builder weight(@Range(from=1L, to=1024L) int weight) {
            this.weight = OptionalInt.of(Checks.asArgumentRange(weight, "weight", 1, 1024));
            return this;
        }

        public EnchantmentRegistryEntry.Builder maxLevel(@Range(from=1L, to=255L) int maxLevel) {
            this.maxLevel = OptionalInt.of(Checks.asArgumentRange(maxLevel, "maxLevel", 1, 255));
            return this;
        }

        public EnchantmentRegistryEntry.Builder minimumCost(EnchantmentRegistryEntry.EnchantmentCost minimumCost) {
            EnchantmentRegistryEntry.EnchantmentCost validCost = Checks.asArgument(minimumCost, "minimumCost");
            this.minimumCost = Enchantment.leveledCost(validCost.baseCost(), validCost.additionalPerLevelCost());
            return this;
        }

        public EnchantmentRegistryEntry.Builder maximumCost(EnchantmentRegistryEntry.EnchantmentCost maximumCost) {
            EnchantmentRegistryEntry.EnchantmentCost validCost = Checks.asArgument(maximumCost, "maximumCost");
            this.maximumCost = Enchantment.leveledCost(validCost.baseCost(), validCost.additionalPerLevelCost());
            return this;
        }

        public EnchantmentRegistryEntry.Builder anvilCost(@Range(from=0L, to=0x7FFFFFFFL) int anvilCost) {
            Preconditions.checkArgument((anvilCost >= 0 ? 1 : 0) != 0, (Object)"anvilCost must be non-negative");
            this.anvilCost = OptionalInt.of(Checks.asArgumentMin(anvilCost, "anvilCost", 0));
            return this;
        }

        public EnchantmentRegistryEntry.Builder activeSlots(Iterable<EquipmentSlotGroup> activeSlots) {
            this.activeSlots = Lists.newArrayList((Iterable)Iterables.transform(Checks.asArgument(activeSlots, "activeSlots"), CraftEquipmentSlot::getNMSGroup));
            return this;
        }

        public EnchantmentRegistryEntry.Builder exclusiveWith(RegistryKeySet<org.bukkit.enchantments.Enchantment> exclusiveWith) {
            this.exclusiveWith = PaperRegistrySets.convertToNms(RegistryKeys.ENCHANTMENT, this.conversions.lookup(), Checks.asArgument(exclusiveWith, "exclusiveWith"));
            return this;
        }

        @Override
        public Enchantment build() {
            Enchantment.Definition def = new Enchantment.Definition(Checks.asConfigured(this.supportedItems, "supportedItems"), Optional.ofNullable(this.primaryItems), this.weight(), this.maxLevel(), Checks.asConfigured(this.minimumCost, "minimumCost"), Checks.asConfigured(this.maximumCost, "maximumCost"), this.anvilCost(), Collections.unmodifiableList(Checks.asConfigured(this.activeSlots, "activeSlots")));
            return new Enchantment(Checks.asConfigured(this.description, "description"), def, this.exclusiveWith, this.effects);
        }
    }
}

