package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;
import org.bukkit.ServerTickManager;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;

final class CraftServerTickManager implements ServerTickManager {

    private final net.minecraft.server.ServerTickManager manager;

    protected CraftServerTickManager(net.minecraft.server.ServerTickManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean isRunningNormally() {
        return this.manager.shouldTick();
    }

    @Override
    public boolean isStepping() {
        return this.manager.isStepping();
    }

    @Override
    public boolean isSprinting() {
        return this.manager.isSprinting();
    }

    @Override
    public boolean isFrozen() {
        return this.manager.isFrozen();
    }

    @Override
    public float getTickRate() {
        return this.manager.getTickRate();
    }

    @Override
    public void setTickRate(final float tickRate) {
        Preconditions.checkArgument(tickRate >= 1.0F && tickRate <= 10_000.0F, "tick rate must not be less than 1.0 or greater than 10,000.0");
        this.manager.setTickRate(tickRate);
    }

    @Override
    public void setFrozen(final boolean frozen) {
        if (frozen) {
            if (this.manager.isSprinting()) {
                this.manager.stopSprinting();
            }

            if (this.manager.isStepping()) {
                this.manager.stopStepping();
            }
        }

        this.manager.setFrozen(frozen);
    }

    @Override
    public boolean stepGameIfFrozen(final int ticks) {
        return this.manager.step(ticks);
    }

    @Override
    public boolean stopStepping() {
        return this.manager.stopStepping();
    }

    @Override
    public boolean requestGameToSprint(final int ticks) {
        return this.manager.startSprint(ticks);
    }

    @Override
    public boolean stopSprinting() {
        return this.manager.stopSprinting();
    }

    @Override
    public boolean isFrozen(final Entity entity) {
        Preconditions.checkArgument(entity != null, "entity must not be null");
        return this.manager.shouldSkipTick(((CraftEntity) entity).getHandle());
    }

    @Override
    public int getFrozenTicksToRun() {
        return this.manager.getStepTicks();
    }

}