package io.papermc.paper.adventure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.cardboardpowered.adventure.CardboardAdventure;
import org.cardboardpowered.adventure.CardboardAdventureComponent;
import org.cardboardpowered.adventure.NBTLegacyHoverEventSerializer;
import org.cardboardpowered.adventure.WrapperAwareSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.intellij.lang.annotations.Subst;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.JavaOps;

import io.netty.util.AttributeKey;
import me.isaiah.common.cmixin.IMixinMinecraftServer;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.DataComponentValue;
import net.kyori.adventure.text.event.DataComponentValueConverterRegistry;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.util.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.network.Filterable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.nbt.Tag;

// Paper Adventure
public class PaperAdventure {

	public static final AttributeKey<Locale> LOCALE_ATTRIBUTE = AttributeKey.valueOf("adventure:locale");
    private static final Pattern LOCALIZATION_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?s");
    public static final ComponentFlattener FLATTENER = ComponentFlattener.basic().toBuilder()
        .complexMapper(TranslatableComponent.class, (translatable, consumer) -> {
            final @NonNull String translated = Language.getInstance().getOrDefault(translatable.key());

            final Matcher matcher = LOCALIZATION_PATTERN.matcher(translated);
            final List<Component> args = translatable.args();
            int argPosition = 0;
            int lastIdx = 0;
            while (matcher.find()) {
                // append prior
                if (lastIdx < matcher.start()) {
                    consumer.accept(Component.text(translated.substring(lastIdx, matcher.start())));
                }
                lastIdx = matcher.end();

                final @Nullable String argIdx = matcher.group(1);
                // calculate argument position
                if (argIdx != null) {
                    try {
                        final int idx = Integer.parseInt(argIdx) - 1;
                        if (idx < args.size()) {
                            consumer.accept(args.get(idx));
                        }
                    } catch (final NumberFormatException ex) {
                        // ignore, drop the format placeholder
                    }
                } else {
                    final int idx = argPosition++;
                    if (idx < args.size()) {
                        consumer.accept(args.get(idx));
                    }
                }
            }

            // append tail
            if (lastIdx < translated.length()) {
                consumer.accept(Component.text(translated.substring(lastIdx)));
            }
        })
        .build();
    public static final LegacyComponentSerializer LEGACY_SECTION_UXRC = LegacyComponentSerializer.builder().flattener(FLATTENER).hexColors().useUnusualXRepeatedCharacterHexFormat().build();
    public static final PlainComponentSerializer PLAIN = PlainComponentSerializer.builder().flattener(FLATTENER).build();
    public static final GsonComponentSerializer GSON = GsonComponentSerializer.builder()
        .legacyHoverEventSerializer(NBTLegacyHoverEventSerializer.INSTANCE)
        .build();
    public static final GsonComponentSerializer COLOR_DOWNSAMPLING_GSON = GsonComponentSerializer.builder()
        .legacyHoverEventSerializer(NBTLegacyHoverEventSerializer.INSTANCE)
        .downsampleColors()
        .build();
    
    private static final TagParser<Tag> NBT_PARSER = TagParser.create(NbtOps.INSTANCE);
    
    public static final Codec<Tag, String, CommandSyntaxException, RuntimeException> NBT_CODEC = new Codec<>() {
        @Override
        public Tag decode(final String encoded) throws CommandSyntaxException {
            return NBT_PARSER.parseFully(encoded);
        }

        @Override
        public String encode(final Tag decoded) {
            return decoded.toString();
        }
    };
    
    /*
    public static final Codec<CompoundTag, String, IOException, IOException> NBT_CODEC = new Codec<CompoundTag, String, IOException, IOException>() {
        @Override
        public @NonNull CompoundTag decode(final @NonNull String encoded) throws IOException {
            try {
                return TagParser.parseCompoundFully(encoded);
            } catch (final CommandSyntaxException e) {
                throw new IOException(e);
            }
        }

        @Override
        public @NonNull String encode(final @NonNull CompoundTag decoded) {
            return decoded.toString();
        }
    };
    */

    //static final WrapperAwareSerializer WRAPPER_AWARE_SERIALIZER = new WrapperAwareSerializer();
    public static final ComponentSerializer<Component, Component, net.minecraft.network.chat.Component> WRAPPER_AWARE_SERIALIZER = new WrapperAwareSerializer(() -> CraftRegistry.getMinecraftRegistry().createSerializationContext(JavaOps.INSTANCE));

    
    public PaperAdventure() {
    }

    // Key

    public static Identifier asVanilla(final Key key) {
        return Identifier.fromNamespaceAndPath(key.namespace(), key.value());
    }

    public static Identifier asVanillaNullable(final Key key) {
        if (key == null) {
            return null;
        }
        return Identifier.fromNamespaceAndPath(key.namespace(), key.value());
    }

    // Component

    public static Component asAdventure(final net.minecraft.network.chat.Component component) {
        return component == null ? Component.empty() : WRAPPER_AWARE_SERIALIZER.deserialize(component);
    	// return component == null ? Component.empty() : GSON.serializer().fromJson(Text.Serialization.toJsonTree(component), Component.class);
    }

    public static ArrayList<Component> asAdventure(final List<net.minecraft.network.chat.Component> vanillas) {
        final ArrayList<Component> adventures = new ArrayList<>(vanillas.size());
        for (final net.minecraft.network.chat.Component vanilla : vanillas) {
            adventures.add(asAdventure(vanilla));
        }
        return adventures;
    }
    
    public static List<Component> asAdventure(final net.minecraft.network.chat.Component[] vanillas) {
        final List<Component> adventures = new ArrayList<>(vanillas.length);
        for (final net.minecraft.network.chat.Component vanilla : vanillas) {
            adventures.add(asAdventure(vanilla));
        }
        return adventures;
    }

    public static ArrayList<Component> asAdventureFromJson(final List<String> jsonStrings) {
        final ArrayList<Component> adventures = new ArrayList<>(jsonStrings.size());
        for (final String json : jsonStrings) {
            adventures.add(GsonComponentSerializer.gson().deserialize(json));
        }
        return adventures;
    }

    public static List<String> asJson(final List<Component> adventures) {
        final List<String> jsons = new ArrayList<>(adventures.size());
        for (final Component component : adventures) {
            jsons.add(GsonComponentSerializer.gson().serialize(component));
        }
        return jsons;
    }

    public static net.minecraft.network.chat.Component asVanilla(final Component component) {
        if (true) return new CardboardAdventureComponent(component);
        return ((IMixinMinecraftServer)CraftServer.server).IC$from_json(String.valueOf(GSON.serializer().toJsonTree(component)));
    }

    public static List<net.minecraft.network.chat.Component> asVanilla(final List<Component> adventures) {
        final List<net.minecraft.network.chat.Component> vanillas = new ArrayList<>(adventures.size());
        for (final Component adventure : adventures) {
            vanillas.add(asVanilla(adventure));
        }
        return vanillas;
    }

    public static String asJsonString(final Component component, final Locale locale) {
        return GSON.serialize(
            GlobalTranslator.render(
                component,
                // play it safe
                locale != null
                    ? locale
                    : Locale.US
            )
        );
    }

    public static String asJsonString(final net.minecraft.network.chat.Component component, final Locale locale) {
        if ((Object)component instanceof CardboardAdventureComponent) {
            return asJsonString(((CardboardAdventureComponent)(Object) component).adventure, locale);
        }
        return ((IMixinMinecraftServer)CraftServer.server).IC$to_json(component);
        //return Text.Serialization.toJsonString(component);
    }

    // thank you for being worse than wet socks, Bukkit
    public static String superHackyLegacyRepresentationOfComponent(final Component component, final String string) {
        return LEGACY_SECTION_UXRC.serialize(component) + ChatColor.getLastColors(string);
    }

    // BossBar

    public static net.minecraft.world.BossEvent.BossBarColor asVanilla(final BossBar.Color color) {
        if (color == BossBar.Color.PINK) {
            return net.minecraft.world.BossEvent.BossBarColor.PINK;
        } else if (color == BossBar.Color.BLUE) {
            return net.minecraft.world.BossEvent.BossBarColor.BLUE;
        } else if (color == BossBar.Color.RED) {
            return net.minecraft.world.BossEvent.BossBarColor.RED;
        } else if (color == BossBar.Color.GREEN) {
            return net.minecraft.world.BossEvent.BossBarColor.GREEN;
        } else if (color == BossBar.Color.YELLOW) {
            return net.minecraft.world.BossEvent.BossBarColor.YELLOW;
        } else if (color == BossBar.Color.PURPLE) {
            return net.minecraft.world.BossEvent.BossBarColor.PURPLE;
        } else if (color == BossBar.Color.WHITE) {
            return net.minecraft.world.BossEvent.BossBarColor.WHITE;
        }
        throw new IllegalArgumentException(color.name());
    }

    public static BossBar.Color asAdventure(final net.minecraft.world.BossEvent.BossBarColor color) {
        if(color == net.minecraft.world.BossEvent.BossBarColor.PINK) {
            return BossBar.Color.PINK;
        } else if(color == net.minecraft.world.BossEvent.BossBarColor.BLUE) {
            return BossBar.Color.BLUE;
        } else if(color == net.minecraft.world.BossEvent.BossBarColor.RED) {
            return BossBar.Color.RED;
        } else if(color == net.minecraft.world.BossEvent.BossBarColor.GREEN) {
            return BossBar.Color.GREEN;
        } else if(color == net.minecraft.world.BossEvent.BossBarColor.YELLOW) {
            return BossBar.Color.YELLOW;
        } else if(color == net.minecraft.world.BossEvent.BossBarColor.PURPLE) {
            return BossBar.Color.PURPLE;
        } else if(color == net.minecraft.world.BossEvent.BossBarColor.WHITE) {
            return BossBar.Color.WHITE;
        }
        throw new IllegalArgumentException(color.name());
    }

    public static net.minecraft.world.BossEvent.BossBarOverlay asVanilla(final BossBar.Overlay overlay) {
        if (overlay == BossBar.Overlay.PROGRESS) {
            return net.minecraft.world.BossEvent.BossBarOverlay.PROGRESS;
        } else if (overlay == BossBar.Overlay.NOTCHED_6) {
            return net.minecraft.world.BossEvent.BossBarOverlay.NOTCHED_6;
        } else if (overlay == BossBar.Overlay.NOTCHED_10) {
            return net.minecraft.world.BossEvent.BossBarOverlay.NOTCHED_10;
        } else if (overlay == BossBar.Overlay.NOTCHED_12) {
            return net.minecraft.world.BossEvent.BossBarOverlay.NOTCHED_12;
        } else if (overlay == BossBar.Overlay.NOTCHED_20) {
            return net.minecraft.world.BossEvent.BossBarOverlay.NOTCHED_20;
        }
        throw new IllegalArgumentException(overlay.name());
    }

    public static BossBar.Overlay asAdventure(final net.minecraft.world.BossEvent.BossBarOverlay overlay) {
        if (overlay == net.minecraft.world.BossEvent.BossBarOverlay.PROGRESS) {
            return BossBar.Overlay.PROGRESS;
        } else if (overlay == net.minecraft.world.BossEvent.BossBarOverlay.NOTCHED_6) {
            return BossBar.Overlay.NOTCHED_6;
        } else if (overlay == net.minecraft.world.BossEvent.BossBarOverlay.NOTCHED_10) {
            return BossBar.Overlay.NOTCHED_10;
        } else if (overlay == net.minecraft.world.BossEvent.BossBarOverlay.NOTCHED_12) {
            return BossBar.Overlay.NOTCHED_12;
        } else if (overlay == net.minecraft.world.BossEvent.BossBarOverlay.NOTCHED_20) {
            return BossBar.Overlay.NOTCHED_20;
        }
        throw new IllegalArgumentException(overlay.name());
    }

    public static void setFlag(final BossBar bar, final BossBar.Flag flag, final boolean value) {
        if (value) {
            bar.addFlag(flag);
        } else {
            bar.removeFlag(flag);
        }
    }

    // Book

    /*public static ItemStack asItemStack_old(final Book book, final Locale locale) {
        final ItemStack item = new ItemStack(Items.WRITTEN_BOOK, 1);
        final NbtCompound tag = item.getOrCreateNbt();
        tag.putString("title", asJsonString(book.title(), locale));
        tag.putString("author", asJsonString(book.author(), locale));
        final NbtList pages = new NbtList();
        for (final Component page : book.pages()) {
            pages.add(NbtString.of(asJsonString(page, locale)));
        }
        tag.put("pages", pages);
        return item;
    }*/
    
    public static ItemStack asItemStack(final Book book, final Locale locale) {
    	        final ItemStack item = new ItemStack(Items.WRITTEN_BOOK, 1);
    	        item.set(DataComponents.WRITTEN_BOOK_CONTENT, new WrittenBookContent(
    	        		Filterable.passThrough(validateField(asPlain(book.title(), locale), WrittenBookContent.TITLE_MAX_LENGTH, "title")),
    	            asPlain(book.author(), locale),
    	            0,
    	            book.pages().stream().map(c -> Filterable.passThrough(PaperAdventure.asVanilla(c))).toList(), // TODO should we validate legnth?
    	            false
    	        ));
    	        return item;
    	    }
    
    public static String asPlain(final Component component, final Locale locale) {
           return PlainTextComponentSerializer.plainText().serialize(translated(component, locale));
    }
    
    private static Component translated(final Component component, final Locale locale) {
    	        //noinspection ConstantValue
    	        return GlobalTranslator.render(
    	            component,
    	            // play it safe
    	            locale != null
    	                ? locale
    	                : Locale.US
    	        );
    	    }
    
    private static String validateField(final String content, final int length, final String name) {
    	        final int actual = content.length();
    	        if (actual > length) {
    	            throw new IllegalArgumentException("Field '" + name + "' has a maximum length of " + length + " but was passed '" + content + "', which was " + actual + " characters long.");
    	        }
    	        return content;
    	    }

    // Sounds

    public static net.minecraft.sounds.SoundSource asVanilla(final Sound.Source source) {
        if (source == Sound.Source.MASTER) {
            return SoundSource.MASTER;
        } else if (source == Sound.Source.MUSIC) {
            return SoundSource.MUSIC;
        } else if (source == Sound.Source.RECORD) {
            return SoundSource.RECORDS;
        } else if (source == Sound.Source.WEATHER) {
            return SoundSource.WEATHER;
        } else if (source == Sound.Source.BLOCK) {
            return SoundSource.BLOCKS;
        } else if (source == Sound.Source.HOSTILE) {
            return SoundSource.HOSTILE;
        } else if (source == Sound.Source.NEUTRAL) {
            return SoundSource.NEUTRAL;
        } else if (source == Sound.Source.PLAYER) {
            return SoundSource.PLAYERS;
        } else if (source == Sound.Source.AMBIENT) {
            return SoundSource.AMBIENT;
        } else if (source == Sound.Source.VOICE) {
            return SoundSource.VOICE;
        }
        throw new IllegalArgumentException(source.name());
    }

    public static @Nullable SoundSource asVanillaNullable(final Sound.@Nullable Source source) {
        if (source == null) {
            return null;
        }
        return asVanilla(source);
    }

    // NBT

    public static @Nullable BinaryTagHolder asBinaryTagHolder(final @Nullable CompoundTag tag) {
        if (tag == null) {
            return null;
        }
        return BinaryTagHolder.encode(tag, NBT_CODEC);
    }

    // Colors

    public static @NonNull TextColor asAdventure(ChatFormatting minecraftColor) {
        if (minecraftColor.getColor() == null) {
            throw new IllegalArgumentException("Not a valid color");
        }
        return TextColor.color(minecraftColor.getColor());
    }

    public static @Nullable ChatFormatting asVanilla(TextColor color) {
        return ChatFormatting.getById(color.value());
    }
    
    public static Style asAdventure(final net.minecraft.network.chat.Style style) {
        final RegistryOps<Object> ops = CraftRegistry.getMinecraftRegistry().createSerializationContext(JavaOps.INSTANCE);
        final Object encoded = net.minecraft.network.chat.Style.Serializer.CODEC
            .encodeStart(ops, style).getOrThrow(IllegalStateException::new);

        return AdventureCodecs.STYLE_MAP_CODEC.codec()
            .parse(ops, encoded).getOrThrow(IllegalStateException::new);
    }

    public static net.minecraft.network.chat.Component asVanillaNullToEmpty(Component component) {
        if (component == null) {
            return CommonComponents.EMPTY;
        }
        return asVanilla(component);
    }

	public static Key asAdventure(Identifier key) {
        return Key.key((String)key.getNamespace(), (String)key.getPath());
    }
	
	public static Key asAdventureKey(ResourceKey<?> key) {
        return PaperAdventure.asAdventure(key.identifier());
    }
	
	public static Holder<SoundEvent> resolveSound(Key key) {
        Identifier id = PaperAdventure.asVanilla(key);
        Optional<Holder.Reference<SoundEvent>> vanilla = BuiltInRegistries.SOUND_EVENT.get(id);
        if (vanilla.isPresent()) {
            return vanilla.get();
        }
        return Holder.direct(SoundEvent.createVariableRangeEvent(id));
    }

	public static <T> ResourceKey<T> asVanilla(ResourceKey<? extends Registry<T>> registry, Key key) {
        return ResourceKey.create(registry, PaperAdventure.asVanilla(key));
    }

	// NBT

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map<Key, ? extends DataComponentValue> asAdventure(
        final DataComponentPatch patch
    ) {
        if (patch.isEmpty()) {
            return Collections.emptyMap();
        }
        final Map<Key, DataComponentValue> map = new HashMap<>();
        for (final Map.Entry<DataComponentType<?>, Optional<?>> entry : patch.entrySet()) {
            if (entry.getKey().isTransient()) continue;
            @Subst("key:value") final String typeKey = Objects.requireNonNull(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(entry.getKey())).toString();
            if (entry.getValue().isEmpty()) {
                   map.put(Key.key(typeKey), DataComponentValue.removed());
            } else {
                map.put(Key.key(typeKey), new DataComponentValueImpl(entry.getKey().codec(), entry.getValue().get()));
            }
        }
        return map;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static DataComponentPatch asVanilla(final Map<? extends Key, ? extends DataComponentValue> map) {
        if (map.isEmpty()) {
            return DataComponentPatch.EMPTY;
        }
        final DataComponentPatch.Builder builder = DataComponentPatch.builder();
        map.forEach((key, dataComponentValue) -> {
            final DataComponentType<?> type = Objects.requireNonNull(BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(asVanilla(key)));
            if (dataComponentValue instanceof DataComponentValue.Removed) {
                builder.remove(type);
                return;
            }
            final DataComponentValueImpl<?> converted = DataComponentValueConverterRegistry.convert(DataComponentValueImpl.class, key, dataComponentValue);
            builder.set((DataComponentType) type, (Object) converted.value());
        });
        return builder.build();
    }

    public record DataComponentValueImpl<T>(com.mojang.serialization.Codec<T> codec, T value) implements DataComponentValue.TagSerializable {

        @Override
        public BinaryTagHolder asBinaryTag() {
            return BinaryTagHolder.encode(this.codec.encodeStart(CraftRegistry.getMinecraftRegistry().createSerializationContext(NbtOps.INSTANCE), this.value).getOrThrow(IllegalArgumentException::new), NBT_CODEC);
        }
    }


}