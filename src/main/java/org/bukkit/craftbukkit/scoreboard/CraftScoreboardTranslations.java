package org.bukkit.craftbukkit.scoreboard;

import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.RenderType;

final class CardboardScoreboardTranslations {
    private CardboardScoreboardTranslations() {}

    static DisplaySlot toBukkitSlot(net.minecraft.world.scores.DisplaySlot slot) {
        return DisplaySlot.values()[slot.ordinal()];
    }

    static net.minecraft.world.scores.DisplaySlot fromBukkitSlot(DisplaySlot slot) {
        return net.minecraft.world.scores.DisplaySlot.values()[slot.ordinal()];
    }

    static RenderType toBukkitRender(ObjectiveCriteria.RenderType display) {
        return RenderType.valueOf(display.name());
    }

    static ObjectiveCriteria.RenderType fromBukkitRender(RenderType render) {
        return ObjectiveCriteria.RenderType.valueOf(render.name());
    }
}
