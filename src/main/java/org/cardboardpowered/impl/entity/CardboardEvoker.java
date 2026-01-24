package org.cardboardpowered.impl.entity;

import net.minecraft.world.entity.monster.illager.SpellcasterIllager;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.Sheep;

public class CardboardEvoker extends CardboardSpellcaster implements Evoker {

    public CardboardEvoker(CraftServer server, net.minecraft.world.entity.monster.illager.Evoker entity) {
        super(server, entity);
    }

    @Override
    public net.minecraft.world.entity.monster.illager.Evoker getHandle() {
        return (net.minecraft.world.entity.monster.illager.Evoker) super.getHandle();
    }

    @Override
    public String toString() {
        return "Evoker";
    }

    @Override
    public EntityType getType() {
        return EntityType.EVOKER;
    }

    @Override
    public Evoker.Spell getCurrentSpell() {
        return Evoker.Spell.values()[getHandle().getCurrentSpell().ordinal()];
    }

    @Override
    public void setCurrentSpell(Evoker.Spell spell) {
        getHandle().setIsCastingSpell(spell == null ? SpellcasterIllager.IllagerSpell.NONE : SpellcasterIllager.IllagerSpell.byId(spell.ordinal()));
    }

    @Override
    public Sheep getWololoTarget() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setWololoTarget(Sheep arg0) {
        // TODO Auto-generated method stub
    }

}