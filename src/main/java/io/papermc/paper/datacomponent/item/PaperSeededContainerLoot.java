package io.papermc.paper.datacomponent.item;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.key.Key;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.craftbukkit.util.Handleable;

public record PaperSeededContainerLoot(
    net.minecraft.component.type.ContainerLootComponent impl
) implements SeededContainerLoot, Handleable<net.minecraft.component.type.ContainerLootComponent> {

    @Override
    public net.minecraft.component.type.ContainerLootComponent getHandle() {
        return this.impl;
    }

    @Override
    public Key lootTable() {
        return CraftNamespacedKey.fromMinecraft(this.impl.lootTable().getValue());
    }

    @Override
    public long seed() {
        return this.impl.seed();
    }

    static final class BuilderImpl implements SeededContainerLoot.Builder {

        private long seed = LootTable.DEFAULT_SEED;
        private Key key;

        BuilderImpl(final Key key) {
            this.key = key;
        }

        @Override
        public SeededContainerLoot.Builder lootTable(final Key key) {
            this.key = key;
            return this;
        }

        @Override
        public SeededContainerLoot.Builder seed(final long seed) {
            this.seed = seed;
            return this;
        }

        @Override
        public SeededContainerLoot build() {
            return new PaperSeededContainerLoot(new net.minecraft.component.type.ContainerLootComponent(
                RegistryKey.of(RegistryKeys.LOOT_TABLE, PaperAdventure.asVanilla(this.key)),
                this.seed
            ));
        }
    }
}
