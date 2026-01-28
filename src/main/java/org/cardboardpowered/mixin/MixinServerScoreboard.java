package org.cardboardpowered.mixin;

import org.cardboardpowered.bridge.server.level.ServerPlayerBridge;
import org.bukkit.craftbukkit.CraftServer;
import org.cardboardpowered.impl.entity.CraftPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Set;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

@Mixin(value = ServerScoreboard.class, priority = 900)
public class MixinServerScoreboard extends Scoreboard {

    @Shadow
    public Set<Objective> trackedObjectives;

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
    public void startTrackingObjective(Objective objective) {
        List<Packet<?>> list = ((ServerScoreboard)(Object)this).getStartTrackingPackets(objective);
        for (ServerPlayer entityplayer : CraftServer.INSTANCE.getHandle().getPlayers()) {
            if (((CraftPlayer)((ServerPlayerBridge)entityplayer).getBukkitEntity()).getScoreboard().getHandle() != (ServerScoreboard)(Object)this) continue;
            for (Packet<?> packet : list) {
                entityplayer.connection.send(packet);
            }
        }
        this.trackedObjectives.add(objective);
    }
    
    /**
     * @author Cardboard
     * @reason bukkitize scoreboard
     */
    @Overwrite
    public void stopTrackingObjective(Objective objective) {
        List<Packet<?>> list = ((ServerScoreboard)(Object)this).getStopTrackingPackets(objective);
        for (ServerPlayer entityplayer : CraftServer.INSTANCE.getHandle().getPlayers()) {
            if (((CraftPlayer)((ServerPlayerBridge)entityplayer).getBukkitEntity()).getScoreboard().getHandle() != (ServerScoreboard)(Object)this) continue;
            for (Packet<?> packet : list) {
                entityplayer.connection.send(packet);
            }
        }
        this.trackedObjectives.remove(objective);
    }
    
    /**
     * @author Cardboard
     * @reason bukkitize scoreboard
     */
    private void broadcastAll(Packet packet) {
        for (ServerPlayer entityplayer : CraftServer.INSTANCE.getHandle().players) {
            if (((CraftPlayer)((ServerPlayerBridge)entityplayer).getBukkitEntity()).getScoreboard().getHandle() != this) continue;
            entityplayer.connection.send(packet);
        }
    }

}
