package com.agonkolgeci.nexus_hub;

import com.agonkolgeci.nexus.AbstractBootstrap;
import com.agonkolgeci.nexus.AbstractPlugin;
import com.agonkolgeci.nexus.api.database.AbstractDatabaseManager;
import com.agonkolgeci.nexus.common.config.ConfigManager;
import com.agonkolgeci.nexus_hub.core.ads.AdsManager;
import com.agonkolgeci.nexus_hub.core.database.DatabaseManager;
import com.agonkolgeci.nexus_hub.core.gui.GuiManager;
import com.agonkolgeci.nexus_hub.core.interactions.InteractionsManager;
import com.agonkolgeci.nexus_hub.core.jumps.JumpsManager;
import com.agonkolgeci.nexus_hub.core.players.HubPlayersManager;
import com.agonkolgeci.nexus_hub.core.spawn.SpawnManager;
import com.agonkolgeci.nexus_hub.core.utilities.UtilitiesManager;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public final class NexusHub extends AbstractPlugin {

    @NotNull private final ConfigManager configManager;
    @NotNull private final AbstractDatabaseManager databaseManager;

    @NotNull private final HubPlayersManager playersController;
    @NotNull private final GuiManager guiManager;
    @NotNull private final InteractionsManager interactionsManager;

    @NotNull private final UtilitiesManager utilitiesManager;
    @NotNull private final SpawnManager spawnManager;
    @NotNull private final AdsManager adsManager;
    @NotNull private final JumpsManager jumpsManager;

    public NexusHub(@NotNull AbstractBootstrap bootstrap) {
        super(bootstrap);

        configManager = new ConfigManager(this);
        databaseManager = new DatabaseManager(this, configManager.of("database"));

        playersController = new HubPlayersManager(this);
        guiManager = new GuiManager(this);
        interactionsManager = new InteractionsManager(this);

        utilitiesManager = new UtilitiesManager(this);

        spawnManager = new SpawnManager(this, configManager.of("spawn"));
        adsManager = new AdsManager(this, configManager.of("ads"));
        jumpsManager = new JumpsManager(this, configManager.of("jumps"));
    }

    @Override
    public void load() throws Exception {
        databaseManager.load();
        playersController.load();

        utilitiesManager.load();
        spawnManager.load();
        adsManager.load();
        jumpsManager.load();
    }

    @Override
    public void unload() {
        super.unload();

        databaseManager.unload();
        playersController.unload();

        utilitiesManager.unload();
        spawnManager.unload();
        adsManager.unload();
        jumpsManager.unload();
    }

}
