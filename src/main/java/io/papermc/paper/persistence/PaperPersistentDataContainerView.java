package io.papermc.paper.persistence;

import com.google.common.base.Preconditions;
import io.papermc.paper.persistence.PersistentDataContainerView;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataAdapterContext;
import org.bukkit.craftbukkit.persistence.CraftPersistentDataTypeRegistry;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(value=NonNull.class)
public abstract class PaperPersistentDataContainerView
implements PersistentDataContainerView {
    
	/*
	protected final CraftPersistentDataTypeRegistry registry;
    protected final CraftPersistentDataAdapterContext adapterContext;

    public PaperPersistentDataContainerView(CraftPersistentDataTypeRegistry registry) {
        this.registry = registry;
        this.adapterContext = new CraftPersistentDataAdapterContext(this.registry);
    }

    public abstract @Nullable NbtElement getTag(String var1);

    public abstract NbtCompound toTagCompound();

    public <P, C> boolean has(NamespacedKey key, PersistentDataType<P, C> type) {
        Preconditions.checkArgument((key != null ? 1 : 0) != 0, (Object)"The NamespacedKey key cannot be null");
        Preconditions.checkArgument((type != null ? 1 : 0) != 0, (Object)"The provided type cannot be null");
        @Nullable NbtElement value = this.getTag(key.toString());
        if (value == null) {
            return false;
        }
        return this.registry.isInstanceOf(type, value);
    }

    public boolean has(NamespacedKey key) {
        Preconditions.checkArgument((key != null ? 1 : 0) != 0, (Object)"The provided key for the custom value was null");
        return this.getTag(key.toString()) != null;
    }

    public <P, C> @Nullable C get(NamespacedKey key, PersistentDataType<P, C> type) {
        Preconditions.checkArgument((key != null ? 1 : 0) != 0, (Object)"The NamespacedKey key cannot be null");
        Preconditions.checkArgument((type != null ? 1 : 0) != 0, (Object)"The provided type cannot be null");
        @Nullable NbtElement value = this.getTag(key.toString());
        if (value == null) {
            return null;
        }
        return (C)type.fromPrimitive(this.registry.extract(type, value), (PersistentDataAdapterContext)this.adapterContext);
    }

    public <P, C> C getOrDefault(NamespacedKey key, PersistentDataType<P, C> type, C defaultValue) {
        C c2 = this.get(key, type);
        return c2 != null ? c2 : defaultValue;
    }

    public PersistentDataAdapterContext getAdapterContext() {
        return this.adapterContext;
    }

    public byte[] serializeToBytes() throws IOException {
        NbtCompound root = this.toTagCompound();
        ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
        try (DataOutputStream dataOutput = new DataOutputStream(byteArrayOutput);){
            NbtIo.writeCompound(root, dataOutput);
            byte[] byArray = byteArrayOutput.toByteArray();
            return byArray;
        }
    }
    */
    
}

