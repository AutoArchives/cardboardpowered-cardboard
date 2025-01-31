package org.bukkit.craftbukkit.attribute;

import com.google.common.base.Preconditions;
import io.papermc.paper.registry.RegistryKey;
import java.util.Locale;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.legacy.FieldRename;
import org.bukkit.craftbukkit.util.ApiVersion;
import org.bukkit.craftbukkit.util.Handleable;
import org.jetbrains.annotations.NotNull;

public class CraftAttribute implements Attribute, Handleable<net.minecraft.entity.attribute.EntityAttribute> {

    private static int count = 0;

    public static Attribute minecraftToBukkit(net.minecraft.entity.attribute.EntityAttribute minecraft) {
        return CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.ATTRIBUTE);
    }

    public static Attribute minecraftHolderToBukkit(RegistryEntry<net.minecraft.entity.attribute.EntityAttribute> minecraft) {
        return CraftAttribute.minecraftToBukkit(minecraft.value());
    }

    public static Attribute stringToBukkit(String string) {
        Preconditions.checkArgument(string != null);

        // We currently do not have any version-dependent remapping, so we can use current version
        // First convert from when only the names where saved
        string = FieldRename.convertAttributeName(ApiVersion.CURRENT, string);
        string = string.toLowerCase(Locale.ROOT);
        NamespacedKey key = NamespacedKey.fromString(string);
        if (key == null) return null; // Paper - Fixup NamespacedKey handling

        // Now also convert from when keys where saved
        return CraftRegistry.get(RegistryKey.ATTRIBUTE, key, ApiVersion.CURRENT);
    }

    public static net.minecraft.entity.attribute.EntityAttribute bukkitToMinecraft(Attribute bukkit) {
        return CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public static RegistryEntry<net.minecraft.entity.attribute.EntityAttribute> bukkitToMinecraftHolder(Attribute bukkit) {
        Preconditions.checkArgument(bukkit != null);

        net.minecraft.registry.Registry<net.minecraft.entity.attribute.EntityAttribute> registry = CraftRegistry.getMinecraftRegistry(RegistryKeys.ATTRIBUTE);

        if (registry.getEntry(CraftAttribute.bukkitToMinecraft(bukkit)) instanceof RegistryEntry.Reference<net.minecraft.entity.attribute.EntityAttribute> holder) {
            return holder;
        }

        throw new IllegalArgumentException("No Reference holder found for " + bukkit
                + ", this can happen if a plugin creates its own sound effect with out properly registering it.");
    }

    public static String bukkitToString(Attribute bukkit) {
        Preconditions.checkArgument(bukkit != null);

        return bukkit.getKey().toString();
    }

    private final NamespacedKey key;
    private final net.minecraft.entity.attribute.EntityAttribute attributeBase;
    private final String name;
    private final int ordinal;

    public CraftAttribute(NamespacedKey key, net.minecraft.entity.attribute.EntityAttribute attributeBase) {
        this.key = key;
        this.attributeBase = attributeBase;
        // For backwards compatibility, minecraft values will stile return the uppercase name without the namespace,
        // in case plugins use for example the name as key in a config file to receive attribute specific values.
        // Custom attributes will return the key with namespace. For a plugin this should look than like a new attribute
        // (which can always be added in new minecraft versions and the plugin should therefore handle it accordingly).
        if (NamespacedKey.MINECRAFT.equals(key.getNamespace())) {
            this.name = key.getKey().toUpperCase(Locale.ROOT);
        } else {
            this.name = key.toString();
        }
        this.ordinal = CraftAttribute.count++;
    }

    @Override
    public net.minecraft.entity.attribute.EntityAttribute getHandle() {
        return this.attributeBase;
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return this.key;
    }

    @NotNull
    @Override
    public String getTranslationKey() {
        return this.attributeBase.getTranslationKey();
    }

    @Override
    public @NotNull String translationKey() {
        return this.attributeBase.getTranslationKey();
    }

    @Override
    public int compareTo(@NotNull Attribute attribute) {
        return this.ordinal - attribute.ordinal();
    }

    @NotNull
    @Override
    public String name() {
        return this.name;
    }

    @Override
    public int ordinal() {
        return this.ordinal;
    }

    @Override
    public String toString() {
        // For backwards compatibility
        return this.name();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof CraftAttribute otherAttribute)) {
            return false;
        }

        return this.getKey().equals(otherAttribute.getKey());
    }

    @Override
    public int hashCode() {
        return this.getKey().hashCode();
    }
}
