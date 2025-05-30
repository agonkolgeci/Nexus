package com.agonkolgeci.nexus.api.events;

import com.agonkolgeci.nexus.AbstractPlugin;
import com.agonkolgeci.nexus.plugin.PluginManager;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class EventsManager extends PluginManager<AbstractPlugin> {

    @NotNull private final org.bukkit.plugin.PluginManager pluginManager;

    public EventsManager(@NotNull AbstractPlugin instance) {
        super(instance);

        this.pluginManager = instance.getServer().getPluginManager();
    }

    public void registerAdapter(@NotNull ListenerAdapter listener) {
        pluginManager.registerEvents(listener, instance.getPlugin());
    }

    public void unregisterAdapter(@NotNull ListenerAdapter listener) {
        HandlerList.unregisterAll(listener);
    }

    @NotNull
    public <E extends Event> E callEvent(@NotNull E event) {
        pluginManager.callEvent(event);

        return event;
    }

//    @NotNull
//    public handleEvent(@NotNull Supplier<Event> event) {
//        pluginManager.callEvent(event);
//
//        return event;
//    }

}
