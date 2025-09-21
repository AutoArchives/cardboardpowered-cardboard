package io.papermc.paper.datacomponent.item.attribute;


import io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay;
import io.papermc.paper.datacomponent.item.attribute.PaperDefaultDisplay;
import io.papermc.paper.datacomponent.item.attribute.PaperHiddenDisplay;
import io.papermc.paper.datacomponent.item.attribute.PaperOverrideTextDisplay;
import java.lang.runtime.SwitchBootstraps;
import java.util.Objects;
import net.minecraft.component.type.AttributeModifiersComponent;
import org.bukkit.craftbukkit.util.Handleable;

public interface PaperAttributeModifierDisplay<T extends AttributeModifiersComponent.Display>
extends Handleable<T> {
	
	static AttributeModifierDisplay fromNms(AttributeModifiersComponent.Display display) {
        return switch (display) {
            case AttributeModifiersComponent.Display.Default def -> new PaperDefaultDisplay(def);
            case AttributeModifiersComponent.Display.Hidden hidden -> new PaperHiddenDisplay(hidden);
            case AttributeModifiersComponent.Display.Override override -> new PaperOverrideTextDisplay(override);
            default -> throw new UnsupportedOperationException("We do not know how to convert " + display.getClass());
        };
    }

    public static AttributeModifiersComponent.Display toNms(AttributeModifierDisplay display) {
        if (display instanceof PaperAttributeModifierDisplay) {
            PaperAttributeModifierDisplay modifierDisplay = (PaperAttributeModifierDisplay)display;
            return (AttributeModifiersComponent.Display)modifierDisplay.getHandle();
        }
        throw new UnsupportedOperationException("Must implement handleable!");
    }
}

