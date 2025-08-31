package org.bukkit.craftbukkit.entity;

import com.google.common.base.Preconditions;
import java.util.Locale;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
import net.minecraft.registry.Registries;
import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftZombie;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.cardboardpowered.impl.entity.CraftVillager;

public class CraftVillagerZombie extends CraftZombie implements ZombieVillager {

    public CraftVillagerZombie(CraftServer server, ZombieVillagerEntity entity) {
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
        return Villager.Profession.valueOf(Registries.VILLAGER_PROFESSION.getId(getHandle().getVillagerData().profession().value()).getPath().toUpperCase(Locale.ROOT));
    }

    @Override
    public void setVillagerProfession(Villager.Profession profession) {
        Validate.notNull(profession);
        //Identifier id = Identifier.ofVanilla( profession.name().toLowerCase(Locale.ROOT) ); // new Identifier(profession.name().toLowerCase(Locale.ROOT));
        //VillagerProfession data = Registries.VILLAGER_PROFESSION.get(id);
        //getHandle().setVillagerData(getHandle().getVillagerData().withProfession(data));
        this.getHandle().setVillagerData(this.getHandle().getVillagerData().withProfession(CraftVillager.CraftProfession.bukkitToMinecraftHolder(profession)));

    }

    @Override
    public Villager.Type getVillagerType() {
        return Villager.Type.valueOf(Registries.VILLAGER_TYPE.getId(getHandle().getVillagerData().type().value()).getPath().toUpperCase(Locale.ROOT));
    }

    @Override
    public void setVillagerType(Villager.Type type) {
        Validate.notNull(type);
        this.getHandle().setVillagerData(this.getHandle().getVillagerData().withType(CraftVillager.CraftType.bukkitToMinecraftHolder(type)));
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
    public OfflinePlayer getConversionPlayer() {
        // TODO return (getHandle().converter == null) ? null : Bukkit.getOfflinePlayer(getHandle().converter);
        return null;
    }

    @Override
    public void setConversionPlayer(OfflinePlayer conversionPlayer) {
        if (!this.isConverting()) return;
        // TODO getHandle().converter = (conversionPlayer == null) ? null : conversionPlayer.getUniqueId();
    }

    @Override
    public void setConversionTime(int time) {
        this.setConversionTime(time, true);
    }

    public void setConversionTime(int time, boolean broadcastEntityEvent) {
        if (time < 0) {
           //  this.getHandle().conversionTimer = -1;
            // this.getHandle().getDataTracker().set(ZombieVillagerEntity.CONVERTING, false);
           //  this.getHandle().converter = null;
           // this.getHandle().removeStatusEffect(StatusEffects.STRENGTH); // , EntityPotionEffectEvent.Cause.CONVERSION);
        } else {
            // this.getHandle().startConverting(null, time, broadcastEntityEvent);
        }
    }

}