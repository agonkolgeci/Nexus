package com.agonkolgeci.nexus.utils.world;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("all")
public class Cuboid {

    @Getter private final int xMin;
    @Getter private final int xMax;
    @Getter private final int yMin;
    @Getter private final int yMax;
    @Getter private final int zMin;
    @Getter private final int zMax;
    @Getter private final double xMinCentered;
    @Getter private final double xMaxCentered;
    @Getter private final double yMinCentered;
    @Getter private final double yMaxCentered;
    @Getter private final double zMinCentered;
    @Getter private final double zMaxCentered;
    @Getter private final World world;

    public Cuboid(@Nonnull Location point1, @Nonnull Location point2) {
        this.xMin = Math.min(point1.getBlockX(), point2.getBlockX());
        this.xMax = Math.max(point1.getBlockX(), point2.getBlockX());
        this.yMin = Math.min(point1.getBlockY(), point2.getBlockY());
        this.yMax = Math.max(point1.getBlockY(), point2.getBlockY());
        this.zMin = Math.min(point1.getBlockZ(), point2.getBlockZ());
        this.zMax = Math.max(point1.getBlockZ(), point2.getBlockZ());
        this.world = point1.getWorld();
        this.xMinCentered = this.xMin + 0.5;
        this.xMaxCentered = this.xMax + 0.5;
        this.yMinCentered = this.yMin + 0.5;
        this.yMaxCentered = this.yMax + 0.5;
        this.zMinCentered = this.zMin + 0.5;
        this.zMaxCentered = this.zMax + 0.5;
    }

    @Nonnull
    public List<Block> getBlocks() {
        @Nonnull final List<Block> blocks = new ArrayList<>(this.getTotalBlockSize());
        for (int x = this.xMin; x <= this.xMax; ++x) {
            for (int y = this.yMin; y <= this.yMax; ++y) {
                for (int z = this.zMin; z <= this.zMax; ++z) {
                    blocks.add(this.world.getBlockAt(x, y, z));
                }
            }
        }

        return blocks;
    }

    @Nonnull
    public Location getCenter() {
        return new Location(this.world, (this.xMax - this.xMin) / 2 + this.xMin, (this.yMax - this.yMin) / 2 + this.yMin, (this.zMax - this.zMin) / 2 + this.zMin);
    }

    public double getDistance() {
        return this.getPoint1().distance(this.getPoint2());
    }

    public double getDistanceSquared() {
        return this.getPoint1().distanceSquared(this.getPoint2());
    }

    public int getHeight() {
        return this.yMax - this.yMin + 1;
    }

    @Nonnull
    public Location getPoint1() {
        return new Location(this.world, this.xMin, this.yMin, this.zMin);
    }

    @Nonnull
    public Location getPoint2() {
        return new Location(this.world, this.xMax, this.yMax, this.zMax);
    }

    @Nonnull
    public Location getRandomLocation() {
        @Nonnull final Random random = new Random();
        @Nonnull final int x = random.nextInt(Math.abs(this.xMax - this.xMin) + 1) + this.xMin;
        @Nonnull final int y = random.nextInt(Math.abs(this.yMax - this.yMin) + 1) + this.yMin;
        @Nonnull final int z = random.nextInt(Math.abs(this.zMax - this.zMin) + 1) + this.zMin;

        return new Location(this.world, x, y, z);
    }

    public int getTotalBlockSize() {
        return this.getHeight() * this.getXWidth() * this.getZWidth();
    }

    public int getXWidth() {
        return this.xMax - this.xMin + 1;
    }

    public int getZWidth() {
        return this.zMax - this.zMin + 1;
    }

    public boolean isIn(@Nonnull Location location) {
        return location.getWorld() == this.world && location.getBlockX() >= this.xMin && location.getBlockX() <= this.xMax && location.getBlockY() >= this.yMin && location.getBlockY() <= this.yMax && location
                .getBlockZ() >= this.zMin && location.getBlockZ() <= this.zMax;
    }

    public boolean isIn(@Nonnull Player player) {
        return this.isIn(player.getLocation());
    }

    public boolean isInWithMarge(@Nonnull Location location, double marge) {
        return location.getWorld() == this.world && location.getX() >= this.xMinCentered - marge && location.getX() <= this.xMaxCentered + marge && location.getY() >= this.yMinCentered - marge && location
                .getY() <= this.yMaxCentered + marge && location.getZ() >= this.zMinCentered - marge && location.getZ() <= this.zMaxCentered + marge;
    }
}
 