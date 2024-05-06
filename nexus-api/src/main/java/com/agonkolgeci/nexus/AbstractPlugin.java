package com.agonkolgeci.nexus;

import com.agonkolgeci.nexus.common.commands.CommandsManager;
import com.agonkolgeci.nexus.common.events.EventsManager;
import com.agonkolgeci.nexus.plugin.PluginAdapter;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

@Getter
public abstract class AbstractPlugin implements PluginAdapter {

    @NotNull protected final JavaPlugin plugin;

    @NotNull protected final Logger logger;
    @NotNull protected final Server server;

    @NotNull protected final BukkitAudiences adventure;
    @NotNull protected final Audience console;
    @NotNull protected final Audience all;

    @NotNull protected final CommandsManager commandsManager;
    @NotNull protected final EventsManager eventsManager;

    public AbstractPlugin(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;

        this.logger = plugin.getLogger();
        this.server = plugin.getServer();

        this.adventure = BukkitAudiences.create(plugin);
        this.console = adventure.console();
        this.all = adventure.all();

        this.commandsManager = new CommandsManager(this);
        this.eventsManager = new EventsManager(this);
    }

    @Override
    public void load() throws Exception {

    }

    @Override
    public void unload() {
        adventure.close();
    }
}
