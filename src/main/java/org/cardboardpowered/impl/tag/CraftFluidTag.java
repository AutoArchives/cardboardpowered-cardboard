package org.cardboardpowered.impl.tag;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.bukkit.Tag;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;

public class CraftFluidTag extends CraftTag<Fluid, org.bukkit.Fluid> {

    public CraftFluidTag(Registry<Fluid> registry, TagKey<Fluid> tag) {
        super(registry, tag);
    }

    public boolean isTagged(org.bukkit.Fluid fluid) {
    	 
    	Optional<Reference<Fluid>> aa = this.registry.get( CraftNamespacedKey.toMinecraft(fluid.getKey()) );
    	if (aa.isEmpty()) {
    		return false;
    	}
    	
    	return aa.get().is(this.tag);
    	
        // this.registry.entryOf(RegistryKey.of(RegistryKeys.FLUID, CraftNamespacedKey.toMinecraft(fluid.getKey()))).isIn(this.tag);
    }

    public Set<org.bukkit.Fluid> getValues() {
        return this.getHandle().stream().map(fluid -> CraftMagicNumbers.getFluid((Fluid)fluid.value())).collect(Collectors.toUnmodifiableSet());
    }

}