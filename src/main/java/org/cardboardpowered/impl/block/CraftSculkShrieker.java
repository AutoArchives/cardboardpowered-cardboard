package org.cardboardpowered.impl.block;


import net.minecraft.block.entity.SculkShriekerBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.SculkShrieker;
import org.bukkit.entity.Player;
import org.cardboardpowered.impl.entity.PlayerImpl;

public class CraftSculkShrieker extends CardboardBlockEntityState<SculkShriekerBlockEntity> implements SculkShrieker {

    public CraftSculkShrieker(World world, SculkShriekerBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CraftSculkShrieker(CraftSculkShrieker state, Location location) {
        super(state, location);
    }

    public int getWarningLevel() {
        return 0;
    	// TODO return ((SculkShriekerBlockEntity)this.getSnapshot()).warningLevel;
    }

    public void setWarningLevel(int level) {
        // TODO ((SculkShriekerBlockEntity)this.getSnapshot()).warningLevel = level;
    }

    public void tryShriek(Player player) {
        this.requirePlaced();
        ServerPlayerEntity entityPlayer = player == null ? null : ((PlayerImpl)player).getHandle();
        ((SculkShriekerBlockEntity)this.getTileEntity()).shriek(this.world.getHandle(), entityPlayer);
    }

    @Override
    public CraftSculkShrieker copy() {
        return new CraftSculkShrieker(this, null);
    }

    @Override
    public CraftSculkShrieker copy(Location location) {
        return new CraftSculkShrieker(this, location);
    }

}