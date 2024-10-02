package org.bukkit.craftbukkit.inventory.components;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.component.type.ToolComponent;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.craftbukkit.block.CraftBlockType;
import org.bukkit.craftbukkit.inventory.SerializableMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


// import org.bukkit.craftbukkit.tag.CraftBlockTag; 

@SerializableAs(value="Tool")
public final class CraftToolComponent
implements org.bukkit.inventory.meta.components.ToolComponent {

	@Override
	public @NotNull Map<String, Object> serialize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getDefaultMiningSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setDefaultMiningSpeed(float speed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getDamagePerBlock() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setDamagePerBlock(int damage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public @NotNull List<ToolRule> getRules() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRules(@NotNull List<ToolRule> rules) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public @NotNull ToolRule addRule(@NotNull Material block, @Nullable Float speed,
			@Nullable Boolean correctForDrops) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull ToolRule addRule(@NotNull Collection<Material> blocks, @Nullable Float speed,
			@Nullable Boolean correctForDrops) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull ToolRule addRule(@NotNull Tag<Material> tag, @Nullable Float speed,
			@Nullable Boolean correctForDrops) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeRule(@NotNull ToolRule rule) {
		// TODO Auto-generated method stub
		return false;
	}

}

