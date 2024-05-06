package com.agonkolgeci.nexus;

import org.jetbrains.annotations.NotNull;

public class NexusAPIBootstrap extends AbstractBootstrap {

    @Override
    public @NotNull NexusAPI createInstance() {
        return new NexusAPI(this);
    }
}
