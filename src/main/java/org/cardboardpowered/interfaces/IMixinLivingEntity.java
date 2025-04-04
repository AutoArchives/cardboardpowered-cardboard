package org.cardboardpowered.interfaces;

import org.bukkit.craftbukkit.attribute.CraftAttributeMap;
import org.bukkit.event.entity.EntityPotionEffectEvent;

public interface IMixinLivingEntity {

    int getExpReward();

    void pushEffectCause(EntityPotionEffectEvent.Cause cause);

    CraftAttributeMap cardboard_getAttr();

}