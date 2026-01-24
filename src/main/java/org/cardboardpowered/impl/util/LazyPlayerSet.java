package org.cardboardpowered.impl.util;

import java.util.HashSet;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.entity.Player;

import org.cardboardpowered.interfaces.IMixinEntity;

public class LazyPlayerSet extends LazyHashSet<Player> {

    private final MinecraftServer server;

    public LazyPlayerSet(MinecraftServer server) {
        this.server = server;
    }

    @Override
    HashSet<Player> makeReference() {
        if (reference != null) throw new IllegalStateException("Reference already created");
        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        HashSet<Player> reference = new HashSet<Player>(players.size());
        for (ServerPlayer player : players)
            reference.add((Player) ((IMixinEntity)player).getBukkitEntity());
        return reference;
    }

}