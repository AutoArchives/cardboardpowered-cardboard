package org.cardboardpowered.mixin;

import org.cardboardpowered.interfaces.IMixinServerEntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.network.ServerPlayerEntity;
import org.bukkit.craftbukkit.CraftServer;
import org.cardboardpowered.impl.entity.CraftPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Mixin(value = ServerScoreboard.class, priority = 900)
public class MixinServerScoreboard extends Scoreboard {

    @Shadow
    public Set<ScoreboardObjective> syncableObjectives;

    /*
    public void addScoreboardObjective(ScoreboardObjective scoreboardobjective) {
        List<Packet<?>> list = ((ServerScoreboard)(Object)this).createChangePackets(scoreboardobjective);
        Iterator iterator = CraftServer.INSTANCE.getHandle().getPlayerManager().getPlayerList().iterator();

        while (iterator.hasNext()) {
            ServerPlayerEntity entityplayer = (ServerPlayerEntity) iterator.next();
            if (((CraftPlayer)((IMixinServerEntityPlayer)entityplayer).getBukkitEntity()).getScoreboard().getHandle() != (ServerScoreboard)(Object)this) continue; // Bukkit - Only players on this board
            Iterator iterator1 = list.iterator();

            while (iterator1.hasNext()) {
                Packet<?> packet = (Packet) iterator1.next();
                entityplayer.networkHandler.sendPacket(packet);
            }
        }

        this.syncableObjectives.add(scoreboardobjective);
    }

    public void removeScoreboardObjective(ScoreboardObjective scoreboardobjective) {
        List<Packet<?>> list = ((ServerScoreboard)(Object)this).createRemovePackets(scoreboardobjective);
        Iterator iterator = CraftServer.INSTANCE.getHandle().getPlayerManager().getPlayerList().iterator();

        while (iterator.hasNext()) {
            ServerPlayerEntity entityplayer = (ServerPlayerEntity) iterator.next();
            if (((CraftPlayer)((IMixinServerEntityPlayer)entityplayer).getBukkitEntity()).getScoreboard().getHandle() != (ServerScoreboard)(Object)this) continue; // Bukkit - Only players on this board
            Iterator iterator1 = list.iterator();

            while (iterator1.hasNext()) {
                Packet<?> packet = (Packet) iterator1.next();
                entityplayer.networkHandler.sendPacket(packet);
            }
        }

        this.syncableObjectives.remove(scoreboardobjective);
    }

    private void sendAll(Packet packet) {
        for (ServerPlayerEntity entityplayer : CraftServer.server.getPlayerManager().players)
            if (((CraftPlayer)((IMixinServerEntityPlayer)entityplayer).getBukkitEntity()).getScoreboard().getHandle() == (ServerScoreboard)(Object)this)
                entityplayer.networkHandler.sendPacket(packet);
    }
    */
    
    /**
     * @author Cardboard
     * @reason bukkitize scoreboard
     */
    @Overwrite
    public void startSyncing(ScoreboardObjective objective) {
        List<Packet<?>> list = ((ServerScoreboard)(Object)this).createChangePackets(objective);
        for (ServerPlayerEntity entityplayer : CraftServer.INSTANCE.getHandle().getPlayerManager().getPlayerList()) {
            if (((CraftPlayer)((IMixinServerEntityPlayer)entityplayer).getBukkitEntity()).getScoreboard().getHandle() != (ServerScoreboard)(Object)this) continue;
            for (Packet<?> packet : list) {
                entityplayer.networkHandler.sendPacket(packet);
            }
        }
        this.syncableObjectives.add(objective);
    }
    
    /**
     * @author Cardboard
     * @reason bukkitize scoreboard
     */
    @Overwrite
    public void stopSyncing(ScoreboardObjective objective) {
        List<Packet<?>> list = ((ServerScoreboard)(Object)this).createRemovePackets(objective);
        for (ServerPlayerEntity entityplayer : CraftServer.INSTANCE.getHandle().getPlayerManager().getPlayerList()) {
            if (((CraftPlayer)((IMixinServerEntityPlayer)entityplayer).getBukkitEntity()).getScoreboard().getHandle() != (ServerScoreboard)(Object)this) continue;
            for (Packet<?> packet : list) {
                entityplayer.networkHandler.sendPacket(packet);
            }
        }
        this.syncableObjectives.remove(objective);
    }
    
    /**
     * @author Cardboard
     * @reason bukkitize scoreboard
     */
    private void broadcastAll(Packet packet) {
        for (ServerPlayerEntity entityplayer : CraftServer.INSTANCE.getHandle().getPlayerManager().players) {
            if (((CraftPlayer)((IMixinServerEntityPlayer)entityplayer).getBukkitEntity()).getScoreboard().getHandle() != this) continue;
            entityplayer.networkHandler.sendPacket(packet);
        }
    }

}
