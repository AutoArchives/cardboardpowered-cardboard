package org.cardboardpowered.impl.entity;

import com.google.common.base.Preconditions;
import java.util.Locale;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;

public class VillagerZombieImpl extends ZombieImpl implements ZombieVillager {

    public VillagerZombieImpl(CraftServer server, ZombieVillagerEntity entity) {
        super(server, entity);
    }

    @Override
    public ZombieVillagerEntity getHandle() {
        return (ZombieVillagerEntity) super.getHandle();
    }

    @Override
    public String toString() {
        return "CraftVillagerZombie";
    }

    @Override
    public EntityType getType() {
        return EntityType.ZOMBIE_VILLAGER;
    }

    @Override
    public Villager.Profession getVillagerProfession() {
        return Villager.Profession.valueOf(Registries.VILLAGER_PROFESSION.getId(getHandle().getVillagerData().getProfession()).getPath().toUpperCase(Locale.ROOT));
    }

    @Override
    public void setVillagerProfession(Villager.Profession profession) {
        Validate.notNull(profession);
        getHandle().setVillagerData(getHandle().getVillagerData().withProfession(Registries.VILLAGER_PROFESSION.get(new Identifier(profession.name().toLowerCase(Locale.ROOT)))));
    }

    @Override
    public Villager.Type getVillagerType() {
        return Villager.Type.valueOf(Registries.VILLAGER_TYPE.getId(getHandle().getVillagerData().getType()).getPath().toUpperCase(Locale.ROOT));
    }

    @Override
    public void setVillagerType(Villager.Type type) {
        Validate.notNull(type);
        getHandle().setVillagerData(getHandle().getVillagerData().withType(Registries.VILLAGER_TYPE.get(CraftNamespacedKey.toMinecraft(type.getKey()))));
    }

    @Override
    public boolean isConverting() {
        return getHandle().isConverting();
    }

    @Override
    public int getConversionTime() {
        Preconditions.checkState(isConverting(), "Entity not converting");

        // TODO return getHandle().conversionTimer;
        return -1;
    }

    @Override
    public void setConversionTime(int time) {
        // TODO
    }

    @Override
    public OfflinePlayer getConversionPlayer() {
        // TODO return (getHandle().converter == null) ? null : Bukkit.getOfflinePlayer(getHandle().converter);
        return null;
    }

    @Override
    public void setConversionPlayer(OfflinePlayer conversionPlayer) {
        if (!this.isConverting()) return;
        // TODO getHandle().converter = (conversionPlayer == null) ? null : conversionPlayer.getUniqueId();
    }

}