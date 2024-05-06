package com.agonkolgeci.nexus.common.events;

import com.agonkolgeci.nexus.AbstractPlugin;
import com.agonkolgeci.nexus.plugin.AbstractAddon;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@Getter
public class EventsManager extends AbstractAddon<AbstractPlugin> {

    @NotNull private final org.bukkit.plugin.PluginManager pluginManager;

    public EventsManager(@NotNull AbstractPlugin module) {
        super(module);

        this.pluginManager = module.getServer().getPluginManager();
    }

    public void registerEventAdapter(@NotNull ListenerAdapter listener) {
        pluginManager.registerEvents(listener, module.getPlugin());
    }

    public void unregisterEventAdapter(@NotNull ListenerAdapter listener) {
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
