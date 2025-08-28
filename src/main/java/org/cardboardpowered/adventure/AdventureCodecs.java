package org.cardboardpowered.adventure;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.*;
import net.minecraft.text.KeybindTextContent;
import net.minecraft.text.ParsedSelector;
import net.minecraft.text.ScoreTextContent;
//import net.minecraft.text.Style;
import net.kyori.adventure.text.format.Style;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;

public final class AdventureCodecs {
    
	// public static final Codec<Component> COMPONENT_CODEC = Codec.recursive((String)"adventure Component", AdventureCodecs::createCodec);
 
	/*
    static final MapCodec<TextComponent> TEXT_COMPONENT_MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.STRING.fieldOf("text").forGetter(TextComponent::content)).apply(instance, Component::text));

    static final MapCodec<TranslatableComponent> TRANSLATABLE_COMPONENT_MAP_CODEC = RecordCodecBuilder.mapCodec(
    		instance ->
    			instance.group(
    					Codec.STRING.fieldOf("translate").forGetter(TranslatableComponent::key),
    					(App)Codec.STRING.lenientOptionalFieldOf("fallback")
    					.forGetter(AdventureCodecs.nullableGetter(TranslatableComponent::fallback)),
    					(App)ARG_CODEC.listOf().optionalFieldOf("with")
    						.forGetter(c2 -> c2.arguments().isEmpty() ? Optional.empty() : Optional.of(c2.arguments())))
    			.apply((Applicative)instance, (key, fallback, components) ->
    				Component.translatable(key, components.orElse(Collections.emptyList())).fallback(fallback.orElse(null))));
    static final MapCodec<KeybindComponent> KEYBIND_COMPONENT_MAP_CODEC = KeybindTextContent.CODEC.xmap(k -> Component.keybind((String)k.getKey()), k -> new KeybindTextContent(k.keybind()));
    static final MapCodec<ScoreComponent> SCORE_COMPONENT_INNER_MAP_CODEC = ScoreTextContent.INNER_CODEC.xmap(s -> Component.score((String)s.name().map(ParsedSelector::comp_3067, Function.identity()), (String)s.objective()), s -> new ScoreTextContent(ParsedSelector.parse(s.name()).map(Either::left).result().orElse(Either.right(s.name())), s.objective()));
    static final MapCodec<ScoreComponent> SCORE_COMPONENT_MAP_CODEC = SCORE_COMPONENT_INNER_MAP_CODEC.fieldOf("score");
    static final MapCodec<SelectorComponent> SELECTOR_COMPONENT_MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.STRING.fieldOf("selector").forGetter(SelectorComponent::pattern), (App)COMPONENT_CODEC.optionalFieldOf("separator").forGetter(AdventureCodecs.nullableGetter(SelectorComponent::separator))).apply((Applicative)instance, (selector, component) -> Component.selector((String)selector, (ComponentLike)component.orElse(null))));
    static final DataSourceType<StorageDataSource> STORAGE_DATA_SOURCE_TYPE = new DataSourceType(RecordCodecBuilder.mapCodec(instance -> instance.group((App)KEY_CODEC.fieldOf("storage").forGetter(StorageDataSource::storage)).apply((Applicative)instance, StorageDataSource::new)), "storage");
    static final DataSourceType<BlockDataSource> BLOCK_DATA_SOURCE_TYPE = new DataSourceType(RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.STRING.fieldOf("block").forGetter(BlockDataSource::posPattern)).apply((Applicative)instance, BlockDataSource::new)), "block");
    static final DataSourceType<EntityDataSource> ENTITY_DATA_SOURCE_TYPE = new DataSourceType(RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.STRING.fieldOf("entity").forGetter(EntityDataSource::selectorPattern)).apply((Applicative)instance, EntityDataSource::new)), "entity");
    static final MapCodec<NbtComponentDataSource> NBT_COMPONENT_DATA_SOURCE_CODEC = TextCodecs.dispatchingCodec((StringIdentifiable[])new DataSourceType[]{ENTITY_DATA_SOURCE_TYPE, BLOCK_DATA_SOURCE_TYPE, STORAGE_DATA_SOURCE_TYPE}, DataSourceType::codec, NbtComponentDataSource::type, (String)"source");

    public static final MapCodec<Style> STYLE_MAP_CODEC = RecordCodecBuilder.mapCodec(
    		instance -> instance.group((App)TEXT_COLOR_CODEC.optionalFieldOf("color")
    				.forGetter(AdventureCodecs.nullableGetter(Style::color)), (App)SHADOW_COLOR_CODEC.optionalFieldOf("shadow_color")
    				.forGetter(AdventureCodecs.nullableGetter(StyleGetter::shadowColor)), (App)Codec.BOOL.optionalFieldOf("bold")
    				.forGetter(AdventureCodecs.decorationGetter(TextDecoration.BOLD)), (App)Codec.BOOL.optionalFieldOf("italic")
    				.forGetter(AdventureCodecs.decorationGetter(TextDecoration.ITALIC)), (App)Codec.BOOL.optionalFieldOf("underlined")
    				.forGetter(AdventureCodecs.decorationGetter(TextDecoration.UNDERLINED)), (App)Codec.BOOL.optionalFieldOf("strikethrough").
    				forGetter(AdventureCodecs.decorationGetter(TextDecoration.STRIKETHROUGH)), (App)Codec.BOOL.optionalFieldOf("obfuscated")
    				.forGetter(AdventureCodecs.decorationGetter(TextDecoration.OBFUSCATED)), (App)CLICK_EVENT_CODEC.optionalFieldOf("click_event")
    				.forGetter(AdventureCodecs.nullableGetter(Style::clickEvent)), (App)HOVER_EVENT_CODEC.optionalFieldOf("hover_event")
    				.forGetter(AdventureCodecs.nullableGetter(Style::hoverEvent)), (App)Codec.STRING.optionalFieldOf("insertion")
    				.forGetter(AdventureCodecs.nullableGetter(Style::insertion)), (App)KEY_CODEC.optionalFieldOf("font")
    				.forGetter(AdventureCodecs.nullableGetter(Style::font)))
    		.apply((Applicative)instance, (textColor, shadowColor, bold, italic, underlined, strikethrough, obfuscated, clickEvent, hoverEvent, insertion, font) -> Style.style(builder -> {
        textColor.ifPresent(arg_0 -> ((Style.Builder)builder).color(arg_0));
        shadowColor.ifPresent(arg_0 -> ((Style.Builder)builder).shadowColor(arg_0));
        bold.ifPresent(AdventureCodecs.styleBooleanConsumer(builder, TextDecoration.BOLD));
        italic.ifPresent(AdventureCodecs.styleBooleanConsumer(builder, TextDecoration.ITALIC));
        underlined.ifPresent(AdventureCodecs.styleBooleanConsumer(builder, TextDecoration.UNDERLINED));
        strikethrough.ifPresent(AdventureCodecs.styleBooleanConsumer(builder, TextDecoration.STRIKETHROUGH));
        obfuscated.ifPresent(AdventureCodecs.styleBooleanConsumer(builder, TextDecoration.OBFUSCATED));
        clickEvent.ifPresent(arg_0 -> ((Style.Builder)builder).clickEvent(arg_0));
        hoverEvent.ifPresent(arg_0 -> ((Style.Builder)builder).hoverEvent(arg_0));
        insertion.ifPresent(arg_0 -> ((Style.Builder)builder).insertion(arg_0));
        font.ifPresent(arg_0 -> ((Style.Builder)builder).font(arg_0));
    })));
    
    record ComponentType<C extends Component>(MapCodec<C> codec, Predicate<Component> test, String id) implements StringIdentifiable {
        @Override
        public String asString() {
            return this.id;
        }
    }
	
	 static final ComponentType<TextComponent> PLAIN = new ComponentType<>(TEXT_COMPONENT_MAP_CODEC, TextComponent.class::isInstance, "text");
	    static final ComponentType<TranslatableComponent> TRANSLATABLE = new ComponentType<>(TRANSLATABLE_COMPONENT_MAP_CODEC, TranslatableComponent.class::isInstance, "translatable");
	    static final ComponentType<KeybindComponent> KEYBIND = new ComponentType<>(KEYBIND_COMPONENT_MAP_CODEC, KeybindComponent.class::isInstance, "keybind");
	    static final ComponentType<ScoreComponent> SCORE = new ComponentType<>(SCORE_COMPONENT_MAP_CODEC, ScoreComponent.class::isInstance, "score");
	    static final ComponentType<SelectorComponent> SELECTOR = new ComponentType<>(SELECTOR_COMPONENT_MAP_CODEC, SelectorComponent.class::isInstance, "selector");
	    static final ComponentType<NBTComponent<?, ?>> NBT = new ComponentType<>(NBT_COMPONENT_MAP_CODEC, NBTComponent.class::isInstance, "nbt");
	
	static Codec<Component> createCodec(Codec<Component> selfCodec) {
		
		ComponentType[] types = new ComponentType[]{PLAIN, TRANSLATABLE, KEYBIND, SCORE, SELECTOR, NBT};

        MapCodec<Component> legacyCodec = TextCodecs.dispatchingCodec(types, ComponentType::codec, component -> {
            for (final ComponentType<?> type : types) {
                if (type.test().test(component)) {
                    return type;
                }
            }
            throw new IllegalStateException("Unexpected component type " + component);
        }, "type");
        
        final Codec<Component> directCodec = RecordCodecBuilder.create((instance) -> {
            return instance.group(
                legacyCodec.forGetter(Function.identity()),
                Codecs.nonEmptyList(selfCodec.listOf()).optionalFieldOf("extra", List.of()).forGetter(Component::children),
                STYLE_MAP_CODEC.forGetter(Component::style)
            ).apply(instance, (component, children, style) -> {
                return component.style(style).children(children);
            });
        });
        
        //Codec directCodec = RecordCodecBuilder.create(instance ->
        //	instance.group((App)legacyCodec.forGetter(Function.identity()), (App)Codecs.nonEmptyList(selfCodec.listOf()).optionalFieldOf("extra", List.of()).forGetter(Component::children), (App)STYLE_MAP_CODEC.forGetter(Component::style)).apply((Applicative)instance, (component, children, style) -> component.style(style).children(children)));
        return Codec.either((Codec)Codec.either((Codec)Codec.STRING, Codecs.nonEmptyList(selfCodec.listOf())), (Codec)directCodec).xmap(stringOrListOrComponent -> stringOrListOrComponent.map(stringOrList -> stringOrList.map(Component::text, AdventureCodecs::createFromList), Function.identity()), text -> {
            String string = AdventureCodecs.tryCollapseToString(text);
            return string != null ? Either.left(Either.left(string)) : Either.right(text);
        });
    }
	
	static Function<Style, Optional<Boolean>> decorationGetter(TextDecoration decoration) {
        return style -> Optional.ofNullable(style.decoration(decoration) == TextDecoration.State.NOT_SET ? null : Boolean.valueOf(style.decoration(decoration) == TextDecoration.State.TRUE));
    }

    static <R, T> Function<R, Optional<T>> nullableGetter(Function<R, T> getter) {
        return style -> Optional.ofNullable(getter.apply(style));
    }
	
	public static String tryCollapseToString(Component component) {
        if (component instanceof TextComponent) {
            TextComponent textComponent = (TextComponent)component;
            if (component.children().isEmpty() && component.style().isEmpty()) {
                return textComponent.content();
            }
        }
        return null;
    }
    */
}

