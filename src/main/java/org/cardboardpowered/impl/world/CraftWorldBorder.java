/**
 * CardboardPowered - Bukkit/Spigot for Fabric
 * Copyright (C) CardboardPowered.org and contributors
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either 
 * version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.cardboardpowered.impl.world;

import com.google.common.base.Preconditions;
import net.minecraft.util.math.BlockPos;

import java.util.concurrent.TimeUnit;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.checkerframework.checker.index.qual.NonNegative;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public class CraftWorldBorder implements WorldBorder {

    private final World world;
    private final net.minecraft.world.border.WorldBorder handle;

    public CraftWorldBorder(CraftWorld world) {
        this.world = world;
        this.handle = world.getHandle().getWorldBorder();
    }

    @Override
    public void reset() {
        this.setSize(6.0E7D);
        this.setDamageAmount(0.2D);
        this.setDamageBuffer(5.0D);
        this.setWarningDistance(5);
        this.setWarningTime(15);
        this.setCenter(0, 0);
    }

    @Override
    public double getSize() {
        return this.handle.getSize();
    }

    @Override
    public void setSize(double newSize) {
        this.setSize(newSize, 0L);
    }

    @Override
    public void setSize(double newSize, long time) {
    	this.setSize(Math.min(this.getMaxSize(), Math.max(1.0, newSize)), TimeUnit.SECONDS, Math.min(9223372036854775L, Math.max(0L, time)));
    }

    public void setSize(double newSize, TimeUnit unit, long time) {
    	Preconditions.checkArgument(unit != null, "TimeUnit cannot be null.");
    	Preconditions.checkArgument(time >= 0L, "time cannot be lower than 0");
    	Preconditions.checkArgument(newSize >= 1.0 && newSize <= this.getMaxSize(), "newSize must be between 1.0D and %s", this.getMaxSize());
    	if (time > 0L) {
    		this.handle.interpolateSize(this.handle.getSize(), newSize, unit.toMillis(time), this.getWorld().getGameTime());
    	} else {
    		this.handle.setSize(newSize);
    	}
    }

    @Override
    public Location getCenter() {
        return new Location(this.world, this.handle.getCenterX(), 0, this.handle.getCenterZ());
    }

    @Override
    public void setCenter(double x, double z) {
        this.handle.setCenter(Math.min(3.0E7D, Math.max(-3.0E7D, x)), Math.min(3.0E7D, Math.max(-3.0E7D, z)));
    }

    @Override
    public void setCenter(Location location) {
        this.setCenter(location.getX(), location.getZ());
    }

    @Override
    public double getDamageBuffer() {
        return this.handle.getSafeZone();
    }

    @Override
    public void setDamageBuffer(double blocks) {
        this.handle.setSafeZone(blocks);
    }

    @Override
    public double getDamageAmount() {
        return this.handle.getDamagePerBlock();
    }

    @Override
    public void setDamageAmount(double damage) {
        this.handle.setDamagePerBlock(damage);
    }

    @Override
    public int getWarningTime() {
        return this.handle.getWarningTime();
    }

    @Override
    public void setWarningTime(int time) {
        this.handle.setWarningTime(time);
    }

    @Override
    public int getWarningDistance() {
        return this.handle.getWarningBlocks();
    }

    @Override
    public void setWarningDistance(int distance) {
        this.handle.setWarningBlocks(distance);
    }

    @Override
    public boolean isInside(Location location) {
        Preconditions.checkArgument(location != null, "Null Location");
        return location.getWorld().equals(this.world) && this.handle.contains(BlockPos.ofFloored(location.getX(), location.getY(), location.getZ()));
    }

	@Override
	public @Nullable World getWorld() {
		// TODO Auto-generated method stub
		return world;
	}
	
	// 1.19.2

	@Override
	public double getMaxCenterCoordinate() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMaxSize() {
		return net.minecraft.world.border.WorldBorder.STATIC_AREA_SIZE;
	}

	@Override
	public void changeSize(double newSize, @Range(from = 0, to = 2147483647) long ticks) {
		 if (ticks > 0L) {
			 final long startTime = (this.getWorld() != null) ? this.getWorld().getGameTime() : 0; // Virtual Borders don't have a World
			 this.handle.interpolateSize(this.handle.getSize(), newSize, ticks, startTime);
		 } else {
			 this.handle.setSize(newSize);
		 }
	}

	@Override
	public @NonNegative int getWarningTimeTicks() {
		// TODO Auto-generated method stub
		return this.handle.getWarningTime();
	}

	@Override
	public void setWarningTimeTicks(@NonNegative int ticks) {
		// TODO Auto-generated method stub
		this.handle.setWarningTime(ticks);
	}

}
