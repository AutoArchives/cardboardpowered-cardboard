package com.destroystokyo.paper.loottable;

import com.destroystokyo.paper.loottable.LootableInventoryReplenishEvent;
import com.destroystokyo.paper.loottable.PaperLootableInventory;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.cardboardpowered.CardboardMod;
import org.cardboardpowered.interfaces.IMixinEntity;

//import io.papermc.paper.configuration.WorldConfiguration;
//import io.papermc.paper.configuration.type.DurationOrDisabled;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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

    public boolean shouldReplenish(@Nullable net.minecraft.world.entity.player.Player player) {
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
        event.setCancelled(!this.canPlayerLoot(player.getUUID(), null));
        return event.callEvent();
    }

    public void processRefill(@Nullable net.minecraft.world.entity.player.Player player) {
        this.lastFill = System.currentTimeMillis();
        CardboardMod.LOGGER.info("processRefil: TODO stub");
        
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
    
    private static final String ROOT = "Paper.LootableData";
    private static final String LAST_FILL = "lastFill";
    private static final String NEXT_REFILL = "nextRefill";
    private static final String NUM_REFILLS = "numRefills";
    private static final String LOOTED_PLAYERS = "lootedPlayers";
    
    public void loadNbt(ValueInput input) {
        ValueInput data = input.childOrEmpty(ROOT);
        this.lastFill = data.getLongOr(LAST_FILL, -1L);
        this.nextRefill = data.getLongOr(NEXT_REFILL, -1L);
        this.numRefills = data.getIntOr(NUM_REFILLS, 0);
        ValueInput.TypedInputList<SerializedLootedPlayerEntry> list = data.listOrEmpty(LOOTED_PLAYERS, SerializedLootedPlayerEntry.CODEC);
        if (!list.isEmpty()) {
            this.lootedPlayers = new HashMap<UUID, Long>();
            list.forEach(serializedLootedPlayerEntry -> this.lootedPlayers.put(serializedLootedPlayerEntry.uuid, serializedLootedPlayerEntry.time));
        }
    }
    
    record SerializedLootedPlayerEntry(UUID uuid, long time) {
        public static final Codec<SerializedLootedPlayerEntry> CODEC =
        		RecordCodecBuilder.create(instance -> 
        			instance.group(
        					UUIDUtil.CODEC.fieldOf("UUID").forGetter(SerializedLootedPlayerEntry::uuid),
        					Codec.LONG.optionalFieldOf("Time", 0L).forGetter(SerializedLootedPlayerEntry::time)
        			)
        			.apply(instance, SerializedLootedPlayerEntry::new));
    }

    /*
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
    */
    
    public void saveNbt(ValueOutput output) {
        ValueOutput data = output.child(ROOT);
        if (this.nextRefill != -1L) {
            data.putLong(NEXT_REFILL, this.nextRefill);
        }
        if (this.lastFill != -1L) {
            data.putLong(LAST_FILL, this.lastFill);
        }
        if (this.numRefills != 0) {
            data.putInt(NUM_REFILLS, this.numRefills);
        }
        if (this.lootedPlayers != null && !this.lootedPlayers.isEmpty()) {
            ValueOutput.TypedOutputList<SerializedLootedPlayerEntry> list = data.list(LOOTED_PLAYERS, SerializedLootedPlayerEntry.CODEC);
            for (Map.Entry<UUID, Long> entry : this.lootedPlayers.entrySet()) {
                list.add(new SerializedLootedPlayerEntry(entry.getKey(), entry.getValue()));
            }
        }
        if (data.isEmpty()) {
            output.discard(ROOT);
        }
    }

    /*
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
    */

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

