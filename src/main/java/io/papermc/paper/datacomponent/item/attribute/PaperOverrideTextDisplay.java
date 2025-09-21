package io.papermc.paper.datacomponent.item.attribute;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.component.type.AttributeModifiersComponent;

public record PaperOverrideTextDisplay(AttributeModifiersComponent.Display.Override impl) implements AttributeModifierDisplay.OverrideText,
PaperAttributeModifierDisplay<AttributeModifiersComponent.Display.Override> {

    public Component text() {
        return PaperAdventure.asAdventure(this.impl.value());
    }

    @Override
    public AttributeModifiersComponent.Display.Override getHandle() {
        return this.impl;
    }

}