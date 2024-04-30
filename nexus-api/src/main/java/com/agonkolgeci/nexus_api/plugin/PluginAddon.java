package com.agonkolgeci.nexus_api.plugin;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class PluginAddon<M> {

    @NotNull protected final M module;

    public PluginAddon(@NotNull M module) {
        this.module = module;
    }
}
