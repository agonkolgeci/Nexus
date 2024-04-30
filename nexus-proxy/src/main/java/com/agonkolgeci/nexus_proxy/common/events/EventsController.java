package com.agonkolgeci.nexus_proxy.common.events;

import com.agonkolgeci.nexus_proxy.NexusProxy;
import com.agonkolgeci.nexus_proxy.plugin.PluginModule;
import com.velocitypowered.api.event.EventManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class EventsController extends PluginModule {

    @NotNull private final EventManager eventManager;

    public EventsController(@NotNull NexusProxy instance) {
        super(instance);

        this.eventManager = proxy.getEventManager();
    }

    @NotNull
    public <L extends ListenerAdapter> L registerEventAdapter(@NotNull L listener) {
        eventManager.register(instance, listener);

        return listener;
    }

    public void unregisterEventAdapter(@NotNull ListenerAdapter listener) {
        eventManager.unregisterListener(instance, listener);
    }

}
