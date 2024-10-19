package org.bukkit.craftbukkit.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.item.ArrowItem;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftItemType;
import org.bukkit.craftbukkit.inventory.CraftMetaItem;
import org.bukkit.craftbukkit.inventory.SerializableMeta;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;

@DelegateDeserialization(value=SerializableMeta.class)
public class CraftMetaCrossbow
extends CraftMetaItem
implements CrossbowMeta {
    static final CraftMetaItem.ItemMetaKey CHARGED = new CraftMetaItem.ItemMetaKey("charged");
    static final CraftMetaItem.ItemMetaKeyType<ChargedProjectilesComponent> CHARGED_PROJECTILES = new CraftMetaItem.ItemMetaKeyType<ChargedProjectilesComponent>(DataComponentTypes.CHARGED_PROJECTILES, "charged-projectiles");
    private List<ItemStack> chargedProjectiles;

    CraftMetaCrossbow(CraftMetaItem meta) {
        super(meta);
        if (!(meta instanceof CraftMetaCrossbow)) {
            return;
        }
        CraftMetaCrossbow crossbow = (CraftMetaCrossbow)meta;
        if (crossbow.hasChargedProjectiles()) {
            this.chargedProjectiles = new ArrayList<ItemStack>(crossbow.chargedProjectiles);
        }
    }

    CraftMetaCrossbow(ComponentChanges tag, Set<ComponentType<?>> extraHandledDcts) {
        super(tag, extraHandledDcts);
        CraftMetaCrossbow.getOrEmpty(tag, CHARGED_PROJECTILES).ifPresent(p -> {
            List<net.minecraft.item.ItemStack> list = p.getProjectiles();
            if (list != null && !list.isEmpty()) {
                this.chargedProjectiles = new ArrayList<ItemStack>();
                for (int i2 = 0; i2 < list.size(); ++i2) {
                    net.minecraft.item.ItemStack nbttagcompound1 = list.get(i2);
                    this.chargedProjectiles.add(CraftItemStack.asCraftMirror(nbttagcompound1));
                }
            }
        });
    }

    CraftMetaCrossbow(Map<String, Object> map) {
        super(map);
        Iterable projectiles = org.bukkit.craftbukkit.inventory.CraftMetaItem.SerializableMeta.getObject(Iterable.class, map, CraftMetaCrossbow.CHARGED_PROJECTILES.BUKKIT, true);
        if (projectiles != null) {
            for (Object stack : projectiles) {
                if (!(stack instanceof ItemStack)) continue;
                this.addChargedProjectile((ItemStack)stack);
            }
        }
    }

    @Override
    void applyToItem(CraftMetaItem.Applicator tag) {
        super.applyToItem(tag);
        if (this.hasChargedProjectiles()) {
            ArrayList<net.minecraft.item.ItemStack> list = new ArrayList<net.minecraft.item.ItemStack>();
            for (ItemStack item : this.chargedProjectiles) {
                list.add(CraftItemStack.asNMSCopy(item));
            }
            tag.put(CHARGED_PROJECTILES, ChargedProjectilesComponent.of(list));
        }
    }

    @Override
    boolean applicableTo(Material type) {
        return type == Material.CROSSBOW;
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty() && this.isCrossbowEmpty();
    }

    boolean isCrossbowEmpty() {
        return !this.hasChargedProjectiles();
    }

    public boolean hasChargedProjectiles() {
        return this.chargedProjectiles != null;
    }

    public List<ItemStack> getChargedProjectiles() {
        return this.chargedProjectiles == null ? ImmutableList.of() : ImmutableList.copyOf(this.chargedProjectiles);
    }

    public void setChargedProjectiles(List<ItemStack> projectiles) {
        this.chargedProjectiles = null;
        if (projectiles == null) {
            return;
        }
        for (ItemStack i2 : projectiles) {
            this.addChargedProjectile(i2);
        }
    }

    public void addChargedProjectile(ItemStack item) {
        Preconditions.checkArgument((item != null ? 1 : 0) != 0, "item");
        Preconditions.checkArgument((item.getType() == Material.FIREWORK_ROCKET || CraftItemType.bukkitToMinecraft(item.getType()) instanceof ArrowItem ? 1 : 0) != 0, (String)"Item %s is not an arrow or firework rocket", (Object)item);
        if (this.chargedProjectiles == null) {
            this.chargedProjectiles = new ArrayList<ItemStack>();
        }
        this.chargedProjectiles.add(item);
    }

    @Override
    boolean equalsCommon(CraftMetaItem meta) {
        if (!super.equalsCommon(meta)) {
            return false;
        }
        if (meta instanceof CraftMetaCrossbow) {
            CraftMetaCrossbow that = (CraftMetaCrossbow)meta;
            return this.hasChargedProjectiles() ? that.hasChargedProjectiles() && this.chargedProjectiles.equals(that.chargedProjectiles) : !that.hasChargedProjectiles();
        }
        return true;
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaCrossbow || this.isCrossbowEmpty());
    }

    @Override
    int applyHash() {
        int original;
        int hash = original = super.applyHash();
        if (this.hasChargedProjectiles()) {
            hash = 61 * hash + this.chargedProjectiles.hashCode();
        }
        return original != hash ? CraftMetaCrossbow.class.hashCode() ^ hash : hash;
    }

    @Override
    public CraftMetaCrossbow clone() {
        return (CraftMetaCrossbow)super.clone();
    }

    @Override
    ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
        super.serialize(builder);
        if (this.hasChargedProjectiles()) {
            builder.put(CraftMetaCrossbow.CHARGED_PROJECTILES.BUKKIT, this.chargedProjectiles);
        }
        return builder;
    }
}

