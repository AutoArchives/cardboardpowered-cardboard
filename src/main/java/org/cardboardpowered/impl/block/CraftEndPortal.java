package org.cardboardpowered.impl.block;

import net.minecraft.block.entity.EndPortalBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;

public class CraftEndPortal extends CardboardBlockEntityState<EndPortalBlockEntity> {

    public CraftEndPortal(World world, EndPortalBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CraftEndPortal(CraftEndPortal state, Location location) {
        super(state, location);
    }

    @Override
    public CraftEndPortal copy() {
        return new CraftEndPortal(this, null);
    }

    @Override
    public CraftEndPortal copy(Location location) {
        return new CraftEndPortal(this, location);
    }

}