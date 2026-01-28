package org.cardboardpowered.bridge.world.entity.monster;

public interface CreeperBridge {

    void explodeBF();

    int getExplosionRadiusBF();

    void setExplosionRadiusBF(int radius);

    void setFuseTimeBF(int ticks);

    int getFuseTimeBF();

    void setPowered(boolean powered);

    boolean isPoweredBF();

}
