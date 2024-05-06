package com.agonkolgeci.nexus_hub;

import com.agonkolgeci.nexus.AbstractBootstrap;
import org.jetbrains.annotations.NotNull;

public class NexusHubBootstrap extends AbstractBootstrap {

    @Override
    public @NotNull NexusHub createInstance() {
        return new NexusHub(this);
    }
}
