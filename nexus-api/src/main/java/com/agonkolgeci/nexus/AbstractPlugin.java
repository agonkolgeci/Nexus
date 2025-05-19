package com.agonkolgeci.nexus;

import com.agonkolgeci.nexus.api.commands.CommandsManager;
import com.agonkolgeci.nexus.api.events.EventsManager;
import com.agonkolgeci.nexus.plugin.PluginAdapter;
import lombok.Getter;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

@Getter
public abstract class AbstractPlugin implements PluginAdapter {

    @NotNull protected final JavaPlugin plugin;

    @NotNull protected final Logger logger;
    @NotNull protected final Server server;

    @NotNull protected final CommandsManager commandsManager;
    @NotNull protected final EventsManager eventsManager;

    public AbstractPlugin(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;

        this.logger = plugin.getLogger();
        this.server = plugin.getServer();

        this.commandsManager = new CommandsManager(this);
        this.eventsManager = new EventsManager(this);
    }
}
