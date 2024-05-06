package com.agonkolgeci.nexus.common.messaging;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public enum PluginChannelType {

    BUNGEE_CORD("BungeeCord");

    @NotNull private final String name;

    PluginChannelType(@NotNull String name) {
        this.name = name;
    }
}
