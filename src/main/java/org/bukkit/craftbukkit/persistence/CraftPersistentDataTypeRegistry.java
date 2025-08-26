package org.bukkit.craftbukkit.persistence;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.primitives.Primitives;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.nbt.NbtShort;
import net.minecraft.nbt.NbtString;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataContainer;
import org.bukkit.persistence.ListPersistentDataType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public final class CraftPersistentDataTypeRegistry {
    private final Function<Class, TagAdapter> CREATE_ADAPTER = this::createAdapter;
    private final Map<Class, TagAdapter> adapters = new ConcurrentHashMap<Class, TagAdapter>();

    private <T> TagAdapter createAdapter(Class<T> type) {
        if (!Primitives.isWrapperType(type)) {
            type = Primitives.wrap(type);
        }
        if (Objects.equals(Byte.class, type)) {
            return this.createAdapter(Byte.class, NbtByte.class, (byte)1, NbtByte::of, NbtByte::value);
        }
        if (Objects.equals(Short.class, type)) {
            return this.createAdapter(Short.class, NbtShort.class, (byte)2, NbtShort::of, NbtShort::value);
        }
        if (Objects.equals(Integer.class, type)) {
            return this.createAdapter(Integer.class, NbtInt.class, (byte)3, NbtInt::of, NbtInt::value);
        }
        if (Objects.equals(Long.class, type)) {
            return this.createAdapter(Long.class, NbtLong.class, (byte)4, NbtLong::of, NbtLong::value);
        }
        if (Objects.equals(Float.class, type)) {
            return this.createAdapter(Float.class, NbtFloat.class, (byte)5, NbtFloat::of, NbtFloat::value);
        }
        if (Objects.equals(Double.class, type)) {
            return this.createAdapter(Double.class, NbtDouble.class, (byte)6, NbtDouble::of, NbtDouble::value);
        }
        if (Objects.equals(String.class, type)) {
            return this.createAdapter(String.class, NbtString.class, (byte)8, NbtString::of, NbtString::value);
        }
        if (Objects.equals(byte[].class, type)) {
            return this.createAdapter(byte[].class, NbtByteArray.class, (byte)7, array -> new NbtByteArray(Arrays.copyOf(array, ((byte[])array).length)), n -> Arrays.copyOf(n.getByteArray(), n.size()));
        }
        if (Objects.equals(int[].class, type)) {
            return this.createAdapter(int[].class, NbtIntArray.class, (byte)11, array -> new NbtIntArray(Arrays.copyOf(array, ((int[])array).length)), n -> Arrays.copyOf(n.getIntArray(), n.size()));
        }
        if (Objects.equals(long[].class, type)) {
            return this.createAdapter(long[].class, NbtLongArray.class, (byte)12, array -> new NbtLongArray(Arrays.copyOf(array, ((long[])array).length)), n -> Arrays.copyOf(n.getLongArray(), n.size()));
        }
        if (Objects.equals(PersistentDataContainer[].class, type)) {
            return this.createAdapter(PersistentDataContainer[].class, NbtList.class, (byte)9, containerArray -> {
                NbtList list = new NbtList();
                for (PersistentDataContainer persistentDataContainer : containerArray) {
                    list.add(((CraftPersistentDataContainer)persistentDataContainer).toTagCompound());
                }
                return list;
            }, tag -> {
                PersistentDataContainer[] containerArray = new CraftPersistentDataContainer[tag.size()];
                for (int i2 = 0; i2 < tag.size(); ++i2) {
                    CraftPersistentDataContainer container = new CraftPersistentDataContainer(this);
                    NbtCompound compound = tag.getCompoundOrEmpty(i2);
                    for (String key : compound.getKeys()) {
                        container.put(key, compound.get(key));
                    }
                    containerArray[i2] = container;
                }
                return containerArray;
            });
        }
        if (Objects.equals(PersistentDataContainer.class, type)) {
            return this.createAdapter(CraftPersistentDataContainer.class, NbtCompound.class, (byte)10, CraftPersistentDataContainer::toTagCompound, tag -> {
                CraftPersistentDataContainer container = new CraftPersistentDataContainer(this);
                for (String key : tag.getKeys()) {
                    container.put(key, tag.get(key));
                }
                return container;
            });
        }
        if (Objects.equals(List.class, type)) {
            return this.createAdapter(List.class, NbtList.class, (byte)9, this::constructList, this::extractList, this::matchesListTag);
        }
        throw new IllegalArgumentException("Could not find a valid TagAdapter implementation for the requested type " + type.getSimpleName());
    }

    private <T, Z extends NbtElement> TagAdapter<T, Z> createAdapter(Class<T> primitiveType, Class<Z> tagType, byte nmsTypeByte, Function<T, Z> builder, Function<Z, T> extractor) {
        return this.createAdapter(primitiveType, tagType, nmsTypeByte, (type, t) -> builder.apply(t), (type, z) -> extractor.apply(z), (type, t) -> tagType.isInstance(t));
    }

    private <T, Z extends NbtElement> TagAdapter<T, Z> createAdapter(Class<T> primitiveType, Class<Z> tagType, byte nmsTypeByte, BiFunction<PersistentDataType<T, ?>, T, Z> builder, BiFunction<PersistentDataType<T, ?>, Z, T> extractor, BiPredicate<PersistentDataType<T, ?>, NbtElement> matcher) {
        return new TagAdapter<T, Z>(primitiveType, tagType, nmsTypeByte, builder, extractor, matcher);
    }

    public <T> NbtElement wrap(PersistentDataType<T, ?> type, T value) {
        return this.getOrCreateAdapter(type).build(type, value);
    }

    public <T> boolean isInstanceOf(PersistentDataType<T, ?> type, NbtElement base) {
        return this.getOrCreateAdapter(type).isInstance(type, base);
    }

    @NotNull
    private <T, Z extends NbtElement> TagAdapter<T, Z> getOrCreateAdapter(@NotNull PersistentDataType<T, ?> type) {
        return this.adapters.computeIfAbsent(type.getPrimitiveType(), this.CREATE_ADAPTER);
    }

    public <T, Z extends NbtElement> T extract(PersistentDataType<T, ?> type, NbtElement tag) throws ClassCastException, IllegalArgumentException {
        Class<T> primitiveType = type.getPrimitiveType();
        TagAdapter<T, Z> adapter = this.getOrCreateAdapter(type);
        Preconditions.checkArgument(adapter.isInstance(type, tag), "The found tag instance (%s) cannot store %s", tag.getClass().getSimpleName(), primitiveType.getSimpleName());
        T foundValue = adapter.extract(type, tag);
        Preconditions.checkArgument(primitiveType.isInstance(foundValue), "The found object is of the type %s. Expected type %s", foundValue.getClass().getSimpleName(), primitiveType.getSimpleName());
        return primitiveType.cast(foundValue);
    }

    private <P, T extends List<P>> NbtList constructList(@NotNull PersistentDataType<T, ?> type, @NotNull List<P> list) {
        Preconditions.checkArgument((boolean)(type instanceof ListPersistentDataType), (String)"The passed list cannot be written to the PDC with a %s (expected a list data type)", (Object)type.getClass().getSimpleName());
        ListPersistentDataType listPersistentDataType = (ListPersistentDataType)type;
        ArrayList values = Lists.newArrayListWithCapacity((int)list.size());
        for (P primitiveValue : list) {
            values.add(this.wrap(listPersistentDataType.elementType(), primitiveValue));
        }
        return new NbtList(values);
    }

    private <P> List<P> extractList(@NotNull PersistentDataType<P, ?> type, @NotNull NbtList listTag) {
        Preconditions.checkArgument((boolean)(type instanceof ListPersistentDataType), (String)"The found list tag cannot be read with a %s (expected a list data type)", (Object)type.getClass().getSimpleName());
        ListPersistentDataType listPersistentDataType = (ListPersistentDataType)type;
        ObjectArrayList output = new ObjectArrayList(listTag.size());
        for (NbtElement tag : listTag) {
            output.add(this.extract(listPersistentDataType.elementType(), tag));
        }
        return output;
    }

    private boolean matchesListTag(PersistentDataType<List, ?> type, NbtElement tag) {
        if (!(type instanceof ListPersistentDataType)) {
            return false;
        }
        ListPersistentDataType listPersistentDataType = (ListPersistentDataType)type;
        if (!(tag instanceof NbtList)) {
            return false;
        }
        NbtList listTag = (NbtList)tag;
        byte elementType = listTag.getValueType();
        TagAdapter elementAdapter = this.getOrCreateAdapter(listPersistentDataType.elementType());
        return elementAdapter.nmsTypeByte() == elementType || elementType == 0;
    }

    private record TagAdapter<P, T extends NbtElement>(Class<P> primitiveType, Class<T> tagType, byte nmsTypeByte, BiFunction<PersistentDataType<P, ?>, P, T> builder, BiFunction<PersistentDataType<P, ?>, T, P> extractor, BiPredicate<PersistentDataType<P, ?>, NbtElement> matcher) {
        private P extract(PersistentDataType<P, ?> dataType, NbtElement base) {
            Preconditions.checkArgument(this.tagType.isInstance(base), "The provided Tag was of the type %s. Expected type %s", base.getClass().getSimpleName(), this.tagType.getSimpleName());
            return this.extractor.apply(dataType, this.tagType.cast(base));
        }

        private T build(PersistentDataType<P, ?> dataType, Object value) {
            Preconditions.checkArgument((boolean)this.primitiveType.isInstance(value), (String)"The provided value was of the type %s. Expected type %s", value.getClass().getSimpleName(), this.primitiveType.getSimpleName());
            return (T)((NbtElement)this.builder.apply(dataType, this.primitiveType.cast(value)));
        }

        private boolean isInstance(PersistentDataType<P, ?> persistentDataType, NbtElement base) {
            return this.matcher.test(persistentDataType, base);
        }
    }
}

