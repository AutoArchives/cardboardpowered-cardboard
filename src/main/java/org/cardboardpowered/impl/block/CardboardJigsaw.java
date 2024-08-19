package org.cardboardpowered.impl.block;

import net.minecraft.block.entity.JigsawBlockEntity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Jigsaw;

public class CardboardJigsaw extends CardboardBlockEntityState<JigsawBlockEntity> implements Jigsaw {

    public CardboardJigsaw(World world, JigsawBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CardboardJigsaw(CardboardJigsaw state, Location location) {
        super(state, location);
    }

    @Override
    public CardboardJigsaw copy() {
        return new CardboardJigsaw(this, null);
    }

    @Override
    public CardboardJigsaw copy(Location location) {
        return new CardboardJigsaw(this, location);
    }

}