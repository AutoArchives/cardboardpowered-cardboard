package org.bukkit.craftbukkit.scoreboard;

import org.cardboardpowered.interfaces.IMixinPlayerManager;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.ScoreboardManager;
import org.cardboardpowered.impl.entity.CraftPlayer;
import org.cardboardpowered.impl.util.WeakCollection;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

public final class CraftScoreboardManager implements ScoreboardManager {

    private final CardboardScoreboard mainScoreboard;
    private final MinecraftServer server;
    private final Collection<CardboardScoreboard> scoreboards = new WeakCollection<CardboardScoreboard>();
    private final Map<CraftPlayer, CardboardScoreboard> playerBoards = new HashMap<CraftPlayer, CardboardScoreboard>();

    public CraftScoreboardManager(MinecraftServer minecraftserver, net.minecraft.world.scores.Scoreboard scoreboardServer) {
        mainScoreboard = new CardboardScoreboard(scoreboardServer);
        server = minecraftserver;
        scoreboards.add(mainScoreboard);
    }

    @Override
    public CardboardScoreboard getMainScoreboard() {
        return mainScoreboard;
    }

    @Override
    public CardboardScoreboard getNewScoreboard() {
        CardboardScoreboard scoreboard = new CardboardScoreboard(new ServerScoreboard(server));
        scoreboards.add(scoreboard);
        return scoreboard;
    }

    // CardboardBukkit method
    public CardboardScoreboard getPlayerBoard(CraftPlayer player) {
        CardboardScoreboard board = playerBoards.get(player);
        return (CardboardScoreboard) (board == null ? getMainScoreboard() : board);
    }

    // CardboardBukkit method
    public void setPlayerBoard(CraftPlayer player, org.bukkit.scoreboard.Scoreboard bukkitScoreboard) throws IllegalArgumentException {
        Validate.isTrue(bukkitScoreboard instanceof CardboardScoreboard, "Cannot set player scoreboard to an unregistered Scoreboard");

        CardboardScoreboard scoreboard = (CardboardScoreboard) bukkitScoreboard;
        net.minecraft.world.scores.Scoreboard oldboard = getPlayerBoard(player).getHandle();
        net.minecraft.world.scores.Scoreboard newboard = scoreboard.getHandle();
        ServerPlayer entityplayer = player.getHandle();

        if (oldboard == newboard) return;

        if (scoreboard == mainScoreboard) {
            playerBoards.remove(player);
        } else playerBoards.put(player, (CardboardScoreboard) scoreboard);

        // Old objective tracking
        HashSet<Objective> removed = new HashSet<Objective>();
        for (int i = 0; i < 3; ++i) {
            Objective scoreboardobjective = oldboard.getDisplayObjective(DisplaySlot.values()[i]);
            if (scoreboardobjective != null && !removed.contains(scoreboardobjective)) {
                entityplayer.connection.send(new ClientboundSetObjectivePacket(scoreboardobjective, 1));
                removed.add(scoreboardobjective);
            }
        }

        // Old team tracking
        Iterator<?> iterator = oldboard.getPlayerTeams().iterator();
        //while (iterator.hasNext())
        // TODO: 1.17ify    entityplayer.networkHandler.sendPacket(new TeamS2CPacket((Team) iterator.next(), 1));

        // The above is the reverse of the below method. 
        ((IMixinPlayerManager)server.getPlayerList()).sendScoreboardBF((ServerScoreboard) newboard, player.getHandle());
    }

    // CardboardBukkit method
    public void removePlayer(Player player) {
        playerBoards.remove(player);
    }

    // CardboardBukkit method
    public void getScoreboardScores(ObjectiveCriteria criteria, ScoreHolder holder, Consumer<ScoreAccess> consumer) {
        for (CardboardScoreboard scoreboard : scoreboards) {
            Scoreboard board = scoreboard.board;
            board.forAllObjectives(criteria, holder, consumer::accept);
        }
    }

}
