package org.cardboardpowered.impl.entity;

import com.google.common.base.Preconditions;
import net.minecraft.world.entity.monster.illager.SpellcasterIllager;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftIllager;
import org.bukkit.entity.Spellcaster;

public class CardboardSpellcaster extends CraftIllager implements Spellcaster {

    public CardboardSpellcaster(CraftServer server, SpellcasterIllager entity) {
        super(server, entity);
    }

    @Override
    public SpellcasterIllager getHandle() {
        return (SpellcasterIllager) super.getHandle();
    }

    @Override
    public String toString() {
        return "Spellcaster";
    }

    @Override
    public Spell getSpell() {
        return toBukkitSpell(getHandle().getCurrentSpell());
    }

    @Override
    public void setSpell(Spell spell) {
        Preconditions.checkArgument(spell != null, "Use Spell.NONE");
        getHandle().setIsCastingSpell(toNMSSpell(spell));
    }

    public static Spell toBukkitSpell(SpellcasterIllager.IllagerSpell spell) {
        return Spell.valueOf(spell.name());
    }

    public static SpellcasterIllager.IllagerSpell toNMSSpell(Spell spell) {
        return SpellcasterIllager.IllagerSpell.byId(spell.ordinal());
    }

}
