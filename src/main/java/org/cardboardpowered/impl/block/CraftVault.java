package org.cardboardpowered.impl.block;

import net.minecraft.block.entity.VaultBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;
// import org.bukkit.block.Vault;

// TODO: Update Paper API
public class CraftVault extends CardboardBlockEntityState<VaultBlockEntity>
// implements Vault
{

    public CraftVault(World world, VaultBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CraftVault(CraftVault state, Location location) {
        super(state, location);
    }

    @Override
    public CraftVault copy() {
        return new CraftVault(this, null);
    }

    @Override
    public CraftVault copy(Location location) {
        return new CraftVault(this, location);
    }

}