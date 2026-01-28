package org.cardboardpowered.impl.entity;

import com.google.common.base.Preconditions;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftAnimals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Panda;

public class CardboardPanda extends CraftAnimals implements Panda {

    public CardboardPanda(CraftServer server, net.minecraft.world.entity.animal.panda.Panda entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.animal.panda.Panda getHandle() {
        return (net.minecraft.world.entity.animal.panda.Panda) super.getHandle();
    }

    @Override
    public EntityType getType() {
        return EntityType.PANDA;
    }

    @Override
    public String toString() {
        return "Panda";
    }

    @Override
    public Gene getMainGene() {
        return fromNms(getHandle().getMainGene());
    }

    @Override
    public void setMainGene(Gene gene) {
        getHandle().setMainGene(toNms(gene));
    }

    @Override
    public Gene getHiddenGene() {
        return fromNms(getHandle().getHiddenGene());
    }

    @Override
    public void setHiddenGene(Gene gene) {
        getHandle().setHiddenGene(toNms(gene));
    }

    public static Gene fromNms(net.minecraft.world.entity.animal.panda.Panda.Gene gene) {
        Preconditions.checkArgument(gene != null, "Gene must not be null");
        return Gene.values()[gene.ordinal()];
    }

    public static net.minecraft.world.entity.animal.panda.Panda.Gene toNms(Gene gene) {
        Preconditions.checkArgument(gene != null, "Gene must not be null");
        return net.minecraft.world.entity.animal.panda.Panda.Gene.values()[gene.ordinal()];
    }

	@Override
	public int getEatingTicks() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSneezeTicks() {
		return this.getHandle().getSneezeCounter();
	}

	@Override
	public int getUnhappyTicks() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isOnBack() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRolling() {
		return this.getHandle().isRolling();
	}

	@Override
	public boolean isSitting() {
		return this.getHandle().isSitting();
	}

	@Override
	public boolean isSneezing() {
		return this.getHandle().isSneezing();
	}

	@Override
	public void setEatingTicks(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setIsOnBack(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRolling(boolean arg0) {
		this.getHandle().roll(arg0);
	}

	@Override
	public void setSitting(boolean arg0) {
		this.getHandle().sit(arg0);
	}

	@Override
	public void setSneezeTicks(int arg0) {
		this.getHandle().setSneezeCounter(arg0);
	}

	@Override
	public void setSneezing(boolean arg0) {
		this.getHandle().sneeze(arg0);
	}

	@Override
	public void setUnhappyTicks(int arg0) {
		this.getHandle().setUnhappyCounter(arg0);
	}
	
	// 1.19.2:

	@Override
	public boolean isEating() {
		return this.getHandle().isEating();
	}

	@Override
	public boolean isScared() {
		return this.getHandle().isScared();
	}

	@Override
	public void setEating(boolean arg0) {
		this.getHandle().eat(arg0);
	}

	@Override
	public void setOnBack(boolean arg0) {
		this.getHandle().setOnBack(arg0);
	}

	// 1.20.2 API:
	
	@Override
	public Panda.Gene getCombinedGene() {
        return CardboardPanda.fromNms(this.getHandle().getVariant());
    }

}
