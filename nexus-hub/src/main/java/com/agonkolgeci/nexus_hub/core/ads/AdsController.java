package com.agonkolgeci.nexus_hub.core.ads;

import com.agonkolgeci.nexus_hub.NexusHub;
import com.agonkolgeci.nexus_hub.core.players.HubPlayer;
import com.agonkolgeci.nexus_api.common.config.ConfigSection;
import com.agonkolgeci.nexus_api.common.events.ListenerAdapter;
import com.agonkolgeci.nexus_api.common.players.events.PlayerReadyEvent;
import com.agonkolgeci.nexus_api.plugin.PluginController;
import com.agonkolgeci.nexus_api.plugin.PluginModule;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

@Getter
public class AdsController extends PluginModule<NexusHub> implements PluginController, ListenerAdapter {

    @NotNull private final ConfigSection configuration;

    @NotNull private final AdsBossBar adsBossBar;
    @NotNull private final AdsActionBar adsActionBar;

    public AdsController(@NotNull NexusHub instance, @NotNull ConfigSection configuration) {
        super(instance);

        this.configuration = configuration;

        this.adsBossBar = new AdsBossBar(this, configuration.of("boss-bar"));
        this.adsActionBar = new AdsActionBar(this, configuration.of("action-bar"));
    }

    @Override
    public void load() {
        adsBossBar.load();
        adsActionBar.load();

        instance.getEventsController().registerEventAdapter(this);
    }

    @Override
    public void unload() {
        adsBossBar.unload();
        adsActionBar.unload();

        instance.getEventsController().unregisterEventAdapter(this);
    }

    @EventHandler
    public void onPlayerReady(@NotNull PlayerReadyEvent event) {
        @NotNull final HubPlayer hubPlayer = instance.getPlayersController().retrieveUserCache(event.getPlayer());

        adsBossBar.loadPlayer(hubPlayer);
        adsActionBar.loadPlayer(hubPlayer);
    }

    @EventHandler
    public void onPlayerLogout(@NotNull PlayerReadyEvent event) {
        @NotNull final HubPlayer hubPlayer = instance.getPlayersController().retrieveUserCache(event.getPlayer());

        adsBossBar.unloadPlayer(hubPlayer);
        adsActionBar.unloadPlayer(hubPlayer);
    }

}
