package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.context.ContextType;
import net.minecraft.util.math.Vec3d;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.cardboardpowered.impl.world.WorldImpl;

public class CraftLootTable implements org.bukkit.loot.LootTable {

    public static org.bukkit.loot.LootTable minecraftToBukkit(Identifier minecraft) {
        return (minecraft == null) ? null : Bukkit.getLootTable(CraftNamespacedKey.fromMinecraft(minecraft));
    }

    public static org.bukkit.loot.LootTable minecraftToBukkit(RegistryKey<LootTable> minecraft) {
        return (minecraft == null || minecraft.getValue().getPath().isEmpty()) ? null : Bukkit.getLootTable(CraftLootTable.minecraftToBukkitKey(minecraft)); // Paper - fix some NamespacedKey parsing
    }

    public static NamespacedKey minecraftToBukkitKey(RegistryKey<LootTable> minecraft) {
        return (minecraft == null) ? null : CraftNamespacedKey.fromMinecraft(minecraft.getValue());
    }

    public static RegistryKey<LootTable> bukkitToMinecraft(org.bukkit.loot.LootTable table) {
        return (table == null) ? null : CraftLootTable.bukkitKeyToMinecraft(table.getKey());
    }

    public static RegistryKey<LootTable> bukkitKeyToMinecraft(NamespacedKey key) {
        return (key == null) ? null : RegistryKey.of(RegistryKeys.LOOT_TABLE, CraftNamespacedKey.toMinecraft(key));
    }

    private final LootTable handle;
    private final NamespacedKey key;

    public CraftLootTable(NamespacedKey key, LootTable handle) {
        this.handle = handle;
        this.key = key;
    }

    public LootTable getHandle() {
        return this.handle;
    }

    @Override
    public Collection<ItemStack> populateLoot(Random random, LootContext context) {
        Preconditions.checkArgument(context != null, "LootContext cannot be null");
        LootWorldContext nmsContext = this.convertContext(context, random);
        List<net.minecraft.item.ItemStack> nmsItems = this.handle.generateLoot(nmsContext);
        Collection<ItemStack> bukkit = new ArrayList<>(nmsItems.size());

        for (net.minecraft.item.ItemStack item : nmsItems) {
            if (item.isEmpty()) {
                continue;
            }
            bukkit.add(CraftItemStack.asBukkitCopy(item));
        }

        return bukkit;
    }

    @Override
    public void fillInventory(Inventory inventory, Random random, LootContext context) {
        Preconditions.checkArgument(inventory != null, "Inventory cannot be null");
        Preconditions.checkArgument(context != null, "LootContext cannot be null");
        LootWorldContext nmsContext = this.convertContext(context, random);
        CraftInventory craftInventory = (CraftInventory) inventory;
        net.minecraft.inventory.Inventory handle = craftInventory.getInventory();

        // TODO: When events are added, call event here w/ custom reason?
        // this.getHandle().fillInventory(handle, nmsContext, random.nextLong(), true);
        getHandle().supplyInventory(handle, nmsContext, random.nextLong());
    }

    @Override
    public NamespacedKey getKey() {
        return this.key;
    }

    private LootWorldContext convertContext(LootContext context, Random random) {
        Preconditions.checkArgument(context != null, "LootContext cannot be null");
        Location loc = context.getLocation();
        Preconditions.checkArgument(loc.getWorld() != null, "LootContext.getLocation#getWorld cannot be null");
        ServerWorld handle = ((WorldImpl) loc.getWorld()).getHandle();

        LootWorldContext.Builder builder = new LootWorldContext.Builder(handle);
        if (random != null) {
            // builder = builder.withRandom(new RandomSourceWrapper(random));
        }
        this.setMaybe(builder, LootContextParameters.ORIGIN, CraftLocation.toVec3D(loc));
        if (this.getHandle() != LootTable.EMPTY) {
            // builder.luck(context.getLuck());

            if (context.getLootedEntity() != null) {
                Entity nmsLootedEntity = ((CraftEntity) context.getLootedEntity()).getHandle();
                this.setMaybe(builder, LootContextParameters.THIS_ENTITY, nmsLootedEntity);
                this.setMaybe(builder, LootContextParameters.DAMAGE_SOURCE, handle.getDamageSources().generic());
                this.setMaybe(builder, LootContextParameters.ORIGIN, nmsLootedEntity.getPos());
            }

            if (context.getKiller() != null) {
                PlayerEntity nmsKiller = ((CraftHumanEntity) context.getKiller()).getHandle();
                this.setMaybe(builder, LootContextParameters.ATTACKING_ENTITY, nmsKiller);
                // If there is a player killer, damage source should reflect that in case loot tables use that information
                this.setMaybe(builder, LootContextParameters.DAMAGE_SOURCE, handle.getDamageSources().playerAttack(nmsKiller));
                this.setMaybe(builder, LootContextParameters.LAST_DAMAGE_PLAYER, nmsKiller); // SPIGOT-5603 - Set minecraft:killed_by_player
                this.setMaybe(builder, LootContextParameters.TOOL, nmsKiller.getActiveItem()); // SPIGOT-6925 - Set minecraft:match_tool
            }
        }

        // SPIGOT-5603 - Avoid IllegalArgumentException in LootTableInfo#build()
        ContextType.Builder nmsBuilder = new ContextType.Builder();
        for (ContextParameter<?> param : this.getHandle().getType().getRequired()) {
            nmsBuilder.require(param);
        }
        for (ContextParameter<?> param : this.getHandle().getType().getAllowed()) {
            if (!this.getHandle().getType().getRequired().contains(param)) {
                nmsBuilder.allow(param);
            }
        }

        return builder.build(this.getHandle().getType());
    }

    private <T> void setMaybe(LootWorldContext.Builder builder, ContextParameter<T> param, T value) {
        if (this.getHandle().getType().getRequired().contains(param) || this.getHandle().getType().getAllowed().contains(param)) {
            builder.add(param, value);
        }
    }

    public static LootContext convertContext(net.minecraft.loot.context.LootContext info) {
        Vec3d position = info.get(LootContextParameters.ORIGIN);
        if (position == null) {
            position = info.get(LootContextParameters.THIS_ENTITY).getPos(); // Every vanilla context has origin or this_entity, see LootContextParameterSets
        }
        Location location = CraftLocation.toBukkit(position, info.getWorld().getWorld());
        LootContext.Builder contextBuilder = new LootContext.Builder(location);

        if (info.hasParameter(LootContextParameters.ATTACKING_ENTITY)) {
            CraftEntity killer = info.get(LootContextParameters.ATTACKING_ENTITY).getBukkitEntity();
            if (killer instanceof CraftHumanEntity) {
                contextBuilder.killer((CraftHumanEntity) killer);
            }
        }

        if (info.hasParameter(LootContextParameters.THIS_ENTITY)) {
            contextBuilder.lootedEntity(info.get(LootContextParameters.THIS_ENTITY).getBukkitEntity());
        }

        contextBuilder.luck(info.getLuck());
        return contextBuilder.build();
    }

    @Override
    public String toString() {
        return this.getKey().toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof org.bukkit.loot.LootTable)) {
            return false;
        }

        org.bukkit.loot.LootTable table = (org.bukkit.loot.LootTable) obj;
        return table.getKey().equals(this.getKey());
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(key); // Paper
    }

}