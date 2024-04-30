package com.agonkolgeci.nexus_proxy.plugin;

import com.agonkolgeci.nexus_proxy.NexusProxy;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Getter
public abstract class PluginModule {

    @NotNull protected final NexusProxy instance;

    @NotNull protected final ProxyServer proxy;
    @NotNull protected final Logger logger;

    public PluginModule(@NotNull NexusProxy instance) {
        this.instance = instance;

        this.proxy = instance.getProxy();
        this.logger = instance.getLogger();
    }
}
