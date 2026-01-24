package org.bukkit.craftbukkit.persistence;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.apache.commons.lang.Validate;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.util.CraftNBTTagConfigSerializer;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public final class CraftPersistentDataContainer implements PersistentDataContainer {

    private final Map<String, Tag> customDataTags = new HashMap<>();
    private final CraftPersistentDataTypeRegistry registry;
    private final CraftPersistentDataAdapterContext adapterContext;

    public CraftPersistentDataContainer(Map<String, Tag> customTags, CraftPersistentDataTypeRegistry registry) {
        this(registry);
        this.customDataTags.putAll(customTags);
    }

    public CraftPersistentDataContainer(CraftPersistentDataTypeRegistry registry) {
        this.registry = registry;
        this.adapterContext = new CraftPersistentDataAdapterContext(this.registry);
    }
    
    public Tag getTag(String key) {
        return this.customDataTags.get(key);
    }

    @Override
    public <T, Z> void set(NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        this.customDataTags.put(key.toString(), this.registry.wrap(type, type.toPrimitive(value, (PersistentDataAdapterContext)this.adapterContext)));
    }

    @Override
    public <P, C> boolean has(NamespacedKey key, PersistentDataType<P, C> type) {
        Tag value = this.getTag(key.toString());
        if (value == null) {
            return false;
        }
        return this.registry.isInstanceOf(type, value);
    }

    @Override
    public <P, C> C get(NamespacedKey key, PersistentDataType<P, C> type) {
        Tag value = this.getTag(key.toString());
        if (value == null) {
            return null;
        }
        return (C)type.fromPrimitive(this.registry.extract(type, value), (PersistentDataAdapterContext)this.adapterContext);
    }

    @Override
    public <T, Z> Z getOrDefault(NamespacedKey key, PersistentDataType<T, Z> type, Z defaultValue) {
        Z z = get(key, type);
        return z != null ? z : defaultValue;
    }

    @Override
    public void remove(NamespacedKey key) {
        Validate.notNull(key, "The provided key for the custom value was null");
        this.customDataTags.remove(key.toString());
    }

    @Override
    public boolean isEmpty() {
        return this.customDataTags.isEmpty();
    }

    @Override
    public PersistentDataAdapterContext getAdapterContext() {
        return this.adapterContext;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CraftPersistentDataContainer)) return false;
        return Objects.equals(getRaw(), ((CraftPersistentDataContainer) obj).getRaw());
    }

    public CompoundTag toTagCompound() {
        CompoundTag tag = new CompoundTag();
        for (Entry<String, Tag> entry : this.customDataTags.entrySet())
            tag.put(entry.getKey(), entry.getValue());
        return tag;
    }

    public void put(String key, Tag base) {
        this.customDataTags.put(key, base);
    }

    public void putAll(Map<String, Tag> map) {
        this.customDataTags.putAll(map);
    }

    public void putAll(CompoundTag compound) {
        for (String key : compound.keySet()) this.customDataTags.put(key, compound.get(key));
    }

    public Map<String, Tag> getRaw() {
        return this.customDataTags;
    }

    public CraftPersistentDataTypeRegistry getDataTagTypeRegistry() {
        return registry;
    }

    @Override
    public int hashCode() {
        return 3 + this.customDataTags.hashCode();
    }

    @SuppressWarnings("unchecked")
    public String serialize() {
        return CraftNBTTagConfigSerializer.serialize(toTagCompound());
    }

    @SuppressWarnings("deprecation")
    @Override
    public Set<NamespacedKey> getKeys() {
        Set<NamespacedKey> keys = new HashSet<>();
        this.customDataTags.keySet().forEach(key -> {
            String[] keyData = key.split(":", 2);
            if (keyData.length == 2) keys.add(new NamespacedKey(keyData[0], keyData[1]));
        });
        return keys;
    }

    // 1.18.2 api:
    
	@Override
	public boolean has(@NotNull NamespacedKey arg0) {
		// TODO Auto-generated method stub

        Tag value = this.customDataTags.get(arg0.toString());
        if (value == null) {
            return false;
        }

		return true;
	}

	@Override
	public void readFromBytes(byte @NotNull [] arg0, boolean arg1) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte @NotNull [] serializeToBytes() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void copyTo(@NotNull PersistentDataContainer other, boolean replace) {
        CraftPersistentDataContainer target = (CraftPersistentDataContainer) other;
        if (replace) {
            target.customDataTags.putAll(this.customDataTags);
        } else {
            this.customDataTags.forEach(target.customDataTags::putIfAbsent);
        }
	}

	@Override
    public int getSize() {
        return this.customDataTags.size();
    }

}