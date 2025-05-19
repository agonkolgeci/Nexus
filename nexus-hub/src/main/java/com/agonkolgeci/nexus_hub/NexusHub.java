package com.agonkolgeci.nexus_hub;

import com.agonkolgeci.nexus.AbstractBootstrap;
import com.agonkolgeci.nexus.AbstractPlugin;
import com.agonkolgeci.nexus.api.config.ConfigManager;
import com.agonkolgeci.nexus.api.database.DatabaseManager;
import com.agonkolgeci.nexus_hub.core.ads.AdsManager;
import com.agonkolgeci.nexus_hub.core.jumps.JumpsManager;
import com.agonkolgeci.nexus_hub.core.spawn.SpawnManager;
import com.agonkolgeci.nexus_hub.core.utilities.UtilitiesManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Getter
public final class NexusHub extends AbstractPlugin {

    @NotNull private final ConfigManager configManager;

    @NotNull private final DatabaseManager databaseManager;

    @NotNull private final UtilitiesManager utilitiesManager;
    @NotNull private final SpawnManager spawnManager;
    @NotNull private final AdsManager adsManager;
    @NotNull private final JumpsManager jumpsManager;

    public NexusHub(@NotNull AbstractBootstrap bootstrap) {
        super(bootstrap);

        this.configManager = new ConfigManager(this);
        this.databaseManager = new DatabaseManager(this, configManager.of("database"), Objects.requireNonNull(plugin.getResource("database/schema.sql"), "DB schema is required."));

        this.utilitiesManager = new UtilitiesManager(this);

        this.spawnManager = new SpawnManager(this, configManager.of("spawn"));
        this.adsManager = new AdsManager(this, configManager.of("ads"));
        this.jumpsManager = new JumpsManager(this, configManager.of("jumps"));
    }

    @Override
    public void load() throws Exception {
        databaseManager.load();

        utilitiesManager.load();
        spawnManager.load();
        adsManager.load();
        jumpsManager.load();
    }

    @Override
    public void unload() {
        databaseManager.unload();

        utilitiesManager.unload();
        spawnManager.unload();
        adsManager.unload();
        jumpsManager.unload();
    }

}
