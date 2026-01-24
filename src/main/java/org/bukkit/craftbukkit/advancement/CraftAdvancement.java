package org.bukkit.craftbukkit.advancement;

import io.papermc.paper.advancement.AdvancementDisplay;
import io.papermc.paper.adventure.PaperAdventure;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import net.kyori.adventure.text.Component;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.server.ServerAdvancementManager;
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
	private static ServerAdvancementManager cardboard$getAdvancements() {
		return CraftServer.server.getAdvancements();
	}
	
    private final AdvancementHolder handle;

    public CraftAdvancement(AdvancementHolder handle) {
        this.handle = handle;
    }

    public AdvancementHolder getHandle() {
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
    	AdvancementNode advancementNode = cardboard$getAdvancements().tree().get(this.handle);
    	if (advancementNode != null) {
    		for (AdvancementNode child : advancementNode.children()) {
    			AdvancementHolder entry = child.holder();
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
    	Optional<AdvancementHolder> entry = this.handle.value().parent().map(cardboard$getAdvancements()::get);
    	if (entry.isEmpty()) {
    		return null;
    	}
    	return ((IMixinAdvancement) (Object) entry.get()).toBukkit();
    }

    @Override
    public org.bukkit.advancement.@NotNull Advancement getRoot() {

    	AdvancementNode node = cardboard$getAdvancements().tree().get(this.handle);
    	
    	Objects.requireNonNull(node, "Could not find Advancement node! " + this.handle.id());
    	
    	AdvancementHolder entry = node.root().holder();
    	Advancement cardboard = ((IMixinAdvancement) (Object) entry).toBukkit();
        return cardboard;
    }

	@Override
	public @NotNull Component displayName() {
		net.minecraft.network.chat.Component name = net.minecraft.advancements.Advancement.name(this.handle);
		Component paperName = PaperAdventure.asAdventure(name);
		
		return paperName;
	}

	@Override
	public AdvancementRequirements getRequirements() {
        return new CraftAdvancementRequirements(this.handle.value().requirements());
    }

}

