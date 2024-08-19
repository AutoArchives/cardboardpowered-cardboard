package org.cardboardpowered.impl.block;

import net.minecraft.block.entity.SculkSensorBlockEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.SculkSensor;

public class CraftSculkSensor<T extends SculkSensorBlockEntity> extends CardboardBlockEntityState<T> implements SculkSensor {

    public CraftSculkSensor(World world, T tileEntity) {
        super(world, tileEntity);
    }

    protected CraftSculkSensor(CraftSculkSensor<T> state, Location location) {
        super(state, location);
    }

    public int getLastVibrationFrequency() {
        return ((SculkSensorBlockEntity)this.getSnapshot()).getLastVibrationFrequency();
    }

    public void setLastVibrationFrequency(int lastVibrationFrequency) {
        // TODO (SculkSensorBlockEntity)this.getSnapshot()).lastVibrationFrequency = lastVibrationFrequency;
    }

    @Override
    public CraftSculkSensor<T> copy() {
        return new CraftSculkSensor<T>(this, null);
    }

    @Override
    public CraftSculkSensor<T> copy(Location location) {
        return new CraftSculkSensor<T>(this, location);
    }

    public int getListenerRange() {
        return ((SculkSensorBlockEntity)this.getSnapshot()).getEventListener().getRange();
    }

    public void setListenerRange(int range) {
        // TODO ((SculkSensorBlockEntity)this.getSnapshot()).rangeOverride = range;
    }

}
