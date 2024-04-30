package com.agonkolgeci.nexus_proxy.plugin;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class PluginAddon<M extends PluginModule> {

    @NotNull protected final M module;

    public PluginAddon(@NotNull M module) {
        this.module = module;
    }
}
