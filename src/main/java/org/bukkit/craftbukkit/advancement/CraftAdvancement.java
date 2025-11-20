package org.bukkit.craftbukkit.advancement;

import io.papermc.paper.advancement.AdvancementDisplay;
import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlacedAdvancement;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementRequirements;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.cardboardpowered.interfaces.IMixinAdvancement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import com.google.common.collect.ImmutableList;

/**
 * Cardboard Implementation of {@link org.bukkit.advancement.Advancement}
 */
public class CraftAdvancement implements org.bukkit.advancement.Advancement {

	/**
	 * Convenience method 
	 */
	private static ServerAdvancementLoader cardboard$getAdvancements() {
		return CraftServer.server.getAdvancementLoader();
	}
	
    private final AdvancementEntry handle;

    public CraftAdvancement(AdvancementEntry handle) {
        this.handle = handle;
    }

    public AdvancementEntry getHandle() {
        return handle;
    }

    @Override
    public NamespacedKey getKey() {
        return CraftNamespacedKey.fromMinecraft(handle.id());
    }

    @Override
    public Collection<String> getCriteria() {
        return Collections.unmodifiableCollection(handle.value().criteria().keySet());
    }

    @Override
    public @NotNull @Unmodifiable Collection<org.bukkit.advancement.Advancement> getChildren() {
    	ImmutableList.Builder<Advancement> children = ImmutableList.builder();
    	PlacedAdvancement advancementNode = cardboard$getAdvancements().getManager().get(this.handle);
    	if (advancementNode != null) {
    		for (PlacedAdvancement child : advancementNode.getChildren()) {
    			AdvancementEntry entry = child.getAdvancementEntry();
    			Advancement cardboard = ((IMixinAdvancement) (Object) entry).toBukkit();
    			children.add(cardboard);
    		}
    	}

    	return children.build();
    }

    @Override
    public @Nullable AdvancementDisplay getDisplay() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public org.bukkit.advancement.@Nullable Advancement getParent() {
    	Optional<AdvancementEntry> entry = this.handle.value().parent().map(cardboard$getAdvancements()::get);
    	if (entry.isEmpty()) {
    		return null;
    	}
    	return ((IMixinAdvancement) (Object) entry.get()).toBukkit();
    }

    @Override
    public org.bukkit.advancement.@NotNull Advancement getRoot() {

    	PlacedAdvancement node = cardboard$getAdvancements().getManager().get(this.handle);
    	
    	Objects.requireNonNull(node, "Could not find Advancement node! " + this.handle.id());
    	
    	AdvancementEntry entry = node.getRoot().getAdvancementEntry();
    	Advancement cardboard = ((IMixinAdvancement) (Object) entry).toBukkit();
        return cardboard;
    }

	@Override
	public @NotNull Component displayName() {
		Text name = net.minecraft.advancement.Advancement.getNameFromIdentity(this.handle);
		Component paperName = PaperAdventure.asAdventure(name);
		
		return paperName;
	}

	@Override
	public AdvancementRequirements getRequirements() {
        return new CraftAdvancementRequirements(this.handle.value().requirements());
    }

}

