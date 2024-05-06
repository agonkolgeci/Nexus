package com.agonkolgeci.nexus_hub.core.database;

import com.agonkolgeci.nexus.AbstractPlugin;
import com.agonkolgeci.nexus.api.database.AbstractDatabaseManager;
import com.agonkolgeci.nexus.common.config.ConfigSection;
import org.jetbrains.annotations.NotNull;

public class DatabaseManager extends AbstractDatabaseManager {

    public DatabaseManager(@NotNull AbstractPlugin instance, @NotNull ConfigSection configuration) {
        super(instance, configuration);
    }

    @Override
    public void load() throws Exception {
        this.executeSchema(plugin.getResource("database/schema.sql"));
    }
}
