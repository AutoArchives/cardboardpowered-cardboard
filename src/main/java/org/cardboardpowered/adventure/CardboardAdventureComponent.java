package org.cardboardpowered.adventure;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents.LiteralContents;
import net.minecraft.util.FormattedCharSequence;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;

public final class CardboardAdventureComponent implements net.minecraft.network.chat.Component {

    public final Component adventure;
    private net.minecraft.network.chat.@MonotonicNonNull Component vanilla;

    public CardboardAdventureComponent(Component adventure) {
        this.adventure = adventure;
    }

    public net.minecraft.network.chat.Component deepConverted() {
        net.minecraft.network.chat.Component vanilla = this.vanilla;
        if (vanilla == null) {
            this.vanilla = vanilla = CardboardAdventure.WRAPPER_AWARE_SERIALIZER.serialize(this.adventure);
        }
        return vanilla;
    }

    public @Nullable net.minecraft.network.chat.Component deepConvertedIfPresent() {
        return this.vanilla;
    }

    @Override
    public Style getStyle() {
        return this.deepConverted().getStyle();
    }

    @Override
    public ComponentContents getContents() {
        if (this.adventure instanceof TextComponent) {
            return new LiteralContents(((TextComponent)this.adventure).content());
        }
        return this.deepConverted().getContents();
    }

    @Override
    public String getString() {
        return PlainTextComponentSerializer.plainText().serialize(this.adventure);
    }

    @Override
    public List<net.minecraft.network.chat.Component> getSiblings() {
        return this.deepConverted().getSiblings();
    }

    @Override
    public MutableComponent plainCopy() {
        return this.deepConverted().plainCopy();
    }

    @Override
    public MutableComponent copy() {
        return this.deepConverted().copy();
    }

    @Override
    public FormattedCharSequence getVisualOrderText() {
        return this.deepConverted().getVisualOrderText();
    }

    public static class Serializer
    implements JsonSerializer<CardboardAdventureComponent> {
        public JsonElement serialize(CardboardAdventureComponent src, Type type, JsonSerializationContext context) {
            return GsonComponentSerializer.gson().serializer().toJsonTree((Object)src.adventure, Component.class);
        }
    }
}
