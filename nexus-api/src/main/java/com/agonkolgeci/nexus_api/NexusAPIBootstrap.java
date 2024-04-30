package com.agonkolgeci.nexus_api;

import org.jetbrains.annotations.NotNull;

public class NexusAPIBootstrap extends NexusBootstrap {

    @Override
    public @NotNull NexusAPI createInstance() {
        return new NexusAPI(this);
    }
}
