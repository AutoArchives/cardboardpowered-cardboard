package io.papermc.paper.datacomponent.item.attribute;

import net.minecraft.component.type.AttributeModifiersComponent;

public record PaperDefaultDisplay(AttributeModifiersComponent.Display.Default impl) implements AttributeModifierDisplay.Default,
PaperAttributeModifierDisplay<AttributeModifiersComponent.Display.Default>
{
    @Override
    public AttributeModifiersComponent.Display.Default getHandle() {
        return this.impl;
    }
}

