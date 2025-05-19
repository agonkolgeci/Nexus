package com.agonkolgeci.nexus.api.config.callback.any;

import com.agonkolgeci.nexus.api.config.ConfigSection;
import org.jetbrains.annotations.NotNull;

public interface ConfigurationObjectCallback<O> {

    @NotNull O retrieveObject(@NotNull String key, @NotNull ConfigSection config);

}