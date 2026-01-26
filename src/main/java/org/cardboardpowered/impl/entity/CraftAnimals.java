package org.cardboardpowered.impl.entity;

import com.google.common.base.Preconditions;
import java.util.UUID;
import net.minecraft.world.entity.animal.Animal;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Animals;
import org.bukkit.inventory.ItemStack;

public class CraftAnimals extends CraftAgeable implements Animals {

    public CraftAnimals(CraftServer server, Animal entity) {
        super(server, entity);
    }

    @Override
    public Animal getHandle() {
        return (Animal) entity;
    }

    @Override
    public String toString() {
        return "Animal";
    }

    @Override
    public UUID getBreedCause() {
        // TODO return getHandle().lovingPlayer;
        return UUID.randomUUID();
    }

    @Override
    public void setBreedCause(UUID uuid) {
        // TODO getHandle().lovingPlayer = uuid;
    }

    @Override
    public boolean isLoveMode() {
        return getHandle().isInLove();
    }

    @Override
    public void setLoveModeTicks(int ticks) {
        Preconditions.checkArgument(ticks >= 0, "Love mode ticks must be positive or 0");
        getHandle().setInLoveTime(ticks);
    }

    @Override
    public int getLoveModeTicks() {
        return getHandle().getInLoveTime();
    }

    // 1.17 API Start

    @Override
    public boolean isBreedItem(ItemStack itemStack) {
        return this.getHandle().isFood(CraftItemStack.asNMSCopy(itemStack));
    }

    @Override
    public boolean isBreedItem(Material material) {
        return this.isBreedItem(new ItemStack(material));
    }
}
