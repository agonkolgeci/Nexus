package com.agonkolgeci.nexus_proxy;

import com.agonkolgeci.nexus_proxy.common.commands.CommandsController;
import com.agonkolgeci.nexus_proxy.common.events.EventsController;
import com.agonkolgeci.nexus_proxy.core.hub.HubsController;
import com.agonkolgeci.nexus_proxy.core.player.PlayersController;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Getter
@Plugin(id = "nexus-proxy", name = "Nexus- Proxy", version = "1.0-SNAPSHOT", authors = "Agon KOLGECI", url = "https://agonkolgeci.com/")
public class NexusProxy {

    @Inject @NotNull private final ProxyServer proxy;
    @Inject @NotNull private final Logger logger;

    @NotNull private final EventsController eventsController;
    @NotNull private final CommandsController commandsController;

    @NotNull private final HubsController hubsController;
    @NotNull private final PlayersController playersController;

    @Inject
    public NexusProxy(@NotNull ProxyServer proxy, @NotNull Logger logger) {
        this.proxy = proxy;
        this.logger = logger;

        this.eventsController = new EventsController(this);
        this.commandsController = new CommandsController(this);

        this.hubsController = new HubsController(this);
        this.playersController = new PlayersController(this);
    }

    @Subscribe
    public void onProxyInitialization(@NotNull ProxyInitializeEvent event) {
        hubsController.load();
        playersController.load();
    }

    @Subscribe
    public void onProxyShutdown(@NotNull ProxyShutdownEvent event) {
        playersController.unload();
        hubsController.unload();
    }
}
