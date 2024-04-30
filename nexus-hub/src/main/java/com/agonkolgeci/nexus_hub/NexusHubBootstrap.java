package com.agonkolgeci.nexus_hub;

import com.agonkolgeci.nexus_api.NexusBootstrap;
import org.jetbrains.annotations.NotNull;

public class NexusHubBootstrap extends NexusBootstrap {

    @Override
    public @NotNull NexusHub createInstance() {
        return new NexusHub(this);
    }
}
