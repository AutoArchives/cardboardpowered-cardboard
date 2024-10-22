package org.cardboardpowered.impl.block;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.spawner.SpawnRule;
import org.bukkit.block.spawner.SpawnerEntry;
import org.bukkit.craftbukkit.entity.CraftEntitySnapshot;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.cardboardpowered.impl.world.WorldImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.spawner.MobSpawnerEntry;
import net.minecraft.block.spawner.MobSpawnerLogic;
import net.minecraft.entity.EquipmentTable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

@SuppressWarnings("deprecation")
public class CardboardMobspawner extends CardboardBlockEntityState<MobSpawnerBlockEntity> implements CreatureSpawner {

    public CardboardMobspawner(World world, MobSpawnerBlockEntity tileEntity) {
        super(world, tileEntity);
    }

    protected CardboardMobspawner(CardboardMobspawner state, Location location) {
        super(state, location);
    }

    @Override
    public CardboardMobspawner copy() {
        return new CardboardMobspawner(this, null);
    }

    @Override
    public CardboardMobspawner copy(Location location) {
        return new CardboardMobspawner(this, location);
    }

    @Override
    public EntityType getSpawnedType() {
        //Identifier key = this.getSnapshot().getLogic().getEntityId(((WorldImpl)this.getWorld()).getHandle(), 
       //         new BlockPos(this.getBlock().getPosition().x, this.getBlock().getPosition().y, this.getBlock().getPosition().z));
        Identifier key = null;
        return (key == null) ? EntityType.PIG : EntityType.fromName(key.getPath());
    }

    @Override
    public void setSpawnedType(EntityType entityType) {
        if (entityType == null || entityType.getName() == null)
            throw new IllegalArgumentException("Can't spawn EntityType " + entityType + " from mobspawners!");
        // this.getSnapshot().getLogic().setEntityId(net.minecraft.entity.EntityType.get(entityType.getName()).get());
        
        Random rand = (this.isPlaced()) ? ((WorldImpl)this.getWorld()).getHandle().getRandom() : Random.create();
        this.getSnapshot().setEntityType(net.minecraft.entity.EntityType.get(entityType.getName()).get(), rand);
    }

    @Override
    public String getCreatureTypeName() {
        return "PIG";// TODO 1.17ify this.getSnapshot().getLogic().getEntityId().getPath();
    }

    @Override
    public void setCreatureTypeByName(String creatureType) {
        EntityType type = EntityType.fromName(creatureType);
        if (type == null) return;
        setSpawnedType(type);
    }

    @Override
    public int getDelay() {
        return this.getSnapshot().getLogic().spawnDelay;
    }

    @Override
    public void setDelay(int delay) {
        this.getSnapshot().getLogic().spawnDelay = delay;
    }

    @Override
    public int getMinSpawnDelay() {
        return this.getSnapshot().getLogic().minSpawnDelay;
    }

    @Override
    public void setMinSpawnDelay(int spawnDelay) {
        this.getSnapshot().getLogic().minSpawnDelay = spawnDelay;
    }

    @Override
    public int getMaxSpawnDelay() {
        return this.getSnapshot().getLogic().maxSpawnDelay;
    }

    @Override
    public void setMaxSpawnDelay(int spawnDelay) {
        this.getSnapshot().getLogic().maxSpawnDelay = spawnDelay;
    }

    @Override
    public int getMaxNearbyEntities() {
        return this.getSnapshot().getLogic().maxNearbyEntities;
    }

    @Override
    public void setMaxNearbyEntities(int maxNearbyEntities) {
        this.getSnapshot().getLogic().maxNearbyEntities = maxNearbyEntities;
    }

    @Override
    public int getSpawnCount() {
        return this.getSnapshot().getLogic().spawnCount;
    }

    @Override
    public void setSpawnCount(int count) {
        this.getSnapshot().getLogic().spawnCount = count;
    }

    @Override
    public int getRequiredPlayerRange() {
        return this.getSnapshot().getLogic().requiredPlayerRange;
    }

    @Override
    public void setRequiredPlayerRange(int requiredPlayerRange) {
        this.getSnapshot().getLogic().requiredPlayerRange = requiredPlayerRange;
    }

    @Override
    public int getSpawnRange() {
        return this.getSnapshot().getLogic().spawnRange;
    }

    @Override
    public void setSpawnRange(int spawnRange) {
        this.getSnapshot().getLogic().spawnRange = spawnRange;
    }

    @Override
    public boolean isActivated() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void resetTimer() {
        // TODO Auto-generated method stub
    }

    @Override
    public void setSpawnedItem(ItemStack arg0) {
        // TODO Auto-generated method stub
    }
    
    // 1.20.3 API:

	@Override
	public @Nullable EntitySnapshot getSpawnedEntity() {
		return null;
		
		/*MobSpawnerEntry spawnData = this.getSnapshot().getLogic().spawnEntry;
        if (spawnData == null) {
            return null;
        }

        return CraftEntitySnapshot.create(spawnData.getNbt());*/
	}

	@Override
	public void setSpawnedEntity(@NotNull EntitySnapshot snapshot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addPotentialSpawn(@NotNull EntitySnapshot snapshot, int weight, @Nullable SpawnRule spawnRule) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addPotentialSpawn(@NotNull SpawnerEntry spawnerEntry) {
        this.addPotentialSpawn(spawnerEntry.getSnapshot(), spawnerEntry.getSpawnWeight(), spawnerEntry.getSpawnRule());
	}

	@Override
	public void setPotentialSpawns(@NotNull Collection<SpawnerEntry> entries) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public @NotNull List<SpawnerEntry> getPotentialSpawns() {
		/*
		 List<SpawnerEntry> entries = new ArrayList<>();

	        for (Present<MobSpawnerEntry> entry : this.getSnapshot().getLogic().spawnPotentials.getEntries()) { // PAIL rename Wrapper
	            CraftEntitySnapshot snapshot = CraftEntitySnapshot.create(entry.data().getNbt());

	            if (snapshot != null) {
	                SpawnRule rule = entry.data().customSpawnRules().map(this::fromMinecraftRule).orElse(null);
	                entries.add(new SpawnerEntry(snapshot, entry.getWeight().getValue(), rule, CraftCreatureSpawner.getEquipment(entry.data().equipment())));
	            }
	        }
	        return entries;
	        */
		return null;
	}

	@Override
	public void setSpawnedEntity(@NotNull SpawnerEntry spawnerEntry) {
		// TODO Auto-generated method stub
        setSpawnedEntity(((MobSpawnerBlockEntity)this.getSnapshot()).getLogic(), spawnerEntry.getSnapshot(), spawnerEntry.getSpawnRule(), spawnerEntry.getEquipment());

	}
	
    public static void setSpawnedEntity(MobSpawnerLogic spawner, EntitySnapshot snapshot, SpawnRule spawnRule, SpawnerEntry.Equipment equipment) {
        // TODO
    	/*
    	spawner.spawnPotentials = DataPool.empty();
        if (snapshot == null) {
            spawner.spawnEntry = new MobSpawnerEntry();
            return;
        }
        NbtCompound compoundTag = ((CraftEntitySnapshot)snapshot).getData();
        spawner.spawnEntry = new MobSpawnerEntry(compoundTag, Optional.ofNullable(toMinecraftRule(spawnRule)), getEquipment(equipment));
        */
    }
    


}