package com.agonkolgeci.nexus_api;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public abstract class NexusBootstrap extends JavaPlugin {

    @Nullable private NexusPlugin instance;

    @NotNull public abstract NexusPlugin createInstance();

    @Override
    public void onEnable() {
        if(instance != null) throw new IllegalStateException("Unable to load the plugin more than once!");

        try {
            instance = createInstance();
            instance.load();
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }

        super.onEnable();
    }

    @Override
    public void onDisable() {
        if(instance != null) {
            instance.unload();
        }

        super.onDisable();
    }
}
