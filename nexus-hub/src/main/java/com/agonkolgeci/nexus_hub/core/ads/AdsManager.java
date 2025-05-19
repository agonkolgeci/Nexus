package com.agonkolgeci.nexus_hub.core.ads;

import com.agonkolgeci.nexus.api.config.ConfigSection;
import com.agonkolgeci.nexus.plugin.PluginAdapter;
import com.agonkolgeci.nexus.plugin.PluginManager;
import com.agonkolgeci.nexus_hub.NexusHub;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        @NotNull final Player player = event.getPlayer();

        adsBossBar.addAudience(player);
        adsActionBar.addAudience(player);
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        @NotNull final Player player = event.getPlayer();

        adsBossBar.removeAudience(player);
        adsActionBar.removeAudience(player);
    }

}
