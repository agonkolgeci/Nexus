package com.agonkolgeci.nexus.utils.entity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EntityUtils {

    @NotNull
    public static <T extends Entity> T createEntity(@NotNull Location location, @NotNull Class<T> clazz) {
        return location.getWorld().spawn(location, clazz);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends Entity> T retrieveEntity(@NotNull World world, @NotNull UUID uuid) {
        return (T) world.getEntities().stream().filter(entity -> entity.getUniqueId().equals(uuid)).findFirst().orElse(null);
    }

    public static void removeEntity(@NotNull World world, @NotNull UUID uuid) {
        @Nullable final Entity entity = retrieveEntity(world, uuid);
        if(entity == null) return;

        entity.getLocation().getChunk().load();
        entity.remove();
    }

    @NotNull
    public static List<Entity> getNearByEntities(@NotNull Location location, int distance) {
        return location.getWorld().getEntities().stream().filter(entity -> entity.getLocation().distance(location) <= distance).collect(Collectors.toList());
    }

}
