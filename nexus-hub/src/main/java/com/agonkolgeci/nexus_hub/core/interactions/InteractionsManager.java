package com.agonkolgeci.nexus_hub.core.interactions;

import com.agonkolgeci.nexus.api.interactions.AbstractInteractionsManager;
import com.agonkolgeci.nexus_hub.NexusHub;
import org.jetbrains.annotations.NotNull;

public class InteractionsManager extends AbstractInteractionsManager<NexusHub> {

    public InteractionsManager(@NotNull NexusHub instance) {
        super(instance);
    }
}
