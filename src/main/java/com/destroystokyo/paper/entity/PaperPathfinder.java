package com.destroystokyo.paper.entity;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.MobEntity;
import org.bukkit.Location;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.cardboardpowered.impl.entity.LivingEntityImpl;

public class PaperPathfinder implements Pathfinder {

	private MobEntity entity;

	public PaperPathfinder(MobEntity entity) {
		this.entity = entity;
	}

	public Mob getEntity() {
		return (Mob)this.entity.getBukkitEntity();
	}

	public void setHandle(MobEntity entity) {
		this.entity = entity;
	}

	public void stopPathfinding() {
		this.entity.getNavigation().stop();
	}

	public boolean hasPath() {
		return this.entity.getNavigation().getCurrentPath() != null && !this.entity.getNavigation().getCurrentPath().isFinished();
	}

	@Nullable
	public PathResult getCurrentPath() {
		Path path = this.entity.getNavigation().getCurrentPath();
		return path != null && !path.isFinished() ? new PaperPathfinder.PaperPathResult(path) : null;
	}

	@Nullable
	public PathResult findPath(Location loc) {
		Preconditions.checkArgument(loc != null, "Location can not be null");
		Path path = this.entity.getNavigation().findPathTo(loc.getX(), loc.getY(), loc.getZ(), 0);
		return path != null ? new PaperPathfinder.PaperPathResult(path) : null;
	}

	@Nullable
	public PathResult findPath(LivingEntity target) {
		Preconditions.checkArgument(target != null, "Target can not be null");
		Path path = this.entity.getNavigation().findPathTo(((LivingEntityImpl)target).getHandle(), 0);
		return path != null ? new PaperPathfinder.PaperPathResult(path) : null;
	}

	public boolean moveTo(@Nonnull PathResult path, double speed) {
		Preconditions.checkArgument(path != null, "PathResult can not be null");
		Path pathEntity = ((PaperPathfinder.PaperPathResult)path).path;
		return this.entity.getNavigation().startMovingAlong(pathEntity, speed);
	}

	public boolean canOpenDoors() {
		return this.entity.getNavigation().pathNodeNavigator.pathNodeMaker.canOpenDoors();
	}

	public void setCanOpenDoors(boolean canOpenDoors) {
		this.entity.getNavigation().pathNodeNavigator.pathNodeMaker.setCanOpenDoors(canOpenDoors);
	}

	public boolean canPassDoors() {
		return this.entity.getNavigation().pathNodeNavigator.pathNodeMaker.canEnterOpenDoors();
	}

	public void setCanPassDoors(boolean canPassDoors) {
		this.entity.getNavigation().pathNodeNavigator.pathNodeMaker.setCanEnterOpenDoors(canPassDoors);
	}

	public boolean canFloat() {
		return this.entity.getNavigation().pathNodeNavigator.pathNodeMaker.canSwim();
	}

	public void setCanFloat(boolean canFloat) {
		this.entity.getNavigation().pathNodeNavigator.pathNodeMaker.setCanSwim(canFloat);
	}

	public class PaperPathResult implements PathResult {
		private final Path path;

		PaperPathResult(Path path) {
			this.path = path;
		}

		@Nullable
		public Location getFinalPoint() {
			PathNode point = this.path.getEnd();
			return point != null ? CraftLocation.toBukkit(point, PaperPathfinder.this.entity.getEntityWorld()) : null;
		}

		public boolean canReachFinalPoint() {
			return this.path.reachesTarget();
		}

		public List<Location> getPoints() {
			List<Location> points = new ArrayList<>();

			for (PathNode point : this.path.nodes) {
				points.add(CraftLocation.toBukkit(point, PaperPathfinder.this.entity.getEntityWorld()));
			}

			return points;
		}

		public int getNextPointIndex() {
			return this.path.getCurrentNodeIndex();
		}

		@Nullable
		public Location getNextPoint() {
			return this.path.isFinished()
					? null
							: CraftLocation.toBukkit(this.path.nodes.get(this.path.getCurrentNodeIndex()), PaperPathfinder.this.entity.getEntityWorld());
		}
	}

}
