package com.agonkolgeci.nexus_hub.core.ads;

import com.agonkolgeci.nexus.api.players.events.PlayerReadyEvent;
import com.agonkolgeci.nexus.common.config.ConfigSection;
import com.agonkolgeci.nexus.plugin.PluginManager;
import com.agonkolgeci.nexus.plugin.PluginAdapter;
import com.agonkolgeci.nexus_hub.NexusHub;
import com.agonkolgeci.nexus_hub.core.players.HubPlayer;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;

@Getter
public class AdsManager extends PluginManager<NexusHub> implements PluginAdapter {

    @NotNull private final ConfigSection configuration;

    @NotNull private final AdsBossBar adsBossBar;
    @NotNull private final AdsActionBar adsActionBar;

    public AdsManager(@NotNull NexusHub instance, @NotNull ConfigSection configuration) {
        super(instance);

        this.configuration = configuration;

        this.adsBossBar = new AdsBossBar(this, configuration.of("boss-bar"));
        this.adsActionBar = new AdsActionBar(this, configuration.of("action-bar"));
    }

    @Override
    public void load() throws Exception {
        adsBossBar.load();
        adsActionBar.load();
    }

    @Override
    public void unload() {
        adsBossBar.unload();
        adsActionBar.unload();
    }

    @EventHandler
    public void onPlayerReady(@NotNull PlayerReadyEvent event) {
        @NotNull final HubPlayer hubPlayer = instance.getPlayersController().retrievePlayerCache(event.getPlayer());

        adsBossBar.loadPlayer(hubPlayer);
        adsActionBar.loadPlayer(hubPlayer);
    }

    @EventHandler
    public void onPlayerLogout(@NotNull PlayerReadyEvent event) {
        @NotNull final HubPlayer hubPlayer = instance.getPlayersController().retrievePlayerCache(event.getPlayer());

        adsBossBar.unloadPlayer(hubPlayer);
        adsActionBar.unloadPlayer(hubPlayer);
    }

}
