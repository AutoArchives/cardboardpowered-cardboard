package org.cardboardpowered.impl.block;


import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.SculkShriekerBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.SculkShrieker;
import org.bukkit.entity.Player;
import org.cardboardpowered.impl.entity.CraftPlayer;

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
        ServerPlayer entityPlayer = player == null ? null : ((CraftPlayer)player).getHandle();
        ((SculkShriekerBlockEntity)this.getTileEntity()).tryShriek(this.world.getHandle(), entityPlayer);
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