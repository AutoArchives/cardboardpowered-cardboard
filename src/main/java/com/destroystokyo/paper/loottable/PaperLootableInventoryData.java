package com.destroystokyo.paper.loottable;

import com.destroystokyo.paper.loottable.LootableInventoryReplenishEvent;
import com.destroystokyo.paper.loottable.PaperLootableInventory;
import com.javazilla.bukkitfabric.BukkitFabricMod;
import com.javazilla.bukkitfabric.interfaces.IMixinEntity;

//import io.papermc.paper.configuration.WorldConfiguration;
//import io.papermc.paper.configuration.type.DurationOrDisabled;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import org.bukkit.entity.Player;
import org.bukkit.loot.LootTable;

public class PaperLootableInventoryData {
    private static final Random RANDOM = new Random();
    private long lastFill = -1L;
    private long nextRefill = -1L;
    private int numRefills = 0;
    private Map<UUID, Long> lootedPlayers;
    private final PaperLootableInventory lootable;

    public PaperLootableInventoryData(PaperLootableInventory lootable) {
        this.lootable = lootable;
    }

    long getLastFill() {
        return this.lastFill;
    }

    long getNextRefill() {
        return this.nextRefill;
    }

    long setNextRefill(long nextRefill) {
        long prev = this.nextRefill;
        this.nextRefill = nextRefill;
        return prev;
    }

    public boolean shouldReplenish(@Nullable PlayerEntity player) {
        LootTable table = this.lootable.getLootTable();
        if (table == null) {
            return false;
        }
        if (this.lastFill == -1L) {
        	//|| !this.lootable.getNMSWorld().paperConfig().lootables.autoReplenish) {
            return true;
        }
        if (player == null) {
            return false;
        }
        if (this.nextRefill == -1L) {
            return false;
        }
        // WorldConfiguration paperConfig = this.lootable.getNMSWorld().paperConfig();
        //if (paperConfig.lootables.maxRefills != -1 && this.numRefills >= paperConfig.lootables.maxRefills) {
        //    return false;
        //}
        if (this.nextRefill > System.currentTimeMillis()) {
            return false;
        }

        Player bukkitPlayer = (Player)((IMixinEntity) player).getBukkitEntity();
        LootableInventoryReplenishEvent event = new LootableInventoryReplenishEvent(bukkitPlayer, this.lootable.getAPILootableInventory());
        event.setCancelled(!this.canPlayerLoot(player.getUuid(), null));
        return event.callEvent();
    }

    public void processRefill(@Nullable PlayerEntity player) {
        this.lastFill = System.currentTimeMillis();
        BukkitFabricMod.LOGGER.info("processRefil: TODO stub");
        
        //WorldConfiguration paperConfig = this.lootable.getNMSWorld().paperConfig();
        /*if (paperConfig.lootables.autoReplenish) {
            long min = paperConfig.lootables.refreshMin.seconds();
            long max = paperConfig.lootables.refreshMax.seconds();
            this.nextRefill = this.lastFill + (min + RANDOM.nextLong(max - min + 1L)) * 1000L;
            ++this.numRefills;
            if (paperConfig.lootables.resetSeedOnFill) {
                this.lootable.setSeed(0L);
            }
            if (player != null) {
                this.setPlayerLootedState(player.getUuid(), true);
            }
        } else {
            this.lootable.clearLootTable();
        }
        */
    }

    public void loadNbt(NbtCompound base) {
        if (!base.contains("Paper.LootableData", 10)) {
            return;
        }
        NbtCompound comp = base.getCompound("Paper.LootableData");
        if (comp.contains("lastFill")) {
            this.lastFill = comp.getLong("lastFill");
        }
        if (comp.contains("nextRefill")) {
            this.nextRefill = comp.getLong("nextRefill");
        }
        if (comp.contains("numRefills")) {
            this.numRefills = comp.getInt("numRefills");
        }
        if (comp.contains("lootedPlayers", 9)) {
            NbtList list = comp.getList("lootedPlayers", 10);
            int size = list.size();
            if (size > 0) {
                this.lootedPlayers = new HashMap<UUID, Long>(list.size());
            }
            for (int i2 = 0; i2 < size; ++i2) {
                NbtCompound cmp = list.getCompound(i2);
                this.lootedPlayers.put(cmp.getUuid("UUID"), cmp.getLong("Time"));
            }
        }
    }

    public void saveNbt(NbtCompound base) {
        NbtCompound comp = new NbtCompound();
        if (this.nextRefill != -1L) {
            comp.putLong("nextRefill", this.nextRefill);
        }
        if (this.lastFill != -1L) {
            comp.putLong("lastFill", this.lastFill);
        }
        if (this.numRefills != 0) {
            comp.putInt("numRefills", this.numRefills);
        }
        if (this.lootedPlayers != null && !this.lootedPlayers.isEmpty()) {
            NbtList list = new NbtList();
            for (Map.Entry<UUID, Long> entry : this.lootedPlayers.entrySet()) {
                NbtCompound cmp = new NbtCompound();
                cmp.putUuid("UUID", entry.getKey());
                cmp.putLong("Time", entry.getValue());
                list.add(cmp);
            }
            comp.put("lootedPlayers", list);
        }
        if (!comp.isEmpty()) {
            base.put("Paper.LootableData", comp);
        }
    }

    void setPlayerLootedState(UUID player, boolean looted) {
        if (looted && this.lootedPlayers == null) {
            this.lootedPlayers = new HashMap<UUID, Long>();
        }
        if (looted) {
            this.lootedPlayers.put(player, System.currentTimeMillis());
        } else if (this.lootedPlayers != null) {
            this.lootedPlayers.remove(player);
        }
    }

    boolean canPlayerLoot(UUID player, Object worldConfiguration) {
        Long lastLooted = this.getLastLooted(player);
        if ( lastLooted == null) {
        	return true;
        }
        
        //if (!worldConfiguration.lootables.restrictPlayerReloot || lastLooted == null) {
        //    return true;
       // }
       // DurationOrDisabled restrictPlayerRelootTime = worldConfiguration.lootables.restrictPlayerRelootTime;
       // if (restrictPlayerRelootTime.value().isEmpty()) {
       //     return false;
       // }
        return true;
       // return TimeUnit.SECONDS.toMillis(restrictPlayerRelootTime.value().get().seconds()) + lastLooted < System.currentTimeMillis();
    }

    boolean hasPlayerLooted(UUID player) {
        return this.lootedPlayers != null && this.lootedPlayers.containsKey(player);
    }

    Long getLastLooted(UUID player) {
        return this.lootedPlayers != null ? this.lootedPlayers.get(player) : null;
    }
}

