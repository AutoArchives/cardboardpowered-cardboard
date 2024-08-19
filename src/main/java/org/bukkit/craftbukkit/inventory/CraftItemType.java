package org.bukkit.craftbukkit.inventory;

import org.bukkit.Material;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;

import net.minecraft.item.Item;

public class CraftItemType {

    public static Material minecraftToBukkit(Item item) {
        return CraftMagicNumbers.getMaterial(item);
    }

    public static Item bukkitToMinecraft(Material material) {
        return CraftMagicNumbers.getItem(material);
    }
	
}
