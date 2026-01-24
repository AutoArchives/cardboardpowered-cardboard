package org.bukkit.craftbukkit.legacy;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Material;
import org.bukkit.craftbukkit.util.CraftLegacyMaterials;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.material.MaterialData;

public class CraftLegacy extends CraftLegacyMaterials {

	 public static MaterialData toLegacyData(Material material) {
	        return CraftLegacy.toLegacyData(material, false);
	    }

	    public static MaterialData toLegacyData(Material material, boolean itemPriority) {
	        // Preconditions.checkArgument(!material.isLegacy(), "toLegacy on legacy Material");
	        MaterialData mappedData = null;

	        if (itemPriority) {
	            Item item = CraftMagicNumbers.getItem(material);
	            mappedData = CraftLegacy.itemToMaterial.get(item);
	        }

	        if (mappedData == null && material.isBlock()) {
	            Block block = CraftMagicNumbers.getBlock(material);
	            BlockState blockData = block.defaultBlockState();

	            // Try exact match first
	            mappedData = CraftLegacy.dataToMaterial.get(blockData);
	            // Fallback to any block
	            if (mappedData == null) {
	                mappedData = CraftLegacy.blockToMaterial.get(block);
	                // Fallback to matching item
	                if (mappedData == null) {
	                    mappedData = CraftLegacy.itemToMaterial.get(block.asItem());
	                }
	            }
	        } else if (!itemPriority) {
	            Item item = CraftMagicNumbers.getItem(material);
	            mappedData = CraftLegacy.itemToMaterial.get(item);
	        }

	        return (mappedData == null) ? new MaterialData(Material.LEGACY_AIR) : mappedData;
	    }
	
}
