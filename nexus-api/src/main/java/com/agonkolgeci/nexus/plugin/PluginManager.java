package com.agonkolgeci.nexus.plugin;

import com.agonkolgeci.nexus.AbstractPlugin;
import com.agonkolgeci.nexus.common.events.ListenerAdapter;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

@Getter
public abstract class PluginManager<P extends AbstractPlugin> implements ListenerAdapter {

    @NotNull protected final P instance;
    @NotNull protected final JavaPlugin plugin;
    @NotNull protected final Logger logger;

    public PluginManager(@NotNull P instance) {
        this.instance = instance;

        this.plugin = instance.getPlugin();
        this.logger = instance.getLogger();

        instance.getEventsManager().registerEventAdapter(this);
    }

}
