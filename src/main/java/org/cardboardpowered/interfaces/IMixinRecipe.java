/**
 * Cardboard - Paper API for Fabric
 * Copyright (C) 2020-2025
 */
package org.cardboardpowered.interfaces;

import org.bukkit.inventory.Recipe;

public interface IMixinRecipe {

    Recipe toBukkitRecipe();

}