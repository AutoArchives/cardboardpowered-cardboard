package io.papermc.paper.datacomponent.item.attribute;

import net.minecraft.component.type.AttributeModifiersComponent;

public record PaperHiddenDisplay(AttributeModifiersComponent.Display.Hidden impl) implements AttributeModifierDisplay.Hidden,
PaperAttributeModifierDisplay<AttributeModifiersComponent.Display.Hidden> {
    @Override
    public AttributeModifiersComponent.Display.Hidden getHandle() {
        return this.impl;
    }
}

