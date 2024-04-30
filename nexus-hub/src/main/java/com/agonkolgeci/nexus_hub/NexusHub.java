package com.agonkolgeci.nexus_hub;

import com.agonkolgeci.nexus_hub.core.ads.AdsController;
import com.agonkolgeci.nexus_hub.core.jumps.JumpsController;
import com.agonkolgeci.nexus_hub.core.players.HubPlayersController;
import com.agonkolgeci.nexus_hub.core.spawn.SpawnController;
import com.agonkolgeci.nexus_hub.core.utils.UtilitiesController;
import com.agonkolgeci.nexus_api.NexusBootstrap;
import com.agonkolgeci.nexus_api.NexusPlugin;
import com.agonkolgeci.nexus_api.common.config.ConfigController;
import com.agonkolgeci.nexus_api.common.database.DatabaseController;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public final class NexusHub extends NexusPlugin {

    @NotNull private final ConfigController configController;
    @NotNull private final DatabaseController databaseController;

    @NotNull private final UtilitiesController utilitiesController;
    @NotNull private final SpawnController spawnController;
    @NotNull private final AdsController adsController;
    @NotNull private final JumpsController jumpsController;

    @NotNull private final HubPlayersController playersController;

    public NexusHub(@NotNull NexusBootstrap bootstrap) {
        super(bootstrap);

        configController = new ConfigController(this);
        databaseController = new DatabaseController(this, configController.of("database"));

        utilitiesController = new UtilitiesController(this);
        spawnController = new SpawnController(this, configController.of("spawn"));
        adsController = new AdsController(this, configController.of("ads"));
        jumpsController = new JumpsController(this, configController.of("jumps"));

        playersController = new HubPlayersController(this);
    }

    @Override
    public void load() throws Exception {
        super.load();

        databaseController.loadSchema(plugin.getResource("database/schema.sql"));

        utilitiesController.load();
        spawnController.load();
        adsController.load();
        jumpsController.load();

        playersController.load();
    }

    @Override
    public void unload() {
        super.unload();

        playersController.unload();

        jumpsController.unload();
        adsController.unload();
        spawnController.unload();
        utilitiesController.unload();
    }

}
