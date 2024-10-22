package org.cardboardpowered.impl.entity;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.craftbukkit.util.Handleable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.jetbrains.annotations.Nullable;

import com.destroystokyo.paper.entity.villager.Reputation;
import com.google.common.base.Preconditions;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.BlockPos;

import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;

public class VillagerImpl extends AbstractVillagerImpl implements Villager {

	public static class CraftProfession
    implements Villager.Profession,
    Handleable<VillagerProfession> {
        private static int count = 0;
        private final NamespacedKey key;
        private final VillagerProfession villagerProfession;
        private final String name;
        private final int ordinal;

        public static Villager.Profession minecraftToBukkit(VillagerProfession minecraft) {
            return (Villager.Profession)CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.VILLAGER_PROFESSION, Registry.VILLAGER_PROFESSION);
        }

        public static VillagerProfession bukkitToMinecraft(Villager.Profession bukkit) {
            return (VillagerProfession)CraftRegistry.bukkitToMinecraft(bukkit);
        }

        public CraftProfession(NamespacedKey key, VillagerProfession villagerProfession) {
            this.key = key;
            this.villagerProfession = villagerProfession;
            this.name = "minecraft".equals(key.getNamespace()) ? key.getKey().toUpperCase(Locale.ROOT) : key.toString();
            this.ordinal = count++;
        }

        @Override
        public VillagerProfession getHandle() {
            return this.villagerProfession;
        }

        public NamespacedKey getKey() {
            return this.key;
        }

        public int compareTo(Villager.Profession profession) {
            return this.ordinal - profession.ordinal();
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
            if (!(other instanceof CraftProfession)) {
                return false;
            }
            return this.getKey().equals((Object)((Villager.Profession)other).getKey());
        }

        public int hashCode() {
            return this.getKey().hashCode();
        }
    }

	public static class CraftType
    implements Villager.Type,
    Handleable<VillagerType> {
        private static int count = 0;
        private final NamespacedKey key;
        private final VillagerType villagerType;
        private final String name;
        private final int ordinal;

        public static Villager.Type minecraftToBukkit(VillagerType minecraft) {
            return (Villager.Type)CraftRegistry.minecraftToBukkit(minecraft, RegistryKeys.VILLAGER_TYPE, Registry.VILLAGER_TYPE);
        }

        public static VillagerType bukkitToMinecraft(Villager.Type bukkit) {
            return (VillagerType)CraftRegistry.bukkitToMinecraft(bukkit);
        }

        public CraftType(NamespacedKey key, VillagerType villagerType) {
            this.key = key;
            this.villagerType = villagerType;
            this.name = "minecraft".equals(key.getNamespace()) ? key.getKey().toUpperCase(Locale.ROOT) : key.toString();
            this.ordinal = count++;
        }

        @Override
        public VillagerType getHandle() {
            return this.villagerType;
        }

        public NamespacedKey getKey() {
            return this.key;
        }

        public int compareTo(Villager.Type type) {
            return this.ordinal - type.ordinal();
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
            if (!(other instanceof CraftType)) {
                return false;
            }
            return this.getKey().equals((Object)((Villager.Type)other).getKey());
        }

        public int hashCode() {
            return this.getKey().hashCode();
        }
    }

	public VillagerImpl(CraftServer server, VillagerEntity entity) {
        super(server, entity);
    }

    @Override
    public VillagerEntity getHandle() {
        return (VillagerEntity) nms;
    }

    @Override
    public String toString() {
        return "CraftVillager";
    }

    @Override
    public EntityType getType() {
        return EntityType.VILLAGER;
    }

    @Override
    public Profession getProfession() {
        return nmsToBukkitProfession(getHandle().getVillagerData().getProfession());
    }

    @Override
    public void setProfession(Profession profession) {
        Validate.notNull(profession);
        getHandle().setVillagerData(getHandle().getVillagerData().withProfession(bukkitToNmsProfession(profession)));
    }

    @Override
    public Type getVillagerType() {
        return Type.valueOf(Registries.VILLAGER_TYPE.getId(getHandle().getVillagerData().getType()).getPath().toUpperCase(Locale.ROOT));
    }

    @Override
    public void setVillagerType(Type type) {
        Validate.notNull(type);
        getHandle().setVillagerData(getHandle().getVillagerData().withType(Registries.VILLAGER_TYPE.get(CraftNamespacedKey.toMinecraft(type.getKey()))));
    }

    @Override
    public int getVillagerLevel() {
        return getHandle().getVillagerData().getLevel();
    }

    @Override
    public void setVillagerLevel(int level) {
        Preconditions.checkArgument(1 <= level && level <= 5, "level must be between [1, 5]");
        getHandle().setVillagerData(getHandle().getVillagerData().withLevel(level));
    }

    @Override
    public int getVillagerExperience() {
        return getHandle().getExperience();
    }

    @Override
    public void setVillagerExperience(int experience) {
        Preconditions.checkArgument(experience >= 0, "Experience must be positive");
        getHandle().setExperience(experience);
    }

    @Override
    public boolean sleep(Location location) {
        Preconditions.checkArgument(location != null, "Location cannot be null");
        Preconditions.checkArgument(location.getWorld() != null, "Location needs to be in a world");
        Preconditions.checkArgument(location.getWorld().equals(getWorld()), "Cannot sleep across worlds");

        BlockPos position = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        BlockState iblockdata = getHandle().getWorld().getBlockState(position);
        if (!(iblockdata.getBlock() instanceof BedBlock)) return false;

        getHandle().sleep(position);
        return true;
    }

    @Override
    public void wakeup() {
        Preconditions.checkState(isSleeping(), "Cannot wakeup if not sleeping");
        getHandle().wakeUp();
    }

    public static Profession nmsToBukkitProfession(VillagerProfession nms) {
        return Profession.valueOf(Registries.VILLAGER_PROFESSION.getId(nms).getPath().toUpperCase(Locale.ROOT));
    }

    public static VillagerProfession bukkitToNmsProfession(Profession bukkit) {
        return Registries.VILLAGER_PROFESSION.get(CraftNamespacedKey.toMinecraft(bukkit.getKey()));
    }

    // Paper start
    public int getRestocksToday() {
        return -1; // TODO
    }

    public void setRestocksToday(int restocksToday) {
        // TODO
    }

    @Override
    public void clearReputations() {
        // TODO Auto-generated method stub
    }

    @Override
    public Reputation getReputation(UUID arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<UUID, Reputation> getReputations() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setReputation(UUID arg0, Reputation arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setReputations(Map<UUID, Reputation> arg0) {
        // TODO Auto-generated method stub
    }
    // Paper end

    // 1.17 API
    @Override
    public void shakeHead() {
        // TODO Auto-generated method stub
    }

	@Override
	public @Nullable ZombieVillager zombify() {
		// TODO Auto-generated method stub
		return null;
	}
	
	// 1.19.2:

	@Override
	public boolean addTrades(int arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
    public boolean increaseLevel(int amount) {
        Preconditions.checkArgument((amount > 0 ? 1 : 0) != 0, (Object)"Level earned must be positive");
        int supposedFinalLevel = this.getVillagerLevel() + amount;
        Preconditions.checkArgument((1 <= supposedFinalLevel && supposedFinalLevel <= 5 ? 1 : 0) != 0, (Object)"Final level reached after the donation (%d) must be between [%d, %d]".formatted(supposedFinalLevel, 1, 5));
        Int2ObjectMap<TradeOffers.Factory[]> trades = TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(this.getHandle().getVillagerData().getProfession());
        if (trades == null || trades.isEmpty()) {
            this.getHandle().setVillagerData(this.getHandle().getVillagerData().withLevel(supposedFinalLevel));
            return false;
        }
        while (amount > 0) {
            // TODO this.getHandle().levelUp();
            --amount;
        }
        return true;
    }

}