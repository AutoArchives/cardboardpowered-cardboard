package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;

import io.papermc.paper.registry.HolderableBase;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.animal.chicken.ChickenVariant;

public class CraftChicken extends CraftAnimals implements Chicken {

    public CraftChicken(CraftServer server, net.minecraft.world.entity.animal.chicken.Chicken entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.animal.chicken.Chicken getHandle() {
        return (net.minecraft.world.entity.animal.chicken.Chicken) entity;
    }

    @Override
    public String toString() {
        return "Chicken";
    }

    @Override
    public EntityType getType() {
        return EntityType.CHICKEN;
    }

	@Override
	public int getEggLayTime() {
		return this.getHandle().eggTime;
	}

	@Override
	public boolean isChickenJockey() {
		return this.getHandle().isChickenJockey();
	}

	@Override
	public void setEggLayTime(int arg0) {
        this.getHandle().eggTime = arg0;
	}

	@Override
	public void setIsChickenJockey(boolean arg0) {
		this.getHandle().setChickenJockey(arg0);
	}
	
	public Chicken.Variant getVariant() {
        return CraftVariant.minecraftHolderToBukkit(this.getHandle().getVariant());
    }

    public void setVariant(Chicken.Variant variant) {
        this.getHandle().setVariant(CraftVariant.bukkitToMinecraftHolder(variant));
    }
    
    public static class CraftVariant extends HolderableBase<ChickenVariant> implements Chicken.Variant {
        public static Chicken.Variant minecraftToBukkit(ChickenVariant minecraft) {
            return (Chicken.Variant)CraftRegistry.minecraftToBukkit(minecraft, Registries.CHICKEN_VARIANT);
        }

        public static Chicken.Variant minecraftHolderToBukkit(Holder<ChickenVariant> minecraft) {
            return (Chicken.Variant)CraftRegistry.minecraftHolderToBukkit(minecraft, Registries.CHICKEN_VARIANT);
        }

        public static ChickenVariant bukkitToMinecraft(Chicken.Variant bukkit) {
            return (ChickenVariant)CraftRegistry.bukkitToMinecraft(bukkit);
        }

        public static Holder<ChickenVariant> bukkitToMinecraftHolder(Chicken.Variant bukkit) {
            return CraftRegistry.bukkitToMinecraftHolder(bukkit);
        }

        public CraftVariant(Holder<ChickenVariant> holder) {
            super(holder);
        }
    }

}