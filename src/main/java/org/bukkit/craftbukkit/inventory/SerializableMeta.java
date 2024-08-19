package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

//import org.bukkit.craftbukkit.inventory.CraftMetaArmor;
//import org.bukkit.craftbukkit.inventory.CraftMetaAxolotlBucket;
//import org.bukkit.craftbukkit.inventory.CraftMetaBundle;
//import org.bukkit.craftbukkit.inventory.CraftMetaColorableArmor;
//import org.bukkit.craftbukkit.inventory.CraftMetaCompass;
//import org.bukkit.craftbukkit.inventory.CraftMetaEntityTag;
//import org.bukkit.craftbukkit.inventory.CraftMetaMusicInstrument;
//import org.bukkit.craftbukkit.inventory.CraftMetaOminousBottle;
//import org.bukkit.craftbukkit.inventory.CraftMetaTropicalFishBucket;

import org.bukkit.inventory.meta.ItemMeta;

@SerializableAs(value="ItemMeta")
public final class SerializableMeta
implements ConfigurationSerializable {
    static final String TYPE_FIELD = "meta-type";
    static final ImmutableMap<Class<? extends CraftMetaItem>, String> classMap;
    static final ImmutableMap<String, Constructor<? extends CraftMetaItem>> constructorMap;

    private SerializableMeta() {
    }

    public static ItemMeta deserialize(Map<String, Object> map) throws Throwable {
        Preconditions.checkArgument((map != null ? 1 : 0) != 0, (Object)"Cannot deserialize null map");
        String type = SerializableMeta.getString(map, TYPE_FIELD, false);
        Constructor constructor = (Constructor)constructorMap.get((Object)type);
        if (constructor == null) {
            throw new IllegalArgumentException(type + " is not a valid meta-type");
        }
        try {
            return (ItemMeta)constructor.newInstance(map);
        }
        catch (InstantiationException e2) {
            throw new AssertionError((Object)e2);
        }
        catch (IllegalAccessException e3) {
            throw new AssertionError((Object)e3);
        }
        catch (InvocationTargetException e4) {
            throw e4.getCause();
        }
    }

    public Map<String, Object> serialize() {
        throw new AssertionError();
    }

    public static String getString(Map<?, ?> map, Object field, boolean nullable) {
        return SerializableMeta.getObject(String.class, map, field, nullable);
    }

    public static boolean getBoolean(Map<?, ?> map, Object field) {
        Boolean value = SerializableMeta.getObject(Boolean.class, map, field, true);
        return value != null && value != false;
    }

    public static <T> T getObject(Class<T> clazz, Map<?, ?> map, Object field, boolean nullable) {
        Object object = map.get(field);
        if (clazz.isInstance(object)) {
            return clazz.cast(object);
        }
        if (object == null) {
            if (!nullable) {
                throw new NoSuchElementException(String.valueOf(map) + " does not contain " + String.valueOf(field));
            }
            return null;
        }
        throw new IllegalArgumentException(String.valueOf(field) + "(" + String.valueOf(object) + ") is not a valid " + String.valueOf(clazz));
    }

    public static <T> Optional<T> getObjectOptionally(Class<T> clazz, Map<?, ?> map, Object field, boolean nullable) {
        Object object = map.get(field);
        if (clazz.isInstance(object)) {
            return Optional.of(clazz.cast(object));
        }
        if (object == null) {
            if (!nullable) {
                throw new NoSuchElementException(String.valueOf(map) + " does not contain " + String.valueOf(field));
            }
            return Optional.empty();
        }
        throw new IllegalArgumentException(String.valueOf(field) + "(" + String.valueOf(object) + ") is not a valid " + String.valueOf(clazz));
    }

    static {
    	
    	classMap = ImmutableMap.<Class<? extends CraftMetaItem>, String>builder()
                //.put(CraftMetaArmor.class, "ARMOR")
                .put(CraftMetaArmorStand.class, "ARMOR_STAND")
                .put(CraftMetaBanner.class, "BANNER")
                .put(CraftMetaBlockState.class, "TILE_ENTITY")
                .put(CraftMetaBook.class, "BOOK")
                .put(CraftMetaBookSigned.class, "BOOK_SIGNED")
                .put(CraftMetaSkull.class, "SKULL")
                .put(CraftMetaLeatherArmor.class, "LEATHER_ARMOR")
                //.put(CraftMetaColorableArmor.class, "COLORABLE_ARMOR")
                .put(CraftMetaMap.class, "MAP")
                .put(CraftMetaPotion.class, "POTION")
                .put(CraftMetaSpawnEgg.class, "SPAWN_EGG")
                .put(CraftMetaEnchantedBook.class, "ENCHANTED")
                .put(CraftMetaFirework.class, "FIREWORK")
                .put(CraftMetaCharge.class, "FIREWORK_EFFECT")
                .put(CraftMetaKnowledgeBook.class, "KNOWLEDGE_BOOK")
                //.put(CraftMetaTropicalFishBucket.class, "TROPICAL_FISH_BUCKET")
                //.put(CraftMetaAxolotlBucket.class, "AXOLOTL_BUCKET")
                .put(CraftMetaCrossbow.class, "CROSSBOW")
                .put(CraftMetaSuspiciousStew.class, "SUSPICIOUS_STEW")
                //.put(CraftMetaEntityTag.class, "ENTITY_TAG")
                //.put(CraftMetaCompass.class, "COMPASS")
                //.put(CraftMetaBundle.class, "BUNDLE")
                //.put(CraftMetaMusicInstrument.class, "MUSIC_INSTRUMENT")
                //.put(CraftMetaOminousBottle.class, "OMINOUS_BOTTLE")
                .put(CraftMetaItem.class, "UNSPECIFIC")
                .build();
    	
        final ImmutableMap.Builder<String, Constructor<? extends CraftMetaItem>> classConstructorBuilder = ImmutableMap.builder();
        for (Map.Entry<Class<? extends CraftMetaItem>, String> mapping : classMap.entrySet()) {
            try {
                classConstructorBuilder.put(mapping.getValue(), mapping.getKey().getDeclaredConstructor(Map.class));
            } catch (NoSuchMethodException e2) {
                throw new AssertionError((Object)e2);
            }
        }
        constructorMap = classConstructorBuilder.build();
    }
}

