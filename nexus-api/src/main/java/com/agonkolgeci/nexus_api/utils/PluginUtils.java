package com.agonkolgeci.nexus_api.utils;

import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PluginUtils {

    @Nullable
    public static <S> S retrieveService(@NotNull Server server, @NotNull Class<S> serviceClass) {
        @Nullable final RegisteredServiceProvider<S> provider = server.getServicesManager().getRegistration(serviceClass);
        if(provider != null) {
            return provider.getProvider();
        }

        return null;
    }

}
