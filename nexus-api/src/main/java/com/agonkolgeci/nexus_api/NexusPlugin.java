package com.agonkolgeci.nexus_api;

import com.agonkolgeci.nexus_api.common.commands.CommandsController;
import com.agonkolgeci.nexus_api.common.events.EventsController;
import com.agonkolgeci.nexus_api.plugin.PluginController;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Server;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

@Getter
public abstract class NexusPlugin implements PluginController {

    protected static NexusPlugin instance;

    @NotNull protected final JavaPlugin plugin;

    @NotNull protected final Logger logger;
    @NotNull protected final Server server;

    @NotNull protected final ServicesManager servicesManager;
    @NotNull protected final ScoreboardManager scoreboardManager;

    @NotNull protected final BukkitAudiences adventure;
    @NotNull protected final Audience console;
    @NotNull protected final Audience all;

    @NotNull protected final CommandsController commandsController;
    @NotNull protected final EventsController eventsController;

    public NexusPlugin(@NotNull JavaPlugin plugin) {
        this.plugin = plugin;

        this.logger = plugin.getLogger();
        this.server = plugin.getServer();

        this.servicesManager = server.getServicesManager();
        this.scoreboardManager = server.getScoreboardManager();

        this.adventure = BukkitAudiences.create(plugin);
        this.console = adventure.console();
        this.all = adventure.all();

        this.commandsController = new CommandsController(this);
        this.eventsController = new EventsController(this);
    }

    @Override
    public void load() throws Exception {
        instance = this;
    }

    @Override
    public void unload() {
        adventure.close();
    }
}
