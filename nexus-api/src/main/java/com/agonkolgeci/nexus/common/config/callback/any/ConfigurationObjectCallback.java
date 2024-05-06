package com.agonkolgeci.nexus.common.config.callback.any;

import com.agonkolgeci.nexus.common.config.ConfigSection;
import org.jetbrains.annotations.NotNull;

public interface ConfigurationObjectCallback<O> {

    @NotNull O retrieveObject(@NotNull String key, @NotNull ConfigSection config);

}