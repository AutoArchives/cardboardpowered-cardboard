package org.bukkit.craftbukkit.block.banner;

import com.google.common.base.Preconditions;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Locale;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.craftbukkit.util.Handleable;
import org.jetbrains.annotations.Nullable;

public class CraftPatternType implements PatternType, Handleable<BannerPattern> {
    private static int count = 0;
    private final NamespacedKey key;
    private final BannerPattern bannerPatternType;
    private final String name;
    private final int ordinal;

    public static PatternType minecraftToBukkit(BannerPattern minecraft) {
        return (PatternType)CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.BANNER_PATTERN, Registry.BANNER_PATTERN);
    }

    public static PatternType minecraftHolderToBukkit(RegistryEntry<BannerPattern> minecraft) {
        return CraftPatternType.minecraftToBukkit(minecraft.value());
    }

    public static BannerPattern bukkitToMinecraft(PatternType bukkit) {
        return (BannerPattern)CraftRegistry.bukkitToMinecraft(bukkit);
    }

    public static RegistryEntry<BannerPattern> bukkitToMinecraftHolder(PatternType bukkit) {
        Preconditions.checkArgument((bukkit != null ? 1 : 0) != 0);
        net.minecraft.registry.Registry registry = CraftRegistry.getMinecraftRegistry(RegistryKeys.BANNER_PATTERN);
        RegistryEntry<BannerPattern> registryEntry = registry.getEntry(CraftPatternType.bukkitToMinecraft(bukkit));
        if (registryEntry instanceof RegistryEntry.Reference) {
            RegistryEntry.Reference holder = (RegistryEntry.Reference)registryEntry;
            return holder;
        }
        throw new IllegalArgumentException("No Reference holder found for " + String.valueOf(bukkit) + ", this can happen if a plugin creates its own banner pattern without properly registering it.");
    }

    public CraftPatternType(NamespacedKey key, BannerPattern bannerPatternType) {
        this.key = key;
        this.bannerPatternType = bannerPatternType;
        this.name = "minecraft".equals(key.getNamespace()) ? key.getKey().toUpperCase(Locale.ROOT) : key.toString();
        this.ordinal = count++;
    }

    @Override
    public BannerPattern getHandle() {
        return this.bannerPatternType;
    }

    public NamespacedKey getKey() {
        return this.key;
    }

    public int compareTo(PatternType patternType) {
        return this.ordinal - patternType.ordinal();
    }

    public String name() {
        return this.name;
    }

    public int ordinal() {
        return this.ordinal;
    }

    public String toString() {
        return this.name();
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof CraftPatternType)) {
            return false;
        }
        return this.getKey().equals((Object)((PatternType)other).getKey());
    }

    public int hashCode() {
        return this.getKey().hashCode();
    }

    public String getIdentifier() {
        return switch (this.name()) {
            case "BASE" -> "b";
            case "SQUARE_BOTTOM_LEFT" -> "bl";
            case "SQUARE_BOTTOM_RIGHT" -> "br";
            case "SQUARE_TOP_LEFT" -> "tl";
            case "SQUARE_TOP_RIGHT" -> "tr";
            case "STRIPE_BOTTOM" -> "bs";
            case "STRIPE_TOP" -> "ts";
            case "STRIPE_LEFT" -> "ls";
            case "STRIPE_RIGHT" -> "rs";
            case "STRIPE_CENTER" -> "cs";
            case "STRIPE_MIDDLE" -> "ms";
            case "STRIPE_DOWNRIGHT" -> "drs";
            case "STRIPE_DOWNLEFT" -> "dls";
            case "SMALL_STRIPES" -> "ss";
            case "CROSS" -> "cr";
            case "STRAIGHT_CROSS" -> "sc";
            case "TRIANGLE_BOTTOM" -> "bt";
            case "TRIANGLE_TOP" -> "tt";
            case "TRIANGLES_BOTTOM" -> "bts";
            case "TRIANGLES_TOP" -> "tts";
            case "DIAGONAL_LEFT" -> "ld";
            case "DIAGONAL_UP_RIGHT" -> "rd";
            case "DIAGONAL_UP_LEFT" -> "lud";
            case "DIAGONAL_RIGHT" -> "rud";
            case "CIRCLE" -> "mc";
            case "RHOMBUS" -> "mr";
            case "HALF_VERTICAL" -> "vh";
            case "HALF_HORIZONTAL" -> "hh";
            case "HALF_VERTICAL_RIGHT" -> "vhr";
            case "HALF_HORIZONTAL_BOTTOM" -> "hhb";
            case "BORDER" -> "bo";
            case "CURLY_BORDER" -> "cbo";
            case "CREEPER" -> "cre";
            case "GRADIENT" -> "gra";
            case "GRADIENT_UP" -> "gru";
            case "BRICKS" -> "bri";
            case "SKULL" -> "sku";
            case "FLOWER" -> "flo";
            case "MOJANG" -> "moj";
            case "GLOBE" -> "glb";
            case "PIGLIN" -> "pig";
            case "FLOW" -> "flw";
            case "GUSTER" -> "gus";
            default -> this.getKey().toString();
        };
    }

}