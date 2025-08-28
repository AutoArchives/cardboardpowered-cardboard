package io.papermc.paper.adventure.providers;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.IOException;
import java.util.UUID;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.json.LegacyHoverEventSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.util.Codec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.StringNbtReader;

public class NBTLegacyHoverEventSerializer
implements LegacyHoverEventSerializer {
    public static final NBTLegacyHoverEventSerializer INSTANCE = new NBTLegacyHoverEventSerializer();
    private static final Codec<NbtCompound, String, CommandSyntaxException, RuntimeException> SNBT_CODEC = Codec.codec(StringNbtReader::readCompound, NbtElement::toString);
    static final String ITEM_TYPE = "id";
    static final String ITEM_COUNT = "Count";
    static final String ITEM_TAG = "tag";
    static final String ENTITY_NAME = "name";
    static final String ENTITY_TYPE = "type";
    static final String ENTITY_ID = "id";

    protected NBTLegacyHoverEventSerializer() {
    }

    public HoverEvent.ShowItem deserializeShowItem(Component input) throws IOException {
        String raw = PlainTextComponentSerializer.plainText().serialize(input);
        try {
            NbtCompound contents = (NbtCompound)SNBT_CODEC.decode(raw);
            NbtCompound tag = contents.getCompoundOrEmpty(ITEM_TAG);
            String keyString = contents.getString("id", "");
            return HoverEvent.ShowItem.showItem(Key.key(keyString), (int)contents.getByte(ITEM_COUNT, (byte)1), tag.isEmpty() ? null : BinaryTagHolder.encode(tag, SNBT_CODEC));
        }
        catch (CommandSyntaxException ex) {
            throw new IOException(ex);
        }
    }

    public HoverEvent.ShowEntity deserializeShowEntity(Component input, Codec.Decoder<Component, String, ? extends RuntimeException> componentCodec) throws IOException {
        String raw = PlainTextComponentSerializer.plainText().serialize(input);
        try {
            NbtCompound contents = (NbtCompound)SNBT_CODEC.decode(raw);
            String keyString = contents.getString(ENTITY_TYPE, "");
            return HoverEvent.ShowEntity.showEntity((Key)Key.key((String)keyString), (UUID)UUID.fromString(contents.getString("id", "")), (Component)((Component)componentCodec.decode(contents.getString(ENTITY_NAME, ""))));
        }
        catch (CommandSyntaxException ex) {
            throw new IOException(ex);
        }
    }

    public Component serializeShowItem(HoverEvent.ShowItem input) throws IOException {
        NbtCompound tag = new NbtCompound();
        tag.putString("id", input.item().asString());
        tag.putByte(ITEM_COUNT, (byte)input.count());
        if (input.nbt() != null) {
            try {
                tag.put(ITEM_TAG, (NbtElement)input.nbt().get(SNBT_CODEC));
            }
            catch (CommandSyntaxException ex) {
                throw new IOException(ex);
            }
        }
        return Component.text((String)((String)SNBT_CODEC.encode(tag)));
    }

    public Component serializeShowEntity(HoverEvent.ShowEntity input, Codec.Encoder<Component, String, ? extends RuntimeException> componentCodec) {
        NbtCompound tag = new NbtCompound();
        tag.putString("id", input.id().toString());
        tag.putString(ENTITY_TYPE, input.type().asString());
        if (input.name() != null) {
            tag.putString(ENTITY_NAME, (String)componentCodec.encode(input.name()));
        }
        return Component.text((String)((String)SNBT_CODEC.encode(tag)));
    }
}

