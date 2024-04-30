package com.agonkolgeci.nexus_api.common.events;

import com.agonkolgeci.nexus_api.NexusPlugin;
import com.agonkolgeci.nexus_api.plugin.PluginModule;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class EventsController extends PluginModule<NexusPlugin> {

    public EventsController(@NotNull NexusPlugin instance) {
        super(instance);
    }

    public void registerEventAdapter(@NotNull ListenerAdapter listener) {
        pluginManager.registerEvents(listener, plugin);
    }

    public void unregisterEventAdapter(@NotNull ListenerAdapter listener) {
        HandlerList.unregisterAll(listener);
    }

    @NotNull
    public <E extends Event> E callEvent(@NotNull E event) {
        pluginManager.callEvent(event);

        return event;
    }

}
