package com.agonkolgeci.nexus.core.binder;

import com.agonkolgeci.nexus.NexusAPI;
import com.agonkolgeci.nexus.core.binder.item.InteractionsBinder;
import com.agonkolgeci.nexus.plugin.PluginAdapter;
import com.agonkolgeci.nexus.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

public class BinderManager extends PluginManager<NexusAPI> implements PluginAdapter {

    @NotNull private final InteractionsBinder interactionsBinder;

    public BinderManager(@NotNull NexusAPI instance) {
        super(instance);

        this.interactionsBinder = new InteractionsBinder(this);
    }

    @Override
    public void load() throws Exception {
        instance.getEventsManager().registerAdapter(interactionsBinder);
    }

    @Override
    public void unload() {
        instance.getEventsManager().unregisterAdapter(interactionsBinder);
    }
}
