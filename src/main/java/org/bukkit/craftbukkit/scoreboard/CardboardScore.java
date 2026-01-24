package org.bukkit.craftbukkit.scoreboard;

import net.kyori.adventure.text.Component;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.cardboardpowered.adventure.CardboardAdventure;
import org.jetbrains.annotations.Nullable;

import io.papermc.paper.scoreboard.numbers.NumberFormat;

public final class CardboardScore implements Score {

    private final ScoreHolder entry;
    private final CardboardObjective objective;

    public CardboardScore(CardboardObjective objective, ScoreHolder entry) {
        this.objective = objective;
        this.entry = entry;
    }

    @Override
    public OfflinePlayer getPlayer() {
        return Bukkit.getOfflinePlayer(entry.getScoreboardName());
    }

    @Override
    public String getEntry() {
        return entry.getScoreboardName();
    }

    @Override
    public Objective getObjective() {
        return objective;
    }

    @Override
    public int getScore() throws IllegalStateException {
        Scoreboard board = objective.checkState().board;

        if(board.getTrackedPlayers().contains(entry)) {
            ReadOnlyScoreInfo score = board.getPlayerScoreInfo(entry, objective.getHandle());
            if(score != null) {
                return score.value();
            }
        }

        return 0;
    }

    @Override
    public void setScore(int score) throws IllegalStateException {
        objective.checkState().board.getOrCreatePlayerScore(entry, objective.getHandle()).set(score);
    }

    @Override
    public boolean isScoreSet() throws IllegalStateException {
        Scoreboard board = objective.checkState().board;
        return board.getTrackedPlayers().contains(entry) &&
                board.getPlayerScoreInfo(entry, objective.getHandle()) != null;
    }

    @Override
    public CardboardScoreboard getScoreboard() {
        return objective.getScoreboard();
    }

    @Override
    public void resetScore() throws IllegalStateException {
        // TODO Auto-generated method stub
    }

    // 1.20.4 API
    
	@Override
	public boolean isTriggerable() {
        if (this.objective.getTrackedCriteria() != Criteria.TRIGGER) {
            return false;
        }
        Scoreboard board = this.objective.checkState().board;
        ReadOnlyScoreInfo scoreInfo = board.getPlayerScoreInfo(this.entry, this.objective.getHandle());
        return scoreInfo != null && !scoreInfo.isLocked();
	}

	@Override
	public void setTriggerable(boolean triggerable) {
        Scoreboard board = this.objective.checkState().board;
        if (triggerable) {
            board.getOrCreatePlayerScore(this.entry, this.objective.getHandle()).unlock();
        } else {
            board.getOrCreatePlayerScore(this.entry, this.objective.getHandle()).lock();
        }
	}

	@Override
	public Component customName() {
        Scoreboard board = this.objective.checkState().board;
        ReadOnlyScoreInfo scoreInfo = board.getPlayerScoreInfo(this.entry, this.objective.getHandle());
        if (scoreInfo == null) {
            return null;
        }
        net.minecraft.network.chat.Component display = board.getOrCreatePlayerScore(this.entry, this.objective.getHandle()).display();
        return display == null ? null : CardboardAdventure.asAdventure(display);
	}

	@Override
	public void customName(@Nullable Component customName) {
		Scoreboard board = this.objective.checkState().board;
        board.getOrCreatePlayerScore(this.entry, this.objective.getHandle()).display(CardboardAdventure.asVanilla(customName));
	}

	@Override
	public @Nullable NumberFormat numberFormat() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void numberFormat(@Nullable NumberFormat format) {
		// TODO Auto-generated method stub
		
	}

}
