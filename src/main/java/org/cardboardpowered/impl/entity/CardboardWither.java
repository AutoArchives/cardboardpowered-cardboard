package org.cardboardpowered.impl.entity;

import net.minecraft.world.entity.boss.wither.WitherBoss;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wither;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CardboardWither extends CraftMonster implements Wither {

    private BossBar bossBar;

    public CardboardWither(CraftServer server, WitherBoss entity) {
        super(server, entity);
        // TODO if (entity.bossBar != null) this.bossBar = new CardboardBossBar(entity.bossBar);
    }

    @Override
    public WitherBoss getHandle() {
        return (WitherBoss) nms;
    }

    @Override
    public String toString() {
        return "Wither";
    }

    @Override
    public EntityType getType() {
        return EntityType.WITHER;
    }

    @Override
    public BossBar getBossBar() {
        return bossBar;
    }

    @Override
    public void rangedAttack(LivingEntity arg0, float arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setChargingAttack(boolean arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean canTravelThroughPortals() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getInvulnerableTicks() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isCharged() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setCanTravelThroughPortals(boolean arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setInvulnerableTicks(int arg0) {
        // TODO Auto-generated method stub
        
    }

	@Override
	public @Nullable LivingEntity getTarget(@NotNull Head arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTarget(@NotNull Head arg0, @Nullable LivingEntity arg1) {
		// TODO Auto-generated method stub
		
	}
	
	// 1.19.4:

	@Override
	public void enterInvulnerabilityPhase() {
        this.getHandle().makeInvulnerable();
	}

	@Override
	public int getInvulnerabilityTicks() {
        return this.getHandle().getInvulnerableTicks();
	}

	@Override
	public void setInvulnerabilityTicks(int arg0) {
        this.getHandle().setInvulnerableTicks(arg0);
	}

}