package com.agonkolgeci.nexus.common.config;

import com.agonkolgeci.nexus.AbstractPlugin;
import com.agonkolgeci.nexus.api.database.DatabaseCredentials;
import com.agonkolgeci.nexus.utils.objects.ObjectUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConfigUtils {

    @NotNull
    public static World retrieveWorld(@NotNull AbstractPlugin instance, @NotNull ConfigSection configuration) {
        return ObjectUtils.requireNonNullElseGet(instance.getServer().getWorld((String) configuration.require("world")), () -> {
            instance.getLogger().warning(String.format("The world provided '%s' in the configuration was incorrect, so it has been replaced by the default world.", configuration.getPath()));

            @NotNull final World defaultWorld = instance.getServer().getWorlds().stream().findFirst().orElseThrow(() -> new IllegalStateException("Unable to access the default world."));

            configuration.set("world", defaultWorld.getName());

            return defaultWorld;
        });
    }

    @NotNull
    public static Location retrieveLocation(@NotNull World world, @NotNull ConfigSection configuration) {
        final double x = configuration.require("x");
        final double y = configuration.require("y");
        final double z = configuration.require("z");
        final float yaw = (float) ((double) configuration.get("yaw", 0.0));
        final float pitch = (float) ((double) configuration.get("pitch", 0.0));

        return new Location(world, x, y, z, yaw, pitch);
    }

    @Nullable
    public static DatabaseCredentials retrieveDatabaseCredentials(@NotNull ConfigSection configuration) {
        @Nullable final String host = configuration.get("host");
        @Nullable final String name = configuration.get("name");
        @Nullable final String username = configuration.get("username");
        @Nullable final String password = configuration.get("password");
        if(host == null || username == null || password == null || name == null) return null;

        final int maxPoolSize = configuration.get("maximum-pool-size", 10);
        final int port = configuration.get("port", 3306);

        return new DatabaseCredentials(host, name, username, password, maxPoolSize, port);
    }

}
