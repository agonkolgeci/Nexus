package com.agonkolgeci.nexus_api.plugin;

import com.agonkolgeci.nexus_api.NexusPlugin;
import lombok.Getter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

@Getter
public abstract class PluginModule<P extends NexusPlugin> {

    @NotNull protected final P instance;
    @NotNull protected final JavaPlugin plugin;

    @NotNull protected final Logger logger;

    @NotNull protected final Server server;
    @NotNull protected final PluginManager pluginManager;
    @NotNull protected final BukkitScheduler bukkitScheduler;

    @NotNull protected final BukkitAudiences adventure;
    @NotNull protected final Audience all;

    public PluginModule(@NotNull P instance) {
        this.instance = instance;
        this.plugin = instance.getPlugin();

        this.logger = instance.getLogger();

        this.server = instance.getServer();
        this.pluginManager = server.getPluginManager();
        this.bukkitScheduler = server.getScheduler();

        this.adventure = instance.getAdventure();
        this.all = adventure.all();
    }
}
