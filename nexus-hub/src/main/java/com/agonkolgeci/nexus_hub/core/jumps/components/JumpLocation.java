package com.agonkolgeci.nexus_hub.core.jumps.components;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

@Getter
public class JumpLocation extends Location {

    final double lowerY;

    public JumpLocation(@NotNull World world, double x, double y, double z, float yaw, float pitch, double lowerY) {
        super(world, x, y, z, yaw, pitch);

        this.lowerY = lowerY;
    }

    @NotNull
    public Location toCenter() {
        return clone().add(.5, .5, .5);
    }
}
